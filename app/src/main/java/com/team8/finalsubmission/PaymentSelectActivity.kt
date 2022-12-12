package com.team8.finalsubmission

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.team8.finalsubmission.databinding.ActivityPaymentSelectBinding

class PaymentSelectActivity : AppCompatActivity() {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityPaymentSelectBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPaymentSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalprice = intent.getIntExtra("totalprice",0)

        binding.totalPriceView2.text = totalprice.toString()+" 원"

    }
}