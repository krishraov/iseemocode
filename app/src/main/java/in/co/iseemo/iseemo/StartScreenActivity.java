package in.co.iseemo.iseemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import in.co.iseemo.iseemo.fragments.ImageGridFragment;


public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // for testing -- goes directly to the video activity
        //Intent videoIntent = new Intent(this, VideoDisplayActivity.class);
        //startActivity(videoIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This is activated upon a button click and it uses Zxing's
     * internal functions to open the scanner with some configurable options.
     *
     * @param view incoming view
     */
    public void openScanner(View view) {

        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setOrientationLocked(true);          // only scan in portrait (check the Manifest file too)
        integrator.setBeepEnabled(false);               // don't beep on scan
        integrator.setCaptureActivity(ScannerActivity.class);   // call the Scanner activity
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);  // scan only QR codes
        integrator.initiateScan();                      // start scanning
    }


    /**
     * After the scan has completed and the scanning activity returns,
     * this function is invoked.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // get the result using the built-in zxing parser
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // handle it
        if (result != null) {

            if (result.getContents() == null) {

                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();

            } else {

                // all the magic goes here.
                // * is the QR code from iSeeMo?
                // * has it expired?
                // * if the code is valid, then
                //   * (decrypt it, if needed)
                //   * read each field (id, num_items, initials, bitly path
                //   * construct a proper URL from the bitly path
                //   * depending on the id and num_items, construct the remaining URLs.
                //   * the above point can be done in the appropriate activity

                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String qrContent = result.getContents();

                // Iteratively check if the first few characters of the decoded string are as expected
                // TODO : can we create a nested if-loop of this and break when the condition is not met?

                boolean validCode = true;

                if (validCode && qrContent.charAt(0) != '9') {
                    validCode = false;
                }

                if (validCode && qrContent.charAt(1) != 'a') {
                    validCode = false;
                }

                if (validCode && qrContent.charAt(2) != '&') {
                    validCode = false;
                }

                if (validCode && qrContent.charAt(3) != 'r') {
                    validCode = false;
                }

                if (validCode && qrContent.charAt(4) != '5') {
                    validCode = false;
                }

                if (validCode && qrContent.charAt(5) != 'Q') {
                    validCode = false;
                }

                if (validCode && qrContent.charAt(6) != '*') {
                    validCode = false;
                }


                // this is not an iSeeMo code OR, the ad has expired.
                // print a message and stay on the launch screen
                if (!validCode) {
                    alertView("Sorry - this code has either expired or it isn't an iSeeMo code.");
                } else {

                    String[] qrArray = qrContent.split(",");

                    // What happens if there are fewer/more than 5 sections?
                    //  ... exit and declare that this is an invalid code!
                    if (qrArray.length != 5) {
                        alertView("Sorry - this code has either expired or it isn't an iSeeMo code.");
                        return;
                    }


                    // Split the input string to its constituent parts
                    String verifCode = qrArray[0];
                    String mID = qrArray[1];
                    String custInitials = qrArray[2];
                    String numItems = qrArray[3];
                    String partialURL = qrArray[4];

                    Integer mIdInt = 64 - Integer.decode("0x" + mID);
                    Integer numItemsInt = 64 - Integer.decode("0x" + numItems);

                    // Recreate the correct base URL to send via an intent.
                    // Depending on the type of media, this will be expanded in future activities.
                    // This is not valid for phone redirects.
                    // e.g. urlBase + image0.png
                    //      urlBase + image1.png
                    //      urlBase + video0.png
                    // change this if we switch to CloudFront (put it's domain here)
                    partialURL = new StringBuilder(partialURL).reverse().toString();
                    final String initURL = "https://s3.ap-south-1.amazonaws.com/iseemo-testing/";
                    final String urlBase = initURL + partialURL;

                    switch (mIdInt) {

                        case 0:
                            // image processing
                            // send the URL to the image display activity.
                            Intent imageIntent = new Intent(this, ImageDisplayActivity.class);
                            imageIntent.putExtra("BASE_URL", urlBase);
                            imageIntent.putExtra("NUM_ITEMS", numItemsInt);
                            imageIntent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
                            startActivity(imageIntent);
                            break;

                        case 1:
                            // audio processing
                            break;

                        case 2:
                            // video processing
                            // This technique uses implicit intents and uses
                            // the inbuilt and user-preferred video player
                            // to playback the video.
                            /*
                            Uri video = Uri.parse(urlBase + "/video1.mp4");
                            Intent intent = new Intent(Intent.ACTION_VIEW, video);
                            intent.setDataAndType(video, "video/mp4");
                            startActivity(Intent.createChooser(intent, "Watch this video using "));
                            */
                            Intent videoIntent = new Intent(this, VideoDisplayActivity.class);
                            videoIntent.putExtra("BASE_URL", urlBase);
                            startActivity(videoIntent);
                            break;

                        case 3:
                            // website redirect
                            //openWebPage(partialURL);
                            break;

                        case 4:
                            // Facebook redirect
                            break;

                        default:
                            // give error message
                            Toast.makeText(this, "Oops! Something is wrong here", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            }

        } else {

            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);

        }

    }

    /**
     * Display this dialog in case of an invalid scan
     *
     * @param message What we want the alertDialog to display
     */
    private void alertView(String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Invalid QR code")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();

    }
}