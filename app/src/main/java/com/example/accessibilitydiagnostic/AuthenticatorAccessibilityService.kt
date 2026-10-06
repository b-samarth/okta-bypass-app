package com.example.accessibilitydiagnostic

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AuthenticatorAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "ACCESS_DIAGNOSTIC"
        private val SIX_DIGIT_REGEX = Regex("""(?<!\d)\d{6}(?!\d)""")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "SERVICE_CONNECTED")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val root = rootInActiveWindow ?: return

        var textNodes = 0
        val foundCodes = mutableListOf<String>()

        inspectNode(root) { hasText, codes ->
            if (hasText) textNodes++
            if (codes.isNotEmpty()) {
                foundCodes.addAll(codes)
            }
        }

        if (foundCodes.isNotEmpty()) {
            Log.i(
                TAG,
                "EVENT " +
                    "package=${event.packageName} " +
                    "type=${event.eventType} " +
                    "textNodes=$textNodes " +
                    "sixDigitCandidates=${foundCodes.size} " +
                    "codes=${foundCodes.joinToString(", ")}"
            )
        } else {
            Log.i(
                TAG,
                "EVENT " +
                    "package=${event.packageName} " +
                    "type=${event.eventType} " +
                    "textNodes=$textNodes " +
                    "sixDigitCandidates=0"
            )
        }
    }

    private fun inspectNode(
        node: AccessibilityNodeInfo,
        callback: (Boolean, List<String>) -> Unit
    ) {
        val text = node.text?.toString().orEmpty()

        if (text.isNotBlank()) {
            val matches = SIX_DIGIT_REGEX.findAll(text).map { it.value }.toList()
            callback(true, matches)
        } else {
            callback(false, emptyList())
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                inspectNode(child, callback)
                child.recycle()
            }
        }
    }

    override fun onInterrupt() {
        Log.i(TAG, "SERVICE_INTERRUPTED")
    }
}