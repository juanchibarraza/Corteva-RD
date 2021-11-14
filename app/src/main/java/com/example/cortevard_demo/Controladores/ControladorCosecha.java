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

public class ControladorCosecha extends AdminSQLiteOpenHelper {

    public ControladorCosecha(Context context){
        super(context);
    }

    public boolean InsertOrUpdate(String cosechadora, String operario, String carreton, String perdidaCosecha, String MSTBordura,
                                  float porcentajeAvance, String observaciones, String imagenCosecha, String fecha, String campana, String estacion,
                                  String cultivo, String localidad, String usuario){

        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("cosechadora", cosechadora);
        values.put("operario", operario);
        values.put("carreton", carreton);
        values.put("perdidaCosecha", perdidaCosecha);
        values.put("MSTBordura", MSTBordura);
        values.put("porcentajeAvance", porcentajeAvance);
        values.put("observaciones", observaciones);
        values.put("imagenCosecha", imagenCosecha);
        values.put("syncro", 0);
        values.put("fecha", fecha);
        values.put("campana", campana);
        values.put("estacion", estacion);
        values.put("cultivo",  cultivo);
        values.put("localidad", localidad);
        String fh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("Fecha_Hora", fh);
        values.put("usuario", usuario);

        boolean resultado = bd.insert("cosecha", null, values)>0;
        bd.close();
        return resultado;
    }

    public JSONArray getCosechasPendientes() {
        JSONArray rows = new JSONArray();
        String sql = "SELECT * FROM cosecha WHERE syncro='0'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                try{
                    JSONObject row = new JSONObject();
                    row.put("idCosecha", cursor.getInt(cursor.getColumnIndex("idCosecha")));
                    row.put("cosechadora", cursor.getString(cursor.getColumnIndex("cosechadora")));
                    row.put("operario", cursor.getString(cursor.getColumnIndex("operario")));
                    row.put("carreton", cursor.getString(cursor.getColumnIndex("carreton")));
                    row.put("perdidaCosecha", cursor.getString(cursor.getColumnIndex("perdidaCosecha")));
                    row.put("MSTBordura", cursor.getString(cursor.getColumnIndex("MSTBordura")));
                    row.put("porcentajeAvance", cursor.getString(cursor.getColumnIndex("porcentajeAvance")));
                    row.put("observaciones", cursor.getString(cursor.getColumnIndex("observaciones")));
                    row.put("imagen", cursor.getString(cursor.getColumnIndex("imagenCosecha")));
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

    public Boolean setCosechasPendientes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues values = new ContentValues();
            values.put("syncro", 1);
            String where = "idCosecha = ?";
            String[] whereArgs = { id };
            success = db.update("cosecha", values, where, whereArgs) > 0;
        } catch (Exception e) {
            success = false;
        }
        db.close();
        return success;
    }
}