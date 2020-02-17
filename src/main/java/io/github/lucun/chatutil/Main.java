package io.github.lucun.chatutil;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer{
    public static Logger LOGGER = LogManager.getLogger();
    public static final SuggestionProvider<? extends CommandSource> REGEX_NAME;
    static {
        REGEX_NAME = new SuggestionProviders.LocalProvider(new Identifier("chatutil:regex_name"), (context, builder) ->
            CommandSource.suggestMatching(new String[]{"default", "block_all"}, builder));

    }
    @Override
    public void onInitialize(){
        LOGGER.info("ChatUtil loading...");
        SuggestionProviders.register(new Identifier("regex_name"), ((SuggestionProviders.LocalProvider) REGEX_NAME));
        LOGGER.info("ChatUtil loading finished.");
    }
}
