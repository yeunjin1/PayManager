package konkuk.yeonj.paymanager

import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Int.convertToTimeString(): String {
    val hour = this / 60
    val min = this % 60
    val dateForamt = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.of(hour, min).format(dateForamt)
}