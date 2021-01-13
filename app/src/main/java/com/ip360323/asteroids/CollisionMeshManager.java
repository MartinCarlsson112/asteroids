package com.ip360323.asteroids;

import java.util.HashMap;

public class CollisionMeshManager {
    private final HashMap<String, CollisionMesh> meshes = new HashMap<>();

    private static class StaticHolder {
        static final CollisionMeshManager INSTANCE = new CollisionMeshManager();
    }

    public static CollisionMeshManager getInstance() {
        return CollisionMeshManager.StaticHolder.INSTANCE;
    }

    private CollisionMeshManager()
    {
    }

    public CollisionMesh GetMesh(float width, float height, float depth)
    {
        String key = "w: " + width + "h: " + height + "d: " + depth;

        if(!meshes.containsKey(key))
        {
            meshes.put(key, new CollisionMesh(width, height, depth));
        }
        return meshes.get(key);
    }


    public CollisionMesh GetMesh(float radius)
    {
        String key = "radius: " + radius;

        if(!meshes.containsKey(key))
        {
            meshes.put(key, new CollisionMesh(radius));
        }
        return meshes.get(key);
    }



}
