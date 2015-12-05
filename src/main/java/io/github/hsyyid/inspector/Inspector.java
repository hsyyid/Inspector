package io.github.hsyyid.inspector;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.github.hsyyid.inspector.cmdexecutors.InspectorExecutor;
import io.github.hsyyid.inspector.cmdexecutors.RollbackExecutor;
import io.github.hsyyid.inspector.cmdexecutors.ToggleInspectorExecutor;
import io.github.hsyyid.inspector.listeners.PlayerBreakBlockListener;
import io.github.hsyyid.inspector.listeners.PlayerInteractBlockListener;
import io.github.hsyyid.inspector.listeners.PlayerPlaceBlockListener;
import io.github.hsyyid.inspector.utilities.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Plugin(id = "Inspector", name = "Inspector", version = "0.1")
public class Inspector
{
	public static Game game;
	public static ConfigurationNode config;
	public static ConfigurationLoader<CommentedConfigurationNode> configurationManager;
	public static Set<UUID> inspectorEnabledPlayers = Sets.newHashSet();
	public static Set<Region> regions = Sets.newHashSet();
	
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
	public void onGameInit(GameInitializationEvent event)
	{
		getLogger().info("Inspector loading..");

		game = event.getGame();

		try
		{
			if (!dConfig.exists())
			{
				dConfig.createNewFile();
				config = confManager.load();
				config.getNode("database", "mysql", "enabled").setValue(false);
				config.getNode("database", "mysql", "host").setValue("localhost");
				config.getNode("database", "mysql", "port").setValue("8080");
				config.getNode("database", "mysql", "username").setValue("username");
				config.getNode("database", "mysql", "password").setValue("pass");
				config.getNode("database", "mysql", "database").setValue("Inspector");
				config.getNode("inspector", "select", "tool").setValue("minecraft:diamond_hoe");
				confManager.save(config);
			}
			configurationManager = confManager;
			config = confManager.load();

		}
		catch (IOException exception)
		{
			getLogger().error("The default configuration could not be loaded or created!");
		}

		HashMap<List<String>, CommandSpec> inspectorSubcommands = new HashMap<List<String>, CommandSpec>();

		inspectorSubcommands.put(Arrays.asList("toggle"), CommandSpec.builder()
		 		.description(Texts.of("Toggle Inspector Command"))
				.permission("inspector.toggle")
		 		.executor(new ToggleInspectorExecutor())
		 		.build());
		
		inspectorSubcommands.put(Arrays.asList("rollback"), CommandSpec.builder()
	 		.description(Texts.of("Rollback Command"))
			.permission("inspector.rollback")
			.arguments(GenericArguments.seq(
				GenericArguments.onlyOne(GenericArguments.string(Texts.of("time"))),
				GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Texts.of("player"))))))
	 		.executor(new RollbackExecutor())
	 		.build());

		CommandSpec inspectorCommandSpec = CommandSpec.builder()
				.description(Texts.of("Inspector Command"))
				.permission("inspector.use")
				.executor(new InspectorExecutor())
				.children(inspectorSubcommands)
				.build();

		game.getCommandManager().register(this, inspectorCommandSpec, "inspector", "ins", "insp");

		game.getEventManager().registerListeners(this, new PlayerPlaceBlockListener());
		game.getEventManager().registerListeners(this, new PlayerInteractBlockListener());
		game.getEventManager().registerListeners(this, new PlayerBreakBlockListener());

		getLogger().info("-----------------------------");
		getLogger().info("Inspector was made by HassanS6000!");
		getLogger().info("Please post all errors on the Sponge Thread or on GitHub!");
		getLogger().info("Have fun, and enjoy! :D");
		getLogger().info("-----------------------------");
		getLogger().info("Inspector loaded!");
	}

	public static ConfigurationLoader<CommentedConfigurationNode> getConfigManager()
	{
		return configurationManager;
	}
}
