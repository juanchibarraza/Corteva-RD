package com.example.cortevard_demo.Controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cortevard_demo.AdminSQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControladorConfig extends AdminSQLiteOpenHelper {

    public ControladorConfig(Context context) {
        super(context);
    }

    /*public boolean deleteOldData() {

        SQLiteDatabase db = this.getWritableDatabase();
        //ELIMINO TEMPERATURAS VIEJAS Y YA SINCRONIZADAS
        String whereClause = "syncro = ? and fechahora < current_date";
        String[] whereArgs = { "1" };
        db.delete("temperaturas", whereClause, whereArgs);

        //ELIMINO SOLICITUDES VIEJAS Y YA SINCRONIZADAS
        whereClause = "syncro = ? and fecha_recepcion < date('now','-15 days')";
        boolean success = db.delete("solicitudes", whereClause, whereArgs) > 0;
        db.close();
        return success;
    }*/

    public boolean LimpiarBasedeDatos() {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = db.delete("usuarios", null, null) > 0;
        db.close();
        return success;
    }

    public String GetSet_last_syncro() {

        SQLiteDatabase db = this.getWritableDatabase();
        String last = null;

        //Busco el valor actual de last_syncro
        String sql = "SELECT last_syncro FROM config WHERE id = '1'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            last = cursor.getString(cursor.getColumnIndex("last_syncro"));
        }
        cursor.close();
        //Actualizo el valor de last_syncro
        ContentValues values = new ContentValues();
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("last_syncro", currentDateTime);
        String where = "id = ?";
        String[] whereArgs = { "1" };
        db.update("config", values, where, whereArgs);

        db.close();
        //Devuelvo el valor viejo
        return last;
    }

}