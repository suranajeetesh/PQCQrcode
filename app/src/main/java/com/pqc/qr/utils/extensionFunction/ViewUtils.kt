package com.pqc.qr.utils.extensionFunction

import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.text.InputType
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.pqc.qr.R

/**
 * Created by Jeetesh surana.
 */

fun TextView.multiLineText(){
    isSingleLine = false
    imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    setLines(5)
    maxLines = 5
    isVerticalScrollBarEnabled = true
    gravity = Gravity.TOP
    movementMethod = ScrollingMovementMethod.getInstance()
    scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
}
fun View.show() {
    visibility = View.VISIBLE
}

fun View.show(isShow: Boolean) {
    if (isShow)
        show()
    else
        hide()
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.invisible(isInvisible: Boolean = true) {
    visibility = if (isInvisible) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

fun View.hide() {
    visibility = View.GONE
}

fun View.isVisibleThenHide() {
    if (visibility == View.VISIBLE) {
        visibility = View.GONE
    }
}

fun View.disable(isAlpha: Boolean = true) {
    isEnabled = false
    if (isAlpha) alpha = 0.5f
    isClickable = false
}

fun View.enable(isAlpha: Boolean = true) {
    isEnabled = true
    if (isAlpha) alpha = 1f
    isClickable = true
}

fun show(vararg views: View) {
    for (view in views) {
        view.show()
    }
}

fun hide(vararg views: View) {
    for (view in views) {
        view.hide()
    }
}


fun isViewSelected(vararg views: View,isChecked:Boolean) {
    for (view in views) {
        if (isChecked){
            view.setTintColor(R.color.bittersweet)
        }else{
            view.setTintColor(R.color.white)
        }
    }
}

fun View.setTintColor(colorResId: Int) {
    when (this) {
        is ImageView -> {
            val color = ContextCompat.getColor(context, colorResId)
            val colorStateList = ColorStateList.valueOf(color)
            ImageViewCompat.setImageTintList(this, colorStateList)
        }
        is TextView -> {
            val color = ContextCompat.getColor(context, colorResId)
            setTextColor(color)
        }
    }
}

fun invisible(vararg views: View) {
    for (view in views) {
        view.invisible()
    }
}

var TextView.setHtmlText: CharSequence
    get() = text
    set(value) {
        text = value.toString().stripHtml()?.trim()
    }

fun String.stripHtml(): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}