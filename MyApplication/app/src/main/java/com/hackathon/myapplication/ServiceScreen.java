package com.hackathon.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class ServiceScreen extends AppCompatActivity {
    RadioGroup group,clothgroup,foodgroup;
    int type = 0;
    AppCompatEditText quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = findViewById(R.id.radio_group1);
        clothgroup=findViewById(R.id.radio_group2);
        foodgroup=findViewById(R.id.radio_group);
        quantity=findViewById(R.id.quantity);


        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.radio_food:
                        type = 0;
                        clothgroup.setVisibility(View.GONE);
                        foodgroup.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_books:
                        clothgroup.setVisibility(View.GONE);
                        foodgroup.setVisibility(View.GONE);
                        type = 1;
                        break;
                    case R.id.radio_cloth:
                        type = 2;
                        clothgroup.setVisibility(View.VISIBLE);
                        foodgroup.setVisibility(View.GONE);
                        break;


                }
            }
        });


    }

    void process() {




    }
}
