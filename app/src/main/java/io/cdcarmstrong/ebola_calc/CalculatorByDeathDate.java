package io.cdcarmstrong.ebola_calc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalculatorByDeathDate extends AppCompatActivity {

    private final int DEFAULT_DAYS_FROM_SXS_TO_DEATH = 10;
    // used to keep track of the request number when changing the calendar date
    private static final int PICK_DATE_REQUEST = 2;
    // set variables to represent the views to be managed
    TextView tvDeathDateSelected, tvStartEstimate, tvEndEstimate, tvSymptomsDateEstimate;
    EditText etOnsetDeathDelay;
    Date selectedDate;
    int minIncubationPeriod, maxIncubationPeriod, onsetDeathDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_by_death_date);
        Intent intent = getIntent();

        tvDeathDateSelected =findViewById(R.id.tvDateOfDeathSelected);
        tvSymptomsDateEstimate =findViewById(R.id.tvEstSxsOnsetDate);
        tvStartEstimate = findViewById(R.id.tvEstStartDate);
        tvEndEstimate = findViewById(R.id.tvEstEndDate);
        etOnsetDeathDelay = findViewById(R.id.etDaysFromSxsToDeath);

        // set incubation period parameter from the Intent passed from MainActivity
        //    these parameters are only modified on the main activity
        minIncubationPeriod = intent.getIntExtra("minIncubationPeriod", 4);
        maxIncubationPeriod = intent.getIntExtra("maxIncubationPeriod", 17);
        TextView tvIncPd = findViewById(R.id.tvIncPdDth);
        tvIncPd.setText(minIncubationPeriod + "-" + maxIncubationPeriod);

        //  at initiation, set selected date to today's date
        setSelectedDate(new Date());

        // set listener to manage change in selected date; this calls up the calendar activity
        //   where the user selects the date;
        //   after date is select, the onActivityResult method (below) will be called
        tvDeathDateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculatorByDeathDate.this, CalendarSelect.class);
                intent.putExtra("selectedDate", selectedDate.getTime());
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_DATE_REQUEST);
            }
        });

        // set default time from symptom onset to death
        onsetDeathDelay = DEFAULT_DAYS_FROM_SXS_TO_DEATH;
        etOnsetDeathDelay.setText(Integer.toString(onsetDeathDelay));

        // set listener for the time between symptom onset and time of death
        etOnsetDeathDelay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Integer delay = 0;
                try{
                    delay = Integer.parseInt(etOnsetDeathDelay.getText().toString());
                }
                catch(Exception e){
                    delay = 0;
                }
                finally{
                    onsetDeathDelay = delay;
                    updateEstimates();
                }
            }
        });

        // after initial setup, make the estimates invisible until a date is selected or
        //    until the time between symptom onset and death is altered.
        setEstimatesVisible(false);
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
        tvDeathDateSelected.setText(getDateString(selectedDate, true));
    }

    // update estimates at bottom of page and make visible to user
    private void updateEstimates(){
        Date symptomsDate, startDate, endDate;
        // calculate estimates
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        c.add(Calendar.DATE, -onsetDeathDelay);
        symptomsDate = c.getTime();
        c.add(Calendar.DATE,-maxIncubationPeriod);
        startDate = c.getTime();
        c.add(Calendar.DATE, (maxIncubationPeriod - minIncubationPeriod));
        endDate = c.getTime();

        // update views and make visible
        tvSymptomsDateEstimate.setText(getDateString(symptomsDate, true));
        tvStartEstimate.setText(getDateString(startDate, false));
        tvEndEstimate.setText(getDateString(endDate, false));
        setEstimatesVisible(true);
    }

    // make estimates invisible (at initiation) or visible (after updated)
    private void setEstimatesVisible(Boolean visible){
        Integer v;
        if (visible) v = View.VISIBLE;
        else v=View.INVISIBLE;
        findViewById(R.id.tvEstSxsOnsetDateLabel).setVisibility(v);
        findViewById(R.id.tvExposureDatesLabel).setVisibility(v);
        findViewById(R.id.tvNDash).setVisibility(v);
        tvStartEstimate.setVisibility(v);
        tvEndEstimate.setVisibility(v);
        tvSymptomsDateEstimate.setVisibility(v);
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
