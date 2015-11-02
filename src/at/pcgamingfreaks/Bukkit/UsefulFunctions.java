package at.pcgamingfreaks.Bukkit;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of static functions that are may be useful for plugins.
 */
public class UsefulFunctions
{
	private static final Class<?> craftItemStackClazz = Reflection.getOBCClass("inventory.CraftItemStack"), nmsItemStackClazz = Reflection.getNMSClass("ItemStack"), nbtTagCompoundClazz = Reflection.getNMSClass("NBTTagCompound");
	private static final Method asNMSCopyMethod = Reflection.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class), saveNmsItemStackMethod = Reflection.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

	public static String convertItemStackToJson(ItemStack itemStack, Logger logger)
	{
		try
		{
			return saveNmsItemStackMethod.invoke(asNMSCopyMethod.invoke(null, itemStack), nbtTagCompoundClazz.newInstance()).toString();
		}
		catch (Throwable t)
		{
			logger.log(Level.SEVERE, "Failed to serialize itemstack to NMS item!", t);
		}
		return "";
	}
}