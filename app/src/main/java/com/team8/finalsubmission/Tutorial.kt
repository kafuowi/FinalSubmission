package com.team8.finalsubmission

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
import kotlinx.android.synthetic.main.list_grid_item_menu.view.*
import kotlinx.android.synthetic.main.menudialog.view.*

class MenuActivity : AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySelectMenuBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    //튜토리얼 fragment
    lateinit var transaction: FragmentTransaction
    var presentFragment: Fragment? = null
    lateinit var fragmentManager : FragmentManager


    lateinit var	databaseMenu: DatabaseReference
    var	itemCount:	Long	=	0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectMenuBinding.inflate(layoutInflater)


        //튜토리얼 fragment
        var firstFragment = TutorialFragment()
        var secondFragment = TutorialFragment2()
        var thirdFragment = TutorialFragment3()

        fragmentManager = supportFragmentManager

        replaceTransaction(firstFragment)
        binding.fragmentTutorial.isClickable;

        binding.fragmentTutorial.setOnClickListener {
            if(presentFragment==firstFragment){
                replaceTransaction(secondFragment)
            }else{
                replaceTransaction(thirdFragment)
            }
        }

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

        refreshMenuGrid(listManager,listAdapter)
        var cart: ArrayList<String> =ArrayList<String>()


        //var cart = arrayListOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8")
        var cartManager = LinearLayoutManager(this)
        var cartAdapter = MenuListAdapterCart(cart)
        refreshCart(cartManager,cartAdapter)


        fragment_tutorial.bringToFront()

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

                        var menuCount =0
                        val builder = AlertDialog.Builder(v.context)
                        val mDialogView = LayoutInflater.from(v.context).inflate(R.layout.menudialog, null)
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
    fun refreshCart(cartManager :LinearLayoutManager, cartAdapter: MenuListAdapterCart){
        var recyclerCart = menuRecyclerCartView.apply{
            setHasFixedSize(true)
            layoutManager = cartManager
            adapter = cartAdapter

        }

    }
    fun refreshMenuGrid(gridManager: GridLayoutManager,gridAdapter: MenuListAdapterGrid){
        var recyclerList = menuRecyclerGridView.apply {
            setHasFixedSize(true)
            layoutManager = gridManager
            adapter = gridAdapter
        }
    }
    //fragment
    fun	replaceTransaction(fragment:	Fragment)	{
        if(presentFragment ==	fragment)	{
            Toast.makeText(this,	"음식을 골라주세요.",
                Toast.LENGTH_SHORT).show()
            binding.fragmentTutorial.visibility=View.INVISIBLE
            return
        }
        transaction	=	fragmentManager.beginTransaction()
        transaction.replace(binding.fragmentTutorial.id, fragment).commit()
        presentFragment =	fragment
    }
}