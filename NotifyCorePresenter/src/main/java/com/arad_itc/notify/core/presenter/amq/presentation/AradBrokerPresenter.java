package com.arad_itc.notify.core.presenter.amq.presentation;

import android.content.Context;
import android.util.Log;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;
import com.arad_itc.notify.core.amq.domain.repositories.OnAradBrokerListener;
import com.arad_itc.notify.core.amq.presentation.AradBroker;
import com.arad_itc.notify.core.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.presenter.amq.presentation.manager.RxBusMessageEvent;
import com.google.firebase.FirebaseApp;

public class AradBrokerPresenter {

  Context _context;
  static AradBrokerPresenter _instance = null;

  public static AradBrokerPresenter getInstance(Context context) {
    if (_instance == null) _instance = new AradBrokerPresenter(context);
    return _instance;
  }

  public AradBrokerPresenter(Context _context) {
    this._context = _context;
  }

  private String appId = "";
  private String username = "";
  private String password = "";
  private String prefaredKey = "";
  private boolean useTls = false;
  private String host;
  private int port = 1883;
  private OnAradBrokerListener onAradBrokerListener;

  public AradBrokerPresenter setAppId(String appId) {
    this.appId = appId;
    return this;
  }

  public AradBrokerPresenter setCredential(String username, String password) {
    this.username = username;
    this.password = password;
    return this;
  }

  public AradBrokerPresenter setPreferredKey(String prefaredKey) {
    this.prefaredKey = prefaredKey;
    return this;
  }

  public AradBrokerPresenter setUseTls(boolean useTls) {
    this.useTls = useTls;
    return this;
  }

  public AradBrokerPresenter setHost(String host, int port) {
    this.host = host;
    this.port = port;
    return this;
  }

  public AradBrokerPresenter setOnBrokerListener(OnAradBrokerListener onAradBrokerListener) {
    this.onAradBrokerListener = onAradBrokerListener;
    return this;
  }

  public AradBrokerPresenter connect() {
    AradBroker connection = AradBroker.getInstance(_context)
      .setAppId(appId)
      .setCredential(username, password)
      .setUseTls(useTls)
      .setHost(host, port)
      .setPreferredKey(prefaredKey)
      .setOnBrokerListener(new OnAradBrokerListener() {
        @Override
        public void onConnectionStatus(ConnectionStatus connectionStatus) {
          Log.i("TAG", "onConnectionStatus, status: " + connectionStatus);
          RXBus.publish(new RxBusMessageEvent.OnConnectionStatusChanged(connectionStatus));
        }

        @Override
        public void onMessageArrived(AMQMessage amqMessage) {
          RXBus.publish(new RxBusMessageEvent.OnMessageArrived(amqMessage));
        }
      })
      .connect();
    return this;
  }

}
