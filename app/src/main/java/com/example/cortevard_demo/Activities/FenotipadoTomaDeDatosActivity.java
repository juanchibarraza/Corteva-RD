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
import android.view.View;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.cortevard_demo.Controladores.ControladorTomaDatos;
import com.example.cortevard_demo.Controladores.ControladorTrait;
import com.example.cortevard_demo.FilePath;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.SaveSharedPreference;
import com.example.cortevard_demo.Syncronizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FenotipadoTomaDeDatosActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 17;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 18;
    ImageSwitcher switcherImage;
    ImageView imageView, viewImage;
    private ArrayList<Uri> imageUris;
    int position = 0;
    String filePath;
    Uri imageUri;
    Spinner spinnerEstadio, spinnerTraits, spinnerAOIScore, spinnerEnmalezamiento, spinnerPresionInsectos,
    spinnerGradoEstres, spinnerTraitAgregado;
    EditText etOperarios, etParcelasFenotipadas, etObservacionTomaDatos;
    int counter = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fenotipado_toma_de_datos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Toma de Datos");

        Bundle bundle = this.getIntent().getExtras();
        final String txtFecha = (String) bundle.get("fecha");
        final String txtCampana = (String) bundle.get("campana");
        final String txtEstacion = (String) bundle.get("estacion");
        final String txtCultivo = (String) bundle.get("cultivo");
        final String txtLocalidad = (String) bundle.get("localidad");
        final String txtUsuario = SaveSharedPreference.getLoggedEmail(getApplicationContext());

        spinnerEstadio = findViewById(R.id.spinnerFenotipadoEstadioFenologico);
        spinnerTraits = findViewById(R.id.spinnerTomaDatosTrait);
        spinnerAOIScore = findViewById(R.id.spinnerFenotipadoAOIScore);
        spinnerEnmalezamiento = findViewById(R.id.spinnerFenotipadoGradoEnmalezamientoDatos);
        spinnerPresionInsectos = findViewById(R.id.spinnerFenotipadoPresionInsectos);
        spinnerGradoEstres = findViewById(R.id.spinnerFenotipadoGradoEstres);

        etOperarios = findViewById(R.id.etFenotipadoOperarios);
        etParcelasFenotipadas = findViewById(R.id.etFenotipadoTomaDeDatosParcelasFenotipadas);
        etObservacionTomaDatos = findViewById(R.id.etFenotipadoObservacionTomaDatos);

        String [] estadio = new String[]{"VE","V1","V2","V3","V4","V5","VT","R1","R2","R3","R4","R5","R5.2","R5.5","R6"};
        ArrayAdapter<String> adapterestadio = new ArrayAdapter<>(this,R.layout.spinner_item,estadio);
        spinnerEstadio.setAdapter(adapterestadio);

        ArrayList<String> trait = new ControladorTrait(this).getTraits();
        ArrayAdapter<String> adapterTrait = new ArrayAdapter<>(this, R.layout.spinner_item, trait);
        spinnerTraits.setAdapter(adapterTrait);

        String [] AOIScore = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        ArrayAdapter<String> adapterAOIScore = new ArrayAdapter<>(this,R.layout.spinner_item,AOIScore);
        spinnerAOIScore.setAdapter(adapterAOIScore);

        String [] enmalezamiento = new String[]{"NULO","BAJO","MEDIO", "ALTO"};
        ArrayAdapter<String> adapterEnmalezamiento = new ArrayAdapter<>(this,R.layout.spinner_item,enmalezamiento);
        spinnerEnmalezamiento.setAdapter(adapterEnmalezamiento);

        String [] presionInsectos = new String[]{"NULO","BAJO","MEDIO", "ALTO"};
        ArrayAdapter<String> adaptepresionInsectos = new ArrayAdapter<>(this,R.layout.spinner_item,presionInsectos);
        spinnerPresionInsectos.setAdapter(adaptepresionInsectos);

        String [] estres = new String[]{"Alto potencial","Medio-alto potencial","Medio potencial","Estrés vegetativo leve","Estrés vegetativo moderado","Estrés vegetativo severo","Estrés prefloracion leve","Estrés prefloracion moderado","Estrés prefloracion leve","Estrés floracion leve","Estrés floracion moderado","Estrés floracion severo","Estres llenado leve","Estres llenado moderado","Estres llenado severo"};
        ArrayAdapter<String> adapterestres = new ArrayAdapter<>(this,R.layout.spinner_item,estres);
        spinnerGradoEstres.setAdapter(adapterestres);

        //BOTON AGREGAR TRAITS
        final LinearLayout contenedorTrait = (LinearLayout) findViewById(R.id.trait);
        Button botonAgregar = findViewById(R.id.btnTrait);
        botonAgregar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (counter != 0){

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View child = inflater.inflate(R.layout.traits, null);
                    contenedorTrait.addView(child);
                    spinnerTraitAgregado = child.findViewById(R.id.spinnerTomaDatosTraitAgregado);
                    spinnerTraitAgregado.setAdapter(adapterTrait);
                    counter--;

                }else{

                    botonAgregar.setEnabled(false);
                    Toast.makeText(FenotipadoTomaDeDatosActivity.this,"Límite de traits alcanzado.",Toast.LENGTH_LONG).show();

                }
            }
        });

        //BOTON FOTO DESDE CAMARA
        final LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        final LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        final Button botonFoto=findViewById(R.id.botonFotoTomaDatos);
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
        final Button botonElegirFoto=findViewById(R.id.botonElegirFotoTomaDatos);
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

        Button enviarFenotipadoDatos = (Button) findViewById(R.id.botonEnviarFenotipadoDatos);
        enviarFenotipadoDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String txtEstadioFenologico = spinnerEstadio.getSelectedItem().toString();
                    String txtTrait = spinnerTraits.getSelectedItem().toString();

                    String txtTraitsChild = "";
                    Spinner traitsText = null;

                    for(int z=0; z<contenedorTrait.getChildCount();z++) {
                        if (contenedorTrait.getChildAt(z) instanceof RelativeLayout) {
                            RelativeLayout relative = (RelativeLayout) contenedorTrait.getChildAt(z);
                            for (int i = 0; i < relative.getChildCount(); i++) {
                                if (relative.getChildAt(i) instanceof Spinner) {
                                    traitsText = (Spinner) relative.getChildAt(i);
                                    txtTraitsChild += traitsText.getSelectedItem().toString() + "/*/";
                                }
                            }
                        }
                    }
                    //quito el ultimo caracter /*/
                    if(txtTraitsChild.endsWith("/*/"))  {
                        txtTraitsChild = txtTraitsChild.substring(0, txtTraitsChild.length() - 3);
                    }

                    String txtOperarios = etOperarios.getText().toString();
                    String txtParcelasFenotipadas = etParcelasFenotipadas.getText().toString();
                    String txtAOIScore = spinnerAOIScore.getSelectedItem().toString();
                    String txtGradoEnmalezamiento = spinnerEnmalezamiento.getSelectedItem().toString();
                    String txtPresionInsectos = spinnerPresionInsectos.getSelectedItem().toString();
                    String txtGradoEstres = spinnerGradoEstres.getSelectedItem().toString();
                    String txtObservacion = etObservacionTomaDatos.getText().toString();

                    boolean cadenaError = false;

                    if(txtOperarios.trim().length() == 0){
                        cadenaError = true;
                        etOperarios.setError("Por favor, complete este campo.");
                    }
                    if(txtParcelasFenotipadas.trim().length() == 0){
                        cadenaError = true;
                        etParcelasFenotipadas.setError("Por favor, complete este campo.");
                    }
                    if(txtObservacion.trim().length() == 0){
                        cadenaError = true;
                        etObservacionTomaDatos.setError("Por favor, complete este campo.");
                    }

                    //Si todos los IFs estan OK.
                    if (cadenaError == false){

                        boolean resultado = new ControladorTomaDatos(FenotipadoTomaDeDatosActivity.this).InsertOrUpdate(txtEstadioFenologico,
                                txtTrait, txtTraitsChild, txtOperarios, txtParcelasFenotipadas, txtAOIScore, txtGradoEnmalezamiento, txtPresionInsectos,
                                txtGradoEstres, txtObservacion, filePath, txtFecha, txtCampana, txtEstacion, txtCultivo, txtLocalidad, txtUsuario);

                        if (resultado){

                            Toast.makeText(FenotipadoTomaDeDatosActivity.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                            new Syncronizer(FenotipadoTomaDeDatosActivity.this,"setTomaDeDatos").execute();
                            Intent intent = new Intent(FenotipadoTomaDeDatosActivity.this, MainActivity.class);
                            startActivity(intent);

                        }

                    }else {

                        Toast.makeText(FenotipadoTomaDeDatosActivity.this,"Complete todos los campos. ",Toast.LENGTH_LONG).show();

                    }

                }catch (Exception e){

                    Toast.makeText(FenotipadoTomaDeDatosActivity.this,"Error al enviar los datos. " + e.getMessage(),Toast.LENGTH_LONG).show();

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
                    Toast.makeText(FenotipadoTomaDeDatosActivity.this,"No hay imágen anterior.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FenotipadoTomaDeDatosActivity.this,"No hay imágen posterior.", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(FenotipadoTomaDeDatosActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FenotipadoTomaDeDatosActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        // super.onBackPressed();
    }
}
