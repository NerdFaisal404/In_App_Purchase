package com.example.in_app_purchase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn_purchase , btn_subscribe ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    initUI();
    btn_purchase.setOnClickListener(view -> {
        startActivity(new Intent(MainActivity.this,PurchaseActivity.class));
    });

    btn_subscribe.setOnClickListener(view -> {
        startActivity(new Intent(MainActivity.this,SubscribeActivity.class));
    });
    }

    private void initUI() {
        btn_purchase = findViewById(R.id.btn_purchase) ;
        btn_subscribe = findViewById(R.id.btn_subscribe);
    }
}