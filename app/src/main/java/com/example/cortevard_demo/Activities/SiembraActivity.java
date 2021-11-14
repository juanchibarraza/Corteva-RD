    package com.example.cortevard_demo.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.cortevard_demo.Controladores.ControladorMaleza;
import com.example.cortevard_demo.Controladores.ControladorSembradora;
import com.example.cortevard_demo.Controladores.ControladorSiembra;
import com.example.cortevard_demo.Controladores.ControladorTractor;
import com.example.cortevard_demo.Controladores.ControladorUsuarios;
import com.example.cortevard_demo.FilePath;
import com.example.cortevard_demo.OfflineGPS;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.SaveSharedPreference;
import com.example.cortevard_demo.Syncronizer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class SiembraActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 23;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 24;
    ImageSwitcher switcherImage;
    ImageView imageView, viewImage;
    String filePath;
    Uri imageUri;
    Spinner spinnerSembradora, spinnerOperario, spinnerTractor, spinnerHumedad, spinnerProfundidad,
            spinnerDensidad, spinnerDosisN, spinnerDosisP, spinnerEnmalezamiento, spinnerMaleza1, spinnerSiembraMalezaAgregado;
    EditText etLatitud, etLongitud, etObservaciones;
    int counter = 2;
    FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<Uri> imageUris;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siembra);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Siembra");

        Bundle bundle = this.getIntent().getExtras();
        final String txtFecha = (String) bundle.get("fecha");
        final String txtCampana = (String) bundle.get("campana");
        final String txtEstacion = (String) bundle.get("estacion");
        final String txtCultivo = (String) bundle.get("cultivo");
        final String txtLocalidad = (String) bundle.get("localidad");
        final String txtUsuario = SaveSharedPreference.getLoggedEmail(getApplicationContext());

        spinnerSembradora = findViewById(R.id.spinnerSiembraSembradora);
        spinnerOperario = findViewById(R.id.spinnerSiembraOperario);
        spinnerTractor = findViewById(R.id.spinnerSiembraTractor);
        spinnerHumedad = findViewById(R.id.spinnerSiembraHumedad);
        spinnerProfundidad = findViewById(R.id.spinnerSiembraProfundidad);
        spinnerDensidad = findViewById(R.id.spinnerSiembraDensidad);
        spinnerDosisN = findViewById(R.id.spinnerSiembraDosisN);
        spinnerDosisP = findViewById(R.id.spinnerSiembraDosisP);
        spinnerEnmalezamiento = findViewById(R.id.spinnerSiembraEnmalezamiento);
        spinnerMaleza1 = findViewById(R.id.spinnerSiembraMaleza);
        etObservaciones = findViewById(R.id.etSiembraObservaciones);

        etLatitud = findViewById(R.id.etSiembraLatitud);
        etLongitud = findViewById(R.id.etSiembraLongitud);

        Slider slider = findViewById(R.id.slider);

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                //NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                //currencyFormat.setCurrency(Currency.getInstance("USD"));
                return value + "%";
            }
        });

        ArrayList<String> sembradora = new ControladorSembradora(this).getSembradoras();
        ArrayAdapter<String> adapterSembradoras = new ArrayAdapter<>(this, R.layout.spinner_item, sembradora);
        spinnerSembradora.setAdapter(adapterSembradoras);

        ArrayList<String> operarios = new ControladorUsuarios(this).getUsuarios();
        ArrayAdapter<String> adapterOperarios = new ArrayAdapter<>(this, R.layout.spinner_item, operarios);
        spinnerOperario.setAdapter(adapterOperarios);

        ArrayList<String> tractores = new ControladorTractor(this).getTractores();
        ArrayAdapter<String> adapterTractor = new ArrayAdapter<>(this, R.layout.spinner_item, tractores);
        spinnerTractor.setAdapter(adapterTractor);

        String [] humedad = new String[]{"BAJA","MEDIA", "ALTA"};
        ArrayAdapter<String> adapterHumedad = new ArrayAdapter<>(this,R.layout.spinner_item,humedad);
        spinnerHumedad.setAdapter(adapterHumedad);

        String [] profundidad = new String[]{"2.5","3", "3.5", "4", "4.5", "5", "5.5", "6"};
        ArrayAdapter<String> adapterProfundidad = new ArrayAdapter<>(this,R.layout.spinner_item,profundidad);
        spinnerProfundidad.setAdapter(adapterProfundidad);

        String [] densidad = new String[]{"60","65", "70", "75", "80", "85", "90"};
        ArrayAdapter<String> adapterDensidad = new ArrayAdapter<>(this,R.layout.spinner_item,densidad);
        spinnerDensidad.setAdapter(adapterDensidad);

        String [] dosisN = new String[]{"50","75", "100", "150", "200", "250"};
        ArrayAdapter<String> adapterDosisN = new ArrayAdapter<>(this,R.layout.spinner_item,dosisN);
        spinnerDosisN.setAdapter(adapterDosisN);

        String [] dosisP = new String[]{"50","75", "100", "150", "200", "250"};
        ArrayAdapter<String> adapterdosisP = new ArrayAdapter<>(this,R.layout.spinner_item,dosisP);
        spinnerDosisP.setAdapter(adapterdosisP);

        String [] enmalezamiento = new String[]{"NULO","BAJO", "MEDIO", "ALTO"};
        ArrayAdapter<String> adapterEnmalezamiento = new ArrayAdapter<>(this,R.layout.spinner_item,enmalezamiento);
        spinnerEnmalezamiento.setAdapter(adapterEnmalezamiento);

        ArrayList<String> maleza = new ControladorMaleza(this).getMalezas();
        ArrayAdapter<String> adapterMaleza = new ArrayAdapter<>(this, R.layout.spinner_item, maleza);
        spinnerMaleza1.setAdapter(adapterMaleza);

        //BOTON AGREGAR MALEZAS
        final LinearLayout contenedorMaleza=(LinearLayout) findViewById(R.id.maleza);
        Button botonAgregar = findViewById(R.id.btnMaleza);
        botonAgregar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (counter != 0){

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View child = inflater.inflate(R.layout.malezas, null);
                    contenedorMaleza.addView(child);
                    spinnerSiembraMalezaAgregado = child.findViewById(R.id.spinnerSiembraMalezaAgregado);
                    spinnerSiembraMalezaAgregado.setAdapter(adapterMaleza);
                    counter--;

                }else{

                    botonAgregar.setEnabled(false);
                    Toast.makeText(SiembraActivity.this,"Límite de malezas alcanzado.",Toast.LENGTH_LONG).show();

                }
            }
        });

        //BOTON FOTO DESDE CAMARA
        final LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        final LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        final Button botonFoto=findViewById(R.id.botonFotoSiembra);
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
        final Button botonElegirFoto=findViewById(R.id.botonElegirFotoSiembra);
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

        //BOTON GPS
        Button botonGPS = (Button) findViewById(R.id.btnGPS);
        botonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(SiembraActivity.this, OfflineGPS.class);
                    startActivityForResult(intent,1);
                }catch (Exception e){
                    Toast.makeText(SiembraActivity.this,"Error al buscar coordenadas. " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        //BOTON  GUARDAR
        Button guardarSiembra = (Button) findViewById(R.id.guardarSiembra);
        guardarSiembra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String txtSembradora = spinnerSembradora.getSelectedItem().toString();
                    String txtOperario = spinnerOperario.getSelectedItem().toString();
                    String txtTractor = spinnerTractor.getSelectedItem().toString();
                    String txtHumedad = spinnerHumedad.getSelectedItem().toString();
                    String txtProfundidad = spinnerProfundidad.getSelectedItem().toString();
                    String txtDensidad = spinnerDensidad.getSelectedItem().toString();
                    String txtDosisN = spinnerDosisN.getSelectedItem().toString();
                    String txtDosisP = spinnerDosisP.getSelectedItem().toString();
                    String txtGradoEnmalezamiento = spinnerEnmalezamiento.getSelectedItem().toString();
                    String txtMaleza = spinnerMaleza1.getSelectedItem().toString();
                    String txtMalezaChild = "";
                    for(int z=0; z<contenedorMaleza.getChildCount();z++) {
                        if (contenedorMaleza.getChildAt(z) instanceof RelativeLayout) {
                            RelativeLayout relative = (RelativeLayout) contenedorMaleza.getChildAt(z);
                            for (int i = 0; i < relative.getChildCount(); i++){
                                if (relative.getChildAt(i) instanceof Spinner) {
                                    Spinner malezasAgregadas = (Spinner) relative.getChildAt(i);
                                    txtMalezaChild += malezasAgregadas.getSelectedItem().toString() + "/*/";
                                }
                            }
                        }
                    }
                    if(!txtMalezaChild.equals(null)){
                        //quito el ultimo caracter /*/
                        txtMalezaChild = txtMalezaChild.substring(0, txtMalezaChild.length() - 3);
                    }

                    String txtLatitud = etLatitud.getText().toString();
                    String txtLongitud = etLongitud.getText().toString();

                    float sliderValue = slider.getValue();

                    String txtObservacionesSiembra = etObservaciones.getText().toString();

                    boolean cadenaError = false;

                    if(txtLatitud.trim().length() == 0){
                        cadenaError = true;
                        etLatitud.setError("Por favor, complete este campo.");
                    }
                    if(txtLongitud.trim().length() == 0){
                        cadenaError = true;
                        etLongitud.setError("Por favor, complete este campo.");
                    }
                    if(txtObservacionesSiembra.trim().length() == 0){
                        cadenaError = true;
                        etObservaciones.setError("Por favor, complete este campo.");
                    }

                    //Si todos los IFs estan OK.
                    if (cadenaError == false){

                        boolean resultado = new ControladorSiembra(SiembraActivity.this).InsertOrUpdate(txtSembradora, txtOperario,
                                txtTractor, txtHumedad, txtProfundidad, txtDensidad, txtDosisN, txtDosisP, txtGradoEnmalezamiento,
                                txtMaleza, txtMalezaChild, txtLatitud, txtLongitud, sliderValue, txtObservacionesSiembra, filePath,
                                txtFecha, txtCampana, txtEstacion, txtCultivo, txtLocalidad, txtUsuario);

                        if(resultado){
                            Toast.makeText(SiembraActivity.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                            new Syncronizer(SiembraActivity.this,"setSiembra").execute();
                            Intent intent = new Intent(SiembraActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }else {
                        Toast.makeText(SiembraActivity.this,"Complete todos los campos. ",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(SiembraActivity.this,"Error al enviar los datos. " + e.getMessage(),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SiembraActivity.this,"No hay imágen anterior.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SiembraActivity.this,"No hay imágen posterior.", Toast.LENGTH_SHORT).show();
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
                    //tvCantidadImagenes.setText("Imágenes Adjuntas: 1");
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

        if(requestCode == 1){ /* GPS response */
            if (resultCode == Activity.RESULT_OK || resultCode == 1) {
                Bundle extras = data.getExtras();
                etLongitud.setText(extras.getString("Longitude"));
                etLatitud.setText(extras.getString("Latitude"));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(SiembraActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SiembraActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
            new Syncronizer(SiembraActivity.this, "Completo").execute();
        }

        return super.onOptionsItemSelected(item);

    }*/

}
