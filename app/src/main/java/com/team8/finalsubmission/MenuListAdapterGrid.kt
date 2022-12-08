package com.team8.finalsubmission

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.list_grid_item_menu.view.*
import kotlinx.android.synthetic.main.menudialog.view.*

class MenuListAdapterGrid(var list: ArrayList<MenuData>): RecyclerView.Adapter<MenuListAdapterGrid.GridAdapter>() {

    class GridAdapter(val layout: View): RecyclerView.ViewHolder(layout)

    interface OnItemClickListener{
        fun onItemClick(v:View, data: MenuData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_grid_item_menu, parent, false)

        return GridAdapter(view)
    }

    override fun onBindViewHolder(holder: GridAdapter, position: Int) {
                holder.layout.X_button.visibility=View.INVISIBLE
        holder.layout.textListTitle.text = list[position].name
        Glide.with(holder.layout)
            .load(list[position].imageURL) // 불러올 이미지 url
            .placeholder(R.drawable.ic_launcher_background) // 이미지 로딩 시작하기 전 표시할 이미지
            .error(R.drawable.rabbit) // 로딩 에러 발생 시 표시할 이미지
            .fallback(R.drawable.cat) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .into(holder.layout.GridItemImage) // 이미지를 넣을 뷰


        holder.layout.layoutListItem.setOnClickListener {
            listener?.onItemClick(holder.layout,list[position],position)
            //Toast.makeText(holder.layout.context, "${listener.toString()} Click!", Toast.LENGTH_SHORT).show()

        }
    /*
        holder.layout.layoutListItem.setOnClickListener {
            val builder = AlertDialog.Builder(holder.layout.context)
            val mDialogView = LayoutInflater.from(holder.layout.context).inflate(R.layout.menudialog, null)
            builder
                .setView(mDialogView)
                .setTitle("Title")
                .setPositiveButton("Start",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Start 버튼 선택 시 수행
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Cancel 버튼 선택 시 수행
                    })
// Create the AlertDialog object and return it
            builder.create()
            val mAlertDialog=builder.show()
            var menuCount =0
            Glide.with(mDialogView)
                .load(list[position].imageURL) // 불러올 이미지 url
                .placeholder(R.drawable.ic_launcher_background) // 이미지 로딩 시작하기 전 표시할 이미지
                .error(R.drawable.rabbit) // 로딩 에러 발생 시 표시할 이미지
                .fallback(R.drawable.cat) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .into(mDialogView.dialogueImage) // 이미지를 넣을 뷰

            mDialogView.MenuNumberMonitor.setText(menuCount.toString())
            mDialogView.backToMenu.setOnClickListener { mAlertDialog.dismiss()}
            mDialogView.ButtonMinus.setOnClickListener {
                if(menuCount>0) {
                    menuCount -= 1
                    mDialogView.MenuNumberMonitor.setText(menuCount.toString())
                }
            }
            mDialogView.ButtonPlus.setOnClickListener {
                menuCount+=1
                mDialogView.MenuNumberMonitor.setText(menuCount.toString())
            }
        }*/
    }


    override fun getItemCount(): Int {
        return list.size
    }


}