package com.arad_itc.notify.core.app.presenter.amq.presentation.manager;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;

public abstract class RxBusMessageEvent {
  public static class OnMessageLongClicked {
    private final AMQMessage message;

    public OnMessageLongClicked(AMQMessage message) {
      this.message = message;
    }

    public AMQMessage getMessage() {
      return message;
    }
  }

  public static class OnMessageArrived {
    private final AMQMessage message;

    public OnMessageArrived(AMQMessage message) {
      this.message = message;
    }

    public AMQMessage getMessage() {
      return message;
    }
  }

  public static class OnConnectionStatusChanged {
    private final ConnectionStatus connectionStatus;

    public OnConnectionStatusChanged(ConnectionStatus connectionStatus) {
      this.connectionStatus = connectionStatus;
    }

    public ConnectionStatus getConnectionStatus() {
      return connectionStatus;
    }
  }
}
