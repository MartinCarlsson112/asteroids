package com.ip360323.asteroids;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TextureManager {

    private final HashMap<String, Texture2D> textures = new HashMap<>();

    private static class StaticHolder {
        static final TextureManager INSTANCE = new TextureManager();
    }

    public static TextureManager getInstance() {
        return TextureManager.StaticHolder.INSTANCE;
    }

    private TextureManager()
    {

    }

    public int GetTextureHandle(Resources res, int resID)
    {
        String key = "texture: " + resID;
        if(!textures.containsKey(key))
        {
            Texture2D texture = new Texture2D();
            texture.LoadTexture(res, resID);
            textures.put(key, texture);
        }
        return textures.get(key).textureHandle;
    }


    public void Clear()
    {
        Iterator it = textures.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ((Texture2D)pair.getValue()).Free();
            it.remove();
        }
    }
}
