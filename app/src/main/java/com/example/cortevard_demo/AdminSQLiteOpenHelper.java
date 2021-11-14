package com.example.cortevard_demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {


    public AdminSQLiteOpenHelper(Context context) {
        super(context,"rdd",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        try {

            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS usuarios(idUsuario INTEGER primary key AUTOINCREMENT, user text, password text, nombre text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS config(id INTEGER primary key, last_syncro DATETIME)");
            BaseDeDatos.execSQL("insert into config (id, last_syncro) values (1, '2021-05-30 10:00:00')");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS campana(idCampana INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS estacion(idEstacion INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS cultivo(idCultivo INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS localidad(idLocalidad INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS parcela(idParcela INTEGER primary key AUTOINCREMENT, latitud text, longitud text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS sembradora(idSembradora INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS tractor(idTractor INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS maleza(idMaleza INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS trait(idTrait INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS aplicador(idAplicador INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS dron(idDron INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS cosechadora(idCosechadora INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS carreton(idCarreton INTEGER primary key AUTOINCREMENT, titulo text, activo INTEGER)");

            //Siembra
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS siembra(idSiembra INTEGER primary key AUTOINCREMENT, sembradora TEXT, operario TEXT," +
                    "tractor TEXT, humedad TEXT, profundidad TEXT, densidad TEXT, dosisN TEXT, dosisP TEXT, gradoEnmalezamiento TEXT, maleza TEXT," +
                    "malezaChild TEXT, latitud TEXT, longitud TEXT, porcentajeAvance FLOAT, observaciones TEXT, imagenSiembra TEXT, syncro INTEGER," +
                    "fecha TEXT, campana TEXT, estacion TEXT, cultivo TEXT, localidad TEXT, Fecha_Hora DATETIME, usuario TEXT)");

            //Pulverizacion
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS pulverizacion(idPulverizacion INTEGER primary key AUTOINCREMENT, objetivo TEXT, tipo TEXT," +
                    "operario TEXT, equipoAplicador TEXT, orden TEXT, observaciones TEXT, imagenPulverizacion TEXT, syncro INTEGER," +
                    "fecha TEXT, campana TEXT, estacion TEXT, cultivo TEXT, localidad TEXT, Fecha_Hora DATETIME, usuario TEXT, viento TEXT," +
                    "humedad TEXT, temperatura TEXT)");

            //Fenotipado
            //Seleccion de Lote
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS seleccionLote(idSeleccionLote INTEGER primary key AUTOINCREMENT, coordReferencia TEXT, cultivoAntecesor TEXT," +
                    "volumenRastrojo TEXT, coberturaRastrojo TEXT, gradoEnmalezamiento TEXT, malezaPresente TEXT, malezaPresenteChild TEXT," +
                    "observacionAccesoLote TEXT, observacionHistorialLote TEXT, imagenSeleccionLote TEXT, syncro INTEGER," +
                    "fecha TEXT, campana TEXT, estacion TEXT, cultivo TEXT, localidad TEXT, Fecha_Hora DATETIME, usuario TEXT)");

            //Vuelo con Dron
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS vueloConDron(idVueloConDron INTEGER primary key AUTOINCREMENT, tipoVuelo TEXT," +
                    "dron TEXT, estadioFenologico TEXT, numeroBaterias TEXT, numeroTarjetaDeMemoria TEXT, piloto TEXT," +
                    "observador TEXT, parcelasFenotipadas TEXT, observacionesVueloConDron TEXT, imagenVueloConDron TEXT, syncro INTEGER," +
                    "fecha TEXT, campana TEXT, estacion TEXT, cultivo TEXT, localidad TEXT, Fecha_Hora DATETIME, usuario TEXT, viento TEXT," +
                    "humedad TEXT, temperatura TEXT)");

            //Toma de Datos
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS tomaDeDatos(idTomaDeDatos INTEGER primary key AUTOINCREMENT, estadioFenologico TEXT, traits TEXT," +
                    "traitsChild text, operarios TEXT, parcelasFenotipadas TEXT, AOIScore TEXT, gradoEnmalezamiento TEXT, presionInsectos TEXT," +
                    "gradoEstres TEXT, observacion TEXT, imagenTomaDeDatos TEXT, syncro INTEGER," +
                    "fecha TEXT, campana TEXT, estacion TEXT, cultivo TEXT, localidad TEXT, Fecha_Hora DATETIME, usuario TEXT)");

            //Cosecha
            BaseDeDatos.execSQL("CREATE TABLE IF NOT EXISTS cosecha(idCosecha INTEGER primary key AUTOINCREMENT, cosechadora TEXT, operario TEXT, carreton TEXT, " +
                    "perdidaCosecha TEXT, MSTBordura TEXT, porcentajeAvance FLOAT, observaciones TEXT, imagenCosecha TEXT, syncro INTEGER," +
                    "fecha TEXT, campana TEXT, estacion TEXT, cultivo TEXT, localidad TEXT, Fecha_Hora DATETIME, usuario TEXT)");

        } catch (Exception er) {
            Log.e("Error", "Error al crear DB "+ er.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<3){
            // Drop older tables if existed
            db.execSQL("DROP TABLE IF EXISTS usuarios");
            db.execSQL("DROP TABLE IF EXISTS campana");
            db.execSQL("DROP TABLE IF EXISTS estacion");
            db.execSQL("DROP TABLE IF EXISTS cultivo");
            db.execSQL("DROP TABLE IF EXISTS localidad");
            db.execSQL("DROP TABLE IF EXISTS parcela");
            db.execSQL("DROP TABLE IF EXISTS sembradora");
            db.execSQL("DROP TABLE IF EXISTS tractor");
            db.execSQL("DROP TABLE IF EXISTS maleza");
            db.execSQL("DROP TABLE IF EXISTS trait");
            db.execSQL("DROP TABLE IF EXISTS aplicador");
            db.execSQL("DROP TABLE IF EXISTS dron");
            db.execSQL("DROP TABLE IF EXISTS cosechadora");
            db.execSQL("DROP TABLE IF EXISTS carreton");
            db.execSQL("DROP TABLE IF EXISTS siembra");
            db.execSQL("DROP TABLE IF EXISTS pulverizacion");
            db.execSQL("DROP TABLE IF EXISTS seleccionLote");
            db.execSQL("DROP TABLE IF EXISTS vueloConDron");
            db.execSQL("DROP TABLE IF EXISTS tomaDeDatos");
            db.execSQL("DROP TABLE IF EXISTS cosecha");
        }
        // Create tables again
        onCreate(db);
    }

    public void onDownUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS campana");
        db.execSQL("DROP TABLE IF EXISTS estacion");
        db.execSQL("DROP TABLE IF EXISTS cultivo");
        db.execSQL("DROP TABLE IF EXISTS localidad");
        db.execSQL("DROP TABLE IF EXISTS parcelas");
        db.execSQL("DROP TABLE IF EXISTS sembradora");
        db.execSQL("DROP TABLE IF EXISTS tractor");
        db.execSQL("DROP TABLE IF EXISTS maleza");
        db.execSQL("DROP TABLE IF EXISTS trait");
        // Create tables again
        onCreate(db);
    }
}