package com.rookia.android.sejo.data.repository

import com.rookia.android.androidutils.domain.vo.Result
import kotlinx.coroutines.flow.Flow


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

interface UserRepository {
    fun createUser(phonePrefix: String, phoneNumber: String, pin: Int): Flow<Result<Int>>
    fun updateUser(
        phonePrefix: String,
        phoneNumber: String,
        pin: Int,
        token: String
    ): Flow<Result<Int>>
}