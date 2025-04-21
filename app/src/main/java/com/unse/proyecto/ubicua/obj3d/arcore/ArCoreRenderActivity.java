package com.unse.proyecto.ubicua.obj3d.arcore;

import static com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService.BASE_URL;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.network.model.response.UserResponse;
import com.unse.proyecto.ubicua.obj3d.DownloadedObject3D;
import com.unse.proyecto.ubicua.obj3d.Modelo3D;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;
import java.util.Collection;

public class ArCoreRenderActivity extends AppCompatActivity implements View.OnClickListener {

    AnchorNode mAnchorNode, mAnchorNode2, nodeInicio, nodeFin;
    Modelo3D modelo, modelo2;
    ArFragment mArSceneView;
    Objeto3D objeto3D;
    CardView cardCargando, cardMensaje, cardClose, cardReAnchor;
    ArrayList<TransformableNode> modelos;
    Integer level;
    PreferenciasManager preferenciasManager;
    boolean placed = false, foundMode = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcore_render);

        getIntentData();

        if (objeto3D != null) {
            loadViews();

            loadData();

            loadListener();

            waitForArSceneViewReady();
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.objNoLoad));
            finish();
        }
    }

    private void getIntentData() {
        if (getIntent().getParcelableExtra(Utils.OBJ_INFO) != null) {
            objeto3D = getIntent().getParcelableExtra(Utils.OBJ_INFO);
        }
        if (getIntent().getBooleanExtra(Utils.FOUND_MODE, false)) {
            foundMode = true;
        }
    }

    private void waitForArSceneViewReady() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (mArSceneView != null && mArSceneView.getArSceneView() != null
                    && mArSceneView.getArSceneView().getScene() != null) {
                setupModel();
                arConfig();
            } else {
                waitForArSceneViewReady();
            }
        }, 100);
    }

    private void loadListener() {
        cardReAnchor.setOnClickListener(this);
        cardClose.setOnClickListener(this);
    }

    private void loadData() {
        modelos = new ArrayList<>();
        preferenciasManager = new PreferenciasManager(getApplicationContext());
        UserResponse userResponse = preferenciasManager.getObject(Utils.USER_DATA, UserResponse.class);
        level = userResponse.getLevel();
        cardReAnchor.setVisibility(View.INVISIBLE);
    }



    private void arConfig() {
        mArSceneView.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            /*Anchor anchor = hitResult.createAnchor();
            mAnchorNode = new AnchorNode(anchor);
            mAnchorNode.setParent(mArSceneView.getArSceneView().getScene());

            if (nodeInicio == null) {
                nodeInicio = new AnchorNode(anchor);
                nodeInicio.setParent(mArSceneView.getArSceneView().getScene());
                createModel(nodeInicio, 1);
            } else {
                nodeFin = new AnchorNode(anchor);
                nodeFin.setParent(mArSceneView.getArSceneView().getScene());
                createModel(nodeFin, 2);
            }*/

        });
        mArSceneView.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if (placed) return;

            Frame frame = mArSceneView.getArSceneView().getArFrame();
            Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

            for (Plane plane : planes) {
                if (plane.getTrackingState() == TrackingState.TRACKING && !placed) {
                    Pose pose = plane.getCenterPose();

                    if (level == 2){
                        generateBlock(plane, pose);
                    }else{
                        Anchor anchor1 = plane.createAnchor(pose);
                        AnchorNode anchorNode1 = new AnchorNode(anchor1);
                        anchorNode1.setParent(mArSceneView.getArSceneView().getScene());
                        createModel(anchorNode1, true);
                    }
                    placed = true;
                    break;
                }
            }
        });

        mArSceneView.getArSceneView().getScene().addOnPeekTouchListener((hitTestResult, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Node node = hitTestResult.getNode();
                if (node != null) {
                    String nombre = node.getName();
                    Utils.showLog(nombre);
                }
            }
        });
        setIconForScan();
    }

    private void generateBlock(Plane plane, Pose pose) {
        // Anchor 1: en la posición original
        Anchor anchor1 = plane.createAnchor(pose);
        AnchorNode anchorNode1 = new AnchorNode(anchor1);
        anchorNode1.setParent(mArSceneView.getArSceneView().getScene());
        createModel(anchorNode1, false);

        // Anchor 2: desplazado hacia adelante 10cm en la dirección de visión del plano
        Pose poseOffset = desplazarPose(pose, 0.60f);
        Anchor anchor2 = plane.createAnchor(poseOffset);
        AnchorNode anchorNode2 = new AnchorNode(anchor2);
        anchorNode2.setParent(mArSceneView.getArSceneView().getScene());
        createModel(anchorNode2, true);
    }

    public static Pose desplazarPose(Pose pose, float distanciaAdelante) {
        float[] zAxis = pose.getZAxis(); // dirección "hacia atrás"

        // Invertimos el eje Z para obtener el "forward"
        float[] forward = {
                -zAxis[0] * distanciaAdelante,
                -zAxis[1] * distanciaAdelante,
                -zAxis[2] * distanciaAdelante
        };

        float[] nuevaPosicion = {
                pose.tx() + forward[0],
                pose.ty() + forward[1],
                pose.tz() + forward[2]
        };

        return new Pose(nuevaPosicion, pose.getRotationQuaternion());
    }


    private void setIconForScan() {
        ViewGroup container = findViewById(R.id.sceneform_hand_layout);
        container.removeAllViews();

        View phoneImage = getLayoutInflater().inflate(R.layout.item_move_phone, container, false);
        container.addView(phoneImage);

        mArSceneView.getPlaneDiscoveryController().setInstructionView(phoneImage);
        mArSceneView.getPlaneDiscoveryController().show(); // Asegura visibilidad
    }


    private void setupModel() {
        cardMensaje.setVisibility(View.INVISIBLE);
        cardCargando.setVisibility(View.VISIBLE);
        modelo = new DownloadedObject3D(getApplicationContext(), objeto3D.getUrl());
        modelo.build(new YesNoDialogListener() {
            @Override
            public void yes() {
                cardCargando.setVisibility(View.GONE);
                cardMensaje.setVisibility(View.VISIBLE);
            }
            @Override
            public void no() {
                errorRenderable(true);
            }
        });
        if (level == 2){
            modelo2 = new DownloadedObject3D(getApplicationContext(), BASE_URL + "objects/model/muro_piedras.glb");
            modelo2.build(new YesNoDialogListener() {
                @Override
                public void yes() {
                    cardMensaje.setVisibility(View.VISIBLE);
                }
                @Override
                public void no() {
                    errorRenderable(true);
                }
            });
        }
    }

    private void errorRenderable(boolean change) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(change ? "¡ERROR DE DESCARGA!" : "¡ERROR DE CARGA!")
                .setTipo(change ? DialogoGeneral.TIPO_LIBRE : DialogoGeneral.TIPO_ACEPTAR)
                .setTextButtonSi("REINTENTAR")
                .setTextButtonNo("CANCELAR")
                .setIcono(R.drawable.ic_error)
                .setDescripcion("HUBO UN ERROR AL INTENTAR CARGAR EL OBJETO" + (change ? ", POR FAVOR REINTENTE" : ""))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        setupModel();
                    }

                    @Override
                    public void no() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo_error");
    }

    private void createModel(AnchorNode anchorNode, Boolean first) {
        cardMensaje.setVisibility(View.GONE);
        TransformableNode node = new TransformableNode(mArSceneView.getTransformationSystem());
        node.setName(objeto3D.getNombre());
        node.getScaleController().setMinScale(0.4999f);
        node.getRotationController().setEnabled(false);
        node.getScaleController().setEnabled(false);
        node.getTranslationController().setEnabled(false);
        node.setParent(anchorNode);
        if (getModelo(first) != null) {
            node.setRenderable(getModelo(first));
            node.select();
            modelos.add(node);
        } else {
            errorRenderable(false);
        }
    }

    private Renderable getModelo(Boolean first) {
        return first ? modelo.getModel() : modelo2.getModel();
    }

    private void loadViews() {
        cardCargando = findViewById(R.id.cardLoad);
        cardClose = findViewById(R.id.cardClose);
        cardMensaje = findViewById(R.id.cardMovePhone);
        cardReAnchor = findViewById(R.id.cardAnchor);
        mArSceneView = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arView);
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        mArSceneView = (ArFragment) fragmentManager.findFragmentByTag("AR_FRAGMENT");
        if (mArSceneView == null) {
            mArSceneView = new ArFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.ar_container, mArSceneView, "AR_FRAGMENT")
                    .commitNow();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mArSceneView != null && mArSceneView.getArSceneView() != null) {
            mArSceneView.getArSceneView().pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mArSceneView != null) {
            mArSceneView.getArSceneView().destroy();
            mArSceneView = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardClose:
                if (foundMode){
                    if (!modelos.isEmpty()){
                        founded();
                    }else{
                        noFounded();
                    }
                }else{
                    onBackPressed();
                }
                break;
        }
    }

    private void noFounded() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo("¡OBJETO AÚN NO VISUALIZADO!")
                .setTipo(DialogoGeneral.TIPO_SI_NO)
                .setIcono(R.drawable.ic_advertencia)
                .setDescripcion("¿ESTÁ SEGURO QUE DESEA CERRAR EL MODO RA?" +
                        "\nSI CIERRA, EL OBJETO NO SERÁ REGISTRADO")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        setResult(Activity.RESULT_CANCELED);
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

    private void founded() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo("¡OBJETO ENCONTRADO!")
                .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                .setIcono(R.drawable.ic_check)
                .setDescripcion("AHORA ES MOMENTO DE COMPRENDER LO REALIZADO")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        setResult(Activity.RESULT_OK);
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
