package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.yanzhenjie.zbar.Config;
import com.yanzhenjie.zbar.Image;
import com.yanzhenjie.zbar.ImageScanner;
import com.yanzhenjie.zbar.Symbol;
import com.yanzhenjie.zbar.SymbolSet;

import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;
import ru.kireev.mir.laregistrationmain.util.CameraPreview;

public class BarCodeScannerActivity extends AppCompatActivity {
    private CameraPreview mPreview;
    private Camera mCamera;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private FrameLayout frameLayout;
    private Button scanButton;
    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
    private MainViewModel mainViewModel;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scanner);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Intent intent = getIntent();
        index = intent.getIntExtra("size", 0);
        initControls();
    }

    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(BarCodeScannerActivity.this, mCamera, previewCb,
                autoFocusCB);
        frameLayout = findViewById(R.id.camera_preview);
        frameLayout.addView(mPreview);
        scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {

                    String scanResult = sym.getData().trim();
                    String[] scanResultArray = scanResult.split("\n");
                    int lengthOfArray = scanResultArray.length;
                    if (lengthOfArray == 3 || lengthOfArray == 4) {
                        if (lengthOfArray == 3) {
                            name = scanResultArray[0];
                            surname = scanResultArray[1];
                            callSign = "";
                            phoneNumber = scanResultArray[2];

                        } else  {
                            name = scanResultArray[0];
                            surname = scanResultArray[1];
                            callSign = scanResultArray[2];
                            phoneNumber = scanResultArray[3];

                        }

                        Volunteer volunteer;
                        if (index == 0) {
                            volunteer = new Volunteer(0, name, surname, callSign, phoneNumber, "false");
                        }
                        else {
                            volunteer = new Volunteer(index, name, surname, callSign, phoneNumber, "false");
                        }
                        mainViewModel.insertVolunteer(volunteer);

                        showAlertDialog("Сканирование прошло успешно");

                        barcodeScanned = true;

                        break;
                    }

                    showAlertDialog("Произошла ошибка при сканировании, попробуйте еще раз");
                    barcodeScanned = true;

                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void showAlertDialog(String message) {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .show();
    }
}
