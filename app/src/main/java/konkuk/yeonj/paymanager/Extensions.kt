package konkuk.yeonj.paymanager

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_work.*
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun Int.convertToTimeString(): String {
    val hour = this / 60
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

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))
internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)
internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
