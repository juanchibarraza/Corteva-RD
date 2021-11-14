package com.example.cortevard_demo.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cortevard_demo.Controladores.ControladorAplicador;
import com.example.cortevard_demo.Controladores.ControladorPulverizacion;
import com.example.cortevard_demo.Controladores.ControladorUsuarios;
import com.example.cortevard_demo.FilePath;
import com.example.cortevard_demo.OfflineClima;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.SaveSharedPreference;
import com.example.cortevard_demo.Syncronizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PulverizacionActivity extends AppCompatActivity {

    Spinner spinnerObjetivo, spinnerTipo, spinnerOperario, spinnerEquipoAplicador;
    EditText etOrden, etObservaciones, etTemperatura, etHumedad, etViento;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 21;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 22;
    ImageSwitcher switcherImage;
    ImageView imageView, viewImage;
    private ArrayList<Uri> imageUris;
    int position = 0;
    String filePath;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulverizacion);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pulverización");

        Bundle bundle = this.getIntent().getExtras();
        final String txtFecha = (String) bundle.get("fecha");
        final String txtCampana = (String) bundle.get("campana");
        final String txtEstacion = (String) bundle.get("estacion");
        final String txtCultivo = (String) bundle.get("cultivo");
        final String txtLocalidad = (String) bundle.get("localidad");
        final String txtUsuario = SaveSharedPreference.getLoggedEmail(getApplicationContext());

        spinnerObjetivo = findViewById(R.id.spinnerPulverizacionObjetivo);
        spinnerTipo = findViewById(R.id.spinnerPulverizacionTipo);
        spinnerOperario = findViewById(R.id.spinnerPulverizacionOperario);
        spinnerEquipoAplicador = findViewById(R.id.spinnerPulverizacionEquipoAplicador);

        etOrden = findViewById(R.id.etPulverizacionOrden);
        etObservaciones = findViewById(R.id.etPulverizacionObservaciones);
        etTemperatura  = findViewById(R.id.temp);
        etHumedad= findViewById(R.id.hum);
        etViento= findViewById(R.id.windSpeed);

        String [] objetivos = new String[]{"Agroquímicos","Fertilización","Otro"};
        ArrayAdapter<String> adapterObjetivos = new ArrayAdapter<>(this,R.layout.spinner_item,objetivos);
        spinnerObjetivo.setAdapter(adapterObjetivos);

        String[] tipo = new String[]{"Pre-emergente","Pre-siembra","Post emergente","Desecante","Barbecho","Nitrogenada","Fosforada","N + P","Otro"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,R.layout.spinner_item,tipo);
        spinnerTipo.setAdapter(adapterTipo);

        ArrayList<String> operarios = new ControladorUsuarios(this).getUsuarios();
        ArrayAdapter<String> adapterOperarios = new ArrayAdapter<>(this, R.layout.spinner_item, operarios);
        spinnerOperario.setAdapter(adapterOperarios);

        ArrayList<String> equipoAplicador = new ControladorAplicador(this).getAplicador();
        ArrayAdapter<String> adapterEquipoAplicador = new ArrayAdapter<>(this, R.layout.spinner_item, equipoAplicador);
        spinnerEquipoAplicador.setAdapter(adapterEquipoAplicador);

        //BOTON CLIMA
        final FloatingActionButton botonClima = (FloatingActionButton) findViewById(R.id.btnClima);
        botonClima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(PulverizacionActivity.this, OfflineClima.class);
                    startActivityForResult(intent,2);
                }catch (Exception e){
                    Toast.makeText(PulverizacionActivity.this,"Error al buscar datos climáticos. " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        //BOTON FOTO DESDE CAMARA
        final LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        final LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        final Button botonFoto=findViewById(R.id.botonFotoPulverizacion);
        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageViewContainer.getVisibility() == v.GONE && imageSwitcherContainer.getVisibility() == v.GONE){
                    inflateCamaraShoot();
                }else if(imageSwitcherContainer.getVisibility() == v.VISIBLE){
                    imageSwitcherContainer.setVisibility(v.GONE);
                    inflateCamaraShoot();
                }
            }
        });

        //BOTON ELEGIR IMAGEN DESDE GALERIA
        final Button botonElegirFoto=findViewById(R.id.botonElegirFotoPulverizacion);
        botonElegirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageSwitcherContainer.getVisibility() == v.GONE && imageViewContainer.getVisibility() == v.GONE){
                    inflateChooseImage();
                }else if(imageViewContainer.getVisibility() == v.VISIBLE){
                    imageViewContainer.setVisibility(v.GONE);
                    inflateChooseImage();
                }
                if(imageViewContainer.getVisibility() == v.VISIBLE){
                    imageViewContainer.setVisibility(v.GONE);
                }
            }
        });

        Button enviarPulverizacion = (Button)findViewById(R.id.botonEnviarPulverizacion);
        enviarPulverizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String txtObjetivo = spinnerObjetivo.getSelectedItem().toString();
                    String txtTipo = spinnerTipo.getSelectedItem().toString();
                    String txtOperario = spinnerOperario.getSelectedItem().toString();
                    String txtEquipoAplicador = spinnerEquipoAplicador.getSelectedItem().toString();
                    String txtOrden = etOrden.getText().toString();
                    String txtObservaciones = etObservaciones.getText().toString();
                    String txtHumedad = etHumedad.getText().toString();
                    String txtTemperatura = etTemperatura.getText().toString();
                    String txtViento = etViento.getText().toString();

                    boolean cadenaError = false;

                    if(txtOrden.trim().length() == 0){
                        cadenaError = true;
                        etOrden.setError("Por favor, complete este campo.");
                    }
                    if(txtObservaciones.trim().length() == 0){
                        cadenaError = true;
                        etObservaciones.setError("Por favor, complete este campo.");
                    }
                    if(txtHumedad.trim().length() == 0){
                        cadenaError = true;
                        etHumedad.setError("Por favor, complete este campo.");
                    }
                    if(txtViento.trim().length() == 0){
                        cadenaError = true;
                        etViento.setError("Por favor, complete este campo.");
                    }
                    if(txtTemperatura.trim().length() == 0){
                        cadenaError = true;
                        etTemperatura.setError("Por favor, complete este campo.");
                    }

                    //Si todos los IFs estan OK.
                    if (cadenaError == false){

                        boolean resultado = new ControladorPulverizacion(PulverizacionActivity.this).InsertOrUpdate(txtObjetivo, txtTipo, txtOperario,
                                txtEquipoAplicador, txtOrden, txtObservaciones, filePath, txtFecha, txtCampana, txtEstacion, txtCultivo, txtLocalidad, txtUsuario, txtHumedad, txtTemperatura, txtViento);

                        if (resultado) {
                            Toast.makeText(PulverizacionActivity.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                            new Syncronizer(PulverizacionActivity.this,"setPulverizacion").execute();
                            Intent intent = new Intent(PulverizacionActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    }else {

                        Toast.makeText(PulverizacionActivity.this,"Complete todos los campos. ",Toast.LENGTH_LONG).show();

                    }

                }catch (Exception e){
                    Toast.makeText(PulverizacionActivity.this,"Error al enviar los datos. " + e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void inflateCamaraShoot(){

        LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View child = inflater.inflate(R.layout.image_view, null);
        imageViewContainer.addView(child);
        imageViewContainer.setVisibility(View.VISIBLE);
        imageView = child.findViewById(R.id.fotoImageView);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "OA_"+timeStamp + ".jpg";
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + imageFileName);
        imageUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void inflateChooseImage(){

        LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View child = inflater.inflate(R.layout.image_switcher, null);
        imageSwitcherContainer.removeAllViews();
        imageSwitcherContainer.addView(child);
        imageSwitcherContainer.setVisibility(View.VISIBLE);
        switcherImage = child.findViewById(R.id.fotoImageSwitcher);
        Button siguiente = child.findViewById(R.id.botonSiguienteImageSwitcher);
        Button anterior = child.findViewById(R.id.botonAnteriorImageSwitcher);
        imageUris = new ArrayList<>();
        switcherImage.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                viewImage = new ImageView(getApplicationContext());
                return viewImage;
            }
        });

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position>0){
                    position--;
                    switcherImage.setImageURI(imageUris.get(position));
                }
            }
        });
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position<imageUris.size() - 1){
                    position++;
                    switcherImage.setImageURI(imageUris.get(position));
                }
            }
        });

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione Imagenes:"), CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            try{
                //si se toma foto desde la camara
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bmp);
                    filePath = FilePath.getPath(this,  imageUri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //si se eligen fotos desde el celu
        if (requestCode == CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE) {
            filePath = "";
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        filePath += FilePath.getPath(this, imageUri) +":";
                        imageUris.add(imageUri);
                    }
                    //quito el ultimo caracter :
                    filePath = filePath.substring(0, filePath.length() - 1);
                    switcherImage.setImageURI(imageUris.get(0));
                    position = 0;
                } else {
                    Uri imageUri = data.getData();
                    filePath = FilePath.getPath(this, imageUri);
                    imageUris.add(imageUri);
                    switcherImage.setImageURI(imageUris.get(0));
                    position = 0;
                }
            }
        }
        if(requestCode == 2){ /* Clima response */
            if (resultCode == Activity.RESULT_OK || resultCode == 1) {
                Bundle extras = data.getExtras();
                etHumedad.setText(extras.getString("Humedad"));
                etTemperatura.setText(extras.getString("Temperatura"));
                etViento.setText(extras.getString("Viento"));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        Intent intent = new Intent(PulverizacionActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PulverizacionActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        // super.onBackPressed();
    }
    /*@Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_syncro, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == R.id.menuSyncro){
            new Syncronizer(PulverizacionActivity.this, "Completo").execute();
        }

        return super.onOptionsItemSelected(item);

    }*/

}