package com.rookia.android.sejo

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rookia.android.androidutils.data.preferences.PreferencesManager
import com.rookia.android.androidutils.data.resources.ResourcesManager
import com.rookia.android.androidutils.ui.common.ViewModelFactory
import com.rookia.android.sejo.ui.login.LoginFragment
import com.rookia.android.sejo.ui.register.number.ValidatePhoneNumberFragment
import com.rookia.android.sejo.ui.register.sms.ValidateSmsFragment
import com.rookia.android.sejo.utils.FingerprintUtils
import com.rookia.android.sejo.utils.TextFormatUtils
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

class SejoFragmentFactory @Inject constructor(
    private val viewModelFactory: ViewModelFactory,
    private val resourcesManager: ResourcesManager,
    private val textFormatUtils: TextFormatUtils,
    private val fingerprintUtils: FingerprintUtils,
    private val preferencesManager: PreferencesManager,
    private val biometricDialog: BiometricPrompt.PromptInfo
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (loadFragmentClass(classLoader, className)) {
            ValidatePhoneNumberFragment::class.java -> ValidatePhoneNumberFragment(viewModelFactory, resourcesManager)
            ValidateSmsFragment::class.java -> ValidateSmsFragment(viewModelFactory, textFormatUtils)
            LoginFragment::class.java -> LoginFragment(viewModelFactory, fingerprintUtils, preferencesManager, biometricDialog)
            else -> super.instantiate(classLoader, className)
        }
    }
}