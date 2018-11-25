package com.leondeklerk.wheremybike;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSet;
    EditText edTxtS, edTxtR, edTxtN;
    SharedPreferences prefences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefences = PreferenceManager.getDefaultSharedPreferences(this);

        edTxtS = findViewById(R.id.editText_stalling);
        edTxtR = findViewById(R.id.editText_rij);
        edTxtN = findViewById(R.id.editText_nummer);
        btnSet = findViewById(R.id.button);

        btnSet.setOnClickListener(this);

        edTxtS.setText(getValue("pref_stalling"));
        edTxtR.setText(getValue("pref_rij"));
        edTxtN.setText(getValue("pref_nummer"));
    }

    @Override
    public void onClick(View view) {

        if(view == btnSet){
            prefences.edit().putInt("pref_stalling", getSetValue(edTxtS))
                    .putInt("pref_rij", getSetValue(edTxtR))
                    .putInt("pref_nummer", getSetValue(edTxtN))
                    .apply();
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edTxtN.getWindowToken(), 0);

        edTxtS.clearFocus();
        edTxtR.clearFocus();
        edTxtN.clearFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefences.edit().putInt("pref_stalling", getSetValue(edTxtS))
                .putInt("pref_rij", getSetValue(edTxtR))
                .putInt("pref_nummer", getSetValue(edTxtN))
                .apply();
    }

    public String getValue(String prefTag){
        return String.valueOf(prefences.getInt(prefTag, 1));
    }

    public int getSetValue(EditText editText){
        if((editText.getText().toString().equals(""))){
            editText.setText(String.valueOf(1));
            return 1;
        }

        return Integer.parseInt(editText.getText().toString());
    }

}
