package io.github.koteber.brassandgunpowder.screen;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class ReloadScreenHandler extends ScreenHandler {
    public Inventory result = new CraftingResultInventory();
    public World world;

    public ReloadScreenHandler(PlayerInventory playerInventory, World world){
        this.world = world;

        for(int var8 = 0; var8 < 3; ++var8) {
            for(int var10 = 0; var10 < 9; ++var10) {
                this.addSlot(new Slot(playerInventory, var10 + var8 * 9 + 9, 8 + var10 * 18, 84 + var8 * 18));
            }
        }

        for(int var9 = 0; var9 < 9; ++var9) {
            this.addSlot(new Slot(playerInventory, var9, 8 + var9 * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
