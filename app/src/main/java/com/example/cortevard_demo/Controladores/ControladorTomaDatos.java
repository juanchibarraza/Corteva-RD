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

public class ControladorTomaDatos extends AdminSQLiteOpenHelper {

    public ControladorTomaDatos(Context context){
        super(context);
    }

    public boolean InsertOrUpdate(String estadioFenologico, String traits, String traitsChild, String operarios,
                                  String parcelasFenotipadas, String AOIScore, String gradoEnmalezamiento, String presionInsectos,
                                  String gradoEstres, String observacion, String imagenTomaDeDatos, String fecha, String campana, String estacion,
                                  String cultivo, String localidad, String usuario){

        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("estadioFenologico", estadioFenologico);
        values.put("traits", traits);
        values.put("traitsChild", traitsChild);
        values.put("operarios", operarios);
        values.put("parcelasFenotipadas", parcelasFenotipadas);
        values.put("AOIScore", AOIScore);
        values.put("gradoEnmalezamiento", gradoEnmalezamiento);
        values.put("presionInsectos", presionInsectos);
        values.put("gradoEstres", gradoEstres);
        values.put("observacion", observacion);
        values.put("imagenTomaDeDatos", imagenTomaDeDatos);
        values.put("syncro", 0);
        values.put("fecha", fecha);
        values.put("campana", campana);
        values.put("estacion", estacion);
        values.put("cultivo",  cultivo);
        values.put("localidad", localidad);
        String fh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("Fecha_Hora", fh);
        values.put("usuario", usuario);

        boolean resultado = bd.insert("tomaDeDatos", null, values)>0;
        bd.close();
        return resultado;

    }

    public JSONArray getTomaDeDatosPendientes() {
        JSONArray rows = new JSONArray();
        String sql = "SELECT * FROM tomaDeDatos WHERE syncro='0'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                try{
                    JSONObject row = new JSONObject();
                    row.put("idTomaDeDatos", cursor.getInt(cursor.getColumnIndex("idTomaDeDatos")));
                    row.put("estadioFenologico", cursor.getString(cursor.getColumnIndex("estadioFenologico")));
                    row.put("traits", cursor.getString(cursor.getColumnIndex("traits")));
                    row.put("traitsChild", cursor.getString(cursor.getColumnIndex("traitsChild")));
                    row.put("operarios", cursor.getString(cursor.getColumnIndex("operarios")));
                    row.put("parcelasFenotipadas", cursor.getString(cursor.getColumnIndex("parcelasFenotipadas")));
                    row.put("AOIScore", cursor.getString(cursor.getColumnIndex("AOIScore")));
                    row.put("gradoEnmalezamiento", cursor.getString(cursor.getColumnIndex("gradoEnmalezamiento")));
                    row.put("presionInsectos", cursor.getString(cursor.getColumnIndex("presionInsectos")));
                    row.put("gradoEstres", cursor.getString(cursor.getColumnIndex("gradoEstres")));
                    row.put("observacion", cursor.getString(cursor.getColumnIndex("observacion")));
                    row.put("imagen", cursor.getString(cursor.getColumnIndex("imagenTomaDeDatos")));
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

    public Boolean setTomaDeDatosPendientes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues values = new ContentValues();
            values.put("syncro", 1);
            String where = "idTomaDeDatos = ?";
            String[] whereArgs = { id };
            success = db.update("tomaDeDatos", values, where, whereArgs) > 0;
        } catch (Exception e) {
            success = false;
        }
        db.close();
        return success;
    }

}
