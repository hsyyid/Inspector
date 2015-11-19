package io.github.hsyyid.inspector.utilities;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class BlockInformation
{
	private Location<World> location;
	private String newBlockID;
	private int newMeta;
	private String oldBlockID;
	private int oldMeta;
	private String timeEdited;
	private UUID playerUUID;
	private String playerName;
	
	public BlockInformation(Location<World> location, String newBlockID, int newMeta, String oldBlockID, int oldMeta, String timeEdited, UUID playerUUID, String playerName)
	{
		this.location = location;
		this.newBlockID = newBlockID;
		this.newMeta = newMeta;
		this.oldBlockID = oldBlockID;
		this.oldMeta = oldMeta;
		this.timeEdited = timeEdited;
		this.playerUUID = playerUUID;
		this.playerName = playerName;
	}
	
	public void setBlockID(String blockID)
	{
		this.oldBlockID = blockID;
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
		this.oldMeta = meta;
	}
	
	public String getNewBlockID()
	{
		return newBlockID;
	}
	
	public int getNewMeta()
	{
		return newMeta;
	}
	
	public String getOldBlockID()
	{
		return oldBlockID;
	}
	
	public int getOldMeta()
	{
		return oldMeta;
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
}
