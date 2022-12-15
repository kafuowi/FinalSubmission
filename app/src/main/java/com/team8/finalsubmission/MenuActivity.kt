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
import kotlinx.android.synthetic.main.list_grid_item_cart_menu.view.*
import kotlinx.android.synthetic.main.menudialog.view.*
import kotlin.collections.ArrayList

class MenuActivity : AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySelectMenuBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    lateinit var cartAdapter : MenuListAdapterCart
    lateinit var cartManager : LinearLayoutManager
    lateinit var cart: ArrayList<MenuData> // 카트 데이터 배열
    lateinit var cartCount: ArrayList<Int>

    lateinit var databaseMenu: DatabaseReference //메뉴 데이터베이스
    lateinit var databaseCategory:DatabaseReference //카테고리 데이터베이스
    var	itemCount: Long = 0

    //튜토리얼 fragment
    lateinit var transaction: FragmentTransaction
    var presentFragment: Fragment? = null
    lateinit var fragmentManager : FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectMenuBinding.inflate(layoutInflater)
        binding.CategoryAddButton.visibility = View.INVISIBLE
        binding.returnButton.text = "결제"

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        val tutorialintent = intent
        val t = tutorialintent.getBooleanExtra("tutorialintent",false)

        if(t){
            //튜토리얼 fragment
            var firstFragment = TutorialFragment()
            var secondFragment = TutorialFragment2()
            var thirdFragment = TutorialFragment3()

            fragmentManager = supportFragmentManager

            replaceTransaction(firstFragment)
            binding.fragmentTutorial.isClickable;

            binding.fragmentTutorial.setOnClickListener {
                if (presentFragment == firstFragment) {
                    replaceTransaction(secondFragment)
                } else {
                    replaceTransaction(thirdFragment)
                }
            }

        }

        binding.returnButton.setOnClickListener {
            val intent = Intent(this, PaymentCheckActivity::class.java)
            intent.putExtra("cart",cart )
            intent.putExtra("cartCount",cartCount)
            startActivity(intent)
        }


        databaseMenu	=	Firebase.database.getReference("menu")
        databaseCategory = Firebase.database.getReference("menucategory")

        var list :ArrayList<MenuData> = ArrayList<MenuData>()
        var listManager = GridLayoutManager(this, 3)
        var listAdapter = MenuListAdapterGrid(list)

        refreshMenuGrid(listManager,listAdapter)//메뉴 그리드 갱신

        var categories :ArrayList<CategoryData> = ArrayList<CategoryData>()
        var categoryManager = LinearLayoutManager(this)
        var categoryAdapter = MenuListAdapterCategory(categories)

        refreshCategory(categoryManager,categoryAdapter)//카테고리 리스트 갱신
        cart  = ArrayList<MenuData>()// 장바구니 데이터 변수
        cartCount  = ArrayList<Int>()


         cartManager = LinearLayoutManager(this)//장바구니 리스트 매니저
         cartAdapter = MenuListAdapterCart(cart)//장바구니 어댑터 매니저
        cartAdapter.setOnItemCreate(object: MenuListAdapterCart.OnItemCreate{//카트 아이템이 생성 될 때 명령어
            override fun onItemCreate(v: View, data: MenuData, pos: Int) {
                v.X_button_cart.setOnClickListener {
                    //Toast.makeText(v.context, "${data.toString()} ClickXXX", Toast.LENGTH_SHORT).show()
                    cart.removeAt(pos)
                    refreshCart(cartManager,cartAdapter)
                }
            }

        })

            /*.setOnItemClickListener(object: MenuListAdapterCart.OnItemClickListener {
                override fun onItemClick(v: View, data: MenuData, pos: Int) {

                v.X_button_cart.setOnClickListener {
                    Toast.makeText(v.context, "${data.toString()} ClickXXX", Toast.LENGTH_SHORT).show()
                    cart.removeAt(pos)
                    refreshCart(cartManager,cartAdapter)
                }
            }
        })*/
        refreshCart(cartManager,cartAdapter)


        fragment_tutorial.bringToFront()

        databaseCategory.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {//데이터가 바뀔 때

                //binding.textList.setText("")
                Log.d("Category", "Count:	${snapshot.childrenCount}")
                itemCount = snapshot.childrenCount
                categories = ArrayList<CategoryData>()
                for (item in snapshot.children) {
                    val key = item.key
                    val category = item.getValue(CategoryData::class.java)
                    //binding.textList.append("name:	${menu?.name}:	password${menu?.password}	\n")
                    if (category != null) {
                        categories.add(category)
                        Log.d("Category", "Name:	${category.name}")
                    }
                }
                categoryAdapter = MenuListAdapterCategory(categories)//카테고리 adapter
                refreshCategory(categoryManager,categoryAdapter)//카테고리 새로고침

                categoryAdapter.setOnItemClickListener(object : MenuListAdapterCategory.OnItemClickListener {
                    override fun onItemClick(v: View, data: CategoryData, pos: Int) {//카테고리 선택 click listener
                        //Toast.makeText(v.context, "${data.name} Click!", Toast.LENGTH_SHORT) .show()

                        databaseMenu	=	Firebase.database.getReference("menus/${data.name}")//클릭한 카테고리 데이터베이스 가져오기
                        refreshGridData(listManager,listAdapter)//메뉴 그리드 갱신
                    }

                }
                )

                databaseMenu	=	Firebase.database.getReference("menus/${categories[0].name}")//0번째 카테고리 데이터베이스 가져오기

                refreshGridData(listManager,listAdapter)//메뉴 그리드 갱신
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        )








    }
    fun refreshCart(cartManager :LinearLayoutManager, cartAdapter: MenuListAdapterCart){// 카트 새로고침

        var recyclerCart = menuRecyclerCartView.apply{
            setHasFixedSize(true)
            layoutManager = cartManager
            adapter = cartAdapter

        }

    }
    fun refreshMenuGrid(gridManager: GridLayoutManager,gridAdapter: MenuListAdapterGrid){// 메뉴 그리드 새로고침
        var recyclerList = menuRecyclerGridView.apply {
            setHasFixedSize(true)
            layoutManager = gridManager
            adapter = gridAdapter
        }
    }
    fun refreshCategory(categoryManager: LinearLayoutManager, categoryAdapter: MenuListAdapterCategory){// 카테고리 새로고침
        var recyclerCategory = MenuCategoryView.apply {
            setHasFixedSize(true)
            layoutManager = categoryManager
            adapter = categoryAdapter
        }
        recyclerCategory.layoutManager =
            LinearLayoutManager(this).also { it.orientation = LinearLayoutManager.HORIZONTAL }
    }

    fun refreshGridData(gridManager: GridLayoutManager, gridAdapter: MenuListAdapterGrid){//메뉴 그리드 데이터 새로고침

        var listAdapter =gridAdapter
        var listManager = gridManager
        databaseMenu.addValueEventListener(object: ValueEventListener {
            override	fun	onDataChange(snapshot: DataSnapshot)	{//데이터가 바뀔 때
                //binding.textList.setText("")
                Log.d("Fire",	"Count:	${snapshot.childrenCount}")
                itemCount =	snapshot.childrenCount
                var list = ArrayList<MenuData>()
                for(item	in	snapshot.children)	{
                    val key	=	item.key
                    val menu	=	item.getValue(MenuData::class.java)
                    //binding.textList.append("name:	${menu?.name}:	password${menu?.password}	\n")
                    if (menu != null) {
                        list.add(menu)
                    }
                }
                list.sortByDescending  { it.serving }//내림차순 정렬

                Log.d("Fire1",	"Count:	")
                listAdapter = MenuListAdapterGrid(list)
                listAdapter.setOnItemClickListener(object : MenuListAdapterGrid.OnItemClickListener{
                    override fun onItemClick(v: View, data: MenuData, pos: Int) {//그리드 아이템 click listener
                        //Toast.makeText(v.context, "${data.toString()} Click!", Toast.LENGTH_SHORT).show()

                        var menuCount =0
                        val builder = AlertDialog.Builder(v.context)
                        val mDialogView = LayoutInflater.from(v.context).inflate(R.layout.menudialog, null)//dialog inflater

                        builder
                            .setView(mDialogView)
                            .setTitle("Title")
                            .setPositiveButton("Start",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // Start 버튼 선택 시 수행
                                    if(menuCount>=1) {
                                        cart.add(MenuData(data))
                                        cart.last().select_count = menuCount
                                        refreshCart(cartManager, cartAdapter)
                                    }

                                })
                            .setNegativeButton("Cancel",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // Cancel 버튼 선택 시 수행
                                })
// Create the AlertDialog object and return it
                        builder.create()//dialog 생성
                        val mAlertDialog=builder.show()

                        mDialogView.priceView.setText(data.price.toString()+" 원")
                        Glide.with(mDialogView)
                            .load(list[pos].imageURL) // 불러올 이미지 url
                            .placeholder(R.drawable.ic_launcher_background) // 이미지 로딩 시작하기 전 표시할 이미지
                            .error(R.drawable.rabbit) // 로딩 에러 발생 시 표시할 이미지
                            .fallback(R.drawable.cat) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                            .into(mDialogView.dialogueImage) // 이미지를 넣을 뷰
                        mDialogView.MenuNumberMonitor.setText(menuCount.toString())//dialog 메뉴 개수 출력
                        mDialogView.backToMenu.setOnClickListener { mAlertDialog.dismiss()}//dialog 메뉴 뒤로가기
                        mDialogView.ButtonMinus.setOnClickListener {//dialog 메뉴 개수 감소
                            if(menuCount>0) {
                                menuCount -= 1
                                mDialogView.MenuNumberMonitor.setText(menuCount.toString())
                            }
                        }
                        mDialogView.ButtonPlus.setOnClickListener {//dialog 메뉴 개수 증가
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