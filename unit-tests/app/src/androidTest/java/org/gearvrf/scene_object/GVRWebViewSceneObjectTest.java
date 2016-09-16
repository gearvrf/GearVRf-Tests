package org.gearvrf.scene_object;

import android.content.Context;
import android.view.Surface;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.gearvrf.DefaultGVRTestActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRTestActivity;
import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.scene_objects.GVRWebViewSceneObject;

/**
 * Created by Douglas on 2/27/15.
 */
public class GVRWebViewSceneObjectTest extends ActivityInstrumentationGVRf {

    public GVRWebViewSceneObjectTest() {
        super(GVRTestActivity.class);
    }

    public void testCreateGVRWebViewSceneObject() {

      GVRWebViewSceneObject gvrWebViewSceneObject = createWebViewObject(TestDefaultGVRViewManager.mGVRContext);
      assertNotNull(gvrWebViewSceneObject);
      try {
          for (int i = 1; i < 35; i++) gvrWebViewSceneObject.onDrawFrame(0.1f);
      }
      catch (IllegalStateException e){}
      catch (Surface.OutOfResourcesException t){}
    }

    private GVRWebViewSceneObject createWebViewObject(GVRContext gvrContext) {
        //WebView webView = getActivity().getWebView();
        GVRWebViewSceneObject webObject = new GVRWebViewSceneObject(gvrContext,
                8.0f, 4.0f, DefaultGVRTestActivity.webView);
        //webObject.setName("web view object");
        //webObject.getRenderData().getMaterial().setOpacity(1.0f);
        //webObject.getTransform().setPosition(0.0f, 0.0f, -4.0f);

        return webObject;
    }

}
