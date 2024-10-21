package com.example.hw2_basicstorage

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    private lateinit var downloadButton: Button
    private lateinit var loadButton: Button
    private lateinit var imageView: ImageView
    private lateinit var saveNameButton: Button
    private lateinit var loadNameButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var displayNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadButton = findViewById(R.id.downloadbutton)
        loadButton = findViewById(R.id.showbutton)
        imageView = findViewById(R.id.imageView)
        saveNameButton = findViewById(R.id.savename)
        loadNameButton = findViewById(R.id.showname)
        nameEditText = findViewById(R.id.editText)
        displayNameTextView = findViewById(R.id.textView2)



        downloadButton.setOnClickListener {
            downloadImage("https://img.zcool.cn/community/019bca578c700f0000018c1b8f140c.jpg@1280w_1l_2o_100sh.jpg")
        }

        loadButton.setOnClickListener {
            loadImageFromLocal("downloaded_image.jpg")
        }

        saveNameButton.setOnClickListener {
            saveNameToLocal(nameEditText.text.toString())
        }

        loadNameButton.setOnClickListener {
            displayNameFromLocal()
        }
    }
    private fun saveNameToLocal(name: String) {
        if (name.isNotEmpty()) {
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("user_name", name)
            editor.apply()
            Toast.makeText(this, "name saved", Toast.LENGTH_SHORT).show()
            nameEditText.text.clear()
        } else {
            Toast.makeText(this, "input a name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayNameFromLocal() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("user_name", "no name saved")
        displayNameTextView.text = name
    }

    private fun loadImageFromLocal(fileName: String) {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir, fileName)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
            Toast.makeText(this, "loaded", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadImage(url: String) {
        Thread {
            try {
                val imageUrl = URL(url)
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                saveImageToLocal(bitmap, "downloaded_image.jpg")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun saveImageToLocal(bitmap: Bitmap?, fileName: String) {
        if (bitmap != null) {
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            println(storageDir)
            val file = File(storageDir, fileName)

            try {
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Picture is saved at: ${file.absolutePath}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}