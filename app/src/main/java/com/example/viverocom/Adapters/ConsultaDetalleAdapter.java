package com.example.viverocom.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.Clases.Home;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;
import com.google.android.material.divider.MaterialDivider;

import java.util.ArrayList;
import java.util.Map;

public class ConsultaDetalleAdapter extends RecyclerView.Adapter<ConsultaDetalleAdapter.ConsultaDetalleViewHolder> {

    private ArrayList<Consulta> listaConsulta;
    private Context context;

    public ConsultaDetalleAdapter(ArrayList<Consulta> listaConsulta, Context context) {
        this.setListaConsulta(listaConsulta);
        this.setContext(context);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ConsultaDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_detalleconsulta, parent, false);
        return new ConsultaDetalleAdapter.ConsultaDetalleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaDetalleViewHolder holder, int position) {
        Consulta consulta = getListaConsulta().get(position);

        if (position == 0) {
            holder.fechaConsulta.setText("Fecha de consulta: "+consulta.getFechahoraconsulta());
            if (!consulta.getIdrespuesta().equals("")) {
                holder.estadoConsulta.setText(" RESPONDIDA");
                holder.estadoConsulta.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                holder.estadoConsulta.setText(" PENDIENTE");
                holder.estadoConsulta.setTextColor(Color.parseColor("#7E877E"));
            }
        } else { //position == 1
            holder.materialDivider.setVisibility(View.VISIBLE);
            holder.respuesta.setVisibility(View.VISIBLE);
            holder.fechaConsulta.setText("Fecha de respuesta: "+consulta.getFechahoraconsulta());
            holder.estado.setVisibility(View.GONE);
            holder.estadoConsulta.setVisibility(View.GONE);
        }
        holder.asuntoConsulta.setText("Asunto: "+consulta.getAsunto());
        holder.cuerpoConsulta.setText(consulta.getCuerpo());

        //SLIDER
        if (consulta.getImagenes() != null) {
            ArrayList<Uri> listaUri = new ArrayList<>();
            for (Map.Entry<String, String> entry : consulta.getImagenes().entrySet()) {
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

    }

    @Override
    public int getItemCount() {
        return getListaConsulta().size();
    }

    public ArrayList<Consulta> getListaConsulta() {
        return listaConsulta;
    }

    public void setListaConsulta(ArrayList<Consulta> listaConsulta) {
        this.listaConsulta = listaConsulta;
    }

    public class ConsultaDetalleViewHolder extends RecyclerView.ViewHolder {

        private MaterialDivider materialDivider;
        private TextView respuesta;

        private TextView fechaConsulta;

        private TextView estado;
        private TextView estadoConsulta;

        private TextView asuntoConsulta;
        private TextView cuerpoConsulta;

        private ViewPager2 viewPagerImageSllider;

        public ConsultaDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.materialDivider = itemView.findViewById(R.id.materialDivider3);
            this.respuesta = itemView.findViewById(R.id.textView24);

            this.fechaConsulta = itemView.findViewById(R.id.idFechaConsultaDetalle);

            this.estado = itemView.findViewById(R.id.textView31);
            this.estadoConsulta = itemView.findViewById(R.id.idEstadoConsultaDetalle);

            this.asuntoConsulta = itemView.findViewById(R.id.idAsuntoConsultaDetalle);
            this.cuerpoConsulta = itemView.findViewById(R.id.idCuerpoConsultaDetalle);
            this.viewPagerImageSllider = itemView.findViewById(R.id.viewPagerImageSlider);
        }
    }

}
