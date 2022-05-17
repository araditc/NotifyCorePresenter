package com.arad_itc.notify.core.presenter.amq.presentation;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;

public interface OnBrokerListener {
  void onConnectionStatus(ConnectionStatus var1);

  void onMessageArrived(AMQMessage var1);
}