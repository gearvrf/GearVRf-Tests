package org.gearvrf.za_gearvrf;

import org.gearvrf.viewmanager.TestDefaultGVRViewManager;
import org.gearvrf.ActivityInstrumentationGVRf;

import org.gearvrf.GVRCustomMaterialShaderId;
import org.gearvrf.GVRCustomPostEffectShaderId;
import org.gearvrf.GVRMaterialShaderManager;
import org.gearvrf.GVRPostEffectShaderManager;

/**
 * Created by j.elidelson on 5/20/2015.
 */
public class GVRCustomMaterialShaderIdTest extends ActivityInstrumentationGVRf {


    public void testCustomMaterialShaderId() {

        String s1="#version 330 core\n" +
                "layout(location = 0) in vec3 vertexPosition_modelspace;\n" +
                "void main(){\n" +
                "  gl_Position.xyz = vertexPosition_modelspace;\n" +
                "  gl_Position.w = 1.0;\n" +
                "}";
        String s2="#version 330 core\n" +
                "out vec3 color;\n" +
                " \n" +
                "void main(){\n" +
                "    color = vec3(1,0,0);\n" +
                "}";

        //s1="";
        //s2="";

        GVRMaterialShaderManager gvrMaterialShaderManager = TestDefaultGVRViewManager.mGVRContext.getMaterialShaderManager();
        GVRCustomMaterialShaderId gvrCustomMaterialShaderId = gvrMaterialShaderManager.addShader(s1,s2);
        assertNotNull("Resource was null: ", gvrCustomMaterialShaderId);
    }

    public void testCustomPostEffectShaderId() {

        String s1="#version 330 core\n" +
                "layout(location = 0) in vec3 vertexPosition_modelspace;\n" +
                "void main(){\n" +
                "  gl_Position.xyz = vertexPosition_modelspace;\n" +
                "  gl_Position.w = 1.0;\n" +
                "}";
        String s2="#version 330 core\n" +
                "out vec3 color;\n" +
                " \n" +
                "void main(){\n" +
                "    color = vec3(1,0,0);\n" +
                "}";

        //s1="";
        //s2="";

        GVRPostEffectShaderManager gvrPostEffectShaderManager = TestDefaultGVRViewManager.mGVRContext.getPostEffectShaderManager();
        GVRCustomPostEffectShaderId gvrCustomPostEffectShaderId = gvrPostEffectShaderManager.addShader(s1,s2);
        assertNotNull("Resource was null: ", gvrCustomPostEffectShaderId);
    }


}
