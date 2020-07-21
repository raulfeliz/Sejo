package com.rookia.android.sejocoreandroid.data.network

import com.rookia.android.sejocoreandroid.data.apis.GroupApi
import com.rookia.android.sejocoreandroid.data.apis.LoginApi
import com.rookia.android.sejocoreandroid.data.apis.SmsCodeApi
import com.rookia.android.sejocoreandroid.data.apis.UserApi
import com.rookia.android.sejocoreandroid.utils.TokenUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

open class NetworkServiceFactory @Inject constructor(
    private val tokenUtils: TokenUtils
) {

    companion object {
//        const val ROOKIA_EXPENSES_SERVER_BASE_URL = "http://10.0.2.2:8080"
        const val ROOKIA_EXPENSES_SERVER_BASE_URL = "http://192.168.8.102:8080"

    }

    @Volatile
    private var smsCodeCodeInstance: SmsCodeApi? =
        null

    @Volatile
    private var userInstance: UserApi? =
        null

    @Volatile
    private var groupInstance: GroupApi? =
        null

    @Volatile
    private var loginInstance: LoginApi? =
        null

    fun getSmsCodeCodeInstance(): SmsCodeApi =
        smsCodeCodeInstance ?: buildSmsCodeNetworkService().also { smsCodeCodeInstance = it }

    fun getUserInstance(): UserApi {
        val bearer = tokenUtils.getToken()

        return userInstance ?: buildUserNetworkService(bearer).also { userApi ->
            bearer?.let { userInstance = userApi }
        }
    }

    fun getLoginInstance(): LoginApi {
        val bearer = tokenUtils.getToken()
        return loginInstance ?: buildLoginNetworkService(bearer).also { loginApi ->
            bearer?.let { loginInstance = loginApi }
        }
    }

    fun getGroupInstance(): GroupApi {
        val bearer = tokenUtils.getToken()

        return groupInstance ?: buildGroupNetworkService(bearer).also { groupApi ->
            bearer?.let { groupInstance = groupApi }
        }
    }

    fun newTokenReceived() {
        smsCodeCodeInstance = null
        userInstance = null
        groupInstance = null
    }

    private fun buildSmsCodeNetworkService(): SmsCodeApi =
        Retrofit.Builder()
            .baseUrl(ROOKIA_EXPENSES_SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmsCodeApi::class.java)

    private fun buildUserNetworkService(bearer: String?): UserApi =
        Retrofit.Builder()
            .baseUrl(ROOKIA_EXPENSES_SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getInterceptorForAuthentication(bearer))
            .build()
            .create(UserApi::class.java)


    private fun buildLoginNetworkService(bearer: String?): LoginApi =
        Retrofit.Builder()
            .baseUrl(ROOKIA_EXPENSES_SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getInterceptorForAuthentication(bearer))
            .build()
            .create(LoginApi::class.java)


    private fun buildGroupNetworkService(bearer: String?): GroupApi =
        Retrofit.Builder()
            .baseUrl(ROOKIA_EXPENSES_SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getInterceptorForAuthentication(bearer))
            .build()
            .create(GroupApi::class.java)


    private fun getInterceptorForAuthentication(bearer: String?): OkHttpClient {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        httpClient.addInterceptor(object : Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request.Builder = chain.request().newBuilder()
                bearer?.let {
                    request.addHeader("Authorization", bearer)
                }
                return chain.proceed(request.build())
            }
        })
        return httpClient.build()
    }


}

