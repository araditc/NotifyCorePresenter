package com.arad_itc.notify.core.presenter.amq.presentation.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.arad_itc.notify.core.presenter.R;
import com.arad_itc.notify.core.presenter.core.classes.BinaryFileDownloader;
import com.arad_itc.notify.core.presenter.core.classes.BinaryFileWriter;
import com.arad_itc.notify.core.presenter.core.constants.Consts;
import com.arad_itc.notify.core.presenter.databinding.ActivityVideoBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class VideoActivity extends AppCompatActivity {
  private static final String TAG = VideoActivity.class.getSimpleName();
  ActivityVideoBinding _binding;
  String url;
  String fileName;

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _binding = ActivityVideoBinding.inflate(getLayoutInflater());
    setContentView(_binding.getRoot());

    Bundle extras = getIntent().getExtras();
    if (!extras.containsKey(Consts.Intent_Video_Url)) {
      throw new IllegalArgumentException(String.format("Pass the %s to VideoActivity", Consts.Intent_Video_Url));
    }
    if (!extras.containsKey(Consts.Intent_Video_Name)) {
      throw new IllegalArgumentException(String.format("Pass the %s to VideoActivity", Consts.Intent_Video_Name));
    }


    url = extras.getString(Consts.Intent_Video_Url);
    fileName = extras.getString(Consts.Intent_Video_Name);
    _binding.mToolBar.setTitle(fileName);
    _binding.mToolBar.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.downlaod) {
        Log.i(TAG, "onOptionsItemSelected: download btn clicked");
        downloadVideo();
        return true;
      }
      return false;
    });

//    this.setSupportActionBar(_binding.myToolbar);

    ExoPlayer player = new ExoPlayer.Builder(this).build();
    _binding.playerView.setPlayer(player);
    _binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
    player.setMediaItem(MediaItem.fromUri(Uri.parse(url)));
    player.prepare();
    player.play();

    player.addListener(new Player.Listener() {
      @Override
      public void onPlayerError(PlaybackException error) {
        Player.Listener.super.onPlayerError(error);

        Log.e(TAG, "onPlayerError: ", error);
      }
    });


    _binding.playerView.setControllerVisibilityListener(visibility -> {
      if (visibility == View.VISIBLE) {
        showSystemBars();
      } else {
        hideSystemBars();
      }
    });
  }


  double downloadProgress = 0.0;
  Call call;

  @RequiresApi(api = Build.VERSION_CODES.M)
  private void downloadVideo() {
    if (ContextCompat.checkSelfPermission(
      this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
      PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "downloadVideo: permition granted");
    } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      requestPermissionLauncher.launch(
        Manifest.permission.WRITE_EXTERNAL_STORAGE);
      return;
    } else {
      requestPermissionLauncher.launch(
        Manifest.permission.WRITE_EXTERNAL_STORAGE);
      return;
    }

    //download video
    OkHttpClient client = new OkHttpClient();

    try {

      View dialogDownloadView = getLayoutInflater().inflate(R.layout.dialog_download, null, false);
      SeekBar seekDownload = dialogDownloadView.findViewById(R.id.seekDownload);
      seekDownload.setMax(100);

      File downlaodsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
      File file = new File(downlaodsDir, String.format("%s.mp4", fileName));
      FileOutputStream outputStream = new FileOutputStream(file);
      BinaryFileDownloader fileDownloader = new BinaryFileDownloader(client, new BinaryFileWriter(outputStream, progress -> {
        Log.i(TAG, "onOptionsItemSelected, progress: " + progress);
        downloadProgress = progress;
        updateDownloafSeekBar((int) downloadProgress, seekDownload);
      }));
//      seekDownload.setProgress((int) (downloadProgress / 100));
      new Thread(() -> {
        try {
          fileDownloader.download(url, call -> {
            this.call = call;
          });

        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();

      AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle(R.string.DownloadFile)
        .setView(dialogDownloadView)
        .setNegativeButton(this.getResources().getString(R.string.CancelDownload), (dialogInterface, listener) -> {
          if (call == null) return;
          call.cancel();
        });

      AlertDialog dialog = builder.create();
      dialog.show();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void updateDownloafSeekBar(int downloadProgress, SeekBar seekBar) {
    seekBar.setProgress(downloadProgress);
  }

  private void hideSystemBars() {
    _binding.mToolBar.setVisibility(View.GONE);
  }

  private void showSystemBars() {
    _binding.mToolBar.setVisibility(View.VISIBLE);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private ActivityResultLauncher<String> requestPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
      if (isGranted) {
        downloadVideo();
      } else {
        // Explain to the user
      }
    });

}