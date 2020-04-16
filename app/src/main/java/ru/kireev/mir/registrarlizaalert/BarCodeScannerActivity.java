package ru.kireev.mir.registrarlizaalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
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
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.yanzhenjie.zbar.Config;
import com.yanzhenjie.zbar.Image;
import com.yanzhenjie.zbar.ImageScanner;
import com.yanzhenjie.zbar.Symbol;
import com.yanzhenjie.zbar.SymbolSet;

import java.util.List;

import ru.kireev.mir.registrarlizaalert.data.MainViewModel;
import ru.kireev.mir.registrarlizaalert.data.Volunteer;
import ru.kireev.mir.registrarlizaalert.util.CameraPreview;

public class BarCodeScannerActivity extends AppCompatActivity {
    private CameraPreview mPreview;
    private Camera mCamera;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private FrameLayout frameLayout;
    private Button scanButton;
    private String name = "";
    private String surname = "";
    private String callSign = "";
    private String phoneNumber = "";
    private String carMark = "";
    private String carModel = "";
    private String carRegistrationNumber = "";
    private String carColor = "";
    private MainViewModel mainViewModel;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scanner);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Intent intent = getIntent();
        index = intent.getIntExtra("size", 0);

        //запрос разрешения на доступ к камере
        //слушатель результатов запроса
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                initControls();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(BarCodeScannerActivity.this,  getString(R.string.permission_denied) + deniedPermissions.toString() , Toast.LENGTH_SHORT).show();
            }
        };
        //добавление запроса разрешения
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(getString(R.string.denied_message))
                .setPermissions(Manifest.permission.CAMERA)
                .check();



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

                    switch (lengthOfArray) {
                        case 3:
                            name = scanResultArray[0];
                            surname = scanResultArray[1];
                            phoneNumber = scanResultArray[2];
                            insertVolunteer();
                            break;
                        case 4:
                            name = scanResultArray[0];
                            surname = scanResultArray[1];
                            callSign = scanResultArray[2];
                            phoneNumber = scanResultArray[3];
                            insertVolunteer();
                            break;
                        case 7:
                            name = scanResultArray[0];
                            surname = scanResultArray[1];
                            phoneNumber = scanResultArray[2];
                            carMark = scanResultArray[3];
                            carModel = scanResultArray[4];
                            carRegistrationNumber = scanResultArray[5];
                            carColor = scanResultArray[6];
                            insertVolunteer();
                            break;
                        case 8:
                            name = scanResultArray[0];
                            surname = scanResultArray[1];
                            callSign = scanResultArray[2];
                            phoneNumber = scanResultArray[3];
                            carMark = scanResultArray[4];
                            carModel = scanResultArray[5];
                            carRegistrationNumber = scanResultArray[6];
                            carColor = scanResultArray[7];
                            insertVolunteer();
                            break;

                            default:
                                showAlertDialog("Произошла ошибка при сканировании, попробуйте еще раз");
                                barcodeScanned = true;

                                break;

                    }

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

    private void insertVolunteer() {
        Volunteer volunteer = new Volunteer(index, name, surname, callSign, phoneNumber, "false",
                carMark, carModel, carRegistrationNumber, carColor, "false");

        mainViewModel.insertVolunteer(volunteer);

        showAlertDialog("Сканирование прошло успешно");

        barcodeScanned = true;

        name = "";
        surname = "";
        callSign = "";
        phoneNumber = "";
        carMark = "";
        carModel = "";
        carRegistrationNumber = "";
        carColor = "";
    }


}
