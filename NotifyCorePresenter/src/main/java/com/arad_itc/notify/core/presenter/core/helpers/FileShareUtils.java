package com.arad_itc.notify.core.presenter.core.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.arad_itc.notify.core.presenter.core.constants.Consts;

import java.io.File;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

/**
 * Created by: Ranit Raj Ganguly on 17/04/21
 */
public class FileShareUtils {

    /**
     * Access file from 'Application Directory'
     *
     * @param context - application context
     * @param fileName - name of file inside application directory to be shared
     * @return uri - returns URI of file.
     */
    public static Uri accessFile(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null), fileName);

        if (file.exists()) {
            return FileProvider.getUriForFile(context,
              Consts.File_Provider, file);
        } else {
            return null;
        }
    }

    /**
     * @param context
     * @param uri
     */
    public static void launchShareFileIntent(Context context,Uri uri) {
        Intent intent = new ShareCompat.IntentBuilder(context)
          .setType("application/pdf")
          .setStream(uri)
          .setChooserTitle("Select application to share file")
          .createChooserIntent()
          .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }
}
