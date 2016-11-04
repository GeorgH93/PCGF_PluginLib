/*
 *   Copyright (C) 2016 GeorgH93
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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.NMSReflection;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

/**
 * The TitleMetadata class holds the more detailed configuration options for the title sender.
 * With it it's possible to configure the type of the tile (title/subtitle).
 * And the times used for the title animations (fade-in/out and stay time).
 */
public final class TitleMetadata
{
	private static final transient Enum<?> ENUM_TITLE = NMSReflection.getNMSEnum("PacketPlayOutTitle$EnumTitleAction.TITLE");
	private static final transient Enum<?> ENUM_SUBTITLE = NMSReflection.getNMSEnum("PacketPlayOutTitle$EnumTitleAction.SUBTITLE");
	private static final transient Gson GSON = new Gson();

	private int fadeIn = 5, fadeOut = 50, stay = 5;
	private boolean subtitle = false;

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 */
	public TitleMetadata() {}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn  Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay    Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	public TitleMetadata(int fadeIn, int fadeOut, int stay)
	{
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		this.stay = stay;
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param isSubtitle Defines if the title should be displayed as a title or a subtitle.
	 */
	public TitleMetadata(boolean isSubtitle)
	{
		subtitle = isSubtitle;
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn     Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut    Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay       Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 * @param isSubtitle Defines if the title should be displayed as a title or a subtitle (false = title, true = subtitle).
	 */
	public TitleMetadata(int fadeIn, int fadeOut, int stay, boolean isSubtitle)
	{
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		this.stay = stay;
		subtitle = isSubtitle;
	}

	/**
	 * Sets the fade-in time of the title.
	 *
	 * @param fadeIn Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 */
	public void setFadeIn(int fadeIn)
	{
		this.fadeIn = fadeIn;
	}

	/**
	 * Sets the fade-out time of the title.
	 *
	 * @param fadeOut Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 */
	public void setFadeOut(int fadeOut)
	{
		this.fadeOut = fadeOut;
	}

	/**
	 * Sets the stay time of the title.
	 *
	 * @param stay Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	public void setStay(int stay)
	{
		this.stay = stay;
	}

	/**
	 * Sets the display type of the title.
	 *
	 * @param subtitle Defines if the title should be displayed as a title or a subtitle (false = title, true = subtitle).
	 */
	public void setSubtitle(boolean subtitle)
	{
		this.subtitle = subtitle;
	}

	/**
	 * Gets the fade-in time of the title.
	 *
	 * @return How long the title will fade-in. Value in ticks (1/20 sec).
	 */
	public int getFadeIn()
	{
		return fadeIn;
	}

	/**
	 * Gets the fade-out time of the title.
	 *
	 * @return How long the title will fade-out. Value in ticks (1/20 sec).
	 */
	public int getFadeOut()
	{
		return fadeOut;
	}

	/**
	 * Gets the stay time of the title.
	 *
	 * @return How long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	public int getStay()
	{
		return stay;
	}

	/**
	 * Sets the display type of the title.
	 *
	 * @return True if the title should be displayed as a subtitle. False if it should be displayed as a title.
	 */
	public boolean isSubtitle()
	{
		return subtitle;
	}

	/**
	 * Gets the NMS type of the title. This will be used from the {@link TitleSender} class.
	 *
	 * @return The matching enum for the display type of the title. NMS!!!
	 */
	public @NotNull Enum<?> getTitleType()
	{
		return (isSubtitle()) ? ENUM_SUBTITLE : ENUM_TITLE;

	}

	/**
	 * Generates a Metadata object from a JSON. This will be used from the {@link at.pcgamingfreaks.Bukkit.Language} class to load the metadata from the language file.
	 *
	 * @param json The JSON to get deserialized and used to create a new TitleMetadata object.
	 * @return The new TitleMetadata object. Or a default object if the JSON couldn't get deserialized.
	 */
	public static @NotNull TitleMetadata fromJson(@NotNull String json)
	{
		try
		{
			return GSON.fromJson(json, TitleMetadata.class);
		}
		catch(Exception ignored) {}
		return new TitleMetadata();
	}
}