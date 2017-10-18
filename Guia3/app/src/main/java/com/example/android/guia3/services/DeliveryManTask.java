package com.example.android.guia3.services;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

public class DeliveryManTask extends AsyncTaskLoader {
    public DeliveryManTask(Context context) {
        super(context);
    }



    @Override
    public String loadInBackground() {
        try {
            Thread.sleep(3000);
            return "Domiciliario de Prueba";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}