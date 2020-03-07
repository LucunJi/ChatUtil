package io.github.lucun.chatutil.command;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientCommands {
    public static IClientCommand CHATUTIL;
    public static List<IClientCommand> commandList = new ArrayList<>();
    private static Set<String> commandSet = Sets.newHashSet();

    static {
        CHATUTIL = register(new SetRegexCommand(), "chatutil");
    }

    private static IClientCommand register(IClientCommand command, String name) {
        commandList.add(command);
        commandSet.add(name);
        return command;
    }

    public static boolean isClientOnlyCommand(String name) {
        try {
            return commandSet.contains(name.split(" ")[0].substring(1));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}