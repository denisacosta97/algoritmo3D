package com.unse.proyecto.ubicua.interfaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.principal.modelo.Sentencia;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FlechasAdapter extends RecyclerView.Adapter<FlechasAdapter.FlechasViewHolder> {

    public static final int HEADER = 1;
    public static final int SENTENCIA = 2;
    public static final int OBJECT = 3;
    public static final int FOOTER = 4;
    public static final int CONDICIONAL = 5;

    ArrayList<Sentencia> mList;
    Context mContextCompat;

    public FlechasAdapter(ArrayList<Sentencia> list, Context contextCompat) {
        mList = list;
        mContextCompat = contextCompat;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getTipo();
    }

    @NonNull
    @Override
    public FlechasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case HEADER:
            case FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inicio_fin, parent, false);
                break;
            case SENTENCIA:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flecha_secuencia, parent, false);
                break;
            case OBJECT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto_sentencia, parent, false);
                break;
            case CONDICIONAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flecha_condicional, parent, false);
                break;
        }
        return new FlechasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlechasViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER) {
            holder.txtTitulo.setText("INICIO");
        } else if (getItemViewType(position) == FOOTER) {
            holder.txtTitulo.setText("FIN");
        } else if (position < mList.size()) {
            Sentencia sentencia = mList.get(position);
            if (getItemViewType(position) == SENTENCIA){
                if (sentencia.getRepeat() > 0){
                    holder.txtRepeat.setText(String.valueOf(sentencia.getRepeat()));
                    holder.layRepeat.setVisibility(View.VISIBLE);
                }else{
                    holder.layRepeat.setVisibility(View.GONE);
                }
            }
            if (getItemViewType(position) == CONDICIONAL){
                holder.txtTitulo.setText("OBSTÁCULO");
                holder.txtTitulo2.setText(sentencia.getMovimiento());
            }else{
                holder.txtTitulo.setText(sentencia.getMovimiento());
            }
            Glide.with(holder.imgIcon.getContext())
                    .load(sentencia.getUrl().isEmpty()
                            ? sentencia.getImg()
                            : sentencia.getUrl())
                    .error(R.drawable.ic_error)
                    .into(holder.imgIcon);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class FlechasViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo, txtTitulo2;
        TextView txtRepeat;
        ImageView imgIcon;
        LinearLayout layRepeat;

        public FlechasViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtTitulo2 = itemView.findViewById(R.id.txtTitulo2);
            txtRepeat = itemView.findViewById(R.id.txtRepeat);
            layRepeat = itemView.findViewById(R.id.layRepeat);
        }
    }
}
