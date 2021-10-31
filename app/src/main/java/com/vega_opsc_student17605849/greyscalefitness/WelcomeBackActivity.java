package com.vega_opsc_student17605849.greyscalefitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.NutritionRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WelcomeBackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_back);

        Calendar date = Calendar.getInstance();
        final Date currDate = date.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        NutritionRepository.getInstance().setCurrentUserNutrition(NutritionRepository.getInstance().getSpecificDayNutrition(UserRepository.getInstance().getCurrUserModel().getUserName(), sdf.format(currDate)));
        GoalsRepository.getInstance().getCustomGoals(UserRepository.getInstance().getCurrUserModel().getUserName());

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //Do something after 100ms
            }
        }, 8000);
    }
}