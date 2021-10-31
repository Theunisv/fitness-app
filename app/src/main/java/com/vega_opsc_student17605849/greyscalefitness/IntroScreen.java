package com.vega_opsc_student17605849.greyscalefitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;

public class IntroScreen extends AppCompatActivity {

    private Button nextButton1;
    private Button nextButton2;
    private Button nextButton3;
    private Button nextButton4;
    private Button loadBasicInfoButton;
    private UserModel newUser = new UserModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen1);

        Intent intent = getIntent();
        newUser = (UserModel)intent.getSerializableExtra("USER_INFO");

        nextButton1 = findViewById(R.id.btnnextintro1);
        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_intro_screen2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                nextButton2 = findViewById(R.id.btnnextintro2);
                nextButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.activity_intro_screen3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        nextButton3 = findViewById(R.id.btnnextintro3);
                        nextButton3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setContentView(R.layout.activity_intro_screen4);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                nextButton4 = findViewById(R.id.btnnextintro4);
                                nextButton4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setContentView(R.layout.activity_intro_screen5);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        loadBasicInfoButton = findViewById(R.id.btnloadbasic);
                                        loadBasicInfoButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), BasicInformationSetup.class);
                                                intent.putExtra("USER_INFO", newUser);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
 /*       nextButton2 = findViewById(R.id.btnnextintro2);
        nextButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_intro_screen3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        nextButton3 = findViewById(R.id.btnnextintro3);
        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_intro_screen4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        nextButton4 = findViewById(R.id.btnnextintro4);
        nextButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_intro_screen5);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        loadBasicInfoButton = findViewById(R.id.btnloadbasic);
        loadBasicInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BasicInformationSetup.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });*/
            }


        });
    }

}