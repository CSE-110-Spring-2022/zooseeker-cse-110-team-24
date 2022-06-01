package com.example.zooseekerteam24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoadActivity extends AppCompatActivity {
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        et = findViewById(R.id.etLoad);
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        findViewById(R.id.btnSendToDir).setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra("loadjson", et.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        });
    }
}