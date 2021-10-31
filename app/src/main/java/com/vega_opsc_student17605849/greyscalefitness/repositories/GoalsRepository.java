package com.vega_opsc_student17605849.greyscalefitness.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vega_opsc_student17605849.greyscalefitness.models.CustomGoalModel;
import com.vega_opsc_student17605849.greyscalefitness.models.GoalsModel;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GoalsRepository {

    private static GoalsRepository INSTANCE = null;

    private GoalsRepository(){}

    public static GoalsRepository getInstance()  {
        if (INSTANCE == null) {
            INSTANCE = new GoalsRepository();
        }
        return(INSTANCE);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private GoalsModel userGoals = new GoalsModel();

    public GoalsModel getUserGoals() {
        return userGoals;
    }

    public List<String> fields = new ArrayList<>();
    public List<String> values = new ArrayList<>();
    public List<CustomGoalModel> customGoals = new ArrayList<>();

    public void setUserGoals(GoalsModel userGoals) {
        this.userGoals = userGoals;
    }

    public void addUserGoals(String userName, GoalsModel goals){
        db.collection("goals").document(userName).set(goals);
        userGoals = goals;
    }

    public void getUserGoals(String userName){
        db.collection("goals").document(userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userGoals = task.getResult().toObject(GoalsModel.class);
            }
        });
    }

    public void initGoal(final String userName){
        final GoalsModel gm = new GoalsModel(userName, 50f, "Lean", 4, 4, 80, 4000);
        db.collection("goals").document(userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()){

                    }
                    else{
                        db.collection("goals").document(userName).set(gm);
                    }
                }
            }
        });

    }

    public void addCustomGoal(final String userName, String goalField, String goalValue, String goalMeasurement){

        Map<String, Object> data = new HashMap<>();
        data.put(goalField, goalValue + "@" + goalMeasurement);
        db.collection("customgoals").document(userName).set(data, SetOptions.merge());
        customGoals.add(new CustomGoalModel(goalField, goalValue, goalMeasurement));

    }

    public void getCustomGoals(String userName){
        customGoals.clear();
        db.collection("customgoals").document(userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {

                        Map<String, Object> map = doc.getData();
                        if(map != null){
                            for (Map.Entry<String, Object> entry : map.entrySet()){
                                String value = entry.getValue().toString();

                                String[] seperated = value.split("@");

                                CustomGoalModel goal = new CustomGoalModel(entry.getKey(), seperated[0], seperated[1]);
                                customGoals.add(goal);
                            }
                        }
                    }
                }
            }
        });

    }

    public void RemoveGoal(String userName, String goalName){

        Map<String, Object> data = new HashMap<>();
        for (CustomGoalModel goal: customGoals
             ) {
            if(goal.getGoalName().equals(goalName)){
                customGoals.remove(goal);
            }
            else{
                data.put(goal.getGoalName(), goal.getGoalAmount() + "@" + goal.getGoalMeasurement());
            }
        }
        db.collection("customgoals").document(userName).set(data);
    }

    public void updateCustomGoal(String userName, String goalName, String newValue){
        Map<String, Object> data = new HashMap<>();
        for (CustomGoalModel goal: customGoals
             ) {
            if(goal.getGoalName().equals(goalName)){
               goal.setGoalAmount(newValue);
                data.put(goal.getGoalName(), goal.getGoalAmount() + "@" + goal.getGoalMeasurement());
            }
            else{
                data.put(goal.getGoalName(), goal.getGoalAmount() + "@" + goal.getGoalMeasurement());
            }
        }
        db.collection("customgoals").document(userName).set(data);

    }

}


