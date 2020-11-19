package konkuk.yeonj.paymanager.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import io.realm.RealmResults
import io.realm.Sort
import konkuk.yeonj.paymanager.MainActivity
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.ui.setting.AddPlaceActivity
import konkuk.yeonj.paymanager.ui.setting.SettingListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.coroutines.selects.select
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment : Fragment() {
    lateinit var v:View
    var selectedDay = LocalDate.now()
    lateinit var rViewAdapter: CalListAdapter
    lateinit var placeRViewAdapter:CalPlaceListAdapter
    lateinit var selectedWorkResult: RealmResults<Work>
    private lateinit var mainActivity: MainActivity



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_calendar, container, false)
        init()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedWorkResult = mainActivity.workResults.where().equalTo("date",Date.from(selectedDay.atStartOfDay(
            ZoneId.systemDefault()).toInstant())).findAll()!!.sort("timePush", Sort.ASCENDING)

        //캘린더 각 날짜의 뷰의 데이터 바인딩
        calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        calendarView.outDateStyle = OutDateStyle.END_OF_GRID
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                container.view.setOnClickListener {
                    it.background = ContextCompat.getDrawable(context!!, R.color.colorPrimaryLight)
                    selectedDay = day.date
                    selectedWorkResult = mainActivity.workResults.where().equalTo("date", Date.from(selectedDay.atStartOfDay(ZoneId.systemDefault()).toInstant())).findAll()!!.sort("timePush", Sort.ASCENDING)
                    Log.d("mytag", selectedDay.toString())
                    Log.d("mytag", selectedWorkResult.toString())
                    //ㅇ여기코드좀 손봐야할ㄷ스 너무 대작업
                    rViewAdapter = CalListAdapter(selectedWorkResult, mainActivity.applicationContext, mainActivity.placeResults)
                    rViewAdapter.itemClickListener = object : CalListAdapter.OnItemClickListener{
                        override fun OnItemClick(
                            holder: CalListAdapter.ViewHolder,
                            view: View,
                            placeId: String
                        ) {
                            val intent = Intent(mainActivity, AddWorkActivity::class.java)
                            intent.putExtra("placeId", placeId)
                            startActivity(intent)
                        }
                    }
                    rView.adapter = rViewAdapter
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
            calendarMonthText.text = it.yearMonth.toString()
        }

        // 아래 recyclerview
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        rView.layoutManager = layoutManager
        rViewAdapter = CalListAdapter(selectedWorkResult, mainActivity.applicationContext, mainActivity.placeResults)
        rViewAdapter.itemClickListener = object : CalListAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: CalListAdapter.ViewHolder,
                view: View,
                placeId: String
            ) {
                val intent = Intent(mainActivity, AddWorkActivity::class.java)
                intent.putExtra("placeId", placeId)
                startActivity(intent)
            }
        }
        rView.adapter = rViewAdapter

        // 가로 탭 recyclerview
        val layoutManager2 = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.HORIZONTAL, false
        )
        placeRView.layoutManager = layoutManager2
        placeRViewAdapter = CalPlaceListAdapter(mainActivity.placeResults, mainActivity.applicationContext)
        placeRView.adapter = placeRViewAdapter
        placeRViewAdapter.itemClickListener= object : CalPlaceListAdapter.OnItemClickListener{
            override fun OnItemClick(holder: CalPlaceListAdapter.ViewHolder, view: View, pos: Int) {
                val intent = Intent(mainActivity, AddWorkActivity::class.java)
                intent.putExtra("placeId", mainActivity.placeResults[pos]!!.id)
                startActivity(intent)
            }
        }
    }

    private fun init(){
        mainActivity = activity as MainActivity
    }
}