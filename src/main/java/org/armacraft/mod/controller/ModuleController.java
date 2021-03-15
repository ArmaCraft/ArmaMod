package org.armacraft.mod.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.module.IModule;
import org.armacraft.mod.module.ModuleSide;
import org.armacraft.mod.module.ModuleState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleController {
    private Map<IModule, ModuleState> modules = new HashMap<>();

    public void loadModules() {
        Map<IModule, ModuleState> enabledModules = modules.entrySet().stream()
                .filter(entry -> entry.getValue() == ModuleState.ENABLED)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //LOAD SERVER SIDE MODULES
        enabledModules.keySet().stream()
                .filter(moduleState -> moduleState.getSide() == ModuleSide.SERVER)
                .forEach(module -> DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> module::load));

        //LOAD CLIENT SIDE MODULES
        enabledModules.keySet().stream()
                .filter(moduleState -> moduleState.getSide() == ModuleSide.CLIENT)
                .forEach(module -> DistExecutor.runWhenOn(Dist.CLIENT, () -> module::load));

        //LOAD BOTH SIDE MODULES
        enabledModules.keySet().stream()
                .filter(moduleState -> moduleState.getSide() == ModuleSide.BOTH).forEach(IModule::load);
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
