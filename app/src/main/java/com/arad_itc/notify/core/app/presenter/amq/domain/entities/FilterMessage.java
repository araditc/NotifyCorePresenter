package com.arad_itc.notify.core.app.presenter.amq.domain.entities;

import com.arad_itc.notify.core.amq.domain.entities.MessageType;

public class FilterMessage {
  final MessageType messageType;
  public boolean isSelected = false;

  public FilterMessage(MessageType messageType) {
    this.messageType = messageType;
  }

  public MessageType getMessageType() {
    return messageType;
  }
}
