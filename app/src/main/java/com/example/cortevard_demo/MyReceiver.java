package com.example.cortevard_demo;

import android.content.Context;
import android.widget.Toast;

import com.example.cortevard_demo.Controladores.ControladorCosecha;
import com.example.cortevard_demo.Controladores.ControladorPulverizacion;
import com.example.cortevard_demo.Controladores.ControladorSeleccionLote;
import com.example.cortevard_demo.Controladores.ControladorSiembra;
import com.example.cortevard_demo.Controladores.ControladorTomaDatos;
import com.example.cortevard_demo.Controladores.ControladorVueloConDron;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

public class MyReceiver extends UploadServiceBroadcastReceiver {
    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        // your implementation
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        // your implementation
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        try {
            String response = serverResponse.getBodyAsString();
            if (!response.contains("Error")){
                //Significa que se realizo OK
                if (uploadInfo.getUploadId().contains("setSiembra")){
                    Boolean success = new ControladorSiembra(context).setSiembrasPendientes(response);
                }else if(uploadInfo.getUploadId().contains("setCosecha")){
                    Boolean success = new ControladorCosecha(context).setCosechasPendientes(response);
                }else if(uploadInfo.getUploadId().contains("setPulverizacion")){
                    Boolean success = new ControladorPulverizacion(context).setPulverizacionesPendientes(response);
                }else if(uploadInfo.getUploadId().contains("setSeleccionDeLote")){
                    Boolean success = new ControladorSeleccionLote(context).setSeleccionLotePendientes(response);
                }else if(uploadInfo.getUploadId().contains("setTomaDeDatos")){
                    Boolean success = new ControladorTomaDatos(context).setTomaDeDatosPendientes(response);
                }else if(uploadInfo.getUploadId().contains("setVueloConDron")){
                    Boolean success = new ControladorVueloConDron(context).setVuelosPendientes(response);
                }
            }
        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        // your implementation
    }
}