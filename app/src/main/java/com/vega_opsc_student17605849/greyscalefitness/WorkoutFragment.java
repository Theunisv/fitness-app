package com.vega_opsc_student17605849.greyscalefitness;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.vega_opsc_student17605849.greyscalefitness.models.WorkoutModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.WorkoutRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class WorkoutFragment extends Fragment {

    public View currView;
    public static WorkoutFragment newInstance(String name, int id) {
        WorkoutFragment workoutFragment = new WorkoutFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        workoutFragment.setArguments(args);

        return workoutFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(
                R.layout.fragment_workout, container, false);

        final TabLayout workoutTabs = view.findViewById(R.id.tabl_workout);
        final RelativeLayout workoutLayout = view.findViewById(R.id.workout_content_layout);
        currView = view;


        workoutTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabAdapterLoader(tab, view);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                workoutLayout.removeAllViews();
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        drawTodayInfo(view);
        return view;
    }

    public Fragment getFragment() {
        return this;
    }

    public void tabAdapterLoader(TabLayout.Tab selectedTab, View view){
        if(selectedTab.getText().toString().equals("Today")) drawTodayInfo(view);
        if(selectedTab.getText().toString().equals("History")) drawHistoryInfo(view);
        if(selectedTab.getText().toString().equals("Activities")) drawActivitiesInfo(view);
    }

    public void drawTodayInfo(View view){
        RelativeLayout workoutLayout = view.findViewById(R.id.workout_content_layout);
        workoutLayout.removeAllViews();
        workoutLayout.setGravity(1);

        WorkoutRepository workoutRepository = WorkoutRepository.getInstance();
        UserRepository ur = UserRepository.getInstance();

        List<WorkoutModel> workouts= workoutRepository.getTodayWorkout();
        int activityTimeMins = 0; //GET FROM DB
        for (WorkoutModel workout: workouts
             ) {
            activityTimeMins += workout.getActivityDuration();
        }
        //exercise time text view
        TextView excerciseTimeTitle = new TextView(getActivity());
        excerciseTimeTitle.setTextAppearance(R.style.TextAppearance_AppCompat_Display1);
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        excerciseTimeTitle.setText("Total Exercise Time");
        excerciseTimeTitle.setId(R.id.workout_fragment_title);
        workoutLayout.addView(excerciseTimeTitle,textparams);

        //Ring Progress Bar
        int activityTimeGoal = GoalsRepository.getInstance().getUserGoals().getActiveTimeGoal();
        RingProgressBar exerciseTimeProcess = new RingProgressBar(getActivity());
        RelativeLayout.LayoutParams ringprogressparams = new RelativeLayout.LayoutParams(300,300);
        ringprogressparams.addRule(RelativeLayout.BELOW, R.id.workout_fragment_title);
        ringprogressparams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        ringprogressparams.setMargins(0,10,0,10);
        exerciseTimeProcess.setMax(100);
        exerciseTimeProcess.setRingColor(R.color.tw__light_gray);
        exerciseTimeProcess.setRingProgressColor(R.color.background_color);
        exerciseTimeProcess.setRingWidth(40);
        exerciseTimeProcess.setTextSize(0);
        exerciseTimeProcess.setId(R.id.workout_fragment_progressbar);
        exerciseTimeProcess.setProgress(Math.round((float)activityTimeMins/(float)activityTimeGoal*100f));
        workoutLayout.addView(exerciseTimeProcess, ringprogressparams);

        //Ring progress Text
        TextView exerciseProgressText = new TextView(getActivity());
        exerciseProgressText.setTextAppearance(R.style.Widget_AppCompat_TextView);
        textparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textparams.addRule(RelativeLayout.ALIGN_TOP, R.id.workout_fragment_progressbar);
        textparams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.workout_fragment_progressbar);
        exerciseProgressText.setGravity(Gravity.CENTER);
        exerciseProgressText.setText(String.format("%d/%d\nminutes",activityTimeMins, activityTimeGoal));
        workoutLayout.addView(exerciseProgressText, textparams);

        //Activities Table Title
        TextView exerciseTableTitle = new TextView(getActivity());
        exerciseTableTitle.setTextAppearance(R.style.TextAppearance_AppCompat_Display1);
        textparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textparams.addRule(RelativeLayout.BELOW, R.id.workout_fragment_progressbar);
        exerciseTableTitle.setText("Activities Completed");
        exerciseTableTitle.setId(R.id.workout_fragment_table_title);
        workoutLayout.addView(exerciseTableTitle,textparams);

        //Activities Table
        TableLayout activitiesTable = new TableLayout(getActivity());
        RelativeLayout.LayoutParams tableParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        activitiesTable.setStretchAllColumns(true);
        tableParams.setMargins(10,10,10,10);
        tableParams.addRule(RelativeLayout.BELOW,R.id.workout_fragment_table_title);
        activitiesTable.setBackground(getActivity().getDrawable(R.drawable.border));

        //Table Row Titles
        TableRow tableRow = new TableRow(getActivity());
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        int row1ID = tableRow.getId();
        tableRow.setBackground(getActivity().getDrawable(R.drawable.border));

        //Title1
        TextView activityTitle = new TextView(getActivity());
        TableRow.LayoutParams indivRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        activityTitle.setPadding(10,5,0,5);
        activityTitle.setTypeface(null,Typeface.BOLD);
        activityTitle.setText("Activity");
        activityTitle.setBackground(getActivity().getDrawable(R.drawable.border));
        tableRow.addView(activityTitle, indivRowParams);

        //Title2
        TextView burnedTitle = new TextView(getActivity());
        burnedTitle.setPadding(10,5,0,5);
        burnedTitle.setTypeface(null,Typeface.BOLD);
        burnedTitle.setText("Calories Burned");
        burnedTitle.setBackground(getActivity().getDrawable(R.drawable.border));
        tableRow.addView(burnedTitle, indivRowParams);

        //Title3
        TextView timeTitle = new TextView(getActivity());
        timeTitle.setPadding(10,5,0,5);
        timeTitle.setTypeface(null,Typeface.BOLD);
        timeTitle.setText("Time");
        timeTitle.setBackground(getActivity().getDrawable(R.drawable.border));
        tableRow.addView(timeTitle, indivRowParams);

        activitiesTable.addView(tableRow, rowParams);
        workoutLayout.addView(activitiesTable, tableParams);




        TableRow[] tableRows = new TableRow[8];
        List<WorkoutModel> todayActivities = workoutRepository.getTodayWorkout();
        for (int i = 0; i < todayActivities.size(); i++){

            tableRows[i] = new TableRow(getActivity());
            TextView activity = new TextView(getActivity());
            activity.setText(todayActivities.get(i).getActivityName());
            activity.setPadding(10,5,0,5);
            activity.setBackground(getActivity().getDrawable(R.drawable.border));

            TextView caloriesBurned = new TextView(getActivity());
            caloriesBurned.setText("Calories burned " + todayActivities.get(i).getCaloriesBurned());
            caloriesBurned.setPadding(10,5,0,5);
            caloriesBurned.setBackground(getActivity().getDrawable(R.drawable.border));

            TextView time = new TextView(getActivity());
            time.setText(todayActivities.get(i).getActivityDuration().toString());
            time.setPadding(10,5,0,5);
            time.setBackground(getActivity().getDrawable(R.drawable.border));

            tableRows[i].addView(activity);
            tableRows[i].addView(caloriesBurned);
            tableRows[i].addView(time);

            activitiesTable.addView(tableRows[i]);
        }

        LayoutInflater inflater = this.getLayoutInflater();
        final View customAlertDialogueViewWorkout = inflater.inflate(R.layout.add_today_dialogue, null, false);

        List<List<String>> activitiesList = workoutRepository.getActivityList();
        final Spinner activityName = customAlertDialogueViewWorkout.findViewById(R.id.spin_activity_today_name);
        final List<String> activityNames = new ArrayList<>(); //IMPORTANT GET ACTIVITY NAMES FROM REPO
        List<Integer> activityCaloriesBurned = new ArrayList<>();

        for (List<String> activity: activitiesList
             ) {
            activityNames.add(activity.get(0));
        }
        //activityNames.add("TeniLeni");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, activityNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityName.setAdapter(dataAdapter);

        final TextView activityTime = customAlertDialogueViewWorkout.findViewById(R.id.activity_today_duration);

        final AlertDialog noActivityTypes = new AlertDialog.Builder(getActivity()).create();
        noActivityTypes.setTitle("Alert");
        noActivityTypes.setMessage("No activity types found! Please add more under the activities tab.");
        noActivityTypes.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog addActivityAlert = new AlertDialog.Builder(getActivity()).setView(customAlertDialogueViewWorkout).setTitle("Add activity").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(activityTime.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(), "Please enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else {
                    String activityNameVal = activityName.getSelectedItem().toString();
                    String timeVal = String.valueOf(activityTime.getText());
                    addTodayActivity(activityNameVal, timeVal);
                    dialog.dismiss();
                }
            }
        }).create();


        FloatingActionButton btnAddActivity = view.findViewById(R.id.fab_workout_add);
        btnAddActivity.setVisibility(View.VISIBLE);
        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!activityNames.isEmpty()) {
                    addActivityAlert.show();
                }
                else{
                    noActivityTypes.show();
                }
            }
        });
    }
    public void drawHistoryInfo(final View view){
        final RelativeLayout workoutLayout = view.findViewById(R.id.workout_content_layout);
        workoutLayout.removeAllViews();

        //History Title
        TextView historyText = new TextView(workoutLayout.getContext());
        historyText.setTextAppearance(R.style.TextAppearance_AppCompat_Display1);
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textparams.addRule(RelativeLayout.ALIGN_TOP, workoutLayout.getId());
        textparams.addRule(RelativeLayout.ALIGN_LEFT, workoutLayout.getId());
        textparams.addRule(RelativeLayout.ALIGN_RIGHT, workoutLayout.getId());
        historyText.setGravity(Gravity.CENTER);

        historyText.setText("Activity History");
        historyText.setId(R.id.workout_history_title);

        workoutLayout.addView(historyText);

        //Chipgroup section
        ChipGroup chipGroup = new ChipGroup(getActivity());
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1){
                    switch (checkedId) {
                        case R.id.workout_history_chip_3mo:
                            resetChipsClickable(view);
                            drawChart(view, 3, workoutLayout);
                            view.findViewById(R.id.workout_history_chip_3mo).setClickable(false);
                            break;
                        case R.id.workout_history_chip_1mo:
                            resetChipsClickable(view);
                            drawChart(view, 1, workoutLayout);
                            view.findViewById(R.id.workout_history_chip_1mo).setClickable(false);
                            break;
                        case R.id.workout_history_chip_2we:
                            resetChipsClickable(view);
                            drawChart(view, 0.5f, workoutLayout);
                            view.findViewById(R.id.workout_history_chip_2we).setClickable(false);
                            break;
                        case R.id.workout_history_chip_6mo:
                            resetChipsClickable(view);
                            drawChart(view, 6, workoutLayout);
                            Chip chip6mo = view.findViewById(R.id.workout_history_chip_6mo);
                            if (chip6mo!=null)
                            chip6mo.setClickable(false);
                            break;
                    }
                }
            }

        });
        chipGroup.setSingleSelection(true);
        RelativeLayout.LayoutParams chipgparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        chipgparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        chipGroup.setId(R.id.workout_history_chip_group);

        workoutLayout.addView(chipGroup, chipgparams);

        ChipGroup inViewCGroup = view.findViewById(R.id.workout_history_chip_group);

        //Chip section
        final Chip chip = new Chip(getActivity());
        RelativeLayout.LayoutParams chipparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        chip.setText("6 Months");
        chip.setId(R.id.workout_history_chip_6mo);
        chip.setCheckable(true);
        chip.setChecked(true);
        chip.setClickable(false);
        inViewCGroup.addView(chip,chipparams);

        Chip chip2 = new Chip(getActivity());
        chip2.setId(R.id.workout_history_chip_3mo);
        chip2.setCheckable(true);
        chip2.setText("3 Months");
        inViewCGroup.addView(chip2,chipparams);

        Chip chip3 = new Chip(getActivity());
        chip3.setId(R.id.workout_history_chip_1mo);
        chip3.setCheckable(true);
        chip3.setText("1 Month");
        inViewCGroup.addView(chip3,chipparams);

        Chip chip4 = new Chip(getActivity());
        chip4.setId(R.id.workout_history_chip_2we);
        chip4.setCheckable(true);
        chip4.setText("2 Weeks");
        inViewCGroup.addView(chip4,chipparams);

        drawChart(view, 6, workoutLayout);

        FloatingActionButton btnAddActivity = view.findViewById(R.id.fab_workout_add);
        btnAddActivity.setVisibility(View.INVISIBLE);
    }

    public void drawActivitiesInfo(final View view){
        RelativeLayout workoutLayout = view.findViewById(R.id.workout_content_layout);

        TextView startWText = new TextView(workoutLayout.getContext());
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        startWText.setText("Activities");
        startWText.setId(R.id.workout_activities_title);
        startWText.setTextAppearance(R.style.TextAppearance_AppCompat_Display1);
        workoutLayout.addView(startWText,textparams);

        TableLayout activitiesTable = new TableLayout(getActivity());
        RelativeLayout.LayoutParams tableparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        tableparams.addRule(RelativeLayout.BELOW,R.id.workout_activities_title);
        tableparams.addRule(RelativeLayout.ALIGN_BASELINE, R.id.fab_workout_add);
        activitiesTable.setId(R.id.workout_activities_table);
        activitiesTable.setStretchAllColumns(true);
        activitiesTable.setBackground(getActivity().getDrawable(R.drawable.border));

        TableRow tableTitle = new TableRow(getActivity());
        RelativeLayout.LayoutParams rowparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView column1Title = new TextView(getActivity());
        column1Title.setPadding(10,5,0,5);
        column1Title.setText("Activity Name");
        column1Title.setId(R.id.workout_activities_column_a);
        TextView column2Title = new TextView(getActivity());
        column2Title.setPadding(10,5,0,5);
        column2Title.setText("Estimated Calories Burned/Hour");
        tableTitle.addView(column1Title);
        tableTitle.addView(column2Title);
        activitiesTable.addView(tableTitle,rowparams);

        TableRow tableContent = new TableRow(getActivity());
        tableContent.setId(R.id.workout_activities_row_content);
        rowparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        rowparams.addRule(RelativeLayout.ABOVE, R.id.fab_workout_add);
        List<String> activityName = new ArrayList<>(); //Create model for activities to read from DB
        List<Integer> calorieCount = new ArrayList<>();
        WorkoutRepository wr = WorkoutRepository.getInstance();
        UserRepository ur = UserRepository.getInstance();
        List<List<String>> dbactivities = wr.getActivityList();
        for (List<String> item:dbactivities
             ) {
            activityName.add(item.get(0));
            double itemEffort = 0;
            if (item.get(1).equals("Low"))
                itemEffort = (ur.getCurrUserModel().getWeight() * 3.5 * 3) /200 *60;
            if (item.get(1).equals("Medium"))
                itemEffort = (ur.getCurrUserModel().getWeight() * 3.5 * 5) /200 *60;
            if (item.get(1).equals("High"))
                itemEffort = (ur.getCurrUserModel().getWeight() * 3.5 * 7) /200 *60;

            calorieCount.add((int) itemEffort);
        }
//        for (int i = 0; i<20;i++){
//            activityName.add("Test Activity "+ i);
//            calorieCount.add((float)Math.random()*1000);
//        }
        List<TableRow> activityTextViewsList = new ArrayList<>();
        ScrollView activitiesScrollView = new ScrollView(getActivity());
        TableRow.LayoutParams sviewparams = new TableRow.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        sviewparams.span = 2;
        activitiesScrollView.setId(R.id.workout_activities_sv);
        //tableContent.addView(activitiesScrollView, sviewparams);
        activitiesTable.addView(tableContent, rowparams);
        workoutLayout.addView(activitiesTable, tableparams);

        TableLayout activitiesLayoutLinear = new TableLayout(getActivity());
        ScrollView.LayoutParams layoutLinearParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT,ScrollView.LayoutParams.MATCH_PARENT);
        activitiesLayoutLinear.setColumnStretchable(1,true);
        // = view.findViewById(R.id.workout_activities_sv);
        for (int i = 0; i< activityName.size(); i++) {
            TableRow currRow = new TableRow(getActivity());
            TextView activitytv = new TextView(getActivity());
            activitytv.setText(activityName.get(i));
            activitytv.setPadding(5,10,40,10);
            activitytv.setBackground(getActivity().getDrawable(R.drawable.border));
            currRow.addView(activitytv);
            TextView calorietv = new TextView(getActivity());
            calorietv.setGravity(Gravity.CENTER);
            calorietv.setText(Integer.toString(Math.round(calorieCount.get(i))));
            calorietv.setBackground(getActivity().getDrawable(R.drawable.border));
            calorietv.setPadding(5,10,0,10);
            currRow.addView(calorietv);
            activityTextViewsList.add(currRow);
        }

        for (TableRow nextView: activityTextViewsList
             ) {
            activitiesLayoutLinear.addView(nextView,rowparams);
            //view.findViewById(R.id.workout_activities_row_content).addView
            //activitiesLayoutLinear.addView(currRow,rowparams);
    }
        activitiesScrollView.addView(activitiesLayoutLinear,layoutLinearParams);

        TableRow contentRow = view.findViewById(R.id.workout_activities_row_content);
        contentRow.addView(activitiesScrollView,sviewparams);
        view.findViewById(R.id.workout_activities_table).invalidate();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View customAlertDialogueView = inflater.inflate(R.layout.twodialoguealert, null, false);

        final Spinner workoutEffort = customAlertDialogueView.findViewById(R.id.spin_workout_effort);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.workout_effort, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutEffort.setAdapter(adapter);

        final TextView activitiyName =customAlertDialogueView.findViewById(R.id.activity_name_alertbox);

        FloatingActionButton btnAddActivity = view.findViewById(R.id.fab_workout_add);
        btnAddActivity.setVisibility(View.VISIBLE);
        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setView(customAlertDialogueView).setTitle("Add activity").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String activityName = String.valueOf(activitiyName.getText());
                        String effort = workoutEffort.getSelectedItem().toString();
                        addActivity(activityName, effort);
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    public void addActivity(String name, String effort){
        WorkoutRepository workoutRepository = WorkoutRepository.getInstance();
        UserRepository ur = UserRepository.getInstance();
        Boolean finished = workoutRepository.addActivityType(ur.getCurrUserModel().getUserName(), name, effort);
        RelativeLayout rl = (RelativeLayout) currView.findViewById(R.id.workout_content_layout);
        rl.removeAllViews();
        drawActivitiesInfo(currView);

        //create database entry and add row to linearlayout in table in activities tab
    }

    public void addTodayActivity(String activityName, String time){
        WorkoutRepository workoutRepository = WorkoutRepository.getInstance();
        UserRepository ur = UserRepository.getInstance();
        String activityEffort = workoutRepository.getActvityEffort(activityName);
        double itemEffort = 0;
        if (activityEffort.equals("Low"))
            itemEffort = (ur.getCurrUserModel().getWeight() * 3.5 * 3) /200 *60;
        if (activityEffort.equals("Medium"))
            itemEffort = (ur.getCurrUserModel().getWeight() * 3.5 * 5) /200 *60;
        if (activityEffort.equals("High"))
            itemEffort = (ur.getCurrUserModel().getWeight() * 3.5 * 7) /200 *60;
        itemEffort = itemEffort * Integer.parseInt(time) / 60;
        final Calendar selectedDate = Calendar.getInstance();
        final SimpleDateFormat dbformat = new SimpleDateFormat("yyyy.MM.dd");
        final String dateDBFormatted = dbformat.format(selectedDate.getTime());
        WorkoutModel workout = new WorkoutModel(activityName, (int) itemEffort, Integer.parseInt(time), dateDBFormatted, activityEffort, ur.getCurrUserModel().getUserName());
        workoutRepository.addDailyActivity(workout);
        RelativeLayout rl = (RelativeLayout) currView.findViewById(R.id.workout_content_layout);
        rl.removeAllViews();
        drawTodayInfo(currView);
        //update DB
        //Calculate estimated calories based on activity effort in db over time.
    }
    public void resetChipsClickable(View view){
        final Chip chip6mo = (Chip) view.findViewById(R.id.workout_history_chip_6mo);
        final Chip chip3mo = (Chip) view.findViewById(R.id.workout_history_chip_3mo);
        final Chip chip1mo = (Chip) view.findViewById(R.id.workout_history_chip_1mo);
        final Chip chip2we = (Chip) view.findViewById(R.id.workout_history_chip_2we);
        if (chip6mo!=null)
        chip6mo.setClickable(true);
        if (chip3mo!=null)
        chip3mo.setClickable(true);
        if (chip1mo!=null)
        chip1mo.setClickable(true);
        if (chip2we!=null)
        chip2we.setClickable(true);
    }

    public void drawChart(View view, float dateRange, RelativeLayout workoutLayout){
        List<String> dateList = new ArrayList<>(); //GET FROM DB
        List<WorkoutModel> workoutHistory = WorkoutRepository.getInstance().getWorkoutHistory();

        List<Integer> activeTime = new ArrayList<>(); // get FROM DB
        List<BarEntry> chartVals = new ArrayList<>();
        final List<String> xAxis = new ArrayList<>();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("MM.dd");

        for (int i = 0; i <dateRange*30; i++){
            //activeTime.add((int) (Math.random()*40));
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE,-i);
            String formattedDate = simpleDate.format(date.getTime());
            xAxis.add(formattedDate);
            int activityTotalForDay = 0;
            for (WorkoutModel workout: workoutHistory
                 ) {
                String workoutDate = workout.getActivityDate();
                Log.d("workoutdate", workoutDate);
                String month = workoutDate.substring(5,7);
                String day = workoutDate.substring(8,10);
                workoutDate = month + "." + day;

                if(formattedDate.equals(workoutDate)){
                    activityTotalForDay += workout.getActivityDuration();
                    Log.d("workoutdatematch", "match found");
                }
            }
            activeTime.add(activityTotalForDay);
        }

        int index = 0;

        for (int active: activeTime) {
            chartVals.add(new BarEntry(index,activeTime.get(index))); //dateList.get(index).toString()
            index++;
        }

        int periodTime = 1;
        int dateField = Calendar.DATE;
        Calendar cEndTime = Calendar.getInstance();
        Calendar cStartTime = Calendar.getInstance();
        cStartTime.add(dateField,periodTime);

        BarDataSet barDataSet = new BarDataSet(chartVals, "Active Time (mins)");
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setColor(R.color.colorPrimary);
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextColor(ColorTemplate.getHoloBlue());
        barDataSet.setHighLightColor(Color.rgb(244, 117, 117));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.BLUE);
        data.setValueTextSize(9f);
        BarChart historyLineChart = new BarChart(getActivity());
        if(view.findViewById(R.id.workout_history_chart) != null) {
           historyLineChart = (BarChart)view.findViewById(R.id.workout_history_chart);
        }
        historyLineChart.setId(R.id.workout_history_chart);
        RelativeLayout.LayoutParams chartParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        chartParams.addRule(RelativeLayout.BELOW,R.id.workout_history_title);
        chartParams.addRule(RelativeLayout.ABOVE,R.id.workout_history_chip_group);
        BarChart barChart = historyLineChart;
        barChart.setData(data);
        XAxis chartXAxis = barChart.getXAxis();
        chartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chartXAxis.setDrawGridLines(false);
        chartXAxis.setValueFormatter(new IndexAxisValueFormatter(xAxis));
        barChart.setDescription(null);

        if(view.findViewById(R.id.workout_history_chart) == null) {
            workoutLayout.addView(historyLineChart, chartParams);
        }
        else{
            view.findViewById(R.id.workout_history_chart).invalidate();
        }
    }
}