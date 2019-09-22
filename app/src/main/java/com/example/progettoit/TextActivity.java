package com.example.progettoit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class TextActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);


        textView = findViewById(R.id.finalTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(getIntent().getStringExtra("txt"));
    }
}
