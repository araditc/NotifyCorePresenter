package com.arad_itc.notify.core.app.presenter.amq.domain.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arad_itc.notify.core.amq.domain.receivers.AmqMessageArrivedListener;
import com.arad_itc.notify.core.app.presenter.amq.data.datastores.ObjectBox;
import com.arad_itc.notify.core.app.presenter.amq.domain.entities.AMQMessagePayload;

import io.objectbox.Box;

public class AmqMessageService extends AmqMessageArrivedListener {
  final String TAG = "Notification";

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (intent.getAction().equals("com.arad_itc.notify.amq.messageReceived")) {
      String message = intent.getStringExtra("com.arad_itc.notify.amq.message_arrived");

      Log.i(TAG, "AmqMessageService onReceive, message is: " + message);
      Box<AMQMessagePayload> messageBox = ObjectBox.get().boxFor(AMQMessagePayload.class);
      messageBox.put(new AMQMessagePayload(message));
    }
  }
}

