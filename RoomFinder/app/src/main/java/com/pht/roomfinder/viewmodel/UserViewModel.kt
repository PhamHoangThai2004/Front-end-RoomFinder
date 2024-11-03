package com.pht.roomfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pht.roomfinder.model.User

class UserViewModel : ViewModel() {
    val user = MutableLiveData<User>()
}