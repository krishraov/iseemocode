package in.co.iseemo.iseemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;


public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


    }
}
