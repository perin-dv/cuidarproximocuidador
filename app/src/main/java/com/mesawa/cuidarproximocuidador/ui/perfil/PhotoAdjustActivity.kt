package com.mesawa.cuidarproximocuidador.ui.perfil

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.mesawa.cuidarproximocuidador.R
import java.io.File
import java.io.FileOutputStream

class PhotoAdjustActivity : FragmentActivity() {

    private lateinit var cropView: CropPhotoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_adjust)

        cropView = findViewById(R.id.cropPhotoView)
        findViewById<Button>(R.id.buttonCancelarFoto).setOnClickListener { finish() }
        findViewById<Button>(R.id.buttonSalvarFotoAjustada).setOnClickListener { salvarFotoAjustada() }

        val uri = intent.getStringExtra(EXTRA_URI)?.let(Uri::parse)
        if (uri == null) {
            Toast.makeText(this, "Não consegui abrir a foto.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        carregarImagem(uri)
    }

    private fun carregarImagem(uri: Uri) {
        runCatching {
            contentResolver.openInputStream(uri).use { input ->
                BitmapFactory.decodeStream(input)
            }
        }.onSuccess { bitmap ->
            if (bitmap == null) {
                Toast.makeText(this, "Imagem inválida.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                cropView.setBitmap(bitmap)
            }
        }.onFailure {
            Toast.makeText(this, "Não consegui ler a foto.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun salvarFotoAjustada() {
        val output = File(cacheDir, "perfil_foto_ajustada.jpg")
        FileOutputStream(output).use { stream ->
            cropView.crop().compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, stream)
        }
        setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT_URI, Uri.fromFile(output).toString()))
        finish()
    }

    companion object {
        const val EXTRA_URI = "foto_uri"
        const val EXTRA_RESULT_URI = "foto_result_uri"
    }
}
