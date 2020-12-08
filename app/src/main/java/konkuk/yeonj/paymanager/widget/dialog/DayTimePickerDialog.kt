package konkuk.yeonj.paymanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.ui.setting.DayButtonListAdapter
import kotlinx.android.synthetic.main.day_time_picker_dialog_layout.*

class DayTimePickerDialog (context: Context) : Dialog(context) {
    open class Builder(val mContext: Context, val startHour: Int, val startMin: Int, val endHour: Int, val endMin: Int, val monDay:Int) {
        open val dialog = CustomDialog(mContext)
        lateinit var dayListAdapter: DayButtonListAdapter
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.day_time_picker_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            var size = Point()
            dialog.window!!.windowManager.defaultDisplay.getSize(size)
            dialog.window!!.setLayout((size.x * 0.872f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
            initTimePicker()
            initDayList()
            dialog.cancelBtn.setOnClickListener { dismissDialog() }
            return this
        }

        private fun initTimePicker(){
            dialog.timePicker1.hour = startHour
            dialog.timePicker1.minute = startMin
            dialog.timePicker2.hour = endHour
            dialog.timePicker2.minute = endMin
            dialog.timePicker1.setIs24HourView(true)
            dialog.timePicker2.setIs24HourView(true)
        }

        private fun initDayList(){
            dialog.dayList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                mContext, RecyclerView.HORIZONTAL, false
            )
            dayListAdapter = DayButtonListAdapter(mContext)
            dialog.dayList.adapter = dayListAdapter
            dayListAdapter.setSelection(monDay)
            dayListAdapter.itemClickListener = object : DayButtonListAdapter.OnItemClickListener{
                override fun OnItemClick(
                    holder: DayButtonListAdapter.ViewHolder,
                    view: View,
                    position: Int
                ) {
                    dayListAdapter.setSelection(position)
                }
            }

        }

        open fun setConfirmButton(onClick: View.OnClickListener): Builder {
            dialog.confirmBtn.setOnClickListener(onClick)
            return this
        }

        open fun dismissDialog() {
            dialog.dismiss()
        }

        open fun show(): CustomDialog {
            dialog.show()
            return dialog
        }

        fun getDay(): Int{
            return dayListAdapter.getSelection()
        }

        fun getSHour(): Int{
            return dialog.timePicker1.hour
        }

        fun getSMin(): Int{
            return dialog.timePicker1.minute
        }

        fun getEHour(): Int{
            return dialog.timePicker2.hour
        }

        fun getEMin(): Int{
            return dialog.timePicker2.minute
        }

    }
}