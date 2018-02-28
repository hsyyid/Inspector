package io.github.hsyyid.inspector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import io.github.hsyyid.inspector.cmdexecutors.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import io.github.hsyyid.inspector.listeners.ExplosionListener;
import io.github.hsyyid.inspector.listeners.PlayerBreakBlockListener;
import io.github.hsyyid.inspector.listeners.PlayerInteractBlockListener;
import io.github.hsyyid.inspector.listeners.PlayerJoinListener;
import io.github.hsyyid.inspector.listeners.PlayerPlaceBlockListener;
import io.github.hsyyid.inspector.utilities.DatabaseManager;
import io.github.hsyyid.inspector.utilities.Region;
import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.world.World;

@Updatifier(repoName = "Inspector", repoOwner = "hsyyid", version = "v" + PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, dependencies = @Dependency(id = "Updatifier", version = "1.0", optional = true) )
public class Inspector
{
	private DatabaseManager databaseManager;

	private static Inspector instance;

	public static CommentedConfigurationNode config;
	public static ConfigurationLoader<CommentedConfigurationNode> configurationManager;
	public static Set<UUID> inspectorEnabledPlayers = Sets.newHashSet();
	public static Set<Region> regions = Sets.newHashSet();

	private Set<String> blockWhiteList = Sets.newHashSet();

	public DatabaseManager getDatabaseManager()
	{
		return databaseManager;
	}

	public static Inspector instance()
	{
		return instance;
	}

	@Inject
	private PluginContainer pluginContainer;

	public PluginContainer getPluginContainer()
	{
		return pluginContainer;
	}

	@Inject
	private Logger logger;

	public Logger getLogger()
	{
		return logger;
	}

	@Inject
	@DefaultConfig(sharedRoot = true)
	private File dConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> confManager;

	@Listener
	public void onGameInit(GameStartedServerEvent event)
	{
		getLogger().info("Inspector loading...");
		instance = this;
		this.databaseManager = new DatabaseManager();

		try
		{
            loadConfig();

		}
		catch (IOException exception)
		{
			getLogger().error("The default configuration could not be loaded or created!");
		}

		HashMap<List<String>, CommandSpec> inspectorSubcommands = new HashMap<List<String>, CommandSpec>();

        inspectorSubcommands.put(Arrays.asList("reload"), CommandSpec.builder()
                .description(Text.of("reload Inspector"))
                .permission("inspector.reload")
                .executor(new reloadInspectorExecutor())
                .build());

		inspectorSubcommands.put(Arrays.asList("pruge"), CommandSpec.builder()
				.description(Text.of("clean up the expired data"))
				.permission("inspector.purge")
				.arguments(GenericArguments.integer(Text.of("Hour")))
				.executor(new purgeInspectorExecutor())
				.build());

		inspectorSubcommands.put(Arrays.asList("toggle"), CommandSpec.builder()
			.description(Text.of("Toggle Inspector Command"))
			.permission("inspector.toggle")
			.executor(new ToggleInspectorExecutor())
			.build());

		inspectorSubcommands.put(Arrays.asList("rollback"), CommandSpec.builder()
			.description(Text.of("Rollback Command"))
			.permission("inspector.rollback")
			.arguments(GenericArguments.seq(GenericArguments.onlyOne(GenericArguments.user(Text.of("player"))), GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Text.of("time"))))))
			.executor(new RollbackExecutor())
			.build());

		CommandSpec inspectorCommandSpec = CommandSpec.builder()
			.description(Text.of("Inspector Command"))
			.permission("inspector.use")
			.executor(new InspectorExecutor())
			.children(inspectorSubcommands)
			.build();

		Sponge.getCommandManager().register(this, inspectorCommandSpec, "inspector", "ins", "insp");

		Sponge.getEventManager().registerListeners(this, new PlayerPlaceBlockListener());
		Sponge.getEventManager().registerListeners(this, new PlayerInteractBlockListener());
		Sponge.getEventManager().registerListeners(this, new PlayerBreakBlockListener());
		Sponge.getEventManager().registerListeners(this, new ExplosionListener());
		Sponge.getEventManager().registerListeners(this, new PlayerJoinListener());

		getLogger().info("-----------------------------");
		getLogger().info("Inspector was created by HassanS6000!");
        getLogger().info("This version was improved by Tollainmear!");
		getLogger().info("Please post all errors on the Sponge Thread or on GitHub!");
		getLogger().info("Have fun, and enjoy! :D");
		getLogger().info("-----------------------------");
		getLogger().info("Inspector loaded!");
		getLogger().warn("The Inspector you using now was NOT from the Official Thread!");
		getLogger().warn("Post any issues you found to mcbbs(http://mcbbs.tvt.im/forum.php?mod=redirect&goto=findpost&ptid=660997&pid=12750382)");
		getLogger().warn("Thank for using Inspector Unofficial Thread Improved by Tollainmear!");
	}

	@Listener
	public void onServerShuttingDown(GameStoppingServerEvent event){
		if (config.getNode("auto_Purge").getNode("Enable").getBoolean()){
			int hours = config.getNode("auto_Purge").getNode("timeThreshold").getInt();
			long timeThreshold = Calendar.getInstance().getTimeInMillis() - hours * 3600000;
			getDatabaseManager().clearExpiredData(timeThreshold);
		}
	}

    public void loadConfig() throws IOException {
		if (!dConfig.exists()) {
			dConfig.createNewFile();
			config = confManager.load();
			config.getNode("database", "mysql", "enabled").setValue(false);
			config.getNode("database", "mysql", "host").setValue("localhost");
			config.getNode("database", "mysql", "port").setValue("8080");
			config.getNode("database", "mysql", "username").setValue("username");
			config.getNode("database", "mysql", "password").setValue("pass");
			config.getNode("database", "mysql", "database").setValue("Inspector");
			config.getNode("inspector", "select", "tool").setValue("minecraft:diamond_hoe");
			Iterator<World> ite = Sponge.getServer().getWorlds().iterator();
			while (ite.hasNext()) {
				config.getNode("worlds").getNode(ite.next().getName()).setValue(false);
			}
			config.getNode("blockWhiteList").setValue("");
			confManager.save(config);
		}
		configurationManager = confManager;
		config = confManager.load();
		config.getNode("-Announcement").setValue("The inspector you using now was NOT from the Official Thread!\nPost any issues you found to mcbbs(http://mcbbs.tvt.im/forum.php?mod=redirect&goto=findpost&ptid=660997&pid=12750382)");
		if (config.getNode("blockWhiteList").isVirtual()) {
			blockWhiteList.add("minecraft:bedrock");
			config.getNode("blockWhiteList").setValue(blockWhiteList).setComment("Attention: the block type here should be specified the unsafeDamage,it means that if you want add Stone here, you should put \"minecraft:stone:0\" instead of\"minecraft:stone\"!");
		} else
			blockWhiteList = config.getNode("blockWhiteList").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toSet());

		if (config.getNode("auto_Purge").isVirtual()) {
			config.getNode("auto_Purge").getNode("timeThreshold").setValue(72).setComment("Time,in hours,inspector will automatically clean up the data before this time threshold(default 72)");
			config.getNode("auto_Purge").getNode("Enable").setValue(true).setComment("if enabled,inspector will automatically clean up the expired data when server is closing(default true)");
			confManager.save(config);
		}
	}

    public static ConfigurationLoader<CommentedConfigurationNode> getConfigManager()
	{
		return configurationManager;
	}

	public Set<String> getBlockWhiteList() {
		return blockWhiteList;
	}
}
