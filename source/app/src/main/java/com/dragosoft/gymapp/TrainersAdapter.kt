package com.dragosoft.gymapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R

class TrainersAdapter(var mList: List<TrainersData>) : RecyclerView.Adapter<TrainersAdapter.TrainersViewHolder>() {

    inner class TrainersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val logo : ImageView = itemView.findViewById(R.id.logoIv)
        val title : TextView = itemView.findViewById(R.id.titleTv)
    }

    fun setFilteredList(mList: List<TrainersData>){
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return TrainersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: TrainersViewHolder, position: Int) {
        holder.logo.setImageURI(Uri.parse(mList[position].photo))
        holder.title.text = mList[position].name
    }
}