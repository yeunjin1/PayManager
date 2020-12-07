package konkuk.yeonj.paymanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import konkuk.yeonj.paymanager.R
import kotlinx.android.synthetic.main.time_picker_dialog_layout.*

class TimePickerDialog (context: Context) : Dialog(context) {
    open class Builder(mContext: Context, val hour: Int, val min: Int) {
        open val dialog = CustomDialog(mContext)
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.time_picker_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.timePicker.hour = hour
            dialog.timePicker.minute = min
            dialog.timePicker.setIs24HourView(true)
            dialog.cancelBtn.setOnClickListener { dismissDialog() }
            return this
        }

        fun getPickerHour(): Int{
            return dialog.timePicker.hour
        }

        fun getPickerMinute(): Int{
            return dialog.timePicker.minute
        }
        open fun setConfirmButton(onClick: View.OnClickListener): Builder {
            dialog.confirmBtn.setOnClickListener(onClick)
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