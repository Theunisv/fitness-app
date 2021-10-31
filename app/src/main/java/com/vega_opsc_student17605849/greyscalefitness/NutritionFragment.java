package com.vega_opsc_student17605849.greyscalefitness;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.NutritionRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NutritionFragment extends Fragment {
    public static final int CAMERA_REQUEST = 1888;
    public static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ImageView targetImageView;
    private String mealImageTaken;
    public NutritionModel currentNutritionalModel;
    final  private UserRepository userRepository = UserRepository.getInstance();
    public View currView;


    public static NutritionFragment newInstance(String name, int id) {
        NutritionFragment nutritionFragment = new NutritionFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        nutritionFragment.setArguments(args);

        return nutritionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(
                R.layout.fragment_nutirtion, container, false);
        currView = view;
        final NutritionRepository nutritionRepository = NutritionRepository.getInstance();
        final GoalsRepository goalsRepository = GoalsRepository.getInstance();

        Log.d("Username", userRepository.getCurrUserModel() + " logged in successfully");
        //nutritionRepository.addNutritionEntry(UserRepository.getInstance().getCurrUserName(),currentNutritionalModel);

        try {
            UserModel activeUser = userRepository.getCurrUserModel();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Views/displays in fragment
        final Button datePickerBtn = view.findViewById(R.id.btn_nutrition_datepicker);
        final DatePickerDialog[] picker = new DatePickerDialog[1];
        final String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        final Calendar selectedDate = Calendar.getInstance();
        final SimpleDateFormat dbformat = new SimpleDateFormat("yyyy.MM.dd");
        final String dateDBFormatted = dbformat.format(selectedDate.getTime());

        //nutritionRepository.initUserNutrition(userRepository.getCurrUserModel().getUserName());

        //nutritionRepository.getSpecificDayNutrition(selectedDate.toString());

        final TextView breakfastAdd = view.findViewById(R.id.txt_nutrition_breakfast_calories);
        final TextView lunchAdd = view.findViewById(R.id.txt_nutrition_lunch_calories);
        final TextView dinnerAdd = view.findViewById(R.id.txt_nutrition_dinner_calories);
        final TextView snackAdd = view.findViewById(R.id.txt_nutrition_snack_calories);





        datePickerBtn.setText(String.format("Today - %d %s", selectedDate.get(Calendar.DAY_OF_MONTH), months[selectedDate.get(Calendar.MONTH)]));
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker[0] = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                datePickerBtn.setText(dayOfMonth + " " +  months[monthOfYear] + " " + year);
                                selectedDate.set(year,monthOfYear,dayOfMonth);

                                final SimpleDateFormat dbformat = new SimpleDateFormat("yyyy.MM.dd");
                                final String dateDBFormatted = dbformat.format(selectedDate.getTime());
                                currentNutritionalModel = nutritionRepository.getSpecificDayNutrition(userRepository.getCurrUserModel().getUserName(),dateDBFormatted);
                                getData(view);

                            }
                        }, year, month, day);

                picker[0].show();
            }
        });

        final AlertDialog.Builder breakfastBuilder = new AlertDialog.Builder(getActivity());
        breakfastBuilder.setTitle("Calorie amount:");

        final EditText breakfastinput = new EditText(getActivity());
        breakfastinput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        breakfastBuilder.setView(breakfastinput);

        breakfastBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (breakfastinput.getText().toString().trim().isEmpty() || Integer.parseInt(breakfastinput.getText().toString()) < 0) {
                    Toast.makeText(getActivity(), "Please ensure amount is positive.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!breakfastAdd.getText().toString().equals("+ADD")) {
                        String[] breakfast = breakfastAdd.getText().toString().split(" ");
                        int total = Integer.parseInt(breakfast[0]);
                        total += Integer.parseInt(breakfastinput.getText().toString());
                        breakfastAdd.setText(Integer.toString(total) + " CAL");

                    } else {
                        breakfastAdd.setText(breakfastinput.getText().toString() + " CAL");
                    }
                    currentNutritionalModel.setBreakfastCalories(currentNutritionalModel.getBreakfastCalories() + Integer.parseInt(breakfastinput.getText().toString()));
                    nutritionRepository.updateNutritionEntry(userRepository.getCurrUserModel().getUserName(),currentNutritionalModel);
                    Toast.makeText(getActivity(), "Meal succesfully added.", Toast.LENGTH_SHORT).show();
                    updateTotal(view);
                }
            }
        });
        breakfastBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        final AlertDialog breakfastAlert = breakfastBuilder.create();

        breakfastAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                breakfastAlert.show();
            }
        });

        final AlertDialog.Builder lunchBuilder = new AlertDialog.Builder(getActivity());
        lunchBuilder.setTitle("Calorie amount:");

        final EditText lunchinput = new EditText(getActivity());
        lunchinput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        lunchBuilder.setView(lunchinput);

        lunchBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (lunchinput.getText().toString().trim().isEmpty() || Integer.parseInt(lunchinput.getText().toString()) < 0 ){
                    Toast.makeText(getActivity(), "Please ensure amount is positive.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!lunchAdd.getText().toString().equals("+ADD")) {
                        String[] lunch = lunchAdd.getText().toString().split(" ");
                        int total = Integer.parseInt(lunch[0]);
                        total += Integer.parseInt(lunchinput.getText().toString());
                        lunchAdd.setText(Integer.toString(total)+ " CAL");
                    } else {
                        lunchAdd.setText(lunchinput.getText().toString() + " CAL");
                    }
                    currentNutritionalModel.setLunchCalories(currentNutritionalModel.getLunchCalories() + Integer.parseInt(lunchinput.getText().toString()));
                    nutritionRepository.updateNutritionEntry(userRepository.getCurrUserModel().getUserName(),currentNutritionalModel);
                    Toast.makeText(getActivity(), "Meal succesfully added.", Toast.LENGTH_SHORT).show();
                    updateTotal(view);
                }
            }
        });
        lunchBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        final AlertDialog lunchAlert = lunchBuilder.create();
        lunchAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lunchAlert.show();
            }
        });
        final AlertDialog.Builder dinnerBuilder = new AlertDialog.Builder(getActivity());
        dinnerBuilder.setTitle("Calorie amount:");

        final EditText dinnerinput = new EditText(getActivity());
        dinnerinput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        dinnerBuilder.setView(dinnerinput);

        dinnerBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dinnerinput.getText().toString().trim().isEmpty() || Integer.parseInt(dinnerinput.getText().toString()) < 0 ){
                    Toast.makeText(getActivity(), "Please ensure amount is positive.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!dinnerAdd.getText().toString().equals("+ADD")) {
                        String[] dinner = dinnerAdd.getText().toString().split(" ");
                        int total = Integer.parseInt(dinner[0]);
                        total += Integer.parseInt(dinnerinput.getText().toString());
                        dinnerAdd.setText(Integer.toString(total)+ " CAL");
                    } else {
                        dinnerAdd.setText(dinnerinput.getText().toString() + " CAL");
                    }
                    currentNutritionalModel.setDinnerCalories(currentNutritionalModel.getDinnerCalories() + Integer.parseInt(dinnerinput.getText().toString()));
                    nutritionRepository.updateNutritionEntry(userRepository.getCurrUserModel().getUserName(),currentNutritionalModel);
                    Toast.makeText(getActivity(), "Meal succesfully added.", Toast.LENGTH_SHORT).show();
                    updateTotal(view);
                }
            }
        });
        dinnerBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        final AlertDialog dinnerAlert = dinnerBuilder.create();
        dinnerAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dinnerAlert.show();
            }
        });

        final AlertDialog.Builder snackbuilder = new AlertDialog.Builder(getActivity());
        snackbuilder.setTitle("Calorie amount:");

        final EditText snackinput = new EditText(getActivity());
        snackinput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        snackbuilder.setView(snackinput);

        snackbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (snackinput.getText().toString().trim().isEmpty() || Integer.parseInt(snackinput.getText().toString()) < 0 ){
                    Toast.makeText(getActivity(), "Please ensure amount is positive.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!snackAdd.getText().toString().equals("+ADD")) {
                        String[] snack = snackAdd.getText().toString().split(" ");
                        int total = Integer.parseInt(snack[0]);
                        total += Integer.parseInt(snackinput.getText().toString());
                        snackAdd.setText(Integer.toString(total) + " CAL");
                    } else {
                        snackAdd.setText(snackinput.getText().toString() + " CAL");
                    }
                    currentNutritionalModel.setSnackCalories(currentNutritionalModel.getSnackCalories() + Integer.parseInt(snackinput.getText().toString()));
                    nutritionRepository.updateNutritionEntry(userRepository.getCurrUserModel().getUserName(),currentNutritionalModel);
                    Toast.makeText(getActivity(), "Meal succesfully added.", Toast.LENGTH_SHORT).show();
                    updateTotal(view);
                }
            }
        });
        snackbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        final AlertDialog snackalert = snackbuilder.create();

        snackAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                snackalert.show();
            }
        });


        final ImageView breakfastImage = view.findViewById(R.id.nutrition_breakfast_picture);
        final ImageView lunchImage = view.findViewById(R.id.nutrition_lunch_picture);
        final ImageView dinnerImage = view.findViewById(R.id.nutrition_dinner_picture);
        final ImageView snackImage = view.findViewById(R.id.nutrition_snack_picture);

        breakfastImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        targetImageView = breakfastImage;
                        mealImageTaken = "breakfast";
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

            }
        });

        lunchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        targetImageView = lunchImage;
                        mealImageTaken = "lunch";
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }


            }
        });

        dinnerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        targetImageView = dinnerImage;
                        mealImageTaken = "dinner";
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

            }
        });


        snackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        targetImageView = snackImage;
                        mealImageTaken = "snack";
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }


            }
        });


        currentNutritionalModel = nutritionRepository.getSpecificDayNutrition(userRepository.getCurrUserModel().getUserName(),dateDBFormatted);
        if (currentNutritionalModel == null){
            currentNutritionalModel = new NutritionModel();
            currentNutritionalModel.init(dateDBFormatted);
        }
        getData(view);
        updateTotal(view);
        return view;
    }
    public Fragment getFragment() {
        return this;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            targetImageView.setImageBitmap(photo);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            final StorageReference photoref = storageRef.child("images/" + userRepository.getCurrUserModel().getUserName() + "/" +currentNutritionalModel.getStrDate() + "/" + mealImageTaken+".jpeg");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] photoData = baos.toByteArray();

            UploadTask uploadTask = photoref.putBytes(photoData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (mealImageTaken.equals("breakfast")){
                        currentNutritionalModel.setBreakfastImagesURI(photoref.getPath());
                        NutritionRepository.getInstance().updateNutritionEntry(userRepository.getCurrUserModel().getUserName(), currentNutritionalModel);
                    }
                    if (mealImageTaken.equals("lunch")){
                        currentNutritionalModel.setLunchImagesURI(photoref.getPath());
                        NutritionRepository.getInstance().updateNutritionEntry(userRepository.getCurrUserModel().getUserName(), currentNutritionalModel);
                    }
                    if (mealImageTaken.equals("dinner")){
                        currentNutritionalModel.setDinnerImagesURI(photoref.getPath());
                        NutritionRepository.getInstance().updateNutritionEntry(userRepository.getCurrUserModel().getUserName(), currentNutritionalModel);
                    }
                    if (mealImageTaken.equals("snack")){
                        currentNutritionalModel.setSnackImagesURI(photoref.getPath());
                        NutritionRepository.getInstance().updateNutritionEntry(userRepository.getCurrUserModel().getUserName(), currentNutritionalModel);
                    }
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });



        }
    }

    public void getData(View view){

        loadImages();

        int recommendedTotalCalories = currentNutritionalModel.getCalorieTarget(); //VALUE TO GET FROM DB
        int breakfastRecommendedCalories = Math.round((float)recommendedTotalCalories*(2f/7f));
        int lunchRecommendedCalories = Math.round((float)recommendedTotalCalories*(2f/7f));
        int dinnerRecommendedCalories = Math.round((float)recommendedTotalCalories*(2f/7f));
        int snackRecommendedCalories = Math.round((float)recommendedTotalCalories*(1f/7f));

        int breakfastCapturedCalories = currentNutritionalModel.getBreakfastCalories(); //Get From DB
        int lunchCapturedCalories =currentNutritionalModel.getLunchCalories(); //Get From DB
        int dinnerCapturedCalories = currentNutritionalModel.getDinnerCalories(); //Get From DB
        int snackCapturedCalories = currentNutritionalModel.getSnackCalories(); //Get From DB

        String breakfastStatus = "Completed";
        String breakfastCalories = String.format("%d CAL", breakfastCapturedCalories);
        String breakfastCaloriesTotal = "";
        if(breakfastCapturedCalories <=0){
            breakfastStatus = "Recommend " + breakfastRecommendedCalories + " CAL";
            breakfastCalories = "+ADD";
        }
        if (breakfastCapturedCalories > 0){
            int breakfastCalorieDifference = breakfastRecommendedCalories - breakfastCapturedCalories;
            if (breakfastCalorieDifference >= 0){
                breakfastCaloriesTotal = breakfastCalorieDifference + " CAL under recommended";
            }
            else{
                breakfastCaloriesTotal = (breakfastCalorieDifference * -1) + " CAL over recommended";
            }
        }

        TextView breakfastStatusTV = currView.findViewById(R.id.txt_nutrition_breakfast_status);
        TextView breakfastCaloriesTV = currView.findViewById(R.id.txt_nutrition_breakfast_calories);
        TextView breakfastRecommendationTV = currView.findViewById(R.id.txt_nutrition_breakfast_calories_recommendation);

        breakfastStatusTV.setText(breakfastStatus);
        breakfastCaloriesTV.setText(breakfastCalories);
        breakfastRecommendationTV.setText(breakfastCaloriesTotal);

        String lunchStatus = "Completed";
        String lunchCalories = String.format("%d CAL", lunchCapturedCalories);
        String lunchCaloriesTotal = "";
        if(lunchCapturedCalories <=0){
            lunchStatus = "Recommend " + lunchRecommendedCalories + " CAL";
            lunchCalories = "+ADD";
        }
        if (lunchCapturedCalories > 0){
            int lunchCalorieDifference = lunchRecommendedCalories - lunchCapturedCalories;
            if (lunchCalorieDifference >= 0){
                lunchCaloriesTotal = lunchCalorieDifference + " CAL under recommended";
            }
            else{
                lunchCaloriesTotal = (lunchCalorieDifference * -1) + " CAL over recommended";
            }
        }

        TextView lunchStatusTV = currView.findViewById(R.id.txt_nutrition_lunch_status);
        TextView lunchCaloriesTV = currView.findViewById(R.id.txt_nutrition_lunch_calories);
        TextView lunchRecommendationTV = currView.findViewById(R.id.txt_nutrition_lunch_calories_recommendation);

        lunchStatusTV.setText(lunchStatus);
        lunchCaloriesTV.setText(lunchCalories);
        lunchRecommendationTV.setText(lunchCaloriesTotal);

        String dinnerStatus = "Completed";
        String dinnerCalories = String.format("%d CAL", dinnerCapturedCalories);

        String dinnerCaloriesTotal = "";
        if(dinnerCapturedCalories <=0){
            dinnerStatus = "Recommend " + dinnerRecommendedCalories + " CAL";
            dinnerCalories = "+ADD";
        }
        if (dinnerCapturedCalories > 0){
            int dinnerCalorieDifference = dinnerRecommendedCalories - dinnerCapturedCalories;
            if (dinnerCalorieDifference >= 0){
                dinnerCaloriesTotal = dinnerCalorieDifference + " CAL under recommended";
            }
            else{
                dinnerCaloriesTotal = (dinnerCalorieDifference * -1) + " CAL over recommended";
            }
        }

        TextView dinnerStatusTV = currView.findViewById(R.id.txt_nutrition_dinner_status);
        TextView dinnerCaloriesTV = currView.findViewById(R.id.txt_nutrition_dinner_calories);
        TextView dinnerRecommendationTV = currView.findViewById(R.id.txt_nutrition_dinner_calories_recommendation);

        dinnerStatusTV.setText(dinnerStatus);
        dinnerCaloriesTV.setText(dinnerCalories);
        dinnerRecommendationTV.setText(dinnerCaloriesTotal);
        String snackStatus = "Completed";
        String snackCalories = String.format("%d CAL", snackCapturedCalories);

        String snackCaloriesTotal = "";
        if(snackCapturedCalories <=0){
            snackStatus = "Recommend "+ snackRecommendedCalories + " CAL";
            snackCalories = "+ADD";
        }
        if (snackCapturedCalories > 0){
            int snackCalorieDifference = snackRecommendedCalories - snackCapturedCalories;
            if (snackCalorieDifference >= 0){
                snackCaloriesTotal = snackCalorieDifference + " CAL under recommended";
            }
            else{
                snackCaloriesTotal = (snackCalorieDifference * -1) + " CAL over recommended";
            }
        }

        TextView snackStatusTV = currView.findViewById(R.id.txt_nutrition_snack_status);
        TextView snackCaloriesTV = currView.findViewById(R.id.txt_nutrition_snack_calories);
        TextView snackRecommendationTV = currView.findViewById(R.id.txt_nutrition_snack_calories_recommendation);

        snackStatusTV.setText(snackStatus);
        snackCaloriesTV.setText(snackCalories);
        snackRecommendationTV.setText(snackCaloriesTotal);

        updateTotal(view);

    }
    public void loadImages(){
        final ImageView breakfastImage = currView.findViewById(R.id.nutrition_breakfast_picture);
        final ImageView lunchImage = currView.findViewById(R.id.nutrition_lunch_picture);
        final ImageView dinnerImage = currView.findViewById(R.id.nutrition_dinner_picture);
        final ImageView snackImage = currView.findViewById(R.id.nutrition_snack_picture);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        if(!currentNutritionalModel.getBreakfastImagesURI().isEmpty()){
            StorageReference breakfastImageRef = storageRef.child(currentNutritionalModel.getBreakfastImagesURI());

            final long ONE_MEGABYTE = 1024 * 1024;
            breakfastImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    breakfastImage.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        else
        {
            breakfastImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait, null));
        }
        if(!currentNutritionalModel.getLunchImagesURI().isEmpty()){
            StorageReference lunchImageRef = storageRef.child(currentNutritionalModel.getLunchImagesURI());

            final long ONE_MEGABYTE = 1024 * 1024;
            lunchImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    lunchImage.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        else
        {
            lunchImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait, null));
        }
        if(!currentNutritionalModel.getDinnerImagesURI().isEmpty()){
            StorageReference dinnerImageRef = storageRef.child(currentNutritionalModel.getDinnerImagesURI());

            final long ONE_MEGABYTE = 1024 * 1024;
            dinnerImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    dinnerImage.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        else
        {
            dinnerImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait, null));
        }
        if(!currentNutritionalModel.getSnackImagesURI().isEmpty()){
            StorageReference snackImageRef = storageRef.child(currentNutritionalModel.getSnackImagesURI());

            final long ONE_MEGABYTE = 1024 * 1024;
            snackImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    snackImage.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        else
        {
            snackImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait, null));
        }

    }
    public void setData(String meal, int calorieAmount){
        switch (meal){
            case "breakfast":;
                break;
            case"lunch": ;
                break;
            case "dinner":;
                break;
            case"snack": ;
                break;

        }


    }

    public void updateTotal(View view){
        TextView totalCalories = (TextView) currView.findViewById(R.id.txt_nutrition_current_calories);
        TextView breakfastCalories = (TextView) currView.findViewById(R.id.txt_nutrition_breakfast_calories);
        TextView lunchCalories = (TextView) currView.findViewById(R.id.txt_nutrition_lunch_calories);
        TextView dinnerCalories = (TextView)currView.findViewById(R.id.txt_nutrition_dinner_calories);
        TextView snackCalories = (TextView) currView.findViewById(R.id.txt_nutrition_snack_calories);

        TextView breakfastStatus = (TextView) currView.findViewById(R.id.txt_nutrition_breakfast_status);
        TextView breakfastRecommendationText = (TextView) currView.findViewById(R.id.txt_nutrition_breakfast_calories_recommendation);
        TextView lunchStatus = (TextView) currView.findViewById(R.id.txt_nutrition_lunch_status);
        TextView lunchRecommendationText = (TextView) currView.findViewById(R.id.txt_nutrition_lunch_calories_recommendation);
        TextView dinnerStatus = (TextView) currView.findViewById(R.id.txt_nutrition_dinner_status);
        TextView dinnerRecommendationText = (TextView) currView.findViewById(R.id.txt_nutrition_dinner_calories_recommendation);
        TextView snackStatus = (TextView) currView.findViewById(R.id.txt_nutrition_snack_status);
        TextView snackRecommendationText = (TextView) currView.findViewById(R.id.txt_nutrition_snack_calories_recommendation);

        ProgressBar calorieProgress = (ProgressBar) currView.findViewById(R.id.nutrition_calorie_progressbar);
        TextView dailyCalories = (TextView) currView.findViewById(R.id.txt_nutrition_total_calories);

        int totalCaloriesCount = 0;
        int recommendedTotalCalories = currentNutritionalModel.getCalorieTarget(); //VALUE TO GET FROM DB
        int recommendedBreakfastCalories = Math.round((float)recommendedTotalCalories*(2f/7f)); //Get From DB
        int recommendedLunchCalories = Math.round((float)recommendedTotalCalories*(2f/7f)); //Get From DB
        int recommendedDinnerCalories = Math.round((float)recommendedTotalCalories*(2f/7f)); //Get From DB
        int recommendedSnackCalories = Math.round((float)recommendedTotalCalories*(1f/7f)); //Get From DB

        if (!breakfastCalories.getText().toString().trim().equals("+ADD")){
            String[] breakfast = breakfastCalories.getText().toString().split(" ");
            int breakfastCaloriesInt = Integer.parseInt(breakfast[0]);
            totalCaloriesCount += breakfastCaloriesInt;
            breakfastStatus.setText("Completed");
            if (breakfastCaloriesInt > recommendedBreakfastCalories) breakfastRecommendationText.setText(String.format("%d CAL over target.", breakfastCaloriesInt - recommendedBreakfastCalories));
            else breakfastRecommendationText.setText(String.format("%d CAL under target.",  recommendedBreakfastCalories - breakfastCaloriesInt));
        }
        if (!lunchCalories.getText().toString().trim().equals("+ADD")){
            String[] lunch = lunchCalories.getText().toString().split(" ");
            int lunchCaloriesInt = Integer.parseInt(lunch[0]);
            totalCaloriesCount += lunchCaloriesInt;
            lunchStatus.setText("Completed");
            if (lunchCaloriesInt > recommendedLunchCalories) lunchRecommendationText.setText(String.format("%d CAL over target.", lunchCaloriesInt - recommendedLunchCalories));
            else lunchRecommendationText.setText(String.format("%d CAL under target.",  recommendedLunchCalories - lunchCaloriesInt));
        }
        if (!dinnerCalories.getText().toString().trim().equals("+ADD")){
            String[] dinner = dinnerCalories.getText().toString().split(" ");
            int dinnerCaloriesInt = Integer.parseInt(dinner[0]);
            totalCaloriesCount += dinnerCaloriesInt;
            dinnerStatus.setText("Completed");
            if (dinnerCaloriesInt > recommendedDinnerCalories) dinnerRecommendationText.setText(String.format("%d CAL over target.", dinnerCaloriesInt - recommendedDinnerCalories));
            else dinnerRecommendationText.setText(String.format("%d CAL under target.",  recommendedDinnerCalories - dinnerCaloriesInt));
        }
        if (!snackCalories.getText().toString().trim().equals("+ADD")){
            String[] snack = snackCalories.getText().toString().split(" ");
            int snackCaloriesInt = Integer.parseInt(snack[0]);
            totalCaloriesCount += snackCaloriesInt;
            snackStatus.setText("Completed");
            if (snackCaloriesInt > recommendedSnackCalories) snackRecommendationText.setText(String.format("%d CAL over target.", snackCaloriesInt - recommendedSnackCalories));
            else snackRecommendationText.setText(String.format("%d CAL under target.",  recommendedSnackCalories - snackCaloriesInt));
        }
        float progressFloat = (float)totalCaloriesCount /((float)recommendedBreakfastCalories + (float)recommendedLunchCalories +(float)recommendedDinnerCalories + (float)recommendedSnackCalories) * 100f;
        calorieProgress.setProgress((int)progressFloat);
        if (recommendedBreakfastCalories + recommendedLunchCalories +recommendedDinnerCalories + recommendedSnackCalories-totalCaloriesCount<0)
        totalCalories.setText(String.format("%d CAL above target",(recommendedBreakfastCalories + recommendedLunchCalories +recommendedDinnerCalories + recommendedSnackCalories-totalCaloriesCount) *-1));
        else
            totalCalories.setText(String.format("%d CAL below target",recommendedBreakfastCalories + recommendedLunchCalories +recommendedDinnerCalories + recommendedSnackCalories-totalCaloriesCount));

        dailyCalories.setText(String.format("%d CAL Total",totalCaloriesCount));

    }


}