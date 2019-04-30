package io.cdcarmstrong.ebola_calc;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rgSelectLang;
    private RadioGroup.OnCheckedChangeListener rgListener;
    private boolean frenchDisclaimerShown = false;
    private boolean englishDisclaimerShown = false;
    private final int DEFAULT_MIN_INCUBATION_PERIOD = 4;
    private final int DEFAULT_MAX_INCUBATION_PERIOD = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.btGoActivitySx);
        Button button2 = findViewById(R.id.btGoActivityDeath);
        Button button3 = findViewById(R.id.btEditIncPd);
        rgSelectLang = findViewById(R.id.rgSelectLanguage);

        rgListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbEnglish)
                    setLocale("en");
                else setLocale("fr");
            }
        };

        // update version name
        String strVersion = "Version " + BuildConfig.VERSION_NAME;
        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(strVersion);

        // set actions for each of the buttons
        // button1: opens the symptom onset activity
        button1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, CalculatorBySymptomDate.class);
               intent.putExtra("minIncubationPeriod", getMinIncubationPeriod());
               intent.putExtra("maxIncubationPeriod", getMaxIncubationPeriod());
               startActivity(intent);
           }
        });
        // button2: opens death onset activity
        button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CalculatorByDeathDate.class);
                    intent.putExtra("minIncubationPeriod", getMinIncubationPeriod());
                    intent.putExtra("maxIncubationPeriod", getMaxIncubationPeriod());
                    startActivity((intent));
                }
        });
        // button3: mades the incubation period parameters (min and max editable)
        button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editTextMin = findViewById(R.id.etMinIncPd);
                    EditText editTextMax = findViewById(R.id.etMaxIncPd);
                    editTextMin.setEnabled(true);
                    editTextMax.setEnabled(true);
                    editTextMin.setAlpha(1.0f);
                    editTextMax.setAlpha(1.0f);
                    findViewById(R.id.tvDays1).setAlpha(1.0f);
                    findViewById(R.id.tvDays2).setAlpha(1.0f);
                    findViewById(R.id.tvMin).setAlpha(1.0f);
                    findViewById(R.id.tvMax).setAlpha(1.0f);
                    findViewById(R.id.btEditIncPd).setEnabled(false); //disable button
                }
        });

        // after the initial setup, go to the disclaimer page;
        // note that this will be triggered again if the langauage is changed (see setLocale method)
        Intent intent = new Intent(this, Disclaimer.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EditText editTextMin = findViewById(R.id.etMinIncPd);
        EditText editTextMax = findViewById(R.id.etMaxIncPd);
        // disable and dim the two EditText views, make sure incubation period button is enabled
        editTextMin.setEnabled(false);
        editTextMax.setEnabled(false);
        editTextMin.setAlpha(0.2f);
        editTextMax.setAlpha(0.2f);
        findViewById(R.id.btEditIncPd).setEnabled(true);
        // dim the text around them:
        findViewById(R.id.tvDays1).setAlpha(0.2f);
        findViewById(R.id.tvDays2).setAlpha(0.2f);
        findViewById(R.id.tvMin).setAlpha(0.2f);
        findViewById(R.id.tvMax).setAlpha(0.2f);
        // Note:  these will be enabled and made opaque when the button3 is pushed

        // Set the language radio button to the default language of the device
        String currLang = getResources().getConfiguration().locale.getLanguage();
        // listener is set to null while radio button is set (so as not to start a loop)
        rgSelectLang.setOnCheckedChangeListener(null);
        if (currLang.equals("en")){
            rgSelectLang.check(R.id.rbEnglish);
        }
        else{
            rgSelectLang.check(R.id.rbFrench);
        }
        // after language is set, the listener is turned back on again
        rgSelectLang.setOnCheckedChangeListener(rgListener);
    }

    // returns the minimum incubation period from the EditText view
    private Integer getMinIncubationPeriod(){
        EditText editTextMin = findViewById(R.id.etMinIncPd);
        Integer n;
        try{
            if (editTextMin.getText().toString().trim().length() == 0){  // view is empty
                n=0;
                editTextMin.setText("0");
            }
            else n = Integer.parseInt(editTextMin.getText().toString() );
        }
        catch(Exception e){
            n = DEFAULT_MIN_INCUBATION_PERIOD;
            editTextMin.setText(n.toString());
        }
        return n;
    }

    // returns the maximum incubation period from the EditText view
    private Integer getMaxIncubationPeriod(){
        EditText editTextMax = findViewById(R.id.etMaxIncPd);
        Integer n;
        try{
            if (editTextMax.getText().toString().trim().length() == 0){  // view is empty
                n=0;
                editTextMax.setText("0");
            }
            else n = Integer.parseInt(editTextMax.getText().toString() );
        }
        catch(Exception e){
            n = DEFAULT_MAX_INCUBATION_PERIOD;
            editTextMax.setText(n.toString());
        }
        return n;
    }

    // used to change the application locale and language when new language is selected
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}
