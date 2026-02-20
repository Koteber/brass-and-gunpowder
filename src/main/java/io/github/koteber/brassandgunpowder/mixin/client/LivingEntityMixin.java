package io.github.koteber.brassandgunpowder.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    public void damage_bullet(Entity damageSource, int amount) {

    }
}
