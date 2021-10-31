package com.vega_opsc_student17605849.greyscalefitness.repositories;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vega_opsc_student17605849.greyscalefitness.NutritionDocument;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class NutritionRepository {

    private static NutritionRepository INSTANCE = null;

    private NutritionRepository(){}

    public static NutritionRepository getInstance()  {
        if (INSTANCE == null) {
            INSTANCE = new NutritionRepository();
        }
        return(INSTANCE);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<NutritionModel> userNutritionHistory = new ArrayList<>();
    NutritionModel currentUserNutrition = new NutritionModel();
    String todayDate = "";

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public void setCurrentUserNutrition(NutritionModel currentUserNutrition) {
        this.currentUserNutrition = currentUserNutrition;
    }

    public NutritionModel getCurrentUserNutrition() {
        return currentUserNutrition;
    }

    //Map
    public void initUserNutrition(final String userName){
        NutritionModel nm = new NutritionModel();
        NutritionModel pm = new NutritionModel();
        nm.init("25.02.2020");
        pm.init("26.02.2020");
        userNutritionHistory.add(nm);
        userNutritionHistory.add(nm);
        Map<String, List<NutritionModel>> data = new HashMap<>();
        data.put("nutritionmodels",userNutritionHistory);
        for (NutritionModel usernutri: userNutritionHistory
             ) {
           // data.put(usernutri.getStrDate(), usernutri);
        }
       // data.put("entries", Arrays.asList(userNutritionHistory));
        db.collection("meals").document(userName + nm.getStrDate()).set(nm);
        db.collection("meals").document(userName + pm.getStrDate()).set(pm);
    }

    public void getTodayNutrition(){
        for (NutritionModel nm: userNutritionHistory
             ) {
            if (nm.getStrDate().equals(currentUserNutrition.getStrDate())){
                currentUserNutrition = nm;
            }
        }
    }
    public void getUserNutritionalHistory(){
        final UserModel currUser = UserRepository.getInstance().getCurrUserModel();
        CollectionReference mealsCollection = db.collection("meals");
        mealsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    userNutritionHistory.clear();
                    for (QueryDocumentSnapshot  doc : task.getResult()) {
                        if (doc.getId().contains(currUser.getUserName())){
                        userNutritionHistory.add(doc.toObject(NutritionModel.class));
                        }
                    }

                    for (NutritionModel entry: userNutritionHistory
                         ) {
                        Log.d("Documentcontent", entry.getStrDate());
                    }

                }
            }
        });

    }

    public NutritionModel getSpecificDayNutrition(final String userName, String date){
        Log.d("arraysize",userNutritionHistory.size() + "elements in array");
        //Boolean
        for (NutritionModel entry: userNutritionHistory
        ) {
            Log.d("entry",entry.getStrDate());
            if (entry.getStrDate().equals(date)){
                Log.d("matchFound","we found a match!");
                return entry;
            }
        }
        NutritionModel newModel = new NutritionModel();
        newModel.init(date);
        newModel.setCalorieTarget(GoalsRepository.getInstance().getUserGoals().getCalorieIntakeGoal());
        addNutritionEntry(userName, newModel);
        return newModel;
    }

    public void addNutritionEntry(final String userName, final NutritionModel newNutritionModel){
        Log.d("addEntry", "Adding model: " + newNutritionModel.getStrDate());

        Boolean entryExists = false;
        GoalsRepository goalsRepository = GoalsRepository.getInstance();
        newNutritionModel.setCalorieTarget(goalsRepository.getUserGoals().getCalorieIntakeGoal());
        for (NutritionModel entry: userNutritionHistory
             ) {
            if (newNutritionModel.getStrDate() == entry.getStrDate()){
                userNutritionHistory.set(userNutritionHistory.indexOf(entry), newNutritionModel);
                Log.d("entry", entry.toString() + "  is in arraylist");
                entryExists = true;
            }
        }
        if(!entryExists){
            userNutritionHistory.add(newNutritionModel);
            if (newNutritionModel.getCalorieTarget() == null) newNutritionModel.setCalorieTarget(goalsRepository.getUserGoals().getCalorieIntakeGoal());
            db.collection("meals").document(userName + newNutritionModel.getStrDate()).set(newNutritionModel);
        }
    }

    public void updateNutritionEntry(String userName, NutritionModel newNutritionModel){
        GoalsRepository goalsRepository = GoalsRepository.getInstance();
        newNutritionModel.setCalorieTarget(goalsRepository.getUserGoals().getCalorieIntakeGoal());
        for (NutritionModel entry: userNutritionHistory
        ) {
            if (newNutritionModel.getStrDate() == entry.getStrDate()){
                userNutritionHistory.set(userNutritionHistory.indexOf(entry), newNutritionModel);
                db.collection("meals").document(userName+newNutritionModel.getStrDate()).set(newNutritionModel);
            }
        }

        //newCityRef.update()

       // db.collection("meals").document(userName).collection("meals").;
    }
}


