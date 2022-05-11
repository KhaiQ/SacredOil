package com.sacredoil;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface SacredOilConfig extends Config
{
	@ConfigItem(
		keyName = "braceletcounter",
		name = "Flamtaer Bracelet Counter",
		description = "Displays Flamtaer Bracelet Charge Counter",
		position = 1
	)
	default boolean braceletCounter()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bracletnotify",
			name = "Flamtaer Bracelet Notification",
			description = "Notifies User when Flamtaer Bracelet breaks",
			position = 2
	)
	default boolean braceletNotify() { return true; }
}
