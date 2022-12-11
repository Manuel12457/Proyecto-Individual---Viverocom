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

import com.example.viverocom.Adapters.ListaConsultasAdapter;
import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.R;

import java.util.ArrayList;

public class ListaConsultasAdapterAdmin extends RecyclerView.Adapter<ListaConsultasAdapterAdmin.ConsultaAdminViewHolder> {

    private ArrayList<Consulta> listaConsultas;
    private Context context;

    public ListaConsultasAdapterAdmin(ArrayList<Consulta> listaConsultas, Context context) {
        this.setListaConsultas(listaConsultas);
        this.setContext(context);
    }

    public ArrayList<Consulta> getListaConsultas() {
        return listaConsultas;
    }

    public void setListaConsultas(ArrayList<Consulta> listaConsultas) {
        this.listaConsultas = listaConsultas;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ListaConsultasAdapterAdmin.ConsultaAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_consulta, parent, false);
        return new ListaConsultasAdapterAdmin.ConsultaAdminViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaConsultasAdapterAdmin.ConsultaAdminViewHolder holder, int position) {
        Consulta consulta = getListaConsultas().get(position);
        holder.asunto.setText("Asunto: " + consulta.getAsunto());
        holder.fechahoraconsulta.setText(consulta.getFechahoraconsulta());
        if (consulta.getIdrespuesta().equals("")) {
            holder.estado.setText("PENDIENTE");
            holder.estado.setTextColor(Color.parseColor("#7E877E"));
        } else {
            holder.estado.setText("RESPONDIDA");
            holder.estado.setTextColor(Color.parseColor("#4CAF50"));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", consulta.getId());
                Navigation.findNavController(v).navigate(R.id.nav_detalleconsulta_admin,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListaConsultas().size();
    }

    public class ConsultaAdminViewHolder extends RecyclerView.ViewHolder {
        private TextView asunto;
        private TextView fechahoraconsulta;
        private TextView estado;
        public ConsultaAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            this.asunto = itemView.findViewById(R.id.asunto_consulta);
            this.fechahoraconsulta = itemView.findViewById(R.id.fechahora_consulta);
            this.estado = itemView.findViewById(R.id.estado_consulta);
        }
    }

}
