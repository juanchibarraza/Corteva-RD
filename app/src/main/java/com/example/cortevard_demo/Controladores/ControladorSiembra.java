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

public class ControladorSiembra extends AdminSQLiteOpenHelper {

    public ControladorSiembra(Context context){
        super(context);
    }

    public boolean InsertOrUpdate(String sembradora, String operario, String tractor, String humedad,
                                  String profundidad, String densidad, String dosisN, String dosisP, String gradoEnmalezamiento,
                                  String maleza, String malezaChild, String latitud, String longitud, float porcentajeAvance,
                                  String observaciones, String imagenSiembra, String fecha, String campana, String estacion,
                                  String cultivo, String localidad, String usuario){

        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("sembradora", sembradora);
        values.put("operario", operario);
        values.put("tractor", tractor);
        values.put("humedad", humedad);
        values.put("profundidad", profundidad);
        values.put("densidad", densidad);
        values.put("dosisN", dosisN);
        values.put("dosisP", dosisP);
        values.put("gradoEnmalezamiento", gradoEnmalezamiento);
        values.put("maleza", maleza);
        values.put("malezaChild", malezaChild);
        values.put("latitud", latitud);
        values.put("longitud", longitud);
        values.put("porcentajeAvance", porcentajeAvance);
        values.put("observaciones", observaciones);
        values.put("imagenSiembra", imagenSiembra);
        values.put("syncro", 0);
        values.put("fecha", fecha);
        values.put("campana", campana);
        values.put("estacion", estacion);
        values.put("cultivo",  cultivo);
        values.put("localidad", localidad);
        String fh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("Fecha_Hora", fh);
        values.put("usuario", usuario);

        boolean resultado = bd.insert("siembra", null, values)>0;
        bd.close();
        return resultado;

    }

    public JSONArray getSiembrasPendientes() {
        JSONArray rows = new JSONArray();
        String sql = "SELECT * FROM siembra WHERE syncro='0'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                try{
                    JSONObject row = new JSONObject();
                    row.put("idSiembra", cursor.getInt(cursor.getColumnIndex("idSiembra")));
                    row.put("sembradora", cursor.getString(cursor.getColumnIndex("sembradora")));
                    row.put("operario", cursor.getString(cursor.getColumnIndex("operario")));
                    row.put("tractor", cursor.getString(cursor.getColumnIndex("tractor")));
                    row.put("humedad", cursor.getString(cursor.getColumnIndex("humedad")));
                    row.put("profundidad", cursor.getString(cursor.getColumnIndex("profundidad")));
                    row.put("densidad", cursor.getString(cursor.getColumnIndex("densidad")));
                    row.put("dosisN", cursor.getString(cursor.getColumnIndex("dosisN")));
                    row.put("dosisP", cursor.getString(cursor.getColumnIndex("dosisP")));
                    row.put("gradoEnmalezamiento", cursor.getString(cursor.getColumnIndex("gradoEnmalezamiento")));
                    row.put("maleza", cursor.getString(cursor.getColumnIndex("maleza")));
                    row.put("malezaChild", cursor.getString(cursor.getColumnIndex("malezaChild")));
                    row.put("latitud", cursor.getString(cursor.getColumnIndex("latitud")));
                    row.put("longitud", cursor.getString(cursor.getColumnIndex("longitud")));
                    row.put("porcentajeAvance", cursor.getString(cursor.getColumnIndex("porcentajeAvance")));
                    row.put("observaciones", cursor.getString(cursor.getColumnIndex("observaciones")));
                    row.put("imagen", cursor.getString(cursor.getColumnIndex("imagenSiembra")));
                    row.put("fecha", cursor.getString(cursor.getColumnIndex("fecha")));
                    row.put("campana", cursor.getString(cursor.getColumnIndex("campana")));
                    row.put("estacion", cursor.getString(cursor.getColumnIndex("estacion")));
                    row.put("cultivo", cursor.getString(cursor.getColumnIndex("cultivo")));
                    row.put("localidad", cursor.getString(cursor.getColumnIndex("localidad")));
                    row.put("Fecha_Hora", cursor.getString(cursor.getColumnIndex("Fecha_Hora")));
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

    public Boolean setSiembrasPendientes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues values = new ContentValues();
            values.put("syncro", 1);
            String where = "idSiembra = ?";
            String[] whereArgs = { id };
            success = db.update("siembra", values, where, whereArgs) > 0;
        } catch (Exception e) {
            success = false;
        }
        db.close();
        return success;
    }
}


