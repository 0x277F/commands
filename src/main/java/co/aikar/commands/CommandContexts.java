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

import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Split;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Values;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.commands.contexts.OnlinePlayer;
import co.aikar.commands.contexts.SenderAwareContextResolver;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class CommandContexts {
    private final CommandManager manager;
    private final Map<Class<?>, ContextResolver<?>> contextMap = Maps.newHashMap();

    CommandContexts(CommandManager manager) {
        this.manager = manager;
        registerContext(Integer.class, (c) -> {
            try {
                return CommandUtil.parseNumber(c.popFirstArg(), c.hasFlag("suffixes")).intValue();
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument("Must be a number");
            }
        });
        registerContext(Long.class, (c) -> {
            try {
                return CommandUtil.parseNumber(c.popFirstArg(), c.hasFlag("suffixes")).longValue();
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument("Must be a number");
            }

        });
        registerContext(Float.class, (c) -> {
            try {
                return CommandUtil.parseNumber(c.popFirstArg(), c.hasFlag("suffixes")).floatValue();
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument("Must be a number");
            }
        });
        registerContext(Double.class, (c) -> {
            try {
                return CommandUtil.parseNumber(c.popFirstArg(), c.hasFlag("suffixes")).doubleValue();
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument("Must be a number");
            }
        });
        registerContext(Number.class, (c) -> {
            try {
                return CommandUtil.parseNumber(c.popFirstArg(), c.hasFlag("suffixes"));
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument("Must be a number");
            }
        });
        registerContext(Boolean.class, (c) -> CommandUtil.isTruthy(c.popFirstArg()));
        registerContext(String.class, (c) -> {
            final Values values = c.getParam().getAnnotation(Values.class);
            if (values != null) {
                return c.popFirstArg();
            }
            if (c.isLastArg() && c.getParam().getAnnotation(Single.class) == null) {
                return CommandUtil.join(c.getArgs());
            }
            return c.popFirstArg();
        });
        registerContext(String[].class, (c) -> {
            String val;
            if (c.isLastArg() && c.getParam().getAnnotation(Single.class) == null) {
                val = CommandUtil.join(c.getArgs());
            } else {
                val = c.popFirstArg();
            }
            Split split = c.getParam().getAnnotation(Split.class);
            if (split != null) {
                if (val.isEmpty()) {
                    throw new InvalidCommandArgument();
                }
                return CommandPatterns.getPattern(split.value()).split(val);
            } else if (!c.isLastArg()) {
                CommandUtil.sneaky(new InvalidConfigurationException("Weird Command signature... String[] should be last or @Split"));
            }

            String[] result = c.getArgs().toArray(new String[c.getArgs().size()]);
            c.getArgs().clear();
            return result;
        });

        registerContext(Enum.class, (c) -> {
            final String first = c.popFirstArg();
            Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) c.getParam().getType();
            Enum<?> match = CommandUtil.simpleMatch(enumCls, first);
            if (match == null) {
                List<String> names = CommandUtil.enumNames(enumCls);
                throw new InvalidCommandArgument("Please specify one of: " + CommandUtil.join(names));
            }
            return match;
        });
    }

    public <T> void registerSenderAwareContext(Class<T> context, SenderAwareContextResolver<T> supplier) {
        contextMap.put(context, supplier);
    }
    public <T> void registerContext(Class<T> context, ContextResolver<T> supplier) {
        contextMap.put(context, supplier);
    }

    public ContextResolver<?> getResolver(Class<?> type) {
        Class<?> rootType = type;
        do {
            if (type == Object.class) {
                break;
            }

            final ContextResolver<?> resolver = contextMap.get(type);
            if (resolver != null) {
                return resolver;
            }
        } while ((type = type.getSuperclass()) != null);

        CommandLog.exception(new InvalidConfigurationException("No context resolver defined for " + rootType.getName()));
        return null;
    }
}
