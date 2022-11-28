package com.team8.finalsubmission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_grid_item_cart_menu.view.*

class MenuListAdapterCart(var list: ArrayList<String>): RecyclerView.Adapter<MenuListAdapterCart.ListAdapter>() {

    class ListAdapter(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_grid_item_cart_menu, parent, false)

        return ListAdapter(view)
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {
        holder.layout.textListTitle.text = list[position]

        holder.layout.layoutListItem.setOnClickListener {
            Toast.makeText(holder.layout.context, "${list[position]} Click!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}