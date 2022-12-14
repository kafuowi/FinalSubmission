package com.team8.finalsubmission

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.team8.finalsubmission.databinding.ActivityPaymentSelectBinding

class PaymentSelectActivity : AppCompatActivity() {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityPaymentSelectBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    private val binding get() = mBinding!!

    lateinit var databaseMenu: DatabaseReference //메뉴 데이터베이스
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPaymentSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalprice = intent.getIntExtra("totalprice",0)

        val cart = intent.getSerializableExtra("cart") as ArrayList<MenuData>//

        databaseMenu	=	Firebase.database.getReference("menu")

        binding.totalPriceView2.setText(totalprice.toString()+" 원")
        binding.PaymentButton1.setOnClickListener {
            paymentProcess(cart,totalprice)
        }
        binding.PaymentButton2.setOnClickListener {
            paymentProcess(cart,totalprice)
        }
        binding.PaymentButton3.setOnClickListener {
            paymentProcess(cart,totalprice)
        }
        binding.PaymentButton4.setOnClickListener {
            paymentProcess(cart,totalprice)
        }
    }
    fun paymentProcess(cart: ArrayList<MenuData>,totalprice: Int){
        for(item in cart){

            val curMenu	=	Firebase.database.getReference("menus/${item.category}/${item.UID}")
            var tempItem = MenuData(item)
            tempItem.serving+= tempItem.select_count
            tempItem.select_count=0
            curMenu.setValue(tempItem)
        }
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)


    }
}