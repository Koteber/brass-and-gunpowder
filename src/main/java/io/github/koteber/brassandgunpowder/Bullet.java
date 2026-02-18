package io.github.koteber.brassandgunpowder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class Bullet extends Entity {
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private int blockId = 0;
    private int blockMeta = 0;
    private boolean inGround = false;
    public boolean pickupAllowed = false;
    public int shake = 0;
    public LivingEntity owner;
    private int life;
    private int inAirTime = 0;

    private int damage;

    public Bullet(World world) {
        super(world);
        this.setBoundingBoxSpacing(0.5F, 0.5F);
    }

    public Bullet(World world, double x, double y, double z) {
        super(world);
        this.setBoundingBoxSpacing(0.5F, 0.5F);
        this.setPosition(x, y, z);
        this.standingEyeHeight = 0.0F;
    }

    public Bullet(World world, LivingEntity owner, float speed, float divergence, int damage) {
        super(world);
        this.damage = damage;
        this.owner = owner;
        this.pickupAllowed = owner instanceof PlayerEntity;
        this.setBoundingBoxSpacing(0.5F, 0.5F);
        this.setPositionAndAnglesKeepPrevAngles(owner.x, owner.y + (double)owner.getEyeHeight(), owner.z, owner.yaw, owner.pitch);
        this.x -= (double)(MathHelper.cos(this.yaw / 180.0F * (float)Math.PI) * 0.16F);
        this.y -= (double)0.1F;
        this.z -= (double)(MathHelper.sin(this.yaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.x, this.y, this.z);
        this.standingEyeHeight = 0.0F;
        this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float)Math.PI));
        this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float)Math.PI));
        this.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float)Math.PI));
        this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, speed, divergence);
    }

    protected void initDataTracker() {
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        float var9 = MathHelper.sqrt(x * x + y * y + z * z);
        x /= (double)var9;
        y /= (double)var9;
        z /= (double)var9;
        x += this.random.nextGaussian() * (double)0.0075F * (double)divergence;
        y += this.random.nextGaussian() * (double)0.0075F * (double)divergence;
        z += this.random.nextGaussian() * (double)0.0075F * (double)divergence;
        x *= (double)speed;
        y *= (double)speed;
        z *= (double)speed;
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        float var10 = MathHelper.sqrt(x * x + z * z);
        this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * (double)180.0F / (double)(float)Math.PI);
        this.prevPitch = this.pitch = (float)(Math.atan2(y, (double)var10) * (double)180.0F / (double)(float)Math.PI);
        this.life = 0;
    }

    @Environment(EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var7 = MathHelper.sqrt(x * x + z * z);
            this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * (double)180.0F / (double)(float)Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(y, (double)var7) * (double)180.0F / (double)(float)Math.PI);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.setPositionAndAnglesKeepPrevAngles(this.x, this.y, this.z, this.yaw, this.pitch);
            this.life = 0;
        }

    }

    public void tick() {
        super.tick();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var1 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.prevYaw = this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * (double)180.0F / (double)(float)Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(this.velocityY, (double)var1) * (double)180.0F / (double)(float)Math.PI);
        }

        int var15 = this.world.getBlockId(this.blockX, this.blockY, this.blockZ);
        if (var15 > 0) {
            Block.BLOCKS[var15].updateBoundingBox(this.world, this.blockX, this.blockY, this.blockZ);
            Box var2 = Block.BLOCKS[var15].getCollisionShape(this.world, this.blockX, this.blockY, this.blockZ);
            if (var2 != null && var2.contains(Vec3d.createCached(this.x, this.y, this.z))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            var15 = this.world.getBlockId(this.blockX, this.blockY, this.blockZ);
            int var21 = this.world.getBlockMeta(this.blockX, this.blockY, this.blockZ);
            if (var15 == this.blockId && var21 == this.blockMeta) {
                ++this.life;
                if (this.life == 1200) {
                    this.markDead();
                }

            } else {
                this.inGround = false;
                this.velocityX *= (double)(this.random.nextFloat() * 0.2F);
                this.velocityY *= (double)(this.random.nextFloat() * 0.2F);
                this.velocityZ *= (double)(this.random.nextFloat() * 0.2F);
                this.life = 0;
                this.inAirTime = 0;
            }
        } else {
            ++this.inAirTime;
            Vec3d var16 = Vec3d.createCached(this.x, this.y, this.z);
            Vec3d var19 = Vec3d.createCached(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            HitResult var3 = this.world.raycast(var16, var19, false, true);
            var16 = Vec3d.createCached(this.x, this.y, this.z);
            var19 = Vec3d.createCached(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            if (var3 != null) {
                var19 = Vec3d.createCached(var3.pos.x, var3.pos.y, var3.pos.z);
            }

            Entity var4 = null;
            List var5 = this.world.getEntities(this, this.boundingBox.stretch(this.velocityX, this.velocityY, this.velocityZ).expand((double)1.0F, (double)1.0F, (double)1.0F));
            double var6 = (double)0.0F;

            for(int var8 = 0; var8 < var5.size(); ++var8) {
                Entity var9 = (Entity)var5.get(var8);
                if (var9.isCollidable() && (var9 != this.owner || this.inAirTime >= 5)) {
                    float var10 = 0.3F;
                    Box var11 = var9.boundingBox.expand((double)var10, (double)var10, (double)var10);
                    HitResult var12 = var11.raycast(var16, var19);
                    if (var12 != null) {
                        double var13 = var16.distanceTo(var12.pos);
                        if (var13 < var6 || var6 == (double)0.0F) {
                            var4 = var9;
                            var6 = var13;
                        }
                    }
                }
            }

            if (var4 != null) {
                var3 = new HitResult(var4);
            }

            if (var3 != null) {
                if (var3.entity != null) {
                    if (var3.entity.damage(this.owner, Math.toIntExact(damage))) {
                        this.world.playSound(this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                        this.markDead();
                    } else {
                        this.velocityX *= (double)-0.1F;
                        this.velocityY *= (double)-0.1F;
                        this.velocityZ *= (double)-0.1F;
                        this.yaw += 180.0F;
                        this.prevYaw += 180.0F;
                        this.inAirTime = 0;
                    }
                } else {
                    this.blockX = var3.blockX;
                    this.blockY = var3.blockY;
                    this.blockZ = var3.blockZ;
                    this.blockId = this.world.getBlockId(this.blockX, this.blockY, this.blockZ);
                    this.blockMeta = this.world.getBlockMeta(this.blockX, this.blockY, this.blockZ);
                    this.velocityX = (double)((float)(var3.pos.x - this.x));
                    this.velocityY = (double)((float)(var3.pos.y - this.y));
                    this.velocityZ = (double)((float)(var3.pos.z - this.z));
                    float var22 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    this.x -= this.velocityX / (double)var22 * (double)0.05F;
                    this.y -= this.velocityY / (double)var22 * (double)0.05F;
                    this.z -= this.velocityZ / (double)var22 * (double)0.05F;
                    this.world.playSound(this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.shake = 7;
                }
            }

            this.x += this.velocityX;
            this.y += this.velocityY;
            this.z += this.velocityZ;
            float var23 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * (double)180.0F / (double)(float)Math.PI);

            for(this.pitch = (float)(Math.atan2(this.velocityY, (double)var23) * (double)180.0F / (double)(float)Math.PI); this.pitch - this.prevPitch < -180.0F; this.prevPitch -= 360.0F) {
            }

            while(this.pitch - this.prevPitch >= 180.0F) {
                this.prevPitch += 360.0F;
            }

            while(this.yaw - this.prevYaw < -180.0F) {
                this.prevYaw -= 360.0F;
            }

            while(this.yaw - this.prevYaw >= 180.0F) {
                this.prevYaw += 360.0F;
            }

            this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
            this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
            float var24 = 1F;
            float var25 = 0.0F;
            if (this.isSubmergedInWater()) {
                for(int var26 = 0; var26 < 4; ++var26) {
                    float var27 = 0.25F;
                    this.world.addParticle("bubble", this.x - this.velocityX * (double)var27, this.y - this.velocityY * (double)var27, this.z - this.velocityZ * (double)var27, this.velocityX, this.velocityY, this.velocityZ);
                }

                var24 = 0.8F;
            }

            this.velocityX *= (double)var24;
            this.velocityY *= (double)var24;
            this.velocityZ *= (double)var24;
            this.velocityY -= (double)var25;
            this.setPosition(this.x, this.y, this.z);
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putShort("xTile", (short)this.blockX);
        nbt.putShort("yTile", (short)this.blockY);
        nbt.putShort("zTile", (short)this.blockZ);
        nbt.putByte("inTile", (byte)this.blockId);
        nbt.putByte("inData", (byte)this.blockMeta);
        nbt.putByte("shake", (byte)this.shake);
        nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        nbt.putBoolean("player", this.pickupAllowed);
    }

    public void readNbt(NbtCompound nbt) {
        this.blockX = nbt.getShort("xTile");
        this.blockY = nbt.getShort("yTile");
        this.blockZ = nbt.getShort("zTile");
        this.blockId = nbt.getByte("inTile") & 255;
        this.blockMeta = nbt.getByte("inData") & 255;
        this.shake = nbt.getByte("shake") & 255;
        this.inGround = nbt.getByte("inGround") == 1;
        this.pickupAllowed = nbt.getBoolean("player");
    }

    public void onPlayerInteraction(PlayerEntity player) {
        if (!this.world.isRemote) {
            if (this.inGround && this.pickupAllowed && this.shake <= 0 && player.inventory.addStack(new ItemStack(Item.ARROW, 1))) {
                this.world.playSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.sendPickup(this, 1);
                this.markDead();
            }

        }
    }

    @Environment(EnvType.CLIENT)
    public float getShadowRadius() {
        return 0.0F;
    }
}
