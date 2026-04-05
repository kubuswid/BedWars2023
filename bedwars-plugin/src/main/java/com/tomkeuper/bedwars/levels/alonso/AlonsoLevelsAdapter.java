package com.tomkeuper.bedwars.levels.alonso;

import com.tomkeuper.bedwars.api.events.player.PlayerXpGainEvent;
import com.tomkeuper.bedwars.api.levels.Level;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.UUID;

public class AlonsoLevelsAdapter implements Level {

    private static final ThreadLocal<NumberFormat> NF = ThreadLocal.withInitial(() -> {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(0);
        return f;
    });

    private Class<?> apiClass;

    public AlonsoLevelsAdapter() {
        try {
            apiClass = Class.forName("com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI");
        } catch (ClassNotFoundException e) {
            apiClass = null;
        }
    }

    private boolean isLoaded(UUID uuid) {
        if (apiClass == null) return false;
        try {
            Method m = apiClass.getMethod("isLoaded", UUID.class);
            return (boolean) m.invoke(null, uuid);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getLevel(Player p) {
        if (!isLoaded(p.getUniqueId())) return "0";
        try {
            Method mColored = apiClass.getMethod("getColoredLevel", UUID.class);
            String coloredLevel = (String) mColored.invoke(null, p.getUniqueId());
            if (coloredLevel != null && !coloredLevel.isEmpty()) {
                return coloredLevel;
            }
        } catch (Exception ignored) {
        }
        return String.valueOf(getPlayerLevel(p));
    }

    @Override
    public int getPlayerLevel(Player p) {
        if (!isLoaded(p.getUniqueId())) return 0;
        try {
            Method m = apiClass.getMethod("getLevel", UUID.class);
            return (int) m.invoke(null, p.getUniqueId());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getRequiredXpFormatted(Player p) {
        return "?";
    }

    @Override
    public String getProgressBar(Player p) {
        if (!isLoaded(p.getUniqueId())) return "";
        try {
            Method m = apiClass.getMethod("getProgressBar", UUID.class);
            return (String) m.invoke(null, p.getUniqueId());
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int getCurrentXp(Player p) {
        if (!isLoaded(p.getUniqueId())) return 0;
        try {
            Method m = apiClass.getMethod("getExperience", UUID.class);
            return (int) m.invoke(null, p.getUniqueId());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getCurrentXpFormatted(Player p) {
        int xp = getCurrentXp(p);
        return formatNumber(xp);
    }

    @Override
    public int getRequiredXp(Player p) {
        return 0;
    }

    @Override
    public void addXp(Player player, int xp, PlayerXpGainEvent.XpSource source) {
        if (isLoaded(player.getUniqueId()) && xp > 0) {
            try {
                Method m = apiClass.getMethod("addExperience", UUID.class, int.class);
                m.invoke(null, player.getUniqueId(), xp);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void setXp(Player player, int currentXp) {
        if (isLoaded(player.getUniqueId()) && currentXp >= 0) {
            try {
                Method m = apiClass.getMethod("setExperience", UUID.class, int.class);
                m.invoke(null, player.getUniqueId(), currentXp);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void setLevel(Player player, int level) {
        if (isLoaded(player.getUniqueId()) && level >= 0) {
            try {
                Method m = apiClass.getMethod("setLevel", UUID.class, int.class);
                m.invoke(null, player.getUniqueId(), level);
            } catch (Exception ignored) {}
        }
    }

    private String formatNumber(int score) {
        NumberFormat f = NF.get();
        if (score >= 1000) return f.format(score / 1000.0) + "k";
        return f.format(score);
    }
}
