package com.example.viverocom.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.viverocom.Adapters.HomeAdapter;
import com.example.viverocom.Adapters.ImagenesEdicionProductoAdapter;
import com.example.viverocom.Adapters.ProductoDetalleAdapter;
import com.example.viverocom.Clases.Orden;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.MainActivity;
import com.example.viverocom.R;
import com.example.viverocom.RegistrarUsuario;
import com.example.viverocom.databinding.FragmentNavDetalleconsultaBinding;
import com.example.viverocom.databinding.FragmentNavProductosBinding;
import com.example.viverocom.databinding.FragmentNavProductosedicionBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class nav_productosedicion extends Fragment implements MenuProvider, ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved, ImagenesEdicionProductoAdapter.ItemClickListener {

    View view;
    Query query;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    ValueEventListener valueEventListener;

    DatabaseReference databaseReferenceOrdenes;
    DatabaseReference databaseReferenceOrdenesDetalle;
    ValueEventListener valueEventListenerOrdenes;
    ValueEventListener valueEventListenerOrdenesDetalle;
    ArrayList<OrdenDetalle> listaOrdenesDetalle = new ArrayList<>();
    ArrayList<String> listaidOrdenes = new ArrayList<>();

    private Uri imageURL;
    StorageReference storageReference;

    RecyclerView recyclerView;
    ArrayList<Uri> listaImagenes = new ArrayList<>();
    ArrayList<Uri> listaImagenesRecibida = new ArrayList<>();
    ArrayList<String> listaNombres = new ArrayList<>();
    HashMap<Uri, String> mapNombresUri = new HashMap<>();
    Button btnCamara;
    Button btnGaleria;
    TextView cantidadImagenes;
    TextView noImagenes;
    ImagenesEdicionProductoAdapter adapter;
    private static final int Read_Permission = 101;
    private static final int PICK_IMAGE = 1;
    boolean masde5imagenes = false;

    boolean nombreValido = true;
    boolean tipoValido = true;
    boolean costeValido = true;
    boolean descripcionValido = true;
    boolean cuidadoPlantaValido = true;
    boolean temperaturaminValida = true;
    boolean temperaturamaxValida = true;
    boolean cantidadImagenesValida = true;

    int vecesnombre = 0;
    int vecescoste = 0;
    int vecesdescripcion = 0;
    int vecescuidadoplanta = 0;
    int vecestempmin = 0;
    int vecestempmax = 0;

    ArrayList<Producto> productoid = new ArrayList<>();
    String id;
    String tipo = "";
    String fechayhora;
    boolean plantaSelected;
    Bundle bundle;

    TextView stock;
    ArrayList<String> listaString = new ArrayList<>();

    //CAMERAX
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    //CAMERAX

    private FragmentNavProductosedicionBinding binding;

    ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    ImageButton imgCapture;
    ImageButton imageClose;
    PreviewView previewView;
    LinearLayout linearLayout5;
    ImageCapture imageCapture;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNavProductosedicionBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        //setUpOnBackPressed();

        //CAMERA
        imgCapture = view.findViewById(R.id.imageCapture);
        imageClose = view.findViewById(R.id.imageClose);
        previewView = view.findViewById(R.id.idPreviewView);
        linearLayout5 = view.findViewById(R.id.linearLayout5);
        //CAMERA

        listenersParaValidacionDeEdicionyEliminacion();

        btnCamara = view.findViewById(R.id.btntomarFoto);
        btnGaleria = view.findViewById(R.id.btn_seleccionarFotos);
        recyclerView = view.findViewById(R.id.recyclerViewFotos);
        cantidadImagenes = view.findViewById(R.id.cantidadImagenes);
        noImagenes = view.findViewById(R.id.noImagenes);
        TextView titulo = view.findViewById(R.id.textView10);

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), PICK_IMAGE);
            }
        });

        productoid = new ArrayList<>();
        listaidOrdenes = new ArrayList<>();
        listaOrdenesDetalle = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        stock = view.findViewById(R.id.stock_editarProducto);

        TextInputLayout nombre = view.findViewById(R.id.inputNombre_edicionProducto);
        nombre.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (nombre.isErrorEnabled()) {
                    if ((nombre.getEditText().getText().toString() != null && !nombre.getEditText().getText().toString().equals(""))) {
                        nombre.setErrorEnabled(false);
                        nombreValido = true;
                    } else {
                        nombre.setError("Ingrese el nombre del producto");
                        nombreValido = false;
                    }
                }

                if (!nombre.isErrorEnabled() && vecesnombre != 0) {
                    if ((nombre.getEditText().getText().toString() != null && !nombre.getEditText().getText().toString().equals(""))) {
                        nombre.setErrorEnabled(false);
                        nombreValido = true;
                    } else {
                        nombre.setError("Ingrese el nombre del producto");
                        nombreValido = false;
                    }
                }
            }
        });

        TextInputLayout coste = view.findViewById(R.id.inputCoste_edicionProducto);
        coste.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean costeEsFloat = true;
                boolean costeEsMayorACero = true;
                boolean costeMaxDosDecimales = true;
                try {
                    double costefloat = Double.parseDouble(coste.getEditText().getText().toString());
                    costeEsMayorACero = costefloat > 0;
                    costeMaxDosDecimales = !(BigDecimal.valueOf(costefloat).scale() > 2);
                } catch (NumberFormatException e) {
                    costeEsFloat = false;
                    costeEsMayorACero = false;
                    costeMaxDosDecimales = false;
                    e.printStackTrace();
                }

                if (coste.isErrorEnabled()) {
                    if ((coste.getEditText().getText().toString() != null && !coste.getEditText().getText().toString().equals(""))) {
                        if (!costeEsFloat) {
                            coste.setError("Un numero");
                            costeValido = false;
                        } else if (!costeEsMayorACero) {
                            coste.setError("Mayor a 0");
                            costeValido = false;
                        } else if (!costeMaxDosDecimales) {
                            coste.setError("Dos decimales");
                            costeValido = false;
                        } else {
                            coste.setErrorEnabled(false);
                            costeValido = true;
                        }
                    } else {
                        coste.setError("Campo obligatorio");
                        costeValido = false;
                    }
                }

                if (!coste.isErrorEnabled() && vecescoste != 0) {
                    if ((coste.getEditText().getText().toString() != null && !coste.getEditText().getText().toString().equals(""))) {
                        if (!costeEsFloat) {
                            coste.setError("Un numero");
                            costeValido = false;
                        } else if (!costeEsMayorACero) {
                            coste.setError("Mayor a 0");
                            costeValido = false;
                        } else if (!costeMaxDosDecimales) {
                            coste.setError("Dos decimales");
                            costeValido = false;
                        } else {
                            coste.setErrorEnabled(false);
                            costeValido = true;
                        }
                    } else {
                        coste.setError("Campo obligatorio");
                        costeValido = false;
                    }
                }
            }
        });

        TextInputLayout descripcion = view.findViewById(R.id.inputDescripcion_edicionProducto);
        descripcion.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (descripcion.isErrorEnabled()) {
                    if ((descripcion.getEditText().getText().toString() != null && !descripcion.getEditText().getText().toString().equals(""))) {
                        descripcion.setErrorEnabled(false);
                        descripcionValido = true;
                    } else {
                        descripcion.setError("Ingrese la descripción del producto");
                        descripcionValido = false;
                    }
                }

                if (!descripcion.isErrorEnabled() && vecesdescripcion != 0) {
                    if ((descripcion.getEditText().getText().toString() != null && !descripcion.getEditText().getText().toString().equals(""))) {
                        descripcion.setErrorEnabled(false);
                        descripcionValido = true;
                    } else {
                        descripcion.setError("Ingrese la descripción del producto");
                        descripcionValido = false;
                    }
                }
            }
        });

        TextInputLayout cuidadoPlanta = view.findViewById(R.id.inputCuidadosPlanta_edicionProducto);
        cuidadoPlanta.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (cuidadoPlanta.isErrorEnabled()) {
                    if ((cuidadoPlanta.getEditText().getText().toString() != null && !cuidadoPlanta.getEditText().getText().toString().equals(""))) {
                        cuidadoPlanta.setErrorEnabled(false);
                        cuidadoPlantaValido = true;
                    } else {
                        cuidadoPlanta.setError("Ingrese los cuidados que deben tenerse con la planta");
                        cuidadoPlantaValido = false;
                    }
                }

                if (!cuidadoPlanta.isErrorEnabled() && vecescuidadoplanta != 0) {
                    if ((cuidadoPlanta.getEditText().getText().toString() != null && !cuidadoPlanta.getEditText().getText().toString().equals(""))) {
                        cuidadoPlanta.setErrorEnabled(false);
                        cuidadoPlantaValido = true;
                    } else {
                        cuidadoPlanta.setError("Ingrese los cuidados que deben tenerse con la planta");
                        cuidadoPlantaValido = false;
                    }
                }
            }
        });

        TextInputLayout tempmin = view.findViewById(R.id.inputTempmin_edicionProducto);
        tempmin.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean tempminEsFloat = true;
                double tempminfloat = 0;
                try {
                    tempminfloat = Double.parseDouble(coste.getEditText().getText().toString());
                } catch (NumberFormatException e) {
                    tempminEsFloat = false;
                    e.printStackTrace();
                }

                if (tempmin.isErrorEnabled()) {
                    if ((tempmin.getEditText().getText().toString() != null && !tempmin.getEditText().getText().toString().equals(""))) {
                        if (!tempminEsFloat) {
                            tempmin.setError("La temperatura debe ser un decimal");
                            temperaturaminValida = false;
                        } else if (tempminfloat < -6) {
                            tempmin.setError("La temperatura mín es de -6°C");
                            temperaturaminValida = false;
                        } else {
                            tempmin.setErrorEnabled(false);
                            temperaturaminValida = true;
                        }
                    } else {
                        tempmin.setError("Ingrese la temp. mín que soporta la planta");
                        temperaturaminValida = false;
                    }
                }

                if (!tempmin.isErrorEnabled() && vecestempmin != 0) {
                    if ((tempmin.getEditText().getText().toString() != null && !tempmin.getEditText().getText().toString().equals(""))) {
                        if (!tempminEsFloat) {
                            tempmin.setError("La temperatura debe ser un decimal");
                            temperaturaminValida = false;
                        } else if (tempminfloat < -6) {
                            tempmin.setError("La temperatura mín es de -6°C");
                            temperaturaminValida = false;
                        } else {
                            tempmin.setErrorEnabled(false);
                            temperaturaminValida = true;
                        }
                    } else {
                        tempmin.setError("Ingrese la temp. mín que soporta la planta");
                        temperaturaminValida = false;
                    }
                }
            }
        });

        TextInputLayout tempmax = view.findViewById(R.id.inputTempmax_edicionProducto);
        tempmax.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean tempmaxEsFloat = true;
                double tempmaxfloat = 0;
                try {
                    tempmaxfloat = Double.parseDouble(tempmax.getEditText().getText().toString());
                } catch (NumberFormatException e) {
                    tempmaxEsFloat = false;
                    e.printStackTrace();
                }

                if (tempmax.isErrorEnabled()) {
                    if ((tempmax.getEditText().getText().toString() != null && !tempmax.getEditText().getText().toString().equals(""))) {
                        if (!tempmaxEsFloat) {
                            tempmax.setError("La temperatura debe ser un decimal");
                            temperaturamaxValida = false;
                        } else if (tempmaxfloat > 32) {
                            tempmax.setError("La temperatura máx es de 32°C");
                            temperaturamaxValida = false;
                        } else {
                            tempmax.setErrorEnabled(false);
                            temperaturamaxValida = true;
                        }
                    } else {
                        tempmax.setError("Ingrese la temp. máx que soporta la planta");
                        temperaturamaxValida = false;
                    }
                }

                if (!tempmax.isErrorEnabled() && vecestempmax != 0) {
                    if ((tempmax.getEditText().getText().toString() != null && !tempmax.getEditText().getText().toString().equals(""))) {
                        if (!tempmaxEsFloat) {
                            tempmax.setError("La temperatura debe ser un decimal");
                            temperaturamaxValida = false;
                        } else if (tempmaxfloat > 32) {
                            tempmax.setError("La temperatura máx es de 32°C");
                            temperaturamaxValida = false;
                        } else {
                            tempmax.setErrorEnabled(false);
                            temperaturamaxValida = true;
                        }
                    } else {
                        tempmax.setError("Ingrese la temp. máx que soporta la planta");
                        temperaturamaxValida = false;
                    }
                }
            }
        });

        TextInputLayout spinnera = view.findViewById(R.id.spinner_tipo_productos);
        AutoCompleteTextView spinner = view.findViewById(R.id.idTipo);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                spinnera.setErrorEnabled(false);

                tipo = spinner.getText().toString();
                if (tipo.equals("Planta de interior") || tipo.equals("Planta de exterior")) {
                    cuidadoPlanta.setVisibility(View.VISIBLE);
                    tempmin.setVisibility(View.VISIBLE);
                    tempmax.setVisibility(View.VISIBLE);
                    plantaSelected = true;
                } else {
                    cuidadoPlanta.getEditText().getText().clear();
                    tempmin.getEditText().getText().clear();
                    tempmax.getEditText().getText().clear();
                    cuidadoPlanta.setVisibility(View.GONE);
                    tempmin.setVisibility(View.GONE);
                    tempmax.setVisibility(View.GONE);
                    plantaSelected = false;
                }
            }
        });

        ImageButton add = view.findViewById(R.id.btn_aumentar_stock);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockInt = Integer.parseInt(stock.getText().toString());
                stock.setText(String.valueOf(stockInt + 1));
            }
        });

        ImageButton minus = view.findViewById(R.id.btn_disminuir_stock);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockInt = Integer.parseInt(stock.getText().toString());
                if (stockInt != 1) {
                    stock.setText(String.valueOf(stockInt - 1));
                }
            }
        });


        TextView textImagenesDelProducto = view.findViewById(R.id.textView16);
        LinearLayout linearLayoutCoste = view.findViewById(R.id.linearLayout2);

        //CAMARAX
        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePhoto();
            }
        });

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewView.setVisibility(View.GONE);
                linearLayout5.setVisibility(View.GONE);
//                                Navigation.findNavController(getView()).navigate(R.id.nav_productosedicion);
            }
        });

        boolean permissionsGranted = allPermissionsGranted();
        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allPermissionsGranted()) {
                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                } else {
                    //ABRO CAMARA
                    Log.d("prueba", "TAMANIO LISTA IMAGENES" + listaImagenes.size());
                    if (listaImagenes.size() < 5) {

                        previewView.setVisibility(View.VISIBLE);
                        linearLayout5.setVisibility(View.VISIBLE);
                        //imgCapture.setVisibility(View.VISIBLE);
                        //imageClose.setVisibility(View.VISIBLE);

                        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(getContext());
                        cameraProviderListenableFuture.addListener(() -> {
                            try {
                                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                                startCameraX(cameraProvider);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }, getExecutor());
//                        Bundle bundleA = new Bundle();
//                        if (id != null && !id.equals("")) {
//                            bundleA.putString("id", id);
//                            bundleA.putStringArrayList("listaNombres", listaNombres);
//                        }
//                        bundleA.putString("nombre", nombre.getEditText().getText().toString());
//                        bundleA.putString("tipo", spinnera.getEditText().getText().toString());
//                        bundleA.putString("coste", coste.getEditText().getText().toString());
//                        bundleA.putString("stock", stock.getText().toString());
//                        bundleA.putString("descripcion", descripcion.getEditText().getText().toString());
//                        bundleA.putString("cuidadoPlanta", cuidadoPlanta.getEditText().getText().toString());
//                        bundleA.putString("tempmin", tempmin.getEditText().getText().toString());
//                        bundleA.putString("tempmax", tempmax.getEditText().getText().toString());
//                        listaString = new ArrayList<>();
//                        Log.d("prueba", "LISTA QUE SE ENVIA CANTIDAD: " + listaImagenes.size());
//                        for (Uri uri : listaImagenes) {
//                            listaString.add(String.valueOf(uri));
//                        }
//                        Log.d("prueba", "LISTA QUE SE ENVIA CANTIDAD: " + listaString.size());
//                        bundleA.putStringArrayList("listaImagenes", listaString);
//
////                        NavOptions navOptions = new NavOptions.Builder()
////                                .setPopUpTo(R.id.nav_camara, true)
////                                .build();
//                        Log.d("prueba", "ENTRO EN BTNCAMERA");
//                        Navigation.findNavController(view).navigate(R.id.nav_camara, bundleA);
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Puede seleccionarse un máximo de 5 imágenes", Snackbar.LENGTH_LONG).show();
                    }

                }
            }
        });
        //CAMARAX

        bundle = this.getArguments();
        if (bundle != null) { //NULO SI SE DA AL BOTON DE NUEVO PRODUCTO

            id = bundle.getString("id");
            if (id != null && !id.equals("")) { //SI SE RECIBE ID, EL PRODUCTO SE ESTA EDITANDO
                titulo.setText("Editar producto");
                query = FirebaseDatabase.getInstance().getReference("productos").orderByChild("id").equalTo(id);
                valueEventListener = query.addValueEventListener(new nav_productosedicion.listener());

//                String nombreBundle = bundle.getString("nombre");
//                if (nombreBundle != null && !nombreBundle.equals("")) {
//                    nombre.getEditText().setText(nombreBundle);
//                }
//                String[] some_array = getResources().getStringArray(R.array.tipos);
//                String tipoBundle = bundle.getString("tipo");
//                if (tipoBundle != null && !tipoBundle.equals("")) {
//
//                    if (tipoBundle.equals("Planta de interior")) {
//                        spinner.setText(some_array[0], false);
//                        tipo = "Planta de interior";
//                        plantaSelected = true;
//                        cuidadoPlanta.setVisibility(View.VISIBLE);
//                        tempmin.setVisibility(View.VISIBLE);
//                        tempmax.setVisibility(View.VISIBLE);
//                    } else if (tipoBundle.equals("Planta de exterior")) {
//                        spinner.setText(some_array[1], false);
//                        tipo = "Planta de exterior";
//                        plantaSelected = true;
//                        cuidadoPlanta.setVisibility(View.VISIBLE);
//                        tempmin.setVisibility(View.VISIBLE);
//                        tempmax.setVisibility(View.VISIBLE);
//                    } else if (tipoBundle.equals("Cuidado de la planta")) {
//                        spinner.setText(some_array[2], false);
//                        tipo = "Cuidado de la planta";
//                    } else if (tipoBundle.equals("Herramientas")) {
//                        spinner.setText(some_array[3], false);
//                        tipo = "Herramientas";
//                    } else if (tipoBundle.equals("Adorno para jardín")) {
//                        spinner.setText(some_array[4], false);
//                        tipo = "Adorno para jardín";
//                    }
//                }
//                String costeBundle = bundle.getString("coste");
//                if (costeBundle != null && !costeBundle.equals("")) {
//                    coste.getEditText().setText(costeBundle);
//                }
//                String stockBundle = bundle.getString("stock");
//                if (stockBundle != null && !stockBundle.equals("")) {
//                    stock.setText(stockBundle);
//                }
//                String descripcionBundle = bundle.getString("descripcion");
//                if (descripcionBundle != null && !descripcionBundle.equals("")) {
//                    descripcion.getEditText().setText(descripcionBundle);
//                }
//                String cuidadoPlantaBundle = bundle.getString("cuidadoPlanta");
//                if (cuidadoPlantaBundle != null && !cuidadoPlantaBundle.equals("")) {
//                    cuidadoPlanta.getEditText().setText(cuidadoPlantaBundle);
//                }
//                String tempminBundle = bundle.getString("tempmin");
//                if (tempminBundle != null && !tempminBundle.equals("")) {
//                    tempmin.getEditText().setText(tempminBundle);
//                }
//                String tempmaxBundle = bundle.getString("tempmax");
//                if (tempmaxBundle != null && !tempmaxBundle.equals("")) {
//                    tempmax.getEditText().setText(tempmaxBundle);
//                }

                //IMAGENES
                listaString = bundle.getStringArrayList("listaImagenes");
                Log.d("prueba", "LISTA QUE SE RECIBE CANTIDAD: " + listaString.size());
                listaNombres = bundle.getStringArrayList("listaNombres");
                if (listaString != null) {
                    int i = 0;
                    for (String s : listaString) {
                        listaImagenesRecibida.add(Uri.parse(s));
                        listaImagenes.add(Uri.parse(s));
                        mapNombresUri.put(Uri.parse(s), listaNombres.get(i));
                        i++;
                    }

//                    String uriFoto = bundle.getString("uri");
//                    Log.d("prueba", "Uri de la foto tomada: " + uriFoto);
//                    if (uriFoto != null && !uriFoto.equals("")) {
//                        listaImagenes.add(Uri.parse(uriFoto));
//                        //listaString.add(uriFoto);
//                    }

                    if (listaImagenes.size() == 1) {
                        cantidadImagenes.setText(listaImagenes.size() + " imagen seleccionada");
                    } else {
                        cantidadImagenes.setText(listaImagenes.size() + " imágenes seleccionadas");
                    }
                } else {
                    noImagenes.setVisibility(View.VISIBLE);
                }
                //IMAGENES
            } else {
                noImagenes.setVisibility(View.VISIBLE);
                titulo.setText("Nuevo producto");

//                String nombreBundle = bundle.getString("nombre");
//                if (nombreBundle != null && !nombreBundle.equals("")) {
//                    nombre.getEditText().setText(nombreBundle);
//                }
//                String[] some_array = getResources().getStringArray(R.array.tipos);
//                String tipoBundle = bundle.getString("tipo");
//                if (tipoBundle != null && !tipoBundle.equals("")) {
//
//                    if (tipoBundle.equals("Planta de interior")) {
//                        spinner.setText(some_array[0], false);
//                        tipo = "Planta de interior";
//                        plantaSelected = true;
//                        cuidadoPlanta.setVisibility(View.VISIBLE);
//                        tempmin.setVisibility(View.VISIBLE);
//                        tempmax.setVisibility(View.VISIBLE);
//                    } else if (tipoBundle.equals("Planta de exterior")) {
//                        spinner.setText(some_array[1], false);
//                        tipo = "Planta de exterior";
//                        plantaSelected = true;
//                        cuidadoPlanta.setVisibility(View.VISIBLE);
//                        tempmin.setVisibility(View.VISIBLE);
//                        tempmax.setVisibility(View.VISIBLE);
//                    } else if (tipoBundle.equals("Cuidado de la planta")) {
//                        spinner.setText(some_array[2], false);
//                        tipo = "Cuidado de la planta";
//                    } else if (tipoBundle.equals("Herramientas")) {
//                        spinner.setText(some_array[3], false);
//                        tipo = "Herramientas";
//                    } else if (tipoBundle.equals("Adorno para jardín")) {
//                        spinner.setText(some_array[4], false);
//                        tipo = "Adorno para jardín";
//                    }
//                }
//                String costeBundle = bundle.getString("coste");
//                if (costeBundle != null && !costeBundle.equals("")) {
//                    coste.getEditText().setText(costeBundle);
//                }
//                String stockBundle = bundle.getString("stock");
//                if (stockBundle != null && !stockBundle.equals("")) {
//                    stock.setText(stockBundle);
//                }
//                String descripcionBundle = bundle.getString("descripcion");
//                if (descripcionBundle != null && !descripcionBundle.equals("")) {
//                    descripcion.getEditText().setText(descripcionBundle);
//                }
//                String cuidadoPlantaBundle = bundle.getString("cuidadoPlanta");
//                if (cuidadoPlantaBundle != null && !cuidadoPlantaBundle.equals("")) {
//                    cuidadoPlanta.getEditText().setText(cuidadoPlantaBundle);
//                }
//                String tempminBundle = bundle.getString("tempmin");
//                if (tempminBundle != null && !tempminBundle.equals("")) {
//                    tempmin.getEditText().setText(tempminBundle);
//                }
//                String tempmaxBundle = bundle.getString("tempmax");
//                if (tempmaxBundle != null && !tempmaxBundle.equals("")) {
//                    tempmax.getEditText().setText(tempmaxBundle);
//                }
//
//                listaString = bundle.getStringArrayList("listaImagenes");
//                if (listaString != null) {
//                    Log.d("prueba", "CANTIDAD LISTA STRING RECIBIDA: " + listaString.size());
//                    int i = 0;
//                    for (String s : listaString) {
//                        listaImagenes.add(Uri.parse(s));
//                    }
//
//                    String uriFoto = bundle.getString("uri");
//                    if (uriFoto != null && !uriFoto.equals("")) {
//                        listaImagenes.add(Uri.parse(uriFoto));
//                        //listaString.add(uriFoto);
//                    }
//
//                    if (listaImagenes.size() == 1) {
//                        cantidadImagenes.setText(listaImagenes.size() + " imagen seleccionada");
//                    } else {
//                        cantidadImagenes.setText(listaImagenes.size() + " imágenes seleccionadas");
//                    }
//                } else {
//                    noImagenes.setVisibility(View.VISIBLE);
//                }
            }

        } else {
            noImagenes.setVisibility(View.VISIBLE);
            titulo.setText("Nuevo producto");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagenesEdicionProductoAdapter(listaImagenes, getContext(), this, this);
        recyclerView.setAdapter(adapter);

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
                Snackbar.make(view.findViewById(R.id.fragment_nav_productosedicion), "Puede seleccionarse un máximo de 5 imágenes", Snackbar.LENGTH_LONG).show();
            }
            noImagenes.setVisibility(View.GONE);
            if (listaImagenes.size() == 1) {
                cantidadImagenes.setText(listaImagenes.size() + " imagen seleccionada");
            } else {
                cantidadImagenes.setText(listaImagenes.size() + " imágenes seleccionadas");
            }
            adapter.notifyDataSetChanged();
        } else {
            if (listaImagenes.isEmpty()) {
                noImagenes.setVisibility(View.VISIBLE);
                cantidadImagenes.setText("");
            }
        }

    }

    public void validarRegistro() {
        TextInputLayout nombre = view.findViewById(R.id.inputNombre_edicionProducto);
        TextInputLayout coste = view.findViewById(R.id.inputCoste_edicionProducto);
        TextInputLayout descripcion = view.findViewById(R.id.inputDescripcion_edicionProducto);
        TextInputLayout cuidadoPlanta = view.findViewById(R.id.inputCuidadosPlanta_edicionProducto);
        TextInputLayout tempmin = view.findViewById(R.id.inputTempmin_edicionProducto);
        TextInputLayout tempmax = view.findViewById(R.id.inputTempmax_edicionProducto);
        TextView stock = view.findViewById(R.id.stock_editarProducto);

        //NOMBRE
        if (nombre.getEditText().getText().toString() == null || nombre.getEditText().getText().toString().equals("")) {
            vecesnombre++;
            nombre.setError("Ingrese el nombre del producto");
            nombreValido = false;
        }
        //NOMBRE

        //TIPO
        tipoValido = (tipo != null && !tipo.equals("")) ? true : false;
        if (!tipoValido) {
            TextInputLayout spinner = view.findViewById(R.id.spinner_tipo_productos);
            spinner.setError("Seleccione un tipo de producto");
        }
        //TIPO

        //COSTE
        boolean costeEsFloat = true;
        boolean costeEsMayorACero = true;
        boolean costeMaxDosDecimales = true;
        try {
            double costefloat = Double.parseDouble(coste.getEditText().getText().toString());
            costeEsMayorACero = costefloat > 0;
            costeMaxDosDecimales = !(BigDecimal.valueOf(costefloat).scale() > 2);
        } catch (NumberFormatException e) {
            costeEsFloat = false;
            costeEsMayorACero = false;
            costeMaxDosDecimales = false;
            e.printStackTrace();
        }
        if ((coste.getEditText().getText().toString() != null && !coste.getEditText().getText().toString().equals(""))) {
            if (!costeEsFloat) {
                vecescoste++;
                coste.setError("Un número");
                costeValido = false;
            } else if (!costeEsMayorACero) {
                vecescoste++;
                coste.setError("Mayor a 0");
                costeValido = false;
            } else if (!costeMaxDosDecimales) {
                vecescoste++;
                coste.setError("Dos decimales");
                costeValido = false;
            }
        } else {
            vecescoste++;
            coste.setError("Campo obligatorio");
            costeValido = false;
        }
        //COSTE

        //DESCRIPCION
        if (descripcion.getEditText().getText().toString() == null || descripcion.getEditText().getText().toString().equals("")) {
            vecesdescripcion++;
            descripcion.setError("Ingrese la descripción del producto");
            descripcionValido = false;
        }
        //DESCRIPCION

        Log.d("click", "PLANTASELECTED: " + plantaSelected);
        if (plantaSelected) {
            //CUIDADOPLANTA
            if (cuidadoPlanta.getEditText().getText().toString() == null || cuidadoPlanta.getEditText().getText().toString().equals("")) {
                vecescuidadoplanta++;
                cuidadoPlanta.setError("Ingrese los cuidados que deben tenerse con la planta");
                cuidadoPlantaValido = false;
            } else {
                cuidadoPlanta.setErrorEnabled(false);
                cuidadoPlantaValido = true;
            }
            //CUIDADOPLANTA

            //TEMPMIN
            double tempminfloat = 0;
            boolean tempminEsFloat = true;
            try {
                tempminfloat = Double.parseDouble(tempmin.getEditText().getText().toString());
            } catch (NumberFormatException e) {
                tempminEsFloat = false;
                e.printStackTrace();
            }
            if ((tempmin.getEditText().getText().toString() != null && !tempmin.getEditText().getText().toString().equals(""))) {
                if (!tempminEsFloat) {
                    vecestempmin++;
                    tempmin.setError("La temperatura debe ser un decimal");
                    temperaturaminValida = false;
                } else if (tempminfloat < -6) {
                    vecestempmin++;
                    tempmin.setError("La temperatura mín es de -6°C");
                    temperaturaminValida = false;
                } else {
                    tempmin.setErrorEnabled(false);
                    temperaturaminValida = true;
                }
            } else {
                vecestempmin++;
                tempmin.setError("Ingrese la temp. mín que soporta la planta");
                temperaturaminValida = false;
            }
            //TEMPMIN

            //TEMPMAX
            boolean tempmaxEsFloat = true;
            double tempmaxfloat = 0;
            try {
                tempmaxfloat = Double.parseDouble(tempmax.getEditText().getText().toString());
            } catch (NumberFormatException e) {
                tempmaxEsFloat = false;
                e.printStackTrace();
            }

            if ((tempmax.getEditText().getText().toString() != null && !tempmax.getEditText().getText().toString().equals(""))) {
                if (!tempmaxEsFloat) {
                    vecestempmax++;
                    tempmax.setError("La temperatura debe ser un decimal");
                    temperaturamaxValida = false;
                } else if (tempmaxfloat > 32) {
                    vecestempmax++;
                    tempmax.setError("La temperatura máx es de 32°C");
                    temperaturamaxValida = false;
                } else {
                    tempmax.setErrorEnabled(false);
                    temperaturamaxValida = true;
                }
            } else {
                vecestempmax++;
                tempmax.setError("Ingrese la temp. máx que soporta la planta");
                temperaturamaxValida = false;
            }
            //TEMPMAX

            Log.d("prueba", "tempmaxEsFloat: " + tempmaxEsFloat);
            Log.d("prueba", "tempminEsFloat: " + tempminEsFloat);
            if (tempmaxEsFloat && tempminEsFloat) {
                Log.d("prueba", "tempmaxfloat: " + tempmaxfloat);
                Log.d("prueba", "tempminfloat: " + tempminfloat);
                Log.d("prueba", "!(tempmaxfloat > tempminfloat): " + !(tempmaxfloat > tempminfloat));
                if (!(tempmaxfloat > tempminfloat)) {
                    tempmax.setError("La temp. máx debe ser mayor que la mín");
                    tempmin.setError("La temp. máx debe ser mayor que la mín");
                    temperaturamaxValida = false;
                    temperaturaminValida = false;
                } else {
                    tempmax.setErrorEnabled(false);
                    tempmin.setErrorEnabled(false);
                }
            }

        } else {
            cuidadoPlantaValido = true;
            temperaturaminValida = true;
            temperaturamaxValida = true;
        }

        //CANTIDAD IMAGENES
        if (listaImagenes.size() > 0) {
            cantidadImagenesValida = true;
        } else {
            Snackbar.make(view.findViewById(R.id.fragment_nav_productosedicion), "El producto debe tener una imagen como mínimo", Snackbar.LENGTH_LONG).show();
            cantidadImagenesValida = false;
        }
        //CANTIDAD IMAGENES

        if (nombreValido && costeValido && tipoValido && descripcionValido && cuidadoPlantaValido && temperaturaminValida && temperaturamaxValida && cantidadImagenesValida) {
            Log.d("task", "VALIDO");

            //Guardar producto en db
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("productos");
            Producto producto = new Producto();

            String idgenerado = "producto" + generarCodigo(20);
            if (id != null) {
                producto.setId(id);
            } else {
                producto.setId(idgenerado);
            }
            producto.setNombre(nombre.getEditText().getText().toString());
            producto.setTipo(tipo);

            producto.setCosto(Math.round(Double.valueOf(coste.getEditText().getText().toString()) * 100.0) / 100.0);
            producto.setStock(Integer.parseInt(stock.getText().toString()));
            producto.setDescripcion(descripcion.getEditText().getText().toString());
//            if (bundle != null) {
//                producto.setCuidadosPlanta(cuidadoPlanta.getEditText().getText().toString());
//                producto.setTempmin(Math.round(Double.valueOf(tempmin.getEditText().getText().toString()) * 100.0) / 100.0);
//                producto.setTempmax(Math.round(Double.valueOf(tempmax.getEditText().getText().toString()) * 100.0) / 100.0);
//            } else {
                if (plantaSelected) {
                    producto.setCuidadosPlanta(cuidadoPlanta.getEditText().getText().toString());
                    producto.setTempmin(Math.round(Double.valueOf(tempmin.getEditText().getText().toString()) * 100.0) / 100.0);
                    producto.setTempmax(Math.round(Double.valueOf(tempmax.getEditText().getText().toString()) * 100.0) / 100.0);
                } else {
                    producto.setCuidadosPlanta("");
                    producto.setTempmin(0);
                    producto.setTempmax(0);
                //}
            }
            producto.setEstado(true);

            if (id != null) {
                //Date currentTime = Calendar.getInstance().getTime();
                //SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                producto.setFechaCreacion(fechayhora);
            } else {
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Log.d("tamanio", "FECHA CREACION: " + sf.format(currentTime));
                producto.setFechaCreacion(sf.format(currentTime));
            }


            if (id == null) { //CREAR

                Log.d("subidaImg", producto.getId());
                Log.d("registro", "DISPOSITIVO GUARDADO");
                databaseReference.child(idgenerado).setValue(producto)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("registro", "DISPOSITIVO GUARDADO");

                                Log.d("subidaImg", producto.getId());
                                for (Uri uri : listaImagenes) {
                                    uploadToFirebase(producto.getId(), uri, UUID.randomUUID().toString());
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString("exito", "Se ha creado el producto exitosamente");
                                Navigation.findNavController(view).navigate(R.id.nav_productos, bundle);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("registro", "DISPOSITIVO NO GUARDADO - " + e.getMessage());
                            }
                        });

            } else { //EDITAR

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage("¿Seguro que desea editar este producto?");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (listaOrdenesDetalle.isEmpty()) {
                                    databaseReference.child(id).setValue(producto)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("registro", "DISPOSITIVO GUARDADO");

                                                    Log.d("subidaImg", producto.getId());
                                                    Log.d("prueba", "LISTA IMAGENES SIZE: " + listaImagenes.size());
                                                    Log.d("prueba", "LISTA IMAGENES RECIBIDAS SIZE: " + listaImagenesRecibida.size());
                                                    Log.d("prueba", "MAP URIS Y NOMBRES: " + mapNombresUri.size());
                                                    for (Uri uri : listaImagenes) {
                                                        if (listaImagenesRecibida.contains(uri)) {
                                                            DatabaseReference ref = firebaseDatabase.getReference().child("productos").child(producto.getId()).child("imagenes");
                                                            ref.child(mapNombresUri.get(uri)).setValue(String.valueOf(uri))
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
                                                            mapNombresUri.remove(uri);
                                                        } else {
                                                            uploadToFirebase(producto.getId(), uri, UUID.randomUUID().toString());
                                                        }
                                                    }
                                                    for (Map.Entry<Uri, String> entry : mapNombresUri.entrySet()) {
                                                        storageReference = FirebaseStorage.getInstance().getReference().child("img/productos/" + producto.getId() + "/" + entry.getValue());
                                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                                    }

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("exito", "Se ha editado el producto exitosamente");
                                                    Navigation.findNavController(view).navigate(R.id.nav_productos, bundle);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("registro", "DISPOSITIVO NO GUARDADO - " + e.getMessage());
                                                }
                                            });
                                } else {
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                                    builder.setMessage("Este producto no puede ser editado debido a que ha sido reservado por un cliente");
                                    builder.setPositiveButton("Aceptar",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    builder.show();
                                }
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
    }

    public String generarCodigo(int longitud) {
        String AlphaNumericStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder s = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int ch = (int) (AlphaNumericStr.length() * Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();
    }

    @Override
    public void clicked(int getSize) {
        if (listaImagenes.size() != 0) {
            if (listaImagenes.size() == 1) {
                cantidadImagenes.setText(listaImagenes.size() + " imagen seleccionada");
            } else {
                cantidadImagenes.setText(listaImagenes.size() + " imágenes seleccionadas");
            }
        } else {
            noImagenes.setVisibility(View.VISIBLE);
            cantidadImagenes.setText("");
        }
    }

    @Override
    public void itemClick(int position) {
        Log.d("click", "IMAGEN SELECCIONADA");
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ImageView imagenDialog = dialog.findViewById(R.id.imageView2);
        Glide.with(getContext())
                .load(listaImagenes.get(position))
                .into(imagenDialog);
        dialog.show();
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                productoid.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Producto producto = ds.getValue(Producto.class);
                    productoid.add(producto);
                }

                TextInputLayout nombre = view.findViewById(R.id.inputNombre_edicionProducto);
                nombre.getEditText().setText(productoid.get(0).getNombre());
                TextInputLayout coste = view.findViewById(R.id.inputCoste_edicionProducto);
                coste.getEditText().setText(String.valueOf(productoid.get(0).getCosto()));
                TextInputLayout descripcion = view.findViewById(R.id.inputDescripcion_edicionProducto);
                descripcion.getEditText().setText(productoid.get(0).getDescripcion());
                TextInputLayout cuidadoPlanta = view.findViewById(R.id.inputCuidadosPlanta_edicionProducto);
                cuidadoPlanta.getEditText().setText(productoid.get(0).getCuidadosPlanta());
                TextInputLayout tempmin = view.findViewById(R.id.inputTempmin_edicionProducto);
                tempmin.getEditText().setText(String.valueOf(productoid.get(0).getTempmin()));
                TextInputLayout tempmax = view.findViewById(R.id.inputTempmax_edicionProducto);
                tempmax.getEditText().setText(String.valueOf(productoid.get(0).getTempmax()));
                stock = view.findViewById(R.id.stock_editarProducto);
                stock.setText(String.valueOf(productoid.get(0).getStock()));

                fechayhora = productoid.get(0).getFechaCreacion();

                String[] some_array = getResources().getStringArray(R.array.tipos);
                TextInputLayout spinnera = view.findViewById(R.id.spinner_tipo_productos);
                AutoCompleteTextView spinner = view.findViewById(R.id.idTipo);
                spinnera.setEnabled(false);
                spinner.setEnabled(false);

                if (productoid.get(0).getTipo().equals("Planta de interior")) {
                    spinner.setText(some_array[0], false);
                    tipo = "Planta de interior";
                    plantaSelected = true;
                    cuidadoPlanta.setVisibility(View.VISIBLE);
                    tempmin.setVisibility(View.VISIBLE);
                    tempmax.setVisibility(View.VISIBLE);
                } else if (productoid.get(0).getTipo().equals("Planta de exterior")) {
                    spinner.setText(some_array[1], false);
                    tipo = "Planta de exterior";
                    plantaSelected = true;
                    cuidadoPlanta.setVisibility(View.VISIBLE);
                    tempmin.setVisibility(View.VISIBLE);
                    tempmax.setVisibility(View.VISIBLE);
                } else if (productoid.get(0).getTipo().equals("Cuidado de la planta")) {
                    spinner.setText(some_array[2], false);
                    tipo = "Cuidado de la planta";
                } else if (productoid.get(0).getTipo().equals("Herramientas")) {
                    spinner.setText(some_array[3], false);
                    tipo = "Herramientas";
                } else if (productoid.get(0).getTipo().equals("Adorno para jardín")) {
                    spinner.setText(some_array[4], false);
                    tipo = "Adorno para jardín";
                }

            } else {
                productoid.clear();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_productosedicion, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
//            case android.R.id.home:
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
//                builder.setMessage("¿Seguro que desea regresar a la pantalla anterior? Perderá los datos ingresados");
//                builder.setPositiveButton("Aceptar",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                NavOptions navOptions = new NavOptions.Builder()
//                                        .setPopUpTo(R.id.nav_productosedicion, true)
//                                        .build();
//                                if (id != null && !id.equals("")) {
//                                    Bundle bundleid = new Bundle();
//                                    bundleid.putString("id", id);
//                                    Navigation.findNavController(view).navigate(R.id.action_nav_productosedicion_to_nav_productosdetalle, bundleid, navOptions);
//                                } else {
//                                    Navigation.findNavController(view).navigate(String.valueOf(R.id.action_nav_productosedicion_to_nav_productos), navOptions);
//                                }}
//                        });
//                builder.setNegativeButton("Cancelar",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                builder.show();
//                return true;
            case R.id.validarEdicion:
                Log.d("menu", "EDITAR/CREAR PRODUCTO");
                validarRegistro();
                return true;
        }
        return false;
    }

//    public void setUpOnBackPressed() {
//        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
//                builder.setMessage("¿Seguro que desea regresar a la pantalla anterior? Perderá los datos ingresados");
//                builder.setPositiveButton("Aceptar",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                NavOptions navOptions = new NavOptions.Builder()
//                                        .setPopUpTo(R.id.nav_productosedicion, true)
//                                        .build();
//                                if (id != null && !id.equals("")) {
//                                    Bundle bundleid = new Bundle();
//                                    bundleid.putString("id", id);
//                                    Navigation.findNavController(view).navigate(R.id.action_nav_productosedicion_to_nav_productosdetalle, bundleid, navOptions);
//                                } else {
//                                    Navigation.findNavController(view).navigate(String.valueOf(R.id.action_nav_productosedicion_to_nav_productos), navOptions);
//                                }
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
        Log.d("destroy", "FRAGMENT DESTRUIDO");
        super.onDestroyView();
        if (query != null) {
            query.removeEventListener(valueEventListener);
        }
        databaseReferenceOrdenes.removeEventListener(valueEventListenerOrdenes);
        databaseReferenceOrdenesDetalle.removeEventListener(valueEventListenerOrdenesDetalle);
        binding = null;
    }

    private void uploadToFirebase(String idProducto, Uri uriImagen, String nombreArchivo) {

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

        storageReference = FirebaseStorage.getInstance().getReference().child("img/productos/" + idProducto + "/" + nombreArchivo);
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
                                    DatabaseReference ref = firebaseDatabase.getReference().child("productos").child(idProducto).child("imagenes");
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

    public void listenersParaValidacionDeEdicionyEliminacion() {
        databaseReferenceOrdenes = FirebaseDatabase.getInstance().getReference().child("ordenes");

        valueEventListenerOrdenes = databaseReferenceOrdenes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaidOrdenes.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Orden orden = ds.getValue(Orden.class);
                        if (orden.getEstado().equals("PENDIENTE")) {
                            listaidOrdenes.add(orden.getId());
                        }
                    }
                } else {
                    listaidOrdenes.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("msg", "Error onCancelled", error.toException());
            }
        });

        databaseReferenceOrdenesDetalle = FirebaseDatabase.getInstance().getReference().child("ordenesdetalle");

        valueEventListenerOrdenesDetalle = databaseReferenceOrdenesDetalle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaOrdenesDetalle.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrdenDetalle ordenDetalle = ds.getValue(OrdenDetalle.class);
                        for (String s : listaidOrdenes) {
                            if (s.equals(ordenDetalle.getIdOrden()) && ordenDetalle.getIdProducto().equals(id)) {
                                listaOrdenesDetalle.add(ordenDetalle);
                            }
                        }
                        listaOrdenesDetalle.add(ordenDetalle);
                    }

                } else {
                    listaOrdenesDetalle.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("msg", "Error onCancelled", error.toException());
            }
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //CAMERA
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(getContext());
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    public void capturePhoto() {
        long timestamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "FOTO TOMADA", Snackbar.LENGTH_LONG).show();
                        listaImagenes.add(outputFileResults.getSavedUri());
                        adapter.notifyDataSetChanged();

                        previewView.setVisibility(View.GONE);
                        linearLayout5.setVisibility(View.GONE);

                        if (listaImagenes.isEmpty()) {
                            noImagenes.setVisibility(View.GONE);
                        } else {
                            if (listaImagenes.size() == 1) {
                                cantidadImagenes.setText(listaImagenes.size() + " imagen seleccionada");
                            } else {
                                cantidadImagenes.setText(listaImagenes.size() + " imágenes seleccionadas");
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "ERROR AL GUARDAR LA FOTO | " + exception.getMessage(), Snackbar.LENGTH_LONG).show();
                        previewView.setVisibility(View.GONE);
                        linearLayout5.setVisibility(View.GONE);
                    }
                }
        );
    }
    //CAMERA

}