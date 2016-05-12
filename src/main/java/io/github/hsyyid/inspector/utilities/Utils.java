package io.github.hsyyid.inspector.utilities;

import io.github.hsyyid.inspector.Inspector;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Utils
{
	public static void revertBlock(CommandSource src, int index, List<BlockInformation> results)
	{
		for (int i = index; i < results.size(); i++)
		{
			BlockInformation blockInfo = results.get(i);

			if (blockInfo.getLocation().getBlock() != blockInfo.getOldBlockSnapshot().getState())
			{
				blockInfo.getLocation().setBlock(blockInfo.getOldBlockSnapshot().getState());
			}
		}
		
		src.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Rollback completed."));
	}

	public static Object getConfigValue(String configValue)
	{
		return Inspector.config.getNode((Object[]) (configValue).split("\\.")).getValue();
	}

	public static void addPointOrCreateRegionOf(UUID playerUUID, Location<World> point, boolean secondary)
	{
		if (secondary)
		{
			for (Region region : Inspector.regions)
			{
				if (region.getOwner().equals(playerUUID))
				{
					region.setPointB(point);
					return;
				}
			}

			Inspector.regions.add(new Region(playerUUID, null, point));
		}
		else
		{
			for (Region region : Inspector.regions)
			{
				if (region.getOwner().equals(playerUUID))
				{
					region.setPointA(point);
					return;
				}
			}

			Inspector.regions.add(new Region(playerUUID, point, null));
		}
	}

	public static Optional<Region> getRegion(UUID playerUUID)
	{
		for (Region region : Inspector.regions)
		{
			if (region.getOwner().equals(playerUUID))
			{
				return Optional.of(region);
			}
		}

		return Optional.empty();
	}
}
