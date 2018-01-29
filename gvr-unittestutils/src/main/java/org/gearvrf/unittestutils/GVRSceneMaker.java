package org.gearvrf.unittestutils;

import android.util.ArrayMap;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
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
      r: 1, g: 1, b: 1, a: 0
     }
     */
    private static RGBAColor getColorCoordinates(JSONObject jsonObject) throws
            JSONException {

        float cordR = (float) jsonObject.optDouble("r", 0.0f);
        float cordG = (float) jsonObject.optDouble("g", 0.0f);
        float cordB = (float) jsonObject.optDouble("b", 0.0f);
        float cordA = (float) jsonObject.optDouble("a", 1.0f);

        RGBAColor coordinates = new RGBAColor(cordR, cordG, cordB, cordA);
        return coordinates;
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

    /*
     {
      shader: ("phong" | "texture" | "cube")
      color: {r: [0.0-1.0], g: [0.0-1.0], b: [0.0-1.0], a: [0.0-1.0]}
      textures:[...]
     }
     */
    private static GVRMaterial createMaterial(GVRContext gvrContext,
                                              ArrayMap<String, GVRTexture> textures,
                                              JSONObject jsonObject) throws JSONException {
        GVRMaterial material;
        String shader_type = jsonObject.optString("shader", "texture");

        if (shader_type.equals("phong")) {
            material = new GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.Phong.ID);
        } else if (shader_type.equals("cube")) {
            material = new GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.Cubemap.ID);
        } else {
            material = new GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.Texture.ID);
        }

        JSONArray jsonArray = jsonObject.optJSONArray("textures");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTexture = jsonArray.getJSONObject(i);

                String sharedId = jsonTexture.optString("shared");
                GVRTexture texture = sharedId.isEmpty() ? createTexture(gvrContext, jsonTexture) :
                        textures.get(sharedId);

                String texture_name = jsonTexture.optString("name");
                if (texture_name.isEmpty()) {
                    if (material.getShaderType() == GVRMaterial.GVRShaderType.Phong.ID) {
                        material.setTexture("diffuseTexture", texture);
                    } else {
                        material.setMainTexture(texture);
                    }
                } else {
                    material.setTexture(texture_name, texture);
                }
            }
        }

        JSONObject jsonColor = jsonObject.optJSONObject("color");
        if (jsonColor != null) {
            RGBAColor color = getColorCoordinates(jsonColor);
            if (material.getShaderType() == GVRMaterial.GVRShaderType.Texture.ID) {
                material.setColor(color.r, color.g, color.b);
            } else {
                material.setDiffuseColor(color.r, color.g, color.b, color.a);
            }
        }

        return material;
    }
}
