package com.example.easyscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle

class ScreenCaptureActivity : Activity() {
    private val REQUEST_CODE = 1000
    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val mediaProjection =
                data?.let { mediaProjectionManager.getMediaProjection(resultCode, it) }
            // 通过 LocalBroadcastManager 发送结果
            val intent = Intent("com.example.easyscreen.ScreenCaptureActivity")
            intent.putExtra("resultCode", resultCode)
            intent.putExtra("data", data)
            sendBroadcast(intent)

        }
        finish()
    }
}

