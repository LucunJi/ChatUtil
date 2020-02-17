package io.github.lucun.chatutil.command;

import java.util.ArrayList;
import java.util.List;

public class ClientCommands {
    public static SetRegexCommand setRegexCommand = new SetRegexCommand();
    public static List<IClientCommand> commandList = new ArrayList<>();

    static {
        register(setRegexCommand);
    }

    private static void register(IClientCommand command) {
        commandList.add(command);
    }
}
