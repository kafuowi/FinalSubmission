package com.team8.finalsubmission

import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

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
                var tempUID :String
                for (x in 0 until result.count()-1) {
                    tempUID = result[x].split(":".toRegex()).toTypedArray()[1]
                    Firebase.database.getReference("menus/${tempUID}")

                    Log.d("test", Firebase.database.getReference("menus/${tempUID}").key.toString())

                    //여기부터 구현해야함
                    //cart에 담고 주문 페이지 보이기 구현 해야합니다.
                }
            }else{
                //QRCode Scan result 없는경우
                Toast.makeText(act, "인식된 QR-data가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }else{
            //QRCode Scan 실패
            Toast.makeText(act, "QR스캔에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

    }
}