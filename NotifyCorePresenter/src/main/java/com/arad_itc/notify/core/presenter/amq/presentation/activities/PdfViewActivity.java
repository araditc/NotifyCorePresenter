package com.arad_itc.notify.core.presenter.amq.presentation.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import com.arad_itc.notify.core.presenter.core.constants.Consts;
import com.arad_itc.notify.core.presenter.databinding.ActivityPdfViewBinding;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.appcompat.app.AppCompatActivity;

public class PdfViewActivity extends AppCompatActivity {

  private static final String TAG = PdfViewActivity.class.getSimpleName();
  ActivityPdfViewBinding _binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
    setContentView(_binding.getRoot());

    Bundle extra = getIntent().getExtras();
    if (!extra.containsKey(Consts.Intent_File_Url)) {
      finish();
      return;
    }

    String url = extra.getString(Consts.Intent_File_Url);
    new RetrivePDFfromUrl().execute(url);
  }

  // create an async task class for loading pdf file from URL.
  class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
    @Override
    protected InputStream doInBackground(String... strings) {
      // we are using inputstream
      // for getting out PDF.
      InputStream inputStream = null;
      try {
        URL url = new URL(strings[0]);
        // below is the step where we are
        // creating our connection.
        HttpURLConnection urlConnection;
        if(url.getProtocol().equals("https")){
          urlConnection = (HttpsURLConnection) url.openConnection();
        }else{
          urlConnection = (HttpURLConnection) url.openConnection();
        }
        if (urlConnection.getResponseCode() == 200) {
          // response is success.
          // we are getting input stream from url
          // and storing it in our variable.
          inputStream = new BufferedInputStream(urlConnection.getInputStream());
        }

      } catch (IOException e) {
        // this is the method
        // to handle errors.
        e.printStackTrace();
        return null;
      }
      return inputStream;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
      // after the execution of our async
      // task we are loading our pdf in our pdf view.
      _binding.idPDFView.fromStream(inputStream).load();
    }
  }
}