package com.example.cortevard_demo;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;


import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdateSyncronizer extends AsyncTask<Void, Void, Void> {

    HttpHandler sh = new HttpHandler();
    //Propiedades de la clase
    Activity activity;
    String tipo;
    private long downloadID;
    private static final int REQUEST_INSTALL=1115;
    //private int version =  0 ;

    public UpdateSyncronizer(Activity activity, String tipo) {
        this.activity = activity;
        this.tipo = tipo;
    }

    protected Void doInBackground(Void... params) {

        //validar si hay conexion a internet
        if (isConnected(activity.getApplicationContext())) {
            try {
                activity.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                downloadApk();
            } catch (Exception e) {
                Log.e("DOWNLOAD APK", e.getMessage());
                e.printStackTrace();
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "No hay internet para sincronizar", Toast.LENGTH_LONG).show();
                }
            });
        }
        return null;
    }

    protected static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    private void downloadApk() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/", "rd_app.apk");
        //Delete update file if exists
        if (file.exists())
            file.delete();
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse("https://cortevasemillas.com/rd/rd_app.apk"))
                .setTitle("R&D APP")// Title of the Download Notification
                .setDescription("Descargando")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                //.setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                try{
                    File file =  new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/", "rd_app.apk");
                    if (!file.exists())
                        return;
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",file);
                    intent2.setDataAndType(uri, "application/vnd.android" + ".package-archive");
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// without this flag android returned a intent error!
                    intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //Paso como parametro la version a la que estoy actualizando
                    //intent2.putExtra("version", version);
                    activity.startActivityForResult(intent2,REQUEST_INSTALL);

                    activity.unregisterReceiver(onDownloadComplete);
                } catch (Exception e) {
                    Log.e("UpdateAPP", "Update error! " + e.getMessage());
                }
            }
        }
    };
}
