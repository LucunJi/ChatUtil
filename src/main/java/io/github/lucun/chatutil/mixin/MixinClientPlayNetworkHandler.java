package io.github.lucun.chatutil.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.github.lucun.chatutil.command.ClientCommands;
import io.github.lucun.chatutil.command.SetRegexCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "<init>", at = @At(
            value = "RETURN"
    ))
    private void onNewInstance(MinecraftClient client, Screen screen, ClientConnection clientConnection, GameProfile gameProfile, CallbackInfo ci) {
        ClientCommands.commandList.forEach(command -> command.register(commandDispatcher));
    }

    @Inject(method = "onCommandTree", at = @At(
            value = "RETURN"
    ))
    private void onNewInstance(CommandTreeS2CPacket packet, CallbackInfo ci) {
        ClientCommands.commandList.forEach(command -> command.register(commandDispatcher));
    }
}
