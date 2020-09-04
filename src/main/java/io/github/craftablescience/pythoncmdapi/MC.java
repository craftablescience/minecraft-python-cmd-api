package io.github.craftablescience.pythoncmdapi;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

import java.util.UUID;


public class MC {
    public MC() {}

    public void say(String msg) {
        if (MinecraftClient.getInstance().getServer() != null)
            MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText(msg), UUID.fromString("903f6b43-d59b-42e6-919e-2f10c2ee141c"));
    }
}
