package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.Inspector;
import java.text.SimpleDateFormat;
import java.util.*;

import jdk.nashorn.internal.ir.Block;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent.Break;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerBreakBlockListener {
	public PlayerBreakBlockListener() {
	}

	@Listener
	public void onPlayerBreakBlock(Break event, @First Player player) {
		if(!(Inspector.config.getNode("worlds").getNode(player.getWorld().getName()).getBoolean())){
			return;
		}
		Iterator var3 = event.getTransactions().iterator();

			List<Transaction<BlockSnapshot>> list = event.getTransactions();
			for (Transaction<BlockSnapshot> transaction : list){
//				BlockType blockType = transaction.getOriginal().getState().getType();
//				Set<String> bwl = Inspector.instance().getBlockWhiteList();
//
				ItemStack itemStack;
				try {
					itemStack = ItemStack.builder().fromBlockSnapshot(transaction.getOriginal()).build();
				} catch (Exception e) {
					return;
				}
				if (isBanned(itemStack,player)) {
					return;
				}
			}

		while(var3.hasNext()) {
			Transaction<BlockSnapshot> transaction = (Transaction)var3.next();
			Location<World> transactionLocation = (Location)((BlockSnapshot)transaction.getFinal()).getLocation().get();
//			Calendar cal = Calendar.getInstance();
//			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
//			format.setTimeZone(TimeZone.getTimeZone("GMT"));
//			String timeInGMT = format.format(cal.getTime());
			String time = Calendar.getInstance().getTime().getTime()+"";
			Inspector.instance().getDatabaseManager().updateBlockInformation(transactionLocation.getBlockX(), transactionLocation.getBlockY(), transactionLocation.getBlockZ(), ((World)transactionLocation.getExtent()).getUniqueId(), player.getUniqueId(), player.getName(), time, (BlockSnapshot)transaction.getOriginal(), (BlockSnapshot)transaction.getFinal());
		}

	}

	private boolean isBanned(ItemStack itemStack,Player player) {
//		World world = player.getWorld();
		String itemType = itemStack.getItem().getId();

		DataContainer container = itemStack.toContainer();
		DataQuery query = DataQuery.of('/', "UnsafeDamage");

		Set<String> bwl = Inspector.instance().getBlockWhiteList();
		if (bwl.contains(itemType)||bwl.contains(itemType+":"+container.get(query).get().toString())||bwl.contains(itemType+":*")){
			return true;
		}

		////
////		if (player.hasPermission("stackban.bypass." + itemType + ":" + container.get(query).get().toString()) || player.hasPermission("stackban.bypass." + itemType)) {
////			return false;
////		}
//
//		ConfigurationNode config = ConfigManager.get(Main.getPlugin(), world.getName()).getConfig();
//
//		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
//			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
//		}
//
//		if (!config.getNode("items", itemType).isVirtual()) {
//			return !config.getNode("items", itemType, action.getName()).getBoolean();
//		}
//
//		config = ConfigManager.get(Main.getPlugin(), "global").getConfig();
//
//		if (!config.getNode("items", itemType + ":" + container.get(query).get().toString()).isVirtual()) {
//			return !config.getNode("items", itemType + ":" + container.get(query).get().toString(), action.getName()).getBoolean();
//		}
//
//		if (!config.getNode("items", itemType).isVirtual()) {
//			return !config.getNode("items", itemType, action.getName()).getBoolean();
//		}

		return false;
	}
}