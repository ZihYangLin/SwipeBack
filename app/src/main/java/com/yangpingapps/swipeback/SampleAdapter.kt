package com.yangpingapps.swipeback

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SampleAdapter : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SampleViewHolder {
        return SampleViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_sample, p0, false))
    }

    override fun getItemCount(): Int = 500

    override fun onBindViewHolder(p0: SampleViewHolder, p1: Int) {
        p0.setText("position: $p1")
    }


    inner class SampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun setText(str: String) {
            itemView.findViewById<TextView>(R.id.textview1).text = str
        }
    }
}