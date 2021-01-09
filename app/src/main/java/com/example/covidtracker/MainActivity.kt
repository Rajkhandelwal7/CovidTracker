@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.example.covidtracker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.AbsListView
import androidx.work.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.coroutines.*
import okhttp3.OkHttp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var stateadapter: StateCovidAdapter

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_header, list, false))
        fetchResult()
        swipeToRefresh.setOnRefreshListener {
            fetchResult()
        }
        initWorker()
        list.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                    view: AbsListView,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
            ) {
                if (list.getChildAt(0) != null) {
                    swipeToRefresh.isEnabled = list.firstVisiblePosition === 0 && list.getChildAt(
                            0
                    ).getTop() === 0
                }
            }
        })
    }







    private fun fetchResult() {
        GlobalScope.launch(Dispatchers.Main)
        {
            val response = withContext(Dispatchers.IO)
            { Client.api.clone().execute() }

            if (response.isSuccessful) {
                swipeToRefresh.isRefreshing = false
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise[0])
                    bindStatewiseData(data.statewise.subList(1, data.statewise.size))
                }
            }

        }
    }

    private fun bindStatewiseData(subList: List<StatewiseItem>) {
        stateadapter = StateCovidAdapter(subList)
        list.adapter = stateadapter

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun bindCombinedData(data: StatewiseItem) {
        val lastupdatedtime = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastupdatedtv.text = "Last Updated\n ${
            getTimeAgo(
                    simpleDateFormat.parse(lastupdatedtime)
            )
        }"
        confirmedTv.text = data.confirmed
        activeTv.text = data.active
        deceasedTv.text = data.deaths
        recoveredTv.text = data.recovered


    }
    @InternalCoroutinesApi
   private fun initWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "JOB_TAG",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }



}
fun getTimeAgo(past: Date): String {
    val now = Date()
    val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
    val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

    return when {
        seconds < 60 -> {
            "Few seconds ago"
        }
        minutes < 60 -> {
            "$minutes minutes ago"
        }
        hours < 24 -> {
            "$hours hour ${minutes % 60} min ago"
        }
        else -> {
            SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
        }
    }
}
