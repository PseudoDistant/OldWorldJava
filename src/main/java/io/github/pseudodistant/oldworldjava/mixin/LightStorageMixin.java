package io.github.pseudodistant.oldworldjava.mixin;

import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LightStorage.class)
abstract class LightStorageMixin {
    @Inject(method = "get", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/chunk/light/LightStorage;getLightSection(JZ)Lnet/minecraft/world/chunk/ChunkNibbleArray;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    protected void checkNull(long blockPos, CallbackInfoReturnable<Integer> cir, long l, ChunkNibbleArray chunkNibbleArray) {
        if (chunkNibbleArray == null) {
            cir.setReturnValue(0);
        }
    }
}