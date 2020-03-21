/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends RangedWrapper
{
    private final PlayerInventory inventoryPlayer;

    public PlayerMainInvWrapper(PlayerInventory inv)
    {
        super(new InvWrapper(inv), 0, inv.field_70462_a.size());
        inventoryPlayer = inv;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest.func_190916_E()!= stack.func_190916_E())
        {
            // the stack in the slot changed, animate it
            ItemStack inSlot = getStackInSlot(slot);
            if(!inSlot.func_190926_b())
            {
                if (getInventoryPlayer().field_70458_d.field_70170_p.field_72995_K)
                {
                    inSlot.func_190915_d(5);
                }
                else if(getInventoryPlayer().field_70458_d instanceof ServerPlayerEntity) {
                    getInventoryPlayer().field_70458_d.field_71070_bA.func_75142_b();
                }
            }
        }
        return rest;
    }

    public PlayerInventory getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
