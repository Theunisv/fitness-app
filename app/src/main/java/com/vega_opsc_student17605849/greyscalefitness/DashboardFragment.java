package com.vega_opsc_student17605849.greyscalefitness;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vega_opsc_student17605849.greyscalefitness.models.GoalsModel;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.models.WaterIntakeModel;
import com.vega_opsc_student17605849.greyscalefitness.models.WorkoutModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.ExperienceRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.NutritionRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.WorkoutRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class DashboardFragment extends Fragment {

    public static DashboardFragment newInstance(String name, int id) {
        DashboardFragment dashboardFragment = new DashboardFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        dashboardFragment.setArguments(args);


        return dashboardFragment;
    }

    UserModel currUser = new UserModel();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(
                R.layout.fragment_dashboard, container, false);

        //Views/displays in fragment
        ProgressBar dashboardPointsProgress = view.findViewById(R.id.dashboard_gs_points_progressbar);
        String userName = UserRepository.getInstance().getCurrUserModel().getUserName();//Get from DB
        TextView greetingMessage = view.findViewById(R.id.txt_dashboard_greeting);
        TextView pointsMessage = view.findViewById(R.id.txt_dashboard_gs_points);
        TextView currentlevel = view.findViewById(R.id.txt_dashboard_current_level);
        TextView nextlevel = view.findViewById(R.id.txt_dashboard_next_level);
        final TextView waterConsumpText = view.findViewById(R.id.txt_dashboard_waterconsump);
        TextView txtSteps = view.findViewById(R.id.dashboard_steps_amount);
        TextView workoutText = view.findViewById(R.id.txt_dashboard_training);
        final TextView txtCalories = view.findViewById(R.id.dashboard_calories_amount);
        RingProgressBar stepsProgressBar = (RingProgressBar) view.findViewById(R.id.steps_progress_bar);
        final RingProgressBar caloriesProgressBar = (RingProgressBar) view.findViewById(R.id.calories_progress_bar);
        ImageView profilePictureView = view.findViewById(R.id.dashboard_profile_picture);

        //WaterLayout
        final LinearLayout waterLayout = view.findViewById(R.id.waterLinearLayout);
        //WorkoutLayout
        LinearLayout workoutLayout = view.findViewById(R.id.workoutlinearlayout);
        WorkoutRepository workoutRepository = WorkoutRepository.getInstance();
        GoalsRepository goalsRepository = GoalsRepository.getInstance();

        UserRepository userRepository = UserRepository.getInstance();
        currUser = userRepository.getCurrUserModel();

        int currentDashPoints = ExperienceRepository.getInstance().getCurrUserExperienceModel().getExperience(); //GetDashPoints from db
        int maxDashPoints = 100;
        int currentDashLevel = ExperienceRepository.getInstance().getCurrUserExperienceModel().getCurrentLevel(); //GetDashLevel from db
        int nextDashlevel = currentDashLevel + 1;
        greetingMessage.setText(String.format("Hi %s! You're doing great!\nKeep up the good work!",currUser.getFirstName()));
        currentlevel.setText(Integer.toString(currentDashLevel));
        nextlevel.setText(Integer.toString(nextDashlevel));
        pointsMessage.setText(String.format("You are %d GS points away from leveling up!", maxDashPoints - currentDashPoints));

        dashboardPointsProgress.setProgress(currentDashPoints);


        if(currUser.getImageUrl()!= null && !currUser.getImageUrl().isEmpty())
        {
            //profilePictureView.setImageBitmap(userRepository.getUserProfilePicture());
           getProfilePicture(profilePictureView);
        }

        //Water Intake section



        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,0,0,0);




        //Set amount of empty and full glasses based on db data
        Calendar date = Calendar.getInstance();
        final Date currDate = date.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        final WaterIntakeModel[] currWaterintake = {new WaterIntakeModel()};

        final int targetWaterIntake = goalsRepository.getUserGoals().getWaterIntakeGoal(); //Set in db?
        final Integer[] currentWaterIntake = {0}; //Get from db
        db.collection("waterintake").document(currUser.getUserName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    currWaterintake[0] = task.getResult().toObject(WaterIntakeModel.class);

                    if (currWaterintake[0].getCurrentDate().equals(sdf.format(currDate))) currentWaterIntake[0] = currWaterintake[0].getGlassesDrank();
                    else{
                        currWaterintake[0].setCurrentDate(sdf.format(currDate));
                        currWaterintake[0].setGlassesDrank(0);
                        db.collection("waterintake").document(currUser.getUserName()).set(currWaterintake[0]);
                    }
                    Log.d("waterdrank", currentWaterIntake[0] + " glasses");
                }
                else   {
                    currWaterintake[0].setUserName(currUser.getUserName());
                    currWaterintake[0].setCurrentDate(sdf.format(currDate));
                    currWaterintake[0].setGlassesDrank(0);
                    db.collection("waterintake").document(currUser.getUserName()).set(currWaterintake[0]);
                }
                drawDroplets(waterLayout, currentWaterIntake[0],targetWaterIntake,params);
                waterConsumpText.setText(String.format("%d/%d glasses", currentWaterIntake[0],targetWaterIntake));
            }
        });

        Button addGlass = view.findViewById(R.id.btn_dashboard_addglass);

        addGlass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currWaterintake[0].setGlassesDrank(currWaterintake[0].getGlassesDrank()+1);
                currentWaterIntake[0]++;
                db.collection("waterintake").document(currUser.getUserName()).set(currWaterintake[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        drawDroplets(waterLayout, currentWaterIntake[0], targetWaterIntake, params);
                        waterConsumpText.setText(currentWaterIntake[0]+"/"+ targetWaterIntake+" glasses");
                        Toast.makeText(getActivity(),"Clink! Congrats on having another glass!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        NutritionModel todayNutrition = NutritionRepository.getInstance().getCurrentUserNutrition();
        float calorieTarget = GoalsRepository.getInstance().getUserGoals().getCalorieIntakeGoal(); //Pull from db
        float calorieCurrent = todayNutrition.getBreakfastCalories() + todayNutrition.getDinnerCalories() + todayNutrition.getLunchCalories()+ todayNutrition.getSnackCalories(); //Pull from db
        txtCalories.setText(String.format("%d/%d", Math.round(calorieCurrent), Math.round(calorieTarget)));

        int caloriesProgress =Math.round((calorieCurrent / calorieTarget) * 100f);
        caloriesProgressBar.setProgress(caloriesProgress);
        //final  = {new NutritionModel[]};
        db.collection("meals").document(currUser+ sdf.format(currDate)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                NutritionModel todayNutrition= task.getResult().toObject(NutritionModel.class);

                //Calories section

            }
        });



        //Workout section
        int currentWorkoutsComplete = workoutRepository.getSessionsOverLastFiveDays(); //Get actual data from db
        int targetWorkouts = goalsRepository.getUserGoals().getTrainingGoal(); //Get target data from db REPO GOALS
        workoutLayout.removeAllViews();

        for (int i = 0; i < currentWorkoutsComplete; i++){
            ImageView workoutImageComplete = new ImageView(workoutLayout.getContext());
            workoutImageComplete.setLayoutParams(params);
            workoutImageComplete.setImageResource(R.drawable.workout_complete_icon);
            workoutLayout.addView(workoutImageComplete);
        }

        for (int j = 0; j < targetWorkouts - currentWorkoutsComplete; j++){
            ImageView workoutImageIncomplete = new ImageView(workoutLayout.getContext());
            workoutImageIncomplete.setLayoutParams(params);
            workoutImageIncomplete.setImageResource(R.drawable.workout_incomplet_icon);
            workoutLayout.addView(workoutImageIncomplete);
        }
        workoutText.setText(String.format("%d/%d sessions",currentWorkoutsComplete,targetWorkouts));
        //Steps section

        float activityTarget = goalsRepository.getUserGoals().getActiveTimeGoal(); //Pull from db
        List<WorkoutModel> workouts= workoutRepository.getTodayWorkout();
        int activityTimeMins = 0;
        for (WorkoutModel workout: workouts
        ) {
            activityTimeMins += workout.getActivityDuration();
        }
        float activityCurrent = activityTimeMins; //Pull from phone?
        txtSteps.setText(String.format("%d/%d", Math.round(activityCurrent), Math.round(activityTarget)));

        int stepsProgress =Math.round((activityCurrent/activityTarget) * 100f);
        stepsProgressBar.setProgress(stepsProgress);

        view.invalidate();
        return view;
    }
    public Fragment getFragment() {
        return this;
    }

    private void drawDroplets(LinearLayout waterLayout, int currentWaterIntake, int targetWaterIntake, ViewGroup.LayoutParams params){
        waterLayout.removeAllViews();
        Log.d("drawdrank", "You drank " +currentWaterIntake);
        for (int i = 0; i < currentWaterIntake; i++){
            ImageView waterImageFull = new ImageView(waterLayout.getContext());
            waterImageFull.setLayoutParams(params);
            waterImageFull.setImageResource(R.drawable.waterdrop_full);
            waterLayout.addView(waterImageFull);
        }

        for (int j = 0; j < targetWaterIntake - currentWaterIntake; j++){
            ImageView waterImageEmpty = new ImageView(waterLayout.getContext());
            waterImageEmpty.setLayoutParams(params);
            waterImageEmpty.setImageResource(R.drawable.waterdrop_empty);
            waterLayout.addView(waterImageEmpty);
        }
    }
    private void getProfilePicture(final ImageView targetImageView){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference breakfastImageRef = storageRef.child(currUser.getImageUrl());

        final long ONE_MEGABYTE = 1024 * 1024;
        breakfastImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                targetImageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

}