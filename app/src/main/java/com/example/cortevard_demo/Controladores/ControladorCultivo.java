package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorCultivo extends AdminSQLiteOpenHelper {


    public ControladorCultivo(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idCultivo, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCultivo", idCultivo);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idCultivo = ?";
        String[] whereArgs = { Integer.toString(idCultivo) };
        boolean success = bd.update("cultivo", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("cultivo", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getCultivos(){
        ArrayList<String> listaCultivo=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM cultivo WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaCultivo.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaCultivo;
    }
}

