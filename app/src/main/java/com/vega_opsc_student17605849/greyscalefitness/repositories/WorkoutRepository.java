package com.vega_opsc_student17605849.greyscalefitness.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.Document;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.models.WorkoutModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class WorkoutRepository {

    private static WorkoutRepository INSTANCE = null;

    private WorkoutRepository(){}

    public static WorkoutRepository getInstance()  {
        if (INSTANCE == null) {
            INSTANCE = new WorkoutRepository();
        }
        return(INSTANCE);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public int userSteps;
    public int totalActivityTime;
    public int workoutSessionsThisWeek;
    private int sessionsOverLastFiveDays = 0;

    public int getSessionsOverLastFiveDays() {
        return sessionsOverLastFiveDays;
    }

    public List<List<String>> activityList = new ArrayList<>();
    public List<WorkoutModel> workoutHistory = new ArrayList<>();
    public List<WorkoutModel> todayWorkout = new ArrayList<>();
    public List<WorkoutModel> getTodayWorkout() {
        return todayWorkout;
    }

    public List<List<String>> getActivityList() {
        return activityList;
    }

    public List<WorkoutModel> getWorkoutHistory() {
        return workoutHistory;
    }

    public boolean addActivityType(String username, String activityName, String estimatedEffort){
        List<String> activityInfo = new ArrayList<>();
        activityInfo.add(activityName);
        activityInfo.add(estimatedEffort);
        Map<String, Object> data = new HashMap<>();
        data.put(activityName, activityInfo);
        final Boolean[] isComplete = {false};

        db.collection("workouts").document(username+".activities").set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                isComplete[0] = true;
            }
        });
        activityList.add(activityInfo);
        return true;
    }

    public void getActivityTypeList(final String username){
        db.collection("workouts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot taskResult: task.getResult()
                         ) {
                        if (taskResult.getId().equals(username + ".activities")){
                            activityList.clear();
                           for(Map.Entry<String, Object> result : taskResult.getData().entrySet()) {
                               List<String> entry = new ArrayList<>();
                               entry = (ArrayList) result.getValue();
                               activityList.add(entry);
                           }
                        }
                    }
                }
            }
        });
    }

    public void getTodayActivities(final String username) {
        final Calendar selectedDate = Calendar.getInstance();
        final SimpleDateFormat dbformat = new SimpleDateFormat("yyyy.MM.dd");
        final String dateDBFormatted = dbformat.format(selectedDate.getTime());

        db.collection("workouts").whereEqualTo("activityDate", dateDBFormatted).whereEqualTo("userName", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot taskResult : task.getResult()
                    ) {
                        todayWorkout.add(taskResult.toObject(WorkoutModel.class));
                    }
                }
            }
        });
    }

    public void addDailyActivity(WorkoutModel workout){
        db.collection("workouts").document().set(workout);
        todayWorkout.add(workout);
        workoutHistory.add(workout);
    }

    public String getActvityEffort(String activityName){
        for (List<String> activity: activityList
             ) {
            if (activity.get(0).equals(activityName)){
                return activity.get(1);
            }
        }
        return "Create More Activities";
    }

    public void getFullhistory(final String username){
        db.collection("workouts").whereEqualTo("userName", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc: task.getResult()
                     ) {
                    workoutHistory.add(doc.toObject(WorkoutModel.class));

                }
                populateLastFiveDays();
            }
        });

    }

    public void populateLastFiveDays(){
        sessionsOverLastFiveDays = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Boolean trainedToday = false;
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String[] days = new String[6];
        days[0] = sdf.format(date);

        for(int i = 1; i< 6; i++){
            cal.add(Calendar.DAY_OF_MONTH,-1);
            date=cal.getTime();
            days[i]=sdf.format(date);
        }

        for (String day: days
             ) {
            trainedToday = false;
            for (WorkoutModel workout: workoutHistory
                 ) {
                if (workout.getActivityDate().equals(day)){
                    trainedToday = true;
                }
            }
            if (trainedToday) sessionsOverLastFiveDays++; //THIS VALUE NEEDS TO BE TAKEN FROM GOALS REPO
        }



    }
}


