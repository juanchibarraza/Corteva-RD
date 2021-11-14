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
import com.example.cortevard_demo.Controladores.ControladorCultivo;
import com.example.cortevard_demo.Controladores.ControladorMaleza;
import com.example.cortevard_demo.Controladores.ControladorSeleccionLote;
import com.example.cortevard_demo.FilePath;
import com.example.cortevard_demo.OfflineGPS;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.SaveSharedPreference;
import com.example.cortevard_demo.Syncronizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FenotipadoSeleccionDeLoteActivity extends AppCompatActivity{

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 15;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 16;
    ImageSwitcher switcherImage;
    ImageView imageView, viewImage;
    private ArrayList<Uri> imageUris;
    int position = 0;
    String filePath;
    Uri imageUri;
    Spinner spinnerCultivoAntecesor, spinnerVolumenRastrojo, spinnerCoberturaRastrojo, spinnerEnmalezamiento,
            spinnerMaleza, spinnerMalezaAgregado;
    EditText etCoordReferencia, etObservacionAccesoLote, etObservacionHistoriaLote;
    int counter = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fenotipado_seleccion_de_lote);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Selección de Lote");

        Bundle bundle = this.getIntent().getExtras();
        final String txtFecha = (String) bundle.get("fecha");
        final String txtCampana = (String) bundle.get("campana");
        final String txtEstacion = (String) bundle.get("estacion");
        final String txtCultivo = (String) bundle.get("cultivo");
        final String txtLocalidad = (String) bundle.get("localidad");
        final String txtUsuario = SaveSharedPreference.getLoggedEmail(getApplicationContext());

        spinnerCultivoAntecesor = findViewById(R.id.spinnerFenotipadoCultivo);
        spinnerVolumenRastrojo = findViewById(R.id.spinnerFenotipadoVolumenRastrojo);
        spinnerCoberturaRastrojo = findViewById(R.id.spinnerFenotipadoCoberturaRastrojo);
        spinnerEnmalezamiento = findViewById(R.id.spinnerFenotipadoGradoEnmalezamiento);
        spinnerMaleza = findViewById(R.id.spinnerSeleccionLoteMaleza);

        etCoordReferencia = findViewById(R.id.etCoordReferencia);
        etObservacionAccesoLote = findViewById(R.id.etSeleccionLoteAccesoLote);
        etObservacionHistoriaLote = findViewById(R.id.etSeleccionLoteHistoriaLote);

        //imageView = findViewById(R.id.fotoSeleccionLote);

        ArrayList<String> cultivo = new ControladorCultivo(this).getCultivos();
        ArrayAdapter<String> adapterCultivo = new ArrayAdapter<>(this, R.layout.spinner_item, cultivo);
        spinnerCultivoAntecesor.setAdapter(adapterCultivo);

        String [] volumenRastrojo = new String[]{"BAJO","MEDIO", "ALTO"};
        ArrayAdapter<String> adapterVolumenRastrojo = new ArrayAdapter<>(this,R.layout.spinner_item,volumenRastrojo);
        spinnerVolumenRastrojo.setAdapter(adapterVolumenRastrojo);

        String [] coberturaRastrojo = new String[]{"UNIFORME", "DESUNIFORME"};
        ArrayAdapter<String> adapterCoberturaRastrojo = new ArrayAdapter<>(this,R.layout.spinner_item,coberturaRastrojo);
        spinnerCoberturaRastrojo.setAdapter(adapterCoberturaRastrojo);

        String [] enmalezamiento = new String[]{"NULO","BAJO","MEDIO", "ALTO"};
        ArrayAdapter<String> adapterEnmalezamiento = new ArrayAdapter<>(this,R.layout.spinner_item,enmalezamiento);
        spinnerEnmalezamiento.setAdapter(adapterEnmalezamiento);

        ArrayList<String> maleza = new ControladorMaleza(this).getMalezas();
        ArrayAdapter<String> adapterMaleza = new ArrayAdapter<>(this, R.layout.spinner_item, maleza);
        spinnerMaleza.setAdapter(adapterMaleza);


        //BOTON AGREGAR MALEZAS
        final LinearLayout contenedorMaleza=(LinearLayout) findViewById(R.id.malezaFenotipado);
        Button botonAgregar = findViewById(R.id.btnMalezaFenotipado);
        botonAgregar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (counter != 0){

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View child = inflater.inflate(R.layout.maleza_fenotipado, null);
                    contenedorMaleza.addView(child);
                    spinnerMalezaAgregado = child.findViewById(R.id.spinnerMalezaFenotipadoAgregado);
                    spinnerMalezaAgregado.setAdapter(adapterMaleza);
                    counter--;

                }else{

                    botonAgregar.setEnabled(false);
                    Toast.makeText(FenotipadoSeleccionDeLoteActivity.this,"Límite de malezas alcanzado.",Toast.LENGTH_LONG).show();

                }
            }
        });

        //BOTON FOTO DESDE CAMARA
        final LinearLayout imageSwitcherContainer = (LinearLayout) findViewById(R.id.imageSwitcher);
        final LinearLayout imageViewContainer = (LinearLayout) findViewById(R.id.imageView);
        final Button botonFoto=findViewById(R.id.botonFotoSeleccionLote);
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
        final Button botonElegirFoto=findViewById(R.id.botonElegirFotoSeleccionLote);
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
        Button botonGPS = (Button) findViewById(R.id.btnGPSSeleccionLote);
        botonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(FenotipadoSeleccionDeLoteActivity.this, OfflineGPS.class);
                    startActivityForResult(intent,1);
                }catch (Exception e){
                    Toast.makeText(FenotipadoSeleccionDeLoteActivity.this,"Error al buscar coordenadas. " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        //Boton Guardar
        Button enviarLote = (Button)findViewById(R.id.botonEnviarFenotipadoLote);
        enviarLote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String txtCoordReferencia = etCoordReferencia.getText().toString();
                    String txtCultivoAntecesor = spinnerCultivoAntecesor.getSelectedItem().toString();
                    String txtVolumenRastrojo = spinnerVolumenRastrojo.getSelectedItem().toString();
                    String txtCoberturaRastrojo = spinnerCoberturaRastrojo.getSelectedItem().toString();
                    String txtGradoEnmalezamiento = spinnerEnmalezamiento.getSelectedItem().toString();
                    String txtMalezaPresente = spinnerMaleza.getSelectedItem().toString();
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
                    if(txtMalezaChild.endsWith("/*/")){
                        //quito el ultimo caracter /
                        txtMalezaChild = txtMalezaChild.substring(0, txtMalezaChild.length() - 3);
                    }

                    String txtObservacionAccesoLote = etObservacionAccesoLote.getText().toString();
                    String txtObservacionHistorialLote = etObservacionHistoriaLote.getText().toString();

                    boolean cadenaError = false;

                    if(txtCoordReferencia.trim().length() == 0){
                        cadenaError = true;
                        etCoordReferencia.setError("Por favor, complete este campo.");
                    }
                    if(txtObservacionAccesoLote.trim().length() == 0){
                        cadenaError = true;
                        etObservacionAccesoLote.setError("Por favor, complete este campo.");
                    }
                    if(txtObservacionHistorialLote.trim().length() == 0){
                        cadenaError = true;
                        etObservacionHistoriaLote.setError("Por favor, complete este campo.");
                    }

                    //Si todos los IFs estan OK.
                    if (cadenaError == false){

                        boolean resultado = new ControladorSeleccionLote(FenotipadoSeleccionDeLoteActivity.this).InsertOrUpdate(txtCoordReferencia,
                                txtCultivoAntecesor, txtVolumenRastrojo, txtCoberturaRastrojo, txtGradoEnmalezamiento, txtMalezaPresente, txtMalezaChild,
                                txtObservacionAccesoLote, txtObservacionHistorialLote, filePath,
                                txtFecha, txtCampana, txtEstacion, txtCultivo, txtLocalidad, txtUsuario);

                        if (resultado){
                            Toast.makeText(FenotipadoSeleccionDeLoteActivity.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                            new Syncronizer(FenotipadoSeleccionDeLoteActivity.this,"setSeleccionDeLote").execute();
                            Intent intent = new Intent(FenotipadoSeleccionDeLoteActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    }else {

                        Toast.makeText(FenotipadoSeleccionDeLoteActivity.this,"Complete todos los campos. ",Toast.LENGTH_LONG).show();

                    }

                }catch (Exception e){

                    Toast.makeText(FenotipadoSeleccionDeLoteActivity.this,"Error al enviar los datos. " + e.getMessage(),Toast.LENGTH_LONG).show();

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
                    Toast.makeText(FenotipadoSeleccionDeLoteActivity.this,"No hay imágen anterior.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FenotipadoSeleccionDeLoteActivity.this,"No hay imágen posterior.", Toast.LENGTH_SHORT).show();
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
        if(requestCode == 1) { /* GPS response */
            if (resultCode == Activity.RESULT_OK || resultCode == 1) {
                Bundle extras = data.getExtras();
                etCoordReferencia.setText(extras.getString("Latitude") + " " + extras.getString("Longitude"));
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(FenotipadoSeleccionDeLoteActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FenotipadoSeleccionDeLoteActivity.this, InicioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        // super.onBackPressed();
    }
}
