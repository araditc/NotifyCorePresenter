package com.arad_itc.notify.core.presenter.amq.domain.entities;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class AMQMessagePayload {
   @Id
   public long id;

   String payload;

   public AMQMessagePayload() {
   }

   public AMQMessagePayload(String payload) {
      this.payload = payload;
   }

   public void setPayload(String payload) {
      this.payload = payload;
   }

   public String getPayload() {
      return payload;
   }
}
