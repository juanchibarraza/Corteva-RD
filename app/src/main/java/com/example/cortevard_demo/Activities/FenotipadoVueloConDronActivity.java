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
import com.example.cortevard_demo.Controladores.ControladorDron;
import com.example.cortevard_demo.Controladores.ControladorVueloConDron;
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

public class FenotipadoVueloConDronActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 19;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 20;
    ImageSwitcher switcherImage;
    ImageView imageView, viewImage;
    private ArrayList<Uri> imageUris;
    int position = 0;
    String filePath;
    Uri imageUri;
    Spinner spinnerTipoVuelo, spinnerDron, spinnerEstadioFenologico;
    EditText etNumeroBaterias, etNumeroTarjetasDeMemorias, etPiloto, etObservador, etParcelasFenotipdas, etObservaciones,
    etTemperatura, etHumedad, etViento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fenotipado_vuelo_con_dron);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vuelo con Dron");

        Bundle bundle = this.getIntent().getExtras();
        final String txtFecha = (String) bundle.get("fecha");
        final String txtCampana = (String) bundle.get("campana");
        final String txtEstacion = (String) bundle.get("estacion");
        final String txtCultivo = (String) bundle.get("cultivo");
        final String txtLocalidad = (String) bundle.get("localidad");
        final String txtUsuario = SaveSharedPreference.getLoggedEmail(getApplicationContext());

        spinnerTipoVuelo = findViewById(R.id.spinnerFenotipadoTipoVuelo);
        spinnerDron = findViewById(R.id.spinnerFenotipadoDron);
        spinnerEstadioFenologico = findViewById(R.id.spinnerFenotipadoDronEstadioFenologico);

        etNumeroBaterias = findViewById(R.id.etFenotipadoNumeroBaterias);
        etNumeroTarjetasDeMemorias = findViewById(R.id.etFenotipadoTarjetaDeMemoria);
        etPiloto = findViewById(R.id.etFenotipadoPiloto);
        etObservador = findViewById(R.id.etFenotipadoObservadorVueloConDron);
        etParcelasFenotipdas = findViewById(R.id.etFenotipadoVueloConDronParcelasFenotipadas);
        etObservaciones = findViewById(R.id.etFenotipadoDronObservaciones);
        etTemperatura  = findViewById(R.id.temp);
        etHumedad= findViewById(R.id.hum);
        etViento= findViewById(R.id.windSpeed);

        String [] tipoVuelo = new String[]{"Plot Quality","Plot Score","Maturity","Plant Height","Stay Green","Disease","Otro"};
        ArrayAdapter<String> adapterTipoVuelo = new ArrayAdapter<>(this, R.layout.spinner_item, tipoVuelo);
        spinnerTipoVuelo.setAdapter(adapterTipoVuelo);

        ArrayList<String> dron = new ControladorDron(this).getDron();
        ArrayAdapter<String> adapterDron = new ArrayAdapter<>(this, R.layout.spinner_item, dron);
        spinnerDron.setAdapter(adapterDron);

        String [] EstadioFenologico = new String[]{"VE","V1","V2","V3","V4","V5","VT","R1","R2","R3","R4","R5","R5.2","R5.5","R6"};
        ArrayAdapter<String> adapterEstadioFenologico = new ArrayAdapter<>(this, R.layout.spinner_item, EstadioFenologico);
        spinnerEstadioFenologico.setAdapter(adapterEstadioFenologico);

        //BOTON CLIMA
        final FloatingActionButton botonClima = (FloatingActionButton) findViewById(R.id.btnClima);
        botonClima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(FenotipadoVueloConDronActivity.this, OfflineClima.class);
                    startActivityForResult(intent,2);
                }catch (Exception e){
                    Toast.makeText(FenotipadoVueloConDronActivity.this,"Error al buscar datos climáticos. " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        //BOTON FOTO DESDE CAMARA
        final LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        final LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        final Button botonFoto=findViewById(R.id.botonFotoVueloConDron);
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
        final Button botonElegirFoto=findViewById(R.id.botonElegirFotoVueloConDron);
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

        Button enviarVueloConDron = (Button) findViewById(R.id.enviarVueloConDron);
        enviarVueloConDron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String txtTipoVuelo = spinnerTipoVuelo.getSelectedItem().toString();
                    String txtDron = spinnerDron.getSelectedItem().toString();
                    String txtEstadioFenologico = spinnerEstadioFenologico.getSelectedItem().toString();
                    String txtNumeroBaterias = etNumeroBaterias.getText().toString();
                    String txtNumeroTarjetaDeMemoria = etNumeroTarjetasDeMemorias.getText().toString();
                    String txtPiloto = etPiloto.getText().toString();
                    String txtObservador = etObservador.getText().toString();
                    String txtParcelasFenotipadas = etParcelasFenotipdas.getText().toString();
                    String txtObservacionesVueloConDron = etObservaciones.getText().toString();
                    String txtHumedad = etHumedad.getText().toString();
                    String txtTemperatura = etTemperatura.getText().toString();
                    String txtViento = etViento.getText().toString();

                    boolean cadenaError = false;

                    if(txtNumeroBaterias.trim().length() == 0){
                        cadenaError = true;
                        etNumeroBaterias.setError("Por favor, complete este campo.");
                    }
                    if(txtNumeroTarjetaDeMemoria.trim().length() == 0){
                        cadenaError = true;
                        etNumeroTarjetasDeMemorias.setError("Por favor, complete este campo.");
                    }
                    if(txtPiloto.trim().length() == 0){
                        cadenaError = true;
                        etPiloto.setError("Por favor, complete este campo.");
                    }
                    if(txtObservador.trim().length() == 0){
                        cadenaError = true;
                        etObservador.setError("Por favor, complete este campo.");
                    }
                    if(txtParcelasFenotipadas.trim().length() == 0){
                        cadenaError = true;
                        etParcelasFenotipdas.setError("Por favor, complete este campo.");
                    }
                    if(txtObservacionesVueloConDron.trim().length() == 0){
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

                        boolean resultado = new ControladorVueloConDron(FenotipadoVueloConDronActivity.this).InsertOrUpdate(txtTipoVuelo, txtDron,
                                txtEstadioFenologico, txtNumeroBaterias, txtNumeroTarjetaDeMemoria, txtPiloto, txtObservador,
                                txtParcelasFenotipadas, txtObservacionesVueloConDron, filePath,txtFecha, txtCampana, txtEstacion, txtCultivo, txtLocalidad,
                                txtUsuario, txtHumedad, txtTemperatura, txtViento);

                        if(resultado){

                            Toast.makeText(FenotipadoVueloConDronActivity.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                            new Syncronizer(FenotipadoVueloConDronActivity.this,"setVueloConDron").execute();
                            Intent intent = new Intent(FenotipadoVueloConDronActivity.this, MainActivity.class);
                            startActivity(intent);

                        }

                    }else {

                        Toast.makeText(FenotipadoVueloConDronActivity.this,"Complete todos los campos. ",Toast.LENGTH_LONG).show();

                    }


                }catch (Exception e){

                    Toast.makeText(FenotipadoVueloConDronActivity.this,"Error al enviar los datos. " + e.getMessage(),Toast.LENGTH_LONG).show();

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
                }else {
                    Toast.makeText(FenotipadoVueloConDronActivity.this,"No hay imágen anterior.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position<imageUris.size() - 1){
                    position++;
                    switcherImage.setImageURI(imageUris.get(position));
                }else{
                    Toast.makeText(FenotipadoVueloConDronActivity.this,"No hay imágen posterior.", Toast.LENGTH_SHORT).show();
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
                    //Bitmap bmp = (Bitmap) data.getExtras().get("data"); //Esto traiga un thumbnail
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bmp);
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    //Uri selectedImage = FilePath.getImageUri(getContext(), bmp);
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
        Intent intent = new Intent(FenotipadoVueloConDronActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FenotipadoVueloConDronActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        // super.onBackPressed();
    }
/*
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_syncro, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == R.id.menuSyncro){
            new Syncronizer(FenotipadoVueloConDronActivity.this, "Completo").execute();
        }

        return super.onOptionsItemSelected(item);

    }*/

}
