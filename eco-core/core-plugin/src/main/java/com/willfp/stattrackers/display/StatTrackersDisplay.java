package com.willfp.stattrackers.display;

import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.stattrackers.StatTrackersPlugin;
import com.willfp.stattrackers.stats.Stat;
import com.willfp.stattrackers.stats.util.StatChecks;
import com.willfp.stattrackers.tracker.util.TrackerUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StatTrackersDisplay {
    /**
     * Instance of StatTrackers.
     */
    private static final AbstractEcoPlugin PLUGIN = StatTrackersPlugin.getInstance();

    /**
     * The prefix for all stat lines to have in lore.
     */
    public static final String PREFIX = "§y";

    /**
     * Revert display.
     *
     * @param item The item to revert.
     * @return The item, updated.
     */
    public static ItemStack revertDisplay(@Nullable final ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> itemLore;

        if (meta.hasLore()) {
            itemLore = meta.getLore();
        } else {
            itemLore = new ArrayList<>();
        }

        if (itemLore == null) {
            itemLore = new ArrayList<>();
        }

        itemLore.removeIf(s -> s.startsWith(PREFIX));

        meta.setLore(itemLore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Show stat in item lore.
     *
     * @param item The item to update.
     * @return The item, updated.
     */
    public static ItemStack displayStat(@Nullable final ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return item;
        }

        revertDisplay(item);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        List<String> itemLore = new ArrayList<>();

        if (meta.hasLore()) {
            itemLore = meta.getLore();
        }

        if (itemLore == null) {
            itemLore = new ArrayList<>();
        }

        Stat stat = StatChecks.getActiveStat(item);

        if (stat == null) {
            Stat trackerStat = TrackerUtils.getTrackedStat(item);
            if (trackerStat == null) {
                return item;
            }

            meta.setDisplayName(PLUGIN.getLangYml().getString("tracker"));
            List<String> lore = PLUGIN.getLangYml().getStrings("tracker-description");

            for (int i = 0; i < lore.size(); i++) {
                String string = lore.get(i);
                string = StringUtils.translate(string);
                string = string.replace("%stat%", trackerStat.getColor() + trackerStat.getDescription());
                string = PREFIX + string;
                lore.set(i, string);
            }

            meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            meta.setLore(lore);
            item.setItemMeta(meta);

            return item;
        }

        itemLore.add(PREFIX + "§f" + stat.getColor() + stat.getDescription() + PLUGIN.getLangYml().getString("delimiter") + StringUtils.internalToString(StatChecks.getStatOnItem(item, stat)));
        meta.setLore(itemLore);
        item.setItemMeta(meta);

        return item;
    }
}