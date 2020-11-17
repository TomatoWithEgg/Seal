package com.ketchup.seal.callback

/**
 * 权限请求回调
 */
interface Callback {

    fun granted()

    fun denied(permissions: List<String>)

    fun never(permissions: List<String>)

}