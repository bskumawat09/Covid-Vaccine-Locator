package com.example.covidvaccination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.covidvaccination.databinding.ItemViewBinding
import com.example.covidvaccination.model.Center

class CenterAdapter : RecyclerView.Adapter<CenterAdapter.CenterViewHolder>() {
    var centerList = arrayListOf<Center>()

    inner class CenterViewHolder(val itemBinding: ItemViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenterViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CenterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CenterViewHolder, position: Int) {
        holder.itemBinding.apply {
            val currentCenter = centerList.get(position)
            tvCenter.text = currentCenter.name
            tvLocation.text = currentCenter.address + ", " + currentCenter.state
            tvTiming.text = "From ${currentCenter.from} To ${currentCenter.to}"
            tvVaccine.text = currentCenter.vaccine
            tvFees.text = currentCenter.fees
            tvAgeLimit.text = "Age limit: ${currentCenter.ageLimit}"
            tvAvailability.text = "Available: ${currentCenter.availability}"
        }
    }

    override fun getItemCount(): Int {
        return centerList.size
    }

    fun update(newCenterList: List<Center>) {
        centerList.clear()
        centerList.addAll(newCenterList)
        notifyDataSetChanged()
    }
}