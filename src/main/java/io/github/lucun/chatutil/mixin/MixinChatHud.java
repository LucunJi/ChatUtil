package io.github.lucun.chatutil.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lucun.chatutil.mixininterface.IMixinChatScreen;
import io.github.lucun.chatutil.mixininterface.IMixinChatHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IMixinChatHud {

    @Shadow @Final private List<ChatHudLine> visibleMessages;

    @Shadow public abstract boolean isChatFocused();

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract boolean method_23677();

    @Shadow public abstract double getChatScale();

    @Shadow public abstract int getVisibleLineCount();

    @Shadow public abstract int getWidth();

    @Shadow private int scrolledLines;

    @Shadow protected static double getMessageOpacityMultiplier(int age){return 0.0D;}

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(
            value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private int onQueryListSize1(List<Text> list) {
        return list.size() - (1024 - 100);
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(
            value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2))
    private int onQueryListSize2(List<Text> list) {
        return list.size() - (1024 - 100);
    }

    @Inject(method = "render", at = @At(
            value = "HEAD"
    ))
    private void onRender(int ticks, CallbackInfo ci) {
        if (this.method_23677()) {
            int i = this.getVisibleLineCount();
            int j = this.visibleMessages.size();
            if (j > 0) {
                boolean bl = false;
                if (this.isChatFocused()) {
                    bl = true;
                }

                double d = this.getChatScale();
                int k = MathHelper.ceil((double)this.getWidth() / d);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d, d, 1.0D);
                double e = this.client.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
                double f = this.client.options.textBackgroundOpacity;
                int l = 0;
                Matrix4f matrix4f = Matrix4f.translate(0.0F, 0.0F, -100.0F);

                int n;
                int o;
                int p;
                for(int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; ++m) {
                    ChatHudLine chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
                    if (chatHudLine != null) {
                        n = ticks - chatHudLine.getCreationTick();
                        if (n < 200 || bl) {
                            double g = bl ? 1.0D : getMessageOpacityMultiplier(n);
                            o = (int)(255.0D * g * e);
                            p = (int)(255.0D * g * f);
                            ++l;
                            if (o > 3) {
                                int r = -m * 9;
                                Screen screen = MinecraftClient.getInstance().currentScreen;
                                if (screen instanceof ChatScreen && ((IMixinChatScreen) screen).getSelections().contains(chatHudLine)) {
                                    DrawableHelper.fill(matrix4f, -2, r - 9, k + 4, r, p << 24);
                                }
                                RenderSystem.enableBlend();
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                            }
                        }
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public int getMessageIndex(double x, double y) {
        if (this.isChatFocused() && !this.client.options.hudHidden && this.method_23677()) {
            double d = this.getChatScale();
            double e = x - 2.0D;
            double f = (double)this.client.getWindow().getScaledHeight() - y - 40.0D;
            e = MathHelper.floor(e / d);
            f = MathHelper.floor(f / d);
            if (e >= 0.0D && f >= 0.0D) {
                int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
                if (e <= (double) MathHelper.floor((double)this.getWidth() / this.getChatScale())) {
                    if (f < (double)(9 * i + i)) {
                        int j = (int)(f / 9.0D + (double)this.scrolledLines);
                        if (j >= 0 && j < this.visibleMessages.size()) {
                            return j;
                        }

                        return -1;
                    }
                }

            }
        }
        return -1;
    }

    public List<ChatHudLine> getVisibleMessages() {
        return this.visibleMessages;
    }
}
