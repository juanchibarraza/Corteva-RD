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

public class ControladorPulverizacion extends AdminSQLiteOpenHelper {

    public ControladorPulverizacion(Context context){
        super(context);
    }

    public boolean InsertOrUpdate(String objetivo, String tipo, String operario, String equipoAplicador,String orden, String observaciones,
                                  String imagenPulverizacion, String fecha, String campana, String estacion, String cultivo, String localidad,
                                  String usuario , String humedad, String temperatura, String viento){

        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("objetivo", objetivo);
        values.put("tipo", tipo);
        values.put("operario", operario);
        values.put("equipoAplicador", equipoAplicador);
        values.put("orden", orden);
        values.put("observaciones", observaciones);
        values.put("imagenPulverizacion", imagenPulverizacion);
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
        boolean resultado = bd.insert("pulverizacion", null, values)>0;
        bd.close();
        return resultado;
    }

    public JSONArray getPulverizacionesPendientes() {
        JSONArray rows = new JSONArray();
        String sql = "SELECT * FROM pulverizacion WHERE syncro='0'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                try{
                    JSONObject row = new JSONObject();
                    row.put("idPulverizacion", cursor.getInt(cursor.getColumnIndex("idPulverizacion")));
                    row.put("objetivo", cursor.getString(cursor.getColumnIndex("objetivo")));
                    row.put("tipo", cursor.getString(cursor.getColumnIndex("tipo")));
                    row.put("operario", cursor.getString(cursor.getColumnIndex("operario")));
                    row.put("equipoAplicador", cursor.getString(cursor.getColumnIndex("equipoAplicador")));
                    row.put("orden", cursor.getString(cursor.getColumnIndex("orden")));
                    row.put("observaciones", cursor.getString(cursor.getColumnIndex("observaciones")));
                    row.put("imagen", cursor.getString(cursor.getColumnIndex("imagenPulverizacion")));
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

    public Boolean setPulverizacionesPendientes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues values = new ContentValues();
            values.put("syncro", 1);
            String where = "idPulverizacion = ?";
            String[] whereArgs = { id };
            success = db.update("pulverizacion", values, where, whereArgs) > 0;
        } catch (Exception e) {
            success = false;
        }
        db.close();
        return success;
    }
}
