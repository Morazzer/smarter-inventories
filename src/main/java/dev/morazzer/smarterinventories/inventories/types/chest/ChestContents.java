package dev.morazzer.smarterinventories.inventories.types.chest;

import dev.morazzer.smarterinventories.inventories.Position;
import dev.morazzer.smarterinventories.inventories.InventoryContents;
import dev.morazzer.smarterinventories.inventories.items.Item;

import java.util.UUID;

public class ChestContents extends InventoryContents.Implementation {

    public ChestContents(ChestInventory.Size size, UUID uuid) {
        super(size.getRows(), size.getColumns(), uuid);
    }

    public void fillRow(int row, Item item) {
        for (int column = 0; column < this.columns; column++) {
            set(new Position(row, column), item);
        }
    }

    public void fillColumn(int column, Item item) {
        for (int row = 0; row < this.rows; row++) {
            set(new Position(row, column), item);
        }
    }

    public void fillBorders(Item item) {
        fillRow(0, item);
        fillRow(this.rows - 1, item);
        fillColumn(0, item);
        fillColumn(this.columns - 1, item);
    }

    public void fillRectangle(Position from, Position to, Item item) {
        for (int row = from.row(); row <= to.row(); row++) {
            for (int column = from.column(); column < to.column(); column++) {
                set(new Position(row, column), item);
            }
        }
    }
}
