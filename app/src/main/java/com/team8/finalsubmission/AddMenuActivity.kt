package com.team8.finalsubmission

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.team8.finalsubmission.databinding.DialogAddMenuBinding

class AddMenuActivity: AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: DialogAddMenuBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogAddMenuBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
    }
}