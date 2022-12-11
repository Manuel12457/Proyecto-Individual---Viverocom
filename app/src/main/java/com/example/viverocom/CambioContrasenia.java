package com.example.viverocom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CambioContrasenia extends AppCompatActivity {

    boolean correoValido = true;
    int vecesCorreo = 0;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia);
        getSupportActionBar().setTitle("Cambio de contraseña");

        TextInputLayout correo = findViewById(R.id.inputCorreo_cambioPsw);
        correo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (correo.isErrorEnabled()) {
                    if ((correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals(""))) {
                        correo.setErrorEnabled(false);
                        correoValido = true;
                    } else {
                        correo.setError("Ingrese su correo");
                        correoValido = false;
                    }
                }

                if (!correo.isErrorEnabled() && vecesCorreo != 0) {
                    if ((correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals(""))) {
                        correo.setErrorEnabled(false);
                        correoValido = true;
                    } else {
                        correo.setError("Ingrese su correo");
                        correoValido = false;
                    }
                }
            }
        });

        firebaseAuth = firebaseAuth.getInstance();
    }

    public void validarCorreo(View view) {
        TextInputLayout correo = findViewById(R.id.inputCorreo_cambioPsw);

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

        if (correoValido) {
            firebaseAuth.sendPasswordResetEmail(correo.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //Esconder edittext y cambiar texto
                    Log.d("forgetpsw", "Correo enviado para cambio de contrasenia");
                    Intent intent = new Intent(CambioContrasenia.this, IniciarSesion.class);
                    intent.putExtra("exito", "Se le ha enviado un correo para proceder con la solicitud de cambio de contraseña");
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("forgetpsw", "Ourrio un error - " + e.getMessage());
                }
            });
        }
    }

}