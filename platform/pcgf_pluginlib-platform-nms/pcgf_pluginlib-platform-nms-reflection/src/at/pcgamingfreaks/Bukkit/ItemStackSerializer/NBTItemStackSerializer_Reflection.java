/*
 *   Copyright (C) 2023 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.ItemStackSerializer;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Bukkit.OBCReflection;
import at.pcgamingfreaks.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

@SuppressWarnings("ConstantConditions")
public final class NBTItemStackSerializer_Reflection implements ItemStackSerializer
{
	//region Reflection Variables
	private static final Class<?> CLASS_NBT_BASE                    = NmsReflector.INSTANCE.getNmsClass("NBTBase");
	private static final Class<?> CLASS_NBT_TAG_COMPOUND            = NmsReflector.INSTANCE.getNmsClass("NBTTagCompound");
	private static final Class<?> CLASS_NBT_TAG_LIST                = NmsReflector.INSTANCE.getNmsClass("NBTTagList");
	private static final Class<?> CLASS_NBT_COMPRESSED_STREAM_TOOLS = NmsReflector.INSTANCE.getNmsClass("NBTCompressedStreamTools");
	private static final Class<?> CLASS_NMS_ITEM_STACK              = NmsReflector.INSTANCE.getNmsClass("ItemStack");
	private static final Class<?> CLASS_CRAFT_ITEM_STACK            = OBCReflection.getOBCClass("inventory.CraftItemStack");
	private static final Constructor<?> CONSTRUCTOR_NBT_TAG_COMPOUND= Reflection.getConstructor(CLASS_NBT_TAG_COMPOUND);
	private static final Constructor<?> CONSTRUCTOR_NBT_TAG_LIST    = Reflection.getConstructor(CLASS_NBT_TAG_LIST);
	private static final Constructor<?> CONSTRUCTOR_NMS_ITEM_STACK  = (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_11) && MCVersion.isOlderThan(MCVersion.MC_1_13)) ? Reflection.getConstructor(CLASS_NMS_ITEM_STACK, CLASS_NBT_TAG_COMPOUND) : null;
	private static final Method METHOD_NBT_TAG_C_SET_INT            = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "setInt", String.class, int.class);
	private static final Method METHOD_NBT_TAG_C_SET_BYTE           = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "setByte", String.class, byte.class);
	private static final Method METHOD_NBT_TAG_C_SET_NBT_BASE       = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "set", String.class, CLASS_NBT_BASE);
	private static final Method METHOD_NBT_TAG_LIST_ADD             = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_LIST, "add", CLASS_NBT_BASE) : NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_LIST, "b", int.class, CLASS_NBT_BASE);
	private static final Method METHOD_NBT_COMP_STEAM_A             = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_COMPRESSED_STREAM_TOOLS, "a", CLASS_NBT_TAG_COMPOUND, OutputStream.class);
	private static final Method METHOD_NBT_TAG_C_SET2               = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "set", String.class, NmsReflector.INSTANCE.getNmsClass("NBTBase"));
	private static final Method METHOD_SAVE                         = NmsReflector.INSTANCE.getNmsMethod(CLASS_NMS_ITEM_STACK, "save", CLASS_NBT_TAG_COMPOUND);
	private static final Method METHOD_AS_NMS_COPY                  = Reflection.getMethod(CLASS_CRAFT_ITEM_STACK, "asNMSCopy", ItemStack.class);
	private static final Method METHOD_GET_INT                      = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "getInt", String.class);
	private static final Method METHOD_GET_BYTE                     = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "getByte", String.class);
	private static final Method METHOD_HAS_KEY_OF_TYPE              = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "hasKeyOfType", String.class, int.class);
	private static final Method METHOD_GET_COMPOUND                 = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "getCompound", String.class);
	private static final Method METHOD_GET_COMPOUND_LIST            = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_COMPOUND, "getList", String.class, int.class);
	private static final Method METHOD_GET_COMPOUND_FROM_LIST       = (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13)) ? NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_LIST, "getCompound", int.class) : NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_LIST, "get", int.class);
	private static final Method METHOD_CREATE_STACK                 = (MCVersion.isOlderThan(MCVersion.MC_1_11)) ? NmsReflector.INSTANCE.getNmsMethod(CLASS_NMS_ITEM_STACK, "createStack", CLASS_NBT_TAG_COMPOUND) : (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13)) ? NmsReflector.INSTANCE.getNmsMethod(CLASS_NMS_ITEM_STACK, "a", CLASS_NBT_TAG_COMPOUND) : null;
	private static final Method METHOD_AS_BUKKIT_COPY               = Reflection.getMethod(CLASS_CRAFT_ITEM_STACK, "asBukkitCopy", CLASS_NMS_ITEM_STACK);
	private static final Method METHOD_NBT_COMP_STREAM_A2           = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_COMPRESSED_STREAM_TOOLS, "a", InputStream.class);
	private static final Method METHOD_NBT_COMP_STREAM_FROM_DATA_INPUT = MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R3) ? null : NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_COMPRESSED_STREAM_TOOLS, "a", DataInput.class);
	private static final Method METHOD_NBT_TAG_LIST_SIZE            = NmsReflector.INSTANCE.getNmsMethod(CLASS_NBT_TAG_LIST, "size");
	private static final Method METHOD_DATA_FIXER_UPDATE;
	private static final Enum<?> ENUM_DATA_FIX_TYPE;
	//endregion


	private static final Object DATA_FIXER;
	private static final int CURRENT_DATA_VERSION;

	private static final String KEY_INVENTORY = "Inventory", KEY_DATA_VERSION = "DataVersion";

	static
	{
		Object dataFixer = null;
		Method fixerUpdate = null;
		Enum<?> fixType = null;
		if(MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13))
		{
			try
			{
				final Class<?> classDataFixer = Reflection.getClass("com.mojang.datafixers.DataFixer");
				if(MCVersion.isOlderThan(MCVersion.MC_1_17))
					dataFixer = NmsReflector.INSTANCE.getNmsField("MinecraftServer", "dataConverterManager").get(OBCReflection.getOBCMethod("CraftServer", "getServer").invoke(Bukkit.getServer()));
				else
				{
					dataFixer = NmsReflector.INSTANCE.getNmsMethod("MinecraftServer", "getDataFixer").invoke(OBCReflection.getOBCMethod("CraftServer", "getServer").invoke(Bukkit.getServer()));
					if (!classDataFixer.isAssignableFrom(dataFixer.getClass()))
					{
						dataFixer = Reflection.getMethodFromReturnType(NmsReflector.INSTANCE.getNmsClass("MinecraftServer"), classDataFixer).invoke(OBCReflection.getOBCMethod("CraftServer", "getServer").invoke(Bukkit.getServer()));
					}
				}
				if(MCVersion.isOlderThan(MCVersion.MC_1_14))
				{
					fixerUpdate = NmsReflector.INSTANCE.getNmsMethod("GameProfileSerializer", "a", classDataFixer, Reflection.getClass("com.mojang.datafixers.DSL$TypeReference"), CLASS_NBT_TAG_COMPOUND, int.class);
					fixType = Reflection.getEnum(Reflection.getClass("com.mojang.datafixers.DataFixTypes"), "PLAYER");
				}
				else if (MCVersion.isOlderThan(MCVersion.MC_NMS_1_19_R3))
				{
					fixerUpdate = NmsReflector.INSTANCE.getNmsMethod("GameProfileSerializer", "a", classDataFixer, NmsReflector.INSTANCE.getNmsClass("DataFixTypes"), CLASS_NBT_TAG_COMPOUND, int.class);
					fixType = NmsReflector.INSTANCE.getNmsEnum("DataFixTypes", "PLAYER");
				}
				else
				{
					final Class<?> fixTypes = NmsReflector.INSTANCE.getNmsClass("DataFixTypes");
					fixerUpdate = Reflection.getMethodFromReturnType(fixTypes, CLASS_NBT_TAG_COMPOUND, classDataFixer, CLASS_NBT_TAG_COMPOUND, int.class);
					fixType = Reflection.getEnum(fixTypes, "PLAYER");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		DATA_FIXER = dataFixer;
		METHOD_DATA_FIXER_UPDATE = fixerUpdate;
		ENUM_DATA_FIX_TYPE = fixType;

		//region get data version
		// Data version can be found in: net.minecraft.server.<version>.EntityHuman.java (search for "DataVersion")
		if(MCVersion.isOlderOrEqualThan(MCVersion.MC_NMS_1_9_R1)) CURRENT_DATA_VERSION = 176;
		else if(MCVersion.is(MCVersion.MC_NMS_1_9_R2)) CURRENT_DATA_VERSION = 184;
		else if(MCVersion.is(MCVersion.MC_NMS_1_10_R1)) CURRENT_DATA_VERSION = 512;
		else if(MCVersion.is(MCVersion.MC_NMS_1_11_R1)) CURRENT_DATA_VERSION = 922;
		else if(MCVersion.is(MCVersion.MC_NMS_1_12_R1)) CURRENT_DATA_VERSION = 1343;
		else if(MCVersion.is(MCVersion.MC_NMS_1_13_R1)) CURRENT_DATA_VERSION = 1519;
		else if(MCVersion.is(MCVersion.MC_NMS_1_13_R2)) CURRENT_DATA_VERSION = 1631;
		else if(MCVersion.isNewerOrEqualThan(MCVersion.MC_NMS_1_14_R1))
		{
			String getGameVersion = MCVersion.isOlderThan(MCVersion.MC_NMS_1_15_R1) ? "a" : "getGameVersion";
			int version = -1;
			try
			{
				Method methodSharedConstantsGetGameVersion = NmsReflector.INSTANCE.getNmsMethod("SharedConstants", getGameVersion);
				Object gameVersion = methodSharedConstantsGetGameVersion.invoke(null);
				Method methodGameVersionGetWorldVersion;
				if (MCVersion.isOlderThan(MCVersion.MC_NMS_1_19_R3))
				{
					methodGameVersionGetWorldVersion = Reflection.getMethod(Reflection.getClass("com.mojang.bridge.game.GameVersion"), "getWorldVersion");
					version = (int) methodGameVersionGetWorldVersion.invoke(gameVersion);
				}
				else
				{
					methodGameVersionGetWorldVersion = NmsReflector.INSTANCE.getNmsMethod("WorldVersion", "getWorldVersion");
					Object dataVersion = methodGameVersionGetWorldVersion.invoke(gameVersion);
					version = (int) NmsReflector.INSTANCE.getNmsMethod("DataVersion", "getVersion").invoke(dataVersion);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			CURRENT_DATA_VERSION = version;
		}
		else CURRENT_DATA_VERSION = -1;
		//endregion
	}

	private Logger logger = null;

	@Override
	public void setLogger(@Nullable Logger logger)
	{
		this.logger = logger;
		if(logger == null) return;
		if(CLASS_NBT_TAG_COMPOUND == null || METHOD_NBT_TAG_C_SET2 == null || METHOD_NBT_TAG_C_SET_INT == null || METHOD_SAVE == null || METHOD_AS_NMS_COPY == null || METHOD_NBT_COMP_STEAM_A == null ||
				METHOD_NBT_COMP_STREAM_A2 == null || METHOD_GET_INT == null || METHOD_HAS_KEY_OF_TYPE == null || METHOD_AS_BUKKIT_COPY == null || METHOD_GET_COMPOUND == null ||
				(METHOD_CREATE_STACK == null && CONSTRUCTOR_NMS_ITEM_STACK == null))
		{
			logger.warning("It seems like the system wasn't able to find some Bukkit/Minecraft classes and/or methods.\n" +
					               "Is the plugin up-to-date and compatible with the used server version?\nBukkit Version: " + Bukkit.getVersion());
		}
	}

	private void convertOldFormatToNew(Object localNBTTagCompound, int size) throws Exception
	{
		Object nbtItemList = CONSTRUCTOR_NBT_TAG_LIST.newInstance();
		METHOD_NBT_TAG_C_SET_NBT_BASE.invoke(localNBTTagCompound, KEY_INVENTORY, nbtItemList);
		for(int i = 0; i < size; i++)
		{
			if((boolean) METHOD_HAS_KEY_OF_TYPE.invoke(localNBTTagCompound, String.valueOf(i), 10))
			{
				Object itemNBTCompound = METHOD_GET_COMPOUND.invoke(localNBTTagCompound, String.valueOf(i));
				METHOD_NBT_TAG_C_SET_BYTE.invoke(itemNBTCompound, "Slot", (byte) i);
				METHOD_NBT_TAG_LIST_ADD.invoke(nbtItemList, itemNBTCompound);
			}
		}
	}

	/**
	 * Deserialize a serialized byte array to an ItemStack array.
	 *
	 * @param data The data that should get deserialized.
	 * @return The deserialized ItemStack array.
	 */
	@Override
	public ItemStack[] deserialize(byte[] data)
	{
		if(data != null)
		{
			try
			{
				Object localNBTTagCompound = METHOD_NBT_COMP_STREAM_A2.invoke(null, new ByteArrayInputStream(data));
				if (METHOD_NBT_COMP_STREAM_FROM_DATA_INPUT != null) localNBTTagCompound = METHOD_NBT_COMP_STREAM_FROM_DATA_INPUT.invoke(null, localNBTTagCompound);
				int size = (int) METHOD_GET_INT.invoke(localNBTTagCompound, "size"), dataVersion = CURRENT_DATA_VERSION;
				if((boolean) METHOD_HAS_KEY_OF_TYPE.invoke(localNBTTagCompound, KEY_DATA_VERSION, 3)) dataVersion = (int) METHOD_GET_INT.invoke(localNBTTagCompound, KEY_DATA_VERSION);
				if (dataVersion == MCVersion.MC_1_19_4.getProtocolVersion()) dataVersion = 3337;
				if(!(boolean) METHOD_HAS_KEY_OF_TYPE.invoke(localNBTTagCompound, KEY_INVENTORY, 9)) convertOldFormatToNew(localNBTTagCompound, size);
				if(MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) && dataVersion < CURRENT_DATA_VERSION)
				{ // MC 1.13 has moved the data-format update code out of the deserializer, so it needs to be done manually
					if (MCVersion.isOlderThan(MCVersion.MC_NMS_1_19_R3))
					{
						localNBTTagCompound = METHOD_DATA_FIXER_UPDATE.invoke(null, DATA_FIXER, ENUM_DATA_FIX_TYPE, localNBTTagCompound, dataVersion);
					}
					else
					{
						localNBTTagCompound = METHOD_DATA_FIXER_UPDATE.invoke(ENUM_DATA_FIX_TYPE, DATA_FIXER, localNBTTagCompound, dataVersion);
					}
				}
				ItemStack[] its = new ItemStack[size];
				Object nbtItemList = METHOD_GET_COMPOUND_LIST.invoke(localNBTTagCompound, KEY_INVENTORY, 10);
				int listSize = (int) METHOD_NBT_TAG_LIST_SIZE.invoke(nbtItemList);
				for(int i = 0; i < listSize; i++)
				{
					Object compound = METHOD_GET_COMPOUND_FROM_LIST.invoke(nbtItemList, i);
					byte slot = (byte) METHOD_GET_BYTE.invoke(compound, "Slot");
					try
					{
						its[slot] = deserializeNBTCompound(compound);
					}
					catch(Exception ignored)
					{
						if(logger != null) logger.warning("Failed to restore item on slot " + i + " with json:\n" + compound.toString());
					}
				}
				return its;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings({ "ConstantConditions", "Duplicates" })
	private static @Nullable ItemStack deserializeNBTCompound(@NotNull Object compound) throws Exception
	{
		Object nmsItemStack;
		if(MCVersion.isNewerOrEqualThan(MCVersion.MC_1_11) && MCVersion.isOlderThan(MCVersion.MC_1_13))
		{
			nmsItemStack = CONSTRUCTOR_NMS_ITEM_STACK.newInstance(compound);
		}
		else
		{
			nmsItemStack = METHOD_CREATE_STACK.invoke(null, compound);
		}
		return (nmsItemStack != null) ? (ItemStack) METHOD_AS_BUKKIT_COPY.invoke(null, nmsItemStack) : null;
	}

	/**
	 * Serializes a ItemStack array to a byte array.
	 *
	 * @param itemStacks The ItemStacks that should be serialized.
	 * @return Serialized ItemsStacks as byte array. Null if serialization failed.
	 */
	@Override
	public byte[] serialize(ItemStack[] itemStacks)
	{
		byte[] ba = null;
		if(itemStacks != null)
		{
			try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream))
			{
				//noinspection ConstantConditions
				Object localNBTTagCompound = CONSTRUCTOR_NBT_TAG_COMPOUND.newInstance();
				METHOD_NBT_TAG_C_SET_INT.invoke(localNBTTagCompound, "size", itemStacks.length);
				METHOD_NBT_TAG_C_SET_INT.invoke(localNBTTagCompound, KEY_DATA_VERSION, CURRENT_DATA_VERSION);
				Object nbtItemList = CONSTRUCTOR_NBT_TAG_LIST.newInstance();
				METHOD_NBT_TAG_C_SET_NBT_BASE.invoke(localNBTTagCompound, KEY_INVENTORY, nbtItemList);
				for(int i = 0, used = -1; i < itemStacks.length; i++)
				{
					if(itemStacks[i] != null)
					{
						Object itemNBTCompound = CONSTRUCTOR_NBT_TAG_COMPOUND.newInstance();
						METHOD_NBT_TAG_C_SET_BYTE.invoke(itemNBTCompound, "Slot", (byte) i);
						METHOD_SAVE.invoke(METHOD_AS_NMS_COPY.invoke(null, itemStacks[i]), itemNBTCompound);
						if(MCVersion.isOlderThan(MCVersion.MC_1_14))
							METHOD_NBT_TAG_LIST_ADD.invoke(nbtItemList, itemNBTCompound);
						else
							METHOD_NBT_TAG_LIST_ADD.invoke(nbtItemList, ++used, itemNBTCompound);
					}
				}
				METHOD_NBT_COMP_STEAM_A.invoke(null, localNBTTagCompound, dataOutputStream);
				dataOutputStream.flush();
				ba = byteArrayOutputStream.toByteArray();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ba;
	}

	@Override
	public boolean checkIsMCVersionCompatible()
	{
		return isMCVersionCompatible();
	}

	public static boolean isMCVersionCompatible()
	{
		return MCVersion.isNewerOrEqualThan(MCVersion.MC_1_7) && MCVersion.isOlderOrEqualThan(MCVersion.MC_NMS_1_20_R3);
	}
}
