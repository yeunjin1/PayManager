package konkuk.yeonj.paymanager.ui.calendar

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import io.realm.Realm
import konkuk.yeonj.paymanager.R
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
    lateinit var placeId:String
    var workId: String? = null
    lateinit var thisPlace: Place
    val dateFormat = SimpleDateFormat("HH:mm")
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
        placeId = intent.getStringExtra("placeId")
        Log.d("mytag", "placeId : " + placeId)
        workId = intent.getStringExtra("workId")
        //해당 날짜도 넘겨 받아야함
        thisPlace = realm.where(Place::class.java).equalTo("id", placeId).findFirst()!!
        toolbar.title = thisPlace.name

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initListener()
        if (workId != null){

        }
    }

    fun initListener(){
        confirm_button.setOnClickListener{
            if (workId == null){
                //추가
                realm.beginTransaction()
                val item = realm.createObject(Work::class.java, UUID.randomUUID().toString())
                item.date = Date.from(LocalDate.of(2020, 11, 19).atStartOfDay(ZoneId.systemDefault()).toInstant())
                item.placeId = placeId
                item.timePush = System.currentTimeMillis()
                item.timeStart = startHour * 60 + startMin
                item.timeEnd = endHour * 60 + endMin
                item.timeDuring = item.timeEnd - item.timeStart
                item.breakTime = breakEdit.text.toString().toFloat()
                item.nightTime = nightEdit.text.toString().toFloat()
                item.overTime = overEdit.text.toString().toFloat()
                realm.commitTransaction()
                Log.d("mytag", item.toString())
                finish()
            }
            else{
                //수정
            }
            
        }

        startTime.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                startTime.text = dateFormat.format(cal.time)
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
                endTime.text = dateFormat.format(cal.time)
                endHour = hourOfDay
                endMin = minute
            }
            val dialog = TimePickerDialog(this@AddWorkActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            dialog.updateTime(endHour, endMin)
            dialog.show()
        }
    }


}