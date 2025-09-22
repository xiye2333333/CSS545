package com.example.easyscreen

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException



class MainActivity : ComponentActivity() {

    private lateinit var cropView: CropView
    private lateinit var screenshotButton: Button
    private lateinit var startButton: Button
    private var screenshotBitmap: Bitmap? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>
    private var resultData: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cropView = findViewById(R.id.cropView)

        screenshotButton = findViewById(R.id.screenshotButton)
        screenshotButton.setOnClickListener {
            takeScreenshot()
        }

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
//            startService(Intent(this, FloatingButtonService::class.java))
        }

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        screenCaptureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                resultData = result.data
                // 启动 Service 并传递权限数据
                startMyService(resultData)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            val OVERLAY_PERMISSION_REQUEST_CODE = 1234
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }

        startScreenCapture()
    }


    private fun startScreenCapture() {
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        screenCaptureLauncher.launch(intent)
    }

    private fun startMyService(resultData: Intent?) {
        val serviceIntent = Intent(this, FloatingButtonService::class.java).apply {
            putExtra("media_projection_data", resultData)
        }
        startForegroundService(serviceIntent)
    }

    private fun takeScreenshot() {
        val rootView = window.decorView.rootView
        rootView.isDrawingCacheEnabled = true
        screenshotBitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        // 显示 CropView
        cropView.visibility = View.VISIBLE
        cropView.invalidate()
    }

    fun cropScreenshot(rect: Rect) {
        screenshotBitmap?.let { bitmap ->
            // 计算裁剪区域
            val croppedBitmap =
                Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
//            saveCroppedBitmap(croppedBitmap)
            translateCroppedBitmap(croppedBitmap)
        }

    }

    private fun saveCroppedBitmap(bitmap: Bitmap) {
        val savedUri: Uri? = try {
            // 获取当前时间作为文件名
            val timeStamp = System.currentTimeMillis()
            val imageName = "CroppedImage_$timeStamp.jpg"

            // 获取图片的插入信息
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // 指定保存路径
            }

            // 插入图片并获取 URI
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // 使用输出流将位图写入 URI
            uri?.let {
                contentResolver.openOutputStream(it).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                }
            }
            uri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        // 提示用户保存结果
        if (savedUri != null) {
            Toast.makeText(this, "图片已保存到相册！", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "保存失败，请重试！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun translateCroppedBitmap(bitmap: Bitmap) {
        // 创建文本识别器
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // 将位图转换为输入图像
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        // 进行文本识别
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                // 提取识别的文本
                val recognizedText = visionText.text
                if (recognizedText.isNotEmpty()) {
                    // 调用翻译函数
                    translateText(recognizedText)
                    println(recognizedText)
                } else {
                    Toast.makeText(this, "未识别到任何文本", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "文本识别失败", Toast.LENGTH_SHORT).show()
            }
    }

    // 翻译文本的方法
    private fun translateText(text: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.CHINESE)
            .build()
        val translator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Toast.makeText(this, "Download success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Download failed", Toast.LENGTH_LONG).show()
            }

        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                // 显示翻译结果
                Toast.makeText(this, "翻译结果: $translatedText", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "翻译失败", Toast.LENGTH_SHORT).show()
            }
    }
}

