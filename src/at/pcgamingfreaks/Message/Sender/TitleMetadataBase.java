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

package at.pcgamingfreaks.Message.Sender;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

public abstract class TitleMetadataBase implements ITitleMetadata
{
	@Getter @Setter private int fadeIn = 5, fadeOut = 50, stay = 5;
	@Getter @Setter @NotNull private TitleLocation location = TitleLocation.TITLE;

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 */
	public TitleMetadataBase() {}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn  Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay    Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	public TitleMetadataBase(int fadeIn, int fadeOut, int stay)
	{
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		this.stay = stay;
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param fadeIn     Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 * @param fadeOut    Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 * @param stay       Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 * @param location   Defines the display location of the title.
	 */
	public TitleMetadataBase(int fadeIn, int fadeOut, int stay, @NotNull TitleLocation location)
	{
		this(fadeIn, fadeOut, stay);
		this.location = location;
	}

	/**
	 * Creates a new TitleMetadata object to configure how the title will be displayed.
	 *
	 * @param location   Defines the display location of the title.
	 */
	public TitleMetadataBase(@NotNull TitleLocation location)
	{
		this.location = location;
	}

	protected static <T extends TitleMetadataBase> @NotNull T parseJson(@NotNull T metadata, @NotNull String json)
	{
		try
		{
			JsonObject object = new JsonParser().parse(json).getAsJsonObject();
			object.entrySet().forEach(e -> {
				switch(e.getKey().toLowerCase(Locale.ROOT))
				{
					case "fadein": metadata.setFadeIn(e.getValue().getAsInt()); break;
					case "stay": metadata.setStay(e.getValue().getAsInt()); break;
					case "fadeout": metadata.setFadeOut(e.getValue().getAsInt()); break;
					case "subtitle": if(e.getValue().getAsBoolean()) metadata.setSubtitle(); break;
					case "location": metadata.setLocation(TitleLocation.valueOf(e.getValue().getAsString().toUpperCase(Locale.ROOT).replace("ACTIONBAR", "ACTION_BAR").replace("SUB_TITLE", "SUBTITLE")));
				}
			});

			return metadata;
		}
		catch(Exception ignored) {}
		return metadata;
	}
}