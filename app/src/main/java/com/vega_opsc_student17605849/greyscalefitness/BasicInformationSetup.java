package com.vega_opsc_student17605849.greyscalefitness;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class BasicInformationSetup extends AppCompatActivity {

    String weightMetric = "kg";
    String heightMetric = "m";
    private TextInputEditText height1;
    private TextInputEditText height2;
    private TextInputEditText heightVal1;
    private TextInputEditText heightVal2;
    private TextInputEditText weight1;
    private TextInputEditText weightVal1;
    private Spinner fitnessLevel;
    private Spinner exerciseFrequency;
    private Spinner fitnessGoal;
    private Spinner genderSpin;
    private TextInputEditText dailyActivity;
    private UserModel newUser = new UserModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_information_setup);

        Intent intent = getIntent();
        newUser = (UserModel)intent.getSerializableExtra("USER_INFO");
        Toast.makeText(this,"User info is: " + newUser.getLastName(),Toast.LENGTH_SHORT).show();
        heightVal1 = findViewById(R.id.tvHeightInput1);
        heightVal2 = findViewById(R.id.tvHeightInput2);
        weightVal1 = findViewById(R.id.tvWeightInput);

        Button check = findViewById(R.id.btnLoadDash);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSpaces();
            }
        });
        weight1 = findViewById(R.id.tvWeightMetricKG_LB);
        Switch weightSwitch = (Switch) findViewById(R.id.switchWeightUnit);
        weightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weight1 = findViewById(R.id.tvWeightMetricKG_LB);
                if (isChecked) {
                    weight1.setText("lb");
                    weightMetric = "lb";
                }

                else {
                    weight1.setText("kg");
                    weightMetric = "kg";
                }
            }
        });
        Switch lengthSwitch = (Switch) findViewById(R.id.switchLengthUnit);
        height1 = findViewById(R.id.tvHeight1Unit);
        height2 = findViewById(R.id.tvHeight2Unit);
        lengthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    height1.setText("ft");
                    height2.setText("in");
                    heightMetric = "ft";
                }

                else {
                    height1.setText("m");
                    height2.setText("cm");
                    heightMetric = "m";
                }
            }
        });

        String[] fitnessLevels = { "Beginner", "Intermediate", "Advanced", "Expert" };
        fitnessLevel = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fitnessLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessLevel.setAdapter(adapter);

        String[] exerciseFrequencies = { "1 day per week", "2 days per week", "3 days per week", "4+ days per week" };
        exerciseFrequency = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exerciseFrequencies);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseFrequency.setAdapter(adapter2);

        String[] fitnessGoals = { "Lose weight", "Bulk up", "Improve fitness", "Improve strength" };
        fitnessGoal = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fitnessGoals);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessGoal.setAdapter(adapter3);

        dailyActivity =  findViewById(R.id.tfDailyActivity);
        dailyActivity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    KeyListener defaultKL = dailyActivity.getKeyListener();
                    dailyActivity.setKeyListener(null);
                    popupAlert(defaultKL);
                    dailyActivity.clearFocus();
                }
            }
        });
        String[] genders = { "Male", "Female", "Other"};
        genderSpin = (Spinner) findViewById(R.id.gender);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpin.setAdapter(adapter4);



        /*String[] dailyActivity = { "Sedentary", "Lightly Active", "Moderately Active", "Very Active" };

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dailyActivity);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin4.setAdapter(adapter4);*/
    }

    private void popupAlert(final KeyListener defaultListener){
        final String[] activityLevels = {"Sedentary - little or no exercise", "Lightly Active - some of the day standing/walking", "Moderately Active - on my feet most of the day", "Very Active - hard, physical labour daily"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Your Activity Level");
        builder.setItems(activityLevels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextInputEditText txtDailyActivity =  findViewById(R.id.tfDailyActivity);
                String[] optionSplit = activityLevels[which].split("-");
                txtDailyActivity.setInputType(0);
                txtDailyActivity.setKeyListener(defaultListener);
                txtDailyActivity.setText(optionSplit[0]);
                txtDailyActivity.setTextIsSelectable(false);
                txtDailyActivity.setFocusableInTouchMode(true);
            }
        });
        builder.show();
    }

    private void checkSpaces(){
        if(Objects.requireNonNull(heightVal1.getText()).toString().trim().isEmpty() || Objects.requireNonNull(heightVal2.getText()).toString().trim().isEmpty()
                || weightVal1.getText() == null || fitnessLevel.getSelectedItem() == null
                || exerciseFrequency.getSelectedItem() == null || fitnessGoal.getSelectedItem().toString().trim().isEmpty()
        || dailyActivity.getText().toString().isEmpty() || genderSpin.getSelectedItem().toString().trim().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please fill all the boxes.",
                    Toast.LENGTH_SHORT).show();
        }
        else saveData();

    }

    private void saveData(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {


            double heightVal;
            double weightVal;
            List<String> fitnessGoalVal = new ArrayList<String>();

            heightVal = parseDouble(Objects.requireNonNull(heightVal1.getText()).toString()) + (parseDouble(Objects.requireNonNull(heightVal2.getText()).toString()) / 100);

            weightVal = parseDouble(Objects.requireNonNull(weightVal1.getText().toString()));

            String fitnessLevelVal = fitnessLevel.getSelectedItem().toString();
            String exerciseFrequencyVal = exerciseFrequency.getSelectedItem().toString();
            fitnessGoalVal.add(fitnessGoal.getSelectedItem().toString());
            String dailyActivityVal = dailyActivity.getText().toString();
            String genderVal = genderSpin.getSelectedItem().toString();


            newUser.setHeight(heightVal);
            newUser.setHeightMetricPref(heightMetric);
            newUser.setWeight(weightVal);
            newUser.setWeightMeticPref(weightMetric);
            newUser.setFitnessLevel(fitnessLevelVal);
            newUser.setExerciseFrequency(exerciseFrequencyVal);
            newUser.setFitnessGoals(fitnessGoalVal);
            newUser.setDailyActivity(dailyActivityVal);
            newUser.setGender(genderVal);
            newUser.setDisplayName(newUser.getFirstName() + newUser.getLastName().substring(0,1));
            newUser.setTargetWeight(weightVal);
            newUser.setTargetCalories(2000);

            UserRepository ur = UserRepository.getInstance();
            ur.writeUserToDB(newUser);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            // User is signed in
        } else {
            // No user is signed in
        }
    }
}