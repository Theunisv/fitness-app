package com.vega_opsc_student17605849.greyscalefitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.data.model.User;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;

public class WelcomeScreen extends AppCompatActivity {
    UserModel newUser = new UserModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Intent intent = getIntent();
        newUser = (UserModel)intent.getSerializableExtra("USER_INFO");
        Button loadIntro = findViewById(R.id.btnloadintro);
        loadIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loadIntroScreen();
            }
        });
        Button loadBasic = findViewById(R.id.btnLoadBasicWelcome);
        loadBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loadBasicScreen();
            }
        });

    }

    private void loadIntroScreen(){
        Intent intent = new Intent(this, IntroScreen.class);
        intent.putExtra("USER_INFO", newUser);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void loadBasicScreen(){
        Intent intent = new Intent(this, BasicInformationSetup.class);
        intent.putExtra("USER_INFO", newUser);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}