/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.viewmanager.controls;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderPass.GVRCullFaceEnum;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScript;
import org.gearvrf.GVRTexture;
import org.gearvrf.tests.R;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRRotationByAxisAnimation;
import org.gearvrf.animation.GVRScaleAnimation;
import org.gearvrf.animation.GVRTransformAnimation;
import org.gearvrf.periodic.GVRPeriodicEngine;
import org.gearvrf.scene_objects.GVRConeSceneObject;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.viewmanager.controls.Worm.MovementDirection;
import org.gearvrf.viewmanager.controls.focus.ControlSceneObjectBehavior;
import org.gearvrf.viewmanager.controls.gamepad.GamepadObject;
import org.gearvrf.viewmanager.controls.input.GamepadInput;
import org.gearvrf.viewmanager.controls.menu.Menu;
import org.gearvrf.viewmanager.controls.util.RenderingOrder;
import org.gearvrf.viewmanager.controls.util.Util;
import org.gearvrf.viewmanager.controls.util.VRSamplesTouchPadGesturesDetector.SwipeDirection;

import java.util.ArrayList;
import java.util.concurrent.Future;

public class Test01MainScript extends GVRScript {

    private GVRContext mGVRContext;
    private GVRScene scene;

    private Worm worm;
    private GVRSceneObject skybox, surroundings, sun, ground, fence;
    private GVRSceneObject clouds;
    private float GROUND_Y_POSITION = -1;
    private float SKYBOX_SIZE = 1;
    private float SUN_ANGLE_POSITION = 30;
    private float SUN_Y_POSITION = 10;
    private float CLOUDS_DISTANCE = 15;

    private float SCENE_SIZE = 0.75f;
    private float SCENE_Y = -1.0f;

    private Menu mMenu = null;

    private GamepadObject gamepadObject;
    GVRSceneObject video;
    GVRSphereSceneObject gvrSphereSceneObject;
    GVRCubeSceneObject gvrCubeSceneObject;
    GVRConeSceneObject gvrConeSceneObject;
    GVRCylinderSceneObject gvrCylinderSceneObject;
    GVRTextViewSceneObject gvrTextViewSceneObject;
    GVRTextViewSceneObject gvrTextViewSceneObject2;
    MediaPlayer mediaPlayer;
    GVRPeriodicEngine.PeriodicEvent EV;
    GVRPeriodicEngine.PeriodicEvent EV2;
    GVRPeriodicEngine.PeriodicEvent EV3;

    @Override
    public void onInit(GVRContext gvrContext) {

        closeSplashScreen();

        // save context for possible use in onStep(), even though that's empty
        // in this sample
        mGVRContext = gvrContext;

        scene = gvrContext.getMainScene();

        gvrContext.getMainScene().getMainCameraRig().getRightCamera()
                .setBackgroundColor(Color.GREEN);
        gvrContext.getMainScene().getMainCameraRig().getLeftCamera()
                .setBackgroundColor(Color.GREEN);

        // set background color
        GVRCameraRig mainCameraRig = scene.getMainCameraRig();
        mainCameraRig.getRightCamera().setBackgroundColor(Color.GREEN);
        mainCameraRig.getLeftCamera().setBackgroundColor(Color.RED);
        mainCameraRig.getTransform().setPositionY(0);

        createSkybox();
        createClouds();
        createGround();

        //mediaPlayer = MediaPlayer.create(mGVRContext.getContext(), R.drawable.tron);
        //mediaPlayer.start();
        //video = new GVRVideoSceneObject(mGVRContext, 4.0f, 4.0f, mediaPlayer, GVRVideoSceneObject.GVRVideoType.MONO);
        //video.getTransform().setPosition(2.0f, 0.0f, -5.0f);

        // load texture asynchronously
        Future<GVRTexture> futureTexture0 = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.earthmap1k));
        Future<GVRTexture> futureTexture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.gearvr_logo));
        Future<GVRTexture> futureTextureTop = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.top));
        Future<GVRTexture> futureTextureBottom = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.bottom));
        ArrayList<Future<GVRTexture>> futureTextureList = new ArrayList<Future<GVRTexture>>(3);
        futureTextureList.add(futureTextureTop);
        futureTextureList.add(futureTexture);
        futureTextureList.add(futureTextureBottom);

        // setup material
        GVRMaterial material = new GVRMaterial(gvrContext);
        material.setMainTexture(futureTexture);

        gvrSphereSceneObject = new GVRSphereSceneObject(mGVRContext,true,futureTexture0);
        gvrSphereSceneObject.getTransform().setPosition(-2.0f, 1.0f, -5.0f);

        gvrCubeSceneObject = new GVRCubeSceneObject(mGVRContext,true,futureTexture0);
        gvrCubeSceneObject.getTransform().setPosition(-2.0f, 1.0f, -5.0f);

        gvrCylinderSceneObject = new GVRCylinderSceneObject(gvrContext, 0.5f, 0.5f, 1.0f, 10, 36, true, futureTextureList, 2, 4);
        //gvrCylinderSceneObject = new GVRCylinderSceneObject(mGVRContext,true,futureTexture2);
        gvrCylinderSceneObject.getTransform().setPosition(2.0f, 1.0f, -5.0f);

        gvrTextViewSceneObject = new GVRTextViewSceneObject(mGVRContext);
        float txtSize=gvrTextViewSceneObject.getTextSize();
        gvrTextViewSceneObject.setTextSize(txtSize * 1.2f);
        gvrTextViewSceneObject.getTransform().setPosition(0.0f, -1.0f, -5.0f);
        gvrTextViewSceneObject.setTextColor(Color.BLUE);
        gvrTextViewSceneObject.setText("waiting collision");
        gvrTextViewSceneObject.setRefreshFrequency(GVRTextViewSceneObject.IntervalFrequency.HIGH);



        gvrTextViewSceneObject2 = new GVRTextViewSceneObject(mGVRContext);
        float txtSize2=gvrTextViewSceneObject2.getTextSize();
        gvrTextViewSceneObject2.setTextSize(txtSize2 * 1.2f);
        gvrTextViewSceneObject2.getTransform().setPosition(0.0f, 2.0f, -5.0f);
        gvrTextViewSceneObject2.setTextColor(Color.YELLOW);
        gvrTextViewSceneObject2.setText("Repetition:");
        gvrTextViewSceneObject2.setRefreshFrequency(GVRTextViewSceneObject.IntervalFrequency.HIGH);




        //scene.addSceneObject(video);
        scene.addSceneObject(gvrSphereSceneObject);
        scene.addSceneObject(gvrCubeSceneObject);
        scene.addSceneObject(gvrCylinderSceneObject);
        scene.addSceneObject(gvrTextViewSceneObject);
        scene.addSceneObject(gvrTextViewSceneObject2);

        createSun();
        createSurroundings();
        createWorm();
        createFence();
        createMenu();
        createGamepad3D();

        final GVRAnimationEngine gvrAnimationEngine = mGVRContext.getAnimationEngine();
        Runnable pulse = new Runnable() {

            public void run() {
                new GVRScaleAnimation(gvrCubeSceneObject, 0.5f, 0.5f,0.5f,0.5f) //
                        .setRepeatMode(GVRRepeatMode.PINGPONG) //
                        .start(gvrAnimationEngine);
            }
        };


        Runnable pulse2 = new Runnable() {

            public void run() {
                new GVRRotationByAxisAnimation(gvrSphereSceneObject,1.0f,1.0f,1.0f,0.0f,0.0f).setRepeatMode(GVRRepeatMode.PINGPONG).start(gvrAnimationEngine);
            }
        };

        Runnable pulse3 = new Runnable() {

            public void run() {
                new GVRScaleAnimation(gvrCylinderSceneObject, 0.5f, 0.5f,0.5f,0.5f) //
                        .setRepeatMode(GVRRepeatMode.PINGPONG) //
                        .start(gvrAnimationEngine);
            }
        };


        EV = mGVRContext.getPeriodicEngine().runEvery(pulse, 1.0f, 3.0f, new GVRPeriodicEngine.KeepRunning() {
            @Override
            public boolean keepRunning(GVRPeriodicEngine.PeriodicEvent event) {
                gvrTextViewSceneObject2.setText("Repetition: "+Integer.toString(event.getRunCount()));
                return true;
            }
        });
        EV.runEvery(1.0f, 1.5f, new GVRPeriodicEngine.KeepRunning() {
                @Override
                public boolean keepRunning(GVRPeriodicEngine.PeriodicEvent event) {
                    gvrTextViewSceneObject2.setText("Repetition: " + Integer.toString(event.getRunCount()));
                    gvrTextViewSceneObject2.setTextColor(Color.BLACK);
                    return true;
                }
            });

        EV2 = mGVRContext.getPeriodicEngine().runEvery(pulse2, 1.0f, 3.0f, new GVRPeriodicEngine.KeepRunning() {
            @Override
            public boolean keepRunning(GVRPeriodicEngine.PeriodicEvent event) {
                gvrTextViewSceneObject2.setText("Repetition: "+Integer.toString(event.getRunCount()));
                return true;
            }
        });
        EV2.runEvery(1.0f, 1.5f, new GVRPeriodicEngine.KeepRunning() {
            @Override
            public boolean keepRunning(GVRPeriodicEngine.PeriodicEvent event) {
                gvrTextViewSceneObject2.setText("Repetition: " + Integer.toString(event.getRunCount()));
                gvrTextViewSceneObject2.setTextColor(Color.BLACK);
                return true;
            }
        });


        EV3 = mGVRContext.getPeriodicEngine().runAfter(pulse3, 1.0f);
        EV3.runEvery(1.0f,1.5f);
        EV3.runEvery(1.0f,1.5f,2);
        /*EV3.runEvery(1.0f, 1.5f, new GVRPeriodicEngine.KeepRunning() {
            @Override
            public boolean keepRunning(GVRPeriodicEngine.PeriodicEvent event) {
                gvrTextViewSceneObject2.setText("Repetition: " + Integer.toString(event.getRunCount()));
                gvrTextViewSceneObject2.setTextColor(Color.BLACK);
                return true;
            }
        });*/


    }

    private void createFence() {

        GVRMesh mesh = mGVRContext.getAssetLoader().loadMesh(
                new GVRAndroidResource(mGVRContext, R.raw.fence));
        GVRTexture texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.atlas01));
        fence = new GVRSceneObject(mGVRContext, mesh, texture);
        fence.getTransform().setPositionY(GROUND_Y_POSITION);
        fence.getTransform().setScale(SCENE_SIZE, SCENE_SIZE, SCENE_SIZE);
        fence.getRenderData().setCullFace(GVRCullFaceEnum.None);
        fence.getRenderData().setRenderingOrder(RenderingOrder.FENCE);
        scene.addSceneObject(fence);

    }

    private void createWorm() {

        worm = new Worm(mGVRContext);
        scene.addSceneObject(worm);
    }

    private void createGround() {

        GVRMesh mesh = mGVRContext.createQuad(55, 55);
        GVRTexture texture = mGVRContext.loadTexture(new GVRAndroidResource(mGVRContext, R.drawable.ground_tile));

        ground = new GVRSceneObject(mGVRContext, mesh, texture);
        ground.getTransform().setPositionY(GROUND_Y_POSITION);
        ground.getTransform().setScale(SCENE_SIZE, SCENE_SIZE, SCENE_SIZE);
        ground.getTransform().setRotationByAxis(-90, 1, 0, 0);
        ground.getRenderData().setRenderingOrder(RenderingOrder.GROUND);
        scene.addSceneObject(ground);
    }

    private void createSkybox() {

        GVRMesh mesh = mGVRContext.getAssetLoader().loadMesh(
                new GVRAndroidResource(mGVRContext, R.raw.skybox));
        GVRTexture texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.skybox));

        skybox = new GVRSceneObject(mGVRContext, mesh, texture);
        skybox.getTransform().setScale(SKYBOX_SIZE, SKYBOX_SIZE, SKYBOX_SIZE);
        skybox.getRenderData().setRenderingOrder(RenderingOrder.SKYBOX);
        scene.addSceneObject(skybox);
    }

    private void createClouds() {

        clouds = new Clouds(mGVRContext, CLOUDS_DISTANCE, 9);
    }

    private void createSurroundings() {

        GVRMesh mesh = mGVRContext.getAssetLoader().loadMesh(
                new GVRAndroidResource(mGVRContext, R.raw.stones));
        GVRTexture texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.atlas01));

        surroundings = new GVRSceneObject(mGVRContext, mesh, texture);
        surroundings.getTransform().setScale(SCENE_SIZE, SCENE_SIZE, SCENE_SIZE);
        surroundings.getTransform().setPositionY(SCENE_Y);
        surroundings.getRenderData().setRenderingOrder(RenderingOrder.FLOWERS);
        scene.addSceneObject(surroundings);
        // ground.addChildObject(surroundings);

        mesh = mGVRContext.getAssetLoader().loadMesh(
                new GVRAndroidResource(mGVRContext, R.raw.grass));
        texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.atlas01));

        surroundings = new GVRSceneObject(mGVRContext, mesh, texture);
        surroundings.getTransform().setScale(SCENE_SIZE, SCENE_SIZE, SCENE_SIZE);
        surroundings.getTransform().setPositionY(SCENE_Y);
        scene.addSceneObject(surroundings);
        // ground.addChildObject(surroundings);
        surroundings.getRenderData().setRenderingOrder(RenderingOrder.GRASS);

        mesh = mGVRContext.getAssetLoader().loadMesh(
                new GVRAndroidResource(mGVRContext, R.raw.flowers));
        texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.atlas01));

        surroundings = new GVRSceneObject(mGVRContext, mesh, texture);
        surroundings.getTransform().setScale(SCENE_SIZE, SCENE_SIZE, SCENE_SIZE);
        surroundings.getTransform().setPositionY(SCENE_Y);
        scene.addSceneObject(surroundings);
        // ground.addChildObject(surroundings);
        surroundings.getRenderData().setRenderingOrder(RenderingOrder.FLOWERS);

        mesh = mGVRContext.getAssetLoader().loadMesh(
                new GVRAndroidResource(mGVRContext, R.raw.wood));
        texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.atlas01));
        surroundings = new GVRSceneObject(mGVRContext, mesh, texture);
        surroundings.getTransform().setScale(SCENE_SIZE, SCENE_SIZE, SCENE_SIZE);
        surroundings.getTransform().setPositionY(SCENE_Y);
        surroundings.getRenderData().setCullFace(GVRCullFaceEnum.None);
        scene.addSceneObject(surroundings);
        // ground.addChildObject(surroundings);
        surroundings.getRenderData().setRenderingOrder(RenderingOrder.WOOD);
    }

    private void createSun() {

        GVRMesh mesh = mGVRContext.createQuad(25, 25);
        GVRTexture texture = mGVRContext.loadTexture(
                new GVRAndroidResource(mGVRContext, R.drawable.sun));

        sun = new GVRSceneObject(mGVRContext, mesh, texture);
        sun.getTransform().setRotationByAxis(90, 1, 0, 0);
        sun.getTransform().setPositionY(SUN_Y_POSITION);
        sun.getTransform().rotateByAxisWithPivot(SUN_ANGLE_POSITION, 1, 0, 0, 0, 0, 0);
        sun.getRenderData().setRenderingOrder(RenderingOrder.SUN);
        scene.addSceneObject(sun);
    }

    @Override
    public void onStep() {
        //worm.chainMove(mGVRContext);

        //GamepadInput.process();

        //GamepadInput.interactWithDPad(worm);
        //ControlSceneObjectBehavior.process(mGVRContext);

        //video.getTransform().rotateByAxis(0.5f, 0.0f, 1.0f, 0.0f);
        //gvrSphereSceneObject.getTransform().setPositionY(gvrSphereSceneObject.getTransform().getPositionY() + 0.01f);

        //gvrTextViewSceneObject.getTransform().setPosition(0.0f, gvrTextViewSceneObject.getTransform().getPositionY()+0.01f, -5.0f);
        gvrSphereSceneObject.getTransform().rotateByAxis(0.75f, 0.0f, 1.0f, 0.0f);
        gvrCubeSceneObject.getTransform().rotateByAxisWithPivot(1.0f, 0.0f, 1.0f, 0.0f, 0, gvrCylinderSceneObject.getTransform().getPositionY(), gvrCylinderSceneObject.getTransform().getPositionZ());
        gvrCylinderSceneObject.getTransform().rotateByAxis(0.75f, 0.0f, 0.0f, 1.0f);

        if(gvrCubeSceneObject.isColliding(gvrSphereSceneObject)==true){
            gvrTextViewSceneObject.setTextColor(Color.RED);
            gvrTextViewSceneObject.setText("colliding sphere");
        }
        else {
            gvrTextViewSceneObject.setTextColor(Color.BLUE);
            gvrTextViewSceneObject.setText("waiting collision");
        }

        EV.getCurrentWait();
        EV2.getCurrentWait();
        EV3.getCurrentWait();
        if(EV.getRunCount()==1)  EV.cancel();
        if(EV2.getRunCount()==1) EV2.cancel();
        if(EV3.getRunCount()==1) EV3.cancel();
        /*if(gvrCylinderSceneObject.isColliding(gvrSphereSceneObject)==true){
            gvrTextViewSceneObject.setTextColor(Color.RED);
            gvrTextViewSceneObject.setText("collinding cilynder");
        }
        else {
            gvrTextViewSceneObject.setTextColor(Color.BLUE);
            gvrTextViewSceneObject.setText("waiting collision");
        }*/


        if (gamepadObject != null) {

            gamepadObject.getGamepadVirtual().handlerAnalogL(
                    GamepadInput.getCenteredAxis(MotionEvent.AXIS_X),
                    GamepadInput.getCenteredAxis(MotionEvent.AXIS_Y),
                    0);

            gamepadObject.getGamepadVirtual().handlerAnalogR(
                    GamepadInput.getCenteredAxis(MotionEvent.AXIS_RX),
                    GamepadInput.getCenteredAxis(MotionEvent.AXIS_RY),
                    0);

            gamepadObject.getGamepadVirtual().dpadTouch(
                    GamepadInput.getCenteredAxis(MotionEvent.AXIS_HAT_X),
                    GamepadInput.getCenteredAxis(MotionEvent.AXIS_HAT_Y));

            gamepadObject.getGamepadVirtual().handlerLRButtons(
                    GamepadInput.getKey(KeyEvent.KEYCODE_BUTTON_L1),
                    GamepadInput.getKey(KeyEvent.KEYCODE_BUTTON_R1));

            gamepadObject.getGamepadVirtual().buttonsPressed(
                    GamepadInput.getKey(KeyEvent.KEYCODE_BUTTON_X),
                    GamepadInput.getKey(KeyEvent.KEYCODE_BUTTON_Y),
                    GamepadInput.getKey(KeyEvent.KEYCODE_BUTTON_A),
                    GamepadInput.getKey(KeyEvent.KEYCODE_BUTTON_B));
        }
    }

    public void animateWorm(
            org.gearvrf.viewmanager.controls.util.VRSamplesTouchPadGesturesDetector.SwipeDirection swipeDirection) {

        float duration = 0.6f;
        float movement = 0.75f;
        float degree = 22.5f;

        if (swipeDirection.name() == SwipeDirection.Up.name()) {
            worm.moveAlongCameraVector(duration, movement);
            worm.rotateWorm(MovementDirection.Up);

        } else if (swipeDirection.name() == SwipeDirection.Down.name()) {
            worm.moveAlongCameraVector(duration, -movement);
            worm.rotateWorm(MovementDirection.Down);

        } else if (swipeDirection.name() == SwipeDirection.Forward.name()) {
            worm.rotateAroundCamera(duration, -degree);
            worm.rotateWorm(MovementDirection.Right);

        } else {
            worm.rotateAroundCamera(duration, degree);
            worm.rotateWorm(MovementDirection.Left);
        }
    }

    private void createMenu() {
        mMenu = new Menu(mGVRContext);
        mMenu.getTransform().setScale(0.4f, 0.4f, 0.4f);
        mMenu.getTransform().setPosition(0, -.5f, -3f);
        mMenu.getRenderData().getMaterial().setOpacity(0.5f);
        // scene.addSceneObject(mMenu);
    }

    private void createGamepad3D() {
        GVRCameraRig cameraObject = mGVRContext.getMainScene()
                .getMainCameraRig();
        gamepadObject = new GamepadObject(mGVRContext);

        gamepadObject.getTransform().setPosition(-3, 1.f, 8f);
        float angle = Util.getYRotationAngle(gamepadObject, cameraObject);

        gamepadObject.getTransform().rotateByAxis(angle, 0, 1, 0);

        scene.addSceneObject(gamepadObject);
    }
}
