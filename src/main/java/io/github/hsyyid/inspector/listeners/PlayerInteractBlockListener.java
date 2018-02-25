package io.github.hsyyid.inspector.listeners;

import com.google.common.collect.Lists;
import io.github.hsyyid.inspector.Inspector;
import io.github.hsyyid.inspector.utilities.BlockInformation;
import io.github.hsyyid.inspector.utilities.Utils;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent.Primary;
import org.spongepowered.api.event.block.InteractBlockEvent.Secondary;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerInteractBlockListener {
	public PlayerInteractBlockListener() {
	}

	@Listener(
			order = Order.FIRST
	)
	public void onPlayerClickBlock(InteractBlockEvent event, @First Player player) {
		if (Inspector.inspectorEnabledPlayers.contains(player.getUniqueId())) {
			if(!(Inspector.config.getNode("worlds").getNode(player.getWorld().getName()).getBoolean())){
				player.sendMessage(Text.of("Inspectator was not enable in this world!"));
				return;
			}
			event.setCancelled(true);
			Optional<Location<World>> location = event.getTargetBlock().getLocation();
			if (location.isPresent()) {
				List<BlockInformation> information = Inspector.instance().getDatabaseManager().getBlockInformationAt((Location)location.get());
				if (information.size() == 0) {
					player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "No information found for this block."}));
					return;
				}

				List<Text> blockChanges = Lists.newArrayList();
				Iterator var6 = information.iterator();

				while(var6.hasNext()) {
					BlockInformation blockInfo = (BlockInformation)var6.next();
					BlockSnapshot oldBlock = blockInfo.getOldBlockSnapshot();
					BlockSnapshot newBlock = blockInfo.getNewBlockSnapshot();
					String playerName = blockInfo.getPlayerName();
					UUID playerUUID = blockInfo.getPlayerUUID();
					String timeEdited = blockInfo.getTimeEdited();
					Text blockChange = Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Time Edited: ", TextColors.GOLD, timeEdited, "\n"})}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Player Edited: ", TextColors.GOLD, TextStyles.UNDERLINE, playerName, "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "UUID: ", TextColors.GOLD, playerUUID.toString()}))).build()}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "Old Block ID: ", TextColors.GOLD, TextStyles.UNDERLINE, oldBlock.getState().getType().getTranslation().get(), "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "ID: ", TextColors.GOLD, oldBlock.getState().getType().getId()}))).build()}).append(new Text[]{Text.builder().append(new Text[]{Text.of(new Object[]{TextColors.GRAY, "New Block ID: ", TextColors.GOLD, TextStyles.UNDERLINE, newBlock.getState().getType().getTranslation().get(), "\n"})}).onHover(TextActions.showText(Text.of(new Object[]{TextColors.GRAY, "ID: ", TextColors.GOLD, newBlock.getState().getType().getId()}))).build()}).build();
					blockChanges.add(blockChange);
				}

				PaginationService paginationService = (PaginationService)Sponge.getServiceManager().provide(PaginationService.class).get();
				Builder paginationBuilder = paginationService.builder().title(Text.of(new Object[]{TextColors.BLUE, "[Inspector] ", TextColors.GRAY, "Block Changes"})).padding(Text.of("-")).contents(blockChanges);
				paginationBuilder.sendTo(player);
			}
		}

	}

	@Listener
	public void onPlayerLeftClickBlock(Primary event, @Root Player player) {
		if (player.hasPermission("inspector.region.use") && player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && ((ItemStack)player.getItemInHand(HandTypes.MAIN_HAND).get()).getItem().getName().equals((String)Utils.getConfigValue("inspector.select.tool"))) {
			if(!(Inspector.config.getNode("worlds").getNode(player.getWorld().getName()).getBoolean())){
				player.sendMessage(Text.of("Inspectator was not enable in this world!"));
				return;
			}
			Location<World> pointA = (Location)event.getTargetBlock().getLocation().get();
			Utils.addPointOrCreateRegionOf(player.getUniqueId(), pointA, false);
			player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Set position A to ", TextColors.GOLD, "(" + pointA.getBlockX() + ", " + pointA.getBlockY() + ", " + pointA.getBlockZ() + ")"}));
			event.setCancelled(true);
		}

	}

	@Listener
	public void onPlayerRightClickBlock(Secondary event, @Root Player player) {
		if (player.hasPermission("inspector.region.use") && player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && ((ItemStack)player.getItemInHand(HandTypes.MAIN_HAND).get()).getItem().getName().equals((String)Utils.getConfigValue("inspector.select.tool"))) {
			if(!(Inspector.config.getNode("worlds").getNode(player.getWorld().getName()).getBoolean())){
				player.sendMessage(Text.of("Inspectator was not enable in this world!"));
				return;
			}
			Location<World> pointB = (Location)event.getTargetBlock().getLocation().get();
			Utils.addPointOrCreateRegionOf(player.getUniqueId(), pointB, true);
			player.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Set position B to ", TextColors.GOLD, "(" + pointB.getBlockX() + ", " + pointB.getBlockY() + ", " + pointB.getBlockZ() + ")"}));
			event.setCancelled(true);
		}

	}
}
