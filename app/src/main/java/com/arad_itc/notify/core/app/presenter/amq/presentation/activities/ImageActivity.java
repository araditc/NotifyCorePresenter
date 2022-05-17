package com.arad_itc.notify.core.app.presenter.amq.presentation.activities;

import android.os.Bundle;

import com.arad_itc.notify.core.app.presenter.core.constants.Consts;
import com.arad_itc.notify.core.app.presenter.databinding.ActivityImageBinding;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {
  private final String Tag = ImageActivity.class.getSimpleName();
  ActivityImageBinding _binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _binding = ActivityImageBinding.inflate(getLayoutInflater());
    setContentView(_binding.getRoot());

    Bundle extras = getIntent().getExtras();
    if (!extras.containsKey(Consts.Intent_Image_Url)){
      throw new IllegalArgumentException(String.format("Pass Intent_Image_Url"));
    }

    Picasso.get()
      .load(extras.getString(Consts.Intent_Image_Url))
      .fit()
      .centerCrop()
      .into(_binding.photoView);

  }
}