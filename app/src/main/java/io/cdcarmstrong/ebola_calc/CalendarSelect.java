package io.cdcarmstrong.ebola_calc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

public class CalendarSelect extends AppCompatActivity {

    CalendarView cvSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_select);

        cvSelect = findViewById(R.id.cvSelectDate);

        //Locale currLocale = getResources().getConfiguration().locale;
        String currLang = getResources().getConfiguration().locale.getLanguage();
        if (currLang == "en"){
            cvSelect.setFirstDayOfWeek(1);
        }
        else {
            cvSelect.setFirstDayOfWeek(2);
        }

        Intent intent = getIntent();
        cvSelect.setDate(intent.getLongExtra("selectedDate", new Date().getTime()));

        cvSelect.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year,month,dayOfMonth);
                Date d = c.getTime();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedDate", d.getTime());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
