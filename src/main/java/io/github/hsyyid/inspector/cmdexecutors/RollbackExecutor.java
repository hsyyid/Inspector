package io.github.hsyyid.inspector.cmdexecutors;

import com.google.common.collect.Lists;
import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.Region;
import io.github.hsyyid.inspector.utilities.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Consumer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RollbackExecutor implements CommandExecutor {
	public RollbackExecutor() {
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
		User targetPlayer = (User)ctx.getOne("player").get();
		Optional<String> time = ctx.getOne("time");
		if (src instanceof Player) {
			Player player = (Player)src;
			Optional<Region> optionalRegion = Utils.getRegion(player.getUniqueId());
			if (optionalRegion.isPresent()) {
				Region region = (Region)optionalRegion.get();
				if (region.getPointA() != null && region.getPointB() != null) {
					if (((World)region.getPointA().getExtent()).getUniqueId().equals(((World)region.getPointB().getExtent()).getUniqueId())) {
						LocalDate lclDate = null;
						if (time.isPresent()) {
							lclDate = this.extractDateFromString((String)time.get());
							if (lclDate == null) {
								player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Invalid time. Format is dd:mm:yyyy"}));
								return CommandResult.empty();
							}
						}

						if (lclDate == null) {
							this.displayRevertOptions(player, region.getContainingBlocks(), targetPlayer.getUniqueId());
						} else {
							try {
								this.displayRevertOptions(player, region.getContainingBlocks(), targetPlayer.getUniqueId(), lclDate);
							} catch (ParseException var10) {
								player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "An error occurred while parsing a date."}));
							}
						}

						player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Select an option."}));
					} else {
						player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may do a rollback for a region in two different worlds."}));
					}
				} else {
					player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You are missing one or more points for your region."}));
				}
			} else {
				player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You have not selected a region yet."}));
			}
		} else {
			src.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may only use this command as an in-game player."}));
		}

		return CommandResult.success();
	}

	private void displayRevertOptions(Player player, Set<Location<World>> locations, UUID targetPlayer) {
		List<BlockInformation> results = Inspector.instance().getDatabaseManager().getBlockInformationAt(locations, targetPlayer);
		List<Text> blockChanges = Lists.newArrayList();
		if (!results.isEmpty()) {
			Iterator var6 = results.iterator();

			while(var6.hasNext()) {
				BlockInformation blockInfo = (BlockInformation)var6.next();
				BlockSnapshot oldBlock = blockInfo.getOldBlockSnapshot();
				BlockSnapshot newBlock = blockInfo.getNewBlockSnapshot();
				String playerName = blockInfo.getPlayerName();
				UUID playerUUID = blockInfo.getPlayerUUID();
				String timeEdited = blockInfo.getTimeEdited();
				Text blockChange = Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Time Edited: ", TextColors.GOLD, timeEdited, "\n"})}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Player Edited: ", TextColors.GOLD, TextStyles.UNDERLINE, playerName, "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "UUID: ", TextColors.GOLD, playerUUID.toString()}))).build()}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Old Block ID: ", TextColors.GOLD, TextStyles.UNDERLINE, oldBlock.getState().getType().getTranslation().get(), "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "ID: ", TextColors.GOLD, oldBlock.getState().getType().getId()}))).build()}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "New Block ID: ", TextColors.GOLD, TextStyles.UNDERLINE, newBlock.getState().getType().getTranslation().get(), "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "ID: ", TextColors.GOLD, newBlock.getState().getType().getId()}))).build()}).onClick(TextActions.executeCallback((src) -> {
					Utils.revertBlock(src, results.indexOf(blockInfo), results);
				})).build();
				blockChanges.add(blockChange);
			}
		}

		PaginationService paginationService = (PaginationService)Sponge.getServiceManager().provide(PaginationService.class).get();
		Builder paginationBuilder = paginationService.builder().title(Text.of(new Object[]{TextColors.BLUE, "[Inspector] ", TextColors.GRAY, "Block Changes"})).padding(Text.of("-")).contents(blockChanges);
		paginationBuilder.sendTo(player);
	}

	private void displayRevertOptions(Player player, Set<Location<World>> locations, UUID targetPlayer, LocalDate lclDate) throws ParseException {
		List<BlockInformation> results = Inspector.instance().getDatabaseManager().getBlockInformationAt(locations, targetPlayer);
		List<Text> blockChanges = Lists.newArrayList();
		if (!results.isEmpty()) {
			Iterator var7 = results.iterator();

			while(var7.hasNext()) {
				BlockInformation blockInfo = (BlockInformation)var7.next();
				BlockSnapshot oldBlock = blockInfo.getOldBlockSnapshot();
				BlockSnapshot newBlock = blockInfo.getNewBlockSnapshot();
				String playerName = blockInfo.getPlayerName();
				UUID playerUUID = blockInfo.getPlayerUUID();
				SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date timeEdited = format.parse(blockInfo.getTimeEdited());
				if (timeEdited.getYear() + 1900 == lclDate.getYear() && timeEdited.getMonth() + 1 == lclDate.getMonthValue() && timeEdited.getDate() == lclDate.getDayOfMonth()) {
					Text blockChange = Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Time Edited: ", TextColors.GOLD, timeEdited, "\n"})}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Player Edited: ", TextColors.GOLD, TextStyles.UNDERLINE, playerName, "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "UUID: ", TextColors.GOLD, playerUUID.toString()}))).build()}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Old Block ID: ", TextColors.GOLD, TextStyles.UNDERLINE, oldBlock.getState().getType().getTranslation().get(), "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "ID: ", TextColors.GOLD, oldBlock.getState().getType().getId()}))).build()}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "New Block ID: ", TextColors.GOLD, TextStyles.UNDERLINE, newBlock.getState().getType().getTranslation().get(), "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "ID: ", TextColors.GOLD, newBlock.getState().getType().getId()}))).build()}).onClick(TextActions.executeCallback((src) -> {
						Utils.revertBlock(src, results.indexOf(blockInfo), results);
					})).build();
					blockChanges.add(blockChange);
				}
			}
		}

		PaginationService paginationService = (PaginationService)Sponge.getServiceManager().provide(PaginationService.class).get();
		Builder paginationBuilder = paginationService.builder().title(Text.of(new Object[]{TextColors.BLUE, "[Inspector] ", TextColors.GRAY, "Block Changes"})).padding(Text.of("-")).contents(blockChanges);
		paginationBuilder.sendTo(player);
	}

	public LocalDate extractDateFromString(String timeString) {
		String[] date = timeString.split(":");

		try {
			LocalDate lclDate = LocalDate.of(Integer.parseInt(date[2]), Month.of(Integer.parseInt(date[0])), Integer.parseInt(date[1]));
			return lclDate;
		} catch (NumberFormatException var4) {
			return null;
		}
	}
}