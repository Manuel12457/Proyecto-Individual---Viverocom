package com.example.viverocom.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.viverocom.Adapters.CarritoAdapter;
import com.example.viverocom.Adapters.ImagenesEdicionProductoAdapter;
import com.example.viverocom.Adapters.ProductoDetalleAdapter;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.R;
import com.example.viverocom.SharedViewModel;
import com.example.viverocom.databinding.FragmentNavCarritoBinding;
import com.example.viverocom.databinding.FragmentNavConsultasBinding;
import com.example.viverocom.databinding.FragmentNavHistorialBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class nav_carrito extends Fragment implements ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved, ImagenesEdicionProductoAdapter.ItemClickListener {

    Button btnFecha;
    int hora = 0;
    Button btnHora;
    int min = 0;
    String fecha;
    Long fechaInMilliseconds = MaterialDatePicker.todayInUtcMilliseconds();
    MaterialDatePicker<Long> materialDatePicker;

    DatabaseReference databaseReferenceOrdenes;
    ValueEventListener valueEventListenerOrdenesDetalle;

    CarritoAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;

    SharedViewModel viewModel;
    ArrayList<OrdenDetalle> carrito = new ArrayList<>();
    private FragmentNavCarritoBinding binding;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_nav_carrito, container, false);
        binding = FragmentNavCarritoBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        recyclerView = view.findViewById(R.id.idRecyclerCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CarritoAdapter(carrito, getContext(),this,this);
        recyclerView.setAdapter(adapter);

        btnFecha = view.findViewById(R.id.btnFechaCarrito);
        btnHora = view.findViewById(R.id.btnHoraCarrito);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCarrito().observe(getViewLifecycleOwner(), new Observer<ArrayList<OrdenDetalle>>() {
            @Override
            public void onChanged(ArrayList<OrdenDetalle> ordenDetalles) {
                Log.d("prueba", "TAMANIO LISTA ORDEN DETALLES DE MODELVIEW: " + String.valueOf(ordenDetalles.size()));
                carrito = ordenDetalles;
                Log.d("prueba", String.valueOf(carrito.size()));
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void openTimePicker() {
        boolean isSystem24Hour = DateFormat.is24HourFormat(getContext());
        int formatoTiempo = (isSystem24Hour) ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(formatoTiempo)
                        .setHour(hora)
                        .setMinute(min)
                        .setTitleText("Seleccione la hora de reocojo")
                        .build();
        picker.show(getChildFragmentManager(),"tag");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hora = picker.getHour();
                String horaStr = String.valueOf(hora);
                if (hora <= 9) {
                    horaStr = "0"+horaStr;
                }
                min = picker.getMinute();
                String minStr = String.valueOf(min);
                if (min <= 9) {
                    minStr = "0"+minStr;
                }
                btnHora.setText(horaStr+":"+minStr+" h");
            }
        });

    }

    private void openDatePicker() {
        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione la fecha de recojo")
                .setSelection(fechaInMilliseconds)
                .setCalendarConstraints(calendarConstraints.build())
                .build();
        materialDatePicker.show(getChildFragmentManager(),"tag");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                fecha = materialDatePicker.getHeaderText();
                Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.setTimeInMillis(selection);
                fechaInMilliseconds = utc.getTimeInMillis();
                btnFecha.setText(fecha);
            }
        });
    }

    @Override
    public void clicked(int getSize) {

    }

    @Override
    public void itemClick(int position) {

    }
}