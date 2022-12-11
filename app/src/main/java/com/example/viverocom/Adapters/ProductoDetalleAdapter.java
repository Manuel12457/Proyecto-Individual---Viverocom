package com.example.viverocom.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.Coordenada;
import com.example.viverocom.Clases.DataCoordenada;
import com.example.viverocom.Clases.DataDepartamento;
import com.example.viverocom.Clases.DataProvincia;
import com.example.viverocom.Clases.DatosUbicacion;
import com.example.viverocom.Clases.Departamento;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.Clases.Provincia;
import com.example.viverocom.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductoDetalleAdapter extends RecyclerView.Adapter<ProductoDetalleAdapter.ProductoDetalleViewHolder> {

    GoogleMap mapa;
    private ArrayList<Producto> listaProductos;
    private Context context;
    private FragmentActivity fragmentActivity;
    private String latitud;
    private String lontigud;
    int vecesMapa = 1;

    ArrayList<Departamento> departamentos = new ArrayList<>();
    LinkedHashMap<Provincia, Integer> provinciasYPuntaje = new LinkedHashMap<>();

    public ProductoDetalleAdapter(ArrayList<Producto> listaProductos, Context context, FragmentActivity fragmentActivity, String latitud, String lontigud) {
        this.setListaProductos(listaProductos);
        this.setContext(context);
        this.setFragmentActivity(fragmentActivity);
        this.setLatitud(latitud);
        this.setLontigud(lontigud);
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productodetalle, parent, false);
        return new ProductoDetalleAdapter.ProductoDetalleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoDetalleViewHolder holder, int position) {

        departamentos.clear();
        provinciasYPuntaje.clear();

        Producto producto = getListaProductos().get(position);
        holder.nombre.setText(producto.getNombre());
        holder.tipo.setText(producto.getTipo());
        holder.coste.setText("S/" + producto.getCosto());
        holder.stock.setText("Stock: " + producto.getStock());
        holder.descripcion.setText(producto.getDescripcion());

        if ((producto.getCuidadosPlanta() != null && !producto.getCuidadosPlanta().equals(""))) {
            holder.textView20.setVisibility(View.VISIBLE);
            holder.cuidadoPlanta.setVisibility(View.VISIBLE);
            String cuidadosenvista = "";
            String cuidadosdelaplanta = producto.getCuidadosPlanta();
            List<String> listadoCuidados = Arrays.asList(cuidadosdelaplanta.split("\\. "));
            for (String s : listadoCuidados) {
                cuidadosenvista = cuidadosenvista + s + "\n";
            }
            cuidadosenvista = cuidadosenvista + "Temperatura óptima: " + producto.getTempmin() + "-" + producto.getTempmax() + "°C";
            holder.cuidadoPlanta.setText(cuidadosenvista);
        }

        //SLIDER
        if (producto.getImagenes() != null) {
            ArrayList<Uri> listaUri = new ArrayList<>();
            for (Map.Entry<String, String> entry : producto.getImagenes().entrySet()) {
                listaUri.add(Uri.parse(entry.getValue()));
            }
            Log.d("tamanio", "Size lista uris: " + listaUri.size());
            holder.viewPagerImageSllider.setAdapter(new SliderAdapter(listaUri, holder.viewPagerImageSllider));

            holder.viewPagerImageSllider.setClipToPadding(false);
            holder.viewPagerImageSllider.setClipChildren(false);
            holder.viewPagerImageSllider.setOffscreenPageLimit(3);
            holder.viewPagerImageSllider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                }
            });
            holder.viewPagerImageSllider.setPageTransformer(compositePageTransformer);
        }
        //SLIDER

        if (producto.getTipo().equals("Planta de interior") || producto.getTipo().equals("Planta de exterior")) {

            if (!getLatitud().equals("") && !getLontigud().equals("")) {

                holder.lugaresOptimoCrecimiento.setVisibility(View.VISIBLE);
                holder.map.setVisibility(View.VISIBLE);
                holder.noResultados.setVisibility(View.VISIBLE);

                Log.d("mapa", "VECES MAPA: " + vecesMapa);
                if (vecesMapa == 1) {
                    holder.map.onCreate(null);
                    holder.map.onResume();
                    holder.map.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng peru = new LatLng(-10.3431231425854, -75.71601953226326);
                            mapa = googleMap;
                            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(peru, 4.5f));
                            mapa.getUiSettings().setZoomGesturesEnabled(false);
                            mapa.getUiSettings().setScrollGesturesEnabled(false);
                            mapa.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);

                            iniciarListaDepartamentos();
                        }
                    });
                    vecesMapa++;
                }

                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + getLatitud() + "&lon=" + getLontigud() + "&appid=d8558817cb542ceb08ae698fa735b439&units=metric&lang=es";
                RequestQueue queue = Volley.newRequestQueue(getContext());
                StringRequest stringRequest = new StringRequest(
                        Request.Method.GET,
                        url,
                        response -> {
                            Gson gson = new Gson();
                            DatosUbicacion datosUbicacion = gson.fromJson(response, DatosUbicacion.class);
                            holder.datosUbicacion.setVisibility(View.VISIBLE);
                            holder.textViewClimaPrincipal.setVisibility(View.VISIBLE);
                            holder.textViewClimaPrincipal.setText("Clima principal: " + datosUbicacion.getWeather().get(0).getMain() + ", " + datosUbicacion.getWeather().get(0).getDescription());
                            holder.textViewTemperatura.setVisibility(View.VISIBLE);
                            holder.textViewTemperatura.setText("Temperatura: " + datosUbicacion.getMain().get("temp") + "°C");
                            holder.textViewHumedad.setVisibility(View.VISIBLE);
                            holder.textViewHumedad.setText("Humedad: " + datosUbicacion.getMain().get("humidity") + "%");
                            holder.icono.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load("https://openweathermap.org/img/wn/" + datosUbicacion.getWeather().get(0).getIcon() + "@2x.png").into(holder.icono);

                            LatLng ubicacion = new LatLng(Double.parseDouble(getLatitud()) , Double.parseDouble(getLontigud()));
                            mapa.addMarker(new MarkerOptions()
                                    .position(ubicacion)
                                    .title("Usted se ubica aquí"));
                        },
                        error -> Log.e("data", error.getMessage()));
                queue.add(stringRequest);
            }
        }

    }

    @Override
    public int getItemCount() {
        return getListaProductos().size();
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentActivity;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public String getLontigud() {
        return lontigud;
    }

    public void setLontigud(String lontigud) {
        this.lontigud = lontigud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public class ProductoDetalleViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private TextView tipo;
        private TextView coste;
        private TextView stock;
        private TextView descripcion;
        private TextView cuidadoPlanta;
        private TextView textView20;

        private ViewPager2 viewPagerImageSllider;

        private TextView textViewClimaPrincipal;
        private TextView textViewTemperatura;
        private TextView textViewHumedad;
        private ImageView icono;

        private TextView lugaresOptimoCrecimiento;
        private TextView datosUbicacion;
        private TextView noResultados;

        private MapView map;

        public ProductoDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.idTituloProducto);
            this.tipo = itemView.findViewById(R.id.idTipoProducto);
            this.coste = itemView.findViewById(R.id.idCostoProducto);
            this.stock = itemView.findViewById(R.id.idStockProducto);
            this.descripcion = itemView.findViewById(R.id.idDescripcionProducto);
            this.cuidadoPlanta = itemView.findViewById(R.id.idCuidadoPlantaProducto);
            this.textView20 = itemView.findViewById(R.id.textView20);
            this.viewPagerImageSllider = itemView.findViewById(R.id.viewPagerImageSlider);
            this.map = itemView.findViewById(R.id.map);

            this.textViewClimaPrincipal = itemView.findViewById(R.id.textViewClimaPrincipal);
            this.textViewTemperatura = itemView.findViewById(R.id.textViewTemperatura);
            this.textViewHumedad = itemView.findViewById(R.id.textViewHumedad);
            this.icono = itemView.findViewById(R.id.imageViewIcon);

            this.lugaresOptimoCrecimiento = itemView.findViewById(R.id.textView8);
            this.datosUbicacion = itemView.findViewById(R.id.textView11);
            this.noResultados = itemView.findViewById(R.id.textView18);
        }
    }

    public void iniciarListaDepartamentos() {

        String url = "https://funciones-polygon.azurewebsites.net/api/departamento";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    DataDepartamento dataDepartamentos = gson.fromJson(response, DataDepartamento.class);
                    departamentos = (ArrayList<Departamento>) dataDepartamentos.getData();

                    //2. Retorno listaProvincias
                    iniciarMapaProvinciasPuntaje();

                },
                error -> Log.e("data", error.getMessage()));
        queue.add(stringRequest);

    }

    public void iniciarMapaProvinciasPuntaje() {
        String url = "https://funciones-polygon.azurewebsites.net/api/provincia";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    DataProvincia dataProvincia = gson.fromJson(response, DataProvincia.class);
                    ArrayList<Provincia> listaProvincias = (ArrayList<Provincia>) dataProvincia.getData();
                    for (Provincia p : listaProvincias) {
                        provinciasYPuntaje.put(p, 0);
                    }

                    //3. y 4. Actualizo valores del HashMap
                    iniciarMapaProvinciasTemperaturaMin(getListaProductos().get(0).getTempmin());

                },
                error -> Log.e("data", error.getMessage()));
        queue.add(stringRequest);
    }

    public void iniciarMapaProvinciasTemperaturaMin(Double tempertura) {
        String url = "https://funciones-polygon.azurewebsites.net/api/provincia?temp=" + tempertura;
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    DataProvincia dataProvincia = gson.fromJson(response, DataProvincia.class);
                    ArrayList<Provincia> listaProvinciasTemperatura = (ArrayList<Provincia>) dataProvincia.getData();

                    for (Map.Entry<Provincia, Integer> entry : provinciasYPuntaje.entrySet()) {
                        for (Provincia p : listaProvinciasTemperatura) {
                            if (p.getIdprovincia().equals(entry.getKey().getIdprovincia())) {
                                provinciasYPuntaje.put(entry.getKey(), entry.getValue() + 1);
                            }
                        }
                    }

                    iniciarMapaProvinciasTemperaturaMax(getListaProductos().get(0).getTempmax());
                },
                error -> Log.e("data", error.getMessage()));


        queue.add(stringRequest);
    }

    public void iniciarMapaProvinciasTemperaturaMax(Double tempertura) {
        String url = "https://funciones-polygon.azurewebsites.net/api/provincia?temp=" + tempertura;
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    DataProvincia dataProvincia = gson.fromJson(response, DataProvincia.class);
                    ArrayList<Provincia> listaProvinciasTemperatura = (ArrayList<Provincia>) dataProvincia.getData();

                    for (Map.Entry<Provincia, Integer> entry : provinciasYPuntaje.entrySet()) {
                        for (Provincia p : listaProvinciasTemperatura) {
                            if (p.getIdprovincia().equals(entry.getKey().getIdprovincia())) {
                                provinciasYPuntaje.put(entry.getKey(), entry.getValue() + 1);
                            }
                        }
                    }

                    //5. Itero listaDepartamentos
                    for (Departamento d : departamentos) {
                        int puntajeTotal = 0;
                        int puntajeDepartamento = 0;
                        for (Map.Entry<Provincia, Integer> entry : provinciasYPuntaje.entrySet()) {
                            if (entry.getKey().getDepartamentos_iddepartamentos().equals(d.getIddepartamentos())) {
                                puntajeTotal = puntajeTotal + 2;
                                puntajeDepartamento = puntajeDepartamento + entry.getValue();
                            }
                        }
                        Log.d("coordenadas", "DEPARTAMENTO: " + d.getNombre());
                        Log.d("coordenadas", "PUNTAJE TOTAL: " + String.valueOf(puntajeTotal));
                        Log.d("coordenadas", "PUNTAJE DEPARTAMENTO: " + String.valueOf(puntajeDepartamento));
                        Log.d("coordenadas", String.valueOf((float) puntajeDepartamento / puntajeTotal));
                        Log.d("coordenadas", "VALOR: " + ((float) puntajeDepartamento / puntajeTotal >= 0.8));
                        if ((float) puntajeDepartamento / puntajeTotal >= 0.7) {
                            retornoCoordenadasDepartamento(d.getIddepartamentos());
                        }
                    }
                },
                error -> Log.e("data", error.getMessage()));
        queue.add(stringRequest);
    }

    public void retornoCoordenadasDepartamento(String id) {
        String url = "https://funciones-polygon.azurewebsites.net/api/polygon?id=" + id;
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    DataCoordenada dataCoordenada = gson.fromJson(response, DataCoordenada.class);
                    ArrayList<Coordenada> coordenadas = (ArrayList<Coordenada>) dataCoordenada.getData();
                    dibujarPolyline(coordenadas);

                },
                error -> Log.e("data", error.getMessage()));
        queue.add(stringRequest);
    }

    public void dibujarPolyline(ArrayList<Coordenada> coordenadas) {
        PolygonOptions polygonOptions = new PolygonOptions();
        for (Coordenada c : coordenadas) {
            polygonOptions.add(new LatLng(Double.parseDouble(c.getLatitud()), Double.parseDouble(c.getLongitud())));
        }
        polygonOptions.strokeColor(Color.parseColor("#26602C"));
        polygonOptions.fillColor(Color.parseColor("#26602C"));
        Polygon polygon = mapa.addPolygon(polygonOptions);

    }
}
