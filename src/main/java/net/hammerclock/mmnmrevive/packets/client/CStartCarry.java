package net.hammerclock.mmnmrevive.packets.client;

import net.hammerclock.mmnmrevive.PlayerReviveCompatI18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.pixelatedw.mineminenomi.events.CombatModeEvents;

import java.util.UUID;
import java.util.function.Supplier;

public class CStartCarry {

    UUID playerUUID;
    private UUID targetUUID;

    public CStartCarry() {
    }

    public CStartCarry(PlayerEntity player, PlayerEntity target) {
        this.playerUUID = player.getUUID();
        this.targetUUID = target.getUUID();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(playerUUID);
        buffer.writeUUID(targetUUID);
    }

    public static CStartCarry decode(PacketBuffer buffer) {
        CStartCarry msg = new CStartCarry();
        msg.playerUUID = buffer.readUUID();
        msg.targetUUID = buffer.readUUID();
        return msg;
    }

    public static void handle(CStartCarry message, final Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity player = ctx.get().getSender();
                PlayerEntity target = player.level.getPlayerByUUID(message.targetUUID);

                if (target != null && !target.isSpectator()) {
                    if (!CombatModeEvents.Common.tryPickupTarget(
                            player.getServer().getPlayerList().getPlayer(message.playerUUID),
                            player.getServer().getPlayerList().getPlayer(message.targetUUID))
                    ) {
                        player.sendMessage(new TranslationTextComponent(PlayerReviveCompatI18n.CARRY_FAIL).withStyle(net.minecraft.util.text.TextFormatting.RED), Util.NIL_UUID);
                    }
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
