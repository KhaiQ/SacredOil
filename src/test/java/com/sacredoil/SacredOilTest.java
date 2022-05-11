package com.sacredoil;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SacredOilTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SacredOilPlugin.class);
		RuneLite.main(args);
	}
}