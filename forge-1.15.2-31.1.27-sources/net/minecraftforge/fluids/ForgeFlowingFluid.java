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

package net.minecraftforge.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class ForgeFlowingFluid extends FlowingFluid
{
    private final Supplier<? extends Fluid> flowing;
    private final Supplier<? extends Fluid> still;
    @Nullable
    private final Supplier<? extends Item> bucket;
    @Nullable
    private final Supplier<? extends FlowingFluidBlock> block;
    private final FluidAttributes.Builder builder;
    private final boolean canMultiply;
    private final int slopeFindDistance;
    private final int levelDecreasePerBlock;
    private final float explosionResistance;
    //private final BlockRenderLayer renderLayer; // TODO: reimplement
    private final int tickRate;

    protected ForgeFlowingFluid(Properties properties)
    {
        this.flowing = properties.flowing;
        this.still = properties.still;
        this.builder = properties.attributes;
        this.canMultiply = properties.canMultiply;
        this.bucket = properties.bucket;
        this.block = properties.block;
        this.slopeFindDistance = properties.slopeFindDistance;
        this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
        this.explosionResistance = properties.explosionResistance;
        //this.renderLayer = properties.renderLayer;
        this.tickRate = properties.tickRate;
    }

    @Override
    public Fluid func_210197_e()
    {
        return flowing.get();
    }

    @Override
    public Fluid func_210198_f()
    {
        return still.get();
    }

    @Override
    protected boolean func_205579_d()
    {
        return canMultiply;
    }

    @Override
    protected void func_205580_a(IWorld worldIn, BlockPos pos, BlockState state)
    {
        TileEntity tileentity = state.func_177230_c().func_149716_u() ? worldIn.func_175625_s(pos) : null;
        Block.func_220059_a(state, worldIn.func_201672_e(), pos, tileentity);
    }

    @Override
    protected int func_185698_b(IWorldReader worldIn)
    {
        return slopeFindDistance;
    }

    @Override
    protected int func_204528_b(IWorldReader worldIn)
    {
        return levelDecreasePerBlock;
    }

    /*@Override
    public BlockRenderLayer getRenderLayer()
    {
        return renderLayer;
    }*/

    @Override
    public Item func_204524_b()
    {
        return bucket != null ? bucket.get() : Items.field_190931_a;
    }

    @Override
    protected boolean func_215665_a(IFluidState state, IBlockReader world, BlockPos pos, Fluid fluidIn, Direction direction)
    {
        // Based on the water implementation, may need to be overriden for mod fluids that shouldn't behave like water.
        return direction == Direction.DOWN && !func_207187_a(fluidIn);
    }

    @Override
    public int func_205569_a(IWorldReader world)
    {
        return tickRate;
    }

    @Override
    protected float func_210195_d()
    {
        return explosionResistance;
    }

    @Override
    protected BlockState func_204527_a(IFluidState state)
    {
        if (block != null)
            return block.get().func_176223_P().func_206870_a(FlowingFluidBlock.field_176367_b, func_207205_e(state));
        return Blocks.field_150350_a.func_176223_P();
    }

    @Override
    public boolean func_207187_a(Fluid fluidIn) {
        return fluidIn == still.get() || fluidIn == flowing.get();
    }

    @Override
    protected FluidAttributes createAttributes()
    {
        return builder.build(this);
    }

    public static class Flowing extends ForgeFlowingFluid
    {
        public Flowing(Properties properties)
        {
            super(properties);
            func_207183_f(func_207182_e().func_177621_b().func_206870_a(field_207210_b, 7));
        }

        protected void func_207184_a(StateContainer.Builder<Fluid, IFluidState> builder) {
            super.func_207184_a(builder);
            builder.func_206894_a(field_207210_b);
        }

        public int func_207192_d(IFluidState state) {
            return state.func_177229_b(field_207210_b);
        }

        public boolean func_207193_c(IFluidState state) {
            return false;
        }
    }

    public static class Source extends ForgeFlowingFluid
    {
        public Source(Properties properties)
        {
            super(properties);
        }

        public int func_207192_d(IFluidState state) {
            return 8;
        }

        public boolean func_207193_c(IFluidState state) {
            return true;
        }
    }

    public static class Properties
    {
        private Supplier<? extends Fluid> still;
        private Supplier<? extends Fluid> flowing;
        private FluidAttributes.Builder attributes;
        private boolean canMultiply;
        private Supplier<? extends Item> bucket;
        private Supplier<? extends FlowingFluidBlock> block;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1;
        //private BlockRenderLayer renderLayer = BlockRenderLayer.TRANSLUCENT;
        private int tickRate = 5;

        public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, FluidAttributes.Builder attributes)
        {
            this.still = still;
            this.flowing = flowing;
            this.attributes = attributes;
        }

        public Properties canMultiply()
        {
            canMultiply = true;
            return this;
        }

        public Properties bucket(Supplier<? extends Item> bucket)
        {
            this.bucket = bucket;
            return this;
        }

        public Properties block(Supplier<? extends FlowingFluidBlock> block)
        {
            this.block = block;
            return this;
        }

        public Properties slopeFindDistance(int slopeFindDistance)
        {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        public Properties levelDecreasePerBlock(int levelDecreasePerBlock)
        {
            this.levelDecreasePerBlock = levelDecreasePerBlock;
            return this;
        }

        public Properties explosionResistance(float explosionResistance)
        {
            this.explosionResistance = explosionResistance;
            return this;
        }

        /*public Properties renderLayer(BlockRenderLayer layer)
        {
            this.renderLayer = layer;
            return this;
        }*/
    }
}
