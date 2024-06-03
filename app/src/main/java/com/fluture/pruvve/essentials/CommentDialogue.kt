package com.fluture.pruvve.essentials

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import com.fluture.pruvve.R

class CommentDialogue(context: Context) : Dialog(context) {

    init {
        // Requesting window features and setting content view
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialogue_comments_and_likes)

        // Set the dialog window attributes
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        val displayMetrics = context.resources.displayMetrics
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = (displayMetrics.heightPixels * 0.5).toInt()
        window?.attributes = layoutParams

        // Set the dialog to be draggable
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        // If dialog takes less than 30% of screen, auto close
        if (layoutParams.height < displayMetrics.heightPixels * 0.3) {
            setCancelable(true) // Allow dialog to be cancelable
        } else {
            setCancelable(false) // Prevent dialog from being cancelable
        }
    }
}