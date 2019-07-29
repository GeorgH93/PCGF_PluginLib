/*
 *   Copyright (C) 2019 GeorgH93
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

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Message.Sender.TitleLocation;
import at.pcgamingfreaks.Message.Sender.TitleMetadataBase;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

/**
 * The TitleMetadata class holds the more detailed configuration options for the title sender.
 * With it it's possible to configure the type of the tile (title/subtitle).
 * And the times used for the title animations (fade-in/out and stay time).
 */
public final class TitleMetadata extends TitleMetadataBase implements ITitleMetadataBukkit
{
	private static final transient Enum<?> ENUM_TITLE = NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.TITLE");
	private static final transient Enum<?> ENUM_SUBTITLE = NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.SUBTITLE");
	private static final transient Enum<?> ENUM_ACTION_BAR = (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_11)) ? NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.ACTIONBAR") : ENUM_SUBTITLE;
	private static final transient Gson GSON = new Gson();

	private boolean subtitle = false; // workaround to keep old metadata jsons being parsed correctly

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 */
	public TitleMetadata(){}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn  Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay    Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	public TitleMetadata(int fadeIn, int fadeOut, int stay)
	{
		super(fadeIn, fadeOut, stay);
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param isSubtitle Defines if the title should be displayed as a title or a subtitle.
	 */
	@Deprecated
	public TitleMetadata(boolean isSubtitle)
	{
		super((isSubtitle) ? TitleLocation.SUBTITLE : TitleLocation.TITLE);
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn     Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut    Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay       Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 * @param isSubtitle Defines if the title should be displayed as a title or a subtitle (false = title, true = subtitle).
	 */
	@Deprecated
	public TitleMetadata(int fadeIn, int fadeOut, int stay, boolean isSubtitle)
	{
		super(fadeIn, fadeOut, stay, (isSubtitle) ? TitleLocation.SUBTITLE : TitleLocation.TITLE);
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn     Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut    Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay       Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 * @param location   Defines the display location of the title.
	 */
	public TitleMetadata(int fadeIn, int fadeOut, int stay, TitleLocation location)
	{
		super(fadeIn, fadeOut, stay, location);
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param location   Defines the display location of the title.
	 */
	public TitleMetadata(TitleLocation location)
	{
		super(location);
	}

	/**
	 * Gets the NMS type of the title. This will be used from the {@link TitleSender} class.
	 *
	 * @return The matching enum for the display type of the title. NMS!!!
	 */
	public @NotNull Enum<?> getTitleType()
	{
		switch(getLocation())
		{
			case TITLE: return ENUM_TITLE;
			case SUBTITLE: return ENUM_SUBTITLE;
			case ACTION_BAR: return ENUM_ACTION_BAR;
		}
		return ENUM_TITLE;
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
			TitleMetadata metadata = GSON.fromJson(json, TitleMetadata.class);
			if(metadata.subtitle) metadata.setSubtitle();
			return metadata;
		}
		catch(Exception ignored) {}
		return new TitleMetadata();
	}
}