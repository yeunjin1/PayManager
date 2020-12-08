package konkuk.yeonj.paymanager.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TimeSet: RealmObject() {
    @PrimaryKey
    var id: String = ""
    var startHour: Int = 12
    var startMin: Int = 0
    var endHour: Int = 12
    var endMin : Int = 0
    var day: Int = 0
}