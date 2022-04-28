package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    val activityRequestCode = 1

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        val switch = findViewById<Switch>(R.id.switch1)
        textView = findViewById(R.id.textView)

        button.setOnClickListener {
            val intent = Intent(this, FaceIdActivity::class.java)
            intent.putExtra("mode", (if (switch.isChecked) "strong" else "simple"))
            startActivityForResult(intent, activityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == activityRequestCode) {
            if (resultCode == RESULT_OK) {
                textView.setText(data?.getStringExtra("code"));
            } else if (resultCode == RESULT_CANCELED) {
                textView.setText(data?.getStringExtra("error"))
            }
        }
    }
}
