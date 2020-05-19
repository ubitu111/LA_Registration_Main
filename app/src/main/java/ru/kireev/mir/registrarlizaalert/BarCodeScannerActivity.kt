package ru.kireev.mir.registrarlizaalert

import android.Manifest
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yanzhenjie.zbar.Config
import com.yanzhenjie.zbar.Image
import com.yanzhenjie.zbar.ImageScanner
import kotlinx.android.synthetic.main.activity_bar_code_scanner.*
import ru.kireev.mir.registrarlizaalert.data.MainViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.util.CameraPreview

class BarCodeScannerActivity : AppCompatActivity() {

    private var mCamera: Camera? = null
    private lateinit var autoFocusHandler: Handler
    private lateinit var scanner: ImageScanner
    private var barcodeScanned = false
    private var previewing = true
    private var name = ""
    private var surname = ""
    private var callSign = ""
    private var phoneNumber = ""
    private var carMark = ""
    private var carModel = ""
    private var carRegistrationNumber = ""
    private var carColor = ""
    private lateinit var viewModel: MainViewModel
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner)
        val model by viewModels<MainViewModel>()
        viewModel = model
        index = intent.getIntExtra("size", 0)

        //запрос разрешения на доступ к камере
        //слушатель результатов запроса
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                initControls()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@BarCodeScannerActivity, getString(R.string.permission_denied) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        //добавление запроса разрешения
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(getString(R.string.denied_message))
                .setPermissions(Manifest.permission.CAMERA)
                .check()
    }

    private fun initControls() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        autoFocusHandler = Handler()
        mCamera = getCameraInstance()

        scanner = ImageScanner()
        scanner.setConfig(0, Config.X_DENSITY, 3)
        scanner.setConfig(0, Config.Y_DENSITY, 3)

        val mPreview = mCamera?.let { CameraPreview(this, it, previewCb, autoFocusCB) }
        camera_preview.addView(mPreview)

        scan_button.setOnClickListener {
            if (barcodeScanned) {
                barcodeScanned = false
                mCamera?.setPreviewCallback(previewCb)
                mCamera?.startPreview()
                previewing = true
                mCamera?.autoFocus(autoFocusCB)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getCameraInstance(): Camera? {
        var cam: Camera? = null
        try {
            cam = Camera.open()
        } catch (e: Exception) {

        }
        return cam
    }

    private fun releaseCamera() {
        mCamera?.let {
            previewing = false
            it.setPreviewCallback(null)
            it.release()
            mCamera = null
        }
    }

    private fun doAutoFocus() = Runnable {
        if (previewing) {
            mCamera?.autoFocus(autoFocusCB)
        }
    }

    private val previewCb = Camera.PreviewCallback { data, camera ->
        val parameters = camera.parameters
        val size = parameters.previewSize

        val barcode = Image(size.width, size.height, "Y800")
        barcode.data = data

        val result = scanner.scanImage(barcode)
        if (result != 0) {
            previewing = false
            mCamera?.setPreviewCallback(null)
            mCamera?.stopPreview()

            val syms = scanner.results
            for (sym in syms) {
                val scanResult = sym.data.trim()
                val scanResultArray = scanResult.split("\n")

                when(scanResultArray.size) {
                    3 -> {
                        name = scanResultArray[0]
                        surname = scanResultArray[1]
                        phoneNumber = scanResultArray[2]
                        insertVolunteer()
                    }

                    4 -> {
                        name = scanResultArray[0]
                        surname = scanResultArray[1]
                        callSign = scanResultArray[2]
                        phoneNumber = scanResultArray[3]
                        insertVolunteer()
                    }

                    7 -> {
                        name = scanResultArray[0]
                        surname = scanResultArray[1]
                        phoneNumber = scanResultArray[2]
                        carMark = scanResultArray[3]
                        carModel = scanResultArray[4]
                        carRegistrationNumber = scanResultArray[5]
                        carColor = scanResultArray[6]
                        insertVolunteer()
                    }

                    8 -> {
                        name = scanResultArray[0]
                        surname = scanResultArray[1]
                        callSign = scanResultArray[2]
                        phoneNumber = scanResultArray[3]
                        carMark = scanResultArray[4]
                        carModel = scanResultArray[5]
                        carRegistrationNumber = scanResultArray[6]
                        carColor = scanResultArray[7]
                        insertVolunteer()
                    }

                    else -> {
                        showAlertDialog(getString(R.string.error_qr_code_scan_message))
                        barcodeScanned = true
                    }
                }
            }
        }
    }

    private val autoFocusCB: Camera.AutoFocusCallback = Camera.AutoFocusCallback { _, _ ->
        autoFocusHandler.postDelayed(doAutoFocus(), 1000)
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->  })
                .show()
    }

    private fun insertVolunteer() {
        val volunteer = Volunteer(0, index, name, surname, callSign, phoneNumber, "false",
        carMark, carModel, carRegistrationNumber, carColor, "false")
        viewModel.insertVolunteer(volunteer)
        showAlertDialog(getString(R.string.success_qr_code_scan_message))
        barcodeScanned = true

        name = ""
        surname = ""
        callSign = ""
        phoneNumber = ""
        carMark = ""
        carModel = ""
        carRegistrationNumber = ""
        carColor = ""
    }
}