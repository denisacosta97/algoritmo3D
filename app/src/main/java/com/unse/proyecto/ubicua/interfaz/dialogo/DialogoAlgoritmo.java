package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unse.compilador.lenguaje.model.Movement;
import com.unse.compilador.lenguaje.model.MovementType;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.adapter.FlechasAdapter;
import com.unse.proyecto.ubicua.interfaz.listener.OnClickListener;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.Sentencia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogoAlgoritmo extends DialogFragment {

    View view;
    CardView mCardView;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    FlechasAdapter mObjetosAdapter;
    Context mContext;
    ArrayList<Sentencia> mList;
    Objeto3D mObjeto3D;
    AppCompatImageView btnClose;
    OnClickListener<Objeto3D> listener;

    private final Map<MovementType, String> map;
    private final Map<MovementType, Integer> mapImage;

    public DialogoAlgoritmo(Context context, Objeto3D mObjeto3D, OnClickListener<Objeto3D> listener) {
        this.mContext = context;
        this.listener = listener;
        this.mObjeto3D = mObjeto3D;
        this.map = new HashMap<>();
        this.map.put(MovementType.ADVANCE, "AVANZAR");
        this.map.put(MovementType.BACK, "RETROCEDER");
        this.map.put(MovementType.LEFT, "GIRAR IZQUIERDA");
        this.map.put(MovementType.RIGHT, "GIRAR DERECHA");
        this.mapImage = new HashMap<>();
        this.mapImage.put(MovementType.ADVANCE, R.drawable.ic_arriba);
        this.mapImage.put(MovementType.BACK, R.drawable.ic_abajo);
        this.mapImage.put(MovementType.LEFT, R.drawable.ic_izquierda);
        this.mapImage.put(MovementType.RIGHT, R.drawable.ic_derecha);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogo_algoritmo, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        loadListener();

        return view;
    }

    private void loadListener() {
        btnClose.setOnClickListener(v -> {
            this.dismiss();
            if (listener != null)
                listener.onClick(mObjeto3D);
        });
    }

    private void loadViews() {
        mRecyclerView = view.findViewById(R.id.recycler);
        mCardView = view.findViewById(R.id.card);
        btnClose = view.findViewById(R.id.btn_close);
    }

    private void loadData() {
        mList = new ArrayList<>();
        processList();
        mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mObjetosAdapter = new FlechasAdapter(mList, getContext());
        mRecyclerView.setAdapter(mObjetosAdapter);
    }

    private void processList() {
        mList.add(new Sentencia(FlechasAdapter.HEADER));
        for (Movement movement : mObjeto3D.getMovementList()) {
            mList.add(buildSentencia(movement));
        }
        mList.add(new Sentencia(FlechasAdapter.OBJECT, mObjeto3D.getNombre().toUpperCase(), mObjeto3D.getUrlImg()));
        mList.add(new Sentencia(FlechasAdapter.FOOTER));
    }

    private Sentencia buildSentencia(Movement movement) {
        switch (movement.getType()) {
            case ADVANCE:
            case BACK:
            case LEFT:
            case RIGHT:
                return new Sentencia(FlechasAdapter.SENTENCIA, map.get(movement.getType()), mapImage.get(movement.getType()));
            case REPEAT:
                return new Sentencia(FlechasAdapter.SENTENCIA, map.get(movement.getMovement()), mapImage.get(movement.getMovement()), movement.getValue());
            case IF:
                if (mObjeto3D.getBlockObject().isEmpty())
                    return new Sentencia(FlechasAdapter.CONDICIONAL, map.get(movement.getMovement()), R.drawable.ic_multiple_3d);
                else
                    return new Sentencia(FlechasAdapter.CONDICIONAL, map.get(movement.getMovement()), mObjeto3D.getBlockObject());

        }
        return null;
    }
}