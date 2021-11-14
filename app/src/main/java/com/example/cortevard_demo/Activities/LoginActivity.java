package com.example.cortevard_demo.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cortevard_demo.Controladores.ControladorUsuarios;
import com.example.cortevard_demo.R;
import com.example.cortevard_demo.SaveSharedPreference;
import com.example.cortevard_demo.Syncronizer;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private String nombreUser;
    EditText email, pass;
    Button login;
    private CheckBox chkRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PackageManager manager = this.getPackageManager();
        TextView version = findViewById(R.id.version);
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            if(info != null){
                version.setText("Versión instalada: " + Integer.toString(info.versionCode));
            }else {
                version.setText("Versión instalada: 0");
            }
        } catch (PackageManager.NameNotFoundException e) {
            version.setText("Versión instalada: 0");
        }

        //Primer sincronizacion automatica
        //IMPORTANTE Se hace aca para que funcione el versionado
        new Syncronizer(LoginActivity.this, "Main").execute();

        chkRememberMe = (CheckBox) findViewById(R.id.checkRecordar);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.contra);
        login = findViewById(R.id.loginBtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = email.getText().toString();
                String pwd = pass.getText().toString();
                if(userName.isEmpty() && pwd.isEmpty()){
                    email.setError("Por favor, ingrese credenciales.");
                }else {
                    attemptLogin(userName, pwd);
                }
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        // La actividad se ha vuelto visible (ahora se "reanuda").
        // Check if UserResponse is Already Logged In
        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void attemptLogin(String user, String pwd) {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        email.setError(null);
        pass.setError(null);

        mAuthTask = new UserLoginTask(user, pwd, this);
        mAuthTask.execute((Void) null);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;

        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            mContext= context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                nombreUser = new ControladorUsuarios(mContext).validarUsuario(mEmail, mPassword);
                if(nombreUser!=null){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                if (chkRememberMe.isChecked())
                {
                    SaveSharedPreference.setLoggedIn(getApplicationContext(), true, mEmail, nombreUser);
                } else {
                    SaveSharedPreference.setLoggedIn(getApplicationContext(), false, mEmail, nombreUser);
                }
                finish();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                LoginActivity.this.startActivity(intent);
                Toast.makeText(LoginActivity.this, "Bienvenido!", Toast.LENGTH_LONG).show();
            } else {
                pass.setError("E-Mail o contraseña incorrecta, vuelva a intentarlo.");
                pass.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}