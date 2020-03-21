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

package net.minecraftforge.server.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;
import java.util.function.Function;

public class CommandSetDimension
{
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.setdim.invalid.entity"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TranslationTextComponent("commands.forge.setdim.invalid.dim", dim));
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.func_197057_a("setdimension")
            .requires(cs->cs.func_197034_c(2)) //permission
            .then(Commands.func_197056_a("targets", EntityArgument.func_197093_b())
                .then(Commands.func_197056_a("dim", DimensionArgument.func_212595_a())
                    .then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgument.func_197087_c(ctx, "targets"), DimensionArgument.func_212592_a(ctx, "dim"), BlockPosArgument.func_197274_b(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx.getSource(), EntityArgument.func_197087_c(ctx, "targets"), DimensionArgument.func_212592_a(ctx, "dim"), new BlockPos(ctx.getSource().func_197036_d())))
                )
            );
    }
    
    private static int execute(CommandSource sender, Collection<? extends Entity> entities, DimensionType dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(e -> !canEntityTeleport(e));
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        //if (!DimensionManager.isDimensionRegistered(dim))
        //    throw INVALID_DIMENSION.create(dim);

        entities.stream().filter(e -> e.field_71093_bK == dim).forEach(e -> sender.func_197030_a(new TranslationTextComponent("commands.forge.setdim.invalid.nochange", e.func_145748_c_().func_150254_d(), dim), true));
        entities.stream().filter(e -> e.field_71093_bK != dim).forEach(e ->  e.changeDimension(dim, new ITeleporter()
        {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
            {
                Entity repositionedEntity = repositionEntity.apply(false);
                repositionedEntity.func_70634_a(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
                return repositionedEntity;
            }
        }));

        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.func_184218_aH() && !entity.func_184207_aI() && entity.func_184222_aU();
    }
}
