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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.server.ServerMultiWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.server.ServerModLoader;
import net.minecraftforge.registries.ClearableRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;

public class DimensionManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker DIMMGR = MarkerManager.getMarker("DIMS");

    private static final ClearableRegistry<DimensionType> REGISTRY = new ClearableRegistry<>(new ResourceLocation("dimension_type"), DimensionType.class);

    private static final Int2ObjectMap<Data> dimensions = Int2ObjectMaps.synchronize(new Int2ObjectLinkedOpenHashMap<>());
    private static final IntSet unloadQueue = IntSets.synchronize(new IntLinkedOpenHashSet());
    private static final ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys().weakValues().makeMap();
    private static final Multiset<Integer> leakedWorlds = HashMultiset.create();
    private static final Map<ResourceLocation, SavedEntry> savedEntries = new HashMap<>();
    private static volatile Set<World> playerWorlds = new HashSet<>();

    /**
     * Register or get the existing dimension type for the given dimtype name.
     *
     * Dimensions already known to the save are loaded into the DimensionType map before the {@link RegisterDimensionsEvent} is fired.
     * You can use this helper to get your existing dimension, or create it if it is not found.
     *
     * @param name Registry name
     * @param type ModDimension type data
     * @param data Extra data for the ModDimension
     * @param hasSkyLight does this dimension have a skylight?
     * @param magnifier The biome generation processor
     * @return the DimensionType for the dimension.
     */
    public static DimensionType registerOrGetDimension(ResourceLocation name, ModDimension type, PacketBuffer data, boolean hasSkyLight) {
        return REGISTRY.func_218349_b(name).orElseGet(()->registerDimension(name, type, data, hasSkyLight));
    }
    /**
     * Registers a real unique dimension, Should be called on server init, or when the dimension is created.
     * On the client, the list will be reset/reloaded every time a InternalServer is refreshed.
     *
     * Forge will save this data and keep track of registered values, syncing automatically from the remote server.
     *
     * @param name Registry name for this new dimension.
     * @param type Dimension Type.
     * @param data Configuration data for this dimension, passed into
     * @param hasSkyLight skylight for this dimension
     * @param magnifier The biome generation processor
     * @return the DimensionType for the dimension.
     */
    public static DimensionType registerDimension(ResourceLocation name, ModDimension type, PacketBuffer data, boolean hasSkyLight)
    {
        Validate.notNull(name, "Can not register a dimension with null name");
        Validate.isTrue(!REGISTRY.func_212607_c(name), "Dimension: " + name + " Already registered");
        Validate.notNull(type, "Can not register a null dimension type");

        int id = REGISTRY.getNextId();
        SavedEntry old = savedEntries.get(name);
        if (old != null)
        {
            id = old.getId();
            if (!type.getRegistryName().equals(old.getType()))
                LOGGER.info(DIMMGR, "Changing ModDimension for '{}' from '{}' to '{}'", name.toString(), old.getType() == null ? null : old.getType().toString(), type.getRegistryName().toString());
            savedEntries.remove(name);
        }
        @SuppressWarnings("deprecation")
        DimensionType instance = new DimensionType(id, "", name.func_110624_b() + "/" + name.func_110623_a(), type.getFactory(), hasSkyLight, type.getMagnifier(), type, data);
        REGISTRY.func_218382_a(id, name, instance);
        LOGGER.info(DIMMGR, "Registered dimension {} of type {} and id {}", name.toString(), type.getRegistryName().toString(), id);
        return instance;
    }

    /**
     * Configures if the dimension will stay loaded in memory even if all chunks are unloaded.
     *
     * @param dim The dimension
     * @param value True to keep loaded, false to allow unloading
     * @return The old value for this dimension.
     */
    public static boolean keepLoaded(DimensionType dim, boolean value)
    {
        Validate.notNull(dim, "Dimension type must not be null");
        Data data = getData(dim);
        boolean ret = data.keepLoaded;
        data.keepLoaded = value;
        return ret;
    }

    /**
     * Determines if the dimension will stay loaded in memory even if all chunks are unloaded.
     * @param dim The dimension
     * @return True if the dimension will stay loaded with no chunks loaded.
     */
    public static boolean keepLoaded(DimensionType dim)
    {
        Validate.notNull(dim, "Dimension type must not be null");
        Data data = dimensions.get(dim.func_186068_a());
        return data == null ? false : data.keepLoaded;
    }

    /**
     * Retrieves the world from the server allowing for null return, and optionally resetting it's unload timer.
     *
     * @param server The server that controls this world.
     * @param dim Dimension to load.
     * @param resetUnloadDelay True to reset the unload timer, which is a delay that is used to prevent constant world loading/unloading cycle.
     * @param forceLoad True to attempt to load the dimension if the server has it unloaded.
     * @return The world, null if unloaded and not loadable.
     */
    @Nullable
    public static ServerWorld getWorld(MinecraftServer server, DimensionType dim, boolean resetUnloadDelay, boolean forceLoad)
    {
        Validate.notNull(server, "Must provide server when creating world");
        Validate.notNull(dim, "Dimension type must not be null");

        if (ServerModLoader.hasErrors()) {
            throw new RuntimeException("The server has failed to initialize correctly due to mod loading errors. Examine the crash report for more details.");
        }
        // If we're in the early stages of loading, we need to return null so CommandSource can work properly for command function
        if (StartupQuery.pendingQuery()) {
            return null;
        }

        if (resetUnloadDelay && unloadQueue.contains(dim.func_186068_a()))
            getData(dim).ticksWaited = 0;

        @SuppressWarnings("deprecation")
        ServerWorld ret = server.forgeGetWorldMap().get(dim);
        if (ret == null && forceLoad)
            ret = initWorld(server, dim);
        return ret;
    }

    //==========================================================================================================
    //                                         FORGE INTERNAL
    //==========================================================================================================

    public static void unregisterDimension(int id)
    {
        Validate.isTrue(dimensions.containsKey(id), String.format("Failed to unregister dimension for id %d; No provider registered", id));
        dimensions.remove(id);
    }

    public static DimensionType registerDimensionInternal(int id, ResourceLocation name, ModDimension type, PacketBuffer data, boolean hasSkyLight)
    {
        Validate.notNull(name, "Can not register a dimension with null name");
        Validate.notNull(type, "Can not register a null dimension type");
        Validate.isTrue(!REGISTRY.func_212607_c(name), "Dimension: " + name + " Already registered");
        Validate.isTrue(REGISTRY.func_148745_a(id) == null, "Dimension with id " + id + " already registered as name " + REGISTRY.func_177774_c(REGISTRY.func_148745_a(id)));

        @SuppressWarnings("deprecation")
        DimensionType instance = new DimensionType(id, "", name.func_110624_b() + "/" + name.func_110623_a(), type.getFactory(), hasSkyLight, type.getMagnifier(), type, data);
        REGISTRY.func_218382_a(id, name, instance);
        LOGGER.info(DIMMGR, "Registered dimension {} of type {} and id {}", name.toString(), type.getRegistryName().toString(), id);
        return instance;
    }

    @SuppressWarnings("deprecation")
    public static ServerWorld initWorld(MinecraftServer server, DimensionType dim)
    {
        Validate.isTrue(dim != DimensionType.field_223227_a_, "Can not hotload overworld. This must be loaded at all times by main Server.");
        Validate.notNull(server, "Must provide server when creating world");
        Validate.notNull(dim, "Must provide dimension when creating world");

        ServerWorld overworld = getWorld(server, DimensionType.field_223227_a_, false, false);
        Validate.notNull(overworld, "Cannot Hotload Dim: Overworld is not Loaded!");

        @SuppressWarnings("resource")
        ServerWorld world = new ServerMultiWorld(overworld, server, server.func_213207_aT(), overworld.func_217485_w(), dim, server.func_213185_aS(), new NoopChunkStatusListener());
        if (!server.func_71264_H())
            world.func_72912_H().func_76060_a(server.func_71265_f());
        server.forgeGetWorldMap().put(dim, world);
        server.markWorldsDirty();

        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));

        return world;
    }

    private static boolean canUnloadWorld(ServerWorld world)
    {
        return world.func_201675_m().func_186058_p() != DimensionType.field_223227_a_
                && world.func_217369_A().isEmpty()
                && world.func_217469_z().isEmpty()
                && !getData(world.func_201675_m().func_186058_p()).keepLoaded
                && !playerWorlds.contains(world);
    }

    /**
     * Queues a dimension to unload, if it can be unloaded.
     * @param world The world to unload
     */
    public static void unloadWorld(ServerWorld world)
    {
        if (world == null || !canUnloadWorld(world))
            return;

        int id = world.func_201675_m().func_186058_p().func_186068_a();
        if (unloadQueue.add(id))
            LOGGER.debug(DIMMGR,"Queueing dimension {} to unload", id);
    }

    @SuppressWarnings("deprecation")
    public static void unloadWorlds(MinecraftServer server, boolean checkLeaks)
    {
        IntIterator queueIterator = unloadQueue.iterator();
        while (queueIterator.hasNext())
        {
            int id = queueIterator.nextInt();
            DimensionType dim = DimensionType.func_186069_a(id);

            if (dim == null)
            {
                LOGGER.warn(DIMMGR, "Dimension with unknown type '{}' added to unload queue, removing", id);
                queueIterator.remove();
                continue;
            }

            Data dimension = dimensions.computeIfAbsent(id, k -> new Data());
            if (dimension.ticksWaited < ForgeConfig.SERVER.dimensionUnloadQueueDelay.get())
            {
                dimension.ticksWaited++;
                continue;
            }

            queueIterator.remove();

            ServerWorld w = server.forgeGetWorldMap().get(dim);

            dimension.ticksWaited = 0;
            // Don't unload the world if the status changed
            if (w == null || !canUnloadWorld(w))
            {
                LOGGER.debug(DIMMGR,"Aborting unload for dimension {} as status changed", id);
                continue;
            }
            try
            {
                w.func_217445_a(null, true, true);
            }
            catch (Exception e)
            {
                LOGGER.error(DIMMGR,"Caught an exception while saving all chunks:", e);
            }
            finally
            {
                MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
                LOGGER.debug(DIMMGR, "Unloading dimension {}", id);
                try {
                    w.close();
                } catch (IOException e) {
                    LOGGER.error("Exception closing the level", e);
                }
                server.forgeGetWorldMap().remove(dim);
                server.markWorldsDirty();
            }
        }

        if (checkLeaks)
        {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(server.forgeGetWorldMap().values());
            allWorlds.stream().map(System::identityHashCode).forEach(leakedWorlds::add);

            for (World w : allWorlds)
            {
                int hash = System.identityHashCode(w);
                int leakCount = leakedWorlds.count(hash);
                if (leakCount == 5)
                    LOGGER.debug(DIMMGR,"The world {} ({}) may have leaked: first encounter (5 occurrences).\n", Integer.toHexString(hash), w.func_72912_H().func_76065_j());
                else if (leakCount % 5 == 0)
                    LOGGER.debug(DIMMGR,"The world {} ({}) may have leaked: seen {} times.\n", Integer.toHexString(hash), w.func_72912_H().func_76065_j(), leakCount);
            }
        }
    }

    public static void writeRegistry(CompoundNBT data)
    {
        data.func_74768_a("version", 1);
        List<SavedEntry> list = new ArrayList<>();
        for (DimensionType type : REGISTRY)
            list.add(new SavedEntry(type));
        savedEntries.values().forEach(list::add);

        Collections.sort(list, (a, b) -> a.id - b.id);
        ListNBT lst = new ListNBT();
        list.forEach(e -> lst.add(e.write()));

        data.func_218657_a("entries", lst);
    }

    public static void readRegistry(CompoundNBT data)
    {
        int version = data.func_74762_e("version");
        if (version != 1)
            throw new IllegalStateException("Attempted to load world with unknown Dimension data format: " + version);

        LOGGER.debug(DIMMGR, "Reading Dimension Entries.");
        Map<ResourceLocation, DimensionType> vanilla = REGISTRY.func_201756_e().filter(DimensionType::isVanilla).collect(Collectors.toMap(REGISTRY::func_177774_c, v -> v));
        REGISTRY.clear();
        vanilla.forEach((key, value) -> {
            LOGGER.debug(DIMMGR, "Registering vanilla entry ID: {} Name: {} Value: {}", value.func_186068_a() + 1, key.toString(), value.toString());
            REGISTRY.func_218382_a(value.func_186068_a() + 1, key, value);
        });

        savedEntries.clear();

        @SuppressWarnings("unused")
        boolean error = false;
        ListNBT list = data.func_150295_c("entries", 10);
        for (int x = 0; x < list.size(); x++)
        {
            SavedEntry entry = new SavedEntry(list.func_150305_b(x));
            if (entry.type == null)
            {
                DimensionType type = REGISTRY.func_82594_a(entry.name);
                if (type == null)
                {
                    LOGGER.error(DIMMGR, "Vanilla entry '{}' id {} in save file not found in registry.", entry.name.toString(), entry.id);
                    error = true;
                    continue;
                }
                int id = REGISTRY.func_148757_b(type);
                if (id != entry.id)
                {
                    LOGGER.error(DIMMGR, "Vanilla entry '{}' id {} in save file has incorrect in {} in registry.", entry.name.toString(), entry.id, id);
                    error = true;
                    continue;
                }
            }
            else
            {
                ModDimension mod = ForgeRegistries.MOD_DIMENSIONS.getValue(entry.type);
                if (mod == null)
                {
                    LOGGER.error(DIMMGR, "Modded dimension entry '{}' id {} type {} in save file missing ModDimension.", entry.name.toString(), entry.id, entry.type.toString());
                    savedEntries.put(entry.name, entry);
                    continue;
                }
                registerDimensionInternal(entry.id, entry.name, mod, entry.data == null ? null : new PacketBuffer(Unpooled.wrappedBuffer(entry.data)), entry.skyLight());
            }
        }
    }

    public static void fireRegister()
    {
        MinecraftForge.EVENT_BUS.post(new RegisterDimensionsEvent(savedEntries));
        if (!savedEntries.isEmpty())
        {
            savedEntries.values().forEach(entry -> {
                LOGGER.warn(DIMMGR, "Missing Dimension Name: '{}' Id: {} Type: '{}", entry.name.toString(), entry.id, entry.type.toString());
            });
        }
    }

    @Deprecated //Forge: Internal use only.
    public static MutableRegistry<DimensionType> getRegistry()
    {
        return REGISTRY;
    }

    private static Data getData(DimensionType dim)
    {
        return dimensions.computeIfAbsent(dim.func_186068_a(), k -> new Data());
    }

    private static class Data
    {
        int ticksWaited = 0;
        boolean keepLoaded = false;
    }

    public static class SavedEntry
    {
        int id;
        ResourceLocation name;
        ResourceLocation type;
        byte[] data;
        boolean skyLight;

        public int getId()
        {
            return id;
        }

        public ResourceLocation getName()
        {
            return name;
        }

        @Nullable
        public ResourceLocation getType()
        {
            return type;
        }

        @Nullable
        public byte[] getData()
        {
            return data;
        }

        public boolean skyLight()
        {
            return this.skyLight;
        }

        private SavedEntry(CompoundNBT data)
        {
            this.id = data.func_74762_e("id");
            this.name = new ResourceLocation(data.func_74779_i("name"));
            this.type = data.func_150297_b("type", 8) ? new ResourceLocation(data.func_74779_i("type")) : null;
            this.data = data.func_150297_b("data", 7) ? data.func_74770_j("data") : null;
            this.skyLight = data.func_150297_b("sky_light", 99) ? data.func_74767_n("sky_light") : true;
        }

        private SavedEntry(DimensionType data)
        {
            this.id = REGISTRY.func_148757_b(data);
            this.name = REGISTRY.func_177774_c(data);
            if (data.getModType() != null)
                this.type = data.getModType().getRegistryName();
            if (data.getData() != null)
                this.data = data.getData().array();
            this.skyLight = data.func_218272_d();
        }

        private CompoundNBT write()
        {
            CompoundNBT ret = new CompoundNBT();
            ret.func_74768_a("id", id);
            ret.func_74778_a("name", name.toString());
            if (type != null)
                ret.func_74778_a("type", type.toString());
            if (data != null)
                ret.func_74773_a("data", data);
            ret.func_74757_a("sky_light", skyLight);
            return ret;
        }
    }

    private static class NoopChunkStatusListener implements IChunkStatusListener
    {
        @Override public void func_219509_a(ChunkPos center) { }
        @Override public void func_219508_a(ChunkPos p_219508_1_, ChunkStatus p_219508_2_) { }
        @Override public void func_219510_b() { }
    }

    public static boolean rebuildPlayerMap(PlayerList players, boolean changed)
    {
        playerWorlds = players.func_181057_v().stream().map(e -> e.field_70170_p).collect(Collectors.toSet());
        return changed;
    }
}
