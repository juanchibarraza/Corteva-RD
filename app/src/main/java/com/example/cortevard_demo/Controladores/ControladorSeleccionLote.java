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

public class ControladorSeleccionLote extends AdminSQLiteOpenHelper {

    public ControladorSeleccionLote(Context context){
        super(context);
    }

    public boolean InsertOrUpdate(String coordReferencia, String cultivoAntecesor, String volumenRastrojo, String coberturaRastrojo,
                                  String gradoEnmalezamiento, String malezaPresente, String malezaPresenteChild, String observacionAccesoLote,
                                  String observacionHistorialLote, String imagenSeleccionLote, String fecha, String campana, String estacion,
                                  String cultivo, String localidad, String usuario){

        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("coordReferencia", coordReferencia);
        values.put("cultivoAntecesor", cultivoAntecesor);
        values.put("volumenRastrojo", volumenRastrojo);
        values.put("coberturaRastrojo", coberturaRastrojo);
        values.put("gradoEnmalezamiento", gradoEnmalezamiento);
        values.put("malezaPresente", malezaPresente);
        values.put("malezaPresenteChild", malezaPresenteChild);
        values.put("observacionAccesoLote", observacionAccesoLote);
        values.put("observacionHistorialLote", observacionHistorialLote);
        values.put("imagenSeleccionLote", imagenSeleccionLote);
        values.put("syncro", 0);
        values.put("fecha", fecha);
        values.put("campana", campana);
        values.put("estacion", estacion);
        values.put("cultivo",  cultivo);
        values.put("localidad", localidad);
        String fh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("Fecha_Hora", fh);
        values.put("usuario", usuario);

        boolean resultado = bd.insert("seleccionLote", null, values)>0;
        bd.close();
        return resultado;

    }

    public JSONArray getSeleccionLotePendientes() {
        JSONArray rows = new JSONArray();
        String sql = "SELECT * FROM seleccionLote WHERE syncro='0'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                try{
                    JSONObject row = new JSONObject();
                    row.put("idSeleccionLote", cursor.getInt(cursor.getColumnIndex("idSeleccionLote")));
                    row.put("coordReferencia", cursor.getString(cursor.getColumnIndex("coordReferencia")));
                    row.put("cultivoAntecesor", cursor.getString(cursor.getColumnIndex("cultivoAntecesor")));
                    row.put("volumenRastrojo", cursor.getString(cursor.getColumnIndex("volumenRastrojo")));
                    row.put("coberturaRastrojo", cursor.getString(cursor.getColumnIndex("coberturaRastrojo")));
                    row.put("gradoEnmalezamiento", cursor.getString(cursor.getColumnIndex("gradoEnmalezamiento")));
                    row.put("malezaPresente", cursor.getString(cursor.getColumnIndex("malezaPresente")));
                    row.put("malezaPresenteChild", cursor.getString(cursor.getColumnIndex("malezaPresenteChild")));
                    row.put("observacionAccesoLote", cursor.getString(cursor.getColumnIndex("observacionAccesoLote")));
                    row.put("observacionHistorialLote", cursor.getString(cursor.getColumnIndex("observacionHistorialLote")));
                    row.put("imagen", cursor.getString(cursor.getColumnIndex("imagenSeleccionLote")));
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

    public Boolean setSeleccionLotePendientes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues values = new ContentValues();
            values.put("syncro", 1);
            String where = "idSeleccionLote = ?";
            String[] whereArgs = { id };
            success = db.update("seleccionLote", values, where, whereArgs) > 0;
        } catch (Exception e) {
            success = false;
        }
        db.close();
        return success;
    }

}
