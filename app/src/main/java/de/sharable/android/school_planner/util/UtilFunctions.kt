package de.sharable.android.school_planner.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class UtilFunctions {

    companion object {
        fun createRadioButtonAlertDialog(context: Context, title: String, cancelButton: String?, items: Array<String>, checkedItem: Int,
                                          radioButtonClicked: (dialog: DialogInterface, index: Int) -> Unit,
                                          cancelAction: (dialog: DialogInterface, which: Int) -> Unit): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            if(cancelButton != null)
                builder.setNeutralButton(cancelButton) { dialog, which -> cancelAction(dialog, which) }
            builder.setSingleChoiceItems(items, checkedItem) { dialog: DialogInterface, which: Int -> radioButtonClicked(dialog, which) }

            return builder.create()
        }

        fun createConfirmAlertDialog(context: Context, title: String, message: String?, positiveButton: String?, negativeButton: String?,
                                     positiveButtonAction: (dialog: DialogInterface, which: Int) -> Unit,
                                     negativeButtonAction: (dialog: DialogInterface, which: Int) -> Unit): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            if(positiveButton != null)
                builder.setPositiveButton(positiveButton) { dialog, which -> positiveButtonAction(dialog, which) }
            if(negativeButton != null)
                builder.setNegativeButton(negativeButton) { dialog, which -> negativeButtonAction(dialog, which) }

            if(message != null)
                builder.setMessage(message)

            return builder.create()
        }
    }

}