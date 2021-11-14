package com.example.cortevard_demo;

import android.app.Application;
import net.gotev.uploadservice.UploadService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set your application namespace to avoid conflicts with other apps
        // using this library
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        // Set upload service debug log messages level
        //Logger.setLogLevel(Logger.LogLevel.DEBUG);

        // Set up the Http Stack to use. If you omit this or comment it, HurlStack will be
        // used by default
        //UploadService.HTTP_STACK = new OkHttpStack(getOkHttpClient());
    }
}
