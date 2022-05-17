package com.arad_itc.notify.core.presenter.amq.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arad_itc.notify.core.presenter.R;
import com.arad_itc.notify.core.presenter.amq.domain.entities.FilterMessage;
import com.arad_itc.notify.core.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.presenter.amq.presentation.manager.RxBusOnFilterMessageEvent;
import com.arad_itc.notify.core.presenter.databinding.ItemFilterMessageBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilterMessageListAdapter extends RecyclerView.Adapter<FilterMessageListAdapter.ViewHolder> {

  final Context _context;
  List<FilterMessage> _filterMessages;

  public FilterMessageListAdapter(Context context, List<FilterMessage> filterMessages) {
    _context = context;
    _filterMessages = filterMessages;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(ItemFilterMessageBinding.bind(LayoutInflater.from(_context).inflate(R.layout.item_filter_message, parent, false)));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(_context, _filterMessages.get(position));
  }

  @Override
  public int getItemCount() {
    return _filterMessages == null ? 0 : _filterMessages.size();
  }

  @SuppressLint("NotifyDataSetChanged")
  public void updateItems(List<FilterMessage> messages) {
    _filterMessages = messages;
    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ItemFilterMessageBinding _binding;

    public ViewHolder(@NonNull ItemFilterMessageBinding binding) {
      super(binding.getRoot());
      _binding = binding;
    }

    public void bind(Context context, FilterMessage filterMessage) {
      try {
        int titleIdentifire = context.getResources().getIdentifier(filterMessage.getMessageType().getName(), "string", context.getPackageName());
        _binding.btnFilter.setText(context.getString(titleIdentifire));
      } catch (Exception exception) {
        _binding.btnFilter.setText(filterMessage.getMessageType().getName());
      }

      _binding.btnDeleteFilter.setVisibility(filterMessage.isSelected ? View.VISIBLE : View.GONE);

      _binding.btnFilter.setOnClickListener(v -> {
        RXBus.publish(new RxBusOnFilterMessageEvent.OnFilterMessageClicked(filterMessage));
      });

//      _binding.btnDeleteFilter.setOnClickListener(v -> {
//        RXBus.publish(new RxBusOnFilterMessageEvent.OnFilterMessageClicked(filterMessage));
//      });

    }
  }
}
