package com.rookia.android.kotlinutils.repository

import com.rookia.android.kotlinutils.domain.vo.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun <T, A> resultFromPersistenceAndNetworkInFlow(
    persistedDataQuery: () -> Flow<T>,
    networkCall: suspend () -> Result<A>,
    persistCallResult: suspend (A?) -> Unit,
    isThePersistedInfoOutdated: (T?) -> Boolean
): Flow<Result<T>> =
    flow {

        persistedDataQuery.invoke().collect { cachedData ->
            val needToGetInfoFromServer = isThePersistedInfoOutdated(cachedData)
            if (needToGetInfoFromServer) {
                //show data from db but keep the loading state, as a network call will be done
                emit(Result.loading(cachedData))
            } else {
                //no network call -> show success
                emit(Result.success(cachedData))
            }

            if (needToGetInfoFromServer) {
                val responseStatus = networkCall.invoke()
                // Stop the previous emission to avoid dispatching the updated user
                // as `loading`.
                if (responseStatus.status == Result.Status.ERROR) {
                    emit(Result.error(responseStatus.message, cachedData))
                } else {
                    emit(Result.success(cachedData))
                    persistCallResult.invoke(responseStatus.data)
                }
            }
        }
    }

fun <T> resultOnlyFromOneSourceInFlow(
sourceCall: suspend () -> Result<T>
): Flow<Result<T>> =
flow {
    emit(
        Result.loading(null)
    )
    emit(
        sourceCall.invoke()
    )
}

suspend fun <T, A> resultFromPersistenceAndNetwork(
    persistedDataQuery: suspend () -> T?,
    networkCall: suspend () -> Result<A>,
    persistCallResult: suspend (A?) -> Unit,
    isThePersistedInfoOutdated: (T?) -> Boolean
): T? {
    val persistedData = persistedDataQuery.invoke()
    return if (isThePersistedInfoOutdated(persistedData)) {
        //network call and persist the result
        val responseFromNetwork = networkCall.invoke()
        if (responseFromNetwork.status == Result.Status.SUCCESS) {
            persistCallResult.invoke(responseFromNetwork.data)
        }
        persistedDataQuery.invoke()
    } else {
        persistedData
    }
}
