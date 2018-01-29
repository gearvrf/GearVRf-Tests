package org.gearvrf.unittestutils;

import android.util.ArrayMap;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
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

    private static void setScale(GVRTransform transform, JSONObject jsonScale)
            throws JSONException {

        float x = (float) jsonScale.optDouble("x", 1.0f);
        float y = (float) jsonScale.optDouble("y", 1.0f);
        float z = (float) jsonScale.optDouble("z", 1.0f);

        transform.setScale(x, y, z);
    }

    private static void setPosition(GVRTransform transform, JSONObject jsonPosition)
            throws JSONException {

        float x = (float) jsonPosition.optDouble("x", 0.0f);
        float y = (float) jsonPosition.optDouble("y", 0.0f);
        float z = (float) jsonPosition.optDouble("z", 0.0f);

        transform.setPosition(x, y, z);
    }

    private static void setRotation(GVRTransform transform, JSONObject jsonRotation)
            throws JSONException {

        float x = (float) jsonRotation.optDouble("x", 0.0f);
        float y = (float) jsonRotation.optDouble("y", 0.0f);
        float z = (float) jsonRotation.optDouble("z", 0.0f);
        float w = (float) jsonRotation.optDouble("w", 0.0f);

        transform.setRotation(w, x, y, z);
    }

    /*
     {
      position: {x: 0, y: 0, z: 0}
      scale: {x: 0, y: 0, z: 0}
      rotation: {w: 0, x: 0, y: 0, z: 0}
     }
     */
    private static void setTransform(GVRTransform transform, JSONObject jsonObject) throws JSONException {

        JSONObject jsonPosition = jsonObject.optJSONObject("position");
        if (jsonPosition != null) {
            setPosition(transform, jsonPosition);
        }

        JSONObject jsonRotation = jsonObject.optJSONObject("rotation");
        if (jsonRotation != null) {
            setRotation(transform, jsonRotation);
        }

        JSONObject jsonScale = jsonObject.optJSONObject("scale");
        if (jsonScale != null) {
            setScale(transform, jsonScale);
        }
    }

    private static void setPointLightIntensity(GVRPointLight light, JSONObject jsonLight)
            throws JSONException {

        JSONObject jsonAmbientIntensity = jsonLight.optJSONObject("ambientintensity");
        if (jsonAmbientIntensity != null) {
            RGBAColor ambientCoord = getColorCoordinates(jsonAmbientIntensity);
            light.setAmbientIntensity(ambientCoord.r, ambientCoord.g, ambientCoord.b,
                    ambientCoord.a);
        }

        JSONObject jsonDiffuseIntensity = jsonLight.optJSONObject("diffuseintensity");
        if (jsonDiffuseIntensity != null) {
            RGBAColor diffuseCoord = getColorCoordinates(jsonDiffuseIntensity);
            light.setDiffuseIntensity(diffuseCoord.r, diffuseCoord.g, diffuseCoord.b,
                    diffuseCoord.a);
        }

        JSONObject jsonSpecularIntensity = jsonLight.optJSONObject("specularintensity");
        if (jsonSpecularIntensity != null) {
            RGBAColor specularCoord = getColorCoordinates(jsonSpecularIntensity);
            light.setSpecularIntensity(specularCoord.r, specularCoord.g, specularCoord.b,
                    specularCoord.a);
        }
    }

    private static void setDirectLightIntensity(GVRDirectLight light, JSONObject jsonLight)
            throws JSONException {

        JSONObject jsonAmbientIntensity = jsonLight.optJSONObject("ambientintensity");
        if (jsonAmbientIntensity != null) {
            RGBAColor ambientCoord = getColorCoordinates(jsonAmbientIntensity);
            light.setAmbientIntensity(ambientCoord.r, ambientCoord.g, ambientCoord.b,
                    ambientCoord.a);
        }

        JSONObject jsonDiffuseIntensity = jsonLight.optJSONObject("diffuseintensity");
        if (jsonDiffuseIntensity != null) {
            RGBAColor diffuseCoord = getColorCoordinates(jsonDiffuseIntensity);
            light.setDiffuseIntensity(diffuseCoord.r, diffuseCoord.g, diffuseCoord.b,
                    diffuseCoord.a);
        }

        JSONObject jsonSpecularIntensity = jsonLight.optJSONObject("specularintensity");
        if (jsonSpecularIntensity != null) {
            RGBAColor specularCoord = getColorCoordinates(jsonSpecularIntensity);
            light.setSpecularIntensity(specularCoord.r, specularCoord.g, specularCoord.b,
                    specularCoord.a);
        }
    }

    private static void setLightConeAngle(GVRSpotLight light, JSONObject jsonLight)
            throws JSONException {

        float innerAngle = (float) jsonLight.optDouble("innerconeangle");
        if (!Double.isNaN(innerAngle)) {
            light.setInnerConeAngle(innerAngle);
        }

        float outAngle = (float) jsonLight.optDouble("outerconeangle");
        if (!Double.isNaN(outAngle)) {
            light.setOuterConeAngle(outAngle);
        }
    }

    private static GVRSceneObject createSpotLight(GVRContext gvrContext, JSONObject jsonLight)
            throws JSONException {

        GVRSceneObject lightObj = new GVRSceneObject(gvrContext);
        GVRSpotLight spotLight = new GVRSpotLight(gvrContext);
        setPointLightIntensity(spotLight, jsonLight);
        setLightConeAngle(spotLight, jsonLight);
        lightObj.attachLight(spotLight);

        return lightObj;
    }

    private static GVRSceneObject createDirectLight(GVRContext gvrContext, JSONObject jsonLight)
            throws JSONException {

        GVRSceneObject lightObj = new GVRSceneObject(gvrContext);
        lightObj.setName("lightNode");
        GVRDirectLight directLight = new GVRDirectLight(gvrContext);
        setDirectLightIntensity(directLight, jsonLight);
        lightObj.attachLight(directLight);

        return lightObj;
    }

    private static GVRSceneObject createPointLight(GVRContext gvrContext, JSONObject jsonLight)
            throws JSONException {

        GVRSceneObject lightObj = new GVRSceneObject(gvrContext);
        GVRPointLight pointLight = new GVRPointLight(gvrContext);
        setPointLightIntensity(pointLight, jsonLight);
        lightObj.attachLight(pointLight);

        return lightObj;
    }

    /*
     {
      type: ("spot" | "directional" | "point")
      castshadow: ("true" | "false")
      position: {x: 0, y: 0, z: 0}
      rotation: {w: 0, x: 0, y: 0, z: 0}
      ambientintensity: {r: [0.0-1.0], g: [0.0-1.0], b: [0.0-1.0], a: [0.0-1.0]}
      diffuseintensity:  {r: [0.0-1.0], g: [0.0-1.0], b: [0.0-1.0], a: [0.0-1.0]}
      specularintensity:  {r: [0.0-1.0], g: [0.0-1.0], b: [0.0-1.0], a: [0.0-1.0]}
      innerconeangle: [0.0-9.0]+
      outerconeangle: [0.0-9.0]+
     }
     */
    private static GVRSceneObject createLight(GVRContext gvrContext, JSONObject jsonLight) throws
            JSONException {

        GVRSceneObject light = null;
        String type = jsonLight.optString("type");

        if (type.equals("spot")) {
            light = createSpotLight(gvrContext, jsonLight);
        } else if (type.equals("directional")) {
            light = createDirectLight(gvrContext, jsonLight);
        } else if (type.equals("point")) {
            light = createPointLight(gvrContext, jsonLight);
        }

        if (light != null) {
            light.getLight().setCastShadow(jsonLight.optBoolean("castshadow"));
            setTransform(light.getTransform(), jsonLight);
        }

        return light;
    }

    /*
     {
      vertices: [0, ... n]
      normals: [0, ... n]
      texcoords: [[0, ... n], [0, ... n]]
      triangles: [0, ... n]
     }
     */
    private static GVRMesh createPolygonMesh(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        String descriptor = "float3 a_position";

        if (jsonObject.has( "texcoords")) {
            descriptor += " float2 a_texcoord";
        }

        if (jsonObject.has("normals")) {
            descriptor += " float3 a_normal";
        }

        GVRMesh mesh = new GVRMesh(gvrContext, descriptor);

        if (jsonObject.has("vertices")) {
            mesh.setVertices(jsonToFloatArray(jsonObject.optJSONArray("vertices")));
        }

        if (jsonObject.has("normals")) {
            mesh.setNormals(jsonToFloatArray(jsonObject.optJSONArray("normals")));
        }

        if (jsonObject.has("triangles")) {
            mesh.setTriangles(jsonToIntArray(jsonObject.optJSONArray("triangles")));
        }

        if (jsonObject.has("texcoords")) {
            JSONArray jsonCorrds = jsonObject.optJSONArray("texcoords");
            if (jsonCorrds != null) {
                for (int i = 0; i < jsonCorrds.length(); i++) {
                    mesh.setTexCoords(jsonToFloatArray(jsonCorrds.optJSONArray(i)), i);
                }
            }
        }

        return mesh;
    }

    private static GVRSceneObject createQuad(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        float width = 1.0f;
        float height = 1.0f;
        String descriptor = "float3 a_position float2 a_texcoord float3 a_normal";

        if (jsonObject != null) {
            width = (float) jsonObject.optDouble("width", width);
            height = (float) jsonObject.optDouble("height", height);
            descriptor = jsonObject.optString("descriptor", descriptor);
        }

        return new GVRSceneObject(gvrContext,
                GVRMesh.createQuad(gvrContext, descriptor, width, height));
    }

    private static GVRSceneObject createPolygon(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        return new GVRSceneObject(gvrContext, createPolygonMesh(gvrContext, jsonObject));
    }

    private static GVRSceneObject createCube(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        String descriptor = "float3 a_position float2 a_texcoord float3 a_normal";

        float width = (float) jsonObject.optDouble("width", 1.0f);
        float height = (float) jsonObject.optDouble("height", 1.0f);
        float depth = (float) jsonObject.optDouble("depth", 1.0f);
        boolean facing_out = jsonObject.optBoolean("facing_out", true);
        descriptor = jsonObject.optString("descriptor", descriptor);

        GVRMesh mesh = GVRCubeSceneObject.createCube(gvrContext, descriptor, facing_out,
                new org.joml.Vector3f(width, height, depth));

        return new GVRSceneObject(gvrContext, mesh);
    }

    private static GVRSceneObject createCylinder(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        return null;
    }

    private static GVRSceneObject createSphere(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        float radius = (float) jsonObject.optDouble("radius", 1.0f);
        boolean facing_out = jsonObject.optBoolean("facing_out", true);

        return new GVRSphereSceneObject(gvrContext, facing_out, radius);
    }

    /*
     {
      type: ("quad" | "cylinder" | "sphere" | "cube" | "polygon")
      width: [0.0-9.0]+
      height: [0.0-9.0]+
      depth: [0.0-9.0]+
      radius: [0.0-9.0]+
      vertices: [[0.0-9.0]+, ...]
      normals: [[0.0-9.0]+, ...]
      texcoords: [[0.0-9.0]+, ...]
      triangles:  [[0-9]+, ...]
      bone_weights:  [[0.0-9.0]+, ...]
      bone_indices:  [[0-9]+, ...]
     }
     */
    private static GVRSceneObject createGeometry(GVRContext gvrContext, JSONObject jsonObject)
            throws JSONException {
        GVRSceneObject sceneObject = null;

        String type = jsonObject.optString("type");

        if (type.equals("polygon")) {
            sceneObject = createPolygon(gvrContext, jsonObject);
        } else if (type.equals("cube")) {
            sceneObject = createCube(gvrContext, jsonObject);
            sceneObject.setName("cubeSceneObj");
        } else if (type.equals("cylinder")) {
            sceneObject = createCylinder(gvrContext, jsonObject);
        } else if (type.equals("sphere")) {
            sceneObject = createSphere(gvrContext, jsonObject);
        } else {
            sceneObject = createQuad(gvrContext, jsonObject);
        }

        return sceneObject;
    }
}
