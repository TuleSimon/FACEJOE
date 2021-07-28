package com.thee.horrorcorian.facedetection

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class MainActivity: AppCompatActivity(){

    private val GALLERY = 78
    var imgTakePicture:ImageView?=null
    var btnProcessNext:Button?=null
    var btnTakePicture:Button?=null
    var txtSampleDesc:TextView?=null
    var txtTakenPicDesc:TextView?=null
    lateinit var detector:FaceDetector
    var editedBitmap:Bitmap?=null
    var currentIndex = 0
    lateinit var dialog:MaterialDialog
    var imageArray:IntArray?=null
    private var imageUri:Uri?=null
    private val REQUEST_WRITE_PERMISSION = 200
    private val CAMERA_REQUEST = 101

    private val SAVED_INSTANCE_URI = "uri"
    private val SAVED_INSTANCE_BITMAP = "bitmap"


    private var  LOAD_IMAGE_PHONE = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main )
        //imageArray = intArrayOf(R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3)

        CoroutineScope(Main).launch {

                showDialog()

        }

        finish.setOnClickListener {
            finish()
        }

    }
    suspend fun showDialog(){
        val int =intent.extras!!.get("imageInt")
        if(int!=null){
            imageView.setImageResource(int as Int)
        }
        else{
            val bitmap = intent.extras!!.get("imageBitmap")
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap as Bitmap)
            }
            else{
                val uri = intent.extras!!.get("imageUri")
                if(uri!=null){
                    imageUri = uri as Uri
                    imageView.setImageURI(imageUri)
                }
            }
        }
        dialog =Dialogs().getCustomViewDialogs(this,R.layout.dialog)
        val text = dialog.findViewById<TextView>(R.id.currentText)
        dialog.show()


        for(i in 1 ..3){
            if(i==1){
                delay(1000)
            }
            else if(i==2){
                text.text = "Fecthing Graphic instance for rendering face position, orientation, " +
                        "and landmarks within an associated\n" +
                        "  graphic overlay view"
                delay(1000)
            }
            else if(i==3){
                text.text = "Using 2D facial recognition algorithm in Coordination with Machine Learning Algorithms " +
                        "to analyze Provided Image Sample"
                delay(1000)
            }
        }
        withContext(IO){
            start()

        }
    }

    suspend fun start(){

        detector = FaceDetector.Builder(baseContext)
            //.Builder(ApplicationProvider.getApplicationContext<Context>())
            .setTrackingEnabled(false)
            .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setMode(FaceDetector.FAST_MODE)
            .build()
        val int =intent.extras!!.get("imageInt")
        if(int!=null){

            processImage(int as Int)
        }
        else{
            val bitmap = intent.extras!!.get("imageBitmap")
            if(bitmap!=null){
                processCameraPicture(bitmap as Bitmap)
            }
            else{
                val uri = intent.extras!!.get("imageUri")
                if(uri!=null){
                    imageUri = uri as Uri
                    processCameraPicture()
                }
            }
        }
    }










    fun launchMediaScanIntent() {
            val mediaScanIntent =  Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = imageUri
        this.sendBroadcast(mediaScanIntent)
    }





    private suspend fun processImage(image:Int) {

        val bitmap = decodeBitmapImage(image)
        withContext(Main) {
            if (detector.isOperational && bitmap != null) {
                editedBitmap = Bitmap.createBitmap(
                    bitmap.width, bitmap
                        .height, bitmap.config
                )

                val scale = resources.displayMetrics.density
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = resources.getColor(R.color.purple_200)
                paint.textSize = (8 * scale)
                paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                val canvas = Canvas(editedBitmap!!)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                val frame = Frame.Builder().setBitmap(editedBitmap).build()
                val faces = detector.detect(frame)
                output.text = ""
                expression.text = ""

                for (index in 0 until faces.size()) {
                    val face = faces.valueAt(index)
                    canvas.drawRect(
                        face.position.x,
                        face.position.y,
                        face.position.x + face.width,
                        face.position.y + face.height, paint
                    )


                    canvas.drawText(
                        "Face " + (index + 1), face.position.x +
                                face.width, face.position.y + face.height, paint
                    )

                    output.text = "${output!!.text} FACE " + (index + 1) + "\n"
                    output.text = "${output!!.text}" + "Smile probability:" + " " +
                            face.isSmilingProbability + "\n"
                    output.text = "${output!!.text}" + "Left Eye Is Open Probability: " + " " +
                            face.isLeftEyeOpenProbability + "\n"
                    output.text = "${output!!.text}" + "Right Eye Is Open Probability: " + " " +
                            face.isRightEyeOpenProbability + "\n\n"


                    for (landmark in face.landmarks) {
                        val cx = landmark.position.x.toInt()
                        val cy = landmark.position.y.toInt()
                        canvas.drawCircle(cx.toFloat(), cy.toFloat(), 4f, paint)
                    }
                    if (face.isSmilingProbability < 0.5) {
                        expression.text =
                            "${expression.text} \nExpression for face ${index + 1} : Not Smilling"
                    } else {
                        expression.text =
                            "${expression.text} \nExpression for face ${index + 1}: Smilling"
                    }

                }

                if (faces.size() == 0) {
                    output.text = "Scan Failed: Found nothing to scan"
                } else {
                    imageView.setImageBitmap(editedBitmap)
                    output2.text = "No of Faces Detected: " +
                            (faces.size()).toString()
                }
            } else {
                output.text = "Could not set up the detector!"
            }
            dialog.dismiss()
        }
    }

    fun decodeBitmapImage(image:Int):Bitmap {
        val targetW = 300
        val targetH = 300
        val bmOptions = BitmapFactory.Options()
        bmOptions .inJustDecodeBounds = true

        BitmapFactory.decodeResource(
            resources, image,
            bmOptions)

        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeResource(
            resources, image,
            bmOptions)
    }

    private suspend fun processCameraPicture(bitmap: Bitmap)  {
        withContext(Main) {
            if (detector.isOperational && bitmap != null) {
                editedBitmap = Bitmap.createBitmap(
                    bitmap.width, bitmap
                        .height, bitmap.config
                )

                val scale = resources.displayMetrics.density
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = resources.getColor(R.color.purple_200)
                paint.textSize = (8 * scale)
                paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                val canvas = Canvas(editedBitmap!!)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                val frame = Frame.Builder().setBitmap(editedBitmap).build()
                val faces = detector.detect(frame)
                output.text = ""
                expression.text = ""

                for (index in 0 until faces.size()) {
                    val face = faces.valueAt(index)
                    canvas.drawRect(
                        face.position.x,
                        face.position.y,
                        face.position.x + face.width,
                        face.position.y + face.height, paint
                    )


                    canvas.drawText(
                        "Face " + (index + 1), face.position.x +
                                face.width, face.position.y + face.height, paint
                    )

                    output.text = "${output!!.text} FACE " + (index + 1) + "\n"
                    output.text = "${output!!.text}" + "Smile probability:" + " " +
                            face.isSmilingProbability + "\n"
                    output.text = "${output!!.text}" + "Left Eye Is Open Probability: " + " " +
                            face.isLeftEyeOpenProbability + "\n"
                    output.text = "${output!!.text}" + "Right Eye Is Open Probability: " + " " +
                            face.isRightEyeOpenProbability + "\n\n"


                    for (landmark in face.landmarks) {
                        val cx = landmark.position.x.toInt()
                        val cy = landmark.position.y.toInt()
                        canvas.drawCircle(cx.toFloat(), cy.toFloat(), 4f, paint)
                    }
                    if (face.isSmilingProbability < 0.5) {
                        expression.text =
                            "${expression.text} \nExpression for face ${index + 1} : Not Smilling"
                    } else {
                        expression.text =
                            "${expression.text} \nExpression for face ${index + 1}: Smilling"
                    }

                }

                if (faces.size() == 0) {
                    output.text = "Scan Failed: Found nothing to scan"
                } else {
                    imageView.setImageBitmap(editedBitmap)
                    output2.text = "No of Faces Detected: " +
                            (faces.size()).toString()
                }
            } else {
                output.text = "Could not set up the detector!"
            }
            dialog.dismiss()
        }
    }

    private suspend fun processCameraPicture()  {
        val bitmap = decodeBitmapUri(this, imageUri!!)
        withContext(Main) {
            if (detector.isOperational && bitmap != null) {
                editedBitmap = Bitmap.createBitmap(
                    bitmap.width, bitmap
                        .height, bitmap.config
                )

                val scale = resources.displayMetrics.density
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = resources.getColor(R.color.purple_200)
                paint.textSize = (8 * scale)
                paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                val canvas = Canvas(editedBitmap!!)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                val frame = Frame.Builder().setBitmap(editedBitmap).build()
                val faces = detector.detect(frame)
                output.text = ""
                expression.text = ""

                for (index in 0 until faces.size()) {
                    val face = faces.valueAt(index)
                    canvas.drawRect(
                        face.position.x,
                        face.position.y,
                        face.position.x + face.width,
                        face.position.y + face.height, paint
                    )


                    canvas.drawText(
                        "Face " + (index + 1), face.position.x +
                                face.width, face.position.y + face.height, paint
                    )

                    output.text = "${output!!.text} FACE " + (index + 1) + "\n"
                    output.text = "${output!!.text}" + "Smile probability:" + " " +
                            face.isSmilingProbability + "\n"
                    output.text = "${output!!.text}" + "Left Eye Is Open Probability: " + " " +
                            face.isLeftEyeOpenProbability + "\n"
                    output.text = "${output!!.text}" + "Right Eye Is Open Probability: " + " " +
                            face.isRightEyeOpenProbability + "\n\n"


                    for (landmark in face.landmarks) {
                        val cx = landmark.position.x.toInt()
                        val cy = landmark.position.y.toInt()
                        canvas.drawCircle(cx.toFloat(), cy.toFloat(), 4f, paint)
                    }
                    if (face.isSmilingProbability < 0.5) {
                        expression.text =
                            "${expression.text} \nExpression for face ${index + 1} : Not Smilling"
                    } else {
                        expression.text =
                            "${expression.text} \nExpression for face ${index + 1}: Smilling"
                    }

                }

                if (faces.size() == 0) {
                    output.text = "Scan Failed: Found nothing to scan"
                } else {
                    imageView.setImageBitmap(editedBitmap)
                    output2.text = "No of Faces Detected: " +
                            (faces.size()).toString()
                }
            } else {
                output.text = "Could not set up the detector!"
            }
            dialog.dismiss()
        }
    }


    private suspend fun decodeBitmapUri(ctx:Context, uri:Uri): Bitmap {
        lateinit var value:Deferred<Bitmap>
        withContext(IO) {
           value = async {
                val targetW = 300
                val targetH = 300
                val bmOptions = BitmapFactory.Options()
                bmOptions.inJustDecodeBounds = true
                BitmapFactory.decodeStream(
                    ctx.contentResolver.openInputStream(uri),
                    null,
                    bmOptions
                )
                val photoW = bmOptions.outWidth
                val photoH = bmOptions.outHeight

                val scaleFactor = Math.min(photoW / targetW, photoH / targetH)
                bmOptions.inJustDecodeBounds = false
                bmOptions.inSampleSize = scaleFactor

                return@async BitmapFactory.decodeStream(
                    ctx.contentResolver
                        .openInputStream(uri), null, bmOptions
                )!!
            }
        }
        return value.await()
    }


    override fun onDestroy() {
        super.onDestroy()
        detector.release()
    }





}
