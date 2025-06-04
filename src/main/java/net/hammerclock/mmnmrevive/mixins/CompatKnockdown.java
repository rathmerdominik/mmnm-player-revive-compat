package net.hammerclock.mmnmrevive.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.hammerclock.mmnmrevive.PlayerReviveCompatI18n;
import net.hammerclock.mmnmrevive.PlayerReviveCompatPacketHandler;
import net.hammerclock.mmnmrevive.config.CommonConfig;
import net.hammerclock.mmnmrevive.packets.client.CRemoveHelper;
import net.hammerclock.mmnmrevive.packets.client.CStartCarry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.client.ReviveEventClient;
import xyz.pixelatedw.mineminenomi.ModMain;

import java.util.List;

@Mixin(ReviveEventClient.class)
abstract class CompatKnockdown {
    @Unique
    private static Minecraft mc = Minecraft.getInstance();
    @Unique
    private int pressedTicks = 0;
    @Unique
    private static final int MAX_TICKS_TO_CARRY = 60; // Max ticks for holding the key

    @Unique
    private void renderKnockdownGraphic(MatrixStack matrixStack, ResourceLocation knockdownGraphic, int currentTicks, int maxTicks) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int imageWidth = 64;
        int imageHeight = 64;

        int xPosition = (screenWidth - imageWidth) / 2;
        int yPosition = ((screenHeight - imageHeight) / 2) + 50;

        mc.getTextureManager().bind(knockdownGraphic);

        matrixStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int filledWidth = 0;
        if (maxTicks > 0 && currentTicks > 0) {
            filledWidth = (int) (((float) currentTicks / maxTicks) * imageWidth);
        }
        filledWidth = Math.min(filledWidth, imageWidth);
        filledWidth = Math.max(0, filledWidth);

        if (filledWidth > 0) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(matrixStack, xPosition, yPosition, 0, 0, filledWidth, imageHeight, imageWidth, imageHeight);
        }

        int unfilledWidth = imageWidth - filledWidth;
        if (unfilledWidth > 0) {
            RenderSystem.color4f(0.3F, 0.3F, 0.3F, 1.0F);
            AbstractGui.blit(matrixStack, xPosition + filledWidth, yPosition, filledWidth, 0, unfilledWidth, imageHeight, imageWidth, imageHeight);
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        matrixStack.popPose();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lteam/creative/playerrevive/server/PlayerReviveServer;getBleeding(Lnet/minecraft/entity/player/PlayerEntity;)Lteam/creative/playerrevive/api/IBleeding;", shift = At.Shift.AFTER, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    public void mmnmrevive$tickHandleCustomKnockdownHud(TickEvent.RenderTickEvent renderTickEvent, CallbackInfo ci, PlayerEntity player, IBleeding revive, PlayerEntity revivingPlayer, List<ITextComponent> list) {
        if (!CommonConfig.INSTANCE.isGiveKnockdownEffect()) {
            return;
        }

        ResourceLocation knockdownGraphic = new ResourceLocation(ModMain.PROJECT_ID, "textures/abilities/knockdown.png");

        if (mc.options.keyUse.isDown()) {
            if (pressedTicks < MAX_TICKS_TO_CARRY) {
                pressedTicks++;
            }
        } else {
            if (pressedTicks > 0) {
                pressedTicks = 0;
            }
        }

        list.add(new TranslationTextComponent(PlayerReviveCompatI18n.PRESS_TO_CARRY, mc.options.keyUse.getTranslatedKeyMessage()));

        MatrixStack currentMatrixStack = new MatrixStack();
        this.renderKnockdownGraphic(currentMatrixStack, knockdownGraphic, pressedTicks, MAX_TICKS_TO_CARRY);

        if (pressedTicks >= MAX_TICKS_TO_CARRY) {
            pressedTicks = 0;
            if (mc.player != null) {
                PlayerReviveCompatPacketHandler.INSTANCE.sendToServer(new CStartCarry(player, revivingPlayer));
                PlayerReviveCompatPacketHandler.INSTANCE.sendToServer(new CRemoveHelper(revivingPlayer));
            }
        }
    }
}
