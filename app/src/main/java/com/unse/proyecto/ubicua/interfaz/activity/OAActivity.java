package com.unse.proyecto.ubicua.interfaz.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoDescargarDatos;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoProcesando;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class OAActivity extends AppCompatActivity {

    WebView webView;
    CardView cardView;
    Integer level = -1;
    public static List<String> FOLDER = new ArrayList<>();

    static {
        FOLDER.add("OA-secuencia");
        FOLDER.add("OA-condicional");
        FOLDER.add("OA-repeticin");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oa);

        if (getIntent().getIntExtra(Utils.INT_TYPE, -1) != -1) {
            level = getIntent().getIntExtra(Utils.INT_TYPE, -1);
        }

        loadViews();

        loadData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            loadListener();
        }

    }

    private void errorDialog() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo("ERROR AL CARGA OA")
                .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                .setIcono(R.drawable.ic_error)
                .setDescripcion("En este momento no es posible visualizar el OA solicitado.")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {

                    }

                    @Override
                    public void no() {

                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }

    private void preLoad() {
        YesNoDialogListener listener = new YesNoDialogListener() {
            @Override
            public void yes() {
                loadData();
            }

            @Override
            public void no() {

            }
        };
        DialogoProcesando procesando = new DialogoProcesando(getApplicationContext(), listener, "Cargando OA...");
        procesando.show(getSupportFragmentManager(), "dialogo");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadListener() {
        cardView.setOnClickListener(v -> {
            /*webView.evaluateJavascript(
                    "(function() {" +
                            "var resultText = document.getElementById('s0b7-result').innerText;" +
                            "return resultText;" +
                            "})()",
                    value -> {
                        //Procesamiento de respusta
                    }
            );
            webView.evaluateJavascript("(function() {" +
                    "    var element1 = document.getElementById('sa0b8_141');" +
                    "    var element2 = document.getElementById('sa1b8_141');" +
                    "    var element3 = document.getElementById('sa2b8_141');" +
                    "    var selected = '';" +
                    "    if (window.getComputedStyle(element1).display === 'block') { selected = element1.innerText; }" +
                    "    else if (window.getComputedStyle(element2).display === 'block') { selected = element2.innerText; }" +
                    "    else if (window.getComputedStyle(element3).display === 'block') { selected = element3.innerText; }" +
                    "    return selected;" +
                    "})()", value -> {
                value = value.trim();
                value.length();
            });
            webView.evaluateJavascript(
                    "(function() {" +
                            "var resultText = document.getElementById('exe-sortableList-0-feedback').innerText;" +
                            "return resultText;" +
                            "})()",
                    value -> {
                        // Aquí manejas el resultado después de que el usuario presiona el botón
                        value = value.trim();
                        value.length();
                    }
            );*/

            DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                    .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                    .setTitulo("¡Buen intento!")
                    .setIcono(R.drawable.ic_advertencia)
                    .setDescripcion("Recuerda que los enunciados tienen retroalimentacion para saber en qué te equivocaste.\n¡Sigue asi!")
                    .setListener(new YesNoDialogListener() {
                        @Override
                        public void yes() {
                        }

                        @Override
                        public void no() {
                        }
                    });
            DialogoGeneral dialogoGeneral = builder.build();
            dialogoGeneral.setCancelable(false);
            dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
        });
    }

    private void loadData() {
        String filePath = "file://" + getExternalFilesDir(null) + "/" + FOLDER.get(level - 1) + "/index.html";
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webView.loadUrl(filePath);
    }

    private void loadViews() {
        cardView = findViewById(R.id.cardPerfil);
        webView = findViewById(R.id.webview);
    }
}
