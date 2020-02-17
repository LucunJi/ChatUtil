package io.github.lucun.chatutil.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.lucun.chatutil.mixininterface.IMixinChatHud;
import io.github.lucun.chatutil.setting.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SetRegexCommand implements IClientCommand {

    public void register(CommandDispatcher<? super ServerCommandSource> dispatcher) {
        ((CommandDispatcher<ServerCommandSource>) dispatcher).register(CommandManager.literal("chatutil")
                .then(CommandManager.literal("filter")
                        .then(CommandManager.literal("set")
                        .then(CommandManager.argument("name", StringArgumentType.word()).suggests(CommandSuggestions.REGEX_NAME)
                        .then(CommandManager.argument("regex", StringArgumentType.greedyString())
                        .executes(SetRegexCommand::setRegex)
                        )))
                        .then(CommandManager.literal("use")
                        .then(CommandManager.argument("name", StringArgumentType.word()).suggests(CommandSuggestions.REGEX_NAME)
                        .executes(SetRegexCommand::applyRegex)))

                )
                .then(CommandManager.literal("buffer")
                        .then(CommandManager.argument("buffer", IntegerArgumentType.integer(20, 1024)).suggests((context, builder) ->
                                CommandSource.suggestMatching(new String[]{"100"}, builder))
                        .executes(SetRegexCommand::setBufferSize))
                )
        );
    }

    private static int setBufferSize(CommandContext<ServerCommandSource> context) {
        Settings.BUFFER_SIZE = IntegerArgumentType.getInteger(context, "buffer");
        MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                String.format("Buffer size of chat hud is set to %d.", Settings.BUFFER_SIZE)), false);
        ((IMixinChatHud) MinecraftClient.getInstance().inGameHud.getChatHud()).updateFromBuffer();
        return 1;
    }

    private static int applyRegex(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if (!name.matches("^[a-zA-Z0-9_]+$")) {
            MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                    String.format("Invalid name: \"%s\"\nName should only contain alphabets, numbers and underlines.", name)).formatted(Formatting.RED), false);
            return -1;
        }
        Pattern pattern = Settings.PATTERN_MAP.get(name);
        if (pattern == null) {
            MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                    String.format("Pattern \"%s\" no found, check your pattern name.", name)).formatted(Formatting.RED), false);
            return -1;
        }
        Settings.CURRENT_PATTERN = name;
        MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                String.format("Current filter is set to %s.", name)), false);
        ((IMixinChatHud) MinecraftClient.getInstance().inGameHud.getChatHud()).updateFromBuffer();
        return 1;
    }

    private static int setRegex(CommandContext<ServerCommandSource> context) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(StringArgumentType.getString(context, "regex"));
        } catch (PatternSyntaxException e) {
            MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                    String.format("Invalid regex: %s.\n \"%s\" at position %d.", e.getDescription(), e.getPattern(), e.getIndex())).formatted(Formatting.RED), false);
            return -1;
        }
        String name = StringArgumentType.getString(context, "name");
        if (!name.matches("^[a-zA-Z0-9_]+$")) {
            MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                    String.format("Invalid name: \"%s\"\nName should only contain alphabets, numbers and underlines.", name)).formatted(Formatting.RED), false);
            return -1;
        }
        Settings.PATTERN_MAP.put(name, pattern);
        MinecraftClient.getInstance().player.addChatMessage(new LiteralText(
                String.format("Regex pattern \"%s\" is set as %s.", pattern.pattern(), name)), false);
        if (name.equals(Settings.CURRENT_PATTERN))
            ((IMixinChatHud) MinecraftClient.getInstance().inGameHud.getChatHud()).updateFromBuffer();
        return 1;
    }
}
