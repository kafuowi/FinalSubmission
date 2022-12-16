package com.team8.finalsubmission

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team8.finalsubmission.databinding.ActivityPaymentCheckBinding
import com.team8.finalsubmission.databinding.MenuRecommendBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Thread.sleep

class RecommendActivity: AppCompatActivity()  {
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: MenuRecommendBinding? = null
    lateinit var databaseMenu: DatabaseReference //메뉴 데이터베이스

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private var curNum : Int = 0

    private var curString :String = ""
    private var plusString : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = MenuRecommendBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        binding.ButtonMinus.setOnClickListener{
            curNum = binding.MenuNumberMonitor.text.toString().toInt()
            binding.MenuNumberMonitor.setText((curNum!! - 1).toString())
        }

        binding.ButtonPlus.setOnClickListener{
            curNum = binding.MenuNumberMonitor.text.toString().toInt()
            binding.MenuNumberMonitor.setText((curNum!! + 1).toString())
        }

        binding.ButtonSubmit.setOnClickListener {
            curNum = binding.MenuNumberMonitor.text.toString().toInt()
            while(curNum!! >=4){
                val temp = Firebase.database.getReference("recommend/4").get()
                    .addOnSuccessListener {
                        val recommend = it.getValue(Recommend::class.java)
                        plusString = recommend?.name.toString()
                        Log.d("plus", plusString)
                    }
                curString += plusString
                Log.d("test", curString)
                curNum -= 4
            }
            if(curNum == 3){
                Firebase.database.getReference("recommend/3").get()
                    .addOnSuccessListener {
                        val recommend = it.getValue(Recommend::class.java)
                        curString+= recommend?.name.toString()
                    }
            }
            else if(curNum == 2){
                Firebase.database.getReference("recommend/2").get()
                    .addOnSuccessListener {
                        val recommend = it.getValue(Recommend::class.java)
                        curString+= recommend?.name.toString()
                    }
            }
            else if(curNum == 1){
                Firebase.database.getReference("recommend/1").get()
                    .addOnSuccessListener {
                        val recommend = it.getValue(Recommend::class.java)
                        curString+= recommend?.name.toString()
                    }
            }

            databaseMenu = Firebase.database.getReference("menu")
            var list :ArrayList<MenuData> = ArrayList<MenuData>()
            val result = curString.split("//".toRegex()).toTypedArray()

            CoroutineScope(Dispatchers.IO).launch {
                for (x in 0 until result.count()-1) {
                    val tempCategory = result[x].split(":".toRegex()).toTypedArray()[0]
                    val tempUID = result[x].split(":".toRegex()).toTypedArray()[1]
                    val tempCount = result[x].split(":".toRegex()).toTypedArray()[2]
                    val menuref =
                        Firebase.database.getReference("menus/${tempCategory}/${tempUID}").get()
                            .addOnSuccessListener {


                                val menu = it.getValue(MenuData::class.java)
                                    ?.let { it1 -> MenuData(it1) }

                                if (menu != null) {
                                    Log.d("test", menu.UID)
                                    menu.select_count = tempCount.toInt()
                                    list.add(menu)
                                }


                            }.await()
                }
                val intent = Intent(this@RecommendActivity, PaymentCheckActivity::class.java)
                intent.putExtra("cart",list )
                startActivity(intent, Bundle.EMPTY)
            }
        }
    }


}