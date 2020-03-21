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

import java.util.function.Function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

final class FancyMissingModel implements IUnbakedModel
{
    private static final ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
    private static final Material font2 = new Material(AtlasTexture.field_110575_b, new ResourceLocation("minecraft", "font/ascii"));
    private static final TransformationMatrix smallTransformation = new TransformationMatrix(null, null, new Vector3f(.25f, .25f, .25f), null)
            .blockCenterToCorner();
    private static final SimpleModelFontRenderer fontRenderer = Util.func_199748_a(() -> {
        float [] mv = new float[16];
        mv[2*4+0] = 1f / 128f;
        mv[0*4+1] =mv[1*4+2] = -mv[2*4+0];
        mv[3*4+3] = 1;
        mv[0*4+3] = 1;
        mv[0*4+3] = 1 + 1f / 0x100;
        mv[0*4+3] = 0;
        Matrix4f m = new Matrix4f(mv);
        return new SimpleModelFontRenderer(
            Minecraft.func_71410_x().field_71474_y,
            font,
            Minecraft.func_71410_x().func_110434_K(),
            false,
            m
        ) {/* TODO Implement once SimpleModelFontRenderer is fixed
            @Override
            protected float renderUnicodeChar(char c, boolean italic)
            {
                return super.renderDefaultChar(126, italic);
            }
      */};
    });

    private final IUnbakedModel missingModel;
    private final String message;

    public FancyMissingModel(IUnbakedModel missingModel, String message)
    {
        this.missingModel = missingModel;
        this.message = message;
    }

    @Override
    public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<com.mojang.datafixers.util.Pair<String, String>> p_225614_2_)
    {
        return ImmutableList.of(font2);
    }

    @Override
    public Collection<ResourceLocation> func_187965_e()
    {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public IBakedModel func_225613_a_(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
    {
        IBakedModel bigMissing = missingModel.func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation);
        ModelTransformComposition smallState = new ModelTransformComposition(modelTransform, new SimpleModelTransform(smallTransformation));
        IBakedModel smallMissing = missingModel.func_225613_a_(bakery, spriteGetter, smallState, modelLocation);
        return new BakedModel(bigMissing, smallMissing, fontRenderer, message, spriteGetter.apply(font2));
    }

    static final class BakedModel implements IBakedModel
    {
        private final SimpleModelFontRenderer fontRenderer;
        private final String message;
        private final TextureAtlasSprite fontTexture;
        private final IBakedModel missingModel;
        private final IBakedModel otherModel;
        private final boolean big;
        private ImmutableList<BakedQuad> quads;

        public BakedModel(IBakedModel bigMissing, IBakedModel smallMissing, SimpleModelFontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture)
        {
            this.missingModel = bigMissing;
            otherModel = new BakedModel(smallMissing, fontRenderer, message, fontTexture, this);
            this.big = true;
            this.fontRenderer = fontRenderer;
            this.message = message;
            this.fontTexture = fontTexture;
        }

        public BakedModel(IBakedModel smallMissing, SimpleModelFontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture, BakedModel big)
        {
            this.missingModel = smallMissing;
            otherModel = big;
            this.big = false;
            this.fontRenderer = fontRenderer;
            this.message = message;
            this.fontTexture = fontTexture;
        }

        @Override
        public List<BakedQuad> func_200117_a(@Nullable BlockState state, @Nullable Direction side, Random rand)
        {
            if (side == null)
            {
                if (quads == null)
                {
                    fontRenderer.setSprite(fontTexture);
                    fontRenderer.setFillBlanks(true);
                    String[] lines = message.split("\\r?\\n");
                    List<String> splitLines = Lists.newArrayList();
                    for (int y = 0; y < lines.length; y++)
                    {
                        splitLines.addAll(fontRenderer.func_78271_c(lines[y], 0x80));
                    }
                    for (int y = 0; y < splitLines.size(); y++)
                    {
                        fontRenderer.func_211126_b(splitLines.get(y), 0, ((y - splitLines.size() / 2f) * fontRenderer.field_78288_b) + 0x40, 0xFF00FFFF);
                    }
                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                    builder.addAll(missingModel.func_200117_a (state, side, rand));
                    builder.addAll(fontRenderer.build());
                    quads = builder.build();
                }
                return quads;
            }
            return missingModel.func_200117_a (state, side, rand);
        }

        @Override
        public boolean func_177555_b() { return true; }

        @Override
        public boolean func_177556_c() { return false; }

        @Override
        public boolean func_230044_c_() { return false; }

        @Override
        public boolean func_188618_c() { return false; }

        @Override
        public TextureAtlasSprite func_177554_e() { return fontTexture; }

        @Override
        public ItemOverrideList func_188617_f() { return ItemOverrideList.field_188022_a; }

        @Override
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
        {
            TransformationMatrix transform = TransformationMatrix.func_227983_a_();
            boolean big = true;
            switch (cameraTransformType)
            {

                case THIRD_PERSON_LEFT_HAND:
                    break;
                case THIRD_PERSON_RIGHT_HAND:
                    break;
                case FIRST_PERSON_LEFT_HAND:
                    transform = new TransformationMatrix(new Vector3f(-0.62f, 0.5f, -.5f), new Quaternion(1, -1, -1, 1), null, null);
                    big = false;
                    break;
                case FIRST_PERSON_RIGHT_HAND:
                    transform = new TransformationMatrix(new Vector3f(-0.5f, 0.5f, -.5f), new Quaternion(1, 1, 1, 1), null, null);
                    big = false;
                    break;
                case HEAD:
                    break;
                case GUI:
                    if (ForgeConfig.CLIENT.zoomInMissingModelTextInGui.get())
                    {
                        transform = new TransformationMatrix(null, new Quaternion(1, 1, 1, 1), new Vector3f(4, 4, 4), null);
                        big = false;
                    }
                    else
                    {
                        transform = new TransformationMatrix(null, new Quaternion(1, 1, 1, 1), null, null);
                        big = true;
                    }
                    break;
                case FIXED:
                    transform = new TransformationMatrix(null, new Quaternion(-1, -1, 1, 1), null, null);
                    break;
                default:
                    break;
            }
            mat.func_227866_c_().func_227870_a_().func_226595_a_(transform.func_227988_c_());
            if (big != this.big)
            {
                return otherModel;
            }
            return this;
        }
    }
}
