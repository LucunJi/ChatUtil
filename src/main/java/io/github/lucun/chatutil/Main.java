package io.github.lucun.chatutil;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer{
    public static Logger LOGGER = LogManager.getLogger();
    @Override
    public void onInitialize(){
        LOGGER.info("ChatUtil loading...");
        LOGGER.info("ChatUtil loading finished.");
    }
}
