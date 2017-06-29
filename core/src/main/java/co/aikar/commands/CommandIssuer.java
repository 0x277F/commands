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

import co.aikar.locales.MessageKey;

public interface CommandIssuer {
    /**
     * Gets the issuer in the platforms native object
     * @param <T>
     * @return
     */
    <T> T getIssuer();

    CommandManager getManager();

    /**
     * Is this issue a player, or server/console sender
     * @return
     */
    boolean isPlayer();

    /**
     * Send the Command Issuer a message
     * @param message
     */
    default void sendMessage(String message) {
        sendMessage(MessageType.ERROR, message);
    }

    /**
     * Has permission node
     * @param permission
     * @return
     */
    boolean hasPermission(String permission);

    default void sendMessage(MessageType type, MessageKey key, String... replacements) {
        getManager().sendMessage(this, type, key, replacements);
    }

    void sendMessage(MessageType type, String message);

    default String format(CommandManager manager, MessageType type,  String message) {
        MessageFormatter formatter = manager.formatters.get(type);
        if (formatter != null) {
            message = formatter.format(message);
        }
        return message;
    }
}
