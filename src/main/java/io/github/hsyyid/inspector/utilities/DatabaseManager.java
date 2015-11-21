package io.github.hsyyid.inspector.utilities;

import com.google.common.collect.Lists;
import io.github.hsyyid.inspector.Inspector;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import javax.sql.DataSource;

public class DatabaseManager
{
	public static void updateBlockInformation(UUID worldUUID, int x, int y, int z, UUID playerUUID, String playerName, String time, String newBlockID, int newBlockMeta, String oldBlockID, int oldBlockMeta)
	{
		if ((boolean) getConfigValue("database.mysql.enabled").orElse(false))
		{
			SqlService sql = Inspector.game.getServiceManager().provide(SqlService.class).get();
			String host = (String) getConfigValue("database.mysql.host").orElse("");
			String port = (String) getConfigValue("database.mysql.port").orElse("");
			String username = (String) getConfigValue("database.mysql.username").orElse("");
			String password = (String) getConfigValue("database.mysql.password").orElse("");
			String database = (String) getConfigValue("database.mysql.database").orElse("");

			try
			{
				DataSource datasource = sql.getDataSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password);

				String executeString = "CREATE TABLE IF NOT EXISTS BLOCKINFO" + 
									"(X             INT       NOT NULL," + 
									" Y             INT       NOT NULL," +
									" Z             INT       NOT NULL," + 
									" WORLDUUID     TEXT      NOT NULL," +
									" PLAYERUUID    TEXT      NOT NULL," + 
									" PLAYERNAME    TEXT      NOT NULL," +
									" TIME          TEXT      NOT NULL," +
									" NEWBLOCKID    TEXT      NOT NULL," +
									" NEWMETA       INT       NOT NULL," +
									" OLDBLOCKID    TEXT      NOT NULL," +
									" OLDMETA 		INT 	  NOT NULL)";
				execute(executeString, datasource);

				executeString = "INSERT INTO BLOCKINFO (X,Y,Z,WORLDUUID,PLAYERUUID,PLAYERNAME,TIME,NEWBLOCKID,NEWMETA,OLDBLOCKID,OLDMETA) " +
							"VALUES (" + x + "," + y + "," + z + ",'" + worldUUID.toString() + "','" + playerUUID.toString() + "'," +
							"'" + playerName + "','" + time + "','" + newBlockID + "'," + newBlockMeta + ",'" + oldBlockID + "'," + oldBlockMeta + ");";
				execute(executeString, datasource);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Class.forName("org.sqlite.JDBC");
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("[Inspector]: Error! You do not have any database software installed. This plugin cannot work correctly!");
				return;
			}

			try
			{
				Connection c = DriverManager.getConnection("jdbc:sqlite:Inspector.db");
				Statement stmt = c.createStatement();

				String sql = "CREATE TABLE IF NOT EXISTS BLOCKINFO" + 
					"(X             INT       NOT NULL," + 
					" Y             INT       NOT NULL," +
					" Z             INT       NOT NULL," + 
					" WORLDUUID     TEXT      NOT NULL," +
					" PLAYERUUID    TEXT      NOT NULL," + 
					" PLAYERNAME    TEXT      NOT NULL," +
					" TIME          TEXT      NOT NULL," +
					" NEWBLOCKID    TEXT      NOT NULL," +
					" NEWMETA       INT       NOT NULL," +
					" OLDBLOCKID    TEXT      NOT NULL," +
					" OLDMETA 		INT 	  NOT NULL)";
				stmt.executeUpdate(sql);

				sql = "INSERT INTO BLOCKINFO (X,Y,Z,WORLDUUID,PLAYERUUID,PLAYERNAME,TIME,NEWBLOCKID,NEWMETA,OLDBLOCKID,OLDMETA) " +
					"VALUES (" + x + "," + y + "," + z + ",'" + worldUUID.toString() + "','" + playerUUID.toString() + "'," +
					"'" + playerName + "','" + time + "','" + newBlockID + "'," + newBlockMeta + ",'" + oldBlockID + "'," + oldBlockMeta + ");";
				stmt.executeUpdate(sql);

				stmt.close();
				c.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Executes commands on DataSources
	 * @param execute - Command to execute
	 * @param datasource - DataSource to execute command on.
	 */
	public static void execute(String execute, DataSource datasource)
	{
		try
		{
			Connection connection = datasource.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(execute);
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static Optional<Object> getConfigValue(String configValue)
	{
		ConfigurationNode valueNode = Inspector.config.getNode((Object[]) (configValue).split("\\."));

		if (valueNode.getValue() != null)
		{
			return Optional.of(valueNode.getValue());
		}
		else
		{
			return Optional.empty();
		}
	}

	public static BlockInformation getBlockInformationAt(Location<World> location, String player, String timeInGMT)
	{
		BlockInformation lastPlayerEditedBlock = null;
		List<BlockInformation> blockInformation = getBlockInformationAt(location, timeInGMT);

		for (BlockInformation blockInfo : blockInformation)
		{
			if (blockInfo.getPlayerName().equals(player))
			{
				if (lastPlayerEditedBlock != null && wasChangedBefore(lastPlayerEditedBlock, blockInfo) && wasChangedBefore(blockInfo, timeInGMT))
				{
					lastPlayerEditedBlock = blockInfo;
				}
				else if (lastPlayerEditedBlock == null && wasChangedBefore(blockInfo, timeInGMT))
				{
					lastPlayerEditedBlock = blockInfo;
				}
			}
		}

		return lastPlayerEditedBlock;
	}

	public static List<BlockInformation> getBlockInformationAt(Location<World> location, String timeInGMT)
	{
		List<BlockInformation> blockInformation = Lists.newArrayList();

		if ((boolean) getConfigValue("database.mysql.enabled").orElse(false))
		{
			SqlService sql = Inspector.game.getServiceManager().provide(SqlService.class).get();
			String host = (String) getConfigValue("database.mysql.host").orElse("");
			String port = (String) getConfigValue("database.mysql.port").orElse("");
			String username = (String) getConfigValue("database.mysql.username").orElse("");
			String password = (String) getConfigValue("database.mysql.password").orElse("");
			String database = (String) getConfigValue("database.mysql.database").orElse("");

			try
			{
				DataSource datasource = sql.getDataSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password);

				DatabaseMetaData metadata = datasource.getConnection().getMetaData();
				ResultSet rs = metadata.getTables(null, null, "BLOCKINFO", null);

				while (rs.next())
				{
					int x = rs.getInt("x");
					int y = rs.getInt("y");
					int z = rs.getInt("z");
					UUID worldUUID = UUID.fromString(rs.getString("worldUUID"));
					Optional<World> world = Inspector.game.getServer().getWorld(worldUUID);

					if (x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ() && worldUUID.equals(location.getExtent().getUniqueId()) && world.isPresent())
					{
						BlockInformation blockInfo = new BlockInformation(new Location<World>(world.get(), x, y, z), rs.getString("newBlockID"), rs.getInt("newMeta"), rs.getString("oldBlockID"), rs.getInt("oldMeta"), rs.getString("time"), UUID.fromString(rs.getString("playerUUID")), rs.getString("playerName"));

						if (wasChangedBefore(blockInfo, timeInGMT))
							blockInformation.add(blockInfo);
					}
				}

			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Class.forName("org.sqlite.JDBC");
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("[Inspector]: Error! You do not have any database software installed. This plugin cannot work correctly!");
				return blockInformation;
			}

			try
			{
				Connection c = DriverManager.getConnection("jdbc:sqlite:Inspector.db");
				PreparedStatement stmt = c.prepareStatement("SELECT * FROM BLOCKINFO WHERE x=? AND y=? AND z=? AND worldUUID=?");
				stmt.setInt(1, location.getBlockX());
				stmt.setInt(2, location.getBlockY());
				stmt.setInt(3, location.getBlockZ());
				stmt.setString(4, location.getExtent().getUniqueId().toString());

				ResultSet rs = stmt.executeQuery();

				while (rs.next())
				{
					BlockInformation blockInfo = new BlockInformation(location, rs.getString("newBlockID"), rs.getInt("newMeta"), rs.getString("oldBlockID"), rs.getInt("oldMeta"), rs.getString("time"), UUID.fromString(rs.getString("playerUUID")), rs.getString("playerName"));

					if (wasChangedBefore(blockInfo, timeInGMT))
						blockInformation.add(blockInfo);
				}

				stmt.close();
				c.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return blockInformation;
	}

	public static List<BlockInformation> getBlockInformationAt(Location<World> location)
	{
		List<BlockInformation> blockInformation = Lists.newArrayList();

		if ((boolean) getConfigValue("database.mysql.enabled").orElse(false))
		{
			SqlService sql = Inspector.game.getServiceManager().provide(SqlService.class).get();
			String host = (String) getConfigValue("database.mysql.host").orElse("");
			String port = (String) getConfigValue("database.mysql.port").orElse("");
			String username = (String) getConfigValue("database.mysql.username").orElse("");
			String password = (String) getConfigValue("database.mysql.password").orElse("");
			String database = (String) getConfigValue("database.mysql.database").orElse("");

			try
			{
				DataSource datasource = sql.getDataSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password);

				DatabaseMetaData metadata = datasource.getConnection().getMetaData();
				ResultSet rs = metadata.getTables(null, null, "BLOCKINFO", null);

				while (rs.next())
				{
					int x = rs.getInt("x");
					int y = rs.getInt("y");
					int z = rs.getInt("z");
					UUID worldUUID = UUID.fromString(rs.getString("worldUUID"));
					Optional<World> world = Inspector.game.getServer().getWorld(worldUUID);

					if (x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ() && worldUUID.equals(location.getExtent().getUniqueId()) && world.isPresent())
					{
						blockInformation.add(new BlockInformation(new Location<World>(world.get(), x, y, z), rs.getString("newBlockID"), rs.getInt("newMeta"), rs.getString("oldBlockID"), rs.getInt("oldMeta"), rs.getString("time"), UUID.fromString(rs.getString("playerUUID")), rs.getString("playerName")));
					}
				}

			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Class.forName("org.sqlite.JDBC");
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("[Inspector]: Error! You do not have any database software installed. This plugin cannot work correctly!");
				return blockInformation;
			}

			try
			{
				Connection c = DriverManager.getConnection("jdbc:sqlite:Inspector.db");
				PreparedStatement stmt = c.prepareStatement("SELECT * FROM BLOCKINFO WHERE x=? AND y=? AND z=? AND worldUUID=?");
				stmt.setInt(1, location.getBlockX());
				stmt.setInt(2, location.getBlockY());
				stmt.setInt(3, location.getBlockZ());
				stmt.setString(4, location.getExtent().getUniqueId().toString());

				ResultSet rs = stmt.executeQuery();

				while (rs.next())
				{
					blockInformation.add(new BlockInformation(location, rs.getString("newBlockID"), rs.getInt("newMeta"), rs.getString("oldBlockID"), rs.getInt("oldMeta"), rs.getString("time"), UUID.fromString(rs.getString("playerUUID")), rs.getString("playerName")));
				}

				stmt.close();
				c.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return blockInformation;
	}

	public static void addPointOrCreateRegionOf(UUID playerUUID, Location<World> point, boolean secondary)
	{
		if (secondary)
		{
			for (Region region : Inspector.regions)
			{
				if (region.getOwner().equals(playerUUID))
				{
					region.setPointB(point);
					return;
				}
			}

			Inspector.regions.add(new Region(playerUUID, null, point));
		}
		else
		{
			for (Region region : Inspector.regions)
			{
				if (region.getOwner().equals(playerUUID))
				{
					region.setPointA(point);
					return;
				}
			}

			Inspector.regions.add(new Region(playerUUID, point, null));
		}
	}

	public static Optional<Region> getRegion(UUID playerUUID)
	{
		for (Region region : Inspector.regions)
		{
			if (region.getOwner().equals(playerUUID))
			{
				return Optional.of(region);
			}
		}

		return Optional.empty();
	}

	public static boolean wasChangedBefore(BlockInformation blockInformationA, BlockInformation blockInformationB)
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date blockInfoATime = format.parse(blockInformationA.getTimeEdited());
			Date blockInfoBTime = format.parse(blockInformationB.getTimeEdited());
			long duration = blockInfoBTime.getTime() - blockInfoATime.getTime();
			return duration >= 0;
		}
		catch (ParseException e)
		{
			return false;
		}
	}

	public static boolean wasChangedBefore(BlockInformation blockInformationA, String timeInGMT)
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date blockInfoATime = format.parse(blockInformationA.getTimeEdited());
			Date blockInfoBTime = format.parse(timeInGMT);
			long duration = blockInfoBTime.getTime() - blockInfoATime.getTime();
			return duration >= 0;
		}
		catch (ParseException e)
		{
			return false;
		}
	}
}
