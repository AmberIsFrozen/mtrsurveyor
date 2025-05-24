package com.lx862.mtrsurveyor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class Config {
    private static final Path configDirectory = FabricLoader.getInstance().getConfigDir().resolve("mtrsurveyor.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Config instance;

    public boolean enableAutoSync = true;
    public boolean showStationWithNoRoute = false;
    public boolean showHiddenRoute = false;

    public boolean addStationLandmarks = true;
    public boolean addDepotLandmarks = false;

    public static void load() {
        try(FileReader fileReader = new FileReader(configDirectory.toFile())) {
            instance = gson.fromJson(fileReader, Config.class);
        } catch (FileNotFoundException e) {
            try {
                write(new Config());
                load();
            } catch (IOException ioe) {
                MTRSurveyor.LOGGER.error("Failed to write config file!", ioe);
                instance = new Config();
            }
        } catch (IOException e) {
            MTRSurveyor.LOGGER.error("", e);
        }
    }

    public static void write(Config instance) throws IOException {
        try(FileWriter fileWriter = new FileWriter(configDirectory.toFile())) {
            gson.toJson(instance, fileWriter);
        }
    }

    public static Config getInstance() {
        if(instance == null) throw new IllegalStateException("Config instance is null!");
        return instance;
    }
}
