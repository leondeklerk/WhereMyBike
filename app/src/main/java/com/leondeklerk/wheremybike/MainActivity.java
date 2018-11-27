package com.leondeklerk.wheremybike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    Button btnSet;
    EditText edTxtS, edTxtR, edTxtN;
    SharedPreferences preferences;
    ListView notificationList;
    List<String> arrayList;
    Set<String> set;
    Set<String> setTest = new TreeSet<>();
    public ArrayAdapter<String> adapterNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        edTxtS = findViewById(R.id.editText_stalling);
        edTxtR = findViewById(R.id.editText_rij);
        edTxtN = findViewById(R.id.editText_nummer);
        btnSet = findViewById(R.id.button);
        notificationList = findViewById(R.id.not_list);
        notificationList.setOnItemClickListener(this);

        btnSet.setOnClickListener(this);

        edTxtS.setText(getValue("pref_stalling"));
        edTxtR.setText(getValue("pref_rij"));
        edTxtN.setText(getValue("pref_nummer"));
        setList(this);
    }

    @Override
    public void onClick(View view) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        int stalling = getSetValue(edTxtS);
        int rij = getSetValue(edTxtR);
        int nummer = getSetValue(edTxtN);
        Boolean workaround = preferences.getBoolean("workaround_switch", false);
        workaround = !workaround;
        set = preferences.getStringSet("notification_array", setTest);
        set.add("(" + stalling + " - " + rij + " - " + nummer + ") - " + df.format(now.getTime()));
        preferences.edit().putInt("pref_stalling", stalling)
                .putInt("pref_rij", rij)
                .putInt("pref_nummer", nummer)
                .putStringSet("notification_array", set)
                .putBoolean("workaround_switch", workaround)
                .apply();

        setList(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edTxtN.getWindowToken(), 0);

        edTxtS.clearFocus();
        edTxtR.clearFocus();
        edTxtN.clearFocus();
    }

    public String getValue(String prefTag) {
        return String.valueOf(preferences.getInt(prefTag, 1));
    }

    public int getSetValue(EditText editText) {
        if ((editText.getText().toString().equals(""))) {
            editText.setText(String.valueOf(1));
            return 1;
        }

        return Integer.parseInt(editText.getText().toString());
    }

    public void setList(Context context) {
        set = preferences.getStringSet("notification_array", setTest);
        arrayList = new ArrayList<>(set);
        arrayList.sort(new StringComparator());
        adapterNotification = new ArrayAdapter<>(context, R.layout.list_item, android.R.id.text1, arrayList);
        notificationList.setAdapter(adapterNotification);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme_dark);
        alertBuilder.setMessage("Do you want to delete this item?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Boolean workaround = preferences.getBoolean("workaround_switch", false);
                workaround = !workaround;
                String givenDateString = notificationList.getItemAtPosition(i).toString();
                set = preferences.getStringSet("notification_array", setTest);
                set.remove(givenDateString);
                preferences.edit().putStringSet("notification_array", set).putBoolean("workaround_switch", workaround).apply();
                setList(getApplicationContext());
            }
        });
        alertBuilder.show();
    }
}
