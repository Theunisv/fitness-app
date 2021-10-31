package com.vega_opsc_student17605849.greyscalefitness.repositories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vega_opsc_student17605849.greyscalefitness.MainActivity;
import com.vega_opsc_student17605849.greyscalefitness.R;
import com.vega_opsc_student17605849.greyscalefitness.WelcomeScreen;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.core.content.ContextCompat.startActivity;


public class UserRepository {

    private static UserRepository INSTANCE = null;

    private UserRepository(){}

    public static UserRepository getInstance()  {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return(INSTANCE);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> userNames = new ArrayList<>();
    private String userEmail;
    private String currUserName;
    private UserModel currUserModel;
    private ArrayList<UserModel> users = new ArrayList<>();
    private MutableLiveData<ArrayList<UserModel>> dbUsers = new MutableLiveData<>();
    private FirebaseUser mAuthUser;
    private Bitmap userProfilePicture;

    public String UserEmail(){
        return userEmail;
    }
    public void setCurrUserModel(UserModel currUserModel){
        this.currUserModel = currUserModel;
    }
    public UserModel getCurrUserModel(){return currUserModel;}

    public Bitmap getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(Bitmap userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public FirebaseUser getmAuthUser() {
        return mAuthUser;
    }
    public String getCurrUserName(){return currUserName;}

    public void setmAuth(FirebaseUser mAuthUser) {
        this.mAuthUser = mAuthUser;
    }

    public void updateCurrentUserInfo(UserModel newModel){
        currUserModel = newModel;
        db.collection("users").document(newModel.getUserName()).set(newModel);
    }

    public void getProfilePicture(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference breakfastImageRef = storageRef.child(currUserModel.getImageUrl());

        final long ONE_MEGABYTE = 1024 * 1024;
        breakfastImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                userProfilePicture = bmp;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void writeUserToDB(UserModel newUser) {
    Map<String, Object> user = new HashMap<>();
    user.put("first_name", newUser.getFirstName());
    user.put("last_name", newUser.getLastName());
    user.put("birth_date", newUser.getDob());
    user.put("height", newUser.getHeight());
    user.put("weight", newUser.getWeight());
    user.put("fitness_level", newUser.getFitnessLevel());
    user.put("exercise_frequency", newUser.getExerciseFrequency());
    user.put("fitness_goal", Arrays.asList(newUser.getFitnessGoals()));
    user.put("daily_activity", newUser.getDailyActivity());
    user.put("username", newUser.getUserName());
    user.put("display_name", newUser.getFirstName() + newUser.getLastName().substring(0,1));
    user.put("target_weight", newUser.getWeight());
    user.put("target_calories", 2000);
    user.put("gender", newUser.getGender());



    db.collection("users").document(newUser.getUserName()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void documentReference) {
                    Log.d("sucess", "DocumentSnapshot added");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("results", "Error adding document", e);
                }
            });
    }

    public List<String> getExistingUsernames(){
         CollectionReference colRef = db.collection("users");
         colRef.get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                             for (QueryDocumentSnapshot document : task.getResult()) {
                                 userNames.add(document.getId());
                                 userNames.add(document.getString("emailAddress"));
                                 Log.d("sucess", document.getId() + " => " + document.getData());
                             }
                         } else {
                             Log.d("fail", "Error getting documents: ", task.getException());
                         }
                     }
                 });
         colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
             @Override
             public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                 if (e != null) {
                     Log.w("failedtolisten", "Listen failed.", e);
                     return;
                 }

                 for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                     if (doc.getId() != null) {
                         userNames.add(doc.getId());
                         userNames.add(doc.getString("emailAddress"));
                     }
                 }
             }
         });

        return userNames;
    }

    public void usernameMatchesEmail(final String username){
        currUserName = username;
        CollectionReference colRef = db.collection("users");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(currUserName)) userEmail = document.getString("emailAddress");
                                Log.d("sucess", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("fail", "Error getting documents: ", task.getException());
                        }
                    }
                });

        Log.d("Found TV ", userEmail + " email address");
        //return userEmail;
    }

    public MutableLiveData<ArrayList<UserModel>> getUsers(){
        MutableLiveData<ArrayList<UserModel>> data = new MutableLiveData<>();
        //getUsersData(data);
        data = dbUsers;
        return data;
    }

    public ArrayList<UserModel> getUsersData(final ArrayList<UserModel> data) {
        users.clear();
        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        UserModel user = new UserModel();
                        user = document.toObject(UserModel.class);
                        users.add(user);
                        /* data conversion code*/
                        data.add(user);
                        Log.d("useradded", "user : " + user.getUserName());
                }
                //If runs on main thread
               // data.setValue(users);
                    //dbUsers.postValue(users);
                }
            }
        });
        return data;
    }


}


