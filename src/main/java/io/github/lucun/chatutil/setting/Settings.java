package io.github.lucun.chatutil.setting;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Pattern;

public class Settings {
    public static int BUFFER_SIZE = 100;
    public static Map <String, Pattern> PATTERN_MAP = Maps.newHashMap();
    public static String CURRENT_PATTERN = "allow_all";

    static {
        PATTERN_MAP.put("allow_all", Pattern.compile(""));
        PATTERN_MAP.put("block_all", Pattern.compile(".*"));
        PATTERN_MAP.put("players_only", Pattern.compile("[^(<.*>)].*"));
        PATTERN_MAP.put("players_blocked", Pattern.compile("<.*>.*"));
    }

    public static Pattern getPattern() {
        return PATTERN_MAP.get(CURRENT_PATTERN);
    }
}
