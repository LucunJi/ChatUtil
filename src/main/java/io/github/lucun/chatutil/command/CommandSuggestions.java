package io.github.lucun.chatutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lucun.chatutil.setting.Settings;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class CommandSuggestions {

    public static CompletableFuture<Suggestions> regexName(CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(Settings.PATTERN_MAP.keySet(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> regexString(CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        Pattern pattern = Settings.PATTERN_MAP.get(StringArgumentType.getString(context, "name"));
        if (pattern == null) {
            return CommandSource.suggestMatching(new ArrayList<>(), suggestionsBuilder);
        } else {
            return CommandSource.suggestMatching(new String[]{pattern.pattern()},suggestionsBuilder);
        }    }
}
