package io.github.lucun.chatutil;

import com.google.common.collect.Lists;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    private static List<Pair<Pattern, BiFunction<ClientPlayerEntity, String, String>>> symbolFunctions = Lists.newArrayList();

    static {
        symbolFunctions.add(Pair.of(
                Pattern.compile("\\\\\\{pos}"),
                (entity, str) -> String.format("[%d,%d,%d]", entity.getBlockPos().getX(), entity.getBlockPos().getY(), entity.getBlockPos().getZ())
        ));
    }

    public static String parseString(String message, ClientPlayerEntity player) {
        String newMsg = message;
        for (Pair<Pattern, BiFunction<ClientPlayerEntity, String, String>> pair : symbolFunctions) {
            Pattern pattern = pair.getLeft();
            BiFunction<ClientPlayerEntity, String, String> converter = pair.getRight();
            newMsg = parseSymbol(newMsg, player, pattern, converter);
        }
        return newMsg;
    }

    private static String parseSymbol(String message, ClientPlayerEntity player, Pattern pattern, BiFunction<ClientPlayerEntity, String, String> converter) {
        Matcher mcr = pattern.matcher(message);
        int lastIndex = 0;
        StringBuilder sb = new StringBuilder();
        while (mcr.find()) {
            int begin = mcr.start(), end = mcr.end();
            sb.append(message.substring(lastIndex, begin));
            String result = converter.apply(player, message.substring(begin, end));
            sb.append(result);
            lastIndex = end;
        }
        sb.append(message.substring(lastIndex));
        return sb.toString();
    }
}
