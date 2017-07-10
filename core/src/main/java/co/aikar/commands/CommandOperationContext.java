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

import java.lang.annotation.Annotation;

/**
 * Holds information about the currently executing command on this thread
 */
public class CommandOperationContext {

    private final CommandManager manager;
    private final CommandIssuer issuer;
    private final BaseCommand command;
    private final String commandLabel;
    private final String[] args;
    private RegisteredCommand registeredCommand;

    CommandOperationContext(CommandManager manager, CommandIssuer issuer, BaseCommand command, String commandLabel, String[] args) {
        this.manager = manager;
        this.issuer = issuer;
        this.command = command;
        this.commandLabel = commandLabel;
        this.args = args;
    }

    public CommandManager getCommandManager() {
        return manager;
    }

    public CommandIssuer getCommandIssuer() {
        return issuer;
    }

    public BaseCommand getCommand() {
        return command;
    }

    public String getCommandLabel() {
        return commandLabel;
    }

    public String[] getArgs() {
        return args;
    }

    public void setRegisteredCommand(RegisteredCommand registeredCommand) {
        this.registeredCommand = registeredCommand;
    }

    public RegisteredCommand getRegisteredCommand() {
        return registeredCommand;
    }

    public <T extends Annotation> T getAnnotation(Class<T> anno) {
        return registeredCommand.method.getAnnotation(anno);
    }

    public boolean hasAnnotation(Class<? extends Annotation> anno) {
        return getAnnotation(anno) != null;
    }

}
