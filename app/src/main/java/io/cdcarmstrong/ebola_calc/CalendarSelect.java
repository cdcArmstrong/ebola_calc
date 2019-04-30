package io.cdcarmstrong.ebola_calc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

public class CalendarSelect extends AppCompatActivity {

    CalendarView cvSelect;
    Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_select);

        cvSelect = findViewById(R.id.cvSelectDate);
        Button btOkay = findViewById(R.id.cvOkayButton);
        selectedDate = new Date();

        //Locale currLocale = getResources().getConfiguration().locale;
        String currLang = getResources().getConfiguration().locale.getLanguage();
        if (currLang.equals("en")){
            cvSelect.setFirstDayOfWeek(1);
        }
        else {
            cvSelect.setFirstDayOfWeek(2);
        }

        Intent intent = getIntent();
        selectedDate.setTime(intent.getLongExtra("selectedDate", new Date().getTime()));
        cvSelect.setDate(selectedDate.getTime());

        cvSelect.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDate = c.getTime();
            }
        });

        btOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedDate", selectedDate.getTime());
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });


    }
}
