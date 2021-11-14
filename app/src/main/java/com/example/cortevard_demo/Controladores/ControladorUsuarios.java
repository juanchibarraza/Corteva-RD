package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**

 */
public class ControladorUsuarios extends AdminSQLiteOpenHelper {

    public ControladorUsuarios(Context context) {
        super(context);
    }

    public boolean InsertOrUpdate(Integer idUsuario, String user, String password, String nombre, Integer activo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idUsuario", idUsuario);
        values.put("user", user);
        values.put("password", password);
        values.put("nombre", nombre);
        values.put("activo", activo);
        //Intento hacer un UPDATE si falla hago INSERT
        String where = "idUsuario = ?";
        String[] whereArgs = { Integer.toString(idUsuario) };
        boolean success = db.update("usuarios", values, where, whereArgs) > 0;
        if (!success){
            success = db.insert("usuarios", null, values) > 0;
        }
        db.close();
        return success;
    }

    public ArrayList<String> getUsuarios(){
        ArrayList<String> listaUsuarios=new ArrayList<String>();
        SQLiteDatabase baseDeDatos=this.getReadableDatabase();
        String consulta="SELECT nombre FROM usuarios WHERE activo=1 ORDER BY nombre ASC";
        Cursor cursor=baseDeDatos.rawQuery(consulta, null);
        if(cursor.moveToFirst()){
            listaUsuarios.add("");
            do{
                listaUsuarios.add(cursor.getString(cursor.getColumnIndex("nombre")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        baseDeDatos.close();
        return listaUsuarios;
    }

    public String validarUsuario(String Email, String Password){
        String sql = "SELECT nombre, password FROM usuarios WHERE user ='" + Email + "' COLLATE NOCASE;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String nombre = null;
        if (cursor.moveToFirst()) {
            String hassPass = cursor.getString(cursor.getColumnIndex("password"));
            BCrypt.Result result = BCrypt.verifyer(BCrypt.Version.VERSION_2Y)
                    .verifyStrict(Password.getBytes(StandardCharsets.UTF_8), hassPass.getBytes(StandardCharsets.UTF_8));
            if (result.verified) {
                nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            }
        }
        cursor.close();
        db.close();
        return nombre;
    }
}
