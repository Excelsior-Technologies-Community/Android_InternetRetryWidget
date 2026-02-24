package com.ext.android_internetretrywidget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.internetretrywidget.InternetRetryWidget
import com.ext.internetretrywidget.NetworkUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val retryWidget = findViewById<InternetRetryWidget>(R.id.retryWidget)

        retryWidget.setOnRetryClick {
            if (NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(this, "Internet Connected ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Still No Internet ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }
}