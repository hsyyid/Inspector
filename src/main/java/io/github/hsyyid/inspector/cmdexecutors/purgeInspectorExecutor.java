package io.github.hsyyid.inspector.cmdexecutors;

import io.github.hsyyid.inspector.Inspector;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Calendar;

public class purgeInspectorExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final long msPerhour = 3600000;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long purgeThreshold = timeNow - (int)args.getOne(Text.of("Hour")).get()*msPerhour;
        if (src instanceof Player){
            Inspector.instance().getLogger().info(((Player)src).getName()+" attempt to run the purge command to clean the data before "+(int)args.getOne(Text.of("Hour")).get()+" hours ago");
        }else Inspector.instance().getLogger().info("Run the purge command from Console, attempt to clean the data before "+(int)args.getOne(Text.of("Hour")).get()+" hours ago");
        Inspector.instance().getDatabaseManager().clearExpiredData(purgeThreshold);
        src.sendMessage(Text.of(TextColors.GREEN,"Inspector data was clean up"));
        return CommandResult.success();
    }
}
