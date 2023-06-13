package com.example.weatherkt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherkt.databinding.CityBinding
import com.example.weatherkt.model.weaModel

class weatherAdapter (val context: Context, val list:ArrayList<weaModel>)
    :RecyclerView.Adapter<weatherAdapter.weatherViewHolder>() {

        inner class weatherViewHolder(val binding: CityBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): weatherViewHolder {
        return weatherViewHolder(
            CityBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: weatherViewHolder, position: Int) {
      holder.binding.cityNameTextView.text=list[position].city
        holder.binding.temperatureTextView.text=list[position].temp
        holder.binding.weatherConditionTextView.text=list[position].desp

    }

    override fun getItemCount(): Int {
        return list.size
    }

}