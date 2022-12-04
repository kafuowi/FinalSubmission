package com.team8.finalsubmission

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team8.finalsubmission.databinding.ActivitySelectMenuBinding
import kotlinx.android.synthetic.main.activity_select_menu.*

class MenuActivity : AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySelectMenuBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    lateinit var	databaseMenu: DatabaseReference
    var	itemCount:	Long	=	0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectMenuBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        databaseMenu	=	Firebase.database.getReference("menu")
        /*binding.btnPost.setOnClickListener {
            val name	=	binding.editName.text.toString()
            val password	=	binding.editPassword.text.toString()
            val user	=	User(name,	password)
            addItem(user)
        }*/
        var list :ArrayList<MenuData> = ArrayList<MenuData>()
        var listManager = GridLayoutManager(this, 3)
        var listAdapter = MenuListAdapterGrid(list)

        var recyclerList = menuRecyclerGridView.apply {
            setHasFixedSize(true)
            layoutManager = listManager
            adapter = listAdapter
        }
        databaseMenu.addValueEventListener(object: ValueEventListener {
            override	fun	onDataChange(snapshot: DataSnapshot)	{
                //binding.textList.setText("")
                Log.d("Fire",	"Count:	${snapshot.childrenCount}")
                itemCount =	snapshot.childrenCount
                list = ArrayList<MenuData>()
                for(item	in	snapshot.children)	{
                    val key	=	item.key
                    val menu	=	item.getValue(MenuData::class.java)
                    //binding.textList.append("name:	${menu?.name}:	password${menu?.password}	\n")
                    if (menu != null) {
                        list.add(menu)
                    }
                }

                Log.d("Fire1",	"Count:	")
                listAdapter = MenuListAdapterGrid(list)

                var recyclerList = menuRecyclerGridView.apply {
                    setHasFixedSize(true)
                    layoutManager = listManager
                    adapter = listAdapter
                }
            }
            override	fun	onCancelled(error: DatabaseError)	{
                print(error.message)
            }
        })



        var cart = arrayListOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8")
        var cartManager = LinearLayoutManager(this)
        var cartAdapter = MenuListAdapterCart(cart)

        var recyclerCart = menuRecyclerCartView.apply{
            setHasFixedSize(true)
            layoutManager = cartManager
            adapter = cartAdapter

        }

    }
}