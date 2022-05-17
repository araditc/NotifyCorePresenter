package com.arad_itc.notify.core.app.presenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.arad_itc.notify.core.presenter.amq.presentation.AradBrokerPresenter;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

        AradBrokerPresenter.getInstance(this)
      .setAppId("626d0d549c4704dcf2258b46")
      .setCredential("989135646251", "i1@bBW?$9v$??16$2Gg8BJ9$jr?z956Q")
      .setUseTls(false)
      .setHost("192.168.100.55", 1883)
      .setPreferredKey("AMplKJBBBLnskVfJEcW8Ww==")
      .connect();

    setContentView(R.layout.activity_main);
  }
}