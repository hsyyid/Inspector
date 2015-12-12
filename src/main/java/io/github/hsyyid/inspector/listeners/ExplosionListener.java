package io.github.hsyyid.inspector.listeners;

import io.github.hsyyid.inspector.utilities.DatabaseManager;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ExplosionListener
{
	@Listener
	public void onExplosion(ExplosionEvent.Detonate event)
	{
		if (event.getCause().first(Player.class).isPresent())
		{
			Player player = event.getCause().first(Player.class).get();

			for (Transaction<BlockSnapshot> transaction : event.getTransactions())
			{
				Location<World> transactionLocation = transaction.getFinal().getLocation().get();
				BlockType oldBlockType = transaction.getOriginal().getState().getType();
				int oldBlockMeta = -1;
				
				if(transaction.getFinal().getState().toContainer().get(new DataQuery("UnsafeMeta")).isPresent())
					oldBlockMeta = (Integer) (transaction.getFinal().getState().toContainer().get(new DataQuery("UnsafeMeta")).get());
				
				BlockType newBlockType = transaction.getFinal().getState().getType();
				int newBlockMeta = -1;
				
				if(transaction.getFinal().getState().toContainer().get(new DataQuery("UnsafeMeta")).isPresent())
					newBlockMeta = (Integer) (transaction.getFinal().getState().toContainer().get(new DataQuery("UnsafeMeta")).get());

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				String timeInGMT = format.format(cal.getTime());
				
				DatabaseManager.updateBlockInformation(transactionLocation.getExtent().getUniqueId(), transactionLocation.getBlockX(), transactionLocation.getBlockY(), transactionLocation.getBlockZ(), player.getUniqueId(), player.getName(), timeInGMT, newBlockType.getName(), newBlockMeta, oldBlockType.getName(), oldBlockMeta);
			}
		}
	}
}
