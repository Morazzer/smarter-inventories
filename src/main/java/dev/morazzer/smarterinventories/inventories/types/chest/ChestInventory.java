package dev.morazzer.smarterinventories.inventories.types.chest;

import dev.morazzer.smarterinventories.inventories.InventoryProvider;
import dev.morazzer.smarterinventories.inventories.SmartInventory;
import dev.morazzer.smarterinventories.inventories.InventoryContents;
import dev.morazzer.smarterinventories.inventories.internal.SmartInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class ChestInventory extends SmartInventory<ChestContents> {

    private HashMap<Player, ChestContents> contents = new HashMap<>();

    private Size size;

    @Override
    public int getColumns() {
        return size.columns;
    }

    @Override
    public Optional<ChestContents> getContents(Player player) {
        return Optional.ofNullable(contents.get(player));
    }

    @Override
    protected ChestContents getContent(Player player) {
        ChestContents contents = new ChestContents(size, player.getUniqueId());
        this.contents.put(player, contents);
        return contents;
    }

    @Override
    protected Inventory createInventory() {
        if (size == Size.SIZE_3x3) {
            return Bukkit.createInventory(new SmartInventoryHolder(this), InventoryType.DROPPER, title);
        }

        return Bukkit.createInventory(new SmartInventoryHolder(this), size.getSize(), title);
    }

    public static class Builder extends SmartInventory.Builder<Builder, ChestInventory, ChestContents> {
        private Size size;

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public ChestInventory build() {
            ChestInventory chestInventory = new ChestInventory();
            chestInventory.size = this.size;

            build(chestInventory);

            return chestInventory;
        }
    }

    public enum Size {
        SIZE_1x9(1, 9),
        SIZE_2x9(2, 9),
        SIZE_3x9(3, 9),
        SIZE_4x9(4, 9),
        SIZE_5x9(5, 9),
        SIZE_6x9(6, 9),
        SIZE_3x3(3, 3);

        private final int size;
        private final int rows;
        private final int columns;

        Size(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            this.size = rows * columns;
        }

        public int getColumns() {
            return columns;
        }

        public int getRows() {
            return rows;
        }

        public int getSize() {
            return size;
        }
    }
}
