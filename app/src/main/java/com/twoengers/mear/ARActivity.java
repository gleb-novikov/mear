package com.twoengers.mear;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ARActivity extends AppCompatActivity {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    ArFragment arFragment;
    ModelRenderable lampPostRenderable;

    ModelRenderable mercuryRenderable;
    ModelRenderable venusRenderable;
    ModelRenderable earthRenderable;
    ModelRenderable marsRenderable;
    ModelRenderable jupiterRenderable;
    ModelRenderable saturnRenderable;
    ModelRenderable uranusRenderable;
    ModelRenderable neptuneRenderable;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        setContentView(R.layout.ar_activity);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        Button b = findViewById(R.id.check);
        b.setOnClickListener(view -> {
            
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("server.sfb"))
                    .build()
                    .thenAccept(renderable -> lampPostRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("mercury.sfb"))
                    .build()
                    .thenAccept(renderable -> mercuryRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("venus.sfb"))
                    .build()
                    .thenAccept(renderable -> venusRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("earth.sfb"))
                    .build()
                    .thenAccept(renderable -> earthRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("mars.sfb"))
                    .build()
                    .thenAccept(renderable -> marsRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("jupiter.sfb"))
                    .build()
                    .thenAccept(renderable -> jupiterRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("saturn.sfb"))
                    .build()
                    .thenAccept(renderable -> saturnRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("uranus.sfb"))
                    .build()
                    .thenAccept(renderable -> uranusRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("neptune.sfb"))
                    .build()
                    .thenAccept(renderable -> neptuneRenderable = renderable)
                    .exceptionally(throwable -> {
                        Toast toast =
                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });
        }

        arFragment.setOnTapArPlaneListener(
                (HitResult hitresult, Plane plane, MotionEvent motionevent) -> {
                    /*if (lampPostRenderable == null){
                        return;
                    }*/

                    Anchor anchor = hitresult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode planet = new TransformableNode(arFragment.getTransformationSystem());
                    planet.setParent(anchorNode);
                    planet.select();

                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(ARActivity.this);
                    builder.setTitle("Выбрать планету:");
                    // add a list
                    String[] planets = {"Марс", "Венера", "Юпитер", "Сатурн", "Меркурий", "Уран", "Земля", "Нептун"};
                    builder.setItems(planets, (dialog, which) -> {
                        switch (which) {
                            case 0: planet.setRenderable(marsRenderable);
                            case 1: planet.setRenderable(venusRenderable);
                            case 2: planet.setRenderable(jupiterRenderable);
                            case 3: planet.setRenderable(saturnRenderable);
                            case 4: planet.setRenderable(mercuryRenderable);
                            case 5: planet.setRenderable(uranusRenderable);
                            case 6: planet.setRenderable(earthRenderable);
                            case 7: planet.setRenderable(neptuneRenderable);
                        }
                    });
                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
/*
                    Anchor anchor = hitresult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode lamp = new TransformableNode(arFragment.getTransformationSystem());
                    lamp.setParent(anchorNode);
                    lamp.setRenderable(lampPostRenderable);
                    lamp.select();
                    lamp.getLocalPosition().*/
                }
        );

    }

    private void onUpdate(FrameTime frameTime, Node contentNode) {
        ArrayList<Node> overlappedNodes = arFragment.getArSceneView().getScene().overlapTestAll(contentNode);
        for (Node node : overlappedNodes) {
            /*if (node instanceof PassiveNode) {
                // May want to use a flag to check that the node wasn't overlapping the previous frame.
                // Play sound if overlapping started.
            }*/
        }
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
