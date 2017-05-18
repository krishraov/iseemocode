package in.co.iseemo.iseemo;

import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * This activity has a margin.
 */
public class ScannerActivity extends CaptureActivity {

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_scanner);

        TextView textView = (TextView) findViewById(R.id.zxing_status_view);
        if (textView!= null)
            textView.setText("");

        return (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
    }
}