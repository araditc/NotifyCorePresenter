package com.arad_itc.notify.core.app.presenter.amq.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arad_itc.notify.core.amq.domain.entities.AudioStruct;
import com.arad_itc.notify.core.amq.domain.entities.ContactStruct;
import com.arad_itc.notify.core.amq.domain.entities.DocumentStruct;
import com.arad_itc.notify.core.amq.domain.entities.FileStruct;
import com.arad_itc.notify.core.amq.domain.entities.ImageStruct;
import com.arad_itc.notify.core.amq.domain.entities.LocationStruct;
import com.arad_itc.notify.core.amq.domain.entities.TextStruct;
import com.arad_itc.notify.core.amq.domain.entities.TransactionStruct;
import com.arad_itc.notify.core.amq.domain.entities.TransactionType;
import com.arad_itc.notify.core.amq.domain.entities.VideoStruct;
import com.arad_itc.notify.core.app.presenter.R;
import com.arad_itc.notify.core.app.presenter.amq.presentation.activities.ImageActivity;
import com.arad_itc.notify.core.app.presenter.amq.presentation.activities.PdfViewActivity;
import com.arad_itc.notify.core.app.presenter.amq.presentation.activities.VideoActivity;
import com.arad_itc.notify.core.app.presenter.amq.presentation.widgets.ContactMessageBottomSheet;
import com.arad_itc.notify.core.app.presenter.core.classes.CircleTransform;
import com.arad_itc.notify.core.app.presenter.core.classes.RoundedCornersTransformation;
import com.arad_itc.notify.core.app.presenter.core.constants.Consts;
import com.arad_itc.notify.core.app.presenter.core.helpers.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MessageListUiBuilder {
  public static View buildTextUi(Context context, TextStruct text) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_text, null, false);
    TextView txt = view.findViewById(R.id.txt);
    txt.setText("" + text.getText());
    return view;
  }

  public static View buildLocationUi(Context context, LocationStruct location) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_location, null, false);

    //build location
    ImageView imgStaticMap = view.findViewById(R.id.imgStaticMap);
//      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(512, 512);
//      imgStaticMap.setLayoutParams(params);

    Picasso.get()
      .load(Utils.getStaticImageUrl(location.getLat(), location.getLng()))
//        .centerCrop()
      .resize(1920, 640)
      .into(imgStaticMap);

    TextView txtCaption = view.findViewById(R.id.txtCaption);
    txtCaption.setText("" + location.getCaption());

    view.setOnClickListener(v -> {
      Utils.showMap(context, location.getLat(), location.getLng());
    });
    return view;
  }

  public static View buildTransactionUi(Context context, TransactionStruct transaction) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, null, false);
    TextView txtTransaction = view.findViewById(R.id.txtTransaction);
    txtTransaction.setText("" + transaction.getData());

    LinearLayout llayTransType = view.findViewById(R.id.llayTransType);
    llayTransType.setBackground(context.getResources().getDrawable(transaction.transactionType == TransactionType.Payment ? R.drawable.transaction_payment_background : R.drawable.transaction_recive_background));
    return view;
  }

  @SuppressLint("UnsafeOptInUsageError")
  public static View buildVideoUi(Context context, VideoStruct video) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_video, null, false);
    if (video.thumbnail() != null) {
      ImageView imgThumbnail = view.findViewById(R.id.imgThumbnail);
      Picasso.get()
        .load(video.thumbnail().imageFile().getData())
        .into(imgThumbnail);
    }

    TextView txtTitle = view.findViewById(R.id.txtTitle);
    txtTitle.setText("" + video.videoFile().getFileName());

    view.setOnClickListener(v -> {
      Intent intent = new Intent(context, VideoActivity.class);
      intent.putExtra(Consts.Intent_Video_Url, video.videoFile().getData());
      intent.putExtra(Consts.Intent_Video_Name, video.videoFile().getFileName());
      context.startActivity(intent);
    });

    return view;
  }

  public static View buildImageUi(Context context, ImageStruct image) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_image, null, false);
    ImageView img = view.findViewById(R.id.img);
    Picasso.get().load(image.imageFile().getData()).centerCrop().resize(image.getWidth(), image.getHeight()).transform(new RoundedCornersTransformation(12, 5)).into(img);

    TextView txtTitle = view.findViewById(R.id.txtTitle);
    txtTitle.setText("" + image.imageFile().getFileName());

    view.setOnClickListener(v -> {
      Intent intent = new Intent(context, ImageActivity.class);
      intent.putExtra(Consts.Intent_Image_Url,image.imageFile().getData());
      context.startActivity(intent);
    });
    return view;
  }

  public static View buildContactUi(Context context, ContactStruct contact) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_contact, null, false);
    ImageView imgProfile = view.findViewById(R.id.imgProfile);
    TextView txtUsername = view.findViewById(R.id.txtUsername);
    TextView txtname = view.findViewById(R.id.txtname);

    //build image
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(contact.profileImage().getWidth(), contact.profileImage().getHeight());
    imgProfile.setLayoutParams(params);
    Picasso.get().load(contact.profileImage().imageFile().getData()).transform(new CircleTransform()).into(imgProfile);

    //fill the txts
    txtUsername.setText("" + contact.getUsername());
    txtname.setText("" + String.format("%s %s", contact.getFirstname(), contact.getLastname()));

    view.setOnClickListener(v -> {
      ContactMessageBottomSheet contactMessageBottomSheet = new ContactMessageBottomSheet(context, contact);
      AppCompatActivity activity = (AppCompatActivity) context;
      contactMessageBottomSheet.show(activity.getSupportFragmentManager(), ContactMessageBottomSheet.Tag);
    });

    return view;
  }

  public static View buildAudioUi(Context context, AudioStruct audio) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_voice, null, false);
    TextView txtFileName = view.findViewById(R.id.txtfileName);
    TextView txtDuration = view.findViewById(R.id.txtDuration);
    txtFileName.setText("" + audio.audioFile().getFileName());

    LinearLayout llayPlayContainer = view.findViewById(R.id.llayPlayContainer);

    Button btnPlayPause = view.findViewById(R.id.btnPlayPause);
    SeekBar seekBar = view.findViewById(R.id.seekbar);

    ContentResolver contentResolver = context.getContentResolver();
    Cursor cursor = contentResolver.query(Uri.parse(audio.audioFile().getData()), null, null, null, null);

    if (cursor != null && cursor.moveToFirst()) {
      //do some staf
    }

    MediaPlayer mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    try {
      mediaPlayer.setDataSource(context, Uri.parse(audio.audioFile().getData()));
      mediaPlayer.prepareAsync();
    } catch (IOException e) {
      e.printStackTrace();
    }

    mediaPlayer.setOnPreparedListener(mPlayer -> {
      seekBar.setMax(mPlayer.getDuration());
      btnPlayPause.setClickable(true);
    });

    mediaPlayer.setOnCompletionListener(mPlayer -> {
      btnPlayPause.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
    });

    btnPlayPause.setOnClickListener(v -> {
      if (!mediaPlayer.isPlaying()) {
        mediaPlayer.start();
        btnPlayPause.setBackground(context.getResources().getDrawable(R.drawable.ic_pause));
        updateSeekBar(mediaPlayer, seekBar, txtDuration);
      } else {
        mediaPlayer.pause();
        btnPlayPause.setBackground(context.getResources().getDrawable(R.drawable.ic_play));
      }
    });


    return view;
  }

  private static void updateSeekBar(MediaPlayer player, SeekBar seekBar, TextView txtDuration) {
    seekBar.setProgress(player.getCurrentPosition());
    txtDuration.setText(Utils.milliSecondsToTimer(player.getCurrentPosition()));
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        updateSeekBar(player, seekBar, txtDuration);
      }
    }, 1000);
  }

  public static View buildDocumentUi(Context context, DocumentStruct document) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_text, null, false);
    TextView txt = view.findViewById(R.id.txt);
    ImageView imgTxt = view.findViewById(R.id.imgTxt);
    imgTxt.setImageResource(R.drawable.txt);
    txt.setText("" + document.getCaption());

    switch (document.getMimeType()) {
      case "mp3":
        AudioStruct audioStruct = new AudioStruct();
        audioStruct.setId(document.getId());
        audioStruct.setCaption(document.getCaption());

        FileStruct audioFile = new FileStruct();
        audioFile.setFileName(document.getFileName());
        audioFile.setData(document.getFilePath());
        audioFile.setMimeType(document.getMimeType());
        audioStruct.audioFile(audioFile);

        view = buildAudioUi(context, audioStruct);
        break;
      case "mp4":
      case "mkv":
        VideoStruct videoStruct = new VideoStruct();
        videoStruct.setId(document.getId());
        videoStruct.setCaption(document.getCaption());

        FileStruct videoFile = new FileStruct();
        videoFile.setFileName(document.getFileName());
        videoFile.setData(document.getFilePath());
        videoFile.setMimeType(document.getMimeType());
        videoStruct.videoFile(videoFile);

        view = buildVideoUi(context, videoStruct);
        break;
      case "png":
      case "jpg":
        ImageStruct imageStruct = new ImageStruct();
        imageStruct.setId(document.getId());
        imageStruct.setCaption(document.getCaption());
        imageStruct.setWidth(document.getWidth());
        imageStruct.setHeight(document.getHeight());

        FileStruct imageFile = new FileStruct();
        imageFile.setFileName(document.getFileName());
        imageFile.setData(document.getFilePath());
        imageFile.setMimeType(document.getMimeType());
        imageStruct.imageFile(imageFile);

        view = buildImageUi(context, imageStruct);
        break;
      case "pdf":
        view = LayoutInflater.from(context).inflate(R.layout.item_text, null, false);
        txt = view.findViewById(R.id.txt);
        imgTxt = view.findViewById(R.id.imgTxt);

        imgTxt.setImageResource(R.drawable.documents);
        txt.setText("" + document.getCaption());
        view.setOnClickListener(v -> {
          Intent intent = new Intent(context, PdfViewActivity.class);
          intent.putExtra(Consts.Intent_File_Url, document.getFilePath());
          context.startActivity(intent);
        });
        break;
    }

    return view;
  }
}
