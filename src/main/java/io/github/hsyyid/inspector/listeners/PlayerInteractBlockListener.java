package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

public class PlayerInteractBlockListener
{
	@Listener
	public void onPlayerClickBlock(InteractBlockEvent event)
	{
		if (event.getCause().first(Player.class).isPresent())
		{
			Player player = (Player) event.getCause().first(Player.class).get();

			if (Inspector.inspectorEnabledPlayers.contains(player.getUniqueId()))
			{
				List<BlockInformation> information = DatabaseManager.getBlockInformationAt(event.getTargetBlock().getLocation().get());

				if (information.size() == 0)
				{
					player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "No information found for this block."));
					return;
				}

				for (BlockInformation blockInfo : information)
				{
					String blockID = blockInfo.getBlockID();
					String playerName = blockInfo.getPlayerName();
					UUID playerUUID = blockInfo.getPlayerUUID();
					String timeEdited = blockInfo.getTimeEdited();

					player.sendMessage(Texts.of(TextColors.GRAY, "-------------------------"));
					player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Player Edited: ", TextColors.GOLD, playerName));
					player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "UUID of Player Edited: ", TextColors.GOLD, playerUUID.toString()));
					player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Time Edited: ", TextColors.GOLD, timeEdited));
					player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Block ID: ", TextColors.GOLD, blockID));
					if (blockInfo.getMeta() != -1)
						player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Block Meta: ", TextColors.GOLD, blockInfo.getMeta()));
					player.sendMessage(Texts.of(TextColors.GRAY, "-------------------------"));
				}

				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onPlayerLeftClickBlock(InteractBlockEvent.Primary event)
	{
		if (event.getCause().first(Player.class).isPresent())
		{
			Player player = (Player) event.getCause().first(Player.class).get();

			if (player.hasPermission("inspector.region.use") && player.getItemInHand().isPresent() && player.getItemInHand().get().getItem().getName().equals((String) DatabaseManager.getConfigValue("inspector.select.tool").orElse("")))
			{
				Location<World> pointA = event.getTargetBlock().getLocation().get();
				DatabaseManager.addPointOrCreateRegionOf(player.getUniqueId(), pointA, false);
				player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Set position A to ", TextColors.GOLD, "(" + pointA.getBlockX() + ", " + pointA.getBlockY() + ", " + pointA.getBlockZ() + ")"));
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onPlayerRightClickBlock(InteractBlockEvent.Secondary event)
	{
		if (event.getCause().first(Player.class).isPresent())
		{
			Player player = (Player) event.getCause().first(Player.class).get();

			if (player.hasPermission("inspector.region.use") && player.getItemInHand().isPresent() && player.getItemInHand().get().getItem().getName().equals((String) DatabaseManager.getConfigValue("inspector.select.tool").orElse("")))
			{
				Location<World> pointB = event.getTargetBlock().getLocation().get();
				DatabaseManager.addPointOrCreateRegionOf(player.getUniqueId(), pointB, true);
				player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Set position B to ", TextColors.GOLD, "(" + pointB.getBlockX() + ", " + pointB.getBlockY() + ", " + pointB.getBlockZ() + ")"));
				event.setCancelled(true);
			}
		}
	}
}
