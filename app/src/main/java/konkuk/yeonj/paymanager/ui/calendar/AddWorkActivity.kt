package konkuk.yeonj.paymanager.ui.calendar

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import io.realm.Realm
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.convertToTimeString
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import kotlinx.android.synthetic.main.activity_add_work.*
import kotlinx.android.synthetic.main.activity_add_work.confirm_button
import kotlinx.android.synthetic.main.activity_add_work.toolbar
import java.text.SimpleDateFormat
import java.util.*

class AddWorkActivity : AppCompatActivity() {
    lateinit var realm:Realm
    var place: Place? = null
    var work: Work? = null
    var selectedDay = Date()
    val timeFormat = SimpleDateFormat("HH:mm")
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
        if(placeId != null)
            place = realm.where(Place::class.java).equalTo("id", placeId).findFirst()
        //수정
        val workId = intent.getStringExtra("workId")
        if(workId != null)
            work = realm.where(Work::class.java).equalTo("id", workId).findFirst()

        selectedDay.time = intent.getLongExtra("selectedDay", 0L)

        initLayout()
        initListener()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun initLayout(){
        if(place != null) {
            //추가
            toolbar.title = place!!.name
            dateText.text = dateFormat.format(selectedDay)
        }

        if(work != null) {
            //수정
            toolbar.title = work!!.place?.name
            startTime.text = work!!.timeStart.convertToTimeString()
            endTime.text = work!!.timeEnd.convertToTimeString()
            dateText.text = dateFormat.format(work!!.date)
            breakEdit.setText(work!!.breakTime.toString())
            nightEdit.setText(work!!.nightTime.toString())
            overEdit.setText(work!!.overTime.toString())

            startHour = work!!.timeStart / 60
            startMin = work!!.timeStart % 60
            endHour = work!!.timeEnd / 60
            endMin = work!!.timeEnd % 60

        }
    }

    fun initListener(){
        confirm_button.setOnClickListener{
            if(place != null){
                //추가
                realm.beginTransaction()
                val item = realm.createObject(Work::class.java, UUID.randomUUID().toString())
                item.date = selectedDay
                item.placeId = place!!.id
                item.place = place!!
                item.timePush = System.currentTimeMillis()
                item.timeStart = startHour * 60 + startMin
                item.timeEnd = endHour * 60 + endMin
                item.timeDuring = item.timeEnd - item.timeStart
                item.breakTime = breakEdit.text.toString().toInt()
                item.nightTime = nightEdit.text.toString().toInt()
                item.overTime = overEdit.text.toString().toInt()
                realm.commitTransaction()
                Log.d("mytag", item.toString())
                finish()
            }

            if(work != null) {
                //수정
                realm.beginTransaction()
                work!!.timePush = System.currentTimeMillis()
                work!!.timeStart = startHour * 60 + startMin
                work!!.timeEnd = endHour * 60 + endMin
                work!!.timeDuring = work!!.timeEnd - work!!.timeStart
                work!!.breakTime = breakEdit.text.toString().toInt()
                work!!.nightTime = nightEdit.text.toString().toInt()
                work!!.overTime = overEdit.text.toString().toInt()
                realm.commitTransaction()
                finish()
            }
            
        }

        startTime.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                startTime.text = timeFormat.format(cal.time)
                startHour = hour
                startMin = minute
            }
            val dialog = TimePickerDialog(this@AddWorkActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            dialog.updateTime(startHour, startMin)
            dialog.show()
        }

        endTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                endTime.text = timeFormat.format(cal.time)
                endHour = hourOfDay
                endMin = minute
            }
            val dialog = TimePickerDialog(this@AddWorkActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            dialog.updateTime(endHour, endMin)
            dialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}