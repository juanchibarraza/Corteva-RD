package com.example.cortevard_demo.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cortevard_demo.Controladores.ControladorCarreton;
import com.example.cortevard_demo.Controladores.ControladorCosecha;
import com.example.cortevard_demo.Controladores.ControladorCosechadora;
import com.example.cortevard_demo.Controladores.ControladorUsuarios;
import com.example.cortevard_demo.FilePath;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.SaveSharedPreference;
import com.example.cortevard_demo.Syncronizer;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CosechaActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 13;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 14;
    ImageSwitcher switcherImage;
    ImageView imageView, viewImage;
    private ArrayList<Uri> imageUris;
    int position = 0;
    String filePath;
    Uri imageUri;
    Spinner spinnerCosechadora, spinnerOperario, spinnerCarreton;
    EditText etPerdidaCosecha, etMSTBordura, etObservaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosecha);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cosecha");

        Bundle bundle = this.getIntent().getExtras();
        final String txtFecha = (String) bundle.get("fecha");
        final String txtCampana = (String) bundle.get("campana");
        final String txtEstacion = (String) bundle.get("estacion");
        final String txtCultivo = (String) bundle.get("cultivo");
        final String txtLocalidad = (String) bundle.get("localidad");
        final String txtUsuario = SaveSharedPreference.getLoggedEmail(getApplicationContext());

        spinnerCosechadora = findViewById(R.id.spinnerCosechaCosechadora);
        spinnerCarreton = findViewById(R.id.spinnerCosechaCarreton);
        spinnerOperario = findViewById(R.id.spinnerCosechaOperario);
        etPerdidaCosecha = findViewById(R.id.etCosechaPerdidaCosecha);
        etMSTBordura = findViewById(R.id.etCosechaBordura);
        etObservaciones = findViewById(R.id.etCosechaObservaciones);

        //imageView = findViewById(R.id.fotoCosecha);

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

        ArrayList<String> cosechadora = new ControladorCosechadora(this).getCosechadora();
        ArrayAdapter<String> adapterCosechadora = new ArrayAdapter<>(this, R.layout.spinner_item, cosechadora);
        spinnerCosechadora.setAdapter(adapterCosechadora);

        ArrayList<String> carreton = new ControladorCarreton(this).getCarreton();
        ArrayAdapter<String> adapterCarreton = new ArrayAdapter<>(this, R.layout.spinner_item, carreton);
        spinnerCarreton.setAdapter(adapterCarreton);

        ArrayList<String> operarios = new ControladorUsuarios(this).getUsuarios();
        ArrayAdapter<String> adapterOperarios = new ArrayAdapter<>(this, R.layout.spinner_item, operarios);
        spinnerOperario.setAdapter(adapterOperarios);

        //BOTON FOTO DESDE CAMARA
        final LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        final LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        final Button botonFoto=findViewById(R.id.botonFotoCosecha);
        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageViewContainer.getVisibility() == v.GONE && imageSwitcherContainer.getVisibility() == v.GONE){
                    inflateCamaraShoot();
                }else if(imageSwitcherContainer.getVisibility() == v.VISIBLE){
                    imageSwitcherContainer.removeAllViews();
                    inflateCamaraShoot();
                }
            }
        });

        //BOTON ELEGIR IMAGEN DESDE GALERIA
        final Button botonElegirFoto=findViewById(R.id.botonElegirFotoCosecha);
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

        Button enviarCosecha = (Button)findViewById(R.id.enviarCosecha);
        enviarCosecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String txtCosechadora = spinnerCosechadora.getSelectedItem().toString();
                    String txtCarreton = spinnerCarreton.getSelectedItem().toString();
                    String txtOperario = spinnerOperario.getSelectedItem().toString();
                    String txtPerdidaCosecha = etPerdidaCosecha.getText().toString();
                    String txtMSTBordura = etMSTBordura.getText().toString();
                    float txtPorcentajeAvance = slider.getValue();
                    String txtObservaciones = etObservaciones.getText().toString();

                    boolean cadenaError = false;

                    if(txtPerdidaCosecha.trim().length() == 0){
                        cadenaError = true;
                        etPerdidaCosecha.setError("Por favor, complete este campo.");
                    }
                    if(txtMSTBordura.trim().length() == 0){
                        cadenaError = true;
                        etMSTBordura.setError("Por favor, complete este campo.");
                    }
                    if(txtObservaciones.trim().length() == 0){
                        cadenaError = true;
                        etObservaciones.setError("Por favor, complete este campo.");
                    }

                    //Si todos los IFs estan OK.
                    if (cadenaError == false){

                        boolean resultado = new ControladorCosecha(CosechaActivity.this).InsertOrUpdate(txtCosechadora,txtOperario, txtCarreton,
                                txtPerdidaCosecha,txtMSTBordura, txtPorcentajeAvance, txtObservaciones, filePath,
                                txtFecha, txtCampana, txtEstacion, txtCultivo, txtLocalidad, txtUsuario);

                        if (resultado){

                            Toast.makeText(CosechaActivity.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                            new Syncronizer(CosechaActivity.this,"setCosecha").execute();
                            Intent intent = new Intent(CosechaActivity.this, MainActivity.class);
                            startActivity(intent);

                        }

                    }else {

                        Toast.makeText(CosechaActivity.this,"Complete todos los campos. ",Toast.LENGTH_LONG).show();

                    }


                }catch (Exception e){

                    Toast.makeText(CosechaActivity.this,"Error al enviar los datos. " + e.getMessage(),Toast.LENGTH_LONG).show();

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

        Button botonBorrar = child.findViewById(R.id.eliminarFotoCosecha);

        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageTag = imageUri.getPath();
                File fileTag = new File(imageTag);

                if(fileTag.exists()){
                    fileTag.delete();
                    imageView.setImageBitmap(null);
                    filePath = "";
                    Toast.makeText(CosechaActivity.this, "Imagen borrada.", Toast.LENGTH_SHORT).show();
                }

            }
        });

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
                    Toast.makeText(CosechaActivity.this,"No hay imágen anterior.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CosechaActivity.this,"No hay imágen posterior.", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        Intent intent = new Intent(CosechaActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CosechaActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        //super.onBackPressed();
    }
}
