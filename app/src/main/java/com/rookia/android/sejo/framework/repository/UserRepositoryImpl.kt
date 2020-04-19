package com.rookia.android.sejo.framework.repository

import androidx.annotation.VisibleForTesting
import com.rookia.android.androidutils.domain.vo.Result
import com.rookia.android.androidutils.framework.repository.resultOnlyFromNetworkInFlow
import com.rookia.android.sejo.data.repository.UserRepository
import com.rookia.android.sejo.domain.network.user.UserCreationRequestClient
import com.rookia.android.sejo.domain.network.user.UserUpdateRequestClient
import com.rookia.android.sejo.framework.network.NetworkServiceFactory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Copyright (C) Rookia - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Roll <raulfeliz@gmail.com>, April 2020
 *
 *
 */

class UserRepositoryImpl @Inject constructor(
    private val networkServiceFactory: NetworkServiceFactory
) : UserRepository {
    override fun createUser(phonePrefix: String, phoneNumber: String, pin: Int): Flow<Result<Int>> =
        resultOnlyFromNetworkInFlow {
            createUserInServer(phonePrefix, phoneNumber, pin)
        }

    @VisibleForTesting
    suspend fun createUserInServer(
        phonePrefix: String,
        phoneNumber: String,
        pin: Int
    ): Result<Int> =
        try {
            val api = networkServiceFactory.getUserInstance()
            val userRequest = UserCreationRequestClient(phonePrefix, phoneNumber, pin)
            val resp = api.addUser(userRequest)
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()?.result)
            } else {
                Result.error(resp.message())
            }
        } catch (e: Exception) {
            Result.error(e.message)
        }


    override fun updateUser(
        phonePrefix: String,
        phoneNumber: String,
        pin: Int,
        token: String
    ): Flow<Result<Int>> =
        resultOnlyFromNetworkInFlow {
            updateUserInServer(phonePrefix, phoneNumber, pin, token)
        }


    @VisibleForTesting
    suspend fun updateUserInServer(
        phonePrefix: String,
        phoneNumber: String,
        pin: Int,
        token: String
    ): Result<Int> =
        try {
            val api = networkServiceFactory.getUserInstance()
            val updateUserValidation = UserUpdateRequestClient(phonePrefix, phoneNumber, pin, token)
            val resp = api.updateUser(updateUserValidation)
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()?.result)
            } else {
                Result.error(resp.message())
            }
        } catch (e: Exception) {
            Result.error(e.message)
        }



}