package com.teamnusocial.nusocial.utils

import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import kotlin.math.roundToInt

open class KeyboardToggleListener(
    private val root: View?,
    private val onKeyboardToggleAction: (shown: Boolean) -> Unit
) : ViewTreeObserver.OnGlobalLayoutListener {
    private var shown = false
    override fun onGlobalLayout() {
        root?.run {
            val heightDiff = rootView.height - height
            val keyboardShown = heightDiff > dpToPx(200f)
            if (shown != keyboardShown) {
                onKeyboardToggleAction.invoke(keyboardShown)
                shown = keyboardShown
            }
        }
    }


}

fun View.dpToPx(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).roundToInt()