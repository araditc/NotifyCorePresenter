package com.arad_itc.notify.core.app.presenter.amq.data.datastores;

import android.content.Context;

import com.arad_itc.notify.core.app.presenter.amq.domain.entities.MyObjectBox;

import io.objectbox.BoxStore;

public class ObjectBox {
   private static BoxStore boxStore;

   public static void init(Context context) {
      if (boxStore == null)
         boxStore = MyObjectBox.builder()
           .androidContext(context.getApplicationContext())
           .build();
   }

   public static BoxStore get() {
      return boxStore;
   }
}
