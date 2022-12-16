package com.team8.finalsubmission

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.team8.finalsubmission.databinding.ActivityMainpageBinding
import java.util.*

class MainPageActivity: AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainpageBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    private var tts: TextToSpeech? = null
    private val ttsText = "주문을 하려면 주문하기 버튼을 누르거나 화면을 두번 탭하세요."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainpageBinding.inflate(layoutInflater)
        var qrCodeScan = QRCodeScan(this)

        tts = TextToSpeech(this){status ->
            if (status == TextToSpeech.SUCCESS) {
                val speechResult = tts!!.setLanguage(Locale.KOREAN)
                tts!!.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, "start")
                tts!!.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, "silence")
                tts!!.speak(ttsText, TextToSpeech.QUEUE_ADD, null, "start")
            }
        }

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        binding.enterOrderMenu.setOnClickListener {

            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            //버튼을 누르면 tts 종료
            tts!!.stop()
        }
        binding.tutorialButton.setOnClickListener {

            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("tutorialintent",true)

            startActivity(intent)
            //버튼을 누르면 tts 종료
            tts!!.stop()
        }

        binding.recommendMenu.setOnClickListener{
            val intent = Intent(this, RecommendActivity::class.java)
            intent.putExtra("recommendintent",true)

            startActivity(intent)
        }

        binding.qrMenu.setOnClickListener{
            qrCodeScan.startQRScan()
        }

        //더블 클릭시 주문화면으로
        var doubleClick: Boolean? = false
        binding.mainTitle.setOnClickListener {
            if (doubleClick!!) {
                //Toast.makeText(binding.root.context, "double click", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuActivity::class.java)

                startActivity(intent)
                tts!!.stop()
            }
            doubleClick = true
            android.os.Handler().postDelayed({ doubleClick = false }, 200)
        }
    }

    //액티비티가 종료되었을때, tts도 같이 종료
    public override fun onDestroy() {
        // Shutdown TTS when activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}