package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;

import java.util.ArrayList;

public class ControladorCampana extends AdminSQLiteOpenHelper {


    public ControladorCampana(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idCampana, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCampana", idCampana);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idCampana = ?";
        String[] whereArgs = { Integer.toString(idCampana) };
        boolean success = bd.update("campana", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("campana", null, values) > 0;
        }
        bd.close();
        return success;
    }

    public ArrayList<String> getCampanas(){
        ArrayList<String> listaCampanas=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM campana WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaCampanas.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaCampanas;
    }
}

