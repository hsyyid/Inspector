package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import io.github.hsyyid.inspector.utilities.Region;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

public class RollbackExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String time = ctx.<String> getOne("time").get();
		Optional<String> targetPlayer = ctx.<String> getOne("player");

		if (src instanceof Player)
		{
			Player player = (Player) src;

			String timeInGMT = getTimeInGMT(time);

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
							revertBlock(player, block, targetPlayer, timeInGMT);
						}
						
						player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Rollback completed."));
					}
					else
					{
						player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may do a rollback for a region in two different worlds."));
					}
				}
				else
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You are missing one or more points for your region."));
				}
			}
			else
			{
				player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You have not selected a region yet."));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may only use this command as an in-game player."));
		}

		return CommandResult.success();
	}

	public void revertBlock(Player player, Location<World> location, Optional<String> targetPlayer, String timeInGMT)
	{
		if (targetPlayer.isPresent())
		{
			BlockInformation blockInfo = DatabaseManager.getBlockInformationAt(location, targetPlayer.get(), timeInGMT);

			if (blockInfo != null)
			{
				if (Inspector.game.getRegistry().getType(BlockType.class, blockInfo.getOldBlockID()).isPresent())
				{
					BlockType blockType = Inspector.game.getRegistry().getType(BlockType.class, blockInfo.getOldBlockID()).get();
					BlockState blockState = Inspector.game.getRegistry().createBuilder(BlockState.Builder.class).blockType(blockType).build();

					if (blockInfo.getOldMeta() != -1)
					{
						DataContainer container = blockState.toContainer().set(DataQuery.of("UnsafeMeta"), blockInfo.getOldMeta());
						blockState = Inspector.game.getRegistry().createBuilder(BlockState.Builder.class).blockType(blockType).build(container).get();
					}

					blockInfo.getLocation().setBlock(blockState);
				}
				else
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Block could not be recreated!"));
				}
			}
		}
		else
		{
			List<BlockInformation> blockInformation = DatabaseManager.getBlockInformationAt(location, timeInGMT);

			if (blockInformation.size() != 0)
			{
				//Gets the most recent change before this block.
				BlockInformation blockInfo = null;
				
				for (BlockInformation block : blockInformation)
				{
					if(blockInfo != null && DatabaseManager.wasChangedBefore(block, timeInGMT) && DatabaseManager.wasChangedBefore(blockInfo, block))
					{
						blockInfo = block;
					}
					else if(DatabaseManager.wasChangedBefore(block, timeInGMT))
					{
						blockInfo = block;
					}
				}
				
				if (Inspector.game.getRegistry().getType(BlockType.class, blockInfo.getOldBlockID()).isPresent())
				{
					BlockType blockType = Inspector.game.getRegistry().getType(BlockType.class, blockInfo.getOldBlockID()).get();
					BlockState blockState = Inspector.game.getRegistry().createBuilder(BlockState.Builder.class).blockType(blockType).build();

					if (blockInfo.getOldMeta() != -1)
					{
						DataContainer container = blockState.toContainer().set(DataQuery.of("UnsafeMeta"), blockInfo.getOldMeta());
						blockState = Inspector.game.getRegistry().createBuilder(BlockState.Builder.class).blockType(blockType).build(container).get();
					}

					blockInfo.getLocation().setBlock(blockState);
				}
				else
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Block could not be recreated!"));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String getTimeInGMT(String playerInputTime)
	{
		String timeInGMT = "";

		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date date = sdf.parse(playerInputTime);
			Calendar playerInput = GregorianCalendar.getInstance();
			playerInput.setTime(date);
			int hours = playerInput.get(Calendar.HOUR);
			int minutes = playerInput.get(Calendar.MINUTE);
			int seconds = playerInput.get(Calendar.SECOND);
			
			Calendar currentTime = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date newTime = currentTime.getTime();
			
			newTime.setHours(newTime.getHours() - hours);
			newTime.setMinutes(newTime.getMinutes() - minutes);
			newTime.setSeconds(newTime.getSeconds() - seconds);
			
			timeInGMT = format.format(newTime);
		}
		catch (ParseException e)
		{
			;
		}

		return timeInGMT;
	}

}
