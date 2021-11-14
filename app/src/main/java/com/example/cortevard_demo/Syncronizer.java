package com.example.cortevard_demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.cortevard_demo.Controladores.ControladorAplicador;
import com.example.cortevard_demo.Controladores.ControladorCampana;
import com.example.cortevard_demo.Controladores.ControladorCarreton;
import com.example.cortevard_demo.Controladores.ControladorConfig;
import com.example.cortevard_demo.Controladores.ControladorCosecha;
import com.example.cortevard_demo.Controladores.ControladorCosechadora;
import com.example.cortevard_demo.Controladores.ControladorCultivo;
import com.example.cortevard_demo.Controladores.ControladorDron;
import com.example.cortevard_demo.Controladores.ControladorEstacion;
import com.example.cortevard_demo.Controladores.ControladorLocalidad;
import com.example.cortevard_demo.Controladores.ControladorMaleza;
import com.example.cortevard_demo.Controladores.ControladorParcela;
import com.example.cortevard_demo.Controladores.ControladorPulverizacion;
import com.example.cortevard_demo.Controladores.ControladorSeleccionLote;
import com.example.cortevard_demo.Controladores.ControladorSembradora;
import com.example.cortevard_demo.Controladores.ControladorSiembra;
import com.example.cortevard_demo.Controladores.ControladorTomaDatos;
import com.example.cortevard_demo.Controladores.ControladorTractor;
import com.example.cortevard_demo.Controladores.ControladorTrait;
import com.example.cortevard_demo.Controladores.ControladorUsuarios;
import com.example.cortevard_demo.Controladores.ControladorVueloConDron;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Syncronizer extends AsyncTask<Void, Void, Void> {

    HttpHandler sh = new HttpHandler();
    //Propiedades de la clase
    Activity activity;
    String tipo;

    public Syncronizer(Activity activity, String tipo) {
        this.activity = activity;
        this.tipo = tipo;
    }

    protected Void doInBackground(Void... params) {

        //validar si hay conexion a internet
        if (isConnected(activity.getApplicationContext())) {
            try {
                //mostrar un Toast que diga sincronizando
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), "Iniciando sincronización completa.\nPor favor aguarde.", Toast.LENGTH_LONG).show();
                    }
                });
                if (tipo.equals("setCosecha")) {
                    uploadMultipart("setCosecha");
                }else if(tipo.equals("setSeleccionDeLote")){
                    uploadMultipart("setSeleccionDeLote");
                }else if(tipo.equals("setTomaDeDatos")){
                    uploadMultipart("setTomaDeDatos");
                }else if(tipo.equals("setVueloConDron")){
                    uploadMultipart("setVueloConDron");
                }else if(tipo.equals("setPulverizacion")){
                    uploadMultipart("setPulverizacion");
                }else if(tipo.equals("setSiembra")){
                    uploadMultipart("setSiembra");
                }else {
                    //Primero envio los datos de la app
                    uploadMultipart("setCosecha");
                    uploadMultipart("setSeleccionDeLote");
                    uploadMultipart("setTomaDeDatos");
                    uploadMultipart("setVueloConDron");
                    uploadMultipart("setPulverizacion");
                    uploadMultipart("setSiembra");
                    //Luego solicito los que estan en la nube
                    int totalDatos = 0;
                    int totalUsers = 0;
                    if (tipo.equals("Completo")) {
                        //Traigo todos los Usuarios para sincronizarlos
                        totalUsers = GetData("getUsers", null);
                        totalDatos += GetData("getCampanas", null);
                        totalDatos += GetData("getEstaciones", null);
                        totalDatos += GetData("getCultivos", null);
                        totalDatos += GetData("getLocalidades", null);
                        totalDatos += GetData("getParcelas", null);
                        totalDatos += GetData("getSembradoras", null);
                        totalDatos += GetData("getTractores", null);
                        totalDatos += GetData("getMalezas", null);
                        totalDatos += GetData("getAplicadores", null);
                        totalDatos += GetData("getDrones", null);
                        totalDatos += GetData("getCosechadoras", null);
                        totalDatos += GetData("getCarretones", null);
                        totalDatos += GetData("getTraits", null);
                        GetData("getLastVersion", null);
                        final int total1 = totalDatos;
                        final int total2 = totalUsers;
                        //mostrar un Toast con los resultados
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity.getApplicationContext(), "Sincronización Finalizada.\nDatos recibidos:" + total1 + "\nUsuarios actualizados:" + total2, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {

                        //busco el valor de last_syncro para enviarlo como parametro y ademas, lo actualizo con la fechahoractual
                        String last_syncro = new ControladorConfig(activity.getApplicationContext()).GetSet_last_syncro();

                        //Calculo tiempo transcurrido desde la ultima syncro
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date ultima_syncro = df.parse(last_syncro);
                        Date ahora = df.parse(df.format(new Date()));

                        long millis = Math.abs(ahora.getTime() - ultima_syncro.getTime());
                        int diferencia = (int) (millis / (60 * 1000)); //espera en minutos

                        if (diferencia >= 60) {
                            //si la ultima syncro fue mas de 60 minutos o es la primera vez que se ejecuta la app -> traigo todos los registros
                            totalUsers = GetData("getUsers", null);
                            totalDatos += GetData("getCampanas", null);
                            totalDatos += GetData("getEstaciones", null);
                            totalDatos += GetData("getCultivos", null);
                            totalDatos += GetData("getLocalidades", null);
                            totalDatos += GetData("getParcelas", null);
                            totalDatos += GetData("getSembradoras", null);
                            totalDatos += GetData("getTractores", null);
                            totalDatos += GetData("getMalezas", null);
                            totalDatos += GetData("getAplicadores", null);
                            totalDatos += GetData("getDrones", null);
                            totalDatos += GetData("getCosechadoras", null);
                            totalDatos += GetData("getCarretones", null);
                            totalDatos += GetData("getTraits", null);
                            GetData("getLastVersion", null);
                            final int total1 = totalDatos;
                            final int total2 = totalUsers;
                            //mostrar un Toast con los resultados
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity.getApplicationContext(), "Sincronización Finalizada.\nDatos recibidos:" + total1 + "\nUsuarios actualizados:" + total2, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (diferencia > 2) {
                            //sino pido los datos desde la last_syncro
                            //Traigo los Usuarios modificados
                            totalUsers = GetData("getUsers", last_syncro);
                            GetData("getLastVersion", null);
                            final int total1 = totalDatos;
                            final int total2 = totalUsers;
                            //mostrar un Toast con los resultados
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity.getApplicationContext(), "Sincronización Finalizada.\nDatos recibidos:" + total1 + "\nUsuarios actualizados:" + total2, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("SINCRONIZACION ERROR", e.getMessage());
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

    protected int GetData(String method, String last_syncro) {
        JSONObject row = new JSONObject();
        JSONArray jsonParam = new JSONArray();
        try {
            row.put("last_syncro", last_syncro);
            jsonParam.put(row);
        } catch (Exception e) {

        }
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(method, jsonParam);
        //Log.e("SINCRONIZACION", "Response from url: " + jsonStr);
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray datos = jsonObj.getJSONArray("datos");

                // looping through All Datos
                for (int i = 0; i < datos.length(); i++) {
                    JSONObject c = datos.getJSONObject(i);

                    if (method.equals("getUsers") || method.equals("getUsersByDatetime")) {
                        Integer idUsuario = c.getInt("idUsuario");
                        String user = c.getString("usuario");
                        String password = c.getString("password");
                        String nombre = c.getString("Nombre");
                        Integer activo = c.getInt("activo");
                        Boolean success = new ControladorUsuarios(activity.getApplicationContext()).InsertOrUpdate(idUsuario, user, password, nombre, activo);
                    } else if (method.equals("getAplicadores")) {
                        Integer id = c.getInt("idAplicador");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorAplicador(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getCarretones")) {
                        Integer id = c.getInt("idCarreton");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorCarreton(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getCampanas")) {
                        Integer id = c.getInt("idCampana");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorCampana(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getCosechadoras")) {
                        Integer id = c.getInt("idCosechadora");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorCosechadora(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getCultivos")) {
                        Integer id = c.getInt("idCultivo");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorCultivo(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getDrones")) {
                        Integer id = c.getInt("idDron");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorDron(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getEstaciones")) {
                        Integer id = c.getInt("idEstacion");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorEstacion(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getLocalidades")) {
                        Integer id = c.getInt("idLocalidad");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorLocalidad(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    }else if (method.equals("getMalezas")) {
                        Integer id = c.getInt("idMaleza");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorMaleza(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getParcelas")) {
                        Integer id = c.getInt("idParcela");
                        String latitud = c.getString("latitud");
                        String longitud = c.getString("longitud");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorParcela(activity.getApplicationContext()).InsertOrUpdate(id, latitud, longitud, activo);
                    } else if (method.equals("getSembradoras")) {
                        Integer id = c.getInt("idSembradora");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorSembradora(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    }else if (method.equals("getTractores")) {
                        Integer id = c.getInt("idTractor");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorTractor(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    } else if (method.equals("getTraits")) {
                        Integer id = c.getInt("idTrait");
                        String titulo = c.getString("titulo");
                        Integer activo = c.getInt("Activo");
                        Boolean success = new ControladorTrait(activity.getApplicationContext()).InsertOrUpdate(id, titulo, activo);
                    }else if (method.equals("getLastVersion")) {
                        Integer version = c.getInt("version");
                        PackageManager manager = activity.getPackageManager();
                        try {
                            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
                            Integer versionCode = (Integer) info.versionCode;
                            if (version > versionCode) {
                                SaveSharedPreference.setNewVersion(activity, true);
                            }else{
                                SaveSharedPreference.setNewVersion(activity, false);
                            }
                        } catch (PackageManager.NameNotFoundException e) {

                        }
                    }
                }
                return datos.length();
            } catch (final JSONException e) {
                Log.e("SINCRONIZACION", "Json parsing error: " + e.getMessage());
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void uploadMultipart(final String method) throws JSONException {
        //Uploading code
        JSONArray jsonParam = new JSONArray();
        if (method.equals("setCosecha")) {
            jsonParam = new ControladorCosecha(activity.getApplicationContext()).getCosechasPendientes();
        } else if (method.equals("setSeleccionDeLote")) {
            jsonParam = new ControladorSeleccionLote(activity.getApplicationContext()).getSeleccionLotePendientes();
        } else if (method.equals("setTomaDeDatos")) {
            jsonParam = new ControladorTomaDatos(activity.getApplicationContext()).getTomaDeDatosPendientes();
        } else if (method.equals("setVueloConDron")) {
            jsonParam = new ControladorVueloConDron(activity.getApplicationContext()).getVuelosPendientes();
        } else if (method.equals("setPulverizacion")) {
            jsonParam = new ControladorPulverizacion(activity.getApplicationContext()).getPulverizacionesPendientes();
        } else if (method.equals("setSiembra")) {
            jsonParam = new ControladorSiembra(activity.getApplicationContext()).getSiembrasPendientes();
        }

        try {
            for (int i = 0; i < jsonParam.length(); i++) {
                try {
                    JSONObject c = jsonParam.getJSONObject(i);
                    //Creating a multi part request
                    String uploadId = method + "_" + UUID.randomUUID().toString();
                    MultipartUploadRequest request = new MultipartUploadRequest(activity.getApplicationContext(), uploadId, "https://cortevasemillasconosur.com/rd/rd_api_rest.php");
                    if (c.has("imagen")) {
                        if(c.getString("imagen").contains(":")){
                            //hay muchas imagenes
                            String[] myData = c.getString("imagen").split(":");
                            for (String s : myData) {
                                request.addFileToUpload(s, "imagen[]");
                            }
                        }else if(!c.getString("imagen").equals(null)){
                            request.addFileToUpload(c.getString("imagen"), "imagen[]");
                        }
                    }
                    request.addParameter("accion", method)
                            .addParameter("user", "MobileApp")
                            .addParameter("pwd", "12345")
                            .addParameter("datos", c.toString()) //Adding text parameter to the request
                            .setUtf8Charset()
                            //Las notificaciones son obligatorias
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //La respuesta del envio se recibe en MyReceiver
                } catch (Exception exc) {
                    Toast.makeText(activity.getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            //Log.e("SINCRONIZACION SENDING", "Exception: " + e.getMessage());
        }
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
}