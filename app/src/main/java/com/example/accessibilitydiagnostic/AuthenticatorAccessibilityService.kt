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
        var candidates = 0

        fun inspect(node: AccessibilityNodeInfo) {
            node.text?.toString()?.let { value ->
                if (value.isNotBlank()) {
                    textNodes++
                    if (SIX_DIGIT.containsMatchIn(value)) candidates++
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

        if (candidates > 0) {
            Log.i(TAG, "ACCESSIBLE_CODE_PATTERN_DETECTED package=" +
                event.packageName + " textNodes=" + textNodes +
                " sixDigitCandidates=" + candidates)
        }
    }

    override fun onInterrupt() {
        Log.i(TAG, "Accessibility service interrupted")
    }
}
