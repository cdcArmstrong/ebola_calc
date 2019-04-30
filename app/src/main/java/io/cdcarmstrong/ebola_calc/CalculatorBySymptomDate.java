package io.cdcarmstrong.ebola_calc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalculatorBySymptomDate extends AppCompatActivity {

    private final int DEFAULT_DAYS_BLEEDING = 7;
    private final int DEFAULT_DAYS_DIARRHEA = 4;

    // parameters used to calculate the exposure window.
    // these are set at various points below
    private Date selectedDate;
    private Boolean bleedingYesNo, diarrheaYesNo;
    private int daysBleeding, daysDiarrhea;
    private int minIncubationPeriod, maxIncubationPeriod;

    // used to keep track of the request number when changing the calendar date
    private static final int PICK_DATE_REQUEST = 1;
    // variables to hold the various views in the activity
    private RadioGroup rgBleedingYesNo, rgDiarrheaYesNo;
    private EditText etBleedingTime, etDiarrheaTime;
    private TextView tvBleedingYesNo_label, tvBleedingTime_label,
            tvDiarrheaYesNo_label, tvDiarrheaTime_label,
            tvExposureDates_label,
            tvSymptomOnset_label, tvSymptomOnset_est,
            tvIncubationStart, tvDash, tvIncubationEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_by_symptom_date);

        // initialize the view references
        rgBleedingYesNo = findViewById(R.id.radioGroupBleeding);
        etBleedingTime = findViewById(R.id.editTextBleedingTime);
        etBleedingTime.setText(Integer.toString(DEFAULT_DAYS_BLEEDING));
        rgDiarrheaYesNo = findViewById(R.id.radioGroupDiarrhea);
        etDiarrheaTime = findViewById(R.id.editTextDiarrheaTime);
        etDiarrheaTime.setText(Integer.toString(DEFAULT_DAYS_DIARRHEA));
        tvBleedingYesNo_label = findViewById(R.id.labelBleedingYN);
        tvBleedingTime_label = findViewById(R.id.labelBleedingTime);
        tvDiarrheaYesNo_label = findViewById(R.id.labelDiarrheaYN);
        tvDiarrheaTime_label = findViewById(R.id.labelDiarrheaTime);
        tvExposureDates_label = findViewById(R.id.tvExposureDatesLabel);
        tvSymptomOnset_label = findViewById(R.id.tvEstSymptDateLabel);
        tvSymptomOnset_est = findViewById(R.id.tvEstSymptDate);
        tvIncubationStart = findViewById(R.id.tvEstStart);
        tvIncubationEnd = findViewById(R.id.tvEstEnd);
        tvDash = findViewById(R.id.tvDash);

        // get incubation period parameters from the Intent passed from the main page and
        //    set those on this page
        Intent intent = getIntent();
        minIncubationPeriod = intent.getIntExtra("minIncubationPeriod", 4);
        maxIncubationPeriod = intent.getIntExtra("maxIncubationPeriod", 17);
        TextView tvIncPd = findViewById(R.id.tvIncPeriodSx);
        tvIncPd.setText(minIncubationPeriod + "-" + maxIncubationPeriod);

        // on startup, set date to today's date
        setSelectedDate(new Date());
        // initialize bleed, diarrheaYesNo and other values, initialize estimates to invisible
        //   these become visible only after the parameters re altered
        updateValues();
        setEstimatesVisibility(View.INVISIBLE);

        // symptom onset date listener: opens CalendarSelect activity and returns data to onActivityResult()
        TextView tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculatorBySymptomDate.this, CalendarSelect.class);
                intent.putExtra("selectedDate", selectedDate.getTime());
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_DATE_REQUEST);
            }
        });

        // set the bleedingYesNo yes/no listener
        rgBleedingYesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateValues();
                updateEstimates();
            }
        });

        // set the bleeding days listener
        etBleedingTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateValues();
                updateEstimates();
            }
        });

        // set the diarrheaYesNo yes/no listener
        rgDiarrheaYesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateValues();
                updateEstimates();
            }
        });

        // diarrhea days listener
        etDiarrheaTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                updateValues();
                updateEstimates();
            }
        });
    }

    // this captures the selected date after getting back from the calendar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){

        if((requestCode == PICK_DATE_REQUEST) && (resultCode==RESULT_OK)){
            Date date = new Date();
            date.setTime(intent.getLongExtra("selectedDate", new Date().getTime()));
            setSelectedDate(date);
            updateEstimates();
        }
        else{
            setSelectedDate(new Date());
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    // sets the selected date and formats it to either French or English
    private void setSelectedDate(Date date){
        selectedDate = date;
        TextView tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedDate.setText(getDateString(selectedDate, true));
    }

    //  turn estimates either off (on initialization) or on (after parameters set)
    private void setEstimatesVisibility(int v){
        tvExposureDates_label.setVisibility(v);
        tvSymptomOnset_label.setVisibility(v);
        tvSymptomOnset_est.setVisibility(v);
        tvIncubationStart.setVisibility(v);
        tvIncubationEnd.setVisibility(v);
        tvDash.setVisibility(v);
    }

    // update the values based on input and make approprite views active or inactive
    private void updateValues(){
        if (rgBleedingYesNo.getCheckedRadioButtonId() == R.id.rbBleedingYes){
            bleedingYesNo = true;
            makeViewActive(tvBleedingTime_label, true);
            makeViewActive(etBleedingTime, true);
            makeViewActive(tvDiarrheaYesNo_label, false);
            makeViewActive(rgDiarrheaYesNo, false);
            makeViewActive(tvDiarrheaTime_label, false);
            makeViewActive(etDiarrheaTime, false);
        }
        else{
            bleedingYesNo = false;
            makeViewActive(tvBleedingTime_label, false);
            makeViewActive(etBleedingTime, false);
            makeViewActive(tvDiarrheaYesNo_label, true);
            makeViewActive(rgDiarrheaYesNo, true);

            if (rgDiarrheaYesNo.getCheckedRadioButtonId() == R.id.rbDiarrheaYes){
                diarrheaYesNo =true;
                makeViewActive(tvDiarrheaTime_label,true);
                makeViewActive(etDiarrheaTime, true);
                makeViewActive(tvBleedingYesNo_label, false);
                makeViewActive(rgBleedingYesNo, false);

            }
            else{
                diarrheaYesNo =false;
                makeViewActive(tvDiarrheaTime_label, false);
                makeViewActive(etDiarrheaTime, false);
                makeViewActive(tvBleedingYesNo_label, true);
                makeViewActive(rgBleedingYesNo, true);
            }
        }

        try{
            daysBleeding = Integer.parseInt(etBleedingTime.getText().toString());
        }
        catch(Exception e){
            daysBleeding = 0;
        }
        try{
            daysDiarrhea = Integer.parseInt(etDiarrheaTime.getText().toString());
        }
        catch(Exception e){
            daysDiarrhea = 0;
        }
    }

    // update the estimated values
    private void updateEstimates(){
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        if (bleedingYesNo) c.add(Calendar.DATE, -daysBleeding);
        else if (diarrheaYesNo) c.add(Calendar.DATE, -daysDiarrhea);

        tvSymptomOnset_est.setText(getDateString(c.getTime(), true));
        c.add(Calendar.DATE, -maxIncubationPeriod);
        tvIncubationStart.setText(getDateString(c.getTime(), false));
        c.add(Calendar.DATE, maxIncubationPeriod - minIncubationPeriod);
        tvIncubationEnd.setText(getDateString(c.getTime(), false));

        setEstimatesVisibility(View.VISIBLE);
    }

    // method for enabling or disabling a view and making it opaque or semi-transparent
    private void makeViewActive(View view, Boolean active){
        view.setEnabled(active);
        if (active) view.setAlpha(1.0f);
        else view.setAlpha(0.25f);
        if (view instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) view;
            int n = radioGroup.getChildCount();
            for (int i=0;i<n;i++){
                View o = radioGroup.getChildAt(i);
                if (o instanceof RadioButton) o.setEnabled(active);
            }

        }
    }

    // formatter for dates -- with or without year at end
    // adjusts dates to international settings
    private String getDateString(Date date, Boolean withYear){
        Locale locale = getResources().getConfiguration().locale;
        String pattern;
        if (withYear) {
            if (locale.getLanguage().equals("en")) pattern = "EEE dd-MMM-yyyy";
            else pattern = "EEEdd-MMM-yyyy";
        }
        else{
            if (locale.getLanguage().equals("en")) pattern = "EEE dd-MMM";
            else pattern = "EEEdd-MMM";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return sdf.format(date);
    }
}
