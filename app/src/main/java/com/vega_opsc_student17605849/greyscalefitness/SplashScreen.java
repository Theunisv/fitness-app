package com.vega_opsc_student17605849.greyscalefitness;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.TrustedWebUtils;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vega_opsc_student17605849.greyscalefitness.models.ExperienceModel;
import com.vega_opsc_student17605849.greyscalefitness.models.GoalsModel;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.models.WeightInfoModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.ExperienceRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.NutritionRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.WeightRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.WorkoutRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static androidx.core.content.ContextCompat.startActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity implements View.OnClickListener {

    private ScaleAnimation buttonResize =  new ScaleAnimation(1f,1.1f,1f,1.1f);
    private ScaleAnimation resetButton =  new ScaleAnimation(1.1f,1f,1.1f,1f);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ProgressBar ringProgressBar = findViewById(R.id.splash_checking_login_ring);
        ringProgressBar.setVisibility(View.INVISIBLE);




        if (user != null) {
            ringProgressBar.setVisibility(View.VISIBLE);
            ringProgressBar.setIndeterminate(true);
            Toast.makeText(this, "Loading login information.", Toast.LENGTH_SHORT).show();
            // User is signed in.
            UserRepository userRepository = UserRepository.getInstance();
           if(userRepository.getCurrUserName() == null) {
               Intent intent = new Intent(this, MainActivity.class);
               setUserModel(user.getEmail());
            }
        }
        else {

            // No user is signed in.
        }


        Button login = findViewById(R.id.btnloadloginscreen);
        ImageButton twitterSignup = findViewById(R.id.btntwitterlogin);

        ImageButton mailSignup = findViewById(R.id.btnemaillogin);

        ImageButton instaSignup = findViewById(R.id.btninstalogin);

        ImageButton googleSignup = findViewById(R.id.btngooglelogin);

        ImageButton facebookSignup = findViewById(R.id.btnfacebooklogin);
        buttonResize.setDuration(1000);

        login.setOnClickListener(this);
        mailSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                v.startAnimation(buttonResize);
                loadSignUp(v);

            }
        });
        instaSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.startAnimation(buttonResize);

            }
        });
        googleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.startAnimation(buttonResize);
            }
        });
        facebookSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.startAnimation(buttonResize);
            }
        });
        twitterSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.startAnimation(buttonResize);
            }
        });

    }


    private void setUserModel(final String currUserEmail){

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollectionRef = db.collection("users");

        final Query usersQuery = usersCollectionRef.whereEqualTo("emailAddress", currUserEmail);
        usersQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        final UserModel currUser = doc.toObject(UserModel.class);
                        UserRepository.getInstance().setCurrUserModel(currUser);
                        if(currUser.getImageUrl() != null & !currUser.getImageUrl().isEmpty()) UserRepository.getInstance().getProfilePicture();

                        final NutritionRepository nutritionRepository = NutritionRepository.getInstance();
                        nutritionRepository.getUserNutritionalHistory();

                        GoalsRepository.getInstance().initGoal(currUser.getUserName());
                        GoalsRepository.getInstance().getUserGoals(currUser.getUserName());

                        String todayDate = "";
                        Calendar date = Calendar.getInstance();
                        final Date currDate = date.getTime();
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");



                        db.collection("meals").whereEqualTo("strDate", sdf.format(currDate)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().toObjects(NutritionModel.class).isEmpty()){
                                        NutritionModel todayNutrition = new NutritionModel( sdf.format(currDate), 0,0,0,0,"","","","", currUser.getTargetCalories());
                                        nutritionRepository.setCurrentUserNutrition(todayNutrition);
                                        db.collection("meals").document(currUser.getUserName()+sdf.format(currDate)).set(todayNutrition);
                                    }
                                else{
                                        nutritionRepository.setCurrentUserNutrition(task.getResult().toObjects(NutritionModel.class).get(0));
                                    }
                                }
                            }
                        });


                        WorkoutRepository workoutRepository = WorkoutRepository.getInstance();
                        workoutRepository.getActivityTypeList(currUser.getUserName());
                        workoutRepository.getTodayActivities(currUser.getUserName());
                        workoutRepository.getFullhistory(currUser.getUserName());
                        WeightRepository weightInfoRep = WeightRepository.getInstance();
                        weightInfoRep.getFullWeightHistory(currUser.getUserName());


                        final Intent intent = new Intent(getApplicationContext(), WelcomeBackActivity.class);
                        final ExperienceRepository experienceRepository = ExperienceRepository.getInstance();
                        db.collection("experience").document(currUser.getUserName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    experienceRepository.setCurrUserExperienceModel(task.getResult().toObject(ExperienceModel.class));
                                    db.collection("goals").document(currUser.getUserName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            GoalsRepository.getInstance().setUserGoals(task.getResult().toObject(GoalsModel.class));

                                        }
                                    });
                                    db.collection("meals").whereEqualTo("strDate", sdf.format(currDate)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                if (task.getResult().toObjects(NutritionModel.class).isEmpty()){
                                                    NutritionModel todayNutrition = new NutritionModel( sdf.format(currDate), 0,0,0,0,"","","","", currUser.getTargetCalories());
                                                    nutritionRepository.setCurrentUserNutrition(todayNutrition);
                                                    db.collection("meals").document(currUser.getUserName()+sdf.format(currDate)).set(todayNutrition);
                                                }
                                                else{
                                                    nutritionRepository.setCurrentUserNutrition(task.getResult().toObjects(NutritionModel.class).get(0));
                                                }
                                                final Handler handler = new Handler();

                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                        //Do something after 100ms
                                                    }
                                                }, 10000);
                                            }

                                        }
                                    });




                                }
                            }
                        });



                    }
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void loadSignUp(View v){
        Intent intent = new Intent(this, SignUpScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}