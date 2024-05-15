package br.com.projetopi.smartlock

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.databinding.ActivityLiberarBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService

class LiberarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiberarBinding
    private lateinit var cameraProviderFeature: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiberarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

        startCamera()

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


}