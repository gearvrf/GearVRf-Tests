package org.gearvrf.unittestutils;

import org.json.JSONArray;
import org.json.JSONException;

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
}
