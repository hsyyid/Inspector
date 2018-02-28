package io.github.hsyyid.inspector.cmdexecutors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class InspectorExecutor implements CommandExecutor {
	public InspectorExecutor() {
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
		src.sendMessage(Text.of(new Object[]{TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Version: ", TextColors.GOLD, ((PluginContainer)Sponge.getPluginManager().getPlugin("io.github.hsyyid.inspector").get()).getVersion()}));
		return CommandResult.success();
	}
}