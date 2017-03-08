package org.gearvrf;

import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.webkit.WebView;

import org.gearvrf.viewmanager.TestDefaultGVRViewManager;

public class DefaultGVRTestActivity extends GVRActivity {

    protected static DefaultGVRTestActivity mMainActivity = null;
    private ActivityInstrumentationTestCase2 activityInstrumentationGVRf;
    public static WebView webView=null;
    public static boolean sContextLoaded = false;
    public static String packagex = "";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mMainActivity = this;
        if(webView==null) createWebView();
        initGVRTestActivity();
        GVRContext x = TestDefaultGVRViewManager.mGVRContext;
      //  oneTimeInit();
    }


    private void createWebView() {
        webView = new WebView(this);
        webView.setInitialScale(100);
        webView.measure(2000, 1000);
        webView.layout(0, 0, 2000, 1000);
        //WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        /*webView.loadUrl("http://gearvrf.org");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });*/
    }

    protected void initGVRTestActivity() {
        setScript(new TestDefaultGVRViewManager(), "gvr_note4.xml");
    }

    public static DefaultGVRTestActivity getInstance() {
        return mMainActivity;
    }

    public void store(ActivityInstrumentationTestCase2 activityInstrumentationGVRf) {
        this.activityInstrumentationGVRf = activityInstrumentationGVRf;
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}