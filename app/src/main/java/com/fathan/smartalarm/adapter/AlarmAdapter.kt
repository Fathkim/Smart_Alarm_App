package com.fathan.smartalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fathan.smartalarm.AlarmReciver
import com.fathan.smartalarm.R
import com.fathan.smartalarm.data.Alarm
import com.fathan.smartalarm.databinding.RowItemAlarmBinding

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {

    var listAlarm: ArrayList<Alarm> = arrayListOf()

    inner class MyViewHolder (val binding: RowItemAlarmBinding):
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        RowItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alarm = listAlarm[position]
        holder.binding.apply {
            itemDateAlarm.text = alarm.date
            itemTimeAlarm.text = alarm.time
            itemNoteAlarm.text = alarm.message
            if (alarm.type == AlarmReciver.TYPE_ONE_TIME) {
                itemImgOneTime.setImageResource(R.drawable.ic_one_time)
            } else {
                itemImgOneTime.setImageResource(R.drawable.ic_repeating)
            }
        }
    }

    override fun getItemCount() = listAlarm.size

    fun setData(data: List<Alarm>) {
        val alarmDiffutil = AlarmDiffUtil(listAlarm, data)
        val diffutilResult = DiffUtil.calculateDiff(alarmDiffutil)
        listAlarm.clear()
        listAlarm.addAll(data)
        diffutilResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }
}
