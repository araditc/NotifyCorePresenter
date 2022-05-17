package com.arad_itc.notify.core.presenter.amq.presentation.activities;

import android.os.Bundle;
import android.util.Log;


import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;
import com.arad_itc.notify.core.presenter.R;
import com.arad_itc.notify.core.presenter.amq.data.datastores.ObjectBox;
import com.arad_itc.notify.core.presenter.amq.domain.entities.AMQMessagePayload;
import com.arad_itc.notify.core.presenter.amq.presentation.adapters.MessageListAdapter;
import com.arad_itc.notify.core.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.presenter.amq.presentation.manager.RxBusMessageEvent;
import com.arad_itc.notify.core.presenter.databinding.ActivityMessagesListBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.objectbox.Box;

public class MessagesListActivity extends AppCompatActivity {
  private final String TAG = "MessagesListActivity";
  ActivityMessagesListBinding _binding;
  List<AMQMessage> _messages = new ArrayList<>();
  MessageListAdapter _adapter;

  @Override
  protected void onStart() {
    super.onStart();
    getMessagesFromLocalStorage();
    buildRclv();
    setupMessageBrokerListeners();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _binding = ActivityMessagesListBinding.inflate(getLayoutInflater());
    setContentView(_binding.getRoot());
  }

  private void setupMessageBrokerListeners() {


    RXBus.listen(RxBusMessageEvent.OnMessageLongClicked.class).subscribe(o -> {
      AMQMessage message = ((RxBusMessageEvent.OnMessageLongClicked) o).getMessage();
      Log.i(TAG, "setupMessageBrokerListeners: RxBusMessageEvent.OnMessageLongClicked");
      new AlertDialog.Builder(this)
        .setTitle(getString(R.string.dialog_delete_title))
        .setMessage(getString(R.string.dialog_delete_message))
        .setPositiveButton(getString(R.string.dialog_delete_pos_btn), (dialogInterface, i) -> {
          _messages.remove(message);
          _adapter.updateItems(_messages);
        })
        .setNegativeButton(getString(R.string.dialog_delete_neg_btn), (dialogInterface, i) -> {
          dialogInterface.dismiss();
        })
        .create().show();
    }, throwable -> {
      Log.e(TAG, "setupMessageBrokerListeners: " + throwable);
    });

    RXBus.listen(RxBusMessageEvent.OnMessageArrived.class).subscribe(msg -> {
      if (msg == null) return;
      AMQMessage message = ((RxBusMessageEvent.OnMessageArrived) msg).getMessage();
      Log.i(TAG, "message: " + message);
      _adapter.addMessage(message);
      _binding.rclvMessages.scrollToPosition(_adapter.getItemCount() - 1);
    }, error -> {
      Log.e(TAG, "error: " + error.toString());
    });


    RXBus.listen(RxBusMessageEvent.OnConnectionStatusChanged.class).subscribe(stts -> {
      if (stts == null) return;
      ConnectionStatus status = ((RxBusMessageEvent.OnConnectionStatusChanged) stts).getConnectionStatus();
      Log.i(TAG, "status: " + status);
    }, error -> {
      Log.e(TAG, "error: " + error.toString());
    });
  }

  private void getMessagesFromLocalStorage() {
    _messages.clear();
    Box<AMQMessagePayload> messageBox = ObjectBox.get().boxFor(AMQMessagePayload.class);
    for (AMQMessagePayload message : messageBox.getAll()) {
      AMQMessage msg = new AMQMessage(this, null, message.getPayload());
      _messages.add(msg);
    }
  }

  private void buildRclv() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setReverseLayout(true);
    layoutManager.setStackFromEnd(true);
    _binding.rclvMessages.setLayoutManager(layoutManager);
    _adapter = new MessageListAdapter(this, _messages);
    _binding.rclvMessages.setAdapter(_adapter);
    _binding.rclvMessages.scrollToPosition(_messages.size() - 1);
    _binding.rclvMessages.addOnLayoutChangeListener(
      (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
        if (bottom < oldBottom) {
          if (_adapter.getItemCount() <= 0) {
            _binding.rclvMessages.postDelayed(() -> {
              _binding.rclvMessages.smoothScrollToPosition(_adapter.getItemCount());
            }, 100);
          } else {
            _binding.rclvMessages.postDelayed(() -> {
              _binding.rclvMessages.smoothScrollToPosition(_adapter.getItemCount() - 1);
            }, 100);
          }
        }
      });
  }
}