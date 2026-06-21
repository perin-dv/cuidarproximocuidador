package com.mesawa.cuidarproximocuidador.ui.perfil

import android.graphics.BitmapFactory
import android.widget.ImageView
import java.net.URL
import kotlin.concurrent.thread

object ImagemUrlLoader {
    fun carregar(imageView: ImageView, url: String) {
        if (url.isBlank()) return
        thread {
            runCatching {
                URL(url).openStream().use { input -> BitmapFactory.decodeStream(input) }
            }.onSuccess { bitmap ->
                imageView.post {
                    if (bitmap != null) imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}
