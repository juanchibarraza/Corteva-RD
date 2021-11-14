package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorSembradora extends AdminSQLiteOpenHelper {


    public ControladorSembradora(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idSembradora, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idSembradora", idSembradora);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idSembradora = ?";
        String[] whereArgs = { Integer.toString(idSembradora) };
        boolean success = bd.update("sembradora", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("sembradora", null, values) > 0;
        }
        bd.close();
        return success;
    }

    public ArrayList<String> getSembradoras(){
        ArrayList<String> listaSembradora=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM sembradora WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaSembradora.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaSembradora;
    }
}

