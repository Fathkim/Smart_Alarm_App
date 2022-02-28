package com.fathan.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import com.fathan.smartalarm.data.Alarm
import com.fathan.smartalarm.data.local.AlarmDB
import com.fathan.smartalarm.databinding.ActivityRepatingAlarmBinding
import com.fathan.smartalarm.helder.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityRepatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepatingAlarmBinding

    private val db by lazy { AlarmDB(this) }

    private var alarmReceiver: AlarmReciver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepatingAlarmBinding.inflate(layoutInflater)
        alarmReceiver = AlarmReciver()

        setContentView(binding.root)

        binding.apply {
            btnSetTimeRepeting.setOnClickListener {
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }
            btnAdd.setOnClickListener {
                val time = tvOnceTime.text.toString()
                val message = edtNoteRepeting.text.toString()

                if(time == "Time") {
                    Toast.makeText(applicationContext, getString(R.string.txt_toats_set_alarm), Toast.LENGTH_SHORT).show()
                } else {
                    alarmReceiver?.setRepeatingAlarm(
                        applicationContext,
                        AlarmReciver.TYPE_REPEATING,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                //id:0 untuk memilih alarm yang mana
                            0,
                            "Repeting Alarm",
                            time,
                            message,
                            AlarmReciver.TYPE_REPEATING
                        )
                        )
                        Log.i("AddAlarm", "alarm set on: $time with message $message")
                        finish()
                    }
                }
            }

            btnCancel.setOnClickListener {
                finish()
            }
        }
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
      }
}