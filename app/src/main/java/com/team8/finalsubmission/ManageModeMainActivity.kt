package com.team8.finalsubmission

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.team8.finalsubmission.databinding.ActivitySelectMenuBinding
import kotlinx.android.synthetic.main.activity_select_menu.*
import kotlinx.android.synthetic.main.dialog_add_category.view.*
import kotlinx.android.synthetic.main.dialog_add_menu.view.*
import kotlinx.android.synthetic.main.dialog_manage_mode_menu_selected.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ManageModeMainActivity: AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivitySelectMenuBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    private var mDialogView: View?=null
    private val DialogView get() = mDialogView!!
    private var mDialogViewCategory: View?=null
    private val DialogViewCategory get()=mDialogViewCategory!!



    lateinit var	databaseMenu: DatabaseReference
    lateinit var  databaseCategory: DatabaseReference
    var pickImageFromAlbum = 0
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null
    var	itemCount:	Long	=	0
    var imageUri:Uri?=null
    var imageFile :File? = null

    companion object{
        const val REVIEW_MIN_LENGTH = 10
        // 갤러리 권한 요청
        const val REQ_GALLERY = 1

        // API 호출시 Parameter key값
        const val PARAM_KEY_IMAGE = "image"
        const val PARAM_KEY_PRODUCT_ID = "product_id"
        const val PARAM_KEY_REVIEW = "review_content"
        const val PARAM_KEY_RATING = "rating"
    }
    lateinit var imageGlideView : ImageView
    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
        if(result.resultCode == RESULT_OK){
            // 이미지를 받으면 ImageView에 적용한다
            imageUri = result.data?.data
            imageUri?.let { setUri(it) }
            imageUri?.let{

                // 서버 업로드를 위해 파일 형태로 변환한다
                imageFile = File(getRealPathFromURI(it))

                // 이미지를 불러온다
                Glide.with(this)
                    .load(imageUri)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .into(imageGlideView)


            }

        }
    }


    fun getRealPathFromURI(uri: Uri): String {

        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path!!
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    private fun selectGallery(){
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        //권한 확인
        if(writePermission == PackageManager.PERMISSION_DENIED ||
            readPermission == PackageManager.PERMISSION_DENIED){
            // 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)

        }else{
            // 권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            // intent의 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )

            imageResult.launch(intent)
        }
    }
    fun setUri(U:Uri){
        uriPhoto = U
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectMenuBinding.inflate(layoutInflater)
        binding.returnButton.setText("메뉴추가")

        //카테고리 추가 버튼

        fbStorage = FirebaseStorage.getInstance()
        binding.CategoryAddButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            mDialogViewCategory = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)

            builder
                .setView(DialogViewCategory)
                .setTitle("Title")
                .setPositiveButton("Start",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Start 버튼 선택 시 수행
                        val tempItem :CategoryData = CategoryData()
                        tempItem.name = DialogViewCategory.category_name_edit_text.text.toString()
                        tempItem.UID = DialogViewCategory.category_UID_edit_text.text.toString()
                        databaseCategory.child(tempItem.UID).setValue(tempItem);


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
        databaseCategory	=	Firebase.database.getReference("menucategory")

        var list :ArrayList<MenuData> = ArrayList<MenuData>() //메뉴 데이터 초기화
        var listManager = GridLayoutManager(this, 3)//메뉴 그리드 매니저
        var listAdapter = MenuListAdapterGrid(list)// 메뉴 리스트 어댑터

        refreshMenuGrid(listManager,listAdapter)//메뉴 그리드 새로고침


        var cart: ArrayList<MenuData> =ArrayList<MenuData>()//카트 데이터


        var cartManager = LinearLayoutManager(this)//카트 레이아웃 매니저
        var cartAdapter = MenuListAdapterCart(cart)// 카트 어댑터


        refreshCart(cartManager,cartAdapter)//카트 새로고침

        var categories :ArrayList<CategoryData> = ArrayList<CategoryData>()
        var categoryManager = LinearLayoutManager(this)
        var categoryAdapter = MenuListAdapterCategory(categories)


        var currentCategory =""

        refreshCategory(categoryManager,categoryAdapter)
        databaseCategory.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {//데이터가 바뀔 때


                //binding.textList.setText("")
                Log.d("Category", "Count:	${snapshot.childrenCount}")
                itemCount = snapshot.childrenCount
                categories = ArrayList<CategoryData>()
                for (item in snapshot.children) {//스냅샷의 모든 아이템 조회
                    val key = item.key
                    val category = item.getValue(CategoryData::class.java)
                    //binding.textList.append("name:	${menu?.name}:	password${menu?.password}	\n")
                    if (category != null) {
                        categories.add(category)
                        Log.d("Category", "Name:	${category.name}")
                    }
                }
                categoryAdapter = MenuListAdapterCategory(categories)
                refreshCategory(categoryManager, categoryAdapter)
                categoryAdapter.setOnItemClickListener(object : MenuListAdapterCategory.OnItemClickListener {
                    override fun onItemClick(v: View, data: CategoryData, pos: Int) {//카테고리 click listener
                        Toast.makeText(v.context, "${data.name} Click!", Toast.LENGTH_SHORT)
                            .show()

                        databaseMenu	=	Firebase.database.getReference("menus/${data.name}") //클릭한 카테고리 데이터베이스 가져오기
                        refreshGridData(listManager,listAdapter)// 그리드 새로고침
                        currentCategory = data.name
                    }

                }
                )

                databaseMenu	=	Firebase.database.getReference("menus/${categories[0].name}")//0번째 카테고리 데이터베이스 가져오기

                refreshGridData(listManager,listAdapter)//그리드 새로고침
                currentCategory = categories[0].name


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        )
        binding.returnButton.setOnClickListener { //메뉴추가버튼
            val IMAGE_PICK=1111

            var selectImage: Uri?=null
            val builder = AlertDialog.Builder(this)
            mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu, null)
            DialogView.menu_image_view.setOnClickListener{
                imageGlideView = DialogView.menu_image_view
                selectGallery()// 갤러리에서 사진 선택

            }

            builder
                .setView(DialogView)
                .setTitle("Title")
                .setPositiveButton("Start",
                    DialogInterface.OnClickListener { dialog, id ->
                        val tempItem :MenuData = MenuData()
                        tempItem.name = DialogView.menu_name_edit_text.text.toString()
                        tempItem.price =
                            DialogView.menu_price_edit_text.text.toString().toDouble().toInt()
                        tempItem.UID = DialogView.menu_name_edit_text.text.toString()

                        tempItem.quantity=0
                        tempItem.serving=0
                        tempItem.category=currentCategory

                        if (uriPhoto != null) {
                            var fileName =
                                SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                            fbStorage!!.reference.child("image").child(fileName)
                                .putFile(uriPhoto!!)//어디에 업로드할지 지정
                                .addOnSuccessListener {

                                        taskSnapshot -> // 업로드 정보를 담는다
                                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                                        tempItem.imageURL = it.toString()
                                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT)
                                            .show()

                                        databaseMenu.child(tempItem.UID).setValue(tempItem);

                                    }
                                }

                        }

                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Cancel 버튼 선택 시 수행
                    })
// Create the AlertDialog object and return it
            builder.create()
            val mAlertDialog=builder.show()
        }





    }
    fun refreshCart(cartManager : LinearLayoutManager, cartAdapter: MenuListAdapterCart){//카트 새로고침
        var recyclerCart = menuRecyclerCartView.apply{
            setHasFixedSize(true)
            layoutManager = cartManager
            adapter = cartAdapter

        }

    }
    fun refreshMenuGrid(gridManager: GridLayoutManager, gridAdapter: MenuListAdapterGrid){//메뉴 그리드 새로고침
        var recyclerList = menuRecyclerGridView.apply {
            setHasFixedSize(true)
            layoutManager = gridManager
            adapter = gridAdapter
        }
    }
    fun refreshCategory(categoryManager: LinearLayoutManager, categoryAdapter: MenuListAdapterCategory){//카테고리 새로고침
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

                Log.d("Fire1",	"Count:	")
                listAdapter = MenuListAdapterGrid(list)
                listAdapter.setOnItemClickListener(object : MenuListAdapterGrid.OnItemClickListener{
                    override fun onItemClick(v: View, data: MenuData, pos: Int) {//메뉴 아이템 click listener
                        //Toast.makeText(v.context, "${data.toString()} Click!", Toast.LENGTH_SHORT).show()

                        var tempItem = list[pos]
                        val builder = AlertDialog.Builder(v.context)
                        val mDialogView = LayoutInflater.from(v.context).inflate(R.layout.dialog_manage_mode_menu_selected, null)
                        mDialogView.change_price.setOnClickListener {
                            val intent = Intent(v.context, ManageModeChangeNameActivity::class.java)
                            intent.putExtra("menu",data)
                            startActivity(intent)
                        }
                        mDialogView.check_sales.setText("판매량: "+data.serving.toString())
                        builder
                            .setView(mDialogView)
                            .setTitle("Title")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // 확인 버튼 선택 시 수행
                                    if (uriPhoto != null) {
                                        var fileName =
                                            SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                                        fbStorage!!.reference.child("image").child(fileName)
                                            .putFile(uriPhoto!!)//어디에 업로드할지 지정
                                            .addOnSuccessListener {

                                                    taskSnapshot -> // 업로드 정보를 담는다
                                                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                                                    tempItem.imageURL = it.toString()
                                                    Toast.makeText(mDialogView.context, it.toString(), Toast.LENGTH_SHORT)
                                                        .show()

                                                    databaseMenu.child(tempItem.UID).setValue(tempItem);

                                                }
                                            }

                                    }
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

                        mDialogView.change_image_name.setOnClickListener {//이미지 클릭 listener

                            imageGlideView = mDialogView.selected_menu_image//
                            selectGallery()

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
}