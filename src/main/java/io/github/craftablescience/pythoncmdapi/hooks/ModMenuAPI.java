package io.github.craftablescience.pythoncmdapi.hooks;

import io.github.craftablescience.pythoncmdapi.PythonCommandAPI;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


@Environment(EnvType.CLIENT)
public class ModMenuAPI implements ModMenuApi {

    @Override
    public String getModId() {
        return PythonCommandAPI.MOD_ID;
    }
}