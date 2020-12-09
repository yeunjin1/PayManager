package konkuk.yeonj.paymanager.ui.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import io.realm.Realm
import konkuk.yeonj.paymanager.*
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.widget.dialog.CustomDialog
import kotlinx.android.synthetic.main.activity_add_work.*
import kotlinx.android.synthetic.main.activity_add_work.confirm_button
import kotlinx.android.synthetic.main.activity_add_work.toolbar
import kotlinx.android.synthetic.main.row_cal_list.*
import java.text.SimpleDateFormat
import java.util.*

class AddWorkActivity : AppCompatActivity() {
    lateinit var realm:Realm
    var place: Place? = null
    var work: Work? = null
    var selectedDay = Date()
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    private var startHour = 12
    private var startMin = 0
    private var endHour = 12
    private var endMin = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun init(){
        realm = Realm.getDefaultInstance()
        //추가
        val placeId = intent.getStringExtra("placeId")
        if(placeId != null){
            selectedDay.time = intent.getLongExtra("selectedDay", 0L)
            place = realm.where(Place::class.java).equalTo("id", placeId).findFirst()
        }
        //수정
        val workId = intent.getStringExtra("workId")
        if(workId != null)
            work = realm.where(Work::class.java).equalTo("id", workId).findFirst()


        initLayout()
        initListener()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun initLayout(){
        if(place != null) {
            //추가
            dateText.text = dateFormat.format(selectedDay)
            placeName.text = place!!.name
            placeName.background.colorFilter = place!!.color.toColorRes(this).toColorFilter()
            startTime.background.colorFilter = place!!.color.toColorRes(this).toColorFilter()
            endTime.background.colorFilter = place!!.color.toColorRes(this).toColorFilter()
            var timeSet = place!!.timeSetList.where().equalTo("day", selectedDay.convertToDay()).findFirst()
            if(timeSet != null){
                startHour = timeSet!!.startHour
                endHour = timeSet!!.endHour
                startMin = timeSet!!.startMin
                endMin = timeSet!!.endMin
            }
            startTime.text = (startHour * 60 + startMin).convertToTimeString()
            endTime.text = (endHour * 60 + endMin).convertToTimeString()
            nightText.text = getNightTimeText(startHour * 60 + startMin, endHour * 60 + endMin)
            totalTimeText.text = getTotalTimeText(startHour, startMin, endHour, endMin, 0)
        }

        if(work != null) {
            //수정
            startTime.text = work!!.timeStart.convertToTimeString()
            endTime.text = work!!.timeEnd.convertToTimeString()
            dateText.text = dateFormat.format(work!!.date)
            breakEdit.setText(work!!.breakTime.toString())
            nightText.text = getNightTimeText(work!!.timeStart, work!!.timeEnd)
            overEdit.setText(work!!.overTime.toString())
            placeName.text = work!!.place!!.name
            placeName.background.colorFilter = work!!.place!!.color.toColorRes(this).toColorFilter()
            startTime.background.colorFilter = work!!.place!!.color.toColorRes(this).toColorFilter()
            endTime.background.colorFilter = work!!.place!!.color.toColorRes(this).toColorFilter()
            totalTimeText.text = getTotalTimeText(work!!.timeStart, work!!.timeEnd, work!!.breakTime)


            startHour = work!!.timeStart / 60
            startMin = work!!.timeStart % 60
            endHour = work!!.timeEnd / 60
            endMin = work!!.timeEnd % 60

        }
    }

    fun initListener(){
        confirm_button.setOnClickListener{
            val startTime = startHour * 60 + startMin
            var endTime = endHour * 60 + endMin
            if(endTime - startTime < 0)
                endTime += 24 * 60
            var duringTime = endTime - startTime

            if(duringTime == 0){
                //dialog
                var dialog = CustomDialog.Builder(this).create()
                dialog
                    .setContent("시간을 등록해 주세요")
                    .setConfirmButton{ dialog.dismissDialog() }
                    .show()
            }
            else{
                var breakVal = breakEdit.getTextString().toInt()
                var overVal = overEdit.getTextString().toInt()
                if(place != null){
                    //추가
                    realm.beginTransaction()
                    val item = realm.createObject(Work::class.java, UUID.randomUUID().toString())
                    item.date = selectedDay
                    item.placeId = place!!.id
                    item.place = place!!
                    item.timePush = System.currentTimeMillis()
                    item.timeStart = startTime
                    item.timeEnd = endTime
                    item.breakTime = breakVal
                    item.overTime = overVal
                    realm.commitTransaction()
                }

                if(work != null) {
                    //수정
                    realm.beginTransaction()
                    work!!.timeStart = startTime
                    work!!.timeEnd = endTime
                    work!!.breakTime = breakVal
                    work!!.overTime = overVal
                    realm.commitTransaction()
                }
                finish()
            }
        }

        startTime.setOnClickListener{
            var dialog = konkuk.yeonj.paymanager.widget.dialog.TimePickerDialog.Builder(this, startHour, startMin).create()
            dialog.setConfirmButton{
                startHour = dialog.getPickerHour()
                startMin = dialog.getPickerMinute()
                startTime.text = (startHour * 60 + startMin).convertToTimeString()
                nightText.text = getNightTimeText(startHour * 60 + startMin, endHour * 60 + endMin)
                totalTimeText.text = getTotalTimeText(startHour, startMin, endHour, endMin, breakEdit.getTextString().toInt())
                dialog.dismissDialog()
            }.show()
        }

        endTime.setOnClickListener {
            var dialog = konkuk.yeonj.paymanager.widget.dialog.TimePickerDialog.Builder(this, endHour, endMin).create()
            dialog.setConfirmButton{
                endHour = dialog.getPickerHour()
                endMin = dialog.getPickerMinute()
                endTime.text = (endHour * 60 + endMin).convertToTimeString()
                nightText.text = getNightTimeText(startHour * 60 + startMin, endHour * 60 + endMin)
                totalTimeText.text = getTotalTimeText(startHour, startMin, endHour, endMin, breakEdit.getTextString().toInt())
                dialog.dismissDialog()
            }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}