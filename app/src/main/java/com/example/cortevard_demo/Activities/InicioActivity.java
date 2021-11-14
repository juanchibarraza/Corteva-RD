package com.example.cortevard_demo.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cortevard_demo.Controladores.ControladorCampana;
import com.example.cortevard_demo.Controladores.ControladorCultivo;
import com.example.cortevard_demo.Controladores.ControladorEstacion;
import com.example.cortevard_demo.Controladores.ControladorLocalidad;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.Syncronizer;

import java.util.ArrayList;
import java.util.Calendar;

public class InicioActivity extends  AppCompatActivity{

    Button botonSiguiente;
    Spinner spinnerCampana, spinnerEstacion, spinnerCultivo, spinnerLocalidad, spinnerActividad, spinnerFenotipado;
    EditText etFecha;
    Class actividadElegida = SiembraActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registro");

        spinnerCampana = findViewById(R.id.spinnerInicioCampana);
        spinnerEstacion = findViewById(R.id.spinnerInicioEstacion);
        spinnerCultivo = findViewById(R.id.spinnerInicioCultivo);
        spinnerLocalidad = findViewById(R.id.spinnerInicioLocalidad);
        spinnerActividad = findViewById(R.id.spinnerInicioActividad);
        spinnerFenotipado = findViewById(R.id.spinnerFenotipado);

        etFecha = findViewById(R.id.inicioFecha);
        //Fecha por defecto
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        etFecha.setText(today.year + "/" + (today.month+1) + "/" + today.monthDay);
        //CALENDARIO
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int anio = calendar.get(Calendar.YEAR);

                DatePickerDialog date = new DatePickerDialog(InicioActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMoth) {
                        String fecha = year + "/" + (month+1) + "/" + dayOfMoth;
                        etFecha.setText(fecha);
                    }
                }, anio, mes, dia);

                date.show();
            }
        });

        ArrayList<String> campanas = new ControladorCampana(this).getCampanas();
        ArrayAdapter<String> adapterCampana = new ArrayAdapter<>(this, R.layout.spinner_item, campanas);
        spinnerCampana.setAdapter(adapterCampana);

        ArrayList<String> estacion = new ControladorEstacion(this).getEstaciones();
        ArrayAdapter<String> adapterEstacion = new ArrayAdapter<>(this, R.layout.spinner_item, estacion);
        spinnerEstacion.setAdapter(adapterEstacion);

        ArrayList<String> cultivos = new ControladorCultivo(this).getCultivos();
        ArrayAdapter<String> adapterCultivos = new ArrayAdapter<>(this, R.layout.spinner_item, cultivos);
        spinnerCultivo.setAdapter(adapterCultivos);

        ArrayList<String> localidades = new ControladorLocalidad(this).getLocalidades();
        ArrayAdapter<String> adapterLocalidades = new ArrayAdapter<>(this, R.layout.spinner_item, localidades);
        spinnerLocalidad.setAdapter(adapterLocalidades);

        String [] enmalezamiento = new String[]{"NULO","BAJO", "MEDIO", "ALTO"};
        ArrayAdapter<String> adapterEnmalezamiento = new ArrayAdapter<>(this,R.layout.spinner_item,enmalezamiento);

        String [] actividad = new String[]{"Cosecha","Fenotipado","Pulverización","Siembra"};
        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,R.layout.spinner_item,actividad);
        spinnerActividad.setAdapter(adapterActividad);

        String [] fenotipado = new String[]{"Selección de Lote", "Vuelo con Dron", "Toma de Datos"};
        ArrayAdapter<String> adapterFenotipado = new ArrayAdapter<>(this, R.layout.spinner_item, fenotipado);
        spinnerFenotipado.setAdapter(adapterFenotipado);

        /*BOTON SIGUIENTE*/
        botonSiguiente = findViewById(R.id.botonSiguienteInicio);
        botonSiguiente.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(InicioActivity.this, R.style.MyDialogTheme);
            LayoutInflater inflater = LayoutInflater.from(InicioActivity.this);
            final View image = inflater.inflate(R.layout.sample, null);
            alert.setView(image);
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    if (etFecha.getText().toString().isEmpty()) {
                        etFecha.setError(Html.fromHtml("<font color='red'>Fecha Requerida.</font>"));
                        Toast.makeText(InicioActivity.this,"Fecha requerida.",Toast.LENGTH_LONG).show();
                    }else{

                        String txtFecha = etFecha.getText().toString();
                        String txtCampana = spinnerCampana.getSelectedItem().toString();
                        String txtEstacion = spinnerEstacion.getSelectedItem().toString();
                        String txtCultivo = spinnerCultivo.getSelectedItem().toString();
                        String txtLocalidad = spinnerLocalidad.getSelectedItem().toString();
                        String txtActividad = spinnerActividad.getSelectedItem().toString();

                        Intent intent = new Intent(InicioActivity.this, actividadElegida);
                        Bundle extras = new Bundle();
                        extras.putString("fecha", txtFecha);
                        extras.putString("campana", txtCampana);
                        extras.putString("estacion", txtEstacion);
                        extras.putString("cultivo", txtCultivo);
                        extras.putString("localidad", txtLocalidad);
                        extras.putString("actividad", txtActividad);
                        intent.putExtras(extras);

                        startActivity(intent);
                    }
                }
            });
            alert.show();
        });

        spinnerActividad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(adapterView.getItemAtPosition(i).equals("Siembra")){
                    LinearLayout LL = findViewById(R.id.Linear_Layout_fenotipado);
                    if(LL.getVisibility()== View.VISIBLE){
                        LL.setVisibility(View.GONE);
                    }
                    actividadElegida = SiembraActivity.class;
                }

                if(adapterView.getItemAtPosition(i).equals("Pulverización")){
                    LinearLayout LL = findViewById(R.id.Linear_Layout_fenotipado);
                    if(LL.getVisibility()== View.VISIBLE){
                        LL.setVisibility(View.GONE);
                    }
                    actividadElegida = PulverizacionActivity.class;
                }

                if(adapterView.getItemAtPosition(i).equals("Fenotipado")){
                    LinearLayout LL = findViewById(R.id.Linear_Layout_fenotipado);
                    if(LL.getVisibility()== View.GONE){
                        LL.setVisibility(View.VISIBLE);
                    }else{
                        LL.setVisibility(View.GONE);
                    }

                    spinnerFenotipado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView2, View view, int position, long l) {

                            if(adapterView2.getItemAtPosition(position).equals("Selección de Lote")){
                                actividadElegida = FenotipadoSeleccionDeLoteActivity.class;
                            }

                            if(adapterView2.getItemAtPosition(position).equals("Vuelo con Dron")){
                                actividadElegida = FenotipadoVueloConDronActivity.class;
                            }

                            if(adapterView2.getItemAtPosition(position).equals("Toma de Datos")) {
                                actividadElegida = FenotipadoTomaDeDatosActivity.class;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }

                if(adapterView.getItemAtPosition(i).equals("Cosecha")){

                    LinearLayout LL = findViewById(R.id.Linear_Layout_fenotipado);
                    if(LL.getVisibility()== View.VISIBLE){
                        LL.setVisibility(View.GONE);
                    }
                    actividadElegida = CosechaActivity.class;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        Intent intent = new Intent(InicioActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(InicioActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_syncro, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == R.id.menuSyncro){
            new Syncronizer(InicioActivity.this, "Completo").execute();
        }

        return super.onOptionsItemSelected(item);

    }


}


