package com.sacredoil;

import net.runelite.client.config.*;

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

	@ConfigItem(
			keyName = "sanctitycheck",
			name = "Sanctity Notification",
			description = "Toggles Sanctity Notifications",
			position = 3
	)
	default boolean sanctityCheck() { return true; }

	@ConfigItem(
			keyName = "sanctitynotify",
			name = "Sanctity Level",
			description = "Notifies User when Sanctity reaches spcified level",
			position = 4
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int sanctityNotify() { return 100; }

	@ConfigItem(
			keyName = "sanctifydelay",
			name = "Sanctity Notification Delay",
			description = "Time until a notification is sent",
			position = 5
	)
	@Units(Units.MILLISECONDS)
	default int getSanctityDelay() { return 1000; }
}
