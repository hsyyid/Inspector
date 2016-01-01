package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ToggleInspectorExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			
			if (Inspector.inspectorEnabledPlayers.contains(player.getUniqueId()))
			{
				Inspector.inspectorEnabledPlayers.remove(player.getUniqueId());
				player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Toggled inspector ", TextColors.GOLD, "off."));
			}
			else
			{
				Inspector.inspectorEnabledPlayers.add(player.getUniqueId());
				player.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.GRAY, "Toggled inspector ", TextColors.GOLD, "on."));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.BLUE, "[Inspector]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You may only use this command as an in-game player."));
		}

		return CommandResult.success();
	}
}
