package com.ketchup.seal.callback

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ketchup.seal.ASK_CODE
import com.ketchup.seal.ASK_PERMISSIONS
import com.ketchup.seal.R
import com.ketchup.seal.shouldRationale

internal class CallbackFragment : Fragment() {

    private val permissions = arrayListOf<String>()

    private var callback: Callback? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        arguments?.getStringArrayList(ASK_PERMISSIONS)?.let { permissions.addAll(it) }

        return inflater.inflate(R.layout.fragment_callback, container, false)
    }

    override fun onResume() {
        super.onResume()

        if (permissions.isNotEmpty()) {
            requestPermissions(
                    permissions.toArray(
                            arrayOfNulls<String>(permissions.size)
                    ),
                    ASK_CODE
            )
        } else {
            fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
        }
    }

    fun callback(callback: Callback) {
        this.callback = callback
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == ASK_CODE) {
            val denied = mutableListOf<String>()
            val neverAsk = mutableListOf<String>()

            permissions.forEachIndexed { index, s ->

                if (grantResults[index] == PackageManager.PERMISSION_DENIED)

                    if (shouldRationale(s)) denied.add(s) else neverAsk.add(s)

            }

            if (denied.isEmpty() && neverAsk.isEmpty()) {
                callback?.granted()
            } else {
                denied.apply { if (isNotEmpty()) callback?.denied(this) }
                neverAsk.apply { if (isNotEmpty()) callback?.never(this) }
            }

            fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
        }
    }
}