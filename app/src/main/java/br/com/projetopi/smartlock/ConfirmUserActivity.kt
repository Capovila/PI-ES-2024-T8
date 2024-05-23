package br.com.projetopi.smartlock

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetopi.smartlock.databinding.ActivityConfirmUserBinding
import java.io.File

class ConfirmUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmUserBinding
    private lateinit var pathBytes: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imagePath = intent.getStringExtra("Image")

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnNext.setOnClickListener{
            startActivity(Intent(this, LockerOptionsActivity::class.java))
        }

        imagePath?.let { path ->
            // Verificar se o arquivo existe
            if (File(path).exists()) {
                // Decodificar o arquivo de imagem em um Bitmap
                var bitmap = BitmapFactory.decodeFile(File(path).absolutePath)
                val matrix = Matrix()
                matrix.postRotate(90f)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                pathBytes = path.toByteArray(Charsets.UTF_8)

                // Definir o Bitmap no ImageView
                binding.userPhoto.setImageBitmap(bitmap)
            }else{
                Toast.makeText(this, "NFC sem usuário gravado", Toast.LENGTH_LONG).show()
            }
        }

    }
}