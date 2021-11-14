package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorTrait extends AdminSQLiteOpenHelper {


    public ControladorTrait(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idTrait, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idTrait", idTrait);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idTrait = ?";
        String[] whereArgs = { Integer.toString(idTrait) };
        boolean success = bd.update("trait", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("trait", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getTraits(){
        ArrayList<String> listaTrait=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM trait WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaTrait.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaTrait;
    }
}
