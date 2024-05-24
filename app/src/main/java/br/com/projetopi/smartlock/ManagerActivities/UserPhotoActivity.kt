package br.com.projetopi.smartlock.ManagerActivities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.R
import br.com.projetopi.smartlock.databinding.ActivityUserPhotoBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPhotoBinding
    private lateinit var cameraProviderFeature: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        db = Firebase.firestore
        val uNumber = intent.getStringExtra("nUser")
        val qrCode = intent.getStringExtra("qrCode")


        startCamera()

        binding.btnFoto.setOnClickListener{
            blinkPreview()
            takePicture(uNumber.toString().toInt(), qrCode.toString())
        }

        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun startCamera(){
        cameraProviderFeature.addListener({

            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFeature.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Falha ao abrir camera") }
        }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun takePicture(uNumber: Int, qrCode: String) {
        imageCapture?.let {
            val fileName = "FOTO_JPEG_${System.currentTimeMillis()}.jpeg"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("CamerPreview", "A imagem foi salve no diretório: ${file.toURI()}")

                        try{
                            db.collection("rentals")
                                .document(qrCode)
                                .get()
                                .addOnSuccessListener {
                                    val userPhoto = hashMapOf(
                                        if(uNumber == 1){
                                            "user1Photo" to file.absolutePath
                                        }else{
                                            "user2Photo" to file.absolutePath
                                        }
                                    )

                                        it.reference.update(userPhoto as Map<String, Any>)

                                }
                        }catch(e: Exception){
                            Log.e("Firebase", "Erro ao salvar no firevase: $e")
                        }

                        val intent = Intent(this@UserPhotoActivity, WriteUserActivity::class.java)
                        intent.putExtra("Image", file.absolutePath)
                        intent.putExtra("qrCode", qrCode)
                        intent.putExtra("nUser", uNumber.toString())
                        startActivity(intent)
                        finish()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Erro ao salvar foto",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("CameraPreview", "Erro ao gravar foto: $exception")
                    }

                }
            )
        }
    }

    private fun blinkPreview(){
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }


}