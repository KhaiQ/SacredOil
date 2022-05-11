package com.sacredoil;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "1 Sacred Oil"
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

	private SacredOil counterbox;

	private SacredOil temp;

	private int charge = 80;


	// Checks glove equipment slot when starting plugin
	@Override
	protected void startUp() throws Exception {
		clientThread.invokeLater(() -> {
			final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);

			if (container != null)	{
				checkHand(container.getItems());
			}
		});
	}

	// Removes the counter when stopping plugin
	@Override
	protected void shutDown() throws Exception {
		removeInfobox();
		counterbox = null;
	}

	// Checks glove equipment any time the plugin is enabled
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if(event.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT)){
			return;
		}
		checkHand(event.getItemContainer().getItems());
	}

	// Updates value on counter based off of game messages
	// NOTE: For charges 80-2, messages are catagorized as SPAM,
	// 		 Charges 1-0 are considered GAMEMESSAGE
	@Subscribe
	public void onChatMessage(ChatMessage event) {
		final String prompt = "Your Flamtaer bracelet helps you build the temple quicker.";
		if((event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) && event.getMessage().contains(prompt)) {
			if(!event.getMessage().replaceAll("[^0-9]", "").equals("")){
				charge = Integer.parseInt(event.getMessage().replaceAll("[^0-9]", ""));
			}
			else
				charge = 0;
			updateInfobox(charge);
		}
	}

	// Checks if glove equipment matches Flamtaer Bracelet Id
	// If true, the counter is enabled
	// If false, the counter is disabled
	private void checkHand(final Item[] items) {
		if(items[EquipmentInventorySlot.GLOVES.getSlotIdx()].getId() == 21180) {
			updateInfobox(charge);
		}
		else
			removeInfobox();
	}

	// Updates the value of the counter
	private void updateInfobox(int charge) {
		removeInfobox();
		final BufferedImage image = itemManager.getImage(ItemID.FLAMTAER_BRACELET, charge, false);
		counterbox = new SacredOil(this, 21180, charge, "Bracelet", image);
		infoBoxManager.addInfoBox((counterbox));
	}

	// Removes the counter
	private void removeInfobox() {
		infoBoxManager.removeInfoBox(counterbox);
	}

	@Provides
	SacredOilConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SacredOilConfig.class);
	}
}
