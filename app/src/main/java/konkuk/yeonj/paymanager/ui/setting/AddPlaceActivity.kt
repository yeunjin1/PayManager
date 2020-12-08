package konkuk.yeonj.paymanager.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.TimeSet
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.toDayString
import konkuk.yeonj.paymanager.toRealmList
import konkuk.yeonj.paymanager.ui.calendar.AddWorkActivity
import konkuk.yeonj.paymanager.ui.calendar.CalListAdapter
import konkuk.yeonj.paymanager.widget.dialog.CustomDialog
import konkuk.yeonj.paymanager.widget.dialog.DayTimePickerDialog
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

class AddPlaceActivity : AppCompatActivity() {
    lateinit var realm: Realm
    var place: Place? = null
    lateinit var colorListAdapter: ColorListAdapter
    lateinit var detailListAdapter: DetailListAdapter
    var timeSetList = MutableLiveData<ArrayList<TimeSet>>()


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
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        realm = Realm.getDefaultInstance()

        val placeId = intent.getStringExtra("placeId")
        initColorList()
        initObserver()
        timeSetList.value = ArrayList()
        if (placeId != null){
            place = realm.where(Place::class.java).equalTo("id", placeId).findFirst()!!
            timeSetList.value!!.addAll(place!!.timeSetList)
            initLayout()
            toolbar.title = "근무지 수정"
        }
        initDetailListView()
        initListener()

    }

    private fun initObserver(){
        timeSetList.observe(this, {
            it.sortBy{ it2 -> it2.day }
            detailListAdapter.notifyDataSetChanged()
        })
    }

    private fun initDetailListView(){
        detailListView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        detailListAdapter = DetailListAdapter(timeSetList.value!!, this)
        detailListView.adapter = detailListAdapter
        detailListAdapter.itemLongClickListener = object : DetailListAdapter.OnItemLongClickListener{
            override fun OnItemLongClick(
                holder: DetailListAdapter.ViewHolder,
                view: View,
                item: TimeSet
            ) {
                var dialog = CustomDialog.Builder(this@AddPlaceActivity).create()
                dialog
                    .setCancelButton(null)
                    .setConfirmButton{
                        timeSetList.value!!.remove(item)
                        timeSetList.value = timeSetList.value
                        dialog.dismissDialog()
                    }.show()
            }
        }

    }

    private fun initColorList(){
        colorList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, RecyclerView.HORIZONTAL, false
        )
        colorListAdapter = ColorListAdapter(this)
        colorList.adapter = colorListAdapter
        colorListAdapter.itemClickListener = object : ColorListAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: ColorListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                colorListAdapter.setSelection(position)
            }
        }

    }

    private fun initLayout(){
        payEdit.setText(place!!.payByHour.toString())
        nameEdit.setText(place!!.name)
        colorListAdapter.setSelection(place!!.color)
        vacPaySwitch.isChecked = place!!.vacPay
        nightPaySwitch.isChecked = place!!.nightPay
        overPaySwitch.isChecked = place!!.overPay
        taxPaySwitch.isChecked = place!!.taxPay
    }

    private fun initListener(){
        detailButton.setOnClickListener {
            var dialog = DayTimePickerDialog.Builder(this, 12, 0, 12, 0, 0).create()
            dialog
                .setConfirmButton{
                    realm.beginTransaction()
                    val item = realm.createObject(TimeSet::class.java, UUID.randomUUID().toString())
                    item.startHour = dialog.getSHour()
                    item.startMin = dialog.getSMin()
                    item.endHour = dialog.getEHour()
                    item.endMin = dialog.getEMin()
                    item.day = dialog.getDay()
                    realm.commitTransaction()

                    var iter = timeSetList.value!!.iterator()
                    while (iter.hasNext()) {
                        val i = iter.next()
                        if(i.day == item.day)
                            iter.remove()
                    }
                    timeSetList.value!!.add(item)
                    timeSetList.value = timeSetList.value
                    dialog.dismissDialog()
                }.show()
        }

        confirm_button.setOnClickListener{
            if(nameEdit.text.toString().trim() != ""){
                var payByHour = payEdit.hint.toString().toInt()
                if (payEdit.text.toString().trim() != ""){
                    payByHour = payEdit.text.toString().toInt()
                }
                if (place == null){
                    //추가
                    realm.beginTransaction()
                    val item = realm.createObject(Place::class.java, UUID.randomUUID().toString())
                    item.name = nameEdit.text.toString()
                    item.payByHour = payByHour
                    item.color = colorListAdapter.getSelection()
                    item.vacPay = vacPaySwitch.isChecked
                    item.nightPay = nightPaySwitch.isChecked
                    item.taxPay = taxPaySwitch.isChecked
                    item.timePush = System.currentTimeMillis()
                    item.timeSetList = timeSetList.value!!.toRealmList()
                    realm.commitTransaction()
                    finish()
                }
                else{
                    //수정
                    realm.beginTransaction()
                    place!!.name = nameEdit.text.toString()
                    place!!.payByHour = payByHour
                    place!!.color = colorListAdapter.getSelection()
                    place!!.vacPay = vacPaySwitch.isChecked
                    place!!.nightPay = nightPaySwitch.isChecked
                    place!!.taxPay = taxPaySwitch.isChecked
                    place!!.timeSetList = timeSetList.value!!.toRealmList()
                    realm.commitTransaction()
                    finish()
                }
            }
            else{
                val builder = CustomDialog.Builder(this).create()
                builder
                    .setContent("근무지 명을 입력해주세요.")
                    .setConfirmButton{builder.dismissDialog()}
                    .show()
            }
        }
    }

}