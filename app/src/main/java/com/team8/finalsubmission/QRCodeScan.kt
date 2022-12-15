package com.team8.finalsubmission

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QRCodeScan(private val act: MainActivity) {

    lateinit var databaseMenu: DatabaseReference //메뉴 데이터베이스

    /** QRCode Scan */
    fun startQRScan(){
        val intentIntegrator = IntentIntegrator(act)

        intentIntegrator.setPrompt("안내선 안에 QR코드를 맞추면 자동으로 인식됩니다.") //QR코드 스캔 액티비티 하단에 띄울 텍스트 설정
        intentIntegrator.setOrientationLocked(false)                       //화면회전을 막을 것인지 설정 (default 세로모드)
        intentIntegrator.setBeepEnabled(false)                             //QR코드 스캔 시 소리를 낼 지 설정
        activityResult.launch(intentIntegrator.createScanIntent())
    }

    /** onActivityResult */
    private val activityResult = act.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val data = result.data

        val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, data)
        if(intentResult != null){
            //QRCode Scan 성공
            if(intentResult.contents != null){
                //QRCode Scan result 있는경우
                Toast.makeText(act, "인식된 QR-data: ${intentResult.contents}", Toast.LENGTH_SHORT).show()
                databaseMenu = Firebase.database.getReference("menu")
                var list :ArrayList<MenuData> = ArrayList<MenuData>()
                val result = intentResult.contents.split("//".toRegex()).toTypedArray()

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
                    nextIntent(list)

                }




                    //여기부터 구현해야함
                    //cart에 담고 주문 페이지 보이기 구현 해야합니다.




            }else{
                //QRCode Scan result 없는경우
                Toast.makeText(act, "인식된 QR-data가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }else{
            //QRCode Scan 실패
            Toast.makeText(act, "QR스캔에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

    }
    suspend fun nextIntent(list:ArrayList<MenuData>){
        val intent = Intent(act, PaymentCheckActivity::class.java)
        intent.putExtra("cart", list)
        startActivity(act, intent, Bundle.EMPTY)
    }
}