package com.arad_itc.notify.core.app.presenter.core.classes;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class BinaryFileDownloader implements AutoCloseable {
  final String CONTENT_LENGTH =  	"Content-Length";

  private final OkHttpClient client;
  private final BinaryFileWriter writer;

  public BinaryFileDownloader(OkHttpClient client, BinaryFileWriter writer) {
    this.client = client;
    this.writer = writer;
  }

  public long download(String url, SetOnDownloadStarted setOnDownloadStarted) throws IOException {
    Request request = new Request.Builder().url(url).tag("download").build();
    Call newCall = client.newCall(request);
    if (setOnDownloadStarted != null)
      setOnDownloadStarted.onStart(newCall);

    Response response = newCall.execute();

    ResponseBody responseBody = response.body();
    if (responseBody == null) {
      throw new IllegalStateException("Response doesn't contain a file");
    }

    double length = Double.parseDouble(Objects.requireNonNull(response.header(CONTENT_LENGTH, "1")));
    return writer.write(responseBody.byteStream(), length);
  }

  @Override
  public void close() throws Exception {
    writer.close();
  }

  public static interface SetOnDownloadStarted {
    public void onStart(Call call);
  }
}