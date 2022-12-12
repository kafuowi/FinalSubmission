package com.team8.finalsubmission

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.team8.finalsubmission.databinding.ActivityPaymentCheckBinding
import kotlinx.android.synthetic.main.activity_payment_check.*
import kotlinx.android.synthetic.main.activity_select_menu.*

class PaymentCheckActivity: AppCompatActivity() {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityPaymentCheckBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    lateinit var cartAdapter : MenuListAdapterCart
    lateinit var cartManager : LinearLayoutManager
    lateinit var cart: ArrayList<MenuData> // 카트 데이터 배열
    lateinit var cartCount:ArrayList<Int>

    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPaymentCheckBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        cart = intent.getSerializableExtra("cart") as ArrayList<MenuData>
        cartCount = intent.getSerializableExtra("cartCount") as ArrayList<Int>
        binding.returnToMenuButton.setOnClickListener {
            finish()
        }


        cartManager = LinearLayoutManager(this)//장바구니 리스트 매니저
        cartAdapter = MenuListAdapterCart(cart)//장바구니 어댑터 매니저
        refreshCart(cartManager,cartAdapter)
        var totalprice =0
        for(i in cart){
            totalprice+= i.price*i.select_count
        }
        binding.totalPriceView.setText("총 "+totalprice.toString()+" 원")


    }

    fun refreshCart(cartManager :LinearLayoutManager, cartAdapter: MenuListAdapterCart){// 카트 새로고침
        var recyclerCart = paymentCheckListView.apply{
            setHasFixedSize(true)
            layoutManager = cartManager
            adapter = cartAdapter

        }

    }

}