package com.example.viverocom.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viverocom.Clases.Home;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<Home> listaHome;
    private Context context;

    public HomeAdapter(ArrayList<Home> listaHome, Context context) {
        this.setListaHome(listaHome);
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
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_home, parent, false);
        return new HomeAdapter.HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Home home = getListaHome().get(position);
        holder.title.setText(home.getTitulo());

        ListaProductosAdapter listaProductosAdapter = new ListaProductosAdapter(home.getListaProductos(),getContext());
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.childRecyclerView.setAdapter(listaProductosAdapter);
    }

    @Override
    public int getItemCount() {
        return getListaHome().size();
    }

    public ArrayList<Home> getListaHome() {
        return listaHome;
    }

    public void setListaHome(ArrayList<Home> listaHome) {
        this.listaHome = listaHome;
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView childRecyclerView;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.idTitulo);
            this.childRecyclerView = itemView.findViewById(R.id.idRecyclerView_titulo);
        }
    }

}
