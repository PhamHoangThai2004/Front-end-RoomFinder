package com.pht.roomfinder.repositories

import com.pht.roomfinder.response.CategoryResponse
import com.pht.roomfinder.services.CategoryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class CategoryRepository(private val categoryService: CategoryService) {

    suspend fun listCategory(): Result<CategoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<CategoryResponse> = categoryService.listCategory()
                if (response.isSuccessful) Result.success(response.body()!!)
                else Result.failure(
                    Exception("List category failed: ${response.errorBody()?.string()}")
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}