package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    int activityRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch1 = findViewById(R.id.switch1);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FaceIdActivity.class);
                intent.putExtra("mode", (switch1.isChecked()) ? "strong" : "simple");
                startActivityForResult(intent, activityRequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == activityRequestCode) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                textView.setText(data.getStringExtra("code"));
            } else if (resultCode == RESULT_CANCELED) {
                assert data != null;
                textView.setText(data.getStringExtra("error"));
            }
        }
    }
}
