package com.team8.finalsubmission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_grid_item_cart_menu.view.*
import kotlinx.android.synthetic.main.list_grid_item_cart_menu.view.layoutListItem
import kotlinx.android.synthetic.main.list_grid_item_cart_menu.view.textListTitle
import kotlinx.android.synthetic.main.list_grid_item_menu.view.*
import kotlinx.android.synthetic.main.list_item_menu_category.view.*

class MenuListAdapterCategory(var list: ArrayList<CategoryData>): RecyclerView.Adapter<MenuListAdapterCategory.ListAdapter>() {

    class ListAdapter(val layout: View): RecyclerView.ViewHolder(layout)

    interface OnItemClickListener{
        fun onItemClick(v:View, data: CategoryData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_menu_category, parent, false)

        return ListAdapter(view)
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {
        holder.layout.CategoryButton.text = list[position].name
        /*Glide.with(holder.layout)
            .load(list[position].imageURL) // 불러올 이미지 url
            .placeholder(R.drawable.ic_launcher_background) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.rabbit) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.cat) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .into(holder.layout.CartItemImage) // 이미지를 넣을 뷰

         */


        holder.layout.CategoryButton.setOnClickListener {
            listener?.onItemClick(holder.layout,list[position],position)
            //Toast.makeText(holder.layout.context, "${listener.toString()} Click!", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}