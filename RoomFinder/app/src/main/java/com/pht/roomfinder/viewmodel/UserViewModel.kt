package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
import com.pht.roomfinder.helper.CloudinaryConfig
import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.helper.Secure
import com.pht.roomfinder.helper.UIState
import com.pht.roomfinder.helper.UserState
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class UserViewModel : ViewModel() {
    private val authRepository = AuthRepository(UserService.userService)
    private val context = App.getContext()!!

    val uiState = MutableStateFlow(UIState())
    val userState = MutableStateFlow(UserState())
    val toLayout = MutableLiveData<Int?>()
    val user = MutableLiveData<User>()
    val avatar = MutableLiveData<String?>()
    val imagePath = MutableLiveData<String>()

    companion object {
        const val INFORMATION_ACCOUNT = 0
        const val AVATAR = 1
        const val LOCATION = 2
        const val SAVE_ACCOUNT = 3
        const val CHANGE_PASSWORD = 4
        const val THEME = 6
        const val CONTACT_SUPPORT = 7
        const val LOGOUT = 8
        const val OPTION = 9
        const val NEW_POST = 10
    }

    fun setUpdate(isUpdate: Boolean) {
        uiState.value = uiState.value.copy(isUpdate = isUpdate)
        if (!isUpdate) {
            userState.value = userState.value.copy(
                name = user.value?.name,
                phone = user.value?.phoneNumber,
                address = user.value?.address
            )
            avatar.value = user.value?.avatar
        }
    }

    private fun setLoading(isLoading: Boolean) {
        uiState.value = uiState.value.copy(isLoading = isLoading)
    }

    fun logout() {
        Secure.clear(context)
        DataLocal.getInstance().clear()
    }

    fun saveAccount(email: String?, password: String?, isSave: Boolean) {
        if (isSave) {
            Secure.save(context, email, password)
        } else {
            Secure.clear(context)
        }
        DataLocal.getInstance().putBoolean(Const.SAVE_ACCOUNT, isSave)
    }

    fun checkInformation() {
        val user = user.value
        user?.let {
            it.name = userState.value.name?.trim()
            it.phoneNumber = userState.value.phone?.trim()
            it.address = userState.value.address?.trim()
            if (it.checkName() && it.checkPhoneNumber() && it.checkAddress()) changeInformation(user)
            else {
                Toast.makeText(
                    context,
                    context.getString(R.string.invalid_information),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun checkPassword() {
        val oldPassword = userState.value.oldPassword?.trim() ?: ""
        val newPassword = userState.value.newPassword?.trim() ?: ""
        val confirmPassword = userState.value.confirmPassword?.trim() ?: ""

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(
                context,
                context.getString(R.string.full_information_required),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (newPassword.length < 8) {
                Toast.makeText(
                    context,
                    context.getString(R.string.invalid_password),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (newPassword != confirmPassword
            ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.password_not_match),
                    Toast.LENGTH_SHORT
                ).show()
            } else changePassword(oldPassword, newPassword)
        }
    }

    fun toNewPost() {
        toLayout.value = NEW_POST
        toLayout.value = null
    }

    fun uploadImage() {
        val avatar = user.value?.avatar
        viewModelScope.launch {
            val imagePath = imagePath.value
            if (imagePath.isNullOrEmpty().not()) {
                setLoading(true)
                if (avatar != null) {
                    val image = avatar.substringAfterLast("/")
                    val publicId = image.substringBeforeLast(".")
                    val result = CloudinaryConfig().deleteImage(publicId)
                    if (result) {
                        val imageUrl = CloudinaryConfig().uploadImage(imagePath!!)
                        changeAvatar(imageUrl)
                    } else {
                        setLoading(false)
                        setUpdate(false)
                        Toast.makeText(
                            context,
                            context.getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val imageUrl = CloudinaryConfig().uploadImage(imagePath!!)
                    changeAvatar(imageUrl)
                }
            } else {
                setUpdate(false)
                Toast.makeText(
                    context,
                    context.getString(R.string.select_photo),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun changeAvatar(imageUrl: String) {
        if (imageUrl == "") {
            setUpdate(false)
            setLoading(false)
        } else {
            val user = user.value
            user?.avatar = imageUrl
            val result = authRepository.changeAvatar(imageUrl)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.change_avatar_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        this@UserViewModel.user.value = user
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.change_avatar_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "change avatar: ${error?.message}")
            }
            setLoading(false)
            setUpdate(false)
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val token = "Bearer ${DataLocal.getInstance().getString(Const.TOKEN)}"
        viewModelScope.launch {
            val result = authRepository.changePassword(token, oldPassword, newPassword)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    var message = context.getString(R.string.password_change_failed)
                    if (it.status) {
                        message = context.getString(R.string.password_changed_successfully)
                        DataLocal.getInstance().putString(Const.TOKEN, "")
                        Secure.encryptPassword(context, newPassword)
                        DataLocal.getInstance().putBoolean(Const.SAVE_ACCOUNT, false)
                        popBack()
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                val error = result.exceptionOrNull()
                Toast.makeText(App.getContext(), error?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeInformation(user: User) { //Fix lại api
        val token = "Bearer ${DataLocal.getInstance().getString(Const.TOKEN)}"
        viewModelScope.launch {
            val result = authRepository.changeInformation(token, user)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    var message = context.getString(R.string.change_infor_failed)
                    if (it.status) {
                        message = context.getString(R.string.change_infor_successfully)
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        this@UserViewModel.user.value = user
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "change information: ${error?.message}")
                Toast.makeText(
                    context,
                    context.getString(R.string.change_infor_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            setUpdate(false)
        }
    }

    fun popBack() {
        if (toLayout.value == CHANGE_PASSWORD) cancelChangePassword()
        toLayout.value = OPTION
    }

    fun toAvatar() {
        toLayout.value = AVATAR
        setUpdate(false)
    }

    fun toLayout(value: Int) {
        toLayout.value = value
        setShowBottomNavigation(false)
    }

    fun setShowBottomNavigation(isShow: Boolean) {
        uiState.value = uiState.value.copy(isShowBottomNavigation = isShow)
    }

    fun cancelChangePassword() {
        userState.value =
            userState.value.copy(oldPassword = null, newPassword = null, confirmPassword = null)
    }

}