package io.github.lucun.chatutil.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;

public class ClientOnlyCommandSource extends ServerCommandSource {
    public ClientOnlyCommandSource(ClientPlayerEntity clientPlayerEntity) {
        super(clientPlayerEntity, clientPlayerEntity.getPosVector(), clientPlayerEntity.getRotationClient(), null, 0, clientPlayerEntity.getEntityName(), clientPlayerEntity.getName(), null, clientPlayerEntity);
    }

    @Override
    public Collection<String> getPlayerNames() {
        return super.getPlayerNames();
    }
}
