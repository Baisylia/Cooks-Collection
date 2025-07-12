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

package net.frozenblock.wilderwild.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import static net.frozenblock.wilderwild.WWConstants.MOD_ID;
import net.frozenblock.wilderwild.WWPreLoadConstants;

public final class WWBlockConfig {

	public static final Config<WWBlockConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			MOD_ID,
			WWBlockConfig.class,
			WWPreLoadConstants.configPath("block", true),
			JsonType.JSON5,
			null,
			null
		) {
			@Override
			public void onSave() throws Exception {
				super.onSave();
				this.onSync(null);
			}

		}
	);

	@CollapsibleObject
	public final BlockSoundsConfig blockSounds = new BlockSoundsConfig();

	@CollapsibleObject
	public final StoneChestConfig stoneChest = new StoneChestConfig();

	public static WWBlockConfig get() {
		return get(false);
	}

	public static WWBlockConfig get(boolean real) {
		if (real)
			return INSTANCE.instance();
		return INSTANCE.config();
	}

	public static WWBlockConfig getWithSync() {
		return INSTANCE.configWithSync();
	}

	public static class StoneChestConfig {
		@EntrySyncData("stoneChestTimer")
		public int stoneChestTimer = 100;

		public double getStoneChestTimer() {
			return ((double) this.stoneChestTimer) * 0.01D;
		}
	}

}
