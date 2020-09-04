package io.github.craftablescience.pythoncmdapi.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.craftablescience.pythoncmdapi.PythonCommandAPI;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.craftablescience.pythoncmdapi.PythonCommandAPI.*;


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
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("py_scandir")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .executes(RunPythonFileCommand::py_scandir)));
    }

    private static int python(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            String path = context.getArgument("filename", String.class).trim();
            context.getSource().sendFeedback(new TranslatableText(MOD_ID + ".commands.python.loading", path).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);

            try {
                Thread thread = new Thread(() -> {
                    PY_INTERP.execfile(PythonCommandAPI.PYTHON_DIR.toString() + File.separator + path);
                });
                thread.start();
            } catch (Exception e) {
                context.getSource().sendError(new TranslatableText(MOD_ID + ".commands.python.failure", e.getMessage()).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                e.printStackTrace();
            }
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

    private static int py_scandir(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                AtomicInteger count = new AtomicInteger(0);
                ArrayList<String> filenames = new ArrayList<>();

                Stream<Path> stream = Files.walk(PYTHON_DIR, Integer.MAX_VALUE);
                List<String> collect = stream
                        .map(String::valueOf)
                        .sorted()
                        .collect(Collectors.toList());

                collect.forEach((String str) -> {
                    if (str.endsWith(".py")) {
                        count.addAndGet(1);
                        str = str.replace(PYTHON_DIR.toString(), "");
                        filenames.add(str);
                    }
                });

                context.getSource().sendFeedback(new TranslatableText(MOD_ID + ".commands.py_scandir.number_of_files", count.get()).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);
                for (String str : filenames) {
                    context.getSource().sendFeedback(new LiteralText(str).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), true);
                }
            } catch (IOException e) {
                context.getSource().sendFeedback(new TranslatableText(MOD_ID + ".commands.py_scandir.failure", e.getMessage()).setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
                e.printStackTrace();
            }
        });
        return -1;
    }
}