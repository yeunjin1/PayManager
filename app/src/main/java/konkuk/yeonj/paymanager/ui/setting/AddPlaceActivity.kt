package konkuk.yeonj.paymanager.ui.setting

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import io.realm.Realm
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import kotlinx.android.synthetic.main.activity_add_place.*
import java.util.*

class AddPlaceActivity : AppCompatActivity() {
    lateinit var realm: Realm
    //추가일 경우 0, 수정이 경우 1
    private var placeId = ""
    lateinit var colorSpinnerAdapter: ArrayAdapter<String>
    lateinit var startDaySpinnerAdapter: ArrayAdapter<Int>
    private val colors = arrayOf("red", "orange", "yellow", "green", "blue")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun init(){
        toolbar.title = getString(R.string.addPlace_toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        placeId = intent.getStringExtra("id")
        initSpinner()
        if(placeId != ""){
            initLayout()
        }
        initListener()

    }

    fun initLayout(){
        val item = realm.where(Place::class.java).equalTo("id", placeId).findAll()[0]!!
        payEdit.setText(item.payByHour.toString())
        nameEdit.setText(item.name)
        startDaySpinner.setSelection(item.startDay - 1)
        colorSpinner.setSelection(item.color)
        vacPaySwitch.isChecked = item.vacPay
        nightPaySwitch.isChecked = item.nightPay
        overPaySwitch.isChecked = item.overPay
        taxPaySwitch.isChecked = item.taxPay

    }

    fun initSpinner(){
        colorSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item)
        startDaySpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item)
        for(i in 1..31){
            startDaySpinnerAdapter.add(i)
        }
        for(i in 0..colors.size-1){
            colorSpinnerAdapter.add(colors[i])
        }
        startDaySpinner.adapter = startDaySpinnerAdapter
        colorSpinner.adapter = colorSpinnerAdapter
    }

    fun initListener(){
        confirm_button.setOnClickListener{
            Log.d("mytag", placeId)
            if(payEdit.text.toString().trim() != "" && nameEdit.text.toString().trim() != ""){
                if (placeId == ""){
                    //추가
                    realm.beginTransaction()
                    val item = realm.createObject(Place::class.java, UUID.randomUUID().toString())
                    item.name = nameEdit.text.toString()
                    item.payByHour = payEdit.text.toString().toInt()
                    item.color = colorSpinner.selectedItemPosition
                    item.startDay = startDaySpinner.selectedItem as Int
                    item.vacPay = vacPaySwitch.isChecked
                    item.nightPay = nightPaySwitch.isChecked
                    item.taxPay = taxPaySwitch.isChecked
                    item.timePush = System.currentTimeMillis()
                    realm.commitTransaction()
                    finish()
                    Log.d("mytag", "realm push" + item.toString())
                }
                else{
                    //수정
                    val item = realm.where(Place::class.java).equalTo("id", placeId).findAll()[0]!!
                    realm.beginTransaction()
                    item.name = nameEdit.text.toString()
                    item.payByHour = payEdit.text.toString().toInt()
                    item.color = colorSpinner.selectedItemPosition
                    item.startDay = startDaySpinner.selectedItem as Int
                    item.vacPay = vacPaySwitch.isChecked
                    item.nightPay = nightPaySwitch.isChecked
                    item.taxPay = taxPaySwitch.isChecked
                    realm.commitTransaction()
                    finish()
                    Log.d("mytag", "realm update" + item.toString())
                }
            }
            else{
                val builder = AlertDialog.Builder(this@AddPlaceActivity)
                builder.setNegativeButton("확인", null)
                builder.setMessage("근무지 명과 시급을 모두 입력해주세요.")
                builder.show()
            }
        }
    }






}