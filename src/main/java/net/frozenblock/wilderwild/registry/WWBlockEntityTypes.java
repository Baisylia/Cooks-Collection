/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of Wilder Wild.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.wilderwild.registry;

import com.mojang.datafixers.types.Type;
import net.frozenblock.wilderwild.WWConstants;
import net.frozenblock.wilderwild.block.entity.StoneChestBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public final class WWBlockEntityTypes {
	private WWBlockEntityTypes() {
		throw new UnsupportedOperationException("WWBlockEntityTypes contains only static declarations.");
	}

	public static void register() {
		WWConstants.logWithModId("Registering BlockEntities for", WWConstants.UNSTABLE_LOGGING);
	}

	@NotNull
	private static <T extends BlockEntity> BlockEntityType<T> register(@NotNull String path, BlockEntityType.@NotNull Builder<T> builder) {
		Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, WWConstants.string(path));
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, WWConstants.id(path), builder.build(type));
	}

	public static final BlockEntityType<StoneChestBlockEntity> STONE_CHEST = register("stone_chest", BlockEntityType.Builder.of(StoneChestBlockEntity::new, WWBlocks.STONE_CHEST));

}
