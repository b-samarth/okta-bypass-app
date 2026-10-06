# Android Accessibility Diagnostic

Diagnostic Android AccessibilityService project.

It inspects accessible text in memory and reports only redacted metadata when a six-digit numeric pattern is exposed. It does not log, persist, transmit, or submit the actual value.

## Build
GitHub Actions builds a debug APK on pushes to main or via workflow_dispatch.

## Test
Install the APK, enable the accessibility service, open the target app, then run:

    adb logcat -s ACCESS_DIAGNOSTIC:I

A detection reports the package name, text-node count, and candidate count; the six-digit value itself is never output.
