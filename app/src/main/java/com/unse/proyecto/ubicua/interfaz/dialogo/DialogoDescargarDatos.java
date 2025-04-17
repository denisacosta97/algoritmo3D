package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.obj3d.LevelModule;
import com.unse.proyecto.ubicua.principal.database.Objeto3DRepo;
import com.unse.proyecto.ubicua.principal.database.PistaRepo;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.Pista;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogoDescargarDatos extends DialogFragment {

    View view;
    Context mContext;
    TextView txtTitulo;
    ProgressBar mProgressBar;
    Button btnAceptar;
    YesNoDialogListener mYesNoDialogListener;
    PreferenciasManager mPreferenciasManager;
    Objeto3DRepo mDRepo;
    PistaRepo mPistaRepo;
    int e = 0;

    public DialogoDescargarDatos(Context context, YesNoDialogListener yesNoDialogListener) {
        mContext = context;
        mYesNoDialogListener = yesNoDialogListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_descarga, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        loadListener();

        return view;
    }

    private void loadListener() {
        btnAceptar.setOnClickListener(v -> {
            if (e == 0) {
                dismiss();
                mYesNoDialogListener.yes();
            } else if (e == 1)
                loadData();
        });
    }

    private void loadData() {
        txtTitulo.setText("¡DESCARGANDO DATOS!");
        btnAceptar.setText("ACEPTAR");
        btnAceptar.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        loadInfo();
    }

    private void loadInfo() {
        procesarRespuesta("{\n" +
                "\t\"level\":{\n" +
                "\n" +
                "\t\t\"l\": [1,2,3],\n" +
                "\t\t\"xp\": [500, 1500, 3000]\n" +
                "\n" +
                "\t},\n" +
                "\t\"pistas\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 1,\n" +
                "\t\t\t\"desc\": \"Camina hacia la Escuela de Innovación Educativa\",\n" +
                "\t\t\t\"ubi\" :[-64.25462938934894,-27.80254932194857]\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"objetos\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 1,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 1,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Rueda\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/rueda.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/rueda.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25428, -27.79072]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 2,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 1,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Tuerca\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/tuerca.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/tuerca.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25433033734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 3,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 1,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Manublio\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/manublio.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/manublio.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25439933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 4,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 1,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Cubo\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/cubo.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/cubo_rojo.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25438933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 5,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 1,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Arduino Uno\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/arduino.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/arduino_uno.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25437933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 6,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 1,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Auto\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/auto.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/auto.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25463933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 7,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 0,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Cubo Minecraft\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/cubo_minecraft.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/cubo_minecraft.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25453933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 8,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 0,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Espada Minecraft\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/espada_minecraft.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/espada_minecraft.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25443933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 9,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 2,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Monitor\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/monitor.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/monitor.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25243933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 10,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 2,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Teclado\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/teclado.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/teclado.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25143933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 11,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 2,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Mouse\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/mouse.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/mouse.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25144933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 12,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 2,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"CPU\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/cpu.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/cpu.png\",\n" +
                "\t\t\t\"ubi\" :[-64.25247933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 13,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 2,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Computadora\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/pc.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/pc.png\",\n" +
                "\t\t\t\"ubi\" :[-64.252438933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 14,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 0,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Cuadro Mona Lisa\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/cuadro_pintura.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/cuadro_pintura.png\",\n" +
                "\t\t\t\"ubi\" :[-64.253438933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 15,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 3,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Snorlax\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/snorlax.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/snorlax.png\",\n" +
                "\t\t\t\"ubi\" :[-64.253438933734894,-27.80254932194857]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\" : 16,\n" +
                "\t\t\t\"xp\" : 150,\n" +
                "\t\t\t\"c\": 3,\n" +
                "\t\t\t\"p\": 0.1,\n" +
                "\t\t\t\"n\": \"Haunter\",\n" +
                "\t\t\t\"url\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/modelos/haunter.glb\",\n" +
                "\t\t\t\"img\" : \"https://sis.bienestar.unse.edu.ar/react/proyecto/imagenes/haunter.png\",\n" +
                "\t\t\t\"ubi\" :[-64.253438933734894,-27.80254932194857]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}");
    }
        /*String URL = "https://sis.bienestar.unse.edu.ar/react/proyecto/obj.json";
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                procesarRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                e = 1;
                btnAceptar.setVisibility(View.VISIBLE);
                btnAceptar.setText("REINTENTAR");
                txtTitulo.setText("¡ERROR AL DESCARGAR!");
                mProgressBar.setVisibility(View.GONE);


            }
        });
        e = 0;
        btnAceptar.setVisibility(View.VISIBLE);
        btnAceptar.setText("ACEPTAR");
        txtTitulo.setText("¡DATOS DESCARGADOS!");
        mProgressBar.setVisibility(View.GONE);
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);*/

    private void procesarRespuesta(String response) {
        try {
            mDRepo = new Objeto3DRepo(mContext);
            mPistaRepo = new PistaRepo(mContext);
            mPreferenciasManager = new PreferenciasManager(mContext);
            JSONObject jsonObject = new JSONObject(response);
            JSONArray objetos = jsonObject.getJSONArray("objetos");
            for (int i = 0; i < objetos.length(); i++) {
                JSONObject o = objetos.getJSONObject(i);
                Objeto3D objeto3D = Objeto3D.mapper(o);
                mDRepo.insert(objeto3D);
            }
            objetos = jsonObject.getJSONArray("pistas");
            for (int i = 0; i < objetos.length(); i++) {
                JSONObject o = objetos.getJSONObject(i);
                Pista pista = Pista.mapper(o);
                mPistaRepo.insert(pista);
            }

            JSONObject levels = jsonObject.getJSONObject("level");
            JSONArray niveles = levels.getJSONArray("l");
            JSONArray xp = levels.getJSONArray("xp");
            HashSet<String> set = new HashSet<>();
            for (int i = 0; i < xp.length(); i++) {
                set.add(String.valueOf(xp.getInt(i)));
            }
            mPreferenciasManager.setValue(LevelModule.XP_FOR_LEVEL, set);
            e = 0;
            Thread.sleep(2000);
            btnAceptar.setVisibility(View.VISIBLE);
            btnAceptar.setText("ACEPTAR");
            txtTitulo.setText("¡DATOS DESCARGADOS!");
            mProgressBar.setVisibility(View.GONE);
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void loadViews() {
        txtTitulo = view.findViewById(R.id.txtTitulo);
        mProgressBar = view.findViewById(R.id.progres);
        btnAceptar = view.findViewById(R.id.btnSI);

    }
}
