package com.vega_opsc_student17605849.greyscalefitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.ExperienceRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Source;

public class SignUpScreen extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText txtfname;
    private TextInputEditText txtlname;
    private TextInputEditText txtdob;
    private TextInputEditText txtemail;
    private TextInputEditText txtuname;
    private TextInputEditText txtpword;
    private TextInputEditText txtrpword;

    private TextInputLayout imgfname;
    private TextInputLayout imglname;
    private TextInputLayout imgdob;
    private TextInputLayout imgemail;
    private TextInputLayout imguname;
    private TextInputLayout imgpword;
    private TextInputLayout imgrpword;

    private Drawable imgcheckmark;
    private Drawable imgcrossmark;
    private DatePickerDialog picker;
    private Calendar dob;

    private CheckBox signupcheckb;

    private FirebaseAuth mAuth;
    private UserRepository userRepository;

    private List<String> userNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        Button signUpBut = findViewById(R.id.btnSignUpVerify);
        signUpBut.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        userRepository = UserRepository.getInstance();
        userNames = userRepository.getExistingUsernames();

        txtfname = findViewById(R.id.fnameSignUp);
        txtlname = findViewById(R.id.lnameSignUp);
        txtdob = findViewById(R.id.dobSignUp);
        txtemail = findViewById(R.id.emailSignUp);
        txtuname = findViewById(R.id.unameSignUp);
        txtpword = findViewById(R.id.pwordSignUp);
        txtrpword = findViewById(R.id.pwordSignUpConfirm);

        imgfname = findViewById(R.id.layoutfname);
        imglname = findViewById(R.id.layoutlname);
        imgdob = findViewById(R.id.layoutdob);
        imgemail = findViewById(R.id.layoutemail);
        imguname = findViewById(R.id.layoutuname);
        imgpword = findViewById(R.id.layoutpassword);
        imgrpword = findViewById(R.id.layoutrpassword);



        imgcheckmark = getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24,getApplicationContext().getTheme());
        imgcrossmark = getResources().getDrawable(R.drawable.ic_outline_cancel_24,getApplicationContext().getTheme());
        final ColorStateList redColor = ColorStateList.valueOf(getColor(R.color.red_200));
        final ColorStateList greenColor = ColorStateList.valueOf(getColor(R.color.sucess_green));

        signupcheckb = findViewById(R.id.toschecksignup);

        txtfname.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                imgfname.setEndIconDrawable(imgcheckmark);
                imgfname.setEndIconTintList(greenColor);
                if (txtfname.getText() == null || txtfname.getText().toString().trim().isEmpty()){
                    imgfname.setEndIconDrawable(imgcrossmark);
                    imgfname.setEndIconTintList(redColor);
                }
            }
        });

        txtlname.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                imglname.setEndIconDrawable(imgcheckmark);
                imglname.setEndIconTintList(greenColor);
                if (txtlname.getText() == null || txtlname.getText().toString().trim().isEmpty()){
                    imglname.setEndIconDrawable(imgcrossmark);
                    imglname.setEndIconTintList(redColor);
                }
            }
        });
        txtdob.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtdob.setKeyListener(null);
                    launchDatePicker();
                }
            }
        });
        txtdob.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                imgdob.setEndIconDrawable(imgcheckmark);
                imgdob.setEndIconTintList(greenColor);
                if (txtdob.getText() == null || txtdob.getText().toString().trim().isEmpty()){
                    imgdob.setEndIconDrawable(imgcrossmark);
                    imgdob.setEndIconTintList(redColor);
                }
            }
        });

        txtemail.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (isValidEmail(s.toString())) {
                    imgemail.setEndIconDrawable(imgcheckmark);
                    imgemail.setEndIconTintList(greenColor);
                }
                boolean exists = false;
                for (String name : userNames) {
                    if (txtemail.getText().toString().equalsIgnoreCase(name)) exists = true;
                }

                if (txtemail.getText() == null || txtemail.getText().toString().trim().isEmpty() || exists){
                    imgemail.setEndIconDrawable(imgcrossmark);
                    imgemail.setEndIconTintList(redColor);
                }
            }
        });

        txtuname.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                boolean exists = false;
                for (String name : userNames) {
                    if (txtuname.getText().toString().equalsIgnoreCase(name)) exists = true;
                }
                if (!exists){
                    imguname.setEndIconDrawable(imgcheckmark);
                imguname.setEndIconTintList(greenColor);
                }
                else{
                    imguname.setEndIconDrawable(imgcrossmark);
                    imguname.setEndIconTintList(redColor);
                }

                if (txtuname.getText() == null || txtuname.getText().toString().trim().isEmpty()){
                    imguname.setEndIconDrawable(imgcrossmark);
                    imguname.setEndIconTintList(redColor);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });

        txtpword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Password must contain an uppercase letter, a number, and a symbol.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        txtpword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (isValidPassword(s.toString())) {
                    imgpword.setEndIconDrawable(imgcheckmark);
                    imgpword.setEndIconTintList(greenColor);
                }
                else {
                    imgpword.setEndIconDrawable(imgcrossmark);
                    imgpword.setEndIconTintList(redColor);
                }
                }
        });

        txtrpword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (!txtrpword.getText().toString().trim().isEmpty() && txtrpword.getText().toString().equals(txtpword.getText().toString()))
                {
                    imgrpword.setEndIconDrawable(imgcheckmark);
                    imgrpword.setEndIconTintList(greenColor);
                }
                else{
                    imgrpword.setEndIconDrawable(imgcrossmark);
                    imgrpword.setEndIconTintList(redColor);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    @SuppressLint("ShowToast")
    @Override
    public void onClick(View V){
        String email = "empty";
        String username = "empty";
        String password = "empty";
        String rpassword = "nothing";
        String fname = "empty";
        String lname = "empty";
        String dob = "empty";
        final String TAG = "";


    if (txtemail.getText() == null || txtemail.getText().toString().trim().isEmpty() || txtpword.getText() == null || txtpword.getText().toString().trim().isEmpty() || txtrpword.getText() == null || txtrpword.getText().toString().trim().isEmpty() ||
        txtfname.getText() == null || txtfname.getText().toString().trim().isEmpty() || txtlname.getText() == null || txtlname.getText().toString().trim().isEmpty() || txtdob.getText() == null || txtdob.getText().toString().trim().isEmpty() || !signupcheckb.isChecked()){
        Toast.makeText(getBaseContext(), "Please fill in all empty slots and check the T&C's box.", Toast.LENGTH_SHORT).show();

    }
    else {
        fname = txtfname.getText().toString();
        lname = txtlname.getText().toString();
        email = txtemail.getText().toString();
        password = txtpword.getText().toString();
        dob = txtdob.getText().toString();
        username = txtuname.getText().toString();
        rpassword = txtrpword.getText().toString();
         if (!password.equals(rpassword)) {
             Toast.makeText(getBaseContext(), "Passwords do not match." + password +" " + rpassword, Toast.LENGTH_SHORT).show();
        }
         else if (!isValidPassword(password) && password.length() < 8) {

             Toast.makeText(getBaseContext(), "Invalid password. Password must contain an uppercase letter, a number, and a symbol.", Toast.LENGTH_SHORT).show();
         }
         else {
             Log.d("Creating", "Creating user : " + email+ username + password);
            createUser(email, username, password);
        }
    }
    }

    private void createUser(String email, String username, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getBaseContext(), "Creation Succesful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), "Creation Failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signUserIn(String email, String username,String password){

    }

    private void updateUI(FirebaseUser fbuser){
        if(fbuser != null){
            UserModel newUser = new UserModel();
            newUser.setFirstName(txtfname.getText().toString());
            newUser.setLastName(txtlname.getText().toString());
            newUser.setDob(txtdob.getText().toString());
            newUser.setEmailAddress(txtemail.getText().toString());
            newUser.setUserName(txtuname.getText().toString());
            newUser.setImageUrl("");
            ExperienceRepository.getInstance().newUser(newUser.getUserName());

            Intent intent = new Intent(this, WelcomeScreen.class);
            intent.putExtra("USER_INFO", newUser);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

        public static boolean isValidPassword(final String password){
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            return matcher.matches();
        }
    public static boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void launchDatePicker(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(SignUpScreen.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        txtdob.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                    }
                }, year, month, day);

        picker.show();
    }
}