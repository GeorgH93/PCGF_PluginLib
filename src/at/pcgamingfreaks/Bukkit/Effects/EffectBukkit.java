package at.pcgamingfreaks.Bukkit.Effects;

import at.pcgamingfreaks.Bukkit.Reflection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class EffectBukkit extends EffectBase
{
	private final static Class<?> EntityPlayer = Reflection.getNMSClass("EntityPlayer");
	private final static Method sendPacket = Reflection.getMethod(Reflection.getNMSClass("PlayerConnection"), "sendPacket");
	private final static Field playerConnection = Reflection.getField(EntityPlayer, "playerConnection");

	protected void spawnParticle(Location location, double visibleRange, Object particle) throws IllegalAccessException, InvocationTargetException
	{
		if(particle == null || playerConnection == null || sendPacket == null) return;
		Object handle;
		for(Entity entity : location.getWorld().getEntities())
		{
			if(entity instanceof Player && entity.getLocation().getWorld().equals(location.getWorld()) && entity.getLocation().distance(location) < visibleRange)
			{
				handle = Reflection.getHandle(entity);
				if(handle != null && handle.getClass() == EntityPlayer)
				{
					sendPacket.invoke(playerConnection.get(handle), particle);
				}
			}
		}
	}
}