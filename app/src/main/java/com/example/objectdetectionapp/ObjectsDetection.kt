package com.example.objectdetectionapp

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.Log
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectsDetection(bitmap: Bitmap,context_: Context) {
    private var context:Context = context_

    init {
        runObjectDetection(bitmap)
    }


    private lateinit var imageView: ImageView
    private lateinit var results: MutableList<Detection>

    companion object {
        //model
        private const val modelPath = "model_1.tflite"
        private const val maxResults = 5
        private const val scoreThreshold = 0.5f

        //boundbox
        private lateinit var rectF: RectF
        private lateinit var text: String
        private const val boundboxStrokeWidth = 5f
        private const val boundboxStrokeColor = Color.BLACK
        private const val textSize = 50f


    }

    private fun runObjectDetection(bitmap: Bitmap) {

        val image = TensorImage.fromBitmap(bitmap)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(maxResults)
            .setScoreThreshold(scoreThreshold)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            context,
            modelPath,
            options
        )

        results=detector.detect(image)

        results.map {
            val category = it.categories.first()
            text = "${category.label}, ${category.score.times(100).toInt()}%"
            rectF=it.boundingBox
        }
    }
    fun debugPrint() {

        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox

            Log.d("detect_TAG", "Detected object: ${i} ")
            Log.d(
                "detect_TAG",
                "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})"
            )
            for ((j, category) in obj.categories.withIndex()) {
                Log.d("detect_TAG", "    Label $j: ${category.label}")
                val confidence: Int = category.score.times(100).toInt()
                Log.d("detect_TAG", "    Confidence: ${confidence}%")
            }
        }
    }


    fun drawBoundingBoxWithText(imageView: ImageView) {

        val bitmap = imageView.drawToBitmap(Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = boundboxStrokeColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = boundboxStrokeWidth

        canvas.drawRect(rectF, paint)

        paint.getTextBounds(text,0,text.length, Rect(0,0,0,0))
        paint.textAlign= Paint.Align.LEFT
        paint.textSize= textSize
        paint.style= Paint.Style.FILL
        val margin=15f

        canvas.drawText(text,rectF.left+margin,rectF.top-margin,paint)

        imageView.setImageBitmap(bitmap)
    }
}