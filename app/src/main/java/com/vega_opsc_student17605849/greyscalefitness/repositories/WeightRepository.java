package com.vega_opsc_student17605849.greyscalefitness.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vega_opsc_student17605849.greyscalefitness.models.NutritionModel;
import com.vega_opsc_student17605849.greyscalefitness.models.UserModel;
import com.vega_opsc_student17605849.greyscalefitness.models.WeightInfoModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeightRepository {

    private static WeightRepository INSTANCE = null;

    private WeightRepository(){}

    public static WeightRepository getInstance()  {
        if (INSTANCE == null) {
            INSTANCE = new WeightRepository();
        }
        return(INSTANCE);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<WeightInfoModel> fullWeightHistory = new ArrayList<>();
    private WeightInfoModel latestModel = new WeightInfoModel();

    public List<WeightInfoModel> getFullWeightHistory() {
        return fullWeightHistory;
    }

    public WeightInfoModel getLatestModel() {
        return latestModel;
    }

    public void getFullWeightHistory(final String userName){
        fullWeightHistory.clear();
        db.collection("weightinfos").whereEqualTo("userName", userName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int latestYear = 0;
                int latestMonth = 0;
                int latestDay = 0;
                for (QueryDocumentSnapshot doc: task.getResult()
                     ) {
                    fullWeightHistory.add(doc.toObject(WeightInfoModel.class));

                }
                for (WeightInfoModel weightModel: fullWeightHistory
                     ) {
                    int year = Integer.parseInt(weightModel.getDateCaptured().substring(0,4));
                    int month = Integer.parseInt(weightModel.getDateCaptured().substring(5,7));
                    int day = Integer.parseInt(weightModel.getDateCaptured().substring(8,10));
                    if (year>= latestYear){
                        if (month >= latestMonth){
                            if (day > latestDay){
                                latestYear = year;
                                latestMonth = month;
                                latestDay = day;
                                latestModel = weightModel;
                            }
                        }
                    }
                }

            }
        });

    }

    public void addNewMeasurement(WeightInfoModel measurement){
        db.collection("weightinfos").document(measurement.getUserName() + measurement.getDateCaptured()).set(measurement);
        getFullWeightHistory(measurement.getUserName());
    }

    public List<WeightInfoModel> getLastSevenEntries(){
        List<WeightInfoModel> lastSevenRecords = new ArrayList<>();
        List<String> datestring = new ArrayList<>();
        for (WeightInfoModel entry: fullWeightHistory
             ) {
            datestring.add(entry.getDateCaptured().substring(5,10));

        }
        Collections.sort(datestring, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = Integer.valueOf(o1.replaceAll("\\.", ""));
                int i2 = Integer.valueOf(o2.replaceAll("\\.", ""));
                return Integer.compare(i1, i2);
            }
        });
        if (datestring.size() > 7) {
            for (int i = 0; i < 7; i++){
                for (WeightInfoModel weightmodel: fullWeightHistory
                     ) {
                    if (weightmodel.getDateCaptured().contains(datestring.get(i))){
                        lastSevenRecords.add(weightmodel);
                    }
                }
            }
        }
        else {
            for (String date : datestring
            ) {
                for (WeightInfoModel weightmodel: fullWeightHistory
                ) {
                    if (weightmodel.getDateCaptured().contains(date)){
                        lastSevenRecords.add(weightmodel);
                    }
                }
            }
        }
        return lastSevenRecords;
    }
    public void initWeight(WeightInfoModel measure){
        db.collection("weightinfos").document(measure.getUserName() + measure.getDateCaptured()).set(measure);
    }

}


