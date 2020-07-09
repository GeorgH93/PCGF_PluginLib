/*
 * Copyright (C) 2017 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.TestClasses;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

public class TestBlock implements Block
{
	public Material material = Material.AIR;

	@Override
	public byte getData() { return 120; }

	@Override
	public Block getRelative(int i, int i1, int i2) { return null; }

	@Override
	public Block getRelative(BlockFace blockFace) { return null; }

	@Override
	public Block getRelative(BlockFace blockFace, int i) { return null; }

	@Override
	public Material getType() { return material; }

	@Override
	public int getTypeId() { return 0; }

	@Override
	public byte getLightLevel() { return 0; }

	@Override
	public byte getLightFromSky() { return 0; }

	@Override
	public byte getLightFromBlocks() { return 0; }

	@Override
	public World getWorld() { return null; }

	@Override
	public int getX() { return 0; }

	@Override
	public int getY() { return 0; }

	@Override
	public int getZ() { return 0; }

	@Override
	public Location getLocation() { return null; }

	@Override
	public Location getLocation(Location location) { return null; }

	@Override
	public Chunk getChunk() { return null; }

	@Override
	public void setData(byte b) {}

	@Override
	public void setData(byte b, boolean b1) {}

	@Override
	public void setType(Material material) {}

	@Override
	public void setType(Material material, boolean b) {}

	@Override
	public boolean setTypeId(int i) { return false; }

	@Override
	public boolean setTypeId(int i, boolean b) { return false; }

	@Override
	public boolean setTypeIdAndData(int i, byte b, boolean b1) { return false; }

	@Override
	public BlockFace getFace(Block block) { return null; }

	@Override
	public BlockState getState() { return null; }

	@Override
	public Biome getBiome() { return null; }

	@Override
	public void setBiome(Biome biome) {}

	@Override
	public boolean isBlockPowered() { return false; }

	@Override
	public boolean isBlockIndirectlyPowered() { return false; }

	@Override
	public boolean isBlockFacePowered(BlockFace blockFace) { return false; }

	@Override
	public boolean isBlockFaceIndirectlyPowered(BlockFace blockFace) { return false; }

	@Override
	public int getBlockPower(BlockFace blockFace) { return 0; }

	@Override
	public int getBlockPower() { return 0; }

	@Override
	public boolean isEmpty() { return false; }

	@Override
	public boolean isLiquid() { return false; }

	@Override
	public double getTemperature() { return 0; }

	@Override
	public double getHumidity() { return 0; }

	@Override
	public PistonMoveReaction getPistonMoveReaction() { return null; }

	@Override
	public boolean breakNaturally() { return false; }

	@Override
	public boolean breakNaturally(ItemStack itemStack) { return false; }

	@Override
	public Collection<ItemStack> getDrops() { return null; }

	@Override
	public Collection<ItemStack> getDrops(ItemStack itemStack) { return null; }

	@Override
	public void setMetadata(String s, MetadataValue metadataValue) {}

	@Override
	public List<MetadataValue> getMetadata(String s) { return null; }

	@Override
	public boolean hasMetadata(String s) { return false; }

	@Override
	public void removeMetadata(String s, Plugin plugin) {}
}