package ru.kireev.mir.registrarlizaalert.ui.fragments

import android.Manifest
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.terrakok.cicerone.Router
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yanzhenjie.zbar.Config
import com.yanzhenjie.zbar.Image
import com.yanzhenjie.zbar.ImageScanner
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.databinding.FragmentBarCodeScannerBinding
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.BarCodeScannerViewModel
import ru.kireev.mir.registrarlizaalert.ui.BaseFragment
import ru.kireev.mir.registrarlizaalert.ui.util.*

class BarCodeScannerFragment : BaseFragment(R.layout.fragment_bar_code_scanner) {

    private val router: Router by inject()
    private val viewModel: BarCodeScannerViewModel by inject()

    private var index: Int by args()
    private val binding: FragmentBarCodeScannerBinding by viewBinding()

    private var mCamera: Camera? = null
    private lateinit var autoFocusHandler: Handler
    private lateinit var scanner: ImageScanner
    private var barcodeScanned = false
    private var previewing = true

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            initControls()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            view?.showShortSnack(
                "${getString(R.string.permission_denied)} ${deniedPermissions.toString()}"
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setDeniedMessage(R.string.denied_message)
            .setPermissions(Manifest.permission.CAMERA)
            .check()

        observeScanResult()
    }

    private fun observeScanResult() {
        viewModel.result.observe(viewLifecycleOwner, {
            showSimpleAlertDialog(getString(it))
        })
    }

    private fun initControls() {
        autoFocusHandler = Handler()
        mCamera = getCameraInstance()

        scanner = ImageScanner()
        scanner.setConfig(0, Config.X_DENSITY, 3)
        scanner.setConfig(0, Config.Y_DENSITY, 3)

        val mPreview = mCamera?.let { CameraPreview(requireContext(), it, previewCb, autoFocusCB) }
        binding.cameraPreview.addView(mPreview)
        binding.scanButton.setOnClickListener {
            if (barcodeScanned) {
                barcodeScanned = false
                mCamera?.setPreviewCallback(previewCb)
                mCamera?.startPreview()
                previewing = true
                mCamera?.autoFocus(autoFocusCB)
            }
        }
    }

    private fun getCameraInstance(): Camera? {
        var cam: Camera? = null
        try {
            cam = Camera.open()
        } catch (e: Exception) {
        }
        return cam
    }

    private fun doAutoFocus() = Runnable {
        if (previewing) {
            mCamera?.autoFocus(autoFocusCB)
        }
    }

    private fun releaseCamera() {
        mCamera?.let {
            previewing = false
            it.setPreviewCallback(null)
            it.release()
            mCamera = null
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

            viewModel.handleScanResults(
                symbolSet = scanner.results,
                templateFullName = getString(R.string.template_for_legacy_fullname),
                templateCar = getString(R.string.template_for_legacy_car),
                index = index
            )

            barcodeScanned = true
        }
    }

    private val autoFocusCB: Camera.AutoFocusCallback = Camera.AutoFocusCallback { _, _ ->
        autoFocusHandler.postDelayed(doAutoFocus(), 1000)
    }

    override fun onBackPressed() {
        releaseCamera()
        router.exit()
    }

    override fun onStart() {
        super.onStart()
        setActionBarTitle(R.string.actionbar_label_scan_QR)
    }

    companion object {
        fun newInstance(index: Int) =
            BarCodeScannerFragment().apply {
                this.index = index
            }
    }
}