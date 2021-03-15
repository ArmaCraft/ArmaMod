package org.armacraft.mod.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.armacraft.mod.module.IModule;
import org.armacraft.mod.module.ModuleState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModuleController {
    private Map<IModule, ModuleState> modules = new HashMap<>();

    public void loadModules() {
        modules.entrySet().stream()
                .filter(entry -> entry.getValue() == ModuleState.ENABLED)
                .forEach(entry -> entry.getKey().load());
    }

    public <T extends IModule> void register(Class<T> clazz, ModuleState initialState) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/modules.json"))) {
            IModule module = clazz.newInstance();
            Gson gson = new Gson();
            HashMap<String, String> json = gson.fromJson(bufferedReader, HashMap.class);
            if(!json.containsKey(module.getId())) {
                json.put(module.getId(), String.valueOf(initialState.getId()));
                gson.toJson(json);
            } else {
                initialState = ModuleState.by(Integer.parseInt(json.get(module.getId())));
            }
            modules.put(module, initialState);
        } catch (IOException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

    }

}
