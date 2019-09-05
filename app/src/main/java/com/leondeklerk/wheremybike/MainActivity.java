package com.leondeklerk.wheremybike;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@SuppressWarnings("StringBufferReplaceableByString")
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
    AdapterView.OnItemClickListener,
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

  public ArrayAdapter<String> adapterNotification;
  Button button;

  MarkerOptions markerOptions;
  Marker marker;
  float lat;
  float lng;
  Button btnSet;
  EditText edTxtS;
  EditText edTxtR;
  EditText edTxtN;
  SharedPreferences preferences;
  ListView notificationList;
  List<String> arrayList;
  Set<String> set;
  Set<String> setTest = new HashSet<>();
  boolean dark;
  private GoogleMap map;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    dark = preferences.getBoolean("dark", true);

    if (dark) {
      setTheme(R.style.Dark);
    } else {
      setTheme(R.style.Light);
    }

    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

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

    button = findViewById(R.id.set_location_button);

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    //noinspection ConstantConditions
    mapFragment.getMapAsync(this);
    lat = preferences.getFloat("lat", 0);
    lng = preferences.getFloat("lng", 0);
    markerOptions = new MarkerOptions().position(new LatLng(lat, lng));
    if (lat == 0 || lng == 0) {
      markerOptions.visible(false);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.theme_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.theme_menu) {
      preferences.edit().putBoolean("dark", !dark).apply();
      recreate();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View view) {
    Calendar now = Calendar.getInstance();
    int stalling = getSetValue(edTxtS);
    int rij = getSetValue(edTxtR);
    int nummer = getSetValue(edTxtN);
    boolean workaround = preferences.getBoolean("workaround_switch", false);
    workaround = !workaround;
    set = preferences.getStringSet("notification_array", setTest);
    set.add("(" + stalling + " - " + rij + " - " + nummer + ") - " + now.getTimeInMillis());
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
    String location = new StringBuilder()
        .append("(")
        .append(stalling)
        .append(" - ")
        .append(rij)
        .append(" - ")
        .append(nummer)
        .append(") - ")
        .append(df.format(now.getTime()))
        .toString();
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

  /**
   * Method to get the value of an particular String preference.
   *
   * @param prefTag the tag associated with the preference.
   * @return return the value of the preference.
   */
  private String getValue(String prefTag) {
    return String.valueOf(preferences.getInt(prefTag, 1));
  }

  /**
   * Method to extract the input value from a given EditText.
   *
   * @param editText field to extract the input from.
   * @return the user input from the field.
   */
  private int getSetValue(EditText editText) {
    if ((editText.getText().toString().equals(""))) {
      editText.setText(String.valueOf(1));
      return 1;
    }

    return Integer.parseInt(editText.getText().toString());
  }

  /**
   * Process, sort the date for the list and populate the ListView.
   *
   * @param context the current context.
   */
  private void setList(final Context context) {
    set = preferences.getStringSet("notification_array", setTest);
    arrayList = new ArrayList<>(set);
    Collections.sort(arrayList, new StringComparator());
    for (int i = 0; i < arrayList.size(); i++) {
      String[] entryParts = arrayList.get(i).split(" - ");
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(Long.parseLong(entryParts[3]));
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
      arrayList.set(i, new StringBuilder()
          .append(entryParts[0])
          .append(" - ")
          .append(entryParts[1])
          .append(" - ")
          .append(entryParts[2])
          .append(" - ")
          .append(df.format(calendar.getTime()))
          .toString());
    }

      int layout;

    if (dark) {
        layout = R.layout.list_item_dark;

    } else {
        layout = R.layout.list_item_light;

    }
      adapterNotification = new ArrayAdapter<>(context,
          layout,
          android.R.id.text1,
          arrayList);
    notificationList.setAdapter(adapterNotification);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
    alertBuilder.setMessage(R.string.dialog_remove_item_text).setPositiveButton(R.string.dialog_ok,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            boolean workaround = preferences.getBoolean("workaround_switch", false);
            workaround = !workaround;
            set = preferences.getStringSet("notification_array", setTest);
            arrayList = new ArrayList<>(set);
            Collections.sort(arrayList, new StringComparator());
            Log.i("index", String.valueOf(i));
            arrayList.remove(i);
            set = new HashSet<>(arrayList);
            preferences.edit()
                .putStringSet("notification_array", set)
                .putBoolean("workaround_switch", workaround)
                .apply();
            setList(getApplicationContext());
          }
        });
    alertBuilder.show();
  }

  @Override
  public void onMapReady(GoogleMap map) {
    this.map = map;
    if (lat == 0 || lng == 0) {
      this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.1326f, 5.2913f), 6f));
    } else {
      this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 19f));
    }

    marker = this.map.addMarker(markerOptions);
    this.map.getUiSettings().setMyLocationButtonEnabled(false);
    enableMyLocation();
  }

  /**
   * Enables the My Location layer if the fine location permission has been granted.
   */
  private void enableMyLocation() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // Permission to access the location is missing.
      PermissionUtils.requestPermission(this);
      button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Snackbar snackbar = Snackbar.make(v.getRootView(), R.string.no_location_snack,
              Snackbar.LENGTH_LONG);
          View view = snackbar.getView();
          TextView text = view.findViewById(com.google.android.material.R.id.snackbar_text);
          text.setTextColor(getResources().getColor(R.color.white, null));
          snackbar.show();
        }
      });
    } else if (map != null) {
      map.setMyLocationEnabled(true);
      button.setOnClickListener(new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View v) {
          Location location = map.getMyLocation();
          if (location != null) {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            marker.setVisible(true);
            lat = (float) location.getLatitude();
            lng = (float) location.getLongitude();
            preferences.edit().putFloat("lat", lat).putFloat("lng", lng).apply();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 19f));
          } else {
            Snackbar snackbar = Snackbar.make(v.getRootView(), R.string.wait_location_found_snack,
                Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            TextView text = view.findViewById(com.google.android.material.R.id.snackbar_text);
            text.setTextColor(getResources().getColor(R.color.white, null));
            snackbar.show();
          }
        }
      });
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != 1) {
      return;
    }

    if (PermissionUtils.isPermissionGranted(permissions, grantResults
    )) {
      enableMyLocation();
    }
  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
  }
}