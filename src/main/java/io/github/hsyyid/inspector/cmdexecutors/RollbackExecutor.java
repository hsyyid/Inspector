package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import io.github.hsyyid.inspector.utilities.Region;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RollbackExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;

			Optional<Region> optionalRegion = DatabaseManager.getRegion(player.getUniqueId());

			if (optionalRegion.isPresent())
			{
				Region region = optionalRegion.get();

				if (region.getPointA() != null && region.getPointB() != null)
				{
					if (region.getPointA().getExtent().getUniqueId().equals(region.getPointB().getExtent().getUniqueId()))
					{
						Set<Location<World>> blocks = region.getContainingBlocks();

						for (Location<World> block : blocks)
						{
							revertBlock(player, block);
						}
					}
					else
					{
						src.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may do a rollback for a region in two different worlds."));
					}
				}
				else
				{
					src.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You are missing one or more points for your region."));
				}
			}
			else
			{
				src.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You have not selected a region yet."));
			}
		}
		else
		{
			src.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may only use this command as an in-game player."));
		}

		return CommandResult.success();
	}

	public void revertBlock(Player player, Location<World> location)
	{
		List<BlockInformation> blockInformation = DatabaseManager.getBlockInformationAt(location);

		if (blockInformation.size() != 0)
		{
			BlockInformation blockInfo = blockInformation.get(0);

			if (Inspector.game.getRegistry().getType(BlockType.class, blockInfo.getBlockID()).isPresent())
			{
				BlockType blockType = Inspector.game.getRegistry().getType(BlockType.class, blockInfo.getBlockID()).get();
				BlockState blockState = Inspector.game.getRegistry().createBuilder(BlockState.Builder.class).blockType(blockType).build();

				if (blockInfo.getMeta() != -1)
				{
					DataContainer container = blockState.toContainer().set(new DataQuery("UnsafeMeta"), blockInfo.getMeta());
					blockState = Inspector.game.getRegistry().createBuilder(BlockState.Builder.class).blockType(blockType).build(container).get();
				}

				blockInfo.getLocation().setBlock(blockState);

				// player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Reverted this block to its previous state at " + blockInfo.getTimeEdited()));
			}
			else
			{
				player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Block could not be recreated!"));
			}
		}
		else
		{
			// player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "No data found for this block."));
		}
	}
}
