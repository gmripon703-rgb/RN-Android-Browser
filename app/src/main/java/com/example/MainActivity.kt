package com.example

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.browser.BrowserViewModel
import com.example.ui.screens.MainBrowserScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as StealthBrowserApp
                @Suppress("UNCHECKED_CAST")
                return BrowserViewModel(app.repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Auto-detect Android TV / Google TV device
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            viewModel.toggleTvMode()
        }

        // Handle external incoming URL intent (e.g. Browsable URL)
        handleIntent(intent)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainBrowserScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val url = intent.dataString
            if (!url.isNullOrBlank()) {
                viewModel.navigateTo(url)
            }
        }
    }

    // Android TV Remote D-Pad Navigation Support
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_PAGE_UP -> {
                viewModel.scrollUp()
                true
            }
            KeyEvent.KEYCODE_PAGE_DOWN -> {
                viewModel.scrollDown()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
