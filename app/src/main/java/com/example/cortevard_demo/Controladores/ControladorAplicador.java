package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.util.ArrayList;

public class ControladorAplicador extends AdminSQLiteOpenHelper {


    public ControladorAplicador(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idAplicador, String titulo, Integer activo){

        SQLiteDatabase bd = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("idAplicador", idAplicador);
        values.put("titulo", titulo);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idAplicador = ?";
        String[] whereArgs = { Integer.toString(idAplicador) };
        boolean success = bd.update("aplicador", values, where, whereArgs) > 0;
        if (!success){
            success = bd.insert("aplicador", null, values) > 0;
        }
        bd.close();
        return success;
    }

    public ArrayList<String> getAplicador(){
        ArrayList<String> listaAplicador=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT titulo FROM aplicador WHERE activo=1 ORDER BY titulo ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            do{
                listaAplicador.add(cursor.getString(cursor.getColumnIndex("titulo")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaAplicador;
    }
}