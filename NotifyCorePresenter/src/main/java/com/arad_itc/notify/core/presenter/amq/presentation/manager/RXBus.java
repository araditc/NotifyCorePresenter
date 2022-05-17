package com.arad_itc.notify.core.presenter.amq.presentation.manager;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RXBus{
  private static final PublishSubject<Object> publisher = PublishSubject.create();

  public static void publish(Object event) {
    publisher.onNext(event);
  }

  public static Observable listen(Class eventType) {
    return publisher.ofType(eventType);
  }
}
