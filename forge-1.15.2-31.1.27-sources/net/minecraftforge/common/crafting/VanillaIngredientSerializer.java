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

package net.minecraftforge.common.crafting;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient>
{
    public static final VanillaIngredientSerializer INSTANCE  = new VanillaIngredientSerializer();

    public Ingredient parse(PacketBuffer buffer)
    {
        return Ingredient.func_209357_a(Stream.generate(() -> new Ingredient.SingleItemList(buffer.func_150791_c())).limit(buffer.func_150792_a()));
    }

    public Ingredient parse(JsonObject json)
    {
       return Ingredient.func_209357_a(Stream.of(Ingredient.func_199803_a(json)));
    }

    public void write(PacketBuffer buffer, Ingredient ingredient)
    {
        ItemStack[] items = ingredient.func_193365_a();
        buffer.func_150787_b(items.length);

        for (ItemStack stack : items)
            buffer.func_150788_a(stack);
    }
}
