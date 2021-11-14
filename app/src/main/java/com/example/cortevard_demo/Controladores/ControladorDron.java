package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorDron extends AdminSQLiteOpenHelper {


    public ControladorDron(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idDron, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idDron", idDron);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idDron = ?";
        String[] whereArgs = { Integer.toString(idDron) };
        boolean success = bd.update("dron", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("dron", null, values) > 0;
        }
        bd.close();
        return success;
    }

    public ArrayList<String> getDron(){
        ArrayList<String> listaDron=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM dron WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaDron.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaDron;
    }
}