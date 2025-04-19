package com.unse.proyecto.ubicua.interfaz.activity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.controller.OAActivityViewModel;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoProcesando;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.principal.modelo.LearningObject;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OAActivity extends AppCompatActivity {

    private static final String ARCHIVO_ZIP = "archivo.zip";
    WebView webView;
    CardView cardView;
    LearningObject learningObject;
    DialogoProcesando dialogoProcesando;
    private OAActivityViewModel viewModel;
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

        if (getIntent().getParcelableExtra(Utils.OA_DATA) != null) {
            learningObject = getIntent().getParcelableExtra(Utils.OA_DATA);
        }

        viewModel = new ViewModelProvider(this).get(OAActivityViewModel.class);

        loadViews();

        loadObservers();

        loadData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            loadListener();
        }
    }

    private void loadObservers() {
        viewModel.showLoading.observe(this, aBoolean -> {
            if (aBoolean) preLoad();
            else dialogoProcesando.dismiss();
        });

        viewModel.learningObject.observe(this, learningObject -> {
            dialogoProcesando.dismiss();
            if (learningObject != null) {
                saveOA(learningObject);
                loadOA();
            } else {
                errorDialog();
            }
        });
    }

    private void loadOA() {
        String filePath = "file://" + getExternalFilesDir(null) + "/" + getFolderName() + "/index.html";
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.loadUrl(filePath);
    }

    public String getFolderName() {
        String[] filename = learningObject.getArchivo().split("\\.");
        for (String folder : FOLDER) {
            if (folder.contains(filename[0]))
                return folder;
        }
        return "";
    }


    private void preLoad() {
        dialogoProcesando = new DialogoProcesando(getApplicationContext(), "Cargando OA...");
        dialogoProcesando.show(getSupportFragmentManager(), "dialogo");
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
        downloadOA();
    }

    private void downloadOA() {
        viewModel.getObjectFile(learningObject.getArchivo());
    }

    private void loadViews() {
        cardView = findViewById(R.id.cardPerfil);
        webView = findViewById(R.id.webview);
    }

    private void saveOA(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        File file = new File(getExternalFilesDir(null), ARCHIVO_ZIP);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            Utils.unzip(file.getAbsolutePath(), getExternalFilesDir(null) + "/");
        } catch (Exception e) {
            errorDialog();
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
                        finish();
                    }

                    @Override
                    public void no() {

                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }


}
