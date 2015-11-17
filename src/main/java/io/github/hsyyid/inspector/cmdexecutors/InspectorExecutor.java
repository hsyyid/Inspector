package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

public class InspectorExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		src.sendMessage(Texts.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Version: ", TextColors.GOLD, Inspector.game.getPluginManager().getPlugin("Inspector").get().getVersion()));
		return CommandResult.success();
	}
}
