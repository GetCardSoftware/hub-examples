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
import com.getcard.pdvwebsocket.R

class LoadingDialog(private val context: Context) {
    private var dialog: Dialog? = null
    private var textView: TextView? = null
    private var progressBar: ProgressBar? = null
    private var imageView: ImageView? = null

    fun show() {
        if (dialog == null) {
            dialog = Dialog(context).apply {
                val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
                textView = view.findViewById(R.id.loading_text)
                imageView = view.findViewById(R.id.checkmark)
                progressBar = view.findViewById(R.id.loading_spinner)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun updateMessageAndDismiss(newMessage: String, delayMillis: Long = 3000) {
        textView?.text = newMessage
        imageView?.visibility = View.VISIBLE
        progressBar?.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            dismiss()
        }, delayMillis)
    }
}
