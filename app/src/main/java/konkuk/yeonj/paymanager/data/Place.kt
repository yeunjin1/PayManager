package konkuk.yeonj.paymanager.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Place : RealmObject() {
    @PrimaryKey
    var id :String = ""
    var color:Int = 0
    var payByHour: Int = 0

    var name: String = ""
    var startDay: Int = 0
    var vacPay:Boolean = false
    var nightPay: Boolean = false
    var overPay: Boolean = false
    var taxPay: Boolean = false

    var dTimeStart: Long = 0
    var dTimeEnd: Long = 0
    var timePush: Long = 0


}