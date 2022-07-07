package dev.morazzer.smarterinventories.inventories;

import org.bukkit.entity.Player;

public interface InventoryProvider <C extends InventoryContents> {
    public void initialize(Player player, C contents);
    public void update(Player player, C contents);
}
