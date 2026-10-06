package com.example.accessibilitydiagnostic


import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


class AuthenticatorAccessibilityService : AccessibilityService() {


    companion object {
        private const val TAG = "ACCESS_DIAGNOSTIC"
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "SERVICE_CONNECTED")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return


        val packageName = event.packageName?.toString() ?: "unknown"


        Log.i(
            TAG,
            "EVENT " +
                "package=$packageName " +
                "type=${event.eventType} " +
                "class=${event.className} " +
                "sourceAvailable=${event.source != null}"
        )


        val root = rootInActiveWindow


        if (root == null) {
            Log.i(TAG, "ROOT_UNAVAILABLE package=$packageName")
            return
        }


        var nodeCount = 0


        inspectNode(
            node = root,
            depth = 0,
            counter = { nodeCount++ }
        )


        Log.i(
            TAG,
            "TREE_COMPLETE " +
                "package=$packageName " +
                "nodes=$nodeCount"
        )


        root.recycle()
    }


    private fun inspectNode(
        node: AccessibilityNodeInfo,
        depth: Int,
        counter: () -> Unit
    ) {
        counter()


        // Deliberately log metadata only.
        // Actual text/contentDescription values are never logged.
        Log.i(
            TAG,
            "NODE " +
                "depth=$depth " +
                "class=${node.className ?: "null"} " +
                "viewId=${node.viewIdResourceName ?: "null"} " +
                "clickable=${node.isClickable} " +
                "focusable=${node.isFocusable} " +
                "focused=${node.isAccessibilityFocused} " +
                "enabled=${node.isEnabled} " +
                "editable=${node.isEditable} " +
                "password=${node.isPassword} " +
                "textPresent=${!node.text.isNullOrEmpty()} " +
                "descriptionPresent=${!node.contentDescription.isNullOrEmpty()} " +
                "children=${node.childCount}"
        )


        for (i in 0 until node.childCount) {
            val child = node.getChild(i)


            if (child != null) {
                try {
                    inspectNode(
                        node = child,
                        depth = depth + 1,
                        counter = counter
                    )
                } finally {
                    child.recycle()
                }
            }
        }
    }


    override fun onInterrupt() {
        Log.i(TAG, "SERVICE_INTERRUPTED")
    }
}