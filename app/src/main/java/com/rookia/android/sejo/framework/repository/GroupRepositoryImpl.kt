package com.rookia.android.sejo.framework.repository

import androidx.annotation.VisibleForTesting
import com.rookia.android.androidutils.data.preferences.PreferencesManager
import com.rookia.android.androidutils.domain.vo.Result
import com.rookia.android.androidutils.framework.repository.resultFromPersistenceAndNetworkInFlow
import com.rookia.android.androidutils.framework.repository.resultOnlyFromOneSourceInFlow
import com.rookia.android.androidutils.utils.RateLimiter
import com.rookia.android.sejo.Constants
import com.rookia.android.sejo.data.persistence.PersistenceManager
import com.rookia.android.sejo.data.repository.GroupRepository
import com.rookia.android.sejo.domain.local.Group
import com.rookia.android.sejo.domain.local.PhoneContact
import com.rookia.android.sejo.domain.network.group.CreateGroupClient
import com.rookia.android.sejo.domain.network.toCreateGroupContact
import com.rookia.android.sejo.framework.network.NetworkServiceFactory
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject


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

class GroupRepositoryImpl @Inject constructor(
    private val networkServiceFactory: NetworkServiceFactory,
    private val persistenceManager: PersistenceManager,
    private val rateLimiter: RateLimiter,
    private val preferencesManager: PreferencesManager
) : GroupRepository {
    override fun createGroup(
        name: String,
        fee: Int,
        owner: String,
        members: List<PhoneContact>
    ): Flow<Result<Unit>> =
        resultOnlyFromOneSourceInFlow {
            createGroupInServer(
                name,
                fee,
                owner,
                members
            )
        }

    @VisibleForTesting
    suspend fun createGroupInServer(
        name: String,
        fee: Int,
        owner: String,
        members: List<PhoneContact>
    ): Result<Unit> =
        try {
            val api = networkServiceFactory.getGroupInstance()
            val groupRequest =
                CreateGroupClient(name, fee, owner, members.map { it.toCreateGroupContact() })
            val resp = api.createGroup(groupRequest)
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(Unit)
            } else {
                Result.error(resp.message())
            }
        } catch (e: Exception) {
            Result.error(e.message)
        }

    override suspend fun saveGroups(groups: List<Group>) {
        persistenceManager.saveGroups(groups)
    }

    override fun getGroups(userId: String, lastCheckedDate: Long): Flow<Result<List<Group>>> =
        resultFromPersistenceAndNetworkInFlow(
            persistedDataQuery = { persistenceManager.getGroups() },
            networkCall = { getGroupsFromServer(userId, lastCheckedDate) },
            persistCallResult = { listOfGroups ->
                listOfGroups?.let {
                    persistenceManager.saveGroups(it)
                }
            },
            isThePersistedInfoOutdated = {rateLimiter.expired(lastCheckedDate, 5, TimeUnit.MINUTES)}
        )

    private suspend fun getGroupsFromServer(userId: String, dateModification: Long): Result<List<Group>> =
        try {
            val api = networkServiceFactory.getGroupInstance()

            val resp = api.getGroups(userId, dateModification)
            if (resp.isSuccessful && resp.body() != null) {
                val lastCheckedDate = resp.body()?.data?.maxBy { it.dateModification  ?: 0L}?.dateModification ?: 0L
                if(dateModification < lastCheckedDate){
                    preferencesManager.setLongIntoPreferences(Constants.LAST_CHECKED_TIMESTAMP, lastCheckedDate)
                }
                Result.success(resp.body()?.data)
            } else {
                Result.error(resp.message())
            }
        } catch (e: Exception) {
            Result.error(e.message)
        }


}