package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.LavaCauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaCauldronBlock.class)
public class LavaCauldronBlockMixin_TNTCartDamage {


    // Vanilla now queues Entity::setOnFireFromLava through the collision handler (a method reference,
    // so there is no call to redirect any more). Handle TNT carts ourselves and skip the vanilla events.
    @Inject(
            method = "onEntityCollision",
            at = @At("HEAD"),
            cancellable = true
    )
    private void igniteTntCart(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl, CallbackInfo ci) {
        if (MendedMinecartsMod.LAVA_CAULDRON_KILLS_TNT_CART.isEnabled() && entity instanceof TntMinecartEntity tntCart) {
            this.setOnFireFromLava(tntCart);
            ci.cancel();
        }
    }

    private void setOnFireFromLava(TntMinecartEntity tntCart) {
        if (tntCart.isFireImmune()) {
            return;
        }
        tntCart.setOnFireFor(15);
        if (tntCart.getEntityWorld() instanceof ServerWorld serverWorld && tntCart.damage(serverWorld, tntCart.getDamageSources().cactus(), 4.0f)) {
            tntCart.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + tntCart.getEntityWorld().random.nextFloat() * 0.4f);
        }
    }
}
