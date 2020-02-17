package io.github.lucun.chatutil.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.lucun.chatutil.setting.Settings;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import java.util.Map;

public class CommandSuggestions {
    private static Map<String, SuggestionProvider<? extends CommandSource>> providerMap = Maps.newHashMap();

    public static final SuggestionProvider<ServerCommandSource> REGEX_NAME;
    static {
        REGEX_NAME = create("regex_name", (context, builder) ->
                CommandSource.suggestMatching(Settings.PATTERN_MAP.keySet(), builder));
    }

    public static SuggestionProvider<ServerCommandSource> create(String name, SuggestionProvider<CommandSource> provider) {
        SuggestionProvider<? extends CommandSource> provider1 = new SuggestionProviders.LocalProvider(
                new Identifier("chatutil:"+name), provider);
        providerMap.put(name, provider1);
        return (SuggestionProvider<ServerCommandSource>) provider1;
    }

    public static void register() {
        providerMap.forEach((name, provider) ->
                SuggestionProviders.register(new Identifier("chatutil"+name), (SuggestionProviders.LocalProvider) provider));
    }
}
