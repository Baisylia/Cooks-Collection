/*
 * Copyright 2024 FrozenBlock
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

package net.frozenblock.wilderwild.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.wilderwild.block.entity.StoneChestBlockEntity;
import net.frozenblock.wilderwild.config.WWEntityConfig;
import net.frozenblock.wilderwild.entity.Jellyfish;
import net.frozenblock.wilderwild.networking.packet.WWJellyfishStingPacket;
import net.frozenblock.wilderwild.networking.packet.WWLightningStrikePacket;
import net.frozenblock.wilderwild.networking.packet.WWScorchingFirePlacePacket;
import net.frozenblock.wilderwild.networking.packet.WWStoneChestLidPacket;
import net.frozenblock.wilderwild.networking.packet.WWWindPacket;
import net.frozenblock.wilderwild.registry.WWSounds;
import net.frozenblock.wilderwild.tag.WWBlockTags;
import net.frozenblock.wilderwild.wind.WWClientWindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class WWClientNetworking {

	public static void registerPacketReceivers() {
		receiveStoneChestLidPacket();
	}


	public static void receiveStoneChestLidPacket() {
		ClientPlayNetworking.registerGlobalReceiver(WWStoneChestLidPacket.PACKET_TYPE, (packet, ctx, sender) -> {
			ClientLevel clientLevel = ctx.clientLevel;
			if (clientLevel.getBlockEntity(packet.pos()) instanceof StoneChestBlockEntity stoneChestBlockEntity) {
				stoneChestBlockEntity.openProgress = packet.openProgress();
				stoneChestBlockEntity.highestLidPoint = packet.highestLidPoint();
				stoneChestBlockEntity.cooldownTicks = packet.cooldownTicks();
				stoneChestBlockEntity.stillLidTicks = packet.stillLidTicks();
				stoneChestBlockEntity.closing = packet.closing();
			}
		});
	}

}
