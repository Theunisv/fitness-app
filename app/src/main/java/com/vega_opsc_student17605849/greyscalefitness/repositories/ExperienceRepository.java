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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vega_opsc_student17605849.greyscalefitness.models.ExperienceModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ExperienceRepository {

    private static ExperienceRepository INSTANCE = null;

    private ExperienceRepository(){}

    public static ExperienceRepository getInstance()  {
        if (INSTANCE == null) {
            INSTANCE = new ExperienceRepository();
        }
        return(INSTANCE);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExperienceModel currUserExperienceModel = new ExperienceModel();

    public ExperienceModel getCurrUserExperienceModel() {
        return currUserExperienceModel;
    }

    public void setCurrUserExperienceModel(ExperienceModel currUserExperienceModel) {
        this.currUserExperienceModel = currUserExperienceModel;
    }

    public void newUser(String userName){
        ExperienceModel newExperience = new ExperienceModel(userName, 1,0);
        db.collection("experience").document(userName).set(newExperience);
        currUserExperienceModel = newExperience;
    }

    public void getExperienceLevel(final String userName){
        db.collection("experience").whereEqualTo("username", userName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                for (QueryDocumentSnapshot doc : task.getResult()
                     ) {
                    currUserExperienceModel = doc.toObject(ExperienceModel.class);
                }
                else
                    newUser(userName);
            }
        });
    }


}


