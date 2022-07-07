package dev.morazzer.smarterinventories.inventories;

import dev.morazzer.smarterinventories.InventoryManager;
import dev.morazzer.smarterinventories.inventories.internal.SmartInventoryHolder;
import dev.morazzer.smarterinventories.inventories.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryContents {

    void set(Position position, Item item);
    Optional<Position> firstEmpty();
    Optional<Item> get(Position position);
    void add(Item item);
    void remove(Position position);
    void fill(Item item);
    Item[][] items();
    int rows();
    int columns();

    class Implementation implements InventoryContents {

        protected final Item[][] items;
        protected final int rows;
        protected final int columns;
        protected final UUID player;

        public Implementation(int rows, int columns, UUID player) {
            this.rows = rows;
            this.columns = columns;
            this.items = new Item[rows][columns];
            this.player = player;
        }

        @Override
        public void set(Position position, Item item) {
            if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
                throw new IllegalArgumentException("Position " + position.row() + "x" + position.column() + " is out of bounds of inventory with size " + rows + "x" + columns);
            }

            items[position.row()][position.column()] = item;
            update(position, item);
        }

        @Override
        public Optional<Position> firstEmpty() {
            for (int row = 0; row < items.length; row++) {
                for (int column = 0; column < items[row].length; column++) {
                    if (get(new Position(row, column)).isEmpty()) {
                        return Optional.of(new Position(row, column));
                    }
                }
            }

            return Optional.empty();
        }

        @Override
        public Optional<Item> get(Position position) {
            if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
                throw new IllegalArgumentException("Position " + position.row() + "x" + position.column() + " is out of bounds of inventory with size " + rows + "x" + columns);
            }

            return Optional.ofNullable(items[position.row()][position.column()]);
        }

        @Override
        public void add(Item item) {
            Optional<Position> position = firstEmpty();
            position.ifPresent(value -> set(value, item));
        }

        @Override
        public void remove(Position position) {
            if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
                throw new IllegalArgumentException("Position " + position.row() + "x" + position.column() + " is out of bounds of inventory with size " + rows + "x" + columns);
            }

            items[position.row()][position.column()] = null;
            update(position, null);
        }

        @Override
        public void fill(Item item) {
            for (int row = 0; row < items.length; row++) {
                for (int column = 0; column < items[row].length; column++) {
                    set(new Position(row, column), item);
                }
            }
        }

        @Override
        public final Item[][] items() {
            return items;
        }

        @Override
        public int rows() {
            return rows;
        }

        @Override
        public int columns() {
            return columns;
        }

        protected final void update(Position position, Item item) {
            if (!Bukkit.getOfflinePlayer(player).isOnline()) {
                return;
            }

            if (!(Bukkit.getPlayer(player).getOpenInventory().getTopInventory().getHolder() instanceof SmartInventoryHolder)) {
                return;
            }

            Inventory inventory = Bukkit.getPlayer(player).getOpenInventory().getTopInventory();
            inventory.setItem(position.row() * columns + position.column(), item.item());
        }
    }

    static void fillInventory(Inventory inventory, InventoryContents contents, int columns) {
        Item[][] items = contents.items();
        for (int row = 0; row < items.length; row++) {
            for (int column = 0; column < items[row].length; column++) {
                int finalRow = row;
                int finalColumn = column;
                contents.get(new Position(row, column)).ifPresent(item -> inventory.setItem(finalRow * columns + finalColumn, item.item()));
            }
        }
    }
}
