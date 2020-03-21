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

package net.minecraftforge.client;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IRegistryDelegate;

/**
 * Wrapper around ItemModeMesher that cleans up the internal maps to respect ID remapping.
 */
public class ItemModelMesherForge extends ItemModelMesher
{
    final Map<IRegistryDelegate<Item>, ModelResourceLocation> locations = Maps.newHashMap();
    final Map<IRegistryDelegate<Item>, IBakedModel> models = Maps.newHashMap();

    public ItemModelMesherForge(ModelManager manager)
    {
        super(manager);
    }

    @Override
    @Nullable
    public IBakedModel func_199312_b(Item item)
    {
        return models.get(item.delegate);
    }

    @Override
    public void func_199311_a(Item item, ModelResourceLocation location)
    {
        IRegistryDelegate<Item> key = item.delegate;
        locations.put(key, location);
        models.put(key, func_178083_a().func_174953_a(location));
    }

    @Override
    public void func_178085_b()
    {
        final ModelManager manager = this.func_178083_a();
        for (Map.Entry<IRegistryDelegate<Item>, ModelResourceLocation> e : locations.entrySet())
        {
        	models.put(e.getKey(), manager.func_174953_a(e.getValue()));
        }
    }

    public ModelResourceLocation getLocation(@Nonnull ItemStack stack)
    {
        ModelResourceLocation location = locations.get(stack.func_77973_b().delegate);

        if (location == null)
        {
            location = ModelBakery.field_177604_a;
        }

        return location;
    }
}
