package com.arad_itc.notify.core.app.presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;
import com.arad_itc.notify.core.amq.domain.repositories.OnAradBrokerListener;
import com.arad_itc.notify.core.amq.presentation.AradBroker;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusMessageEvent;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


//    AradBroker.getInstance(this)
//      .setAppId("626d0d549c4704dcf2258b46")
//      .setCredential("989135646251", "i1@bBW?$9v$??16$2Gg8BJ9$jr?z956Q")
//      .setUseTls(false)
//      .setHost("192.168.100.55", 1883)
//      .setPreferredKey("AMplKJBBBLnskVfJEcW8Ww==")
//
//      .setOnBrokerListener(new OnAradBrokerListener() {
//        @Override
//        public void onConnectionStatus(ConnectionStatus connectionStatus) {
//          RXBus.publish(new RxBusMessageEvent.OnConnectionStatusChanged(connectionStatus));
//        }
//
//        @Override
//        public void onMessageArrived(AMQMessage amqMessage) {
//          RXBus.publish(new RxBusMessageEvent.OnMessageArrived(amqMessage));
//        }
//      })
//      .connect();

    setContentView(R.layout.activity_main);


  }
}