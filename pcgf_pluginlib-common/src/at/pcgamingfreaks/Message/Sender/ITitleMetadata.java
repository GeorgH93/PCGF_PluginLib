/*
 *   Copyright (C) 2020 GeorgH93
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

package at.pcgamingfreaks.Message.Sender;

public interface ITitleMetadata extends IMetadata
{
	/**
	 * Sets the fade-in time of the title.
	 *
	 * @param fadeIn Defines how long the title will fade-in. Value in ticks (1/20 sec).
	 */
	void setFadeIn(int fadeIn);

	/**
	 * Sets the fade-out time of the title.
	 *
	 * @param fadeOut Defines how long the title will fade-out. Value in ticks (1/20 sec).
	 */
	void setFadeOut(int fadeOut);

	/**
	 * Sets the stay time of the title.
	 *
	 * @param stay Defines how long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	void setStay(int stay);

	/**
	 * Sets the display location of the title.
	 *
	 * @param location The display location of the title.
	 */
	void setLocation(TitleLocation location);

	/**
	 * Sets the display location of the title to subtitle.
	 */
	default void setSubtitle()
	{
		setLocation(TitleLocation.SUBTITLE);
	}

	/**
	 * Sets the display location of the title to the action-bar.
	 */
	default void setActionBar()
	{
		setLocation(TitleLocation.ACTION_BAR);
	}

	/**
	 * Sets the display location of the title to full size title.
	 */
	default void setTitle()
	{
		setLocation(TitleLocation.TITLE);
	}

	/**
	 * Gets the fade-in time of the title.
	 *
	 * @return How long the title will fade-in. Value in ticks (1/20 sec).
	 */
	int getFadeIn();

	/**
	 * Gets the fade-out time of the title.
	 *
	 * @return How long the title will fade-out. Value in ticks (1/20 sec).
	 */
	int getFadeOut();

	/**
	 * Gets the stay time of the title.
	 *
	 * @return How long the title will stay on the screen of the player. Value in ticks (1/20 sec).
	 */
	int getStay();

	/**
	 * Gets the display location of the title.
	 *
	 * @return The display location of the title.
	 */
	TitleLocation getLocation();

	/**
	 * Checks if the display type of the title is subtitle.
	 *
	 * @return True if the title should be displayed as a subtitle. False if it should be displayed as a title or action-bar.
	 */
	default boolean isSubtitle()
	{
		return getLocation() == TitleLocation.SUBTITLE;
	}

	/**
	 * Checks if the display type of the title is action-bar.
	 *
	 * @return True if the title should be displayed in the action-bar. False if it should be displayed as a title.
	 */
	default boolean isActionBar()
	{
		return getLocation() == TitleLocation.ACTION_BAR;
	}

	default boolean isTitle()
	{
		return getLocation() == TitleLocation.TITLE;
	}
}