package com.vega_opsc_student17605849.greyscalefitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vega_opsc_student17605849.greyscalefitness.models.WeightInfoModel;
import com.vega_opsc_student17605849.greyscalefitness.repositories.GoalsRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.UserRepository;
import com.vega_opsc_student17605849.greyscalefitness.repositories.WeightRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeightInfoActivity extends AppCompatActivity {

    private WeightInfoModel todayMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_info);
        final Context currContext =  getApplicationContext();

        SimpleDateFormat simpleDate = new SimpleDateFormat("YYYY.MM.dd");
        Calendar date = Calendar.getInstance();
        String formattedDate = simpleDate.format(date.getTime());

        final WeightRepository weightRepository = WeightRepository.getInstance();

        todayMeasurement = weightRepository.getLatestModel();
        if(todayMeasurement == null)  {

            todayMeasurement.init(UserRepository.getInstance().getCurrUserModel().getUserName(), formattedDate);
        weightRepository.addNewMeasurement(todayMeasurement);
        }
        if(!todayMeasurement.getDateCaptured().equals(formattedDate)){
            todayMeasurement.setDateCaptured(formattedDate);
        }
        MaterialToolbar topBar = findViewById(R.id.topAppBar);
        topBar.setTitle("Weight Info");
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
                 /*   case R.id.menu_item_dashboard:
                        if (this.getClass().equals(MainActivity.class)){}
                        else{
                            Intent intent = new Intent(currContext, MainActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.menu_item_weight:
                        if (this.getClass().equals(WeightInfoActivity.class)){}
                        else{
                            Intent intent = new Intent(currContext, WeightInfoActivity.class);
                            startActivity(intent);
                        }*/
                }
                return false;
            }
        });

        Button btnAddTargetWeight = findViewById(R.id.btn_weight_new_target);

        btnAddTargetWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WeightInfoActivity.this);
                builder.setTitle("Add target weight");

                final EditText input = new EditText(WeightInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().isEmpty()){
                        addWeightTargetToDB(Float.parseFloat(input.getText().toString()));
                        }
                        dialog.dismiss();
                        UserRepository.getInstance().getCurrUserModel().setTargetWeight(Double.parseDouble(input.getText().toString()));
                        UserRepository.getInstance().updateCurrentUserInfo(UserRepository.getInstance().getCurrUserModel());
                        GoalsRepository.getInstance().getUserGoals().setTargetWeight(Float.parseFloat(input.getText().toString()));
                        GoalsRepository.getInstance().addUserGoals( UserRepository.getInstance().getCurrUserModel().getUserName(), GoalsRepository.getInstance().getUserGoals());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        TextView txtWeight = findViewById(R.id.weight_info_weight);
        txtWeight.setText(Float.toString(todayMeasurement.getWeight()));
        TextView txtMuscle= findViewById(R.id.weight_info_muscle);
        txtMuscle.setText(Float.toString(todayMeasurement.getMuscle()));
        TextView txtWater = findViewById(R.id.weight_info_water);
        txtWater.setText(Float.toString(todayMeasurement.getWater()));
        TextView txtFat = findViewById(R.id.weight_info_fat);
        txtFat.setText(Float.toString(todayMeasurement.getFat()));
        TextView targetWeight = findViewById(R.id.weight_info_target_weight);
        targetWeight.setText((todayMeasurement.getTarget()) + " "  + UserRepository.getInstance().getCurrUserModel().getWeightMeticPref());
        //SET THE ABOVE VALUES TO THE CURRENT VALS IN DB

        Button btnAddWeight = findViewById(R.id.btn_weight_new_weight);
        Button btnAddMuscle = findViewById(R.id.btn_weight_new_muscle);
        Button btnAddWater = findViewById(R.id.btn_weight_new_water);
        Button btnAddFat = findViewById(R.id.btn_weight_new_fat);

        btnAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WeightInfoActivity.this);
                builder.setTitle("Record new weight");

                final EditText input = new EditText(WeightInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().isEmpty()) {
                            updateWeight(Float.parseFloat(input.getText().toString()));

                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btnAddMuscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WeightInfoActivity.this);
                builder.setTitle("Record new muscle percentage");

                final EditText input = new EditText(WeightInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().isEmpty())
                            updateMuscle(Float.parseFloat(input.getText().toString()));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btnAddWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WeightInfoActivity.this);
                builder.setTitle("Record new water percentage");

                final EditText input = new EditText(WeightInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().isEmpty())
                            updateWater(Float.parseFloat(input.getText().toString()));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btnAddFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WeightInfoActivity.this);
                builder.setTitle("Record new fat percentage");

                final EditText input = new EditText(WeightInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().isEmpty())
                            updateFat(Float.parseFloat(input.getText().toString()));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        Button btnWeightStats = findViewById(R.id.weightDetailsButton);
        Button btnMuscleStats = findViewById(R.id.muscleDetailsButton);
        Button btnWaterStats = findViewById(R.id.waterDetailsButton);
        Button btnFatStats = findViewById(R.id.fatDetailsButton);

        btnWeightStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawGraphs("weight", "Weight history"); //get from DB and pass through to draw when stats clicked.
            }
        });
        btnMuscleStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawGraphs("muscle","Muscle percentage history"); //get from DB and pass through to draw when stats clicked.
            }
        });
        btnWaterStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawGraphs("water", "Water weight history"); //get from DB and pass through to draw when stats clicked.
            }
        });
        btnFatStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawGraphs("fat", "Fat percentage history"); //get from DB and pass through to draw when stats clicked.

            }
        });

    }

    public void addWeightTargetToDB(float targetWeight){
        if (targetWeight > 0){
            TextView txtWeightTarget = findViewById(R.id.weight_info_target_weight);
            txtWeightTarget.setText(Float.toString(targetWeight) + UserRepository.getInstance().getCurrUserModel().getWeightMeticPref());
            todayMeasurement.setTarget(targetWeight);
            WeightRepository.getInstance().addNewMeasurement(todayMeasurement);
        }
    }

    public void updateWeight(float weight){
        TextView txtWeight = findViewById(R.id.weight_info_weight);
        txtWeight.setText(Float.toString(weight));
        todayMeasurement.setWeight(weight);
        WeightRepository.getInstance().addNewMeasurement(todayMeasurement);
    }

    public void updateMuscle(float muscle){
        TextView txtMuscle = findViewById(R.id.weight_info_muscle);
        txtMuscle.setText(Float.toString(muscle));
        todayMeasurement.setMuscle(muscle);
        WeightRepository.getInstance().addNewMeasurement(todayMeasurement);
    }

    public void updateWater(float water){
        TextView txtWater = findViewById(R.id.weight_info_water);
        txtWater.setText(Float.toString(water));
        todayMeasurement.setWater(water);
        WeightRepository.getInstance().addNewMeasurement(todayMeasurement);
    }

    public void updateFat(float fat){
        TextView txtFat = findViewById(R.id.weight_info_fat);
        txtFat.setText(Float.toString(fat));
        todayMeasurement.setFat(fat);
        WeightRepository.getInstance().addNewMeasurement(todayMeasurement);
    }

    public void drawGraphs(String chartContent, String chartTitle){
        WeightRepository weightRepository = WeightRepository.getInstance();
        List<WeightInfoModel> history = weightRepository.getLastSevenEntries();
        List<String> dateList = new ArrayList<>(); //GET FROM DB
        List<Float> measurementValue = new ArrayList<>(); // get FROM DB
        List<Entry> chartVals = new ArrayList<>();
        final List<String> xAxis = new ArrayList<>();


        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM");
        if (history.size()< 7) {
            for (WeightInfoModel entry: history
                 ) {
                if (chartContent.equals("weight")) {
                    measurementValue.add(entry.getWeight());
                }
                if (chartContent.equals("muscle")) {
                    measurementValue.add(entry.getMuscle());
                }
                if (chartContent.equals("fat")) {
                    measurementValue.add(entry.getFat());
                }
                if (chartContent.equals("water")) {
                    measurementValue.add(entry.getWater());
                }
                xAxis.add(entry.getDateCaptured().substring(5,10).replaceAll("\\.", "/"));
                Log.d("Dateadded", entry.getDateCaptured().substring(5,10));

                Log.d("Xaxislength", xAxis.size() + " date size");
            }
        }
        else
        {
            for (int i = 0; i <7; i++){
                if (chartContent.equals("weight")) {
                    measurementValue.add(history.get(i).getWeight());
                }
                if (chartContent.equals("muscle")) {
                    measurementValue.add(history.get(i).getMuscle());
                }
                if (chartContent.equals("fat")) {
                    measurementValue.add(history.get(i).getFat());
                }
                if (chartContent.equals("water")) {
                    measurementValue.add(history.get(i).getWater());
                }
                xAxis.add(history.get(i).getDateCaptured().substring(5,10).replaceAll("\\.", "/"));
            }
        }
        int index = 0;
        String[] xvals = new String[xAxis.size()];
        for (float measurement: measurementValue) {
            chartVals.add(new Entry(index, measurement));
            xvals[index] = xAxis.get(index);
            //chartVals.add(new BarEntry(index,measurementValue.get(index))); //dateList.get(index).toString()
            index++;
        }



        LineDataSet lineDataSet = new LineDataSet(chartVals, "Measurement");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(R.color.colorPrimary);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextColor(ColorTemplate.getHoloBlue());
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);

        LayoutInflater inflater = this.getLayoutInflater();
        final View customAlertDialogueViewWorkout = inflater.inflate(R.layout.chart_dialogue, null, false);
        LineChart chart = customAlertDialogueViewWorkout.findViewById(R.id.weightGraph);
        chart.setData(null);

        chart.setData(data);
        chart.getLineData().setValueTextSize(12);
        XAxis chartXAxis = chart.getXAxis();

        chartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chartXAxis.setDrawGridLines(false);
        chartXAxis.setValueFormatter(new IndexAxisValueFormatter(xvals));
        chartXAxis.setEnabled(true);
        chartXAxis.setCenterAxisLabels(false);
        chartXAxis.setLabelCount(xvals.length);
        chartXAxis.setLabelRotationAngle(45);
        chartXAxis.setAxisMaximum(xvals.length);

        chart.setDescription(null);



        chart.notifyDataSetChanged();
        chart.invalidate();
        //customAlertDialogueViewWorkout.invalidate();

        new AlertDialog.Builder(WeightInfoActivity.this).setView(customAlertDialogueViewWorkout).setTitle(chartTitle).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).show();
        chart.invalidate();
    }

}