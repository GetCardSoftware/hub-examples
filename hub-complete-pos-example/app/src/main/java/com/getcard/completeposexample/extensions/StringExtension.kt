package com.getcard.completeposexample.extensions

fun String.formatValue(): String {
    return this.replace("[^0-9]".toRegex(), "")
}