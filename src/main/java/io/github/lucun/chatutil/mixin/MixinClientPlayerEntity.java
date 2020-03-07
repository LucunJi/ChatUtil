package io.github.lucun.chatutil.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lucun.chatutil.MessageUtil;
import io.github.lucun.chatutil.command.ClientCommands;
import io.github.lucun.chatutil.command.ClientOnlyCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @Shadow public abstract void sendChatMessage(String string);

    @Shadow public abstract void addChatMessage(Text message, boolean bl);

    @Redirect(method = "sendChatMessage", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"
    ))
    private void onSendChatMessage(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
        String msg = ((ChatMessageC2SPacket) packet).getChatMessage();
        if (ClientCommands.isClientOnlyCommand(msg)) {
            ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
            try {
                playerEntity.networkHandler.getCommandDispatcher().execute(msg.substring(1), new ClientOnlyCommandSource(playerEntity));
            } catch (CommandSyntaxException e) {
                playerEntity.addChatMessage(Texts.toText(e.getRawMessage()).formatted(Formatting.RED), false);
            }
        } else {
            msg = MessageUtil.parseString(msg, (ClientPlayerEntity)(Object) this);
            clientPlayNetworkHandler.sendPacket(new ChatMessageC2SPacket(msg));
        }
    }
}
