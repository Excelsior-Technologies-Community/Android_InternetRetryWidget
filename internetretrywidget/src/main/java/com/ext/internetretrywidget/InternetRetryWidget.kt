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
    private var currentState: WidgetState = WidgetState.CONTENT


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

        if (!isConnected) {
            setState(WidgetState.NO_INTERNET)
        } else {
            setState(WidgetState.CONTENT)
        }

        // ✅ Network observer (THIS WAS MISSING ❗)
        networkObserver = NetworkLiveObserver(context) { isConnected ->
            post {
                if (!isConnected) {
                    setState(WidgetState.NO_INTERNET)
                } else {
                    if (currentState == WidgetState.NO_INTERNET) {
                        setState(WidgetState.CONTENT)
                    }
                }
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
    fun setState(state: WidgetState, message: String? = null) {
        currentState = state

        when (state) {

            WidgetState.LOADING -> {
                visibility = VISIBLE
                binding.progressBar.visibility = VISIBLE
                binding.layoutContent.visibility = GONE
            }

            WidgetState.NO_INTERNET -> {
                visibility = VISIBLE
                binding.progressBar.visibility = GONE
                binding.layoutContent.visibility = VISIBLE
                binding.tvMessage.text = message ?: "No Internet Connection"
            }

            WidgetState.ERROR -> {
                visibility = VISIBLE
                binding.progressBar.visibility = GONE
                binding.layoutContent.visibility = VISIBLE
                binding.tvMessage.text = message ?: "Something went wrong"
            }

            WidgetState.CONTENT -> {
                visibility = GONE
            }
        }
    }
}