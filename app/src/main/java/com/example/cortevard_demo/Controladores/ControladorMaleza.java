package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorMaleza extends AdminSQLiteOpenHelper {


    public ControladorMaleza(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idMaleza, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idMaleza", idMaleza);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idMaleza = ?";
        String[] whereArgs = { Integer.toString(idMaleza) };
        boolean success = bd.update("maleza", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("maleza", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getMalezas(){
        ArrayList<String> listaMaleza=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM maleza WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaMaleza.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaMaleza;
    }
}
