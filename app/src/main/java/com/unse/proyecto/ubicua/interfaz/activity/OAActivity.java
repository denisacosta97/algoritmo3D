package com.unse.proyecto.ubicua.interfaz.activity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.controller.OAActivityViewModel;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoProcesando;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.principal.modelo.LearningObject;
import com.unse.proyecto.ubicua.principal.modelo.OAResult;
import com.unse.proyecto.ubicua.principal.modelo.ObjectRules;
import com.unse.proyecto.ubicua.principal.modelo.ResponseStatus;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OAActivity extends AppCompatActivity {

    private static final String ARCHIVO_ZIP = "archivo.zip";
    public static final String FUNCTION = "(function() { %s })()";
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

    private void loadListener() {
        cardView.setOnClickListener(v -> validateRules());
    }

    private void validateRules() {
        int totalQuestions = learningObject.getRules().size();

        if (totalQuestions == 0) {
            showCongratsDialog(OAResult.EMPTY);
            return;
        }

        List<ResponseStatus> responseList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (ObjectRules rules : learningObject.getRules()) {
                String code = String.format(FUNCTION, rules.getCode());

                webView.evaluateJavascript(code, value -> {
                    value = value.trim().replace("\"", "");
                    if (matchesResponse(value, rules.getEmpty())) {
                        responseList.add(ResponseStatus.PENDING);
                    } else if (matchesResponse(value, rules.getSuccess())) {
                        responseList.add(ResponseStatus.SUCCESS);
                    } else if (matchesResponse(value, rules.getError())) {
                        responseList.add(ResponseStatus.ERROR);
                    }
                    if (responseList.size() == totalQuestions) {
                        processEvaluationResult(responseList);
                    }
                });

            }
        } else {
            showErrorDialog("Tu dispositivo no es compatible con esta evaluación. Requiere Android 4.4 (KitKat) o superior.");
        }
    }

    private boolean matchesResponse(String value, String expected) {
        if (value == null || expected == null) return false;

        String cleanValue = value.trim().toLowerCase();
        String cleanExpected = expected.trim().toLowerCase();

        // Caso especial: ambos vacíos
        if (cleanValue.isEmpty() && cleanExpected.isEmpty()) return true;

        // Evitamos que un expected vacío haga match con cualquier cosa
        if (cleanExpected.isEmpty()) return false;


        return cleanValue.startsWith(cleanExpected) || cleanValue.contains(cleanExpected);
    }

    private void processEvaluationResult(List<ResponseStatus> responses) {
        if (responses.isEmpty()) {
            showErrorDialog("No se detectaron respuestas. Asegúrate de completar la evaluación.");
            return;
        }

        OAResult result = buildOAResult(responses);

        if (responses.contains(ResponseStatus.PENDING)) {
            showErrorDialog("Debes responder todas las preguntas antes de enviar la evaluación.");
        } else if (responses.contains(ResponseStatus.ERROR)) {
            showTryAgainDialog(result);
        } else if (allSuccess(responses)) {
            showCongratsDialog(result);
        } else {
            showErrorDialog("Ocurrió un error inesperado. Intenta nuevamente.");
        }
    }

    private OAResult buildOAResult(List<ResponseStatus> responses) {
        int aciertos = 0;
        int errores = 0;

        for (ResponseStatus status : responses) {
            if (status == ResponseStatus.SUCCESS) {
                aciertos++;
            } else if (status == ResponseStatus.ERROR) {
                errores++;
            }
        }

        return new OAResult(responses.size(), aciertos, errores);
    }

    public void showErrorDialog(String message){
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                .setTitulo("")
                .setIcono(R.drawable.ic_advertencia)
                .setDescripcion(message)
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() { }

                    @Override
                    public void no() {
                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }

    public void showCongratsDialog(OAResult result){
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                .setTitulo("¡Felicidades!")
                .setIcono(R.drawable.ic_check)
                .setDescripcion("Tus respuestas fueron correctas\n¡continua buscando objetos!")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        processResponse(result);
                    }

                    @Override
                    public void no() {
                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }

    public void showTryAgainDialog(OAResult result){
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                .setTitulo("¡Buen intento!")
                .setIcono(R.drawable.ic_advertencia)
                .setDescripcion("Recuerda que los enunciados tienen retroalimentacion " +
                        "para saber en qué te equivocaste.\n¡Sigue asi!")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        processResponse(result);
                    }

                    @Override
                    public void no() {
                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }

    private void processResponse(OAResult result) {

    }

    private boolean allSuccess(List<ResponseStatus> responses) {
        for (ResponseStatus status : responses) {
            if (!ResponseStatus.SUCCESS.equals(status)) {
                return false;
            }
        }
        return true;
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
