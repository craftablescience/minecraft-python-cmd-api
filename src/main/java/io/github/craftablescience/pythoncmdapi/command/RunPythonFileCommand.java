package io.github.craftablescience.pythoncmdapi.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.craftablescience.pythoncmdapi.PythonCommandAPI;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.io.File;

import static io.github.craftablescience.pythoncmdapi.PythonCommandAPI.MOD_ID;
import static io.github.craftablescience.pythoncmdapi.PythonCommandAPI.PY_INTERP;


public class RunPythonFileCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("python")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.argument("filename", StringReader::readString)
                .executes(RunPythonFileCommand::python))));
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("py_runline")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.argument("code to execute", StringReader::readString)
                .executes(RunPythonFileCommand::py_runline))));
    }

    private static int python(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            String path = context.getArgument("filename", String.class).trim();
            context.getSource().sendFeedback(new TranslatableText(MOD_ID + ".commands.python.loading", path).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);

            try {
                PY_INTERP.execfile(PythonCommandAPI.PYTHON_DIR.toString() + File.separator + path);
            } catch (Exception e) {
                context.getSource().sendError(new TranslatableText(MOD_ID + ".commands.python.failure", e.getMessage()).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                e.printStackTrace();
                return;
            }
            context.getSource().sendFeedback(new TranslatableText(MOD_ID + ".commands.python.done", path).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);
        });
        return -1;
    }

    private static int py_runline(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            String code = context.getArgument("code to execute", String.class).trim();

            try {
                PY_INTERP.exec(code);
            } catch (Exception e) {
                context.getSource().sendError(new TranslatableText(MOD_ID + ".commands.py_runline.failure", code, e.getMessage()).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                e.printStackTrace();
            }
        });
        return -1;
    }
}