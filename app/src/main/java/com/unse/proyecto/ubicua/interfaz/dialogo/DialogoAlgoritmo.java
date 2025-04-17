package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.adapter.FlechasAdapter;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.Sentencia;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DialogoAlgoritmo extends DialogFragment {

    View view;
    CardView mCardView;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    FlechasAdapter mObjetosAdapter;
    Context mContext;
    ArrayList<Sentencia> mList;
    Objeto3D mObjeto3D;

    public DialogoAlgoritmo(Context context, Objeto3D mObjeto3D) {
        mContext = context;
        this.mObjeto3D = mObjeto3D;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogo_algoritmo, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Esto es lo nuevoooooooo, evita los bordes cuadrados
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        loadListener();

        return view;
    }

    private void loadViews() {
        mRecyclerView = view.findViewById(R.id.recycler);
        mCardView = view.findViewById(R.id.card);
    }

    private void loadData() {
        mList = new ArrayList<>();
        mList.add(new Sentencia(FlechasAdapter.HEADER));
        //mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "AVANZAR", R.drawable.ic_arriba, 3));
        mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "AVANZAR", R.drawable.ic_arriba));
        mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "AVANZAR", R.drawable.ic_arriba));
        //List.add(new Sentencia(FlechasAdapter.SENTENCIA, "RETROCEDER", R.drawable.ic_abajo));
        //mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "AVANZAR", R.drawable.ic_arriba));
        mList.add(new Sentencia(FlechasAdapter.CONDICIONAL, "GIRAR DERECHA", "https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/muro.png"));
        //mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "GIRAR DERECHA", R.drawable.ic_derecha));
        mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "AVANZAR", R.drawable.ic_arriba));
        mList.add(new Sentencia(FlechasAdapter.SENTENCIA, "GIRAR IZQUIERDA", R.drawable.ic_izquierda));
        mList.add(new Sentencia(FlechasAdapter.OBJECT,
               "MOUSE" //mObjeto3D.getNombre().toUpperCase()
                , R.drawable.mouse));
        mList.add(new Sentencia(FlechasAdapter.FOOTER));
        mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mObjetosAdapter = new FlechasAdapter(mList, getContext());
        mRecyclerView.setAdapter(mObjetosAdapter);
    }


    private void loadListener() {
    }


}