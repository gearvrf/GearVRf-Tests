package org.gearvrf.tester;

import android.content.Context;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRShaderTemplate;
import org.gearvrf.utility.TextFile;

public class VertexColorShader extends GVRShaderTemplate
{
    private static String fragTemplate = null;
    private static String vtxTemplate = null;

    public VertexColorShader(GVRContext gvrcontext)
    {
        super("", 300);
        Context context = gvrcontext.getContext();
        fragTemplate = TextFile.readTextFile(context, R.raw.fragmentshader);
        vtxTemplate = TextFile.readTextFile(context, R.raw.vertexshader);
        setSegment("FragmentTemplate", fragTemplate);
        setSegment("VertexTemplate", vtxTemplate);
    }
}
