package com.rookia.android.sejo.di.modules

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rookia.android.sejo.SejoApplication
import com.rookia.android.sejo.data.repository.SmsCodeRepository
import com.rookia.android.sejo.framework.network.NetworkServiceFactory
import com.rookia.android.sejo.framework.receivers.SMSBroadcastReceiver
import com.rookia.android.sejo.usecases.RequestSmsCodeUseCase
import com.rookia.android.sejo.usecases.ValidateSmsCodeUseCase
import com.rookia.android.sejo.utils.TextFormatUtils
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
class ProvidesModule {

    @Provides
    fun providesContext(application: SejoApplication): Context =
        application.applicationContext

    @Provides
    @Named("smsCode")
    @Singleton
    fun providesSmsCodeObservable(): MutableLiveData<String> = MutableLiveData()

    @Provides
    fun providesSMSBroadcastReceiver(
        @Named("smsCode") code: MutableLiveData<String>,
        textFormatUtils: TextFormatUtils
    ): SMSBroadcastReceiver =
        SMSBroadcastReceiver(code, textFormatUtils)

    @Provides
    fun providesRequestSmsCodeUseCase(repository: SmsCodeRepository): RequestSmsCodeUseCase =
        RequestSmsCodeUseCase(repository)

    @Provides
    fun providesValidateSmsCodeUseCase(repository: SmsCodeRepository): ValidateSmsCodeUseCase =
        ValidateSmsCodeUseCase(repository)

    @Singleton
    @Provides
    fun provideNetworkServiceFactory(): NetworkServiceFactory = NetworkServiceFactory()

    @Provides
    @Named("Main")
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Named("IO")
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named("Default")
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default


}