package me.otavio.todolist.extensions

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

private val locale = Locale("pt", "BR")

/**
 * Returns the date representation as string, formatted to the pt-BR locale
 */
fun Date.format(): String =
    SimpleDateFormat("dd/MM/yyyy", locale).format(this)

/**
 * Returns the text field of the input layout
 */
var TextInputLayout.text : String
    get() = editText?.text?.toString() ?: ""
    set(value) { editText?.setText(value) }

/**
 * Return true if the text field is empty
 */
fun TextInputLayout.hasNoText(): Boolean = this.text.isEmpty()

/**
 * Validates the input layout and sets an error message if the input field has no text
 */
fun TextInputLayout.setErrorIfEmpty(context: Context, errorMessageResource: Int) {
    this.error =
        if (this.hasNoText()) context.getString(errorMessageResource)
        else null
}