package konkuk.yeonj.paymanager

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var placeResults: RealmResults<Place>
    lateinit var workResults: RealmResults<Work>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        Realm.init(applicationContext)
        //db 구조 바뀌면 초기화하는 옵션
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().name("appdb.realm").build()
        Realm.setDefaultConfiguration(config)

        realm = Realm.getDefaultInstance()


        getDataFromRealm()

    }

    fun getDataFromRealm(){
        placeResults = realm.where(Place::class.java).findAll()!!.sort("timePush", Sort.ASCENDING)
        workResults = realm.where(Work::class.java).findAll().sort("date", Sort.ASCENDING)


        //데이터 초기화 구문
//        realm.beginTransaction()
//        realm.where(Work::class.java).findAll()!!.deleteAllFromRealm()
//        realm.commitTransaction()
//        realm.beginTransaction()
//        realm.where(Place::class.java).findAll()!!.deleteAllFromRealm()
//        realm.commitTransaction()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }



    fun getWorkListByDate(date:Date): ArrayList<Work> {
        val list = arrayListOf<Work>()
        val result = workResults.where()
            .equalTo("date", date)
            .findAll()!!.sort("timePush", Sort.ASCENDING)
        list.addAll(realm.copyFromRealm(result))
        return list
    }
}