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

import net.frozenblock.wilderwild.block.property.BubbleDirection;
import net.frozenblock.wilderwild.block.property.FlowerColor;
import net.frozenblock.wilderwild.block.property.GeyserStage;
import net.frozenblock.wilderwild.block.property.GeyserType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.lighting.LightEngine;

public final class WWBlockStateProperties {

	// Stone Chest
	public static final BooleanProperty ANCIENT = BooleanProperty.create("ancient");
	public static final BooleanProperty HAS_SCULK = BooleanProperty.create("has_sculk");

	private WWBlockStateProperties() {
		throw new UnsupportedOperationException("WWBlockStateProperties contains only static declarations.");
	}
}
