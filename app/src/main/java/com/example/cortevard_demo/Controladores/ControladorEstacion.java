package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorEstacion extends AdminSQLiteOpenHelper {


    public ControladorEstacion(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idEstacion, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idEstacion", idEstacion);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idEstacion = ?";
        String[] whereArgs = { Integer.toString(idEstacion) };
        boolean success = bd.update("estacion", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("estacion", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getEstaciones(){
        ArrayList<String> listaEstacion=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM estacion WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaEstacion.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaEstacion;
    }
}
