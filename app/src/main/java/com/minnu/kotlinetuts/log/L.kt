package com.minnu.kotlinetuts.log

import android.content.Context
import android.util.Log
import android.widget.Toast

class L {
    companion object {
        fun l(className: String, msg: String) {
            Log.d(className, "TEST | $msg")
        }

//        fun l(msg: String) {
//            Log.d("TEST", "TEST |  $msg")
//        }

        fun toast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}