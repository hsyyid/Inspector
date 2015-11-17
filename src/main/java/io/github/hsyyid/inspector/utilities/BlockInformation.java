package io.github.hsyyid.inspector.utilities;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class BlockInformation
{
	private Location<World> location;
	private String blockID;
	private String timeEdited;
	private UUID playerUUID;
	private String playerName;
	private int meta;
	
	public BlockInformation(Location<World> location, String blockID, String timeEdited, UUID playerUUID, String playerName, int meta)
	{
		this.location = location;
		this.blockID = blockID;
		this.timeEdited = timeEdited;
		this.playerUUID = playerUUID;
		this.playerName = playerName;
		this.meta = meta;
	}
	
	public void setBlockID(String blockID)
	{
		this.blockID = blockID;
	}
	
	public void setLocation(Location<World> location)
	{
		this.location = location;
	}
	
	public void setPlayerUUID(UUID playerUUID)
	{
		this.playerUUID = playerUUID;
	}
	
	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
	
	public void setTimeEdited(String timeEdited)
	{
		this.timeEdited = timeEdited;
	}
	
	public void setMeta(int meta)
	{
		this.meta = meta;
	}
	
	public String getBlockID()
	{
		return blockID;
	}
	
	public Location<World> getLocation()
	{
		return location;
	}
	
	public UUID getPlayerUUID()
	{
		return playerUUID;
	}
	
	public String getPlayerName()
	{
		return playerName;
	}
	
	public String getTimeEdited()
	{
		return timeEdited;
	}
	
	public int getMeta()
	{
		return meta;
	}
}
