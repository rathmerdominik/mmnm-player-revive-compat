package net.hammerclock.mmnmrevive.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MultiplayerAllowMixin {

    @Shadow
    @Final
    private boolean allowsMultiplayer;

    @Shadow
    @Final
    private boolean allowsChat;

    @Inject(method="allowsMultiplayer", at = @At("HEAD"), cancellable = true)
    public void allowsMultiplayer(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(this.allowsMultiplayer);
    }

    @Inject(method="allowsChat", at = @At("HEAD"), cancellable = true)
    public void allowsChat(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(this.allowsChat);
    }
}