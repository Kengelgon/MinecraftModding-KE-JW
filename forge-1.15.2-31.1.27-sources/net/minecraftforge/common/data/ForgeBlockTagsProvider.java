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

import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;

import static net.minecraftforge.common.Tags.Blocks.*;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ForgeBlockTagsProvider extends BlockTagsProvider
{
    private Set<ResourceLocation> filter = null;

    public ForgeBlockTagsProvider(DataGenerator gen)
    {
        super(gen);
    }

    @Override
    public void func_200432_c()
    {
        super.func_200432_c();
        filter = this.field_200434_b.entrySet().stream().map(e -> e.getKey().func_199886_b()).collect(Collectors.toSet());

        func_200426_a(CHESTS).add(CHESTS_ENDER, CHESTS_TRAPPED, CHESTS_WOODEN);
        func_200426_a(CHESTS_ENDER).func_200048_a(Blocks.field_150477_bB);
        func_200426_a(CHESTS_TRAPPED).func_200048_a(Blocks.field_150447_bR);
        func_200426_a(CHESTS_WOODEN).func_200573_a(Blocks.field_150486_ae, Blocks.field_150447_bR);
        func_200426_a(COBBLESTONE).func_200573_a(Blocks.field_150347_e, Blocks.field_196687_dd, Blocks.field_150341_Y);
        func_200426_a(DIRT).func_200573_a(Blocks.field_150346_d, Blocks.field_196658_i, Blocks.field_196660_k, Blocks.field_196661_l, Blocks.field_150391_bh);
        func_200426_a(END_STONES).func_200048_a(Blocks.field_150377_bs);
        func_200426_a(FENCE_GATES).func_200574_a(FENCE_GATES_WOODEN);
        func_200426_a(FENCE_GATES_WOODEN).func_200573_a(Blocks.field_180390_bo, Blocks.field_180391_bp, Blocks.field_180392_bq, Blocks.field_180386_br, Blocks.field_180387_bt, Blocks.field_180385_bs);
        func_200426_a(FENCES).add(FENCES_NETHER_BRICK, FENCES_WOODEN);
        func_200426_a(FENCES_NETHER_BRICK).func_200048_a(Blocks.field_150386_bk);
        func_200426_a(FENCES_WOODEN).func_200573_a(Blocks.field_180407_aO, Blocks.field_180408_aP, Blocks.field_180404_aQ, Blocks.field_180403_aR, Blocks.field_180405_aT, Blocks.field_180406_aS);
        func_200426_a(GLASS).add(GLASS_COLORLESS, STAINED_GLASS);
        func_200426_a(GLASS_COLORLESS).func_200048_a(Blocks.field_150359_w);
        addColored(func_200426_a(STAINED_GLASS)::func_200048_a, GLASS, "{color}_stained_glass");
        func_200426_a(GLASS_PANES).add(GLASS_PANES_COLORLESS, STAINED_GLASS_PANES);
        func_200426_a(GLASS_PANES_COLORLESS).func_200048_a(Blocks.field_150410_aZ);
        addColored(func_200426_a(STAINED_GLASS_PANES)::func_200048_a, GLASS_PANES, "{color}_stained_glass_pane");
        func_200426_a(GRAVEL).func_200048_a(Blocks.field_150351_n);
        func_200426_a(NETHERRACK).func_200048_a(Blocks.field_150424_aL);
        func_200426_a(OBSIDIAN).func_200048_a(Blocks.field_150343_Z);
        func_200426_a(ORES).add(ORES_COAL, ORES_DIAMOND, ORES_EMERALD, ORES_GOLD, ORES_IRON, ORES_LAPIS, ORES_REDSTONE, ORES_QUARTZ);
        func_200426_a(ORES_COAL).func_200048_a(Blocks.field_150365_q);
        func_200426_a(ORES_DIAMOND).func_200048_a(Blocks.field_150482_ag);
        func_200426_a(ORES_EMERALD).func_200048_a(Blocks.field_150412_bA);
        func_200426_a(ORES_GOLD).func_200048_a(Blocks.field_150352_o);
        func_200426_a(ORES_IRON).func_200048_a(Blocks.field_150366_p);
        func_200426_a(ORES_LAPIS).func_200048_a(Blocks.field_150369_x);
        func_200426_a(ORES_QUARTZ).func_200048_a(Blocks.field_196766_fg);
        func_200426_a(ORES_REDSTONE).func_200048_a(Blocks.field_150450_ax);
        func_200426_a(SAND).add(SAND_COLORLESS, SAND_RED);
        func_200426_a(SAND_COLORLESS).func_200048_a(Blocks.field_150354_m);
        func_200426_a(SAND_RED).func_200048_a(Blocks.field_196611_F);
        func_200426_a(SANDSTONE).func_200573_a(Blocks.field_150322_A, Blocks.field_196585_ak, Blocks.field_196583_aj, Blocks.field_196580_bH, Blocks.field_180395_cM, Blocks.field_196799_hB, Blocks.field_196798_hA, Blocks.field_196582_bJ);
        func_200426_a(STONE).func_200573_a(Blocks.field_196656_g, Blocks.field_196654_e, Blocks.field_196650_c, Blocks.field_196686_dc, Blocks.field_150348_b, Blocks.field_196657_h, Blocks.field_196655_f, Blocks.field_196652_d);
        func_200426_a(STORAGE_BLOCKS).add(STORAGE_BLOCKS_COAL, STORAGE_BLOCKS_DIAMOND, STORAGE_BLOCKS_EMERALD, STORAGE_BLOCKS_GOLD, STORAGE_BLOCKS_IRON, STORAGE_BLOCKS_LAPIS, STORAGE_BLOCKS_QUARTZ, STORAGE_BLOCKS_REDSTONE);
        func_200426_a(STORAGE_BLOCKS_COAL).func_200048_a(Blocks.field_150402_ci);
        func_200426_a(STORAGE_BLOCKS_DIAMOND).func_200048_a(Blocks.field_150484_ah);
        func_200426_a(STORAGE_BLOCKS_EMERALD).func_200048_a(Blocks.field_150475_bE);
        func_200426_a(STORAGE_BLOCKS_GOLD).func_200048_a(Blocks.field_150340_R);
        func_200426_a(STORAGE_BLOCKS_IRON).func_200048_a(Blocks.field_150339_S);
        func_200426_a(STORAGE_BLOCKS_LAPIS).func_200048_a(Blocks.field_150368_y);
        func_200426_a(STORAGE_BLOCKS_QUARTZ).func_200048_a(Blocks.field_150371_ca);
        func_200426_a(STORAGE_BLOCKS_REDSTONE).func_200048_a(Blocks.field_150451_bX);
    }

    private void addColored(Consumer<Block> consumer, Tag<Block> group, String pattern)
    {
        String prefix = group.func_199886_b().func_110623_a().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color  : DyeColor.values())
        {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}",  color.func_176762_d()));
            Tag<Block> tag = getForgeTag(prefix + color.func_176762_d());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block  == Blocks.field_150350_a)
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            func_200426_a(tag).func_200048_a(block);
            consumer.accept(block);
        }
    }

    @SuppressWarnings("unchecked")
    private Tag<Block> getForgeTag(String name)
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

    @Override
    protected Path func_200431_a(ResourceLocation id)
    {
        return filter != null && filter.contains(id) ? null : super.func_200431_a(id); //We don't want to save vanilla tags.
    }

    @Override
    public String func_200397_b()
    {
        return "Forge Block Tags";
    }
}
