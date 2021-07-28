package com.thee.horrorcorian.facedetection

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.bottomsheet.BottomSheetDialog



class Dialogs {

    fun showAlertDialog(title:String, Message:String, context: Context){
        MaterialDialog(context).show {
            title(text = title)
            message(text = Message )
            cancelable(false)
            positiveButton (text = "Ok" )
            positiveButton { this.dismiss() }
        }
    }


        fun showErrorDialog(title: String, Message: String, context: Context) {
            MaterialDialog(context).show {
                title(text = title)
                message(text = Message)
                cancelable(false)
                positiveButton(text = "Ok")
                icon(R.drawable.ic_baseline_error_24)
                positiveButton { this.dismiss() }
            }
        }

        fun getCustomViewDialogs(context: Context, view: Int): MaterialDialog {
            val dialog = MaterialDialog(context)
                .customView(view, scrollable = true)
                .cancelable(false)
                .cancelOnTouchOutside(false)
                .cornerRadius(10f)
            return dialog
        }


        fun getAnAlertDialog(title: String, Message: String, context: Context): MaterialDialog {
            var dialog = MaterialDialog(context)
            dialog.show {
                title(text = title)
                icon(R.drawable.logo2)
                message(text = Message)
                positiveButton(text = "Grant Permission")
                negativeButton(text = "Dismiss")
            }
            return dialog
        }


        fun getBottomSheet(context: Context, view: Int): MaterialDialog {
            val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))
            dialog.show {
                customView(view)
                cancelable(false)
                cancelOnTouchOutside(false)
            }
            return dialog
        }

        fun getUserBottomSheet(context: Context, view: Int): MaterialDialog {
            val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))
            dialog.show {
                cornerRadius(10f)
                customView(view)
                cancelable(false)

            }
            return dialog
        }
    }