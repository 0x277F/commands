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

import co.aikar.commands.annotation.Conditions;
import com.google.common.collect.Maps;

import java.util.Map;

public class ConditionContext <I extends CommandIssuer> {

    private final RegisteredCommand cmd;
    private final I issuer;
    private final Conditions condAnno;
    private final Map<String, String> flags;

    ConditionContext(RegisteredCommand cmd, I issuer, Conditions condAnno) {
        this.cmd = cmd;
        this.issuer = issuer;
        this.condAnno = condAnno;
        this.flags = Maps.newHashMap();
        for (String s : ACFPatterns.COMMA.split(cmd.scope.manager.getCommandReplacements().replace(condAnno.value()))) {
            String[] v = ACFPatterns.EQUALS.split(s, 2);
            this.flags.put(v[0], v.length > 1 ? v[1] : null);
        }
    }

    public I getIssuer() {
        return issuer;
    }

    public boolean hasFlag(String flag) {
        return flags.containsKey(flag);
    }

    public String getFlagValue(String flag, String def) {
        return flags.getOrDefault(flag, def);
    }

    public Integer getFlagValue(String flag, Integer def) {
        return ACFUtil.parseInt(this.flags.get(flag), def);
    }
}
