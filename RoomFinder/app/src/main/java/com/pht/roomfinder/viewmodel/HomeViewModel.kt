package com.pht.roomfinder.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.helper.PostState
import com.pht.roomfinder.helper.SearchHistoryManager
import com.pht.roomfinder.helper.UIState
import com.pht.roomfinder.model.Category
import com.pht.roomfinder.model.Notification
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.repositories.CategoryRepository
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.response.ListGroupData
import com.pht.roomfinder.services.CategoryService
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.services.SQLiteService
import com.pht.roomfinder.utils.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Suppress("SameParameterValue")
class HomeViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository(CategoryService.categoryService)
    private val postRepository = PostRepository(PostService.postService)
    private val searchHistoryManager = SearchHistoryManager(App.getContext()!!)

    private val _selectedPost = MutableStateFlow(0)
    val selectedPost = _selectedPost.asStateFlow()

    val keySearch = MutableLiveData<String>()
    val tmpSearch = MutableLiveData<String>()
    val notificationCount = MutableLiveData(0)
    val toLayout = MutableLiveData<Int>()

    val uiState = MutableStateFlow(UIState())
    val uiStateLiveData: LiveData<UIState> = uiState.asLiveData()
    val postState = MutableStateFlow(PostState())

    val listGroupData = MutableLiveData<List<ListGroupData>>()
    val listSearch = MutableLiveData<List<Post>>()
    val listSearchHistory = MutableLiveData<List<String>>()
    val listNotification = MutableLiveData<List<Notification>>()
    val listCategory = MutableLiveData<MutableList<String>>()

    init {
        postState.value = postState.value.copy(
            categoryName = "Tất cả",
            area = "Toàn quốc",
            price = "Tất cả",
            acreage = "Tất cả"
        )
    }

    companion object {
        const val HOME = 0
        const val SEARCH = 1
        const val FILTER = 2
        const val BONUS = 3
        const val FIND_ROOMMATES = 4
    }

    fun setSelectedPost(postId: Int) {
        _selectedPost.value = postId
    }

    fun toLayout(value: Int) {
        toLayout.value = value
        setShowBottomNavigation(false)
    }

    fun setShowBottomNavigation(isShow: Boolean) {
        uiState.value = uiState.value.copy(isShowBottomNavigation = isShow)
    }

    fun popBack() {
        if (toLayout.value == FILTER) openFilter(false)
        toLayout.value = HOME
    }

    fun getListGroupData() {
        if (listGroupData.value == null) listGroupData()
    }

    fun search() {
        val keySearch = keySearch.value.toString().trim()
        if (keySearch.isNotEmpty()) getListSearch(keySearch)
    }

    fun openFilter(isOpen: Boolean) {
        uiState.value = uiState.value.copy(isOpen = isOpen)
    }

    fun getFilterValue() {
        val valuePrices = filterNumber(postState.value.price!!)
        val valueAcreage = filterNumber(postState.value.acreage!!)
        getPostFilter(
            postState.value.categoryName!!,
            postState.value.area!!,
            valuePrices[0],
            valuePrices[1],
            valueAcreage[0],
            valueAcreage[1]
        )
    }

    private fun setLoading(isLoading: Boolean) {
        uiState.value = uiState.value.copy(isLoading = isLoading)
    }

    fun getListCategory() {
        viewModelScope.launch {
            val result = categoryRepository.listCategory()
            if (result.isSuccess) {
                val response = result.getOrNull()
                var list = listOf<Category>()
                response?.let {
                    list = it.data
                }
                listCategory.value = mutableListOf("Tất cả")
                for (item in list) {
                    listCategory.value?.add(item.categoryName!!)
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListCategory: ${error?.message}")
            }
        }
    }

    private fun listGroupData() {
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.getListGroup()
            setLoading(false)
            if (result.isSuccess) {
                val postListResponse = result.getOrNull()
                postListResponse?.let {
                    listGroupData.value = it.data
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListGroupData: ${error?.message}")
            }
        }
    }

    fun clearSearchHistory() {
        searchHistoryManager.clearKeySearch()
        listSearchHistory.value = emptyList()
    }

    private fun getListSearch(keySearch: String) {
        searchHistoryManager.saveSearchHistory(keySearch)
        listSearchHistory.value = searchHistoryManager.loadSearchHistory()
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.getListSearch(keySearch)
            setLoading(false)
            if (result.isSuccess) {
                val searchResponse = result.getOrNull()
                searchResponse?.let {
                    listSearch.value = it.data
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListSearch: ${error?.message}")
            }
        }
    }

    fun getAllNotifications() {
        val db = SQLiteService(App.getContext()!!)
        val cursor = db.getAllNotifications()
        val list = mutableListOf<Notification>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val message = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                val postId = cursor.getInt(cursor.getColumnIndexOrThrow("postId"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))

                list.add(Notification(id, title, message, postId, time))
            } while (cursor.moveToNext())
        }
        cursor.close()
        listNotification.value = list
    }

    fun getLists(typeSearch: Int, area: String = "Toàn quốc") {
        if (typeSearch == 1) listSearchHistory.value = searchHistoryManager.loadSearchHistory()
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.getListRandom(typeSearch, area)
            setLoading(false)
            if (result.isSuccess) {
                val searchResponse = result.getOrNull()
                searchResponse?.let {
                    listSearch.value = it.data
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListRandom: ${error?.message}")
            }
        }
    }

    private fun getPostFilter(
        categoryName: String,
        area: String,
        minPrice: Int,
        maxPrice: Int,
        minAcreage: Int,
        maxAcreage: Int
    ) {
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.postFilter(
                categoryName,
                area,
                minPrice,
                maxPrice,
                minAcreage,
                maxAcreage
            )
            setLoading(false)
            if (result.isSuccess) {
                val searchResponse = result.getOrNull()
                searchResponse?.let {
                    listSearch.value = it.data
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get list filter: ${error?.message}")
            }

        }
    }

    fun clearNotification() {
        DataLocal.getInstance().putInt("notification", 0)
        notificationCount.value = 0
        val db = SQLiteService(App.getContext()!!)
        db.deleteAllNotifications()
        getAllNotifications()
    }

    fun setNotificationView(value: Boolean) {
        uiState.value = uiState.value.copy(isNotificationVisible = value)
    }

    private fun filterNumber(value: String): List<Int> {
        if (value == "Tất cả") {
            val list = listOf(-1, -1)
            return list
        }

        if (value.indexOf("Dưới") != -1) {
            val list = listOf(-1, value.replace(Regex("[^\\d.]"), "").toInt())
            return list
        }

        if (value.indexOf("Trên") != -1) {
            val list = listOf(value.replace(Regex("[^\\d.]"), "").toInt(), -1)
            return list
        }

        val parts = value.split("-").map { it.trim() }
        val list = listOf(
            parts[0].replace(Regex("[^\\d.]"), "").toInt(),
            parts[1].replace(Regex("[^\\d.]"), "").toInt()
        )

        return list
    }

    fun deleteNotification(id: Int) {
        val count = notificationCount.value
        notificationCount.value = if (count!! > 0) {
            count - 1
        } else {
            0
        }
        DataLocal.getInstance().putInt("notification", notificationCount.value!!)
        val db = SQLiteService(App.getContext()!!)
        db.deleteNotification(id)
        getAllNotifications()
    }

}