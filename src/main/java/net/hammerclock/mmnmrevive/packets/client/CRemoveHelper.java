package net.hammerclock.mmnmrevive.packets.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.packet.HelperPacket;
import team.creative.playerrevive.server.PlayerReviveServer;

import java.util.UUID;
import java.util.function.Supplier;


public class CRemoveHelper {
    private UUID playerUUID;

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
                resetPlayer(
                        player,
                        revive
                );
            });
        }
        ctx.get().setPacketHandled(true);
    }

    // Hella annoying. Had to copy paste this because the Invoker Mixin just doesn't work for some reason??
    private static void resetPlayer(PlayerEntity player, IBleeding revive) {
        player.abilities.invulnerable = player.isCreative();
        player.setInvulnerable(false);

        for (PlayerEntity helper : revive.revivingPlayers()) {
            PlayerRevive.NETWORK.sendToClient(new HelperPacket(null, false), (ServerPlayerEntity) helper);
        }

        revive.revivingPlayers().clear();
        PlayerReviveServer.sendUpdatePacket(player);
    }
}
