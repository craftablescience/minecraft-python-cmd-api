package io.github.craftablescience.pythoncmdapi;


import net.fabricmc.api.ModInitializer;


public class PythonCommandAPI implements ModInitializer {

    public static final String MOD_ID;

    static {
        MOD_ID = "pythoncmdapi";
    }

    @Override
    public void onInitialize() {
        System.out.println("Hello Fabric world!");
    }
}