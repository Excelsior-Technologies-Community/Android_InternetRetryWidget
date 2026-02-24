package com.ext.internetretrywidget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.ext.internetretrywidget.databinding.ViewInternetRetryBinding

class InternetRetryWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding =
        ViewInternetRetryBinding.inflate(LayoutInflater.from(context), this, true)

    private var retryClick: (() -> Unit)? = null

    init {
        orientation = VERTICAL

        binding.btnRetry.setOnClickListener {
            retryClick?.invoke()
        }
    }

    fun setOnRetryClick(action: () -> Unit) {
        retryClick = action
    }

    fun setMessage(message: String) {
        binding.tvMessage.text = message
    }
}