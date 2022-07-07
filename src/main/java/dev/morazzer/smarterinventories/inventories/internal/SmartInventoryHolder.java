package dev.morazzer.smarterinventories.inventories.internal;

import dev.morazzer.smarterinventories.inventories.SmartInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SmartInventoryHolder implements InventoryHolder {

    private final SmartInventory smartInventory;

    public SmartInventoryHolder(SmartInventory smartInventory) {
        this.smartInventory = smartInventory;
    }

    public SmartInventory getSmartInventory() {
        return smartInventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        throw new UnsupportedOperationException("Why are you trying to get an inventory from a SmartInventoryHolder? This class is only used for storing data.");
    }

    @Override
    public String toString() {
        return "SmartInventoryHolder{" +
                "smartInventory='" + smartInventory + '\'' +
                '}';
    }
}
