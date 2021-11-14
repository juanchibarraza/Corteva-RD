package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorCosechadora extends AdminSQLiteOpenHelper {


    public ControladorCosechadora(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idCosechadora, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCosechadora", idCosechadora);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idCosechadora = ?";
        String[] whereArgs = { Integer.toString(idCosechadora) };
        boolean success = bd.update("cosechadora", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("cosechadora", null, values) > 0;
        }
        bd.close();
        return success;
    }

    public ArrayList<String> getCosechadora(){
        ArrayList<String> listaCosechadora=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM cosechadora WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaCosechadora.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaCosechadora;
    }
}