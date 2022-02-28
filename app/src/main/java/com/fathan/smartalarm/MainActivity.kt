package com.fathan.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fathan.smartalarm.adapter.AlarmAdapter
import com.fathan.smartalarm.data.Alarm
import com.fathan.smartalarm.data.local.AlarmDB
import com.fathan.smartalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmAdapter: AlarmAdapter? = null

    private var alarmService: AlarmReciver? = null

    private val db by lazy { AlarmDB(this) }

    override fun onResume() {
        super.onResume()
        db.alarmDao().getAlarm().observe(this){
            alarmAdapter?.setData(it)
            Log.i("GetAlarm", "setupRecyvlerView: with this data $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReciver()

        initView()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.apply {
            alarmAdapter = AlarmAdapter()
                rvReminderAlarm.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = alarmAdapter
            }
            swipeToDelete(rvReminderAlarm)
        }
    }

    private fun initView() {
        binding.apply {
            cvSetOneTimeAlarm.setOnClickListener {
                startActivity(Intent(applicationContext, OneTimeAlarmActivity::class.java))
            }
            cvSetReapitingAlarm.setOnClickListener {
                startActivity(Intent(applicationContext, RepeatingAlarmActivity::class.java))
            }
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)

                CoroutineScope(Dispatchers.IO).launch {
                    deletedItem?.let { db.alarmDao().deleteAlarm(it) }
                    Log.i("DeleteAlarm","onSwiped: succes deleted alarm with $deletedItem")
                }

                Toast.makeText(applicationContext, "Successfully deleted", Toast.LENGTH_SHORT).show()

//                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition)

                deletedItem?.type?.let { alarmService?.cancelAlarm(applicationContext, it) }
            }
        }).attachToRecyclerView(recyclerView)
    }
}
   /* private fun initDateToday() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd ,MMMM, yyyy", Locale.getDefault() )
        val formattedDate = dateFormat.format(calendar.time)

        binding.tvDateToday.text = formattedDate
    }

    private fun initTimeToday() {
        //calender buat dapatin segala hal yang berhubungan dengan waktu di android
        val calendar = Calendar.getInstance()
        //menemukan format jam yang akan digunakan , contohnya 13:45 atau 01:56 p.m. atau 13.45.34a
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)

        binding.tvTimeToday.text = formattedTime
    }*/

