package com.rookia.android.sejo.ui.views


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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rookia.android.androidutils.extensions.gone
import com.rookia.android.androidutils.extensions.visible
import com.rookia.android.sejo.Constants.PASSWORD_LENGTH
import com.rookia.android.sejo.R
import com.rookia.android.sejo.databinding.ComponentPasswordWithKeyboardBinding
import com.rookia.android.sejo.ui.views.numerickeyboard.NumericKeyboardActions
import com.rookia.android.sejo.ui.views.numerickeyboard.NumericKeyboardActions.KeyPressed.KEY_BACK_FINGER


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

class PasswordScreen : ConstraintLayout {
    lateinit var binding: ComponentPasswordWithKeyboardBinding
    private var vibrate = false
    private var isFingerprintSupported = false
    private var useFingerPrint = false

    interface PasswordScreenEvents {
        fun showBiometricsLogin(forceIfAvailable: Boolean)
        fun isFingerPrintSupported(): Boolean
        fun isFingerprintSetByTheUser(): Boolean
        fun checkPassword(password: String)
    }

    private var listener: PasswordScreenEvents? = null

    private val actions: NumericKeyboardActions = object : NumericKeyboardActions(context) {

        override fun onKeyPressed(key: KeyPressed?) {
            super.onKeyPressed(key)
            val nonNullContext = context ?: return
            key ?: return
            if (key == KEY_BACK_FINGER) {
                if (useFingerPrint && isFingerprintSupported && binding.componentPasswordScreenBullets.getText().isBlank()) {
                    listener?.showBiometricsLogin(forceIfAvailable = true)
                } else {
                    binding.componentPasswordScreenBullets.deleteChar()
                }
            } else {
                binding.componentPasswordScreenBullets.addChar(key.value.toString())
            }
            checkFingerprintIcon()

            //Login when the length is correct
            val currentEnteredPassword = binding.componentPasswordScreenBullets.getText()
            if (currentEnteredPassword.length == PASSWORD_LENGTH) {
                listener?.checkPassword(currentEnteredPassword)
            }

        }

        override fun onLongDeleteKeyPressed(): Boolean {
            this.onKeyPressed(KEY_BACK_FINGER)
            binding.componentPasswordScreenBullets.setText("")
            checkFingerprintIcon()
            return super.onLongDeleteKeyPressed()
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(context, attrs)
        init(context)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PasswordScreen, 0, 0)
        try {
            vibrate = a.getBoolean(R.styleable.PasswordScreen_vibrate, false)

        } finally {
            a.recycle()
        }
    }

    private fun init(context: Context) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ComponentPasswordWithKeyboardBinding.inflate(inflater, this, true)
        actions.vibrate = vibrate
        binding.componentPasswordScreenNumericKeyboard.setAction(actions)
        checkFingerprintIcon()
    }

    private fun checkFingerprintIcon() {
        if (isFingerprintSupported && binding.componentPasswordScreenBullets.getText().isBlank()) {
            binding.componentPasswordScreenNumericKeyboard.setFingerprint()
        } else {
            binding.componentPasswordScreenNumericKeyboard.setBackspace()
        }
    }

    fun setListener(listener: PasswordScreenEvents){
        this.listener = listener
        isFingerprintSupported = listener.isFingerPrintSupported()
        useFingerPrint = listener.isFingerprintSetByTheUser()
        checkFingerprintIcon()
    }

    fun setHeader(text: String) {
        binding.componentPasswordScreenHeader.text = text
    }

    fun setSubHeader(text: String) {
        binding.componentPasswordScreenSubheader.text = text
    }

    fun showError(text: String) {
        binding.componentPasswordScreenSubheaderError.text = text
        binding.componentPasswordScreenSubheaderError.visible()
    }

    fun hideError() {
        binding.componentPasswordScreenSubheaderError.gone()
    }

}