package com.example.tuan10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Chucnanglaygio extends AppCompatActivity {

    Button btnLayGio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laygio);
        setControl();
        setEvent();
    }

    public void setControl(){
        btnLayGio = findViewById(R.id.btnXacNhan_henGio);


    }
    public void setEvent(){
        btnLayGio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChiTiet.class);
                Bundle bundle = new Bundle();

                startActivity(intent);
            }
        });

    }
}
