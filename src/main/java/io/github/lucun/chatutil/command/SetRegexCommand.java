package io.github.lucun.chatutil.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.lucun.chatutil.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SetRegexCommand implements IClientCommand {
    public void register(CommandDispatcher<? super ServerCommandSource> dispatcher) {
        ((CommandDispatcher<ServerCommandSource>) dispatcher).register(CommandManager.literal("chatutil")
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("name", StringArgumentType.word()).suggests(((SuggestionProvider<ServerCommandSource>) Main.REGEX_NAME))
                                .then(CommandManager.argument("regex", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            try {
                                                Pattern p = Pattern.compile(StringArgumentType.getString(context, "regex"));
                                            } catch (PatternSyntaxException e) {
                                                MinecraftClient.getInstance().player.addChatMessage(new LiteralText("§cInvalid regex syntax!§r"), false);
                                                return -1;
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

}
