package com.arad_itc.notify.core.app.presenter.amq.presentation.widgets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arad_itc.notify.core.amq.domain.entities.ContactStruct;
import com.arad_itc.notify.core.app.presenter.R;
import com.arad_itc.notify.core.app.presenter.core.classes.CircleTransform;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class ContactMessageBottomSheet extends BottomSheetDialogFragment {
  public static final String Tag = ContactMessageBottomSheet.class.getSimpleName();

  private final Context _context;
  private final ContactStruct _contact;

  public ContactMessageBottomSheet(Context context, ContactStruct contact) {
    _context = context;
    _contact = contact;
  }


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.contact_message_bottom_sheet, container, false);
    ImageView imgProfile = view.findViewById(R.id.imgThumbnail);
    TextView txtUsername = view.findViewById(R.id.txtUsername);
    TextView txtname = view.findViewById(R.id.txtFullName);

    //build image
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(_contact.profileImage().getWidth(), _contact.profileImage().getHeight());
    imgProfile.setLayoutParams(params);
    Picasso.get().load(_contact.profileImage().imageFile().getData()).transform(new CircleTransform()).into(imgProfile);

    //fill the txts
    txtUsername.setText("" + _contact.getUsername());
    String name = String.format("%s %s", _contact.getFirstname(), _contact.getLastname());
    txtname.setText("" + name);

    Button btnSave = view.findViewById(R.id.btnSave);
    Button btnCall = view.findViewById(R.id.btnCall);

    btnSave.setOnClickListener(v -> {
      Intent intent = new Intent(
        ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
        ContactsContract.Contacts.CONTENT_URI);
      intent.setData(Uri.parse(String.format("tel:%s", _contact.getUsername())));//specify your number here
      intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
      startActivity(intent);
    });

    btnCall.setOnClickListener(v -> {
      if (ContextCompat.checkSelfPermission(
        _context, Manifest.permission.CALL_PHONE) ==
        PackageManager.PERMISSION_GRANTED) {
      } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        requestPermissionLauncher.launch(
          Manifest.permission.CALL_PHONE);
        return;
      } else {
        requestPermissionLauncher.launch(
          Manifest.permission.CALL_PHONE);
        return;
      }

      call();
    });
    return view;
  }

  private void call() {
    Intent intent = new Intent(Intent.ACTION_CALL);
    intent.setData(Uri.parse(String.format("tel:%s", _contact.getUsername())));
    startActivity(intent);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private ActivityResultLauncher<String> requestPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
      if (isGranted) {
        call();
      } else {
        // Explain to the user
      }
    });

}
