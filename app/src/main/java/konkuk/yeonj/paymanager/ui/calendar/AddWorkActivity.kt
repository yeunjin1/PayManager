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
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class AddWorkActivity : AppCompatActivity() {
    lateinit var realm:Realm
    var placeId: String? = null
    var workId: String? = null
    lateinit var thisPlace: Place
    var thisWork: Work? = null
    val timeFormat = SimpleDateFormat("HH:mm")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
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
        placeId = intent.getStringExtra("placeId")
        Log.d("mytag", "placeId : " + placeId)
        //수정
        workId = intent.getStringExtra("workId")
        //해당 날짜도 넘겨 받아야함



        initLayout()
        initListener()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun initLayout(){
        if (placeId != null){
            //추가
            thisPlace = realm.where(Place::class.java).equalTo("id", placeId).findFirst()!!
            toolbar.title = thisPlace.name

        }

        if (workId != null){
            //수정

            thisWork = realm.where(Work::class.java).equalTo("id", workId).findFirst()!!
            thisPlace = realm.where(Place::class.java).equalTo("id", thisWork!!.placeId).findFirst()!!
            toolbar.title = thisPlace.name
            startTime.text = thisWork!!.timeStart.convertToTimeString()
            endTime.text = thisWork!!.timeEnd.convertToTimeString()
            dateText.text = dateFormat.format(thisWork!!.date)
            breakEdit.setText(thisWork!!.breakTime.toString())
            nightEdit.setText(thisWork!!.nightTime.toString())
            overEdit.setText(thisWork!!.overTime.toString())

            startHour = thisWork!!.timeStart / 60
            startMin = thisWork!!.timeStart % 60
            endHour = thisWork!!.timeEnd / 60
            endMin = thisWork!!.timeEnd % 60

        }
    }

    fun initListener(){
        confirm_button.setOnClickListener{
            if (placeId != null){
                //추가
                realm.beginTransaction()
                val item = realm.createObject(Work::class.java, UUID.randomUUID().toString())
                item.date = Date.from(LocalDate.of(2020, 11, 19).atStartOfDay(ZoneId.systemDefault()).toInstant())
                item.placeId = placeId!!
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

            if(workId != null){
                //수정
                realm.beginTransaction()
                thisWork!!.timePush = System.currentTimeMillis()
                thisWork!!.timeStart = startHour * 60 + startMin
                thisWork!!.timeEnd = endHour * 60 + endMin
                thisWork!!.timeDuring = thisWork!!.timeEnd - thisWork!!.timeStart
                thisWork!!.breakTime = breakEdit.text.toString().toInt()
                thisWork!!.nightTime = nightEdit.text.toString().toInt()
                thisWork!!.overTime = overEdit.text.toString().toInt()
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


}