package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.world.World;

import java.util.List;

public class RollbackExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;

			BlockRay<World> playerBlockRay = BlockRay.from(player).blockLimit(350).build();
			BlockRayHit<World> finalHitRay = null;

			while (playerBlockRay.hasNext())
			{
				BlockRayHit<World> currentHitRay = playerBlockRay.next();

				if (player.getWorld().getBlockType(currentHitRay.getBlockPosition()).equals(BlockTypes.AIR))
				{
					continue;
				}
				else
				{
					finalHitRay = currentHitRay;
					break;
				}
			}

			if (finalHitRay == null)
			{
				player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Could not find the block you're looking at within range!"));
			}
			else
			{
				List<BlockInformation> blockInformation = DatabaseManager.getBlockInformationAt(finalHitRay.getLocation());

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

						player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Reverted this block to its previous state at " + blockInfo.getTimeEdited()));
					}
					else
					{
						player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Block could not be recreated!"));
					}
				}
				else
				{
					player.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "No data found for this block."));
				}
			}
		}
		else
		{
			src.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may only use this command as an in-game player."));
		}

		return CommandResult.success();
	}
}
