package io.github.hsyyid.inspector.utilities;

import io.github.hsyyid.inspector.Inspector;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Utils {
	public Utils() {
	}

	public static void revertBlock(CommandSource src, int index, List<BlockInformation> results) {
		for(int i = index; i < results.size(); ++i) {
			BlockInformation blockInfo = (BlockInformation)results.get(i);
			if (blockInfo.getLocation().getBlock() != blockInfo.getOldBlockSnapshot().getState()) {
				blockInfo.getLocation().setBlock(blockInfo.getOldBlockSnapshot().getState(), Cause.of(NamedCause.source(Inspector.instance().getPluginContainer())));
			}
		}

		src.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Rollback completed."}));
	}

	public static Object getConfigValue(String configValue) {
		return Inspector.config.getNode((Object[])configValue.split("\\.")).getValue();
	}

	public static void addPointOrCreateRegionOf(UUID playerUUID, Location<World> point, boolean secondary) {
		Iterator var3;
		Region region;
		if (secondary) {
			var3 = Inspector.regions.iterator();

			while(var3.hasNext()) {
				region = (Region)var3.next();
				if (region.getOwner().equals(playerUUID)) {
					region.setPointB(point);
					return;
				}
			}

			Inspector.regions.add(new Region(playerUUID, (Location)null, point));
		} else {
			var3 = Inspector.regions.iterator();

			while(var3.hasNext()) {
				region = (Region)var3.next();
				if (region.getOwner().equals(playerUUID)) {
					region.setPointA(point);
					return;
				}
			}

			Inspector.regions.add(new Region(playerUUID, point, (Location)null));
		}

	}

	public static Optional<Region> getRegion(UUID playerUUID) {
		Iterator var1 = Inspector.regions.iterator();

		Region region;
		do {
			if (!var1.hasNext()) {
				return Optional.empty();
			}

			region = (Region)var1.next();
		} while(!region.getOwner().equals(playerUUID));

		return Optional.of(region);
	}
}