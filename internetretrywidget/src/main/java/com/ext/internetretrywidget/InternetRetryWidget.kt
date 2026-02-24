package com.ext.internetretrywidget

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresPermission
import com.ext.internetretrywidget.databinding.ViewInternetRetryBinding


class InternetRetryWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding =
        ViewInternetRetryBinding.inflate(LayoutInflater.from(context), this, true)

    private var retryClick: (() -> Unit)? = null
    private var networkObserver: NetworkLiveObserver? = null

    init {

        orientation = VERTICAL

        // ✅ Default values
        binding.tvMessage.text = "No Internet Connection"
        binding.btnRetry.text = "Retry"

        // ✅ Attributes
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.InternetRetryWidget
        )

        val message = typedArray.getString(R.styleable.InternetRetryWidget_irw_message)
        val buttonText = typedArray.getString(R.styleable.InternetRetryWidget_irw_buttonText)
        val textColor = typedArray.getColor(
            R.styleable.InternetRetryWidget_irw_textColor,
            binding.tvMessage.currentTextColor
        )
        val buttonColor = typedArray.getColor(
            R.styleable.InternetRetryWidget_irw_buttonColor,
            binding.btnRetry.currentTextColor
        )
        val icon = typedArray.getDrawable(R.styleable.InternetRetryWidget_irw_icon)

        typedArray.recycle()

        // ✅ Apply attributes
        message?.let { binding.tvMessage.text = it }
        buttonText?.let { binding.btnRetry.text = it }
        binding.tvMessage.setTextColor(textColor)
        binding.btnRetry.setTextColor(buttonColor)
        icon?.let { binding.imgIcon.setImageDrawable(it) }

        // ✅ Retry click
        binding.btnRetry.setOnClickListener {
            retryClick?.invoke()
        }

        // ✅ Initial state check (IMPORTANT)
        val isConnected = NetworkUtils.isInternetAvailable(context)
        visibility = if (isConnected) GONE else VISIBLE

        // ✅ Network observer (THIS WAS MISSING ❗)
        networkObserver = NetworkLiveObserver(context) { isConnected ->
            post {
                visibility = if (isConnected) GONE else VISIBLE
            }
        }
    }

    fun setOnRetryClick(action: () -> Unit) {
        retryClick = action
    }

    fun setMessage(message: String) {
        binding.tvMessage.text = message
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        networkObserver?.register()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        networkObserver?.unregister()
    }
}