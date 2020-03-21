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

package net.minecraftforge.fml;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author cpw
 *
 */
public final class FMLWorldPersistenceHook implements WorldPersistenceHooks.WorldPersistenceHook
{

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker WORLDPERSISTENCE = MarkerManager.getMarker("WP");

    @Override
    public String getModId()
    {
        return "fml";
    }

    @Override
    public CompoundNBT getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        CompoundNBT fmlData = new CompoundNBT();
        ListNBT modList = new ListNBT();
        ModList.get().getMods().forEach(mi->
        {
            final CompoundNBT mod = new CompoundNBT();
            mod.func_74778_a("ModId", mi.getModId());
            mod.func_74778_a("ModVersion", MavenVersionStringHelper.artifactVersionToString(mi.getVersion()));
            modList.add(mod);
        });
        fmlData.func_218657_a("LoadingModList", modList);

        CompoundNBT registries = new CompoundNBT();
        fmlData.func_218657_a("Registries", registries);
        LOGGER.debug(WORLDPERSISTENCE,"Gathering id map for writing to world save {}", info.func_76065_j());

        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e : RegistryManager.ACTIVE.takeSnapshot(true).entrySet())
        {
            registries.func_218657_a(e.getKey().toString(), e.getValue().write());
        }
        return fmlData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, CompoundNBT tag)
    {
        if (tag.func_74764_b("LoadingModList"))
        {
            ListNBT modList = tag.func_150295_c("LoadingModList", (byte)10);
            for (int i = 0; i < modList.size(); i++)
            {
                CompoundNBT mod = modList.func_150305_b(i);
                String modId = mod.func_74779_i("ModId");
                if (Objects.equals("minecraft",  modId)) {
                    continue;
                }
                String modVersion = mod.func_74779_i("ModVersion");
                Optional<? extends ModContainer> container = ModList.get().getModContainerById(modId);
                if (!container.isPresent())
                {
                    LOGGER.error(WORLDPERSISTENCE,"This world was saved with mod {} which appears to be missing, things may not work well", modId);
                    continue;
                }
                if (!Objects.equals(modVersion, MavenVersionStringHelper.artifactVersionToString(container.get().getModInfo().getVersion())))
                {
                    LOGGER.warn(WORLDPERSISTENCE,"This world was saved with mod {} version {} and it is now at version {}, things may not work well", modId, modVersion, MavenVersionStringHelper.artifactVersionToString(container.get().getModInfo().getVersion()));
                }
            }
        }

        Multimap<ResourceLocation, ResourceLocation> failedElements = null;

        if (tag.func_74764_b("ModItemData") || tag.func_74764_b("ItemData")) // Pre 1.7
        {
            StartupQuery.notify("This save predates 1.7.10, it can no longer be loaded here. Please load in 1.7.10 or 1.8 first");
            StartupQuery.abort();
        }
        else if (tag.func_74764_b("Registries")) // 1.8, genericed out the 'registries' list
        {
            Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = new HashMap<>();
            CompoundNBT regs = tag.func_74775_l("Registries");
            for (String key : regs.func_150296_c())
            {
                snapshot.put(new ResourceLocation(key), ForgeRegistry.Snapshot.read(regs.func_74775_l(key)));
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
        }

        if (failedElements != null && !failedElements.isEmpty())
        {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader could not load this save.\n\n")
               .append("There are ").append(failedElements.size()).append(" unassigned registry entries in this save.\n")
               .append("You will not be able to load until they are present again.\n\n");

            failedElements.asMap().forEach((name, entries) ->
            {
                buf.append("Missing ").append(name).append(":\n");
                entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
            });

            StartupQuery.notify(buf.toString());
            StartupQuery.abort();
        }
    }
}
