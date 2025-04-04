package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
import com.pht.roomfinder.helper.CloudinaryConfig
import com.pht.roomfinder.helper.PostState
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
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class NewPostViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository(CategoryService.categoryService)
    private val postRepository = PostRepository(PostService.postService)
    val context = App.getContext()!!

    val postState = MutableStateFlow(PostState())

    val listCategories = MutableLiveData<MutableList<String>>()
    val listProvinces = MutableLiveData<List<Area>>()
    val listDistricts = MutableLiveData<List<Area>>()
    val listWards = MutableLiveData<List<Area>>()

    var user: User? = null
    val province = MutableLiveData<String?>()
    val district = MutableLiveData<String?>()
    val ward = MutableLiveData<String?>()
    val village = MutableLiveData<String?>()

    val images = MutableLiveData<MutableList<String>>()
    private val listImages = MutableLiveData<List<Images>>()

    private fun setLoading(isLoading: Boolean) {
        postState.value = postState.value.copy(isLoading = isLoading)
    }

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
        var address = ""
        if (!village.value.isNullOrEmpty()) address = village.value + ", "
        address += ward.value + ", " + district.value + ", " + province.value

        postState.value = postState.value.copy(address = address)

        viewModelScope.launch {
            val result = Const.getLatLngFromAddress(address)
            if (result != null) {
                val (latitude, longitude) = result
                postState.value = postState.value.copy(
                    latitude = latitude,
                    longitude = longitude,
                    locationIsNull = false
                )
            } else {
                postState.value = postState.value.copy(locationIsNull = true)
            }
        }
    }

    private fun checkValid(): Boolean {
        val title = postState.value.title
        val description = postState.value.description
        val price = postState.value.price
        val acreage = postState.value.acreage
        val bonus = postState.value.bonus

        if (postState.value.locationIsNull) return false
        if (title.isNullOrEmpty() || title.trim().length < 30 || title.trim().length > 100) return false
        if (description.isNullOrEmpty() || description.trim().length < 50 || description.trim().length > 3000) return false
        if (price.isNullOrEmpty()) return false
        if (acreage.isNullOrEmpty()) return false
        if ((bonus?.trim()?.length ?: 0) > 200) return false
        return true
    }

    fun posting() {
        viewModelScope.launch {
            if (!checkValid()) {
                Toast.makeText(
                    App.getContext(),
                    context.getString(R.string.full_information_required),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setLoading(true)
                if (images.value.isNullOrEmpty()) listImages.value = listOf()
                else listImages.value = CloudinaryConfig().uploadMultipleImages(images.value!!)
                createNewPost()
            }
        }
    }

    private suspend fun createNewPost() {
        var area = province.value
        if (province.value == "Hồ Chí Minh") area = "TP. Hồ Chí Minh"
        val category = Category(null, postState.value.categoryName)
        val location = Location(
            null,
            postState.value.address,
            postState.value.longitude,
            postState.value.latitude
        )
        val post = Post(
            null, null, category, location,
            postState.value.title?.trim(),
            postState.value.description?.trim(),
            postState.value.price?.toDouble(),
            postState.value.acreage?.toDouble(),
            area, postState.value.bonus?.trim() ?: "",
            null, null, listImages.value, null, null
        )
        try {
            val result = postRepository.newPost(post)
            setLoading(false)
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    if (it.status) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.posting_success),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.posting_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    postState.value = postState.value.copy(isSuccess = it.status)
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "create new post: ${error?.message}")
            }
        } catch (e: Exception) {
            setLoading(false)
            Log.d("BBB", "create new post: ${e.message}")
        }
    }
}