package io.github.lucun.chatutil.mixin;

import io.github.lucun.chatutil.mixininterface.IMixinChatHud;
import io.github.lucun.chatutil.mixininterface.IMixinChatScreen;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen implements IMixinChatScreen {

    @Unique private List<ChatHudLine> selectedLines = new LinkedList<>();
    @Unique private Set<ChatHudLine> selectionSet = new ReferenceLinkedOpenHashSet<>();
    @Unique private static final ChatHud CHAT_HUD = MinecraftClient.getInstance().inGameHud.getChatHud();
    @Unique private int startIndex = -1;
    @Unique private boolean mouseDown = false;

    @Shadow protected TextFieldWidget chatField;

    protected MixinChatScreen(Text title) {
        super(title);
    }

    protected MixinChatScreen(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked", at = @At(
            value = "HEAD"
    ))
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 0) {
            if (Screen.hasShiftDown()) {
                if (!this.commandSuggestor.mouseClicked((int)mouseX, (int)mouseY, button)) {
                    int index = ((IMixinChatHud) CHAT_HUD).getMessageIndex(mouseX, mouseY);
                    if (index != -1) {
                        this.clearSelection();
                        ChatHudLine line = ((IMixinChatHud) CHAT_HUD).getVisibleMessages().get(index);
                        this.addLine(line);
                        this.startIndex = index;
                        this.mouseDown = true;
                    }
                }
            } else {
                this.clearSelection();
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && Screen.hasShiftDown()) {
            int index = ((IMixinChatHud) CHAT_HUD).getMessageIndex(mouseX, mouseY);
            if (index != -1) {
                if (startIndex == -1) startIndex = index;
                this.clearSelection();
                if (startIndex < index) {
                    for (int i = startIndex; i <= index; ++i) {
                        ChatHudLine line = ((IMixinChatHud) CHAT_HUD).getVisibleMessages().get(i);
                        this.addLineReverse(line);
                    }
                } else {
                    for (int i = startIndex; i >= index; --i) {
                        ChatHudLine line = ((IMixinChatHud) CHAT_HUD).getVisibleMessages().get(i);
                        this.addLine(line);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.mouseDown = false;
        return false;
    }

    @Inject(method = "mouseScrolled", at = @At(
            value = "RETURN"
    ))
    private void onMouseScrolled(double d, double e, double amount, CallbackInfoReturnable<Boolean> cir) {
        if (Screen.hasShiftDown() && this.mouseDown) {
            int index = ((IMixinChatHud) CHAT_HUD).getMessageIndex(d, e);
            if (index != -1) {
                if (startIndex == -1) startIndex = index;
                this.clearSelection();
                if (startIndex < index) {
                    for (int i = startIndex; i <= index; ++i) {
                        ChatHudLine line = ((IMixinChatHud) CHAT_HUD).getVisibleMessages().get(i);
                        this.addLineReverse(line);
                    }
                } else {
                    for (int i = startIndex; i >= index; --i) {
                        ChatHudLine line = ((IMixinChatHud) CHAT_HUD).getVisibleMessages().get(i);
                        this.addLine(line);
                    }
                }
            }
        }
    }

    @Inject(method = "keyPressed", at = @At(
            value = "HEAD"
    ))
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.chatField.getSelectedText().isEmpty()) {
            if (keyCode == 67 && Screen.hasControlDown() && Screen.hasShiftDown() && !Screen.hasAltDown() && !selectedLines.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                selectedLines.forEach(line -> sb.append(line.getText().asFormattedString().replaceAll("§\\w", "")).append("\n"));
                MinecraftClient.getInstance().keyboard.setClipboard(sb.toString());
                this.clearSelection();
            }
        }
        if (keyCode == 256 /*ESC*/ || keyCode == 257 /*ENTER*/ || keyCode == 335 /*KEYPAD_ENTER*/) {
            this.clearSelection();
        }
    }

    @Override
    public List<ChatHudLine> getSelections() {
        return this.selectedLines;
    }

    private boolean addLine(ChatHudLine line) {
        if (!this.selectionSet.contains(line)) {
            this.selectionSet.add(line);
            this.selectedLines.add(line);
            return true;
        }
        return false;
    }

    private boolean addLineReverse(ChatHudLine line) {
        if (!this.selectionSet.contains(line)) {
            this.selectionSet.add(line);
            this.selectedLines.add(0, line);
            return true;
        }
        return false;
    }

    private void clearSelection() {
        this.selectionSet.clear();
        this.selectedLines.clear();
    }
}