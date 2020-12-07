package konkuk.yeonj.paymanager

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_work.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun Int.convertToTimeString(): String {
    var hour = this / 60
    if(hour >= 24) hour -= 24
    val min = this % 60
    val dateForamt = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.of(hour, min).format(dateForamt)
}

fun LocalDate.convertToDate(): Date {
    return  Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun Int.toColorFilter(): PorterDuffColorFilter{
    return PorterDuffColorFilter(this, PorterDuff.Mode.MULTIPLY)
}

fun LocalDate.toLastDayOfMonth(): LocalDate{
    return YearMonth.from(this).atEndOfMonth()
}

fun LocalDate.toFirstDayOfMonth(): LocalDate{
    return this.minusDays(this.dayOfMonth.toLong() - 1)
}

fun LocalDate.toLastDayOfWeek(): LocalDate{
    return this.with(DayOfWeek.SATURDAY)
}

fun LocalDate.toFirstDayOfWeek(): LocalDate{
    return this.with(DayOfWeek.MONDAY).minusDays(1)
}

fun periodToString(startDay: LocalDate, endDay: LocalDate): String{
    val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)")
    return startDay.format(dateFormat) + " - " + endDay.format(dateFormat)
}

fun timePeriodToString(startTime: Int, endTime: Int): String{
    return startTime.convertToTimeString() + " - " + endTime.convertToTimeString()
}

//place color -> color res
fun Int.toColorRes(context: Context): Int{
    var color = 0
    when(this) {
        0-> color = R.color.red
        1-> color = R.color.orange
        2-> color = R.color.green
        3-> color = R.color.blue
        4-> color = R.color.purple
    }
    return context.getColor(color)
}

fun Int.minToString(): String{
    val hour = this / 60
    val min = this % 60
    var str = ""
    if(hour == 0){
        str = min.toString() + "분"
    }
    else if(min == 0){
        str = hour.toString() + "시간"
    }
    else {
        str = hour.toString() + "시간 " + min.toString() + "분"
    }
    return str
}

fun calTotalPay(startTime: Int, endTime: Int, overTime: Int, breakTime: Int, payByHour: Int): Int{
    val duringTime = (endTime - startTime - breakTime) / 60.0
    val nightTime = getNightTime(startTime, endTime) / 60.0
    val overTime = overTime / 60.0
    Log.d("mytag", duringTime.toString())
    Log.d("mytag", nightTime.toString())
    Log.d("mytag", overTime.toString())
    return (payByHour * duringTime + (payByHour / 2) * nightTime + (payByHour / 2) * overTime).toInt()
}

fun Int.moneyToString(): String{
    val dec = DecimalFormat("###,###,###원")
    return dec.format(this)
}

fun getNightTime(startTime: Int, endTime: Int): Int{
    var mStart = startTime
    var mEnd = endTime

    if(startTime in 6*60 until 22*60) mStart = 22*60
    else if(startTime in 30*60 until 46*60) mStart = 46*60

    if(endTime in 6*60 .. 22*60) mEnd = 6*60
    else if(endTime in 30*60 .. 46*60) mEnd = 30*60

    var night = mEnd - mStart
    if(night >= 16*60) night -= 16*60
    else if(night < 0) night = 0
    return night
}

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))
internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)
internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
