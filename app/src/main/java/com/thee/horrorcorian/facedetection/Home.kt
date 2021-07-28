package com.thee.horrorcorian.facedetection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Home : AppCompatActivity(), View.OnClickListener  {

    private val REQUEST_WRITE_PERMISSION = 200
    private val CAMERA_REQUEST = 101

    private val SAVED_INSTANCE_URI = "uri"
    private val SAVED_INSTANCE_BITMAP = "bitmap"
    private val GALLERY = 78
    private var imageUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setTheme(R.style.Theme_Facedetection)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getPermission()
        setSlide()
        initViews()
    }


    fun getPermission(){

        CoroutineScope(Dispatchers.Main) .launch {
            try {
                val result = askPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                //all permissions already granted or just granted
                //your action

            } catch (e: PermissionException) {
                if (e.hasDenied()) {
                    Toast.makeText(this@Home,"denied", Toast.LENGTH_LONG).show()
                }
                //but you can ask them again, eg:

                AlertDialog.Builder(this@Home)
                    .setMessage("Please accept our permissions")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain()
                    }
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show();

                if (e.hasForeverDenied()) {
                    finish()
                    e.goToSettings()
                }
            }
        }
    }


    fun selectImage(){
        val intents = Intent()
        intents.type = "image/*"
        intents.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intents, "Select Picture"), GALLERY)

    }

    fun startCamera() {
        // var file: File
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //    if(imageUri==null){

        //      imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.classgist.facerecognition.provider", file);

        //    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //  }
        // else
        //{
        //  intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        //}

        //  if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {

        startActivityForResult(cameraIntent, CAMERA_REQUEST)

        //}


    }








    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startCamera()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Permission Denied!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initViews() {

        selectcamera.setOnClickListener(this)
        selectgallery.setOnClickListener(this)
        selectRandom.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {


            R.id.selectcamera -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE ),
                    REQUEST_WRITE_PERMISSION
                )
            }

            R.id.selectgallery ->{
                selectImage()
            }

            R.id.selectRandom -> {
                search(random())
            }

        }
    }
    fun random():Int{
        return  (0..10).random()

    }
    fun setSlide(){
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.first))
        imageList.add(SlideModel(R.drawable.second))
        imageList.add(SlideModel(R.drawable.third))
        imageList.add(SlideModel(R.drawable.four))
        imageList.add(SlideModel(R.drawable.five))
        imageList.add(SlideModel(R.drawable.six))
        imageList.add(SlideModel(R.drawable.seven))
        imageList.add(SlideModel(R.drawable.eight))
        imageList.add(SlideModel(R.drawable.nine))
        imageList.add(SlideModel(R.drawable.ten))
        imageList.add(SlideModel(R.drawable.eight))
        image_slider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        image_slider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                search(position)
            }
        })

    }

    fun search(position: Int){
        when (position) {


            0 -> {
                startRecognision(R.drawable.first)
            }
            1 -> {
                startRecognision(R.drawable.second)
            }

            2 -> {
                startRecognision(R.drawable.third)
            }
            3 -> {
                startRecognision(R.drawable.four)
            }
            4 -> {
                startRecognision(R.drawable.five)
            }
            5 -> {
                startRecognision(R.drawable.six)
            }
            6 -> {
                startRecognision(R.drawable.seven)
            }
            7 -> {
                startRecognision(R.drawable.eight)
            }
            8 -> {
                startRecognision(R.drawable.nine)
            }
            9 -> {
                startRecognision(R.drawable.ten)
            }
            10 -> {
                startRecognision(R.drawable.eleven)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            imageUri = data!!.data
            val imageBitmap = data.extras!!.get("data") as Bitmap
            startRecognision(imageBitmap)
//            val photo = data!!.getExtras()!!.get("data") as Bitmap

        }

        if (requestCode == GALLERY) {
            imageUri = data!!.data
            startRecognision(imageUri!!)
        }
    }

    fun startRecognision(int:Int){
        val intents = Intent(this@Home, MainActivity::class.java)
        intents.putExtra("imageInt",int)
        startActivity(intents)

    }

    fun startRecognision(bitmap: Bitmap){
        val intents = Intent(this@Home, MainActivity::class.java)
        intents.putExtra("imageBitmap",bitmap)
        startActivity(intents)

    }


    fun startRecognision(bitmap: Uri){
        val intents = Intent(this@Home, MainActivity::class.java)
        intents.putExtra("imageUri",bitmap)
        startActivity(intents)

    }
}