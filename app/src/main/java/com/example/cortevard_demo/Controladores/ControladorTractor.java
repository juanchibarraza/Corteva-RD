package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorTractor extends AdminSQLiteOpenHelper {


    public ControladorTractor(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idTractor, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idTractor", idTractor);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idTractor = ?";
        String[] whereArgs = { Integer.toString(idTractor) };
        boolean success = bd.update("tractor", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("tractor", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getTractores(){
        ArrayList<String> listaTractor=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM tractor WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaTractor.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaTractor;
    }

}
