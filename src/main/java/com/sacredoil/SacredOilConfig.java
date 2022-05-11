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
		description = "Configures if Flamtaer Bracelet Counter is enabled.",
		position = 1
	)
	default boolean braceletCounter()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bracletnotify",
			name = "Flamtaer Bracelet Notification",
			description = "Configures if Flamtaer Braclet notification is enabled.",
			position = 2
	)
	default boolean braceletNotify() { return true; }
}
