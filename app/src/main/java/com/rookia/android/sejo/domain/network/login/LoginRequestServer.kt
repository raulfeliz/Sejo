package com.rookia.android.sejo.domain.network.login


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

data class LoginRequestServer(
    val result: Int,
    val token: String? = null
)