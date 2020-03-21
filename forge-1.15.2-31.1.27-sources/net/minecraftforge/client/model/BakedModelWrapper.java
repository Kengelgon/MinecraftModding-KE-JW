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

package net.minecraftforge.client.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;

public abstract class BakedModelWrapper<T extends IBakedModel> implements IBakedModel
{
    protected final T originalModel;

    public BakedModelWrapper(T originalModel)
    {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> func_200117_a(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return originalModel.func_200117_a(state, side, rand);
    }

    @Override
    public boolean func_177555_b()
    {
        return originalModel.func_177555_b();
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state)
    {
        return originalModel.isAmbientOcclusion(state);
    }

    @Override
    public boolean func_177556_c()
    {
        return originalModel.func_177556_c();
    }

    @Override
    public boolean func_230044_c_()
    {
        return originalModel.func_230044_c_();
    }

    @Override
    public boolean func_188618_c()
    {
        return originalModel.func_188618_c();
    }

    @Override
    public TextureAtlasSprite func_177554_e()
    {
        return originalModel.func_177554_e();
    }

    @Override
    public ItemCameraTransforms func_177552_f()
    {
        return originalModel.func_177552_f();
    }

    @Override
    public ItemOverrideList func_188617_f()
    {
        return originalModel.func_188617_f();
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return originalModel.doesHandlePerspectives();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        return originalModel.handlePerspective(cameraTransformType, mat);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        return originalModel.getParticleTexture(data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return originalModel.getQuads(state, side, rand, extraData);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        return originalModel.getModelData(world, pos, state, tileData);
    }
}
