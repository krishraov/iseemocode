package in.co.iseemo.iseemo;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import in.co.iseemo.iseemo.fragments.ImageGridFragment;
import in.co.iseemo.iseemo.fragments.ImagePagerFragment;

public class ImageDisplayActivity extends AppCompatActivity {

    public static String[] IMAGES;
    public static String baseURL = "";
    public static Integer numImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // this is needed because the ImageDisplayActivity can be called
        // by the ImagePagerFragment.
        if (baseURL == "") {
            baseURL = getIntent().getStringExtra("BASE_URL");
            numImages = getIntent().getIntExtra("NUM_ITEMS", 1);
        }

        // set up all the image URLs
        // create dynamic image view and add them to ViewFlipper
        IMAGES = new String[numImages+1];
        for (int i = 0; i < IMAGES.length; i++) {
            IMAGES[i] = baseURL + "/image" + (i + 1) + ".jpg";
        }

        IMAGES[3] = baseURL + "/image" + (1) + ".jpg";

        int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        Fragment fr;
        String tag;

        switch (frIndex) {
            default:
            case ImageGridFragment.INDEX:
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGridFragment();
                }
                break;
            case ImagePagerFragment.INDEX:
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }

    @Override
    public void onBackPressed() {
        ImageLoader.getInstance().stop();
        super.onBackPressed();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        CardView contactCard = null;
        View view = findViewById(R.id.includeContactCard);
        if (view != null) {
            contactCard = (CardView) view.findViewById(R.id.cardContact);
        }

        if (contactCard != null) {
            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                contactCard.setVisibility(View.GONE);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                contactCard.setVisibility(View.VISIBLE);
            }
        }
    }
}
