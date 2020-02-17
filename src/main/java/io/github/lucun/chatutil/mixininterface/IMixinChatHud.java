package io.github.lucun.chatutil.mixininterface;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;

public interface IMixinChatHud {
    public default ChatHudLine getLine(double x, double y) {
        int index = getMessageIndex(x, y);
        if (index != -1) return this.getVisibleMessages().get(index);
        return null;
    }
    List<ChatHudLine> getVisibleMessages();
    int getMessageIndex(double x, double y);
    void updateFromBuffer();
}
