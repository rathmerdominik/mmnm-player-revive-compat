package net.hammerclock.mmnmrevive.packets.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.packet.HelperPacket;
import team.creative.playerrevive.server.PlayerReviveServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Supplier;


public class CRemoveHelper {
    private UUID playerUUID;

    private static final Method resetPlayer =  ObfuscationReflectionHelper.findMethod(PlayerReviveServer.class, "resetPlayer", PlayerEntity.class, IBleeding.class);

    public CRemoveHelper() {
    }

    public CRemoveHelper(PlayerEntity player) {
        this.playerUUID = player.getUUID();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(playerUUID);
    }

    public static CRemoveHelper decode(PacketBuffer buffer) {
        CRemoveHelper msg = new CRemoveHelper();
        msg.playerUUID = buffer.readUUID();
        return msg;
    }

    public static void handle(CRemoveHelper message, final Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {

                ServerPlayerEntity player = ctx.get().getSender().getServer().getPlayerList().getPlayer(message.playerUUID);
                IBleeding revive = PlayerReviveServer.getBleeding(player);

                try {
                    resetPlayer.invoke(
                            null,
                            player,
                            revive
                    );
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
