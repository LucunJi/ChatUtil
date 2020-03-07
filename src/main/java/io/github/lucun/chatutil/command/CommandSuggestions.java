package io.github.lucun.chatutil.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lucun.chatutil.setting.Settings;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CommandSuggestions {

    public static CompletableFuture<Suggestions> regexName(CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(Settings.PATTERN_MAP.keySet(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> regexString(CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(new String[]{Settings.PATTERN_MAP.get(StringArgumentType.getString(context, "name")).pattern()}, suggestionsBuilder);
    }
}
