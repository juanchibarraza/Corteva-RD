package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorCarreton extends AdminSQLiteOpenHelper {


    public ControladorCarreton(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idCarreton, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCarreton", idCarreton);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idCarreton = ?";
        String[] whereArgs = { Integer.toString(idCarreton) };
        boolean success = bd.update("carreton", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("carreton", null, values) > 0;
        }
        bd.close();
        return success;
    }

    public ArrayList<String> getCarreton(){
        ArrayList<String> listaCarreton=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM carreton WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaCarreton.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaCarreton;
    }
}