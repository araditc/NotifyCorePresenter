package com.arad_itc.notify.core.app.presenter.amq.presentation.manager;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.app.presenter.amq.domain.entities.FilterMessage;

import java.util.List;

public abstract class RxBusOnFilterMessageEvent {
  public static class OnFilterMessageClicked {
    private final FilterMessage filterMessage;

    public OnFilterMessageClicked(FilterMessage filterMessage) {
      this.filterMessage = filterMessage;
    }

    public FilterMessage getFilterMessage() {
      return filterMessage;
    }
  }

  public static class OnSearchClicked {
    final List<AMQMessage> _messages;
    final List<FilterMessage> _filterItems;

    public OnSearchClicked(List<AMQMessage> messages, List<FilterMessage> filterItems) {
      _messages = messages;
      _filterItems = filterItems;
    }

    public List<AMQMessage> get_messages() {
      return _messages;
    }

    public List<FilterMessage> get_filterItems() {
      return _filterItems;
    }
  }
}
