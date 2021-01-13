package com.ip360323.asteroids;

import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MeshManager {
    private final HashMap<String, OBJMesh> meshes = new HashMap<>();

    private static class StaticHolder {
        static final MeshManager INSTANCE = new MeshManager();
    }

    public static MeshManager getInstance() {
        return StaticHolder.INSTANCE;
    }

    private MeshManager()
    {
    }

    public OBJMesh LoadMesh(Resources res, int resID, Shader shader)
    {
        String key = "" + resID + " " + shader.handle;
        if(!meshes.containsKey(key))
        {
            meshes.put(key, LoadOBJ(res, resID, shader));
        }
        return meshes.get(key);
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    ///Terribly slow implementation of a OBJ loader
    //Most performance problems comes down to not finding a good way to parse the file
    //Java.Scanner seems buggy or the documentation is not clear enough (does not use /r/n as delimiter)

    //loads some OBJ meshes
    //All meshes are expected to have Position, UV and normal
    //All faces are expected to be triangles
    //Specific format required
    //To generate a consistent OBJ file
    //Import model to Unity //will generate normals if they dont already exist
    //Export model from Unity using script found @ https://wiki.unity3d.com/index.php/ObjExporter
    //^ensures the obj format is compatible with this loader
    private OBJMesh LoadOBJ(Resources res, int resID, Shader shader) {
        ArrayList<Integer> vertexIndices= new ArrayList<>();
        ArrayList<Integer> uvIndices= new ArrayList<>();
        ArrayList<Integer> normalIndices= new ArrayList<>();
        ArrayList<Point3f> tempVerts = new ArrayList<>();
        ArrayList<Point2f> tempUVs= new ArrayList<>();
        ArrayList<Point3f> tempNorms= new ArrayList<>();
        float point[] = new float[3];
        float uv[] = new float[2];
        InputStream is = res.openRawResource(resID);
        String input = convertStreamToString(is);

        ArrayList<String> lines = Split(input, "\n");
        ArrayList<String> splitLine;
        int index = 0;
        while(index < lines.size())
        {
            String nextLine = lines.get(index);

            splitLine = Split(nextLine, " ");

            String cmd = "";
            if(splitLine.size() > 0)
            {
                cmd = splitLine.get(0);
            }


            switch (cmd) {
                case "v": {
                    int dim = 0;
                    while (dim < 3) {
                        point[dim] = Float.parseFloat(splitLine.get(dim + 1));
                        dim++;
                    }
                    tempVerts.add(new Point3f(point));
                    break;
                }
                case "vt": {
                    int dim = 0;
                    while ((dim < 2)) {
                        uv[dim] = Float.parseFloat(splitLine.get(dim + 1));
                        dim++;
                    }
                    tempUVs.add(new Point2f(uv));
                    break;
                }
                case "vn": {
                    int dim = 0;
                    while ((dim < 3)) {
                        point[dim] = Float.parseFloat(splitLine.get(dim + 1));
                        dim++;
                    }
                    Point3f norm = new Point3f(point);
                    norm.Normalize();
                    tempNorms.add(norm);
                    break;
                }
                case "f": {
                    String faceData = "";
                    int vertexIndex, uvIndex, normalIndex;
                    int dim = 0;
                    while (dim < 3) {
                        faceData = splitLine.get(dim + 1);
                        ArrayList<String> data = Split(faceData, "/");
                        Utils.require(data.size() < 4, "OBJ file has non-triangle faces");
                        if (data.size() > 0) {
                            final String i = data.get(0);
                            vertexIndex = Integer.parseInt(i);
                            vertexIndices.add(vertexIndex);
                        }
                        if (data.size() > 1) {
                            uvIndex = Integer.parseInt(data.get(1));
                            uvIndices.add(uvIndex);
                        }
                        if (data.size() > 2) {
                            normalIndex = Integer.parseInt(data.get(2));
                            normalIndices.add(normalIndex);
                        }

                        dim++;
                    }
                    break;
                }
            }
            index++;
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatBuffer floatBuffer = FloatBuffer.allocate(vertexIndices.size() * 3);
        FloatBuffer uvs = FloatBuffer.allocate(vertexIndices.size() * 2);
        FloatBuffer normals = FloatBuffer.allocate(vertexIndices.size() * 3);

        for(int i = 0; i < vertexIndices.size(); i++) {
            int vertIndex = vertexIndices.get(i) - 1;
            int uvIndex = uvIndices.get(i) - 1;
            int normalIndex = 0;
            if (normalIndices.size() > i) {
                normalIndex = normalIndices.get(i) - 1;
            }

            Point3f pos;
            Point3f normal;
            Point2f uvP;

            if (tempVerts.size() > vertIndex) {
                pos = tempVerts.get(vertIndex);
                floatBuffer.put(pos.x);
                floatBuffer.put(pos.y);
                floatBuffer.put(pos.z);
            }

            if (tempUVs.size() > uvIndex) {
                uvP = tempUVs.get(uvIndex);
                uvs.put(uvP.x);
                uvs.put(uvP.y);
            }
            if (tempNorms.size() > normalIndex) {
                normal = tempNorms.get(normalIndex);
                normals.put(normal.x);
                normals.put(normal.y);
                normals.put(normal.z);
            }
        }
        OBJMesh objMesh = new OBJMesh(shader);
        objMesh.updateBounds(floatBuffer);
        objMesh.normalize(floatBuffer);
        objMesh.InitBuffers(floatBuffer, uvs, normals);
        return objMesh;
    }

    private ArrayList<String> Split(String input, String delimiter)
    {
        ArrayList<String> result = new ArrayList<>();
        while(true)
        {
            int pos = input.indexOf(delimiter);
            if (pos == -1)
            {
                result.add(input);
                break;
            }
            String subStr = input.substring(0, pos);
            if(!subStr.equals(""))
            {
                result.add(subStr);
            }

            final int length = input.length();
            input = input.substring(pos+1, length);
        }
        return result;
    }

    public void Clear()
    {
        Iterator it = meshes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ((OBJMesh)pair.getValue()).Delete();
            it.remove();
        }
    }

}
