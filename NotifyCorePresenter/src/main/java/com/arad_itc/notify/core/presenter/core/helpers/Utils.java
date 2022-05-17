

/*
 * Copyright 2013 Square, Inc.
 *
 * Licensed under the Arad License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://arad-itc.com/%D8%AF%D8%B1%D8%A8%D8%A7%D8%B1%D9%87-%D9%85%D8%A7/%DA%AF%D9%88%D8%A7%D9%87%DB%8C%D9%86%D8%A7%D9%85%D9%87-%D8%A7%DB%8C%D8%B2%D9%88-90012008
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arad_itc.notify.core.presenter.core.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;

import java.util.Locale;

public class Utils {
  public static void runOnUIThread(Looper looper, Runnable runnable, long delay) {
    Handler applicationHandler = new Handler(looper);

    if (delay == 0) {
      applicationHandler.post(runnable);
    } else {
      applicationHandler.postDelayed(runnable, delay);
    }
  }

  public static String getStaticImageUrl(double lat, double lng) {
    return String.format(new Locale("en"),"https://static-maps.yandex.ru/1.x/?lang=en_US&ll=%.10f,%.10f&z=12&l=map&pt=%.10f,%.10f,vkbkm&size=600,250", lat, lng, lat, lng);
  }

  public static void showMap(Context context, double lat, double lng) {
    Intent intent = new Intent(Intent.ACTION_VIEW,
      Uri.parse(String.format(new Locale("en"),"geo:0,0?q=%.10f,%.10f()",lat,lng)));
//      Uri.parse("geo:0,0?q=" + lat + "," + lng + " ( )"));
    context.startActivity(intent);
  }

  public static String milliSecondsToTimer(long milliseconds) {
    String finalTimerString = "";
    String secondsString = "";

    // Convert total duration into time
    int hours = (int) (milliseconds / (1000 * 60 * 60));
    int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
    int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
    // Add hours if there
    if (hours > 0) {
      finalTimerString = hours + ":";
    }

    // Prepending 0 to seconds if it is one digit
    if (seconds < 10) {
      secondsString = "0" + seconds;
    } else {
      secondsString = "" + seconds;
    }

    finalTimerString = finalTimerString + minutes + ":" + secondsString;

    // return timer string
    return finalTimerString;
  }

  public static float dpToPx(Context context,int dip){
    Resources r = context.getResources();
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      dip,
      r.getDisplayMetrics()
    );
  }

  public static String numE2P(String str, boolean reverce) {
    String[][] chars = new String[][]{
      {"0", "۰"},
      {"1", "۱"},
      {"2", "۲"},
      {"3", "۳"},
      {"4", "۴"},
      {"5", "۵"},
      {"6", "۶"},
      {"7", "۷"},
      {"8", "۸"},
      {"9", "۹"}
    };

    for (String[] num : chars) {
      if (reverce) {
        str = str.replace(num[1], num[0]);
      } else {
        str = str.replace(num[0], num[1]);
      }
    }
    return str;
  }
}
