package com.edloca.hermes.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edloca.hermes.R

fun View.hide(){ visibility = View.GONE }
fun View.show(){ visibility = View.VISIBLE }


fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun Context.createLoadDialog(): Dialog {
    val dialog = Dialog(this)

    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setContentView(R.layout.loading_view)
    return dialog
}


fun Context.toast(message: String?){
    Toast.makeText(this,message, Toast.LENGTH_LONG).show()
}