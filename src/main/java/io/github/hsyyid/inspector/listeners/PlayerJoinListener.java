package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.Inspector;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerJoinListener
{
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event)
	{
		if (!Inspector.instance().getDatabaseManager().isPlayerInDatabase(event.getTargetEntity()))
		{
			Inspector.instance().getDatabaseManager().addPlayerToDatabase(event.getTargetEntity());
		}
	}
}
