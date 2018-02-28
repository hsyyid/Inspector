package io.github.hsyyid.inspector.utilities;

import java.util.UUID;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class BlockInformation {
	private Location<World> location;
	private BlockSnapshot oldBlockSnapshot;
	private BlockSnapshot newBlockSnapshot;
	private String timeEdited;
	private UUID playerUUID;
	private String playerName;

	public BlockInformation(Location<World> location, BlockSnapshot oldBlockSnapshot, BlockSnapshot newBlockSnapshot, String timeEdited, UUID playerUUID, String playerName) {
		this.location = location;
		this.oldBlockSnapshot = oldBlockSnapshot;
		this.newBlockSnapshot = newBlockSnapshot;
		this.timeEdited = timeEdited;
		this.playerUUID = playerUUID;
		this.playerName = playerName;
	}

	public Location<World> getLocation() {
		return this.location;
	}

	public BlockSnapshot getOldBlockSnapshot() {
		return this.oldBlockSnapshot;
	}

	public BlockSnapshot getNewBlockSnapshot() {
		return this.newBlockSnapshot;
	}

	public String getTimeEdited() {
		return this.timeEdited;
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}

	public String getPlayerName() {
		return this.playerName;
	}
}
