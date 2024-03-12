package com.pqc.qr.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pqc.qr.R
import com.pqc.qr.data.remote.model.local.DashboardList
import com.pqc.qr.databinding.ItemDashboardBinding
import com.pqc.qr.utils.extensionFunction.isViewSelected


/**
 * Created by Jeetesh Surana.
 */

class DashboardListAdapter(
    private var dashboardList: ArrayList<DashboardList>,
    private var itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<DashboardListAdapter.DashboardAdapterViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardAdapterViewHolder {
        return DashboardAdapterViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_dashboard, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: DashboardAdapterViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val mData = dashboardList[position]
        holder.bindData(mData)
        holder.binding.txtDashboardItem.text = mData.name

        holder.itemView.setOnClickListener {
            itemClickListener.itemClick(position, mData)
        }

        isViewSelected(
            holder.binding.imgDashboardItem,
            holder.binding.txtDashboardItem,
            isChecked = mData.isChecked
        )

        mData.image?.let {
            holder.binding.imgDashboardItem.setImageResource(
                it
            )
        }
    }

    override fun getItemCount() = dashboardList.size

    class DashboardAdapterViewHolder(var binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(list: DashboardList) = binding.apply {
            mData = list
            executePendingBindings()
        }
    }

    interface ItemClickListener {
        fun itemClick(position: Int, item: DashboardList)
    }
}
