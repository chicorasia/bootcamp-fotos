package br.com.chicorialabs.fotosbootcamp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import br.com.chicorialabs.fotosbootcamp.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var image_uri: Uri? = null

    private val pickBtn: MaterialButton by lazy {
        binding.pickBtn
    }

    private val takePictureBtn: MaterialButton by lazy {
        binding.takePictureBtn
    }

    private val imageView: ImageView by lazy {
        binding.imageview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPickBtn()
        takePictureBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permissions, PERMISSION_CODE_CAMERA_CAPTURE)
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }
        }
    }

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "foto capturada pela câmera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)


        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE)

    }

    private fun initPickBtn() {
        pickBtn.setOnClickListener {
            //verifica a versão do SDK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission: Array<String> =
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_PICK_IMAGE)
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        Intent(Intent.ACTION_PICK).let {
            it.type = "image/*"
            startActivityForResult(it, IMAGE_PICK_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE_PICK_IMAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(
                        this, "Operação não autorizada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            PERMISSION_CODE_CAMERA_CAPTURE -> {
                if (grantResults.size == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    } else {
                        Toast.makeText(
                            this, "Operação não autorizada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let {
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                imageView.setImageURI(it.data)
            }
            if (resultCode == Activity.RESULT_OK && requestCode == OPEN_CAMERA_CODE){
                imageView.setImageURI(image_uri)
            }
        }

    }


    companion object {
        private const val PERMISSION_CODE_PICK_IMAGE = 1000
        private const val IMAGE_PICK_CODE = 1001

        private const val PERMISSION_CODE_CAMERA_CAPTURE = 2000
        private const val OPEN_CAMERA_CODE = 2001
    }
}