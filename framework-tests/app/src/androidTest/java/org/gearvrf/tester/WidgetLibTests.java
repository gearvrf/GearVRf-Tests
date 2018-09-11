package org.gearvrf.tester;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAtlasInformation;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;

import org.gearvrf.utility.RuntimeAssertion;
import org.gearvrf.widgetlib.content_scene.ContentSceneController;
import org.gearvrf.widgetlib.log.Log;
import org.gearvrf.widgetlib.main.GVRBitmapTexture;
import org.gearvrf.widgetlib.main.MainScene;
import org.gearvrf.widgetlib.main.WidgetLib;
import org.gearvrf.widgetlib.widget.GroupWidget;
import org.gearvrf.widgetlib.widget.Widget;
import org.gearvrf.widgetlib.widget.layout.Layout;
import org.gearvrf.widgetlib.widget.layout.OrientedLayout;
import org.gearvrf.widgetlib.widget.layout.basic.GridLayout;
import org.gearvrf.widgetlib.widget.layout.basic.LinearLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.gearvrf.utility.Log.tag;

@RunWith(AndroidJUnit4.class)

public class WidgetLibTests {
    private static final String TAG = tag(WidgetLibTests.class);
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private WidgetLib mWidgetLib;
    private ContentSceneController mContentSceneController;
    private MainScene mMainScene;
    private boolean mDoCompare = true;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<>(GVRTestableActivity.class);

    public WidgetLibTests() {
        super();
    }
    @After
    public void tearDown() {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null) {
            scene.clear();
        }
        if (WidgetLib.isInitialized()) {
            WidgetLib.destroy();
        }
    }

    @Before
    public void setUp() throws TimeoutException
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();

        GVRContext ctx  = mTestUtils.getGvrContext();

        try {
            mWidgetLib = WidgetLib.init(ctx, "app_metadata.json");
            Log.init(ctx.getContext(), true);
            Log.enableSubsystem(Log.SUBSYSTEM.INPUT, true);
            Log.d(TAG, "WidgetLib is initialized!");
        } catch (Exception e) {
            Log.e(TAG, "WidgetLib cannot be initialized!");
            e.printStackTrace();
        }

        mWaiter.assertNotNull(mWidgetLib);

        mContentSceneController = WidgetLib.getContentSceneController();

        mMainScene = WidgetLib.getMainScene();
        mMainScene.adjustClippingDistanceForAllCameras();
        mMainScene.addSceneObject(new BackgroundWidget(ctx));
    }

    class BackgroundWidget extends Widget {
        BackgroundWidget(GVRContext gvrContext) {
            super(gvrContext);
            setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND - 1);
            setCullFace(GVRRenderPass.GVRCullFaceEnum.None);
        }
    }

    static class SimpleQuad extends Widget {
        static Map<Integer, GVRTexture> textures = new HashMap<>();

        SimpleQuad(GVRContext gvrContext) {
            super(gvrContext);
        }

        void setSimpleColor(int color) {
            GVRTexture t = textures.get(color);
            if (t == null) {
                t = getSolidColorTexture(getGVRContext(), color);
                Log.d(TAG, "Texture is not found for color %d", color);
            } else {
                Log.d(TAG, "Texture is found for color %d", color);
            }
            setTexture(t);
        }

        private GVRTexture getSolidColorTexture(GVRContext gvrContext, int color) {
            GVRTexture texture = new ImmutableBitmapTexture(gvrContext, makeSolidColorBitmap(color));
            Log.d(TAG, "getSolidColorTexture(): caching texture for 0x%08X", new Object[]{color});
            return texture;
        }

        private static Bitmap makeSolidColorBitmap(int color) {
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            if (color != -1) {
                bitmap.eraseColor(color);
            }

            return bitmap;
        }

        private static class ImmutableBitmapTexture extends GVRBitmapTexture {
            public ImmutableBitmapTexture(GVRContext gvrContext, Bitmap bitmap) {
                super(gvrContext, bitmap);
            }

            public void setAtlasInformation(List<GVRAtlasInformation> atlasInformation) {
                this.onMutatingCall("setAtlasInformation");
            }

            public void updateTextureParameters(GVRTextureParameters textureParameters) {
                this.onMutatingCall("updateTextureParameters");
            }

            private void onMutatingCall(String method) {
                throw new RuntimeAssertion("%s(): mutating call on ImmutableBitmapTexture!", new Object[]{method});
            }
        }

    }

    @Test
    public void simpleContentScene() throws TimeoutException
    {
        mContentSceneController.goTo(new BaseContentScene(mTestUtils.getGvrContext()) {
            protected Widget createContent() {
                SimpleQuad c = new SimpleQuad(mGvrContext);
                c.setSimpleColor(Color.RED);
                return c;
            }

        });
        mTestUtils.waitForXFrames(5);
        mTestUtils.screenShot(getClass().getSimpleName(), "simpleContentScene", mWaiter, mDoCompare);
    }

    @Test
    public void simpleList() throws TimeoutException
    {
        mContentSceneController.goTo(new BaseContentScene(mTestUtils.getGvrContext()) {
            protected Widget createContent() {
                Log.init(mGvrContext.getContext(), true);
                GroupWidget c = new GroupWidget(mGvrContext, 0, 0);

                LinearLayout mainLayout = new LinearLayout();
                mainLayout.setOrientation(OrientedLayout.Orientation.VERTICAL);
                mainLayout.setDividerPadding(0.5f, Layout.Axis.Y);

                c.applyLayout(mainLayout);

                SimpleQuad  quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.RED);
                c.addChild(quad);

                quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.YELLOW);
                c.addChild(quad);

                quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.GREEN);
                c.addChild(quad);

                return c;
            }

        });
        mTestUtils.waitForXFrames(5);
        mTestUtils.screenShot(getClass().getSimpleName(), "simpleList", mWaiter, mDoCompare);
    }

    @Test
    public void simpleGrid() throws TimeoutException
    {
        mContentSceneController.goTo(new BaseContentScene(mTestUtils.getGvrContext()) {
            protected Widget createContent() {
                Log.init(mGvrContext.getContext(), true);
                GroupWidget c = new GroupWidget(mGvrContext, 0, 0);

                GridLayout mainLayout = new GridLayout(2, 2);
                mainLayout.setOrientation(OrientedLayout.Orientation.VERTICAL);
                mainLayout.setDividerPadding(0.5f, Layout.Axis.X);
                mainLayout.setDividerPadding(0.5f, Layout.Axis.Y);
                mainLayout.setVerticalGravity(LinearLayout.Gravity.TOP);
                mainLayout.setHorizontalGravity(LinearLayout.Gravity.LEFT);
                mainLayout.enableClipping(true);
                mainLayout.enableOuterPadding(true);

                c.setViewPortWidth(5.5f);
                c.setViewPortHeight(5.5f);
                c.applyLayout(mainLayout);

                SimpleQuad  quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.RED);
                c.addChild(quad);

                quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.YELLOW);
                c.addChild(quad);

                quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.GREEN);
                c.addChild(quad);

                quad = new SimpleQuad(mGvrContext);
                quad.setSimpleColor(Color.BLUE);
                c.addChild(quad);

                return c;
            }

        });
        mTestUtils.waitForXFrames(10);
        mTestUtils.screenShot(getClass().getSimpleName(), "simpleGrid", mWaiter, mDoCompare);
    }

    abstract class BaseContentScene implements ContentSceneController.ContentScene {
        public BaseContentScene(GVRContext gvrContext) {
            mGvrContext = gvrContext;

            mMainWidget = new GroupWidget(gvrContext, 0, 0);
            mMainWidget.setPositionZ(-5);
            mMainWidget.setName("MainWidget < " + TAG + " >");
        }

        abstract protected Widget createContent();

        protected void setContentWidget(Widget content) {
            if (mContent != null) {
                mMainWidget.removeChild(mContent);
            }

            mContent = content;
            if (mContent != null) {
                mMainWidget.addChild(mContent, 0);
            }
        }

        @Override
        public String getName() {
            return TAG;
        }

        @Override
        public void show() {
            if (mFirstShow) {
                setContentWidget(createContent());
                mFirstShow = false;
            }

            mIsShowing = true;
            mMainScene.addSceneObject(mMainWidget);
        }

        @Override
        public void hide() {
            mMainScene.removeSceneObject(mMainWidget);
            mIsShowing = false;
        }

        protected boolean isShowing() {
            return  mIsShowing;
        }

        @Override
        public void onSystemDialogRemoved() {

        }

        @Override
        public void onSystemDialogPosted() {

        }

        @Override
        public void onProximityChange(boolean b) {

        }

        private boolean mIsShowing = false;
        protected final GVRContext mGvrContext;
        private Widget mContent;
        protected GroupWidget mMainWidget;
        protected boolean mFirstShow = true;

        private final String TAG = tag(BaseContentScene.class);
    }
}