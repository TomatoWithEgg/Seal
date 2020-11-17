package com.ketchup.seal.callback

open class PermissionCallback : Callback {

    private var granted: (() -> Unit)? = null

    private var denied: ((permissions: List<String>) -> Unit)? = null

    private var never: ((permissions: List<String>) -> Unit)? = null

    fun onGranted(onGranted: () -> Unit) {
        granted = onGranted
    }

    fun onDenied(onDenied: (permissions: List<String>) -> Unit) {
        denied = onDenied
    }

    fun onNever(onNeverAsk: (permissions: List<String>) -> Unit) {
        never = onNeverAsk
    }

    override fun granted() {
        granted?.invoke()
    }

    override fun denied(permissions: List<String>) {
        denied?.invoke(permissions)
    }

    override fun never(permissions: List<String>) {
        never?.invoke(permissions)
    }
}