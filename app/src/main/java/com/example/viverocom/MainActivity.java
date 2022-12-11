package com.example.viverocom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.viverocom.Clases.DatosUbicacion;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.Clases.Usuario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

    }

    public boolean verificarEstadoInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void iniciarSesion(View view){
        if (verificarEstadoInternet()) {
            Intent intent = new Intent(this, IniciarSesion.class);
            startActivity(intent);
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setMessage("Verifique su conexión a internet para poder ingresar a la aplicación");
            builder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.show();
        }
    }

    public void registroUsuario(View view){
        if (verificarEstadoInternet()) {
            Intent intent = new Intent(this,RegistrarUsuario.class);
            startActivity(intent);
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setMessage("Verifique su conexión a internet para poder ingresar a la aplicación");
            builder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.show();
        }
    }

    public String generarCodigo(int longitud) {
        String AlphaNumericStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder s = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int ch = (int)(AlphaNumericStr.length()*Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()){

            FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Usuario usuario = ds.getValue(Usuario.class);
                                    if (usuario.getRol().equals("Admin")) {
                                        startActivity(new Intent(getApplicationContext(),NavigationDrawerAdmin.class));
                                    } else {
                                        startActivity(new Intent(getApplicationContext(),NavigationDrawer.class));
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setMessage("¿Seguro que desea salir de la aplicación?");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

}