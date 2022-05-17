package com.arad_itc.notify.core.app.presenter.amq.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.AudioStruct;
import com.arad_itc.notify.core.amq.domain.entities.ContactStruct;
import com.arad_itc.notify.core.amq.domain.entities.DocumentStruct;
import com.arad_itc.notify.core.amq.domain.entities.ImageStruct;
import com.arad_itc.notify.core.amq.domain.entities.LocationStruct;
import com.arad_itc.notify.core.amq.domain.entities.TextStruct;
import com.arad_itc.notify.core.amq.domain.entities.TransactionStruct;
import com.arad_itc.notify.core.amq.domain.entities.VideoStruct;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusMessageEvent;
import com.arad_itc.notify.core.app.presenter.databinding.ItemMessageBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
  final Context _context;
  private List<AMQMessage> _messages;

  public MessageListAdapter(Context context, List<AMQMessage> messages) {
    _context = context;
    _messages = messages;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    AMQMessage message = _messages.get(position);
    holder.bind(_context, message);
  }

  @Override
  public int getItemCount() {
    return _messages == null ? 0 : _messages.size();
  }

  public List<AMQMessage> getMessages() {
    return _messages;
  }

  public void updateItems(List<AMQMessage> messages) {
    _messages = messages;
    notifyDataSetChanged();
  }

  @SuppressLint("NotifyDataSetChanged")
  public void addMessage(AMQMessage message) {
    if (_messages == null) _messages = new ArrayList<>();

    if (!_messages.contains(message))
      _messages.add(message);

    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ItemMessageBinding _binding;

    public ViewHolder(ItemMessageBinding binding) {
      super(binding.getRoot());
      _binding = binding;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void bind(Context context, AMQMessage message) {
      if (message == null || message.getType() == null) return;
      _binding.llayContainer.removeAllViews();
      switch (message.getType()) {
        case Audio:
          AudioStruct audio = (AudioStruct) message.getMessage();
          _binding.llayContainer.addView(MessageListUiBuilder.buildAudioUi(context, audio));
          break;
        case Contact:
          ContactStruct contact = (ContactStruct) message.getMessage();
          _binding.llayContainer.addView(MessageListUiBuilder.buildContactUi(context, contact));
          break;
        case Image:
          try {
            ImageStruct image = (ImageStruct) message.getMessage();
            _binding.llayContainer.addView(MessageListUiBuilder.buildImageUi(context, image));
          } catch (Exception e) {
            Log.e("PoliteCoder", "bind: ", e);
          }
          break;
        case Custom:
          break;
        case Document:
          DocumentStruct document = (DocumentStruct) message.getMessage();
          _binding.llayContainer.addView(MessageListUiBuilder.buildDocumentUi(context, document));
          break;
        case Location:
          LocationStruct location = (LocationStruct) message.getMessage();
          _binding.llayContainer.addView(MessageListUiBuilder.buildLocationUi(context, location));
          break;
        case Text:
          try {
            TextStruct text = (TextStruct) message.getMessage();
            _binding.llayContainer.addView(MessageListUiBuilder.buildTextUi(context, text));
          } catch (Exception e) {
            Log.e("PoliteCoder", "bind: ", e);
          }
          break;
        case Transaction:
          TransactionStruct transaction = (TransactionStruct) message.getMessage();
          _binding.llayContainer.addView(MessageListUiBuilder.buildTransactionUi(context, transaction));
          break;
        case Video:
          VideoStruct video = (VideoStruct) message.getMessage();
          _binding.llayContainer.addView(MessageListUiBuilder.buildVideoUi(context, video));
          break;
      }

      _binding.llayContainer.setOnLongClickListener(v -> {
        RXBus.publish(new RxBusMessageEvent.OnMessageLongClicked(message));
        return false;
      });

    }
  }
}
