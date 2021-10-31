package com.vega_opsc_student17605849.greyscalefitness;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.NutritionRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextInputEditText txtUserName;
    private TextInputEditText txtPassword;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private TranslateAnimation buttonResize =  new TranslateAnimation(1f,2f,1f,2f);
    private AlphaAnimation buttonReset = new AlphaAnimation(0.8F, 1F);
    private ArrayList<UserModel> currentUsers = new ArrayList<>();
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button verifyLogin = findViewById(R.id.btnloginverify);
        verifyLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        txtUserName = findViewById(R.id.txtemaillogin);
        txtUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                txtUserName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

        });
        txtPassword = findViewById(R.id.txtpasswordlogin);
        txtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                txtUserName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

        });
        currentUsers = UserRepository.getInstance().getUsersData(currentUsers);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v){
        final String TAG = "";
        String email = "empty";
        String password = "empty";

        if(txtUserName.getText() != null && !txtUserName.getText().toString().trim().isEmpty()){
            email = txtUserName.getText().toString();
        }
        if(txtPassword.getText() != null && !txtPassword.getText().toString().trim().isEmpty()) {
            password = txtPassword.getText().toString();
        }
        if(email == "empty"){
            txtUserName.setError("Please enter your username/email address.");
             Toast.makeText(getApplicationContext(),"Please enter your username or email.", Toast.LENGTH_SHORT).show();
        }
        else if(password == "empty"){
            txtPassword.setError("Please enter your password.");
            //Toast.makeText(getApplicationContext(),"Please enter your password.", Toast.LENGTH_SHORT).show();
        }
        else {
            tryAuthorize(email, password);
        }
        }

        //v.animate().alpha(1f).setDuration(100);;

    private void tryAuthorize(String email, String password){

        UserRepository userRepository = UserRepository.getInstance();
        Boolean abortAttempt = false;

        if (!email.contains("@")) {
            String userName = email;
            //userArr = userRepository.getUsersData(userArr);

          // Log.d("arr length", Objects.requireNonNull(users.getValue()).toString());

            Log.d("Array length", currentUsers.size() + " array size ");
            for (UserModel u: Objects.requireNonNull(currentUsers)) {
                Log.d("userdetails", u.toString());
                if (u.getUserName() != null){
                if (u.getUserName().equals(email)) email = u.getEmailAddress();
                Log.d("currentemail", u.getUserName() + u.getEmailAddress());
                }
            }
            if(userName.equals(email)){
                txtUserName.setError("Username is incorrect.");
                abortAttempt = true;
            }
        }

        if (!email.trim().isEmpty() && abortAttempt.equals(false)) {
            final String finalEmail = email;
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                for (UserModel u: Objects.requireNonNull(currentUsers)) {
                                    if (u.getUserName() != null){
                                        if (u.getUserName().equals(finalEmail) || u.getEmailAddress().equals(finalEmail))
                                            currentUser = u;
                                    }
                                }
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                txtUserName.setError(task.getException().toString().split(": ")[1]);

                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user){
        if(user != null)
        {
            UserRepository.getInstance().setCurrUserModel(currentUser);
            NutritionRepository nutritionRepository = NutritionRepository.getInstance();
            nutritionRepository.getUserNutritionalHistory();
            Intent intent = new Intent(this, SplashScreen.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }
}