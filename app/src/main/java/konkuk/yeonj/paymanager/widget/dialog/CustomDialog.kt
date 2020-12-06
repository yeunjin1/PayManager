package konkuk.yeonj.paymanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.view.View
import konkuk.yeonj.paymanager.R
import kotlinx.android.synthetic.main.custom_dialog_layout.*

open class CustomDialog(context: Context) : Dialog(context) {

    open class Builder(val mContext: Context) {
        open val dialog = CustomDialog(mContext)
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.custom_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return this
        }

        fun setContent(content: String?): Builder {
            dialog.contentText.text = content
            return this
        }

        open fun setCancelButton(onClick: View.OnClickListener? = null): Builder {
            if(onClick == null)
                dialog.cancelBtn.setOnClickListener { dialog.dismiss() }
            else
                dialog.cancelBtn.setOnClickListener(onClick)
            dialog.cancelBtn.visibility = View.VISIBLE
            return this
        }

        open fun setConfirmButton(onClick: View.OnClickListener? = null): Builder {
            if(onClick == null)
                dialog.confirmBtn.setOnClickListener { dialog.dismiss() }
            else
                dialog.confirmBtn.setOnClickListener(onClick)
            dialog.confirmBtn.visibility = View.VISIBLE
            return this
        }

        fun dismissDialog() {
            dialog.dismiss()
        }
        open fun show(): CustomDialog {
            dialog.show()
            return dialog
        }
    }
}