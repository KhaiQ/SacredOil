package com.sacredoil;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@PluginDescriptor(
	name = "Sacred Oil"
)
public class SacredOilPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private SacredOilConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Notifier notifier;

	private SacredOil counterbox;

	private Instant sanctityThresh;

	private int charge = 80;

	private boolean startTime = false;

	private boolean sancNotiSent = false;

	// Checks glove equipment slot when starting plugin
	@Override
	protected void startUp() {
		clientThread.invokeLater(() -> {
			final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);

			if (container != null)	{
				checkHand(container.getItems());
			}
		});
	}

	// Removes the counter when stopping plugin
	@Override
	protected void shutDown() {
		removeInfobox();
	}

	// Checks glove equipment any time the plugin is enabled
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		startUp();
	}

	// Updates value on counter based off of game messages
	// If Flamtaer Bracelet breaks while Windows is not focused on RuneLite,
	// a Windows Notification will be sent to inform the user.
	// NOTE: For charges 80-2, messages are catagorized as SPAM,
	// 		 charges 1-0 are considered GAMEMESSAGE
	@Subscribe
	public void onChatMessage(ChatMessage event) {
		final String prompt = "Your Flamtaer bracelet helps you build the temple quicker.";
		if((event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) && event.getMessage().contains(prompt)) {

			final String num = event.getMessage().replaceAll("[^0-9]", "");
			charge = Integer.parseInt(num.replaceAll("^0+(?!$)", ""));

			if(charge == 0) {
				charge = 80;
				if(config.braceletNotify())
					notifier.notify("Your Flamtaer Bracelet has broken!");
			}
			updateInfobox(charge);
		}
	}

	// Adds/removes Flamtaer Bracelet counter on/off based on config state
	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if(config.braceletCounter()) {
			startUp();
			return;
		}
		removeInfobox();
	}

	// Monitors sanctity level every game tick
	@Subscribe
	public void onGameTick(GameTick event) {
		sendSancNoti();
	}

	// Checks if glove equipment matches Flamtaer Bracelet Id
	// If true, the counter is enabled
	// If false, the counter is disabled
	private void checkHand(final Item[] items) {
		if(items[EquipmentInventorySlot.GLOVES.getSlotIdx()].getId() == 21180) {
			updateInfobox(charge);
			return;
		}
		removeInfobox();
	}

	// Updates the value of the counter
	private void updateInfobox(int charge) {
		removeInfobox();
		if(config.braceletCounter()) {
			final BufferedImage image = itemManager.getImage(ItemID.FLAMTAER_BRACELET, charge, false);
			counterbox = new SacredOil(this, 21180, charge, "Charges", image);
			infoBoxManager.addInfoBox((counterbox));
		}
	}

	// Removes the counter
	private void removeInfobox() {
		infoBoxManager.removeInfoBox(counterbox);
	}


	// Checks the player's location and proceeds to scan sanctity widget
	// Once sanctity threshold is surpassed, a notification is sent after
	// a User specified delay has passed.
	public void sendSancNoti() {
		if(checkLocation()) {
			int SANCTITY_ID = 11206665;
			Widget widget = client.getWidget(SANCTITY_ID);
			if(widget != null) {
				String count = widget.getText();
				count = count.replaceAll("[^0-9]", "");

				int num = Integer.parseInt(count);

				if (num > config.sanctityNotify()) {
					if (!sancNotiSent) {
						if (!startTime) {
							sanctityThresh = Instant.now();
							startTime = true;
						}
						if (Instant.now().compareTo(sanctityThresh.plus(Duration.ofMillis(config.getSanctityDelay()))) >= 0) {
							notifier.notify("Sanctity has reached threshold");
							sancNotiSent = true;
						}
					}
				} else {
					sancNotiSent = false;
					startTime = false;
				}
			}
		}
	}


	// Checks the Region and Tile the player is standing on and returns
	// true if they are within the bounds of the Temple
	private boolean checkLocation() {
		WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();

		int TEMPLE_REGION_ID = 13875;
		if(playerPos.getRegionID() != TEMPLE_REGION_ID) {
			return false;
		}

		int TEMPLE_LOCATION_NW_X = 47;
		int TEMPLE_LOCATION_SE_X = 53;
		if (playerPos.getRegionX() < TEMPLE_LOCATION_NW_X
			|| playerPos.getRegionX() > TEMPLE_LOCATION_SE_X) {
			return false;
		}

		int TEMPLE_LOCATION_NW_Y = 55;
		int TEMPLE_LOCATION_SE_Y = 49;
		if (playerPos.getRegionY() > TEMPLE_LOCATION_NW_Y
			|| playerPos.getRegionY() < TEMPLE_LOCATION_SE_Y) {
			return false;
		}
		return true;
	}

	@Provides
	SacredOilConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SacredOilConfig.class);
	}
}
