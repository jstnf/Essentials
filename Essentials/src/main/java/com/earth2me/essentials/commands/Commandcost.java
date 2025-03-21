package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commandcost extends EssentialsCommand {
    public Commandcost() {
        super("cost");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        super.run(server, user, commandLabel, args);
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        super.run(server, sender, commandLabel, args);
    }
}
