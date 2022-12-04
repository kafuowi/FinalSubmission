package com.team8.finalsubmission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.list_grid_item_menu.view.*

class MenuListAdapterGrid(var list: ArrayList<MenuData>): RecyclerView.Adapter<MenuListAdapterGrid.GridAdapter>() {

    class GridAdapter(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_grid_item_menu, parent, false)

        return GridAdapter(view)
    }

    override fun onBindViewHolder(holder: GridAdapter, position: Int) {
        holder.layout.textListTitle.text = list[position].name
        Glide.with(holder.layout)
            .load(list[position].imageURL) // 불러올 이미지 url
            .placeholder(R.drawable.ic_launcher_background) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.rabbit) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.cat) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .circleCrop() // 동그랗게 자르기
            .into(holder.layout.GridItemImage) // 이미지를 넣을 뷰

        holder.layout.layoutListItem.setOnClickListener {
            Toast.makeText(holder.layout.context, "${list[position]} Click!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}