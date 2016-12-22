/*
 * Copyright (c) 2016. Starlis LLC / dba Empire Minecraft
 *
 * This source code is proprietary software and must not be redistributed without Starlis LLC's approval
 *
 */

package co.aikar.commands;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public final class CommandUtil {

    public static final Random RANDOM = new Random();

    private CommandUtil() {}

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String formatNumber(Integer balance) {
        return NumberFormat.getInstance().format(balance);
    }

    public static String formatLocation(Location loc) {
        if (loc == null) {
            return null;
        }
        return loc.getWorld().getName() +
                ":" +
                loc.getBlockX() +
                "," +
                loc.getBlockY() +
                "," +
                loc.getBlockZ();
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMsg(CommandSender player, String message) {
        message = color(message);
        if (player == null) {
            for (String msg : Patterns.NEWLINE.split(message)) {
                Log.info(msg);
            }
        } else {
            for (String msg : Patterns.NEWLINE.split(message)) {
                player.sendMessage(msg);
            }
        }
    }

    public static Location stringToLocation(String storedLoc) {
        return stringToLocation(storedLoc, null);
    }
    public static Location stringToLocation(String storedLoc, World forcedWorld) {
        if (storedLoc == null) {
            return null;
        }
        String[] args = Patterns.COLON.split(storedLoc);
        if (args.length >= 4 || (args.length == 3 && forcedWorld != null)) {
            String world = forcedWorld != null ? forcedWorld.getName() : args[0];
            int i = args.length == 3 ? 0 : 1;
            double x = Double.parseDouble(args[i]);
            double y = Double.parseDouble(args[i + 1]);
            double z = Double.parseDouble(args[i + 2]);
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            if (args.length >= 6) {
                loc.setPitch(Float.parseFloat(args[4]));
                loc.setYaw(Float.parseFloat(args[5]));
            }
            return loc;
        } else if (args.length == 2) {
            String[] args2 = Patterns.COMMA.split(args[1]);
            if (args2.length == 3) {
                String world = forcedWorld != null ? forcedWorld.getName() : args[0];
                double x = Double.parseDouble(args2[0]);
                double y = Double.parseDouble(args2[1]);
                double z = Double.parseDouble(args2[2]);
                return new Location(Bukkit.getWorld(world), x, y, z);
            }
        }
        return null;
    }

    public static String fullLocationToString(Location loc) {
        if (loc == null) {
            return null;
        }
        return (new StringBuilder(64))
                .append(loc.getWorld().getName())
                .append(':')
                .append(NumUtil.precision(loc.getX(), 4))
                .append(':')
                .append(NumUtil.precision(loc.getY(), 4))
                .append(':')
                .append(NumUtil.precision(loc.getZ(), 4))
                .append(':')
                .append(NumUtil.precision(loc.getPitch(), 4))
                .append(':')
                .append(NumUtil.precision(loc.getYaw(), 4))
                .toString();
    }

    public static String fullBlockLocationToString(Location loc) {
        if (loc == null) {
            return null;
        }
        return (new StringBuilder(64))
                .append(loc.getWorld().getName())
                .append(':')
                .append(loc.getBlockX())
                .append(':')
                .append(loc.getBlockY())
                .append(':')
                .append(loc.getBlockZ())
                .append(':')
                .append(NumUtil.precision(loc.getPitch(), 4))
                .append(':')
                .append(NumUtil.precision(loc.getYaw(), 4))
                .toString();
    }

    public static String blockLocationToString(Location loc) {
        if (loc == null) {
            return null;
        }

        return (new StringBuilder(32))
                .append(loc.getWorld().getName())
                .append(':')
                .append(loc.getBlockX())
                .append(':')
                .append(loc.getBlockY())
                .append(':')
                .append(loc.getBlockZ())
                .toString();
    }

    public static <T extends Enum> T getEnumFromName(T[] types, String name) {
        return getEnumFromName(types, name, null);
    }
    public static <T extends Enum> T getEnumFromName(T[] types, String name, T def) {
        for (T type : types) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return def;
    }
    public static <T extends Enum> T getEnumFromOrdinal(T[] types, int ordinal) {
        for (T type : types) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        return null;
    }

    public static String ucfirst(String str) {
        return WordUtils.capitalizeFully(str);
    }

    public static Double parseDouble(String var) {
        return parseDouble(var, null);
    }

    public static Double parseDouble(String var, Double def) {
        if (var == null) {
            return def;
        }
        try {
            return Double.parseDouble(var);
        } catch (NumberFormatException ignored) {}
        return def;
    }

    public static Float parseFloat(String var) {
        return parseFloat(var, null);
    }
    public static Float parseFloat(String var, Float def) {
        if (var == null) {
            return def;
        }
        try {
            return Float.parseFloat(var);
        } catch (NumberFormatException ignored) {}
        return def;
    }
    public static Long parseLong(String var) {
        return parseLong(var, null);
    }
    public static Long parseLong(String var, Long def) {
        if (var == null) {
            return def;
        }
        try {
            return Long.parseLong(var);
        } catch (NumberFormatException ignored) {}
        return def;
    }

    public static Integer parseInt(String var) {
        return parseInt(var, null);
    }
    public static Integer parseInt(String var, Integer def) {
        if (var == null) {
            return def;
        }
        try {
            return Integer.parseInt(var);
        } catch (NumberFormatException ignored) {}
        return def;
    }

    public static boolean randBool() {
        return RANDOM.nextBoolean();
    }

    public static <T> T nullDefault(Object val, Object def) {
        return (T) (val != null ? val : def);
    }

    public static String join(Collection<String> args) {
        return StringUtils.join(args, " ");
    }
    public static String join(Collection<String> args, String sep) {
        return StringUtils.join(args, sep);
    }
    public static String join(String[] args) {
        return join(args, 0, ' ');
    }

    public static String join(String[] args, String sep) {
        return StringUtils.join(args, sep);
    }
    public static String join(String[] args, char sep) {
        return join(args, 0, sep);
    }

    public static String join(String[] args, int index) {
        return join(args, index, ' ');
    }

    public static String join(String[] args, int index, char sep) {
        return StringUtils.join(args, sep, index, args.length);
    }

    public static String simplifyString(String str) {
        if (str == null) {
            return null;
        }
        return Patterns.NON_ALPHA_NUMERIC.matcher(str.toLowerCase()).replaceAll("");
    }

    public static double round(double x, int scale) {
        try {
            return (new BigDecimal
                    (Double.toString(x))
                    .setScale(scale, BigDecimal.ROUND_HALF_UP))
                    .doubleValue();
        } catch (NumberFormatException ex) {
            if (Double.isInfinite(x)) {
                return x;
            } else {
                return Double.NaN;
            }
        }
    }
    public static int roundUp(int num, int multiple) {
        if(multiple == 0) {
            return num;
        }

        int remainder = num % multiple;
        if (remainder == 0) {
            return num;
        }
        return num + multiple - remainder;

    }

    public static String removeColors(String msg) {
        return ChatColor.stripColor(CommandUtil.color(msg));

    }

    public static String replaceChatString(String message, String replace, String with) {
        return replaceChatString(message, Pattern.compile(Pattern.quote(replace), Pattern.CASE_INSENSITIVE), with);
    }
    public static String replaceChatString(String message, Pattern replace, String with) {
        final String[] split = replace.split(message + "1");

        if (split.length < 2) {
            return replace.matcher(message).replaceAll(with);
        }
        message = split[0];

        for (int i = 1; i < split.length; i++) {
            final String prev = ChatColor.getLastColors(message);
            message += with + prev + split[i];
        }
        return message.substring(0, message.length() - 1);
    }

    public static boolean isWithinDistance(@NotNull Player p1, @NotNull Player p2, int dist) {
        return isWithinDistance(p1.getLocation(), p2.getLocation(), dist);
    }
    public static boolean isWithinDistance(@NotNull Location loc1, @NotNull Location loc2, int dist) {
        return loc1.getWorld() == loc2.getWorld() && loc1.distance(loc2) <= dist;
    }

    public static String limit(String str, int limit) {
        return str.length() > limit ? str.substring(0, limit) : str;
    }

    /**
     * Plain string replacement, escapes replace value.
     * @param string
     * @param pattern
     * @param repl
     * @return
     */
    public static String replace(String string, Pattern pattern, String repl) {
        return pattern.matcher(string).replaceAll(Matcher.quoteReplacement(repl));
    }

    /**
     * Regex version of {@link #replace(String, Pattern, String)}
     * @param string
     * @param pattern
     * @param repl
     * @return
     */
    public static String replacePattern(String string, Pattern pattern, String repl) {
        return pattern.matcher(string).replaceAll(repl);
    }

    /**
     * Plain String replacement. If you need regex patterns, see {@link #replacePattern(String, String, String)}
     * @param string
     * @param pattern
     * @param repl
     * @return
     */
    public static String replace(String string, String pattern, String repl) {
        return replace(string, Patterns.getPattern(Pattern.quote(pattern)), repl);
    }

    /**
     * Regex version of {@link #replace(String, String, String)}
     * @param string
     * @param pattern
     * @param repl
     * @return
     */
    public static String replacePattern(String string, String pattern, String repl) {
        return replace(string, Patterns.getPattern(pattern), repl);
    }
    /**
     * Pure Regex Pattern matching and replacement, no escaping
     * @param string
     * @param pattern
     * @param repl
     * @return
     */
    public static String replacePatternMatch(String string, Pattern pattern, String repl) {
        return pattern.matcher(string).replaceAll(repl);
    }

    /**
     * Pure Regex Pattern matching and replacement, no escaping
     * @param string
     * @param pattern
     * @param repl
     * @return
     */
    public static String replacePatternMatch(String string, String pattern, String repl) {
        return replacePatternMatch(string, Patterns.getPattern(pattern), repl);
    }

    public static String replaceStrings(String string, String... replacements) {
        if (replacements.length < 2 || replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid Replacements");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            String key = replacements[i];
            String value = replacements[i+1];
            if (value == null) value = "";
            string = replace(string, key, value);
        }
        return string;
    }
    public static String replacePatterns(String string, String... replacements) {
        if (replacements.length < 2 || replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid Replacements");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            String key = replacements[i];
            String value = replacements[i+1];
            if (value == null) value = "";
            string = replacePattern(string, key, value);
        }
        return string;
    }


    /**
     * Copied from Apache Commons WordUtils, with an exception to skip spaces after delimiters.
     *
     * @see org.apache.commons.lang.WordUtils#capitalize(String, char[])
     * @param str
     * @param delimiters
     * @return
     */
    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.isEmpty() || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuilder builder = new StringBuilder(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (isDelimiter(ch, delimiters)) {
                builder.append(ch);
                capitalizeNext = true;
            } else if (ch != ' ' && capitalizeNext) {
                builder.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    public static <T> T random(List<T> arr) {
        if (arr == null || arr.isEmpty()) {
            return null;
        }
        return arr.get(RANDOM.nextInt(arr.size()));
    }
    public static <T> T random(T[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        return arr[RANDOM.nextInt(arr.length)];
    }

    /**
     * Added as im sure we will try to "Find this" again. This is no different than Enum.values() passed to above method logically
     * but the array version is slightly faster.
     * @param enm
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T extends Enum<?>> T random(Class<? extends T> enm) {
        return random(enm.getEnumConstants());
    }

    public static String normalize(String s) {
        if (s == null) {
            return null;
        }
        return Patterns.NON_PRINTABLE_CHARACTERS.matcher(Normalizer.normalize(s, Form.NFD)).replaceAll("");
    }

    public static int indexOf(String arg, String[] split) {
        for (int i = 0; i < split.length; i++) {
            if (arg == null) {
                if (split[i] == null) {
                    return i;
                }
            } else if (arg.equals(split[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String capitalizeFirst(String name) {
        return capitalizeFirst(name, '_');
    }

    public static String capitalizeFirst(String name, char separator) {
        name = name.toLowerCase();
        String[] split = name.split(Character.toString(separator));
        StringBuilder total = new StringBuilder(3);
        for (String s : split) {
            total.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(' ');
        }

        return total.toString().trim();
    }

    public static List<String> enumNames(Enum<?>[] values) {
        return Stream.of(values).map(Enum::name).collect(Collectors.toList());
    }

    public static List<String> enumNames(Class<? extends Enum<?>> cls) {
        return enumNames(cls.getEnumConstants());
    }

    public static String combine(String[] args) {
        return combine(args, 0);
    }
    public static String combine(String[] args, int start) {
        int size = 0;
        for (int i = start; i < args.length; i++) {
            size += args[i].length();
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = start; i < args.length; i++) {
            sb.append(args[i]);
        }
        return sb.toString();
    }

    public static double distance(@NotNull Entity e1, @NotNull Entity e2) {
        return distance(e1.getLocation(), e2.getLocation());
    }
    public static double distance2d(@NotNull Entity e1, @NotNull Entity e2) {
        return distance2d(e1.getLocation(), e2.getLocation());
    }
    public static double distance2d(@NotNull  Location loc1, @NotNull Location loc2) {
        loc1 = loc1.clone();
        loc1.setY(loc2.getY());
        return distance(loc1, loc2);
    }
    public static double distance(@NotNull  Location loc1, @NotNull Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            return 0;
        }
        return loc1.distance(loc2);
    }

    public static Location getTargetLoc(Player player) {
        return getTargetLoc(player, 128);
    }
    public static Location getTargetLoc(Player player, int maxDist) {
        return getTargetLoc(player, maxDist, 1.5);
    }
    public static Location getTargetLoc(Player player, int maxDist, double addY) {
        try {
            Location target = player.getTargetBlock((Set<Material>) null, maxDist).getLocation();
            target.setY(target.getY() + addY);
            return target;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Location getRandLoc(Location loc, int radius) {
        return getRandLoc(loc, radius, radius, radius);
    }
    public static Location getRandLoc(Location loc, int xzRadius, int yRadius) {
        return getRandLoc(loc, xzRadius, yRadius, xzRadius);
    }
    @NotNull public static Location getRandLoc(Location loc, int xRadius, int yRadius, int zRadius) {
        Location newLoc = loc.clone();
        newLoc.setX(NumUtil.rand(loc.getX()-xRadius, loc.getX()+xRadius));
        newLoc.setY(NumUtil.rand(loc.getY()-yRadius, loc.getY()+yRadius));
        newLoc.setZ(NumUtil.rand(loc.getZ()-zRadius, loc.getZ()+zRadius));
        return newLoc;
    }

    @Nullable public static Enum<?> simpleMatch(Class<? extends Enum<?>> list, String item) {
        if (item == null) {
            return null;
        }
        item = CommandUtil.simplifyString(item);
        for (Enum<?> s : list.getEnumConstants()) {
            String simple = CommandUtil.simplifyString(s.name());
            if (item.equals(simple)) {
                return s;
            }
        }

        return null;
    }

    @NotNull public static Boolean isTruthy(String test) {
        switch (test) {
            case "t":
            case "true":
            case "on":
            case "y":
            case "yes":
            case "1":
                return true;
        }
        return false;
    }


    public static Number parseNumber(String num, boolean suffixes) {
        double mod = 1;
        if (suffixes) {
            switch (num.charAt(num.length()-1)) {
                case 'M':
                case 'm':
                    mod = 1000000D;
                    num = num.substring(0, num.length()-1);
                    break;
                case 'K':
                case 'k':
                    mod = 1000D;
                    num = num.substring(0, num.length()-1);
            }
        }

        return Double.parseDouble(num) * mod;
    }

    public static <T> boolean hasIntersection(Collection<T> list1, Collection<T> list2) {
        for (T t : list1) {
            if (list2.contains(t)) {
                return true;
            }
        }

        return false;
    }

    public static <T> Collection<T> intersection(Collection<T> list1, Collection<T> list2) {
        List<T> list = new ArrayList<>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}
