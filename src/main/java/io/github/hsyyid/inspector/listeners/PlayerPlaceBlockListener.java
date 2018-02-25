package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.Inspector;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent.Place;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerPlaceBlockListener {
	public PlayerPlaceBlockListener() {
	}

	@Listener
	public void onPlayerPlaceBlock(Place event, @First Player player) {
        if(!(Inspector.config.getNode("worlds").getNode(player.getWorld().getName()).getBoolean())){
            return;
        }
		Iterator var3 = event.getTransactions().iterator();
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
