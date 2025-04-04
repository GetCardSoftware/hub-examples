package com.getcard.completeposexample.extensions


import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun TextInputEditText.setupError(error: String) {
    this.error = error
}

fun TextInputEditText.string() = this.text.toString()

fun TextInputEditText.stringIsEmpty() = this.text.toString().isEmpty()

fun TextInputEditText.setCurrency() {
    var current = ""
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val stringText = s.toString()

            if (stringText != current) {
                this@setCurrency.removeTextChangedListener(this)

                val locale: Locale = Locale.getDefault()
                val currency = Currency.getInstance(locale)
                val cleanString =
                    stringText.replace("\\W", "").replace("[${currency.symbol},.]".toRegex(), "")
                val parsed = if (cleanString.isEmpty()) 0.0 else cleanString.trim().toDouble()
                val formatted = NumberFormat.getCurrencyInstance(locale).format(parsed / 100)

                current = formatted
                this@setCurrency.setText(formatted)
                this@setCurrency.text?.let { this@setCurrency.setSelection(it.length) }
                this@setCurrency.addTextChangedListener(this)
            }
        }
    })
}