package com.arad_itc.notify.core.app.presenter;

import android.app.Application;
import android.content.IntentFilter;
import android.util.Log;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;
import com.arad_itc.notify.core.amq.domain.repositories.OnAradBrokerListener;
import com.arad_itc.notify.core.amq.presentation.AradBroker;
import com.arad_itc.notify.core.app.presenter.amq.data.datastores.ObjectBox;
import com.arad_itc.notify.core.app.presenter.amq.domain.entities.AMQMessagePayload;
import com.arad_itc.notify.core.app.presenter.amq.domain.receivers.AmqMessageService;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusMessageEvent;
import com.arad_itc.notify.core.notification.AradPushNotificationManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import io.objectbox.Box;

public class App extends Application {

  public static AradBroker connection;

  @Override
  public void onCreate() {
    super.onCreate();

    FirebaseApp.initializeApp(this);

    AmqMessageService amqMessageService = new AmqMessageService();
    this.registerReceiver(amqMessageService, new IntentFilter("com.arad_itc.notify.amq.messageReceived"));

    createConnection();

  }

  void createConnection() {
    FirebaseMessaging.getInstance().getToken()
      .addOnCompleteListener(new OnCompleteListener<String>() {
        @Override
        public void onComplete(@NonNull Task<String> task) {
          if (!task.isSuccessful()) {
            Log.w("TAG", "FCM registration token failed", task.getException());
            return;
          }
          // Get new FCM registration token
          String token = task.getResult();
          Log.i("AradBroker", "onComplete, token: " + token);
          connection = AradBroker.getInstance(getApplicationContext())
            .setAppId("626d0d549c4704dcf2258b46")
            .setCredential("989135646251", "u68p@K$$Q@P8$Z$y$1G7g4q1@j@fuh1T")
            .setUseTls(false)
            .setHost("192.168.100.55", 1883)
            .setPreferredKey("9IDiF8moM8oNhxZE+5D94g==")
            .setDeviceToken(token)
            .setOnBrokerListener(new OnAradBrokerListener() {
              @Override
              public void onConnectionStatus(ConnectionStatus status) {
                RXBus.publish(new RxBusMessageEvent.OnConnectionStatusChanged(status));
              }

              @Override
              public void onMessageArrived(AMQMessage message) {
                RXBus.publish(new RxBusMessageEvent.OnMessageArrived(message));
              }
            })
            .connect();
        }
      });
  }
}
