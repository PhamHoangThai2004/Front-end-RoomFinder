package com.pht.roomfinder.repositories

import com.pht.roomfinder.model.User
import com.pht.roomfinder.response.AuthResponse
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(private val userService: UserService) {
    private val token = "Bearer ${DataLocal.getInstance().getString(Const.TOKEN)}"

    suspend fun login(user: User): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.login(user)

                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Login failed: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkToken(token: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.checkToken(token)

                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(
                        Exception(
                            "Check token failed: ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(user: User): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.register(user)

                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Register failed: ${response.errorBody()?.string()}"))

                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkOTP(email: String, otp: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.checkOTP(email, otp)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Check OTP failed: ${response.errorBody()?.string()}"))
                }
                Result.success(response.body()!!)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun forgotPassword(email: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.forgotPassword(email)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else
                    Result.failure(
                        Exception("Forgot password failed: ${response.errorBody()?.string()}")
                    )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun confirmEmail(email: String, otp: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.confirmEmail(email, otp)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)

                } else {
                    Result.failure(
                        Exception("Confirm email failed: ${response.errorBody()?.string()}")
                    )
                }
                Result.success(response.body()!!)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createPassword(email: String, newPassword: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> =
                    userService.createPassword(email, newPassword)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else
                    Result.failure(
                        Exception("Create password failed: ${response.errorBody()?.string()}")
                    )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun changeInformation(token: String, user: User): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.changeInformation(token, user)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else
                    Result.failure(
                        Exception("Change information failed: ${response.errorBody()?.string()}")
                    )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun changePassword(
        token: String,
        oldPassword: String,
        newPassword: String
    ): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> =
                    userService.changePassword(token, oldPassword, newPassword)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else
                    Result.failure(
                        Exception("Change password failed: ${response.errorBody()?.string()}")
                    )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun changeAvatar(avatar: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = userService.changeAvatar(this@AuthRepository.token, avatar)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else
                    Result.failure(
                        Exception("Change avatar failed: ${response.errorBody()?.string()}")
                    )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}
