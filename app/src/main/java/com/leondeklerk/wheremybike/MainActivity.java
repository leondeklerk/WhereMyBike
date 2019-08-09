package com.leondeklerk.wheremybike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    Button btnSet;
    EditText edTxtS, edTxtR, edTxtN;
    TextView locText;
    SharedPreferences preferences;
    ListView notificationList;
    List<String> arrayList;
    Set<String> set;
    Set<String> setTest = new HashSet<>();
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
        locText = findViewById(R.id.location_text);
        notificationList = findViewById(R.id.not_list);
        notificationList.setOnItemClickListener(this);

        btnSet.setOnClickListener(this);

        edTxtS.setText(getValue("pref_stalling"));
        edTxtR.setText(getValue("pref_rij"));
        edTxtN.setText(getValue("pref_nummer"));
        locText.setText(preferences.getString("location_string", ""));
        setList(this);
    }

    @Override
    public void onClick(View view) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

        int stalling = getSetValue(edTxtS);
        int rij = getSetValue(edTxtR);
        int nummer = getSetValue(edTxtN);
        boolean workaround = preferences.getBoolean("workaround_switch", false);
        workaround = !workaround;
        set = preferences.getStringSet("notification_array", setTest);
        set.add("(" + stalling + " - " + rij + " - " + nummer + ") - " + now.getTimeInMillis());
        String location = "(" + stalling + " - " + rij + " - " + nummer + ") - " + df.format(now.getTime());
        locText.setText(location);
        preferences.edit().putInt("pref_stalling", stalling)
            .putInt("pref_rij", rij)
            .putInt("pref_nummer", nummer)
            .putStringSet("notification_array", set)
            .putBoolean("workaround_switch", workaround)
            .putString("location_string", location)
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
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        set = preferences.getStringSet("notification_array", setTest);
        arrayList = new ArrayList<>(set);
        Collections.sort(arrayList, new StringComparator());
        for (int i = 0; i < arrayList.size(); i++) {
            String[] entryParts = arrayList.get(i).split(" - ");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(entryParts[3]));
            arrayList.set(i, entryParts[0] + " - " + entryParts[1] + " - " + entryParts[2] + " - " + df.format(calendar.getTime()));
        }
        adapterNotification = new ArrayAdapter<>(context, R.layout.list_item, android.R.id.text1, arrayList);
        notificationList.setAdapter(adapterNotification);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme_dark);
        alertBuilder.setMessage("Do you want to delete this item?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boolean workaround = preferences.getBoolean("workaround_switch", false);
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
