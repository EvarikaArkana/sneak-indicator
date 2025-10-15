package eva.sneaker.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eva.sneaker.SneakIndicatorClient.sprindicator;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    private Player getCameraPlayer() {
        return null;
    }

    @Unique
    private static final ResourceLocation texture = Gui.getMobEffectSprite(MobEffects.SLOWNESS);

    @Inject(method = "renderItemHotbar", at = @At(value = "HEAD"))
    private void afterRenderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player basePlayer = this.getCameraPlayer();
        if (!(basePlayer instanceof LocalPlayer player)) {
            return;
        }

        if (!player.isCrouching()) {
            return;
        }
        int i = guiGraphics.guiWidth() / 2;
        int n = guiGraphics.guiHeight() - 20;
        int a = sprindicator ? 24 : 0;
        int o = i + 91 + 6 + a;
        if (player.getMainArm().getOpposite() == HumanoidArm.RIGHT) {
            o = i - 91 - 22 - a;
        }
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, o, n, 18, 18);
    }

    @ModifyArg(
            method = "renderItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"
            ),
            index = 2,
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
                    ), to = @At("TAIL")
            )
    )
    private int modifyAttackIndicatorBackgroundX(int original) {
        return this.adjustAttackIndicatorX(original);
    }

    @ModifyArg(
            method = "renderItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"
            ),
            index = 6,
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
                    ),
                    to = @At("TAIL")
            )
    )
    private int modifyAttackIndicatorForegroundX(int original) {
        return this.adjustAttackIndicatorX(original);
    }

    @Unique
    private int adjustAttackIndicatorX(int original) {
        Player player = this.getCameraPlayer();
        if (player == null) {
            return original;
        }
        HumanoidArm arm = player.getMainArm().getOpposite();
        return original + (arm == HumanoidArm.LEFT ? 24 : -24);
    }
}
