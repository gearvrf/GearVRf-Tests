package org.gearvrf.unittestutils;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRTexture;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GVRSceneMaker {
    private static class RGBAColor {
        public final float r;
        public final float g;
        public final float b;
        public final float a;

        public RGBAColor(float r, float g, float b, float a){
            this. r = r;
            this. g = g;
            this. b = b;
            this. a = a;
        }
    }

    private static float[] jsonToFloatArray(JSONArray jsonArray) throws JSONException {
        float[] array = new float[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = (float) jsonArray.getDouble(i);
        }

        return array;
    }

    private static int[] jsonToIntArray(JSONArray jsonArray) throws JSONException {
        int[] array = new int[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getInt(i);
        }

        return array;
    }

    private static GVRTexture createBitmapTexture(GVRContext gvrContext, JSONObject jsonTexture) throws JSONException {

        int resourceId = jsonTexture.optInt("resource_id", -1);
        if (resourceId == -1) {
            return null;
        }

        return gvrContext.getAssetLoader().loadTexture(
                new GVRAndroidResource(gvrContext, resourceId));
    }

    /*
     {
      id: "texture id"
      name: ("u_texture" | "diffuseTexture")
      type: ("bitmap", "cube", "compressed")
      resourceid: [0-9]+
     }
     */
    private static GVRTexture createTexture(GVRContext gvrContext, JSONObject jsonTexture) throws JSONException {
        GVRTexture texture = null;

        String type = jsonTexture.optString("type");
        if (type.equals("compressed")) {
        } else if (type.equals("cube")) {
        } else {
            // type.equals("bitmap") || type.isEmpty()
            texture = createBitmapTexture(gvrContext, jsonTexture);
        }

        return texture;
    }
}
