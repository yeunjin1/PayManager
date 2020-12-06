package konkuk.yeonj.paymanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import konkuk.yeonj.paymanager.*
import konkuk.yeonj.paymanager.setTextColorRes
import konkuk.yeonj.paymanager.ui.calendar.CircleGridAdapter
import kotlinx.android.synthetic.main.calendar_dialog_layout.*
import kotlinx.android.synthetic.main.calendar_dialog_layout.calendarView
import kotlinx.coroutines.channels.consumesAll
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class CalendarDialog (context: Context) : Dialog(context) {

    open class Builder(val mContext: Context) {
        private var startDate: LocalDate? = null
        private var endDate: LocalDate? = null
        private val now = LocalDate.now()

        open val dialog = CustomDialog(mContext)
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.calendar_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            initCalendarView()
            dialog.cancelBtn.setOnClickListener { dialog.dismiss() }
            dialog.pickButton.setOnClickListener {
                dialog.pickCalendar.visibility = View.VISIBLE
                dialog.pickOptions.visibility = View.GONE
            }
            return this
        }

        private fun initCalendarView(){
            val startBackground: GradientDrawable by lazy {
                mContext.getDrawableCompat(R.drawable.example_4_continuous_selected_bg_start) as GradientDrawable
            }

            val endBackground: GradientDrawable by lazy {
                mContext.getDrawableCompat(R.drawable.example_4_continuous_selected_bg_end) as GradientDrawable
            }

            val dateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월")
            class DayViewContainer(view: View) : ViewContainer(view) {
                val textView = view.findViewById<TextView>(R.id.calendarDayText)
                val roundView = view.findViewById<View>(R.id.roundView)
                lateinit var day: CalendarDay
                init{
                    view.setOnClickListener {
                        if (day.owner == DayOwner.THIS_MONTH){
                            val date = day.date
                            if (startDate != null) {
                                if (date < startDate || endDate != null) {
                                    startDate = date
                                    endDate = null
                                } else if (date != startDate) {
                                    endDate = date
                                }
                            } else {
                                startDate = date
                            }
                            dialog.calendarView.notifyCalendarChanged()
                        }
                    }
                }
            }

            dialog.calendarView.inDateStyle = InDateStyle.ALL_MONTHS
            dialog.calendarView.outDateStyle = OutDateStyle.END_OF_ROW
            dialog.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    container.textView.text = day.date.dayOfMonth.toString()
                    container.textView.background = null
                    container.roundView.visibility = View.INVISIBLE
                    if (day.owner == DayOwner.THIS_MONTH) {
                        when {
                            startDate == day.date && endDate == null -> {
                                container.textView.setTextColorRes(R.color.white)
                                container.roundView.visibility = View.VISIBLE
                                container.roundView.setBackgroundResource(R.drawable.example_4_single_selected_bg)
                            }
                            day.date == startDate -> {
                                container.textView.setTextColorRes(R.color.white)
                                container.textView.background = startBackground
                            }
                            startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                                container.textView.setTextColorRes(R.color.white)
                                container.textView.setBackgroundResource(R.drawable.example_4_continuous_selected_bg_middle)
                            }
                            day.date == endDate -> {
                                container.textView.setTextColorRes(R.color.white)
                                container.textView.background = endBackground
                            }
                            else -> {
                                container.textView.setTextColorRes(R.color.darkGray)
                            }
                        }
                    }
                    else{
                        val startDate = startDate
                        val endDate = endDate
                        if (startDate != null && endDate != null) {
                            if ((day.owner == DayOwner.PREVIOUS_MONTH &&
                                        startDate.monthValue == day.date.monthValue &&
                                        endDate.monthValue != day.date.monthValue) ||
                                (day.owner == DayOwner.NEXT_MONTH &&
                                        startDate.monthValue != day.date.monthValue &&
                                        endDate.monthValue == day.date.monthValue) ||

                                (startDate < day.date && endDate > day.date &&
                                        startDate.monthValue != day.date.monthValue &&
                                        endDate.monthValue != day.date.monthValue)
                            ) {
                                container.textView.setTextColorRes(R.color.white)
                                container.textView.setBackgroundResource(R.drawable.example_4_continuous_selected_bg_middle)
                            }
                        }
                    }
                }
                override fun create(view: View) = DayViewContainer(view)
            }

            //캘린더 날짜 속성 초기화
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(10)
            val lastMonth = currentMonth.plusMonths(10)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            dialog.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
            dialog.calendarView.scrollToMonth(currentMonth)
            dialog.calendarView.monthScrollListener = {
                dialog.calendarMonthText.text = it.yearMonth.format(dateFormat)
            }

            dialog.leftButton.setOnClickListener {
               dialog.calendarView.findFirstVisibleMonth()?.let {
                    dialog.calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
                }
            }

            dialog.rightButton.setOnClickListener {
                dialog.calendarView.findFirstVisibleMonth()?.let {
                    dialog.calendarView.smoothScrollToMonth(it.yearMonth.plusMonths(1))
                }
            }

        }

        open fun setButtons(onConfirmClick: View.OnClickListener, onWeekClick: View.OnClickListener, onMonthClick: View.OnClickListener): Builder{
            dialog.confirmBtn.setOnClickListener(onConfirmClick)
            dialog.weekButton.setOnClickListener(onWeekClick)
            dialog.monthButton.setOnClickListener(onMonthClick)
            return this
        }

        fun dismissDialog() {
            dialog.dismiss()
        }

        open fun show(): CustomDialog {
            dialog.show()
            return dialog
        }

        fun getStartDate(): LocalDate? {
            return startDate
        }

        fun getEndDate(): LocalDate? {
            return endDate
        }

    }
}