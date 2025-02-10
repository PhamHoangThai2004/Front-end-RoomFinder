package com.pht.roomfinder.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Category
import com.pht.roomfinder.model.Images
import com.pht.roomfinder.model.Location
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.CategoryRepository
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.response.Area
import com.pht.roomfinder.services.AreaService
import com.pht.roomfinder.services.CategoryService
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.CloudinaryConfig
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.launch

class NewPostViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository(CategoryService.categoryService)
    private val postRepository = PostRepository(PostService.postService)

    val listCategories = MutableLiveData<MutableList<String>>()
    val listProvinces = MutableLiveData<List<Area>>()
    val listDistricts = MutableLiveData<List<Area>>()
    val listWards = MutableLiveData<List<Area>>()
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    var province = MutableLiveData<String>()
    val district = MutableLiveData<String>()
    val ward = MutableLiveData<String>()
    val village = MutableLiveData<String>()

    // user
    val user = MutableLiveData<User>()

    //category
    val categoryName = MutableLiveData<String>()

    // location
    val address = MutableLiveData<String>()
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()
    val locationIsNull = MutableLiveData<Boolean>()

    // post
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val acreage = MutableLiveData<String>()
    val bonus = MutableLiveData<String>()

    //images
    val images = MutableLiveData<MutableList<String>>()
    private val listImages = MutableLiveData<List<Images>>()

    fun getListCategories() {
        viewModelScope.launch {
            val result = categoryRepository.listCategory()
            if (result.isSuccess) {
                val response = result.getOrNull()
                val list = mutableListOf<String>()
                response?.let {
                    for (item in response.data) {
                        list.add(item.categoryName!!)
                    }
                }
                listCategories.value = list
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get list category: ${error?.message}")
            }
        }
    }

    fun getProvinces() {
        viewModelScope.launch {
            try {
                val response = AreaService.areaService.getProvinces(-1)
                listProvinces.value = response.data.data
            } catch (e: Exception) {
                Log.d("BBB", "get provinces: ${e.message}")
            }
        }
    }

    fun getDistricts(provinceCode: String) {
        viewModelScope.launch {
            try {
                val response = AreaService.areaService.getDistricts(provinceCode, -1)
                listDistricts.value = response.data.data
            } catch (e: Exception) {
                Log.d("BBB", "get districts: ${e.message}")
            }
        }
    }

    fun getWards(districtCode: String) {
        viewModelScope.launch {
            try {
                val response = AreaService.areaService.getWards(districtCode, -1)
                listWards.value = response.data.data
            } catch (e: Exception) {
                Log.d("BBB", "get wards: ${e.message}")
            }
        }
    }

    fun getLocation() {
        address.value = ""
        if (!village.value.isNullOrEmpty()) address.value = village.value!! + ", "
        address.value += ward.value + ", " + district.value + ", " + province.value

        viewModelScope.launch {
            val result = Const.getLatLngFromAddress(address.value!!)
            if (result != null) {
                val (latitude, longitude) = result
                this@NewPostViewModel.latitude.value = latitude
                this@NewPostViewModel.longitude.value = longitude
                locationIsNull.value = false
            } else {
                locationIsNull.value = true
            }
        }
    }

    private fun checkValid(): Boolean {
        if (locationIsNull.value == true) return false
        if (title.value.isNullOrEmpty() || title.value!!.trim().length < 30 || title.value!!.trim().length > 100) return false
        if (description.value.isNullOrEmpty() || description.value!!.trim().length < 50 || description.value!!.trim().length > 3000) return false
        if (price.value.isNullOrEmpty()) return false
        if (acreage.value.isNullOrEmpty()) return false
        if ((bonus.value?.trim()?.length ?: 0) > 200) return false
        return true
    }

    fun posting() {
        viewModelScope.launch {
            if (!checkValid()) {
                Toast.makeText(App.getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                isLoading.value = true
                if (images.value.isNullOrEmpty()) listImages.value = listOf()
                else listImages.value = CloudinaryConfig().uploadMultipleImages(images.value!!)
                createNewPost()
            }
        }
    }

    private suspend fun createNewPost() {
        Log.d("BBB", "create new post")
        var area = province.value
        if (province.value == "Hồ Chí Minh") area = "TP. Hồ Chí Minh"
        val category = Category(null, categoryName.value)
        val location = Location(null, address.value, longitude.value, latitude.value)
        val post = Post(
            null, null, category, location, title.value!!.trim(), description.value!!.trim(),
            price.value?.toDouble(), acreage.value?.toDouble(), area, bonus.value?.trim() ?: "",
            null, null, listImages.value, null, null
        )
        try {
            val result = postRepository.newPost(post)
            isLoading.value = false
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    if (it.status) {
                        Toast.makeText(App.getContext(), "Đăng bài thành công", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(App.getContext(), "Đăng bài thất bại", Toast.LENGTH_SHORT)
                            .show()
                    }
                    isSuccess.value = it.status
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "create new post: ${error?.message}")
            }
        } catch (e: Exception) {
            isLoading.value = false
            Log.d("BBB", "create new post: ${e.message}")
        }
    }
}