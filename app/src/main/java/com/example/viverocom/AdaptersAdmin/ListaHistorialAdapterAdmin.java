package com.example.viverocom.AdaptersAdmin;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viverocom.Adapters.ListaHistorialAdapter;
import com.example.viverocom.Clases.Orden;
import com.example.viverocom.R;

import java.util.ArrayList;

public class ListaHistorialAdapterAdmin extends RecyclerView.Adapter<ListaHistorialAdapterAdmin.HistorialAdminViewHolder> {

    private ArrayList<Orden> listaOrdenes;
    private Context context;

    public ListaHistorialAdapterAdmin(ArrayList<Orden> listaOrdenes, Context context) {
        this.setListaOrdenes(listaOrdenes);
        this.setContext(context);
    }

    public ArrayList<Orden> getListaOrdenes() {
        return listaOrdenes;
    }

    public void setListaOrdenes(ArrayList<Orden> listaOrdenes) {
        this.listaOrdenes = listaOrdenes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ListaHistorialAdapterAdmin.HistorialAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_historialcompras, parent, false);
        return new ListaHistorialAdapterAdmin.HistorialAdminViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaHistorialAdapterAdmin.HistorialAdminViewHolder holder, int position) {
        Orden orden = getListaOrdenes().get(position);

        holder.codigo.setText("Código: " + orden.getCodigo());
        holder.fechahistorial.setText(orden.getFechaEmision());
        holder.monto.setText("Monto total: S/" + orden.getMontoTotal());
        holder.estado.setText(orden.getEstado());
        if (orden.getEstado().equals("PENDIENTE")) {
            holder.estado.setTextColor(Color.parseColor("#7E877E"));
        } else if (orden.getEstado().equals("PROCESADA")) {
            holder.estado.setTextColor(Color.parseColor("#4CAF50"));
        } else if (orden.getEstado().equals("CANCELADA")) {
            holder.estado.setTextColor(Color.parseColor("#E45252"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", orden.getId());
                Navigation.findNavController(v).navigate(R.id.nav_historialdetalle_admin,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getListaOrdenes().size();
    }

    public class HistorialAdminViewHolder extends RecyclerView.ViewHolder {
        private TextView codigo;
        private TextView fechahistorial;
        private TextView monto;
        private TextView estado;
        public HistorialAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            this.codigo = itemView.findViewById(R.id.codigo_historial);
            this.fechahistorial = itemView.findViewById(R.id.fechahora_historial);
            this.monto = itemView.findViewById(R.id.montototal_historial);
            this.estado = itemView.findViewById(R.id.estado_historial);
        }
    }

}
