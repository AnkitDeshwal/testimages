package com.example.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageCache(private val cacheDir: File) {

    init {
        // Ensure that the cache directory exists
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    suspend fun getImage(url: String): Bitmap? {
        // Check memory cache
        val memoryCacheKey = url.hashCode().toString()
        val memoryCachedBitmap = MemoryCache.get(memoryCacheKey)
        if (memoryCachedBitmap != null) {
            return memoryCachedBitmap
        }

        // Check disk cache
        val diskCachedBitmap = withContext(Dispatchers.IO) {
            val file = File(cacheDir, url.hashCode().toString())
            if (file.exists()) {
                FileInputStream(file).use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                null
            }
        }
        diskCachedBitmap?.let {
            // Cache in memory
            MemoryCache.put(memoryCacheKey, it)
            return it
        }

        return null
    }

    suspend fun saveImage(url: String, bitmap: Bitmap) {
        val memoryCacheKey = url.hashCode().toString()

        // Cache in memory
        MemoryCache.put(memoryCacheKey, bitmap)

        // Cache on disk
        withContext(Dispatchers.IO) {
            val file = File(cacheDir, url.hashCode().toString())
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
    }

    object MemoryCache {
        private val cache = LinkedHashMap<String, Bitmap>(10, 0.75f, true)
        private const val MAX_MEMORY_CACHE_SIZE = 10

        fun get(key: String): Bitmap? {
            return cache[key]
        }

        fun put(key: String, bitmap: Bitmap) {
            cache[key] = bitmap
            if (cache.size > MAX_MEMORY_CACHE_SIZE) {
                // Remove the eldest entry
                val iterator = cache.entries.iterator()
                iterator.next()
                iterator.remove()
            }
        }
    }
}