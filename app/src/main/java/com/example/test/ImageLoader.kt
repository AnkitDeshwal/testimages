package com.example.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ImageLoader(private val cache: ImageCache) {

    fun loadImage(url: String, imageView: ImageView) {
        GlobalScope.launch(Dispatchers.Main) {
            val bitmap = cache.getImage(url) ?: downloadImage(url)
            bitmap?.let {
                imageView.setImageBitmap(it)
            }
        }
    }

    private suspend fun downloadImage(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        }?.also {
            cache.saveImage(url, it)
        }
    }
}