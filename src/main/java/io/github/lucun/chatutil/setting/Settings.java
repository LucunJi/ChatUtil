package io.github.lucun.chatutil.setting;

import com.google.common.collect.Maps;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.lucun.chatutil.Main;

import java.io.*;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Settings {
    public static boolean BUFFER_FILTERED = true;
    public static int BUFFER_SIZE = 100;
    public static Map <String, Pattern> PATTERN_MAP = Maps.newHashMap();
    public static String CURRENT_PATTERN = "allow_all";
    private static Pattern ALLOW_ALL = Pattern.compile("^$");

    private static final String FILE_PATH = "./config/chatutil.json";
    private static final File CONFIG_DIR = new File("./config");

    static {
        PATTERN_MAP.put("allow_all", ALLOW_ALL);
        PATTERN_MAP.put("block_all", Pattern.compile(""));
        PATTERN_MAP.put("players_only", Pattern.compile("(^[^<])"));
        PATTERN_MAP.put("block_players", Pattern.compile("<\\w+>"));
    }

    public static Pattern getPattern() {
        if (PATTERN_MAP.containsKey(CURRENT_PATTERN)) {
            return PATTERN_MAP.get(CURRENT_PATTERN);
        } else {
            return ALLOW_ALL;
        }
    }
    
    public static void load() {
        File settingFile = new File(FILE_PATH);
        if (settingFile.isFile() && settingFile.exists()) {
            try (FileReader fileReader = new FileReader(settingFile)) {
                JsonReader jsonReader = new JsonReader(new BufferedReader(fileReader));
                jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String name = jsonReader.nextName();
                        switch (name) {
                            case "buffer_size" :
                                BUFFER_SIZE = jsonReader.nextInt();
                                break;
                            case "current_pattern":
                                CURRENT_PATTERN = jsonReader.nextString();
                                break;
                            case "buffer_filtered":
                                BUFFER_FILTERED = jsonReader.nextBoolean();
                                break;
                            case "patterns" :
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()) {
                                    jsonReader.beginObject();
                                    String patternName = jsonReader.nextName();
                                    String pattern = jsonReader.nextString();
                                    jsonReader.endObject();
                                    try {
                                        PATTERN_MAP.put(patternName, Pattern.compile(pattern));
                                    } catch (PatternSyntaxException e) {
                                        Main.LOGGER.error(String.format("Invalid regex: %s.\n \"%s\" at position %d.", e.getDescription(), e.getPattern(), e.getIndex()));
                                    }
                                }
                                jsonReader.endArray();
                                break;
                        }
                    }
                jsonReader.endObject();
            } catch (Exception e) {
                Main.LOGGER.error("Error occurred during saving settings for ChatUtil.");
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        if (!CONFIG_DIR.exists())
            CONFIG_DIR.mkdirs();
        try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
            JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(fileWriter));
            jsonWriter.setIndent("    ");
            jsonWriter.setLenient(true);
            jsonWriter.beginObject()
                    .name("buffer_size").value(BUFFER_SIZE)
                    .name("current_pattern").value(CURRENT_PATTERN)
                    .name("buffer_filtered").value(BUFFER_FILTERED)
                    .name("patterns").beginArray();
                            for (Map.Entry<String, Pattern> e : PATTERN_MAP.entrySet()) {
                                String name = e.getKey();
                                Pattern pattern = e.getValue();
                                jsonWriter.beginObject().name(name).value(pattern.pattern()).endObject();
                            }
                    jsonWriter.endArray();
            jsonWriter.endObject();
            jsonWriter.flush();
        } catch (Exception e) {
            Main.LOGGER.error("Error occurred during saving settings for ChatUtil.");
            e.printStackTrace();
        }
    }
}
