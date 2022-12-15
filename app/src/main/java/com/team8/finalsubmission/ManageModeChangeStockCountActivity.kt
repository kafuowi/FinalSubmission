package com.team8.finalsubmission

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team8.finalsubmission.databinding.ActivityManageModeChangeStockBinding

class ManageModeChangeStockCountActivity: AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityManageModeChangeStockBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    lateinit var databaseMenu: DatabaseReference //메뉴 데이터베이스
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityManageModeChangeStockBinding.inflate(layoutInflater)


        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        val totalprice = intent.getIntExtra("totalprice",0)

        val menu = intent.getParcelableExtra<MenuData>("menu")

        if (menu != null) {
            binding.currentStockTextView.setText( menu.quantity.toString())
            binding.NewStockEditText.setText(menu.quantity.toString())
            binding.NewStockButton.setOnClickListener {
                StockChangeProcess(menu,binding.NewStockEditText.text.toString().toInt())
            }
        }


        databaseMenu	=	Firebase.database.getReference("menu")

    }

    fun StockChangeProcess(menu:MenuData,stock: Int){

        val curMenu	=	Firebase.database.getReference("menus/${menu.category}/${menu.UID}")
        var tempItem = MenuData(menu)
        tempItem.quantity=stock

        this.setResult(RESULT_OK,intent.putExtra("resultData",tempItem));
        finish();


    }
}