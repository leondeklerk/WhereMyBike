/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leondeklerk.wheremybike;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Utility class for access to runtime permissions.
 */
abstract class PermissionUtils {

  /**
   * Shows the user a request screen with some explanation asking
   * for permission of its fine location,
   * to enable the "save my location" functionality.
   *
   * @param activity the Activity that the request came from.
   */
  static void requestPermission(final AppCompatActivity activity) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
      ActivityCompat.requestPermissions(activity, new String[] {
          android.Manifest.permission.ACCESS_FINE_LOCATION
      }, 1);
    } else {
      new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
          .setMessage(R.string.permission_location)
          .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              ActivityCompat.requestPermissions(activity, new String[] {
                  android.Manifest.permission.ACCESS_FINE_LOCATION
              }, 1);
            }
          })
          .setNegativeButton(R.string.dialog_cancel, null)
          .create().show();
    }
  }

  /**
   * Check if a permission is granted to then use it in the application.
   *
   * @param grantPermissions the permissions to grant
   * @param grantResults     the value of the grant.
   * @return where or not the permission has been granted.
   */
  static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults) {
    for (int i = 0; i < grantPermissions.length; i++) {
      if (android.Manifest.permission.ACCESS_FINE_LOCATION.equals(grantPermissions[i])) {
        return grantResults[i] == PackageManager.PERMISSION_GRANTED;
      }
    }
    return false;
  }
}
