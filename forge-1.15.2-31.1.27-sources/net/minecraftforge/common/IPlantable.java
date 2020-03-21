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

package net.minecraftforge.common;

import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPlantable
{
    default PlantType getPlantType(IBlockReader world, BlockPos pos) {
        if (this instanceof CropsBlock) return PlantType.Crop;
        if (this instanceof SaplingBlock) return PlantType.Plains;
        if (this instanceof FlowerBlock) return PlantType.Plains;
        if (this == Blocks.field_196555_aI)      return PlantType.Desert;
        if (this == Blocks.field_196651_dG)       return PlantType.Water;
        if (this == Blocks.field_150337_Q)   return PlantType.Cave;
        if (this == Blocks.field_150338_P) return PlantType.Cave;
        if (this == Blocks.field_150388_bm)    return PlantType.Nether;
        if (this == Blocks.field_196804_gh)      return PlantType.Plains;
        return net.minecraftforge.common.PlantType.Plains;
    }

    BlockState getPlant(IBlockReader world, BlockPos pos);
}
