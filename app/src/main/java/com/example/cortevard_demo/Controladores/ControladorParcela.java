package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorParcela extends AdminSQLiteOpenHelper {


    public ControladorParcela(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idParcela, String latitud, String longitud, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idParcela", idParcela);
        values.put("latitud", latitud);
        values.put("longitud", longitud);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idParcela = ?";
        String[] whereArgs = { Integer.toString(idParcela) };
        boolean success = bd.update("parcela", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("parcela", null, values) > 0;
        }
        bd.close();
        return success;
    }


    public ArrayList<String> getParcelas(){
        ArrayList<String> listaParcelas=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT latitud, longitud FROM parcelas WHERE activo=1 ORDER BY latitud ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaParcelas.add(cursor.getString(cursor.getColumnIndex("latitud")));
                listaParcelas.add(cursor.getString(cursor.getColumnIndex("longitud")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaParcelas;
    }
}
