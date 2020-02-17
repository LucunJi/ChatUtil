package io.github.lucun.chatutil.mixininterface;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;
import java.util.Set;

public interface IMixinChatScreen {
    List<ChatHudLine> getSelections();
}
