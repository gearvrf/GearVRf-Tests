package org.gearvrf.sample.LodTest;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRLODGroup;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMaterial.GVRShaderType;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRPositionAnimation;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.scene_objects.GVRSphereSceneObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class LODTestMain extends GVRMain {

    private List<GVRAnimation> animations = new ArrayList<GVRAnimation>();
    private GVRAnimationEngine animationEngine;

    @Override
    public void onInit(GVRContext gvrContext) throws IOException {
        animationEngine = gvrContext.getAnimationEngine();

        GVRScene scene = gvrContext.getMainScene();

        scene.setFrustumCulling(true);

        Future<GVRTexture> redFutureTexture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.red));
        Future<GVRTexture> greenFutureTexture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.green));
        Future<GVRTexture> blueFutureTexture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.blue));

        GVRSphereSceneObject root = new GVRSphereSceneObject(gvrContext);

        GVRSphereSceneObject sphereHighDensity = new GVRSphereSceneObject(gvrContext);
        sphereHighDensity.setName("sphereHighDensity");
        setupObject(gvrContext, sphereHighDensity, redFutureTexture);
        root.addChildObject(sphereHighDensity);

        GVRSphereSceneObject sphereMediumDensity = new GVRSphereSceneObject(gvrContext, 9, 9,
                true, new GVRMaterial(gvrContext));
        sphereMediumDensity.setName("sphereMediumDensity");
        setupObject(gvrContext, sphereMediumDensity, greenFutureTexture);
        root.addChildObject(sphereMediumDensity);

        GVRSphereSceneObject sphereLowDensity = new GVRSphereSceneObject(gvrContext, 6, 6,
                true, new GVRMaterial(gvrContext));
        sphereLowDensity.setName("sphereLowDensity");
        setupObject(gvrContext, sphereLowDensity, blueFutureTexture);
        root.addChildObject(sphereLowDensity);

        final GVRLODGroup lodGroup = new GVRLODGroup(gvrContext);
        lodGroup.addRange(9, sphereLowDensity);
        lodGroup.addRange(0, sphereHighDensity);
        root.attachComponent(lodGroup);
        lodGroup.addRange(5, sphereMediumDensity);

        scene.addSceneObject(root);

        for(GVRAnimation animation : animations) {
            animation.start(animationEngine);
        }
    }

    private void setupObject(GVRContext gvrContext, GVRSceneObject object, Future<GVRTexture> futureTexture) {
        object.getTransform().setPosition(0,  0,  -3.0f);
        GVRMaterial unlit = new GVRMaterial(gvrContext, GVRShaderType.Texture.ID);
        unlit.setMainTexture(futureTexture);
        object.getRenderData().setMaterial(unlit);
        setupAnimation(object);
    }

    private void setupAnimation(GVRSceneObject object) {
        GVRAnimation animation = new GVRPositionAnimation(object, 2.0f, 0.0f, 0.0f, -10.0f);
        animation.setRepeatMode(GVRRepeatMode.PINGPONG).setRepeatCount(-1);
        animations.add(animation);
    }
}
