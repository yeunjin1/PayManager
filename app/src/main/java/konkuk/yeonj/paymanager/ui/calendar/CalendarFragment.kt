package konkuk.yeonj.paymanager.ui.calendar

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import io.realm.RealmResults
import io.realm.Sort
import konkuk.yeonj.paymanager.MainActivity
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.convertToDate
import konkuk.yeonj.paymanager.data.Work
import kotlinx.android.synthetic.main.calendar_day_layout.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment : Fragment() {
    lateinit var v:View
    private var oldDate: LocalDate? = null
    private var selectedDay: MutableLiveData<LocalDate> = MutableLiveData()
    lateinit var rViewAdapter: CalListAdapter
    lateinit var placeRViewAdapter:CalPlaceListAdapter
    lateinit var selectedWorkResult: ArrayList<Work>
    private lateinit var mainActivity: MainActivity
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월")


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_calendar, container, false)
        mainActivity = activity as MainActivity
        selectedDay.value = LocalDate.now()
        // 선택한 현재 날짜 가져오기
        selectedWorkResult = mainActivity.getWorkListByDate(selectedDay.value!!.convertToDate())
        val calView = v.findViewById<CalendarView>(R.id.calendarView)
        selectedDay.observe(this, {
            if (oldDate != null) calendarView.notifyDateChanged(oldDate!!)
            oldDate = it
            calView.notifyDateChanged(it)
            selectedWorkResult.clear()
            selectedWorkResult.addAll(mainActivity.getWorkListByDate(selectedDay.value!!.convertToDate()))
            rViewAdapter.notifyDataSetChanged()
        })
        // work 추가, 삭제 시 업데이트
        mainActivity.workResults.addChangeListener { _, _ ->
            calView.notifyDateChanged(selectedDay.value!!)
            selectedWorkResult.clear()
            selectedWorkResult.addAll(mainActivity.getWorkListByDate(selectedDay.value!!.convertToDate()))
            rViewAdapter.notifyDataSetChanged()
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendarView()
        initWorkRView()
        initPlaceRView()
    }

    private fun initCalendarView(){
        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.calendarDayText)
            val rView = view.findViewById<RecyclerView>(R.id.circleListView)
            val tranView = view.findViewById<View>(R.id.tranView)
            lateinit var day:CalendarDay
            init{
                tranView.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && selectedDay.value!! != day.date){
                        selectedDay.value = day.date
                    }
                }
            }
        }

        //캘린더 각 날짜의 뷰의 데이터 바인딩
        calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.textView.text = day.date.dayOfMonth.toString()

                container.rView.layoutManager = GridLayoutManager(mainActivity.applicationContext, 3)
                container.rView.adapter = CircleGridAdapter(
                    mainActivity.getWorkListByDate(day.date.convertToDate()),
                    mainActivity.applicationContext
                )
                if(day.owner == DayOwner.THIS_MONTH){
                    container.textView.setTextColor(Color.DKGRAY)
                    when{
                        selectedDay.value!! == day.date -> {
                            container.view.background = ContextCompat.getDrawable(context!!, R.color.colorPrimaryLight)
                        }
                        else->{
                            container.view.background = null
                        }
                    }
                }
                else{
                    container.textView.setTextColor(Color.GRAY)
                }

            }

            override fun create(view: View) = DayViewContainer(view)
        }

        //캘린더 날짜 속성 초기화
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
        calendarView.monthScrollListener = {
            calendarMonthText.text = it.yearMonth.format(dateFormat)
        }
    }

    private fun initWorkRView(){
        // 아래 recyclerview
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        workRView.layoutManager = layoutManager
        rViewAdapter = CalListAdapter(selectedWorkResult, mainActivity.applicationContext)
        rViewAdapter.itemClickListener = object : CalListAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: CalListAdapter.ViewHolder,
                view: View,
                workId: String
            ) {
                val intent = Intent(mainActivity, AddWorkActivity::class.java)
                intent.putExtra("workId", workId)
                startActivity(intent)
            }
        }
        workRView.adapter = rViewAdapter
    }

    private fun initPlaceRView(){
        // 가로 탭 recyclerview
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.HORIZONTAL, false
        )
        placeRView.layoutManager = layoutManager
        placeRViewAdapter = CalPlaceListAdapter(mainActivity.placeResults, mainActivity.applicationContext)
        placeRView.adapter = placeRViewAdapter
        placeRViewAdapter.itemClickListener= object : CalPlaceListAdapter.OnItemClickListener{
            override fun OnItemClick(holder: CalPlaceListAdapter.ViewHolder, view: View, pos: Int) {
                val intent = Intent(mainActivity, AddWorkActivity::class.java)
                intent.putExtra("placeId", mainActivity.placeResults[pos]!!.id)
                intent.putExtra("selectedDay", selectedDay.value!!.convertToDate().time)
                startActivity(intent)
            }
        }
    }

}