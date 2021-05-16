package ru.kireev.mir.registrarlizaalert.util

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraPreview(context: Context, private val camera: Camera,
                    private val previewCb: Camera.PreviewCallback,
                    private val autoFocusCb: Camera.AutoFocusCallback) : SurfaceView(context), SurfaceHolder.Callback {

    private val mHolder: SurfaceHolder = holder

    init {
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera.setPreviewDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        camera.stopPreview()
        camera.setDisplayOrientation(90)
        camera.setPreviewDisplay(mHolder)
        camera.setPreviewCallback(previewCb)
        camera.startPreview()
        camera.autoFocus(autoFocusCb)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
}