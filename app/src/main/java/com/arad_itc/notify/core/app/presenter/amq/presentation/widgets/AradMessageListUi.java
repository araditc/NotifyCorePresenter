package com.arad_itc.notify.core.app.presenter.amq.presentation.widgets;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;
import com.arad_itc.notify.core.amq.domain.entities.ConnectionStatus;
import com.arad_itc.notify.core.amq.domain.entities.MessageType;
import com.arad_itc.notify.core.amq.domain.repositories.OnAradBrokerListener;
import com.arad_itc.notify.core.amq.presentation.AradBroker;
import com.arad_itc.notify.core.app.presenter.R;
import com.arad_itc.notify.core.app.presenter.amq.data.datastores.ObjectBox;
import com.arad_itc.notify.core.app.presenter.amq.domain.entities.AMQMessagePayload;
import com.arad_itc.notify.core.app.presenter.amq.domain.entities.FilterMessage;
import com.arad_itc.notify.core.app.presenter.amq.presentation.adapters.MessageListAdapter;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RXBus;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusMessageEvent;
import com.arad_itc.notify.core.app.presenter.amq.presentation.manager.RxBusOnFilterMessageEvent;
import com.arad_itc.notify.core.app.presenter.core.helpers.ExcelUtils;
import com.arad_itc.notify.core.app.presenter.core.helpers.FileShareUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.Box;

public class AradMessageListUi extends FrameLayout {
  private String Tag = AradMessageListUi.class.getSimpleName();

  public AradMessageListUi(Context context) {
    super(context);
    initial(context);
  }

  public AradMessageListUi(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initial(context);
  }

  public AradMessageListUi(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initial(context);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public AradMessageListUi(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initial(context);
  }

  LinearLayout lLayRclvContainer;
  LinearLayout lLayFabsContainer;

  List<FilterMessage> _filterItems = new ArrayList<>();

  private void initial(Context context) {
    ObjectBox.init(context);

//    if (!AradBroker.getInstance(context).isConnected()) {
//      AradBroker.getInstance(context)
//        .setOnBrokerListener(new OnAradBrokerListener() {
//          @Override
//          public void onConnectionStatus(ConnectionStatus connectionStatus) {
//            RXBus.publish(new RxBusMessageEvent.OnConnectionStatusChanged(connectionStatus));
//          }
//
//          @Override
//          public void onMessageArrived(AMQMessage amqMessage) {
//            RXBus.publish(new RxBusMessageEvent.OnMessageArrived(amqMessage));
//          }
//        })
//        .connect();
//    }

    LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    rootLayoutParams.gravity = Gravity.BOTTOM;
    this.setLayoutParams(rootLayoutParams);

    getMessagesFromLocalStorage(context);
    buildRclv(context);

    setupMessageBrokerListeners();

    for (MessageType type : MessageType.values()) {
      _filterItems.add(new FilterMessage(type));
    }

    buildFabsUi(context);
  }

  private void buildFabsUi(Context context) {
    lLayFabsContainer = new LinearLayout(context);
    LinearLayout.LayoutParams lLayFabsContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lLayFabsContainerParams.gravity = Gravity.END;
    lLayFabsContainer.setLayoutParams(lLayFabsContainerParams);

    LinearLayout space = new LinearLayout(context);
    LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
    space.setLayoutParams(spaceLayoutParams);

    lLayFabsContainer.addView(space);

    buildSearchFabUi(context);
    buildExportFabUi(context);

    this.addView(lLayFabsContainer);
  }

  private void buildSearchFabUi(Context context) {
    FloatingActionButton fabSearch = new FloatingActionButton(context);
    fabSearch.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_search));

    LinearLayout.LayoutParams fabSearchParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    fabSearchParams.setMargins(
      (int) context.getResources().getDimension(R.dimen.view_m),
      (int) context.getResources().getDimension(R.dimen.view_m),
      (int) context.getResources().getDimension(R.dimen.view_m),
      (int) context.getResources().getDimension(R.dimen.view_m)
    );

    fabSearchParams.gravity = Gravity.BOTTOM | Gravity.END;
    fabSearch.setLayoutParams(fabSearchParams);
    fabSearch.setColorFilter(context.getResources().getColor(R.color.white));
    fabSearch.setOnClickListener(v -> {
      SearchMessagesBottomSheet searchMessagesBottomSheet = new SearchMessagesBottomSheet(context, _messages, _filterItems);
      AppCompatActivity activity = (AppCompatActivity) context;
      searchMessagesBottomSheet.show(activity.getSupportFragmentManager(), SearchMessagesBottomSheet.Tag);

      RXBus.listen(RxBusOnFilterMessageEvent.OnSearchClicked.class).subscribe(o -> {
        RxBusOnFilterMessageEvent.OnSearchClicked event = (RxBusOnFilterMessageEvent.OnSearchClicked) o;

        List<AMQMessage> msgs = event.get_messages();
        _filterItems = event.get_filterItems();
        _messagesListAdapter.updateItems(msgs);
      });
    });
    lLayFabsContainer.addView(fabSearch);
  }

  private void buildExportFabUi(Context context) {
    FloatingActionButton fabExport = new FloatingActionButton(context);
    fabExport.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_share));

    LinearLayout.LayoutParams fabSearchParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    fabSearchParams.setMargins(
      (int) context.getResources().getDimension(R.dimen.view_m),
      (int) context.getResources().getDimension(R.dimen.view_m),
      (int) context.getResources().getDimension(R.dimen.view_m),
      (int) context.getResources().getDimension(R.dimen.view_m)
    );

    fabSearchParams.gravity = Gravity.BOTTOM | Gravity.END;
    fabExport.setLayoutParams(fabSearchParams);
    fabExport.setColorFilter(context.getResources().getColor(R.color.white));
    fabExport.setOnClickListener(v -> {

      boolean isExcelGenerated = ExcelUtils.exportDataIntoWorkbook(context,
        "messages.xlsx", _messagesListAdapter.getMessages());
      Uri fileUri = FileShareUtils.accessFile(context, "messages.xlsx");
      if (fileUri != null) {
        FileShareUtils.launchShareFileIntent(context, fileUri);
      }
    });
    lLayFabsContainer.addView(fabExport);
  }

  private void getMessagesFromLocalStorage(Context context) {
    _messages.clear();
    _messages = fetchedMessage(0, context);
  }

  private boolean loading = false;
  private long previousTotal = 0;
  private long visibleThreshold = 5;

  private List<AMQMessage> fetchedMessage(long offset, Context context) {
    if (loading) return new ArrayList<>();
    loading = true;

    List<AMQMessage> messages = new ArrayList<>();
    Box<AMQMessagePayload> messageBox = ObjectBox.get().boxFor(AMQMessagePayload.class);
    for (AMQMessagePayload message : messageBox.query().build().find(offset, 25)) {
      AMQMessage msg = new AMQMessage(context, null, message.getPayload());
      messages.add(msg);
    }
    loading = false;
    return messages;
  }


  List<AMQMessage> _messages = new ArrayList<>();
  MessageListAdapter _messagesListAdapter;
  RecyclerView messagesRclv;

  void buildRclv(Context context) {
    lLayRclvContainer = new LinearLayout(context);
    LinearLayout.LayoutParams lLayRclvContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lLayRclvContainer.setLayoutParams(lLayRclvContainerParams);
    lLayRclvContainer.setOrientation(LinearLayout.VERTICAL);

    messagesRclv = new RecyclerView(context);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    messagesRclv.setLayoutParams(params);

    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    messagesRclv.setLayoutManager(layoutManager);
    _messagesListAdapter = new MessageListAdapter(context, _messages);
    messagesRclv.setAdapter(_messagesListAdapter);
    messagesRclv.scrollToPosition(_messages.size() - 1);
    messagesRclv.addOnLayoutChangeListener(
      (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
        if (bottom < oldBottom) {
          if (_messagesListAdapter.getItemCount() <= 0) {
            messagesRclv.postDelayed(() -> {
              messagesRclv.smoothScrollToPosition(_messagesListAdapter.getItemCount());
            }, 100);
          } else {
            messagesRclv.postDelayed(() -> {
              messagesRclv.smoothScrollToPosition(_messagesListAdapter.getItemCount() - 1);
            }, 100);
          }
        }
      });

    lLayRclvContainer.addView(messagesRclv);

    Box<AMQMessagePayload> messageBox = ObjectBox.get().boxFor(AMQMessagePayload.class);
    messagesRclv.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if (dy > 0 || dy < 0 && lLayFabsContainer.getVisibility() == VISIBLE) {
          lLayFabsContainer.setVisibility(GONE);
        }

        long visibleItemCount = recyclerView.getChildCount();
        long totalItemCount = layoutManager.getItemCount();
        long firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading) {
          if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
          }
        }
        if (!loading && (totalItemCount - visibleItemCount)
          <= (firstVisibleItem + visibleThreshold)) {
          List<AMQMessage> messages = fetchedMessage(_messagesListAdapter.getItemCount(), context);
          for (AMQMessage message : messages) {
            new Handler().postDelayed(() -> {
              _messagesListAdapter.addMessage(message);
            }, 100);
          }
          loading = true;
        }

      }

      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          lLayFabsContainer.setVisibility(VISIBLE);
        }

        super.onScrollStateChanged(recyclerView, newState);
      }
    });

    this.addView(lLayRclvContainer);
  }

  private void setupMessageBrokerListeners() {
    RXBus.listen(RxBusMessageEvent.OnMessageArrived.class).subscribe(msg -> {
      if (msg == null) return;
      AMQMessage message = ((RxBusMessageEvent.OnMessageArrived) msg).getMessage();
      _messagesListAdapter.addMessage(message);
      messagesRclv.scrollToPosition(_messagesListAdapter.getItemCount() - 1);
    }, error -> {
    });


    RXBus.listen(RxBusMessageEvent.OnConnectionStatusChanged.class).subscribe(stts -> {
      if (stts == null) return;
      ConnectionStatus status = ((RxBusMessageEvent.OnConnectionStatusChanged) stts).getConnectionStatus();
    }, error -> {

    });
  }

}
