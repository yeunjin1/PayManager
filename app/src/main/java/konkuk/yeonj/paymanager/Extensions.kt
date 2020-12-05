package konkuk.yeonj.paymanager

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
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