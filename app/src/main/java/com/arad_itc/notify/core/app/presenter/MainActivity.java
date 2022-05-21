package com.arad_itc.notify.core.app.presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;
import com.arad_itc.notify.core.amq.domain.repositories.OnAradBrokerListener;
import com.arad_itc.notify.core.amq.presentation.AradBroker;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusMessageEvent;
import com.arad_itc.notify.core.notification.AradPushNotificationManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


  @Override
  protected void onStart() {
    super.onStart();
    setupConnectionListener();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


  }

  private void setupConnectionListener() {
    RXBus.listen(RxBusMessageEvent.OnConnectionStatusChanged.class).subscribe(
      o -> {
        RxBusMessageEvent.OnConnectionStatusChanged messageEvent = (RxBusMessageEvent.OnConnectionStatusChanged) o;
        getSupportActionBar().setTitle(messageEvent.getConnectionStatus().name());
      }
    );
  }
}