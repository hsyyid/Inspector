package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.Inspector;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import jdk.nashorn.internal.ir.Block;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent.Break;
import org.spongepowered.api.event.filter.cause.First;
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
				BlockType blockType = transaction.getOriginal().getState().getType();
				if (blockType == BlockTypes.STONE || blockType == BlockTypes.DIRT ||blockType == BlockTypes.GRASS ||blockType == BlockTypes.TALLGRASS||blockType == BlockTypes.RED_FLOWER ||blockType == BlockTypes.SNOW_LAYER||blockType ==BlockTypes.ICE||blockType == BlockTypes.SAND){
					return;
				}
			}

		while(var3.hasNext()) {
			Transaction<BlockSnapshot> transaction = (Transaction)var3.next();
			Location<World> transactionLocation = (Location)((BlockSnapshot)transaction.getFinal()).getLocation().get();
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			String timeInGMT = format.format(cal.getTime());
			Inspector.instance().getDatabaseManager().updateBlockInformation(transactionLocation.getBlockX(), transactionLocation.getBlockY(), transactionLocation.getBlockZ(), ((World)transactionLocation.getExtent()).getUniqueId(), player.getUniqueId(), player.getName(), timeInGMT, (BlockSnapshot)transaction.getOriginal(), (BlockSnapshot)transaction.getFinal());
		}

	}
}