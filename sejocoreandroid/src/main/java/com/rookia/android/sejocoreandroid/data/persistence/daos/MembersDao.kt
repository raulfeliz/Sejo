package com.rookia.android.sejocoreandroid.data.persistence.daos

import androidx.room.Dao
import com.rookia.android.androidutils.persistence.daos.BaseDao
import com.rookia.android.sejocoreandroid.data.persistence.entities.MemberEntity


/**
 * Copyright (C) Rookia - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Roll <raulfeliz@gmail.com>, May 2020
 *
 *
 */

@Dao
abstract class MembersDao : BaseDao<MemberEntity>()