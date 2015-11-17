package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

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
}
