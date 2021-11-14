package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControladorVueloConDron extends AdminSQLiteOpenHelper {

    public ControladorVueloConDron(Context context){
        super(context);
    }

    public boolean InsertOrUpdate(String tipoVuelo, String dron, String estadioFenologico, String numeroBaterias,
                                  String numeroTarjetaDeMemoria, String piloto, String observador,
                                  String parcelasFenotipadas, String observacionesVueloConDron, String imagenVueloConDron, String fecha, String campana, String estacion,
                                  String cultivo, String localidad, String usuario, String humedad, String temperatura, String viento){

        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("tipoVuelo", tipoVuelo);
        values.put("dron", dron);
        values.put("estadioFenologico", estadioFenologico);
        values.put("numeroBaterias", numeroBaterias);
        values.put("numeroTarjetaDeMemoria", numeroTarjetaDeMemoria);
        values.put("piloto", piloto);
        values.put("observador", observador);
        values.put("parcelasFenotipadas", parcelasFenotipadas);
        values.put("observacionesVueloConDron", observacionesVueloConDron);
        values.put("imagenVueloConDron", imagenVueloConDron);
        values.put("syncro", 0);
        values.put("fecha", fecha);
        values.put("campana", campana);
        values.put("estacion", estacion);
        values.put("cultivo",  cultivo);
        values.put("localidad", localidad);
        String fh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("Fecha_Hora", fh);
        values.put("usuario", usuario);
        values.put("temperatura", temperatura);
        values.put("viento",  viento);
        values.put("humedad", humedad);

        boolean resultado = bd.insert("vueloConDron", null, values)>0;
        bd.close();
        return resultado;

    }

    public JSONArray getVuelosPendientes() {
        JSONArray rows = new JSONArray();
        String sql = "SELECT * FROM vueloConDron WHERE syncro='0'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                try{
                    JSONObject row = new JSONObject();
                    row.put("idVueloConDron", cursor.getInt(cursor.getColumnIndex("idVueloConDron")));
                    row.put("tipoVuelo", cursor.getString(cursor.getColumnIndex("tipoVuelo")));
                    row.put("dron", cursor.getString(cursor.getColumnIndex("dron")));
                    row.put("estadioFenologico", cursor.getString(cursor.getColumnIndex("estadioFenologico")));
                    row.put("numeroBaterias", cursor.getString(cursor.getColumnIndex("numeroBaterias")));
                    row.put("numeroTarjetaDeMemoria", cursor.getString(cursor.getColumnIndex("numeroTarjetaDeMemoria")));
                    row.put("piloto", cursor.getString(cursor.getColumnIndex("piloto")));
                    row.put("observador", cursor.getString(cursor.getColumnIndex("observador")));
                    row.put("parcelasFenotipadas", cursor.getString(cursor.getColumnIndex("parcelasFenotipadas")));
                    row.put("observacionesVueloConDron", cursor.getString(cursor.getColumnIndex("observacionesVueloConDron")));
                    row.put("imagen", cursor.getString(cursor.getColumnIndex("imagenVueloConDron")));
                    row.put("fecha", cursor.getString(cursor.getColumnIndex("fecha")));
                    row.put("campana", cursor.getString(cursor.getColumnIndex("campana")));
                    row.put("estacion", cursor.getString(cursor.getColumnIndex("estacion")));
                    row.put("cultivo", cursor.getString(cursor.getColumnIndex("cultivo")));
                    row.put("localidad", cursor.getString(cursor.getColumnIndex("localidad")));
                    row.put("Fecha_Hora", cursor.getString(cursor.getColumnIndex("Fecha_Hora")));
                    row.put("viento", cursor.getString(cursor.getColumnIndex("viento")));
                    row.put("temperatura", cursor.getString(cursor.getColumnIndex("temperatura")));
                    row.put("humedad", cursor.getString(cursor.getColumnIndex("humedad")));
                    row.put("usuario", cursor.getString(cursor.getColumnIndex("usuario")));
                    rows.put(row);
                } catch (Exception e) {
                    //Log.e("getReportesPendientes", e.getMessage().toString());
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return rows;
    }

    public Boolean setVuelosPendientes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues values = new ContentValues();
            values.put("syncro", 1);
            String where = "idVueloConDron = ?";
            String[] whereArgs = { id };
            success = db.update("vueloConDron", values, where, whereArgs) > 0;
        } catch (Exception e) {
            success = false;
        }
        db.close();
        return success;
    }

}
