package konkuk.yeonj.paymanager.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Work : RealmObject() {
    @PrimaryKey
    var id :String = ""

    var date: Date = Date()

    //근무지 아이디
    var placeId: String = ""
    //근무 시작, 끝 시간, 근무 시간(분)
    var timeStart: Int = 0
    var timeEnd: Int = 0
    var timeDuring: Int = 0

    // 휴게, 야간, 초과 시간
    var breakTime: Float = 0.0f
    var nightTime: Float = 0.0f
    var overTime : Float = 0.0f

    var timePush: Long = 0


}