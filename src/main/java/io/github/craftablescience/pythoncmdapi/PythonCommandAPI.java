package io.github.craftablescience.pythoncmdapi;


import io.github.craftablescience.pythoncmdapi.server.command.RunPythonFileCommand;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PythonCommandAPI implements ModInitializer {

    public static final String MOD_ID;
    public static final Logger LOGGER;
    public static final Path PYTHON_DIR;
    public static final PythonInterpreter PY_INTERP;

    static {
        MOD_ID = "pythoncmdapi";
        LOGGER = LogManager.getLogger("pythoncmdapi");
        PYTHON_DIR = Paths.get(System.getProperty("user.dir") + File.separator + "python");
        PY_INTERP = new PythonInterpreter();
    }

    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("[PYTHON-CMD-API] Begun initialization...");

        try {
            Files.createDirectories(PYTHON_DIR);
            LOGGER.info("[PYTHON-CMD-API] Found python directory at \"" + PYTHON_DIR.toString() + "\"");
        } catch (IOException e) {
            LOGGER.error("[PYTHON-CMD-API] ERROR LOADING PYTHON DIR: " + e.getMessage());
        }

        RunPythonFileCommand.register();

        LOGGER.info("[PYTHON-CMD-API] Initialization complete in " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}