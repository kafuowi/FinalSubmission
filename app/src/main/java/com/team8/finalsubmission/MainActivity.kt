package com.team8.finalsubmission

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.team8.finalsubmission.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        var qrCodeScan = QRCodeScan(this)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        binding.orderEnterButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        binding.addMenuEnterButton.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
        binding.mainpageEnterButton.setOnClickListener {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
        binding.changeNameEnterButton.setOnClickListener {
            val intent = Intent(this, ManageModeChangeNameActivity::class.java)
            startActivity(intent)
        }
        binding.activityManageModeMenuEnterButton.setOnClickListener {
            val intent = Intent(this, ManageModeMenuSelectActivity::class.java)
            startActivity(intent)
        }
        binding.manageModeEnterButton.setOnClickListener {
            val intent = Intent(this, ManageModeMainActivity::class.java)
            startActivity(intent)
        }
        binding.orderTutorialEnterButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("tutorialintent",true)
            startActivity(intent)
        }
        binding.scanQrButton.setOnClickListener{
            qrCodeScan.startQRScan()
        }
    }
}
//test