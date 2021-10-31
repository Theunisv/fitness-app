package com.vega_opsc_student17605849.greyscalefitness.viewmodels;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.auth.User;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

public class UserViewModel extends ViewModel {

/*    private UserRepository userRepo = UserRepository.getInstance();

    public void getUserFromDatabase(AppCompatActivity activity) {
        final TextView fName = activity.findViewById(R.id.firstName);
        final TextView lName = activity.findViewById(R.id.lastName);
        final TextView email = activity.findViewById(R.id.email);

        userRepo.getDocumentFromFirestore("users", "qUiuSLFQXTqYbEf6mxBV")
                .observe(activity, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        fName.setText(user.getFirstName());
                        lName.setText(user.getLastName());
                        email.setText(user.getEmail());
                    }
                });*/
}
