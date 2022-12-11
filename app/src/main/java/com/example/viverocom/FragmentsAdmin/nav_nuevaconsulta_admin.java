package com.example.viverocom.FragmentsAdmin;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.viverocom.Adapters.ImagenesEdicionProductoAdapter;
import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavNuevaconsultaAdminBinding;
import com.example.viverocom.databinding.FragmentNavNuevaconsultaBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class nav_nuevaconsulta_admin extends Fragment implements MenuProvider, ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved, ImagenesEdicionProductoAdapter.ItemClickListener {

    boolean asuntoValido = true;
    boolean cuerpoValido = true;
    int vecesAsunto = 0;
    int vecesCuerpo = 0;
    View view;

    ArrayList<Consulta> listaConsultas;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    ArrayList<Uri> listaImagenes = new ArrayList<>();
    TextView fotosAdjuntas;
    ImagenesEdicionProductoAdapter adapter;
    private static final int Read_Permission = 101;
    private static final int PICK_IMAGE = 1;
    boolean masde5imagenes = false;
    private Uri imageURL;
    StorageReference storageReference;

    String id;
    String asuntoRespuesta;
    Bundle bundle;
    private FragmentNavNuevaconsultaAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.fragment_nav_nuevaconsulta, container, false);
        binding = FragmentNavNuevaconsultaAdminBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        bundle = this.getArguments();
        id = bundle.getString("id");
        asuntoRespuesta = bundle.getString("asunto");
        //PASARLE ID DE LA CONSULTA ANTERIOR
        //ACTUALIZAR IDRESPUESTA DE LA CONSULTA A LA QUE SE ESTA RESPONDIENDO


        //setUpOnBackPressed();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("consultas");

        recyclerView = view.findViewById(R.id.recyclerViewFotos);
        fotosAdjuntas = view.findViewById(R.id.fotosAdjuntas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagenesEdicionProductoAdapter(listaImagenes, getContext(), this, this);
        recyclerView.setAdapter(adapter);

        TextInputLayout asunto = view.findViewById(R.id.inputAsunto);
        asunto.getEditText().setText(asuntoRespuesta);
        asunto.setEnabled(false);
//        asunto.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (asunto.isErrorEnabled()) {
//                    if ((asunto.getEditText().getText().toString() != null && !asunto.getEditText().getText().toString().equals(""))) {
//                        asunto.setErrorEnabled(false);
//                        asuntoValido = true;
//                    } else {
//                        asuntoValido = false;
//                        asunto.setError("El asunto no puede estar vacío");
//                    }
//                }
//
//                if (!asunto.isErrorEnabled() && vecesAsunto != 0) {
//                    if ((asunto.getEditText().getText().toString() != null && !asunto.getEditText().getText().toString().equals(""))) {
//                        asunto.setErrorEnabled(false);
//                        asuntoValido = true;
//                    } else {
//                        asuntoValido = false;
//                        asunto.setError("El asunto no puede estar vacío");
//                    }
//                }
//            }
//        });

        TextInputLayout cuerpo = view.findViewById(R.id.inputCuerpoMensaje);
        cuerpo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cuerpo.isErrorEnabled()) {
                    if ((cuerpo.getEditText().getText().toString() != null && !cuerpo.getEditText().getText().toString().equals(""))) {
                        if (cuerpo.getEditText().getText().toString().length() > 500) {
                            cuerpo.setError("El cuerpo del mensaje debe ser menor a 500 caracteres");
                            cuerpoValido = false;
                        } else {
                            cuerpo.setErrorEnabled(false);
                            cuerpoValido = true;
                        }
                    } else {
                        cuerpo.setError("El cuerpo del mensaje no puede estar vacío");
                        cuerpoValido = false;
                    }
                }

                if (!cuerpo.isErrorEnabled() && vecesCuerpo != 0) {
                    if ((cuerpo.getEditText().getText().toString() != null && !cuerpo.getEditText().getText().toString().equals(""))) {
                        if (cuerpo.getEditText().getText().toString().length() > 500) {
                            cuerpo.setError("El cuerpo del mensaje debe ser menor a 500 caracteres");
                            cuerpoValido = false;
                        } else {
                            cuerpo.setErrorEnabled(false);
                            cuerpoValido = true;
                        }
                    } else {
                        cuerpo.setError("El cuerpo del mensaje no puede estar vacío");
                        cuerpoValido = false;
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                int countOfImages = data.getClipData().getItemCount();
                for (int i = 0; i < countOfImages; i++) {
                    if (listaImagenes.size() < 5) {
                        masde5imagenes = false;
                        imageURL = data.getClipData().getItemAt(i).getUri();
                        listaImagenes.add(imageURL);
                    } else {
                        masde5imagenes = true;
                    }
                }
            } else {
                if (listaImagenes.size() < 5) {
                    masde5imagenes = false;
                    imageURL = data.getData();
                    listaImagenes.add(imageURL);
                } else {
                    masde5imagenes = true;
                }
            }

            if (masde5imagenes) {
                Snackbar.make(view.findViewById(R.id.fragment_nav_nuevaconsulta_admin), "Puede seleccionarse un máximo de 5 imágenes", Snackbar.LENGTH_LONG).show();
            }
            fotosAdjuntas.setText("Fotos adjuntas (" + listaImagenes.size() + ")");

            adapter.notifyDataSetChanged();
        } else {
            //noImagenes.setVisibility(View.VISIBLE);
            //cantidadImagenes.setText("");
        }
    }

    public void validarConsulta() {
        TextInputLayout asunto = view.findViewById(R.id.inputAsunto);
        TextInputLayout cuerpo = view.findViewById(R.id.inputCuerpoMensaje);

        if (asunto.getEditText().getText().toString() == null || asunto.getEditText().getText().toString().equals("")) {
            vecesAsunto++;
            asuntoValido = false;
            asunto.setError("El asunto no puede estar vacío");
        }

        if (cuerpo.getEditText().getText().toString() != null && !cuerpo.getEditText().getText().toString().equals("")) {
            if (cuerpo.getEditText().getText().toString().length() > 500) {
                vecesCuerpo++;
                cuerpo.setError("El cuerpo del mensaje debe ser menor a 500 caracteres");
                cuerpoValido = false;
            }
        } else {
            vecesCuerpo++;
            cuerpo.setError("El cuerpo del mensaje no puede estar vacío");
            cuerpoValido = false;
        }

        if (asuntoValido && cuerpoValido) {
            Consulta consulta = new Consulta();

            String idgenerado = "consulta"+generarCodigo(20);
            consulta.setId(idgenerado);
            consulta.setIdUsuario(FirebaseAuth.getInstance().getCurrentUser().getUid());
            consulta.setAsunto(asunto.getEditText().getText().toString());
            consulta.setCuerpo(cuerpo.getEditText().getText().toString());
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            consulta.setFechahoraconsulta(sf.format(currentTime));
            consulta.setIdrespuesta("RESPUESTA DEL ADMINISTRADOR");

            databaseReference.child(idgenerado).setValue(consulta)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("registro", "CONSULTA REALIZADA");
                            Bundle bundle = new Bundle();
                            bundle.putString("exito", "Su consulta ha sido realizada exitosamente");

                            for (Uri uri : listaImagenes) {
                                uploadToFirebase(consulta.getId(), uri, UUID.randomUUID().toString());
                            }

                            FirebaseDatabase.getInstance().getReference().child("consultas").child(id)
                                    .child("idrespuesta").setValue(idgenerado)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("error", "Ocurrio un error - " + e.getMessage());
                                        }
                                    });

                            Navigation.findNavController(view).navigate(R.id.nav_consultas_admin,bundle);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("registro", "CONSULTA NO REALIZADA - " + e.getMessage());
                        }
                    });
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_nuevaconsulta,menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.adjuntarArchivos:
                Log.d("menu", "ADJUNTAR ARCHIVO");
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
                    return true; //?
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), PICK_IMAGE);
                return true;
            case R.id.enviarConsulta:
                Log.d("menu", "ENVIAR LA CONSULTA");
                validarConsulta();
                return true;
        }
        return false;
    }

//    public void setUpOnBackPressed() {
//        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
//                builder.setMessage("¿Seguro que desea regresar a la pantalla anterior? No se realizará su consulta");
//                builder.setPositiveButton("Aceptar",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Navigation.findNavController(view).navigate(R.id.nav_consultas);
//                            }
//                        });
//                builder.setNegativeButton("Cancelar",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                builder.show();
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void clicked(int getSize) {
        if (getSize == 0) {
            fotosAdjuntas.setText("Fotos adjuntas");
        } else {
            fotosAdjuntas.setText("Fotos adjuntas (" + getSize + ")");
        }
    }

    @Override
    public void itemClick(int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ImageView imagenDialog = dialog.findViewById(R.id.imageView2);
        imagenDialog.setImageURI(listaImagenes.get(position));
        dialog.show();
    }

    private void uploadToFirebase(String idConsulta, Uri uriImagen, String nombreArchivo) {

        byte[] bytes = new byte[0];
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriImagen);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        storageReference = FirebaseStorage.getInstance().getReference().child("img/consultas/" + idConsulta + "/" + nombreArchivo);
        storageReference.putBytes(bytes)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isComplete()) {
                            Log.d("subidaImagen", "EXITO");
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("subidaImagen", String.valueOf(uri));
                                    //SUBIDA URI A REALTIMEDATABASE
                                    DatabaseReference ref = firebaseDatabase.getReference().child("consultas").child(idConsulta).child("imagenes");
                                    ref.child(nombreArchivo).setValue(String.valueOf(uri))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("subidaImagen", "FALLIDO - " + e.getMessage());
                    }
                });
    }
}