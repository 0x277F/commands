/*
 * Copyright (c) 2016-2017 Daniel Ennis (Aikar) - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package co.aikar.commands;

import co.aikar.commands.apachecommonslang.ApacheCommonsLangUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class BungeeRootCommand extends Command implements RootCommand, TabExecutor {

    private final BungeeCommandManager manager;
    private final String name;
    private BaseCommand defCommand;
    private Map<String, BaseCommand> subCommands = new HashMap<>();
    private List<BaseCommand> children = new ArrayList<>();
    boolean isRegistered = false;

    BungeeRootCommand(BungeeCommandManager manager, String name) {
        super(name);
        this.manager = manager;
        this.name = name;
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Override
    public void addChild(BaseCommand command) {
        if (this.defCommand == null || !command.subCommands.get("__default").isEmpty()) {
            this.defCommand = command;

        }
        this.addChildShared(this.children, this.subCommands, command);
    }

    @Override
    public CommandManager getManager() {
        return manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        execute(new BungeeCommandIssuer(manager, sender), getName(), args);
    }

    private void execute(CommandIssuer sender, String commandLabel, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            String checkSub = ApacheCommonsLangUtil.join(args, " ", 0, i).toLowerCase();
            BaseCommand subHandler = this.subCommands.get(checkSub);
            if (subHandler != null) {
                subHandler.execute(sender, commandLabel, args);
                return;
            }
        }

        this.defCommand.execute(sender, commandLabel, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return onTabComplete(new BungeeCommandIssuer(manager, commandSender), getName(), strings);
    }

    private List<String> onTabComplete(CommandIssuer sender, String alias, String[] args) throws IllegalArgumentException {
        Set<String> completions = new HashSet<>();
        this.children.forEach(child -> completions.addAll(child.tabComplete(sender, alias, args)));
        return new ArrayList<>(completions);
    }
}
