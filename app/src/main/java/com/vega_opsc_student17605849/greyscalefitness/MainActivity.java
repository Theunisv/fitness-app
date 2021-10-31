package com.vega_opsc_student17605849.greyscalefitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;


public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private BottomNavigationView menuSelectionBarBottom;
    private MenuItem activeMenuItem;
    private LinearLayout viewLayout;

    private static final int PAGES_COUNT = 5;

    //Fragment const names
    public static String DASHBOARD = "Dashboard";
    public static String NUTRITION = "Nutrition";
    public static String WORKOUT = "Workout";
    public static String GOALS = "Goals";
    public static String PROFILE = "Profile";

    //Fragments
    private DashboardFragment fragDashboard = null;
    private NutritionFragment fragNutrition = null;
    private WorkoutFragment fragWorkout = null;
    private GoalsFragment fragGoals = null;
    private ProfileFragment fragProfile = null;

    private String currentFragmentName = "";
    private int newFragID = 0;
    private int oldFragID = 0;
    private UserRepository userRepo;
    private MaterialToolbar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString("HelloKey", "Hello World Initialised");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        userRepo = UserRepository.getInstance();
        viewLayout =  findViewById(R.id.fragment_view);
        topBar = findViewById(R.id.topAppBar);
        topBar.setTitle(currentFragmentName);

        initialiseFragments(savedInstanceState);
        menuSelectionBarBottom = findViewById(R.id.bottom_navigation);

        menuSelectionBarBottom.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_dashboard: showFragment(DASHBOARD);
                    break;

                    case R.id.bottom_nav_nutrition: showFragment(NUTRITION);
                    break;

                    case R.id.bottom_nav_workout: showFragment(WORKOUT);
                    break;

                    case R.id.bottom_nav_goals: showFragment(GOALS);
                    break;

                    case R.id.bottom_nav_profile: showFragment(PROFILE);
                    break;
                }
                topBar.setTitle(currentFragmentName);
                return true;
            }
        });
        showFragment(DASHBOARD);
        topBar.setTitle(DASHBOARD);

        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.menu_item_logout:

                        AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                                        startActivity(intent);
                            }
                    });


                  /*  case R.id.:
                        if (this.getClass().equals(MainActivity.class)){}
                        else{
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.menu_item_weight:
                        if (this.getClass().equals(WeightInfoActivity.class)){}
                        else{
                            Intent intent = new Intent(getApplicationContext(), WeightInfoActivity.class);
                            startActivity(intent);
                        }*/
                }
                return false;
            }
        });
    }

    private void initialiseFragments(Bundle savedInstanceState){

        if (savedInstanceState == null){
            if (fragDashboard == null) fragDashboard = DashboardFragment.newInstance(DASHBOARD, 1);
            if (fragNutrition == null) fragNutrition = NutritionFragment.newInstance(NUTRITION, 2);
            if (fragWorkout == null) fragWorkout = WorkoutFragment.newInstance(WORKOUT, 3);
            if (fragGoals == null) fragGoals = GoalsFragment.newInstance(GOALS, 4);
            if(fragProfile == null) fragProfile = ProfileFragment.newInstance(PROFILE,5);
  /*          if (fragDashboard == null) fragDashboard = DashboardFragment.newInstance(DASHBOARD, 1);
            if (fragDashboard == null) fragDashboard = DashboardFragment.newInstance(DASHBOARD, 1);*/

        }
        else{
            fragDashboard = (DashboardFragment) getSupportFragmentManager().getFragment(savedInstanceState, DASHBOARD);
            fragNutrition = (NutritionFragment) getSupportFragmentManager().getFragment(savedInstanceState, NUTRITION);
            fragWorkout = (WorkoutFragment)getSupportFragmentManager().getFragment(savedInstanceState, WORKOUT);
            fragGoals = (GoalsFragment) getSupportFragmentManager().getFragment(savedInstanceState, GOALS);
            fragProfile = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, PROFILE);

        }
    }

    private void showFragment(String pFragmentName) {
        showFragment(pFragmentName, true);
    }

    private void showFragment(String pFragmentName, boolean addToBackStack) {

        if (currentFragmentName.equals(pFragmentName))
            return; // If this is already the current fragment, do no replace.

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();



        // Then show the fragments
        if (pFragmentName.equals(DASHBOARD)) {
            oldFragID = newFragID;
            newFragID = 1;
            FragmentTransaction as = (newFragID < oldFragID) ? ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right) : ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.fragment_view, getFragDashboard(), DASHBOARD);

        }
        if (pFragmentName.equals(NUTRITION)) {
            oldFragID = newFragID;
            newFragID = 2;
            FragmentTransaction as = (newFragID < oldFragID) ? ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right) : ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.fragment_view, getFragNutrition(), NUTRITION);
        }
        if (pFragmentName.equals(WORKOUT)) {
            oldFragID = newFragID;
            newFragID = 3;
            FragmentTransaction as = (newFragID < oldFragID) ? ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right) : ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.fragment_view, getFragWorkout(), WORKOUT);

        }
        if (pFragmentName.equals(GOALS)) {
            oldFragID = newFragID;
            newFragID = 4;
            FragmentTransaction as = (newFragID < oldFragID) ? ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right) : ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.fragment_view, getFragGoals(), GOALS);

        }
        if (pFragmentName.equals(PROFILE)) {
            oldFragID = newFragID;
            newFragID = 5;
            FragmentTransaction as = (newFragID < oldFragID) ? ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right) : ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.fragment_view, getFragProfile(), PROFILE);

        }
        currentFragmentName = pFragmentName;

        ft.commit();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance

    }

    private DashboardFragment getFragDashboard(){
        if(fragDashboard == null)
            fragDashboard = (DashboardFragment) getSupportFragmentManager().findFragmentByTag(DASHBOARD);
        if (fragDashboard == null)
            fragDashboard = DashboardFragment.newInstance(DASHBOARD,1);

        return fragDashboard;
    }
    private NutritionFragment getFragNutrition(){
        if(fragNutrition == null)
            fragNutrition = (NutritionFragment) getSupportFragmentManager().findFragmentByTag(NUTRITION);
        if (fragNutrition == null)
            fragNutrition = NutritionFragment.newInstance(NUTRITION,2);

        return fragNutrition;
    }
    private WorkoutFragment getFragWorkout(){
        if(fragWorkout == null)
            fragWorkout = (WorkoutFragment) getSupportFragmentManager().findFragmentByTag(WORKOUT);
        if (fragWorkout == null)
            fragWorkout = WorkoutFragment.newInstance(WORKOUT,3);

        return fragWorkout;
    }
    private GoalsFragment getFragGoals(){
        if(fragGoals == null)
            fragGoals = (GoalsFragment) getSupportFragmentManager().findFragmentByTag(GOALS);
        if (fragGoals == null)
            fragGoals = GoalsFragment.newInstance(GOALS,4);

        return fragGoals;
    }
    private ProfileFragment getFragProfile(){
        if(fragProfile == null)
            fragProfile = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(PROFILE);
        if (fragGoals == null)
            fragProfile = ProfileFragment.newInstance(PROFILE,5);

        return fragProfile;
    }
}