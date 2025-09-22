package com.example.walkwise.model;

import android.content.Context;
import android.content.SharedPreferences;

public class ModelVersionManager {
    private static ModelVersionManager instance;
    private SharedPreferences prefs;
    private static final String MODEL_VERSION_KEY = "ModelVersion";

    private ModelVersionManager(Context context) {
        prefs = context.getSharedPreferences("ModelPreferences", Context.MODE_PRIVATE);
    }

    public static synchronized ModelVersionManager getInstance(Context context) {
        if (instance == null) {
            instance = new ModelVersionManager(context.getApplicationContext());
        }
        return instance;
    }

    public int getModelVersion() {
        return prefs.getInt(MODEL_VERSION_KEY, 0); // Return 0 if not set
    }

    public void setModelVersion(int modelVersion) {
        prefs.edit().putInt(MODEL_VERSION_KEY, modelVersion).apply();
    }
}


