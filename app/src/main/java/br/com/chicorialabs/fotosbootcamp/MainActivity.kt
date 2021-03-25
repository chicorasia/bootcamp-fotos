package br.com.chicorialabs.fotosbootcamp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import br.com.chicorialabs.fotosbootcamp.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val pickBtn: MaterialButton by lazy {
        binding.pickBtn
    }

    private val imageView: ImageView by lazy {
        binding.imageview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickBtn.setOnClickListener {
            //verifica a versão do SDK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission: Array<String> =
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE)
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
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED
                )
                    pickImageFromGallery()
            }
            else -> {
                Toast.makeText(this, "Operação não autorizada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let {
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                imageView.setImageURI(it.data)
            }
        }

    }


    companion object {
        private const val PERMISSION_CODE = 1000
        private const val IMAGE_PICK_CODE = 1001
    }
}