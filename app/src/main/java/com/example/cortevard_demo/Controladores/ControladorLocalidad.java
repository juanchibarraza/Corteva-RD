package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorLocalidad extends AdminSQLiteOpenHelper {


    public ControladorLocalidad(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idLocalidad, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idLocalidad", idLocalidad);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idLocalidad = ?";
        String[] whereArgs = { Integer.toString(idLocalidad) };
        boolean success = bd.update("localidad", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("localidad", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getLocalidades(){
        ArrayList<String> listaLocalidad=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM localidad WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaLocalidad.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaLocalidad;
    }
}
