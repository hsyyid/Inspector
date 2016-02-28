package io.github.hsyyid.inspector.listeners;

import com.google.common.collect.Lists;
import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

public class PlayerInteractBlockListener
{
	@Listener
	public void onPlayerClickBlock(InteractBlockEvent event, @First Player player)
	{
		if (Inspector.inspectorEnabledPlayers.contains(player.getUniqueId()))
		{
			List<BlockInformation> information = DatabaseManager.getBlockInformationAt(event.getTargetBlock().getLocation().get());

			if (information.size() == 0)
			{
				player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "No information found for this block."));
				return;
			}

			List<Text> blockChanges = Lists.newArrayList();

			for (BlockInformation blockInfo : information)
			{
				String oldBlockId = blockInfo.getOldBlockID();
				String blockId = blockInfo.getNewBlockID();
				String playerName = blockInfo.getPlayerName();
				UUID playerUUID = blockInfo.getPlayerUUID();
				String timeEdited = blockInfo.getTimeEdited();

				Text blockChange = Text.builder()
					.append(Text.of(TextColors.GRAY, "Player Edited: ", TextColors.GOLD, playerName, "\n"))
					.append(Text.of(TextColors.GRAY, "UUID of Player Edited: ", TextColors.GOLD, playerUUID.toString(), "\n"))
					.append(Text.of(TextColors.GRAY, "Time Edited: ", TextColors.GOLD, timeEdited, "\n"))
					.append(Text.of(TextColors.GRAY, "Old Block ID: ", TextColors.GOLD, oldBlockId, "\n"))
					.append(Text.of(TextColors.GRAY, "New Block ID: ", TextColors.GOLD, blockId, "\n"))
					.build();

				if (blockInfo.getNewMeta() != -1)
				{
					blockChange = Text.builder()
						.append(blockChange)
						.append(Text.of(TextColors.GRAY, "Block Meta: ", TextColors.GOLD, blockInfo.getNewMeta(), "\n"))
						.build();
				}

				blockChanges.add(blockChange);
			}

			PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
			PaginationBuilder paginationBuilder = paginationService.builder().title(Text.of(TextColors.BLUE, "[Inspector] ", TextColors.GRAY, "Block Changes")).paddingString("-").contents(blockChanges);
			paginationBuilder.sendTo(player);
			event.setCancelled(true);
		}
	}

	@Listener
	public void onPlayerLeftClickBlock(InteractBlockEvent.Primary event, @First Player player)
	{
		if (player.hasPermission("inspector.region.use") && player.getItemInHand().isPresent() && player.getItemInHand().get().getItem().getName().equals((String) DatabaseManager.getConfigValue("inspector.select.tool").orElse("")))
		{
			Location<World> pointA = event.getTargetBlock().getLocation().get();
			DatabaseManager.addPointOrCreateRegionOf(player.getUniqueId(), pointA, false);
			player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Set position A to ", TextColors.GOLD, "(" + pointA.getBlockX() + ", " + pointA.getBlockY() + ", " + pointA.getBlockZ() + ")"));
			event.setCancelled(true);
		}
	}

	@Listener
	public void onPlayerRightClickBlock(InteractBlockEvent.Secondary event, @First Player player)
	{
		if (player.hasPermission("inspector.region.use") && player.getItemInHand().isPresent() && player.getItemInHand().get().getItem().getName().equals((String) DatabaseManager.getConfigValue("inspector.select.tool").orElse("")))
		{
			Location<World> pointB = event.getTargetBlock().getLocation().get();
			DatabaseManager.addPointOrCreateRegionOf(player.getUniqueId(), pointB, true);
			player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Set position B to ", TextColors.GOLD, "(" + pointB.getBlockX() + ", " + pointB.getBlockY() + ", " + pointB.getBlockZ() + ")"));
			event.setCancelled(true);
		}
	}
}
