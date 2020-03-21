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

package net.minecraftforge.common.data;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeItemTagsProvider extends ItemTagsProvider
{
    private Set<ResourceLocation> filter = null;

    public ForgeItemTagsProvider(DataGenerator gen)
    {
        super(gen);
    }

    @Override
    public void func_200432_c()
    {
        super.func_200432_c();
        filter = this.field_200434_b.entrySet().stream().map(e -> e.getKey().func_199886_b()).collect(Collectors.toSet());

        func_200426_a(Tags.Items.ARROWS).func_200573_a(Items.field_151032_g, Items.field_185167_i, Items.field_185166_h);
        func_200426_a(Tags.Items.BEACON_PAYMENT).func_200573_a(Items.field_151166_bC, Items.field_151045_i, Items.field_151043_k, Items.field_151042_j);
        func_200426_a(Tags.Items.BONES).func_200048_a(Items.field_151103_aS);
        func_200426_a(Tags.Items.BOOKSHELVES).func_200048_a(Items.field_221651_bN);
        func_200438_a(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
        func_200438_a(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
        func_200438_a(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        func_200438_a(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
        func_200438_a(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
        func_200426_a(Tags.Items.CROPS).add(Tags.Items.CROPS_BEETROOT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_NETHER_WART, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_WHEAT);
        func_200426_a(Tags.Items.CROPS_BEETROOT).func_200048_a(Items.field_185164_cV);
        func_200426_a(Tags.Items.CROPS_CARROT).func_200048_a(Items.field_151172_bF);
        func_200426_a(Tags.Items.CROPS_NETHER_WART).func_200048_a(Items.field_151075_bm);
        func_200426_a(Tags.Items.CROPS_POTATO).func_200048_a(Items.field_151174_bG);
        func_200426_a(Tags.Items.CROPS_WHEAT).func_200048_a(Items.field_151015_O);
        func_200426_a(Tags.Items.DUSTS).add(Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_PRISMARINE, Tags.Items.DUSTS_REDSTONE);
        func_200426_a(Tags.Items.DUSTS_GLOWSTONE).func_200048_a(Items.field_151114_aO);
        func_200426_a(Tags.Items.DUSTS_PRISMARINE).func_200048_a(Items.field_179562_cC);
        func_200426_a(Tags.Items.DUSTS_REDSTONE).func_200048_a(Items.field_151137_ax);
        addColored(func_200426_a(Tags.Items.DYES)::func_200574_a, Tags.Items.DYES, "{color}_dye");
        func_200426_a(Tags.Items.EGGS).func_200048_a(Items.field_151110_aK);
        func_200438_a(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
        func_200426_a(Tags.Items.ENDER_PEARLS).func_200048_a(Items.field_151079_bi);
        func_200426_a(Tags.Items.FEATHERS).func_200048_a(Items.field_151008_G);
        func_200438_a(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        func_200438_a(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        func_200438_a(Tags.Blocks.FENCES, Tags.Items.FENCES);
        func_200438_a(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
        func_200438_a(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        func_200426_a(Tags.Items.GEMS).add(Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ);
        func_200426_a(Tags.Items.GEMS_DIAMOND).func_200048_a(Items.field_151045_i);
        func_200426_a(Tags.Items.GEMS_EMERALD).func_200048_a(Items.field_151166_bC);
        func_200426_a(Tags.Items.GEMS_LAPIS).func_200048_a(Items.field_196128_bn);
        func_200426_a(Tags.Items.GEMS_PRISMARINE).func_200048_a(Items.field_179563_cD);
        func_200426_a(Tags.Items.GEMS_QUARTZ).func_200048_a(Items.field_151128_bU);
        func_200438_a(Tags.Blocks.GLASS, Tags.Items.GLASS);
        copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
        func_200438_a(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        func_200438_a(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL);
        func_200426_a(Tags.Items.GUNPOWDER).func_200048_a(Items.field_151016_H);
        func_200426_a(Tags.Items.HEADS).func_200573_a(Items.field_196185_dy, Items.field_196151_dA, Items.field_196184_dx, Items.field_196182_dv, Items.field_196183_dw, Items.field_196186_dz);
        func_200426_a(Tags.Items.INGOTS).add(Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_BRICK, Tags.Items.INGOTS_NETHER_BRICK);
        func_200426_a(Tags.Items.INGOTS_BRICK).func_200048_a(Items.field_151118_aC);
        func_200426_a(Tags.Items.INGOTS_GOLD).func_200048_a(Items.field_151043_k);
        func_200426_a(Tags.Items.INGOTS_IRON).func_200048_a(Items.field_151042_j);
        func_200426_a(Tags.Items.INGOTS_NETHER_BRICK).func_200048_a(Items.field_196154_dH);
        func_200426_a(Tags.Items.LEATHER).func_200048_a(Items.field_151116_aA);
        func_200426_a(Tags.Items.MUSHROOMS).func_200573_a(Items.field_221692_bh, Items.field_221694_bi);
        func_200426_a(Tags.Items.MUSIC_DISCS).func_200573_a(Items.field_196156_dS, Items.field_196158_dT, Items.field_196160_dU, Items.field_196162_dV, Items.field_196164_dW, Items.field_196166_dX, Items.field_196168_dY, Items.field_196170_dZ, Items.field_196187_ea, Items.field_196188_eb, Items.field_196189_ec, Items.field_196190_ed);
        func_200426_a(Tags.Items.NETHER_STARS).func_200048_a(Items.field_151156_bN);
        func_200438_a(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK);
        func_200426_a(Tags.Items.NUGGETS).add(Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_GOLD);
        func_200426_a(Tags.Items.NUGGETS_IRON).func_200048_a(Items.field_191525_da);
        func_200426_a(Tags.Items.NUGGETS_GOLD).func_200048_a(Items.field_151074_bl);
        func_200438_a(Tags.Blocks.OBSIDIAN, Tags.Items.OBSIDIAN);
        func_200438_a(Tags.Blocks.ORES, Tags.Items.ORES);
        func_200438_a(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
        func_200438_a(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
        func_200438_a(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
        func_200438_a(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
        func_200438_a(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
        func_200438_a(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
        func_200438_a(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
        func_200438_a(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
        func_200426_a(Tags.Items.RODS).add(Tags.Items.RODS_BLAZE, Tags.Items.RODS_WOODEN);
        func_200426_a(Tags.Items.RODS_BLAZE).func_200048_a(Items.field_151072_bj);
        func_200426_a(Tags.Items.RODS_WOODEN).func_200048_a(Items.field_151055_y);
        func_200438_a(Tags.Blocks.SAND, Tags.Items.SAND);
        func_200438_a(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS);
        func_200438_a(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED);
        func_200438_a(Tags.Blocks.SANDSTONE, Tags.Items.SANDSTONE);
        func_200426_a(Tags.Items.SEEDS).add(Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT);
        func_200426_a(Tags.Items.SEEDS_BEETROOT).func_200048_a(Items.field_185163_cU);
        func_200426_a(Tags.Items.SEEDS_MELON).func_200048_a(Items.field_151081_bc);
        func_200426_a(Tags.Items.SEEDS_PUMPKIN).func_200048_a(Items.field_151080_bb);
        func_200426_a(Tags.Items.SEEDS_WHEAT).func_200048_a(Items.field_151014_N);
        func_200426_a(Tags.Items.SLIMEBALLS).func_200048_a(Items.field_151123_aH);
        func_200438_a(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
        func_200438_a(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
        func_200438_a(Tags.Blocks.STONE, Tags.Items.STONE);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
        func_200438_a(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
        func_200426_a(Tags.Items.STRING).func_200048_a(Items.field_151007_F);
    }

    private void addColored(Consumer<Tag<Item>> consumer, Tag<Item> group, String pattern)
    {
        String prefix = group.func_199886_b().func_110623_a().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.func_176762_d()));
            Tag<Item> tag = getForgeItemTag(prefix + color.func_176762_d());
            Item item = ForgeRegistries.ITEMS.getValue(key);
            if (item == null || item  == Items.field_190931_a)
                throw new IllegalStateException("Unknown vanilla item: " + key.toString());
            func_200426_a(tag).func_200048_a(item);
            consumer.accept(tag);
        }
    }

    private void copyColored(Tag<Block> blockGroup, Tag<Item> itemGroup)
    {
        String blockPre = blockGroup.func_199886_b().func_110623_a().toUpperCase(Locale.ENGLISH) + '_';
        String itemPre = itemGroup.func_199886_b().func_110623_a().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            Tag<Block> from = getForgeBlockTag(blockPre + color.func_176762_d());
            Tag<Item> to = getForgeItemTag(itemPre + color.func_176762_d());
            func_200438_a(from, to);
        }
        func_200438_a(getForgeBlockTag(blockPre + "colorless"), getForgeItemTag(itemPre + "colorless"));
    }

    @SuppressWarnings("unchecked")
    private Tag<Block> getForgeBlockTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (Tag<Block>)Tags.Blocks.class.getDeclaredField(name).get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    @SuppressWarnings("unchecked")
    private Tag<Item> getForgeItemTag(String name)
    {
        try
        {
            name = name.toUpperCase(Locale.ENGLISH);
            return (Tag<Item>)Tags.Items.class.getDeclaredField(name).get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
        }
    }

    @Override
    protected Path func_200431_a(ResourceLocation id)
    {
        return filter != null && filter.contains(id) ? null : super.func_200431_a(id); //We don't want to save vanilla tags.
    }

    @Override
    public String func_200397_b()
    {
        return "Forge Item Tags";
    }
}
