package konkuk.yeonj.paymanager

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import io.realm.RealmList
import konkuk.yeonj.paymanager.data.TimeSet
import kotlinx.android.synthetic.main.activity_add_work.*
import java.sql.Time
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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
    return PorterDuffColorFilter(this, PorterDuff.Mode.SRC_IN)
}

fun LocalDate.toLastDayOfMonth(): LocalDate{
    return YearMonth.from(this).atEndOfMonth()
}

fun LocalDate.toFirstDayOfMonth(): LocalDate{
    return this.minusDays(this.dayOfMonth.toLong() - 1)
}

fun LocalDate.toLastDayOfWeek(): LocalDate{
    return this.with(DayOfWeek.SUNDAY)
}

fun LocalDate.toFirstDayOfWeek(): LocalDate{
    return this.with(DayOfWeek.MONDAY)
}

fun Date.toFirstDayOfWeek(): Date{
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DAY_OF_MONTH, (2 - cal.get(Calendar.DAY_OF_WEEK)))
    return cal.time
}

fun Date.toLastDayOfWeek(): Date{
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DAY_OF_MONTH, (8 - cal.get(Calendar.DAY_OF_WEEK)))
    return cal.time
}

fun Date.plus(i:Int): Date{
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, i)
    return cal.time
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
//    Log.d("mytag", duringTime.toString())
//    Log.d("mytag", nightTime.toString())
//    Log.d("mytag", overTime.toString())
    return (payByHour * duringTime + (payByHour / 2) * nightTime + (payByHour / 2) * overTime).toInt()
}

fun Int.moneyToString(): String{
    val dec = DecimalFormat("###,###,###원")
    return dec.format(this)
}

fun getNightTime(startTime: Int, endTime: Int): Int{
    var mStart = startTime
    var mEnd = endTime

    if(mStart > mEnd) mEnd += 24 * 60
    Log.d("mytag", "start" + mStart.toString())
    Log.d("mytag", "end" + mEnd.toString())

    if(mStart in 6*60 until 22*60) mStart = 22*60
    else if(mStart in 30*60 until 46*60) mStart = 46*60

    if(mEnd in 6*60 .. 22*60) mEnd = 6*60
    else if(mEnd in 30*60 .. 46*60) mEnd = 30*60

    var night = mEnd - mStart
    if(night >= 16*60) night -= 16*60
    else if(night < 0) night = 0
    Log.d("mytag", "night" + night.toString())

    return night
}

fun getNightTimeText(startTime: Int, endTime: Int): String{
    return "야간 " + getNightTime(startTime, endTime).toString() + "분"
}


fun Int.toDayString(): String{
    when(this){
        0 -> return "월"
        1 -> return "화"
        2 -> return "수"
        3 -> return "목"
        4 -> return "금"
        5 -> return "토"
        6 -> return "일"
        else -> return ""
    }
}

fun ArrayList<TimeSet>.toRealmList(): RealmList<TimeSet>{
    val realmList = RealmList<TimeSet>()
    realmList.addAll(this)
    return realmList
}

fun Date.convertToDay(): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    val day = cal.get(Calendar.DAY_OF_WEEK)
    if(day >= 2) return day - 2
    else return 6
}

fun EditText.getTextString(): String{
    if(this.text.toString().trim() == "" || this.text.isEmpty()){
        return this.hint.toString()
    }
    else
        return this.text.toString()
}

fun getTotalTimeText(startHour: Int, startMin: Int, endHour: Int, endMin: Int, breakTime: Int): String{
    var endTime = endHour * 60 + endMin
    val startTime = startHour * 60 + startMin
    if(startTime > endTime) endTime += 24 * 60
    val total = endTime - startTime - breakTime
    val hour = total / 60
    val min = total % 60
    return "총 " + hour.toString() + "시간" + min.toString() + "분"
}

fun getTotalTimeText(startTime: Int, endTime: Int, breakTime: Int): String{
    var endTime = endTime
    if(startTime > endTime) endTime += 24 * 60
    var total = endTime - startTime - breakTime
    val hour = total / 60
    val min = total % 60
    return "총 " + hour.toString() + "시간" + min.toString() + "분"
}




internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))
internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)
internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
