package com.arad_itc.notify.core.app.presenter.amq.presentation.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.app.presenter.R;
import com.arad_itc.notify.core.app.presenter.amq.domain.entities.FilterMessage;
import com.arad_itc.notify.core.app.presenter.amq.presentation.adapters.FilterMessageListAdapter;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusOnFilterMessageEvent;
import com.arad_itc.notify.core.app.presenter.databinding.SearchMessagesBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;
import com.sardari.daterangepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.disposables.Disposable;

public class SearchMessagesBottomSheet extends BottomSheetDialogFragment {
  public static final String Tag = SearchMessagesBottomSheet.class.getSimpleName();
  final Context _context;

  final List<AMQMessage> _messages;
  List<AMQMessage> _finalMessageList;

  final List<FilterMessage> _filterItems;

  public SearchMessagesBottomSheet(Context context, List<AMQMessage> messages, List<FilterMessage> filterItems) {
    _context = context;

    if (messages == null)
      messages = new ArrayList<>();
    _messages = messages;

    if (filterItems == null)
      filterItems = new ArrayList<>();
    _filterItems = filterItems;

    buildMessages();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
  }

  SearchMessagesBottomSheetBinding _binding;
  FilterMessageListAdapter _filterMessageAdapter;

  Disposable onFilterMessageClickedSuscription;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    _binding = SearchMessagesBottomSheetBinding.inflate(inflater);

    LinearLayoutManager layoutManager = new LinearLayoutManager(_context, RecyclerView.HORIZONTAL, false);
    _binding.rclvMessageTypes.setLayoutManager(layoutManager);

    _filterMessageAdapter = new FilterMessageListAdapter(_context, _filterItems);
    _binding.rclvMessageTypes.setAdapter(_filterMessageAdapter);

    _binding.txfRangeDate.getEditText().setOnClickListener(v -> {
      showRangeDatePicker();
    });

    onFilterMessageClickedSuscription = RXBus
      .listen(RxBusOnFilterMessageEvent.OnFilterMessageClicked.class)
      .subscribe(o -> {

        Log.i("TAG", "onCreateView, RxBus: RxBusOnFilterMessageEvent.OnFilterMessageClicked fired");

        FilterMessage filterMessage = ((RxBusOnFilterMessageEvent.OnFilterMessageClicked) o).getFilterMessage();
        int index = _filterItems.indexOf(filterMessage);
        filterMessage.isSelected = !filterMessage.isSelected;
        _filterItems.set(index, filterMessage);

        _filterMessageAdapter.updateItems(_filterItems);
        buildMessages();
      }, throwable -> {
        Log.e(Tag, "buildFilter: " + throwable.toString());
      });

    _binding.btnSearch.setOnClickListener(v -> {

      if (_startDate != null && _endDate != null) {
        List<AMQMessage> msgs = new ArrayList<>();
        for (AMQMessage msg : _finalMessageList) {
          if (msg.getTimestamp().after(_startDate) && msg.getTimestamp().before(_endDate))
            msgs.add(msg);
        }
        _finalMessageList = msgs;
      }

      RXBus.publish(new RxBusOnFilterMessageEvent.OnSearchClicked(_finalMessageList, _filterItems));
      this.dismiss();
    });

    return _binding.getRoot();
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {
    onFilterMessageClickedSuscription.dispose();
    super.onDismiss(dialog);
  }

  private void buildMessages() {
    boolean hasAnyItemsSelected = false;

    List<AMQMessage> filteredMessages = new ArrayList<>();
    for (AMQMessage message : _messages) {
      for (FilterMessage filterItem : _filterItems) {
        if (filterItem.isSelected) {
          if (message.getType() == filterItem.getMessageType())
            filteredMessages.add(message);
        }
      }
    }

    for (FilterMessage filterItem : _filterItems) {
      if (!filterItem.isSelected)
        continue;

      hasAnyItemsSelected = true;
      break;
    }

    if (!hasAnyItemsSelected)
      _finalMessageList = _messages;
    else
      _finalMessageList = filteredMessages;
  }


  Date _startDate;
  Date _endDate;

  void showRangeDatePicker() {
    DatePickerDialog datePickerDialog = new DatePickerDialog(_context);
    datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Range);
    datePickerDialog.setTextSizeTitle(10.0f);
    datePickerDialog.setTextSizeWeek(12.0f);
    datePickerDialog.setTextSizeDate(14.0f);
    datePickerDialog.setCanceledOnTouchOutside(true);
    datePickerDialog.setDisableDaysAgo(false);
    datePickerDialog.setOnRangeDateSelectedListener(new DatePickerDialog.OnRangeDateSelectedListener() {
      @Override
      public void onRangeDateSelected(PersianCalendar startDate, PersianCalendar endDate) {
        _startDate = startDate.getTime();
        _endDate = endDate.getTime();

        _binding.txfRangeDate.getEditText()
          .setText(String.format("%s - %s",
            startDate.getPersianShortDate(),
            endDate.getPersianShortDate()));
      }
    });

    datePickerDialog.showDialog();
  }


}
