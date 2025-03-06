package com.getcard.pdvwebsocket

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class LoadingDialog(private val context: Context) {
    private var dialog: Dialog? = null
    private var textView: TextView? = null
    private var progressBar: ProgressBar? = null
    private var successImageView: ImageView? = null
    private var errorImageView: ImageView? = null

    fun show() {
        if (dialog == null) {
            dialog = Dialog(context).apply {
                val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
                textView = view.findViewById(R.id.loading_text)
                successImageView = view.findViewById(R.id.checkmark)
                errorImageView = view.findViewById(R.id.cancel)
                progressBar = view.findViewById(R.id.loading_spinner)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
        dialog?.show()
    }

    private fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    fun updateMessageAndDismiss(
        newMessage: String,
        delayMillis: Long = 3000,
        success: Boolean = true
    ) {

        textView?.text = newMessage

        if (success) {
            successImageView?.visibility = View.VISIBLE
        } else {
            errorImageView?.visibility = View.VISIBLE
        }


        progressBar?.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            dismiss()
        }, delayMillis)
    }
}
