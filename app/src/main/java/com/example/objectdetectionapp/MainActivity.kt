package com.example.objectdetectionapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.drawToBitmap

class MainActivity : Activity() {
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private val PICK_IMAGE = 100
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        button = findViewById(R.id.button)
        button.setOnClickListener { openGallery() }


    }

    private fun openGallery() {
        var gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData()!!
        }
        imageView.setImageURI(imageUri)

        val objectsDetection=ObjectsDetection(
            imageView.drawToBitmap(Bitmap.Config.ARGB_8888),applicationContext)

        objectsDetection.drawBoundingBoxWithText(imageView)
        objectsDetection.debugPrint()
    }
}