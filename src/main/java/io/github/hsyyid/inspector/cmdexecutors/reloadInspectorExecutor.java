package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.io.IOException;

public class reloadInspectorExecutor implements CommandExecutor {
    Inspector inspector;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
         inspector = Inspector.instance();
        try {
            inspector.loadConfig();
            src.sendMessage(Text.of("Inspecter config reloaded"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommandResult.success();
    }
}
