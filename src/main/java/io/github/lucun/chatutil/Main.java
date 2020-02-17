package io.github.lucun.chatutil;

import io.github.lucun.chatutil.setting.Settings;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer{
    public static Logger LOGGER = LogManager.getLogger();
    @Override
    public void onInitialize(){
        LOGGER.info("ChatUtil loading...");
        Settings.load();
        LOGGER.info("ChatUtil loading finished.");
    }
}
