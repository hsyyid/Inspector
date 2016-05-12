package io.github.hsyyid.inspector.utilities;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class BlockInformation
{
	private Location<World> location;
	private BlockSnapshot oldBlockSnapshot;
	private BlockSnapshot newBlockSnapshot;
	private String timeEdited;
	private UUID playerUUID;
	private String playerName;

	public BlockInformation(Location<World> location, BlockSnapshot oldBlockSnapshot, BlockSnapshot newBlockSnapshot, String timeEdited, UUID playerUUID, String playerName)
	{
		this.location = location;
		this.oldBlockSnapshot = oldBlockSnapshot;
		this.newBlockSnapshot = newBlockSnapshot;
		this.timeEdited = timeEdited;
		this.playerUUID = playerUUID;
		this.playerName = playerName;
	}

	public Location<World> getLocation()
	{
		return location;
	}

	public BlockSnapshot getOldBlockSnapshot()
	{
		return oldBlockSnapshot;
	}

	public BlockSnapshot getNewBlockSnapshot()
	{
		return newBlockSnapshot;
	}

	public String getTimeEdited()
	{
		return timeEdited;
	}

	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public String getPlayerName()
	{
		return playerName;
	}
}
