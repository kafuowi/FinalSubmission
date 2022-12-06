package com.team8.finalsubmission

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team8.finalsubmission.databinding.ActivitySelectMenuBinding
import kotlinx.android.synthetic.main.activity_select_menu.*
import kotlinx.android.synthetic.main.activity_select_menu.view.*
import kotlinx.android.synthetic.main.dialog_add_menu.view.*
import kotlinx.android.synthetic.main.dialog_manage_mode_menu_selected.view.*
import kotlinx.android.synthetic.main.menudialog.view.*

class ManageModeMainActivity: AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySelectMenuBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    lateinit var	databaseMenu: DatabaseReference
    var	itemCount:	Long	=	0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectMenuBinding.inflate(layoutInflater)
        binding.returnButton.setText("메뉴추가")

        binding.returnButton.setOnClickListener { //메뉴추가버튼
            Toast.makeText(this, "메뉴추가", Toast.LENGTH_SHORT).show()

            val builder = AlertDialog.Builder(this)
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu, null)
            mDialogView.menu_image_view.setOnClickListener{

            }
            builder
                .setView(mDialogView)
                .setTitle("Title")
                .setPositiveButton("Start",
                    DialogInterface.OnClickListener { dialog, id ->
                        val tempItem :MenuData = MenuData()
                        tempItem.name = mDialogView.menu_name_edit_text.text.toString()
                        tempItem.price =
                            mDialogView.menu_price_edit_text.text.toString().toDouble().toInt()
                        tempItem.UID = mDialogView.menu_name_edit_text.text.toString()
                        tempItem.imageURL ="https://i.ibb.co/7bMtvXy/image.jpg"
                        tempItem.quantity=0
                        tempItem.serving=0
                        databaseMenu.child(tempItem.UID).setValue(tempItem);

                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Cancel 버튼 선택 시 수행
                    })
// Create the AlertDialog object and return it
            builder.create()
            val mAlertDialog=builder.show()
        }

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        databaseMenu	=	Firebase.database.getReference("menu")

        var list :ArrayList<MenuData> = ArrayList<MenuData>()
        var listManager = GridLayoutManager(this, 3)
        var listAdapter = MenuListAdapterGrid(list)

        refreshMenuGrid(listManager,listAdapter)
        var cart: ArrayList<String> =ArrayList<String>()


        //var cart = arrayListOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8")
        var cartManager = LinearLayoutManager(this)
        var cartAdapter = MenuListAdapterCart(cart)
        refreshCart(cartManager,cartAdapter)


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
                listAdapter.setOnItemClickListener(object : MenuListAdapterGrid.OnItemClickListener{
                    override fun onItemClick(v: View, data: String, pos: Int) {
                        Toast.makeText(v.context, "${data.toString()} Click!", Toast.LENGTH_SHORT).show()

                        val builder = AlertDialog.Builder(v.context)
                        val mDialogView = LayoutInflater.from(v.context).inflate(R.layout.dialog_manage_mode_menu_selected, null)
                        builder
                            .setView(mDialogView)
                            .setTitle("Title")
                            .setPositiveButton("Start",
                                DialogInterface.OnClickListener { dialog, id ->
                                    cart.add(data.toString())
                                    refreshCart(cartManager,cartAdapter)

                                })
                            .setNegativeButton("Cancel",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // Cancel 버튼 선택 시 수행
                                })
// Create the AlertDialog object and return it
                        builder.create()
                        val mAlertDialog=builder.show()
                        Glide.with(mDialogView)
                            .load(list[pos].imageURL) // 불러올 이미지 url
                            .placeholder(R.drawable.ic_launcher_background) // 이미지 로딩 시작하기 전 표시할 이미지
                            .error(R.drawable.rabbit) // 로딩 에러 발생 시 표시할 이미지
                            .fallback(R.drawable.cat) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                            .into(mDialogView.selected_menu_image) // 이미지를 넣을 뷰


                    }

                })

                refreshMenuGrid(listManager,listAdapter)


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )





    }
    fun refreshCart(cartManager : LinearLayoutManager, cartAdapter: MenuListAdapterCart){
        var recyclerCart = menuRecyclerCartView.apply{
            setHasFixedSize(true)
            layoutManager = cartManager
            adapter = cartAdapter

        }

    }
    fun refreshMenuGrid(gridManager: GridLayoutManager, gridAdapter: MenuListAdapterGrid){
        var recyclerList = menuRecyclerGridView.apply {
            setHasFixedSize(true)
            layoutManager = gridManager
            adapter = gridAdapter
        }
    }
}