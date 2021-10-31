package com.vega_opsc_student17605849.greyscalefitness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vega_opsc_student17605849.greyscalefitness.models.CustomGoalModel;
import com.vega_opsc_student17605849.greyscalefitness.models.GoalsModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment {

    View currView;

    public static GoalsFragment newInstance(String name, int id) {
        GoalsFragment goalsFragment = new GoalsFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        goalsFragment.setArguments(args);

        return goalsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.fragment_goals, container, false);
        currView = view;


        Button weightButton = view.findViewById(R.id.btn_goals_weightgoals);
        Button fitnessButton = view.findViewById(R.id.btn_goals_fitnessgoals);
        Button trainingButton = view.findViewById(R.id.btn_goals_traininggoals);
        Button waterButton = view.findViewById(R.id.btn_goals_watergoals);
        Button activeButton = view.findViewById(R.id.btn_goals_activegoals);
        Button nutritionButton = view.findViewById(R.id.btn_goals_nutritiongoals);

        TextView weightTextView = view.findViewById(R.id.goals_weightgoal);
        final TextView fitnessTextView = view.findViewById(R.id.goals_fitnessgoal);
        final TextView trainingTextView = view.findViewById(R.id.goals_traininggoal);
        final TextView waterTextView = view.findViewById(R.id.goals_watergoal);
        final TextView activeTextView = view.findViewById(R.id.goals_activegoal);
        final TextView nutrientTextView = view.findViewById(R.id.goals_nutritiongoal);

        final UserRepository userRepository = UserRepository.getInstance();
        final GoalsRepository goalsRepository = GoalsRepository.getInstance();
        final GoalsModel goalsModel = goalsRepository.getUserGoals();
        weightTextView.setText(goalsRepository.getUserGoals().getTargetWeight().toString() + " " + userRepository.getCurrUserModel().getWeightMeticPref());
        fitnessTextView.setText(goalsRepository.getUserGoals().getFitnessGoal());
        trainingTextView.setText(goalsRepository.getUserGoals().getTrainingGoal().toString() + " days/week");
        waterTextView.setText(goalsRepository.getUserGoals().getWaterIntakeGoal().toString() + " glasses/day");
        activeTextView.setText(goalsRepository.getUserGoals().getActiveTimeGoal().toString() + " minutes/day");
        nutrientTextView.setText(goalsRepository.getUserGoals().getCalorieIntakeGoal().toString() + " calories/day");

        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeightInfoActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        final String[] fitnessChoices = getResources().getStringArray(R.array.training_goal);

        fitnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(fitnessChoices, 0, null)
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                String result = fitnessChoices[selectedPosition];
                                fitnessTextView.setText(result);
                                goalsModel.setFitnessGoal(result);
                                goalsRepository.addUserGoals(userRepository.getCurrUserModel().getUserName(), goalsModel);
                            }
                        })
                        .show();
            }
        });

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder trainingBuild = new AlertDialog.Builder(getActivity()).setView(input)
                .setTitle("Set your target training days per week")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!input.getText().toString().isEmpty() && Integer.parseInt(input.getText().toString()) <= 7){
                            dialog.dismiss();
                            trainingTextView.setText(input.getText().toString() + " days/week");
                            goalsModel.setTrainingGoal(Integer.parseInt(input.getText().toString()));
                            goalsRepository.addUserGoals(userRepository.getCurrUserModel().getUserName(), goalsModel);
                            input.setText("");
                        }
                        else Toast.makeText(getActivity(), "Training sessions are limited to 7 days a week.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog alert = trainingBuild.create();;

        trainingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    alert.show();
            }
        });

        final EditText waterinput = new EditText(getActivity());
        waterinput.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder waterBuild = new AlertDialog.Builder(getActivity()).setView(waterinput)
                .setTitle("Set your target glasses of water to drink a day")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!waterinput.getText().toString().isEmpty()){
                            dialog.dismiss();
                            waterTextView.setText(waterinput.getText().toString() + " glasses/day");
                            goalsModel.setWaterIntakeGoal(Integer.parseInt(waterinput.getText().toString()));
                            goalsRepository.addUserGoals(userRepository.getCurrUserModel().getUserName(), goalsModel);
                            waterinput.setText("");
                        }
                        else Toast.makeText(getActivity(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog wateralert = waterBuild.create();;

        waterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                wateralert.show();
            }
        });
        final EditText activityinput = new EditText(getActivity());
        activityinput.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder activebuild = new AlertDialog.Builder(getActivity()).setView(activityinput)
                .setTitle("Set your target active minutes per day")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!activityinput.getText().toString().isEmpty()){
                            dialog.dismiss();
                            activeTextView.setText(activityinput.getText().toString() + " minutes/day");
                            goalsModel.setActiveTimeGoal(Integer.parseInt(activityinput.getText().toString()));
                            goalsRepository.addUserGoals(userRepository.getCurrUserModel().getUserName(), goalsModel);
                            activityinput.setText("");
                        }
                        else Toast.makeText(getActivity(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog activealert = activebuild.create();

        activeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activealert.show();
            }
        });

        final EditText calorieInput = new EditText(getActivity());
        calorieInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder caloriebuild = new AlertDialog.Builder(getActivity()).setView(calorieInput)
                .setTitle("Set your target calories per day")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!calorieInput.getText().toString().isEmpty()){
                            dialog.dismiss();
                            nutrientTextView.setText(calorieInput.getText().toString() + " calories/day");
                            goalsModel.setCalorieIntakeGoal(Integer.parseInt(calorieInput.getText().toString()));
                            goalsRepository.addUserGoals(userRepository.getCurrUserModel().getUserName(), goalsModel);

                            userRepository.getCurrUserModel().setTargetCalories(Integer.parseInt(calorieInput.getText().toString()));
                            userRepository.updateCurrentUserInfo(userRepository.getCurrUserModel());
                            calorieInput.setText("");
                        }
                        else Toast.makeText(getActivity(), "Please enter a valid number.", Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog caloriealert = caloriebuild.create();

        nutritionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                caloriealert.show();
            }
        });



        LayoutInflater inflater2 = this.getLayoutInflater();
        final View customAlertDialogueViewGoals = inflater2.inflate(R.layout.add_custom_goal_dialog, null, false);



        final TextView goalName = customAlertDialogueViewGoals.findViewById(R.id.customgoal_goalname);
        final TextView goalAmount = customAlertDialogueViewGoals.findViewById(R.id.customgoal_goalamount);
        final TextView goalMeasurement = customAlertDialogueViewGoals.findViewById(R.id.customgoal_goalmeasurement);

        final AlertDialog newCustomGoalAlert = new AlertDialog.Builder(getActivity()).setView(customAlertDialogueViewGoals).setTitle("Add Custom Goal").setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String name = goalName.getText().toString();
                String amount = goalAmount.getText().toString();
                String measurement = goalMeasurement.getText().toString();
                if(!name.isEmpty() && !amount.isEmpty() && !measurement.isEmpty()){
                    AddCustomGoal(name, amount, measurement);
                    GoalsRepository.getInstance().addCustomGoal(UserRepository.getInstance().getCurrUserModel().getUserName(), name.trim(), amount.trim(),measurement.trim());
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(customAlertDialogueViewGoals.getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        }).create();

        Button btnAddActivity = view.findViewById(R.id.fab_goals_addCustom);
        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newCustomGoalAlert.show();
            }
        });

        for (CustomGoalModel goal: GoalsRepository.getInstance().customGoals
             ) {
            AddCustomGoal(goal.getGoalName(), goal.getGoalAmount(), goal.getGoalMeasurement());
        }

        return view;
    }
    public Fragment getFragment() {
        return this;
    }

    public void AddCustomGoal(String goalName, String goalAmount, final String goalMeasurement){
        final LinearLayout goalsLayout = currView.findViewById(R.id.goals_layout);

        CoordinatorLayout.LayoutParams coordinateLayoutParams = new CoordinatorLayout.LayoutParams(LayoutParams.MATCH_PARENT, 50);

       /* CoordinatorLayout goalLayout = new CoordinatorLayout(goalsLayout.getContext());
        coordinateLayoutParams.setMargins(15,15,15,15);
        goalLayout.setBackground(currView.getContext().getDrawable(R.drawable.border));*/

        final CoordinatorLayout goalLayoutv2 = (CoordinatorLayout)getLayoutInflater().inflate(R.layout.customgoallayout, null);
        goalsLayout.addView(goalLayoutv2,  currView.findViewById(R.id.weight_layout).getLayoutParams());

        /*CoordinatorLayout.LayoutParams textParams = new CoordinatorLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.setMargins(20,0,0,0);*/
        /*//TextView customTVName = new TextView(goalsLayout.getContext());
        customTVName.setGravity(Gravity.CENTER_VERTICAL);
        customTVName.setText(goalName);
        customTVName.setTextColor(getResources().getColor(R.color.black_900, null));
        customTVName.setId(currView.generateViewId());*/
        //goalLayout.addView(customTVName, currView.findViewById(R.id.goals_description_text).getLayoutParams());


        final TextView customTVName = (TextView)(goalLayoutv2.findViewById(R.id.goals_name_temp));
        customTVName.setText(goalName);
        customTVName.setId(currView.generateViewId());



        /*textParams = (CoordinatorLayout.LayoutParams) currView.findViewById(R.id.goals_weightgoal).getLayoutParams();
        CoordinatorLayout.LayoutParams newParams = textParams;
        newParams.setMargins(0,0,100,0);
        final TextView customTVAmount = new TextView(goalsLayout.getContext());
        customTVAmount.setGravity(Gravity.CENTER | Gravity.RIGHT);
        customTVAmount.setText(goalAmount + " " + goalMeasurement);
        customTVAmount.setId(currView.generateViewId());
        //goalLayout.addView(customTVAmount, currView.findViewById(R.id.goals_weightgoal).getLayoutParams());
        goalLayout.addView(customTVAmount,newParams);*/

        final TextView customTVAmount = (TextView)(goalLayoutv2.findViewById(R.id.goals_layout_value_temp));
        customTVAmount.setText(goalAmount + " " + goalMeasurement);
        customTVAmount.setId(currView.generateViewId());

        //LayoutParams buttonParams = new LayoutParams(40, LayoutParams.WRAP_CONTENT);
        //ContextThemeWrapper newContext = new ContextThemeWrapper(goalsLayout.getContext(),  R.style.SmallEditIconButton);
        //buttonParams.setMargins(0,0,20,0);
        //Button customModifyButton = new Button(newContext, null, R.style.SmallEditIconButton);
        Button customEditButton = (Button)goalLayoutv2.findViewById(R.id.btn_goals_edit);
        customEditButton.setId(currView.generateViewId());

        final AlertDialog.Builder builder = new AlertDialog.Builder(currView.getContext());
        builder.setTitle("Set goal:");

        final EditText input = new EditText(currView.getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!input.getText().toString().trim().isEmpty()) {
                    TextView targetView = (TextView) currView.findViewById(customTVAmount.getId());
                    targetView.setText(input.getText().toString() + " " + goalMeasurement);
                    TextView targetName = currView.findViewById(customTVName.getId());
                    GoalsRepository.getInstance().updateCustomGoal(UserRepository.getInstance().getCurrUserModel().getUserName(), targetName.getText().toString(), input.getText().toString().trim());
                }
                else
                    Toast.makeText(currView.getContext(), "Please enter a value", Toast.LENGTH_SHORT);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog editAlert = builder.create();


        customEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                editAlert.show();
            }
        });

        Button customRemoveButton = (Button)goalLayoutv2.findViewById(R.id.btn_goals_remove);
        customRemoveButton.setId(currView.generateViewId());

        final AlertDialog.Builder builderRemove = new AlertDialog.Builder(currView.getContext());
        builderRemove.setTitle("Removing Goal");
        builderRemove.setMessage("Are you sure you want to remove this goal?");
        builderRemove.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builderRemove.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goalsLayout.removeView(goalLayoutv2);
                RemoveGoal(customTVName.getText().toString());
        }
        });

        final AlertDialog deleteAlert = builderRemove.create();


        customRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert.show();
            }
        });

        goalsLayout.invalidate();
    }


    public void RemoveGoal(String goalName){
        GoalsRepository goalsRepository = GoalsRepository.getInstance();
        goalsRepository.RemoveGoal(UserRepository.getInstance().getCurrUserModel().getUserName(), goalName);
    }


}