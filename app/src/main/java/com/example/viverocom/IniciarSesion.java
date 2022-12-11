package com.example.viverocom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.DataCoordenada;
import com.example.viverocom.Clases.DataProvincia;
import com.example.viverocom.Clases.DatosUbicacion;
import com.example.viverocom.Clases.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class IniciarSesion extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicias_sesion);
        getSupportActionBar().setTitle("Iniciar sesión");

        //prueba - SE VA A DETALLES PRODUCTO SI ESTE ES UNA PLANTA Y SE HAN PEDIDO LOS PERMISOS PARA OBTENER LA LONGITUD Y LATITUD DEL DISPOSITIVO
//        String url = "https://api.openweathermap.org/data/2.5/weather?lat=-12.067400830496553&lon=-77.1008937964395&appid=d8558817cb542ceb08ae698fa735b439&units=metric&lang=es";
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.GET,
//                url,
//                response -> {
//                    Gson gson = new Gson();
//                    DatosUbicacion datosUbicacion = gson.fromJson(response, DatosUbicacion.class);
//                    System.out.println("Temperatura: " + datosUbicacion.getMain().get("temp") + "°C");
//                    System.out.println("Humedad: " + datosUbicacion.getMain().get("humidity") + "%");
//                    System.out.println("Clima principal: " + datosUbicacion.getWeather().get(0).getMain() + ", " + datosUbicacion.getWeather().get(0).getDescription());
//                    imageView = findViewById(R.id.imageView3);
//                    Glide.with(IniciarSesion.this).load("https://openweathermap.org/img/wn/"+datosUbicacion.getWeather().get(0).getIcon()+"@2x.png").into(imageView);
//                },
//                error -> Log.e("data", error.getMessage()));
//        queue.add(stringRequest);
        //prueba

        //prueba - PONER EN OTRO THREAD AL PRINCIPAL PARA OBTENER LOS DISTRITOS QUE SE DIBUJARAN
//        String url = "https://funciones-polygon.azurewebsites.net/api/polygon?id=1";
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.GET,
//                url,
//                response -> {
//                    Gson gson = new Gson();
//                    DataCoordenada dataCoordenada = gson.fromJson(response, DataCoordenada.class);
//                    System.out.println("Cantidad de coordenadas: " + dataCoordenada.getData().size());
//                    },
//                error -> Log.e("data", error.getMessage()));
//        queue.add(stringRequest);
        //prueba

        firebaseAuth = firebaseAuth.getInstance();

        String mensaje_exito = getIntent().getStringExtra("exito");
        if (mensaje_exito != null && !mensaje_exito.equals("")) {
            Snackbar.make(findViewById(R.id.activity_iniciar_sesion), mensaje_exito, Snackbar.LENGTH_LONG).show();
        }
    }

    public void cambiarContrasenia(View view){
        Intent intent = new Intent(this, CambioContrasenia.class);
        startActivity(intent);
    }

    public void validarInicioSesion(View view) {
        TextInputLayout correo = findViewById(R.id.inputCorreo_iniSesion);
        TextInputLayout password = findViewById(R.id.inputPassword_iniSesion);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
        boolean correoValido = true;
        if (correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (!correo.getEditText().getText().toString().matches(emailPattern)) {
                //Texto ingresado NO cumple con el patron de un correo electronico
                correo.setError("Ingrese un correo válido");
                correoValido = false;
            } else {
                //Validar si usuario existe en el sistema
                correo.setErrorEnabled(false);
            }
        } else {
            //Texto NO ha sido ingresado en el edittext
            correo.setError("Ingrese un correo");
            correoValido = false;
        }

        boolean passwordValido = true;
        if (password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            password.setErrorEnabled(false);
        } else {
            password.setError("Ingrese una contraseña");
            passwordValido = false;
        }

        if (correoValido && passwordValido) {

            firebaseAuth.signInWithEmailAndPassword(correo.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("task", "EXITO EN REGISTRO");

                        firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                    FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                                            Usuario usuario = ds.getValue(Usuario.class);
                                                            if (usuario.getRol().equals("Admin")) {
                                                                Intent intent = new Intent(IniciarSesion.this, NavigationDrawerAdmin.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Intent intent = new Intent(IniciarSesion.this, NavigationDrawer.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                } else {
                                    Snackbar.make(findViewById(R.id.activity_iniciar_sesion), "Su cuenta no ha sido verificada. Verifíquela para poder ingresar", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Log.d("task", "ERROR EN REGISTRO - " + task.getException().getMessage());
                        //Ver bien mensaje de error
                        Snackbar.make(findViewById(R.id.activity_iniciar_sesion), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

}
