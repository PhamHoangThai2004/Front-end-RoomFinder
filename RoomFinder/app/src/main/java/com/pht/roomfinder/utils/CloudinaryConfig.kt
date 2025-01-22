package com.pht.roomfinder.utils

import android.util.Log
import com.cloudinary.Cloudinary
import com.pht.roomfinder.model.Images
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class CloudinaryConfig {

    suspend fun uploadImage(filePath: String) {
        withContext(Dispatchers.IO) {
            try {
                val cloudinary = Cloudinary(Const.CLOUDINARY_URL)

                val uploadOptions = mapOf(
                    "resource_type" to "image",
                    "quality" to "auto:best",
                    "format" to "jpg"
                )

                val result = cloudinary.uploader().upload(filePath, uploadOptions)
                Log.d("BBB", "Upload ảnh thành công")
                val imageUrl = result["secure_url"] as String
                val publicId = result["public_id"] as String
                Log.d("BBB", imageUrl)
                Log.d("BBB", publicId)
            } catch (e: Exception) {
                Log.d("BBB", "Lỗi upload ảnh: ${e.message}")
            }
        }
    }

    suspend fun deleteImage(publicId: String) {
        withContext(Dispatchers.IO) {
            try {
                val cloudinary = Cloudinary(Const.CLOUDINARY_URL)
                val options = mapOf("invalidate" to true)

                val result = cloudinary.uploader().destroy(publicId, options)
                if (result["result"] == "ok") {
                    Log.d("BBB", "Xóa ảnh thành công")
                } else {
                    Log.d("BBB", "Xóa ảnh thất bại")
                }
            } catch (e: Exception) {
                Log.d("BBB", "Lỗi xóa ảnh: ${e.message}")
            }
        }

    }

    suspend fun uploadMultipleImages(filePaths: List<String>): List<Images> {
        return withContext(Dispatchers.IO) {
            val images = mutableListOf<Images>()
            try {
                val cloudinary = Cloudinary(Const.CLOUDINARY_URL)

                val uploadOptions = mapOf(
                    "resource_type" to "image",
                    "quality" to "auto:best",
                    "format" to "jpg"
                )

                val results = filePaths.map { filePath ->
                    async {
                        cloudinary.uploader().upload(filePath, uploadOptions)
                    }
                }.awaitAll()
                results.forEachIndexed { index, result ->
                    val imageUrl = result["secure_url"] as String
                    val publicId = result["public_id"] as String
                    images.add(Images(null, imageUrl, publicId))
                    Log.d("BBB", "Ảnh ${index + 1}: URL = $imageUrl, Public ID = $publicId")
                }
            } catch (e: Exception) {
                Log.d("BBB", "Lỗi upload ảnh: ${e.message}")

            }
            images
        }
    }

    suspend fun deleteMultipleImages(publicIds: List<String>) {
        withContext(Dispatchers.IO) {
            try {
                val cloudinary = Cloudinary(Const.CLOUDINARY_URL)
                val options = mapOf("invalidate" to true)

                val results = publicIds.map { publicId ->
                    async {
                        cloudinary.uploader().destroy(publicId, options)
                    }
                }.awaitAll()

                results.forEachIndexed { index, result ->
                    val status = result["result"] as String
                    if (status == "ok") {
                        Log.d("BBB", "Ảnh ${publicIds[index]} đã xóa thành công")
                    } else {
                        Log.d("BBB", "Lỗi khi xóa ảnh ${publicIds[index]}")
                    }
                }
            } catch (e: Exception) {
                Log.d("BBB", "Lỗi xóa ảnh: ${e.message}")
            }
        }
    }


}