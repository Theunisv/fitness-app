package com.vega_opsc_student17605849.greyscalefitness;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.NutritionRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static ProfileFragment newInstance(String name, int id) {
        ProfileFragment profileFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        profileFragment.setArguments(args);

        return profileFragment;
    }

    private View currView;
    public static final int CAMERA_REQUEST = 1888;
    public static final int MY_CAMERA_PERMISSION_CODE = 100;
    private UserModel currUser;
    private ImageView targetImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.fragment_profile, container, false);

        currView = rootView;
        UserRepository userRepository = UserRepository.getInstance();
        currUser = userRepository.getCurrUserModel();

        TextView editProfilePicture = currView.findViewById(R.id.profile_edit_profilepicture);
        final TextView weightMetricSetting = currView.findViewById(R.id.preference_weightmetric);
        weightMetricSetting.setText(currUser.getWeightMeticPref());
        final TextView heightMetricSetting = currView.findViewById(R.id.preference_heightmetric);
        heightMetricSetting.setText(currUser.getHeightMetricPref());
        final TextView namePreferenceText = currView.findViewById(R.id.profile_name);
        namePreferenceText.setText(currUser.getFirstName());
        final TextView lastNamePreferenceText = currView.findViewById(R.id.profile_lastname);
        lastNamePreferenceText.setText(currUser.getLastName());
        final TextView heightPreferenceText = currView.findViewById(R.id.profile_height);
        heightPreferenceText.setText(currUser.getHeight().toString() + currUser.getHeightMetricPref());
        if(currUser.getHeightMetricPref().equals("ft")){
            int ft = (int)(currUser.getHeight() / 100);
            int in = (int)(currUser.getHeight() - ((int)(currUser.getHeight() /100)*100));
            heightPreferenceText.setText(ft + " ft " + in + " in");
        }
        else
            heightPreferenceText.setText(currUser.getHeight().toString() + " m");
        final TextView genderPreferenceText = currView.findViewById(R.id.profile_gender);
        genderPreferenceText.setText(currUser.getGender());
        TextView fullnameText = currView.findViewById(R.id.user_profile_fullname);
        fullnameText.setText(currUser.getFirstName() + " " + currUser.getLastName());
        TextView emailText = currView.findViewById(R.id.user_profile_email);
        emailText.setText(currUser.getEmailAddress());
        TextView dobText = currView.findViewById(R.id.user_profile_dob);
        dobText.setText(currUser.getDob());

        Button btnWeightMetric = currView.findViewById(R.id.btn_profile_weightpref);
        Button btnHeightMetric = currView.findViewById(R.id.btn_profile_heightpref);
        Button btnNamePref = currView.findViewById(R.id.btn_profile_name);
        Button btnLastNamePref = currView.findViewById(R.id.btn_profile_lastname);
        Button btnHeightPref = currView.findViewById(R.id.btn_profile_height);
        Button btnGenderPref = currView.findViewById(R.id.btn_profile_gender);
        Button btnResetPassword = currView.findViewById(R.id.btn_profile_resetpassword);

        targetImageView = currView.findViewById(R.id.user_profile_picture);

        if(currUser.getImageUrl()!= null && !currUser.getImageUrl().isEmpty()){
            getProfilePicture();
        }
        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });


        final String[] weightMetricChoices = {"Kilograms", "Pounds"};

        final AlertDialog weightMetricBuilder = new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(weightMetricChoices, 0, null)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        String result = "";
                        if (selectedPosition == 0)
                            result = "kg";
                        if (selectedPosition == 1)
                            result = "lb";
                        weightMetricSetting.setText(result);
                        currUser.setWeightMeticPref(result);
                        UserRepository.getInstance().updateCurrentUserInfo(currUser);
                    }
                }).create();

        btnWeightMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        weightMetricBuilder.show();
            }
        });

        final String[] heighMetricChoices = {"Meters/Centimeters", "Feet/Inches"};

        final AlertDialog heightMetricBuilder = new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(heighMetricChoices, 0, null)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        String result = "";
                        if (selectedPosition == 0){
                            result = "m";

                            heightMetricSetting.setText(result);
                            heightPreferenceText.setText(currUser.getHeight().toString() + " m");
                            currUser.setHeightMetricPref(result);
                            UserRepository.getInstance().updateCurrentUserInfo(currUser);
                        }
                        if (selectedPosition == 1){
                            result = "ft";
                            int ft = (int)(currUser.getHeight() / 100);
                            int in = (int)(currUser.getHeight()- (((int)(currUser.getHeight() /100))*100));
                            heightPreferenceText.setText(ft + " ft " + in + " in");
                        }
                        heightMetricSetting.setText(result);
                        currUser.setHeightMetricPref(result);
                        UserRepository.getInstance().updateCurrentUserInfo(currUser);

                    }
                }).create();
        btnHeightMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        heightMetricBuilder.show();
            }
        });



        final EditText nameInput = new EditText(getActivity());
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        final AlertDialog.Builder nameBuild = new AlertDialog.Builder(getActivity()).setView(nameInput)
                .setTitle("Enter your name")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!nameInput.getText().toString().isEmpty()){
                            dialog.dismiss();
                            namePreferenceText.setText(nameInput.getText().toString());
                            currUser.setFirstName(nameInput.getText().toString());
                            UserRepository.getInstance().updateCurrentUserInfo(currUser);
                            nameInput.setText("");
                        }
                        else Toast.makeText(getActivity(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog nameAlert = nameBuild.create();

        btnNamePref.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nameAlert.show();
            }
        });
        final EditText lastNameInput = new EditText(getActivity());
        lastNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        final AlertDialog.Builder lastNameBuild = new AlertDialog.Builder(getActivity()).setView(lastNameInput)
                .setTitle("Enter your last name")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!lastNameInput.getText().toString().isEmpty()){
                            dialog.dismiss();
                            lastNamePreferenceText.setText(lastNameInput.getText().toString());
                            currUser.setLastName(lastNameInput.getText().toString());
                            UserRepository.getInstance().updateCurrentUserInfo(currUser);
                            lastNameInput.setText("");
                        }
                        else Toast.makeText(getActivity(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog lastNameAlert = lastNameBuild.create();

        btnLastNamePref.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lastNameAlert.show();
            }
        });

        final EditText heightInput = new EditText(getActivity());
        heightInput.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final AlertDialog.Builder heightBuild = new AlertDialog.Builder(getActivity()).setView(heightInput)
                .setTitle("Enter your height")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!heightInput.getText().toString().isEmpty()){
                            dialog.dismiss();
                            heightPreferenceText.setText(heightInput.getText().toString() + " m");
                            currUser.setHeight(Double.parseDouble(heightInput.getText().toString()));
                            UserRepository.getInstance().updateCurrentUserInfo(currUser);
                            heightInput.setText("");
                        }
                        else Toast.makeText(getActivity(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog heightAlert = heightBuild.create();

        LayoutInflater inflater2 = this.getLayoutInflater();
        final View feetInputDialog = inflater2.inflate(R.layout.feet_height_input_dialog, null, false);


        final TextView feetText = feetInputDialog.findViewById(R.id.height_ft);
        final TextView inchesText = feetInputDialog.findViewById(R.id.height_in);
        final AlertDialog ftHeightAlert = new AlertDialog.Builder(getActivity()).setView(feetInputDialog).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Integer feet = Integer.parseInt(feetText.getText().toString());
                Integer inches =  Integer.parseInt(inchesText.getText().toString());
                currUser.setHeight((double) (feet * 100 + inches));
                UserRepository.getInstance().updateCurrentUserInfo(currUser);
                dialog.dismiss();
                heightPreferenceText.setText(feet.toString() + " ft " + inches.toString() + " in");
                feetText.setText("");
                inchesText.setText("");
            }
        }).create();
        btnHeightPref.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(currUser.getHeightMetricPref().equals("ft")) {
                            ftHeightAlert.show();
                }
                else
                heightAlert.show();
            }
        });


        final String[] genderChoices = {"Male", "Female", "I'd rather not say"};

        final AlertDialog genderBuilder = new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(genderChoices, 0, null)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        String result = genderChoices[selectedPosition];
                        genderPreferenceText.setText(result);
                        currUser.setGender(result);
                        UserRepository.getInstance().updateCurrentUserInfo(currUser);
                    }
                }).create();

        btnGenderPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        genderBuilder.show();
            }
        });

        LayoutInflater inflater3 = this.getLayoutInflater();
        final View passwordResetDialog = inflater3.inflate(R.layout.password_update_alert, null, false);


        final TextView oldPassword = passwordResetDialog.findViewById(R.id.password_oldpassword);
        final TextView newPassword = passwordResetDialog.findViewById(R.id.password_newpassword);

        final AlertDialog passwordAlertBulder = new AlertDialog.Builder(getActivity()).setView(passwordResetDialog).setTitle("Change Password").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String opassword = String.valueOf(oldPassword.getText());
                String nPassword = String.valueOf(newPassword.getText());

                dialog.dismiss();
                AuthCredential oldCredentials = EmailAuthProvider.getCredential(currUser.getEmailAddress(), opassword);
                newPassword(oldCredentials, nPassword);
            }
        }).create();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                passwordAlertBulder.show();
            }
        });






        return rootView;
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

            final StorageReference photoref = storageRef.child("images/" + currUser.getUserName() + "/ProfilePicture.jpeg");


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
                   currUser.setImageUrl(photoref.getPath());
                   UserRepository.getInstance().updateCurrentUserInfo(currUser);
                   UserRepository.getInstance().getProfilePicture();
                }
            });



        }
    }

    private void getProfilePicture(){

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

    private void newPassword(AuthCredential oldCredential, final String newCredential){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.reauthenticate(oldCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("success", "Password updated");
                                    } else {
                                        Log.d("error", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("authfail", "Error auth failed");
                        }
                    }
                });

    }

}