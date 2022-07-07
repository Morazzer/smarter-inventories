package dev.morazzer.smarterinventories.inventories.types.anvil;

import dev.morazzer.smarterinventories.inventories.InventoryContents;
import dev.morazzer.smarterinventories.inventories.Position;
import dev.morazzer.smarterinventories.inventories.items.Item;

import java.util.UUID;

public class AnvilContents extends InventoryContents.Implementation {

    public AnvilContents(UUID uuid) {
        super(1, 3, uuid);
    }

    public void setLeftItem(Item item) {
        set(new Position(0, 0), item);
    }

    public void setRightItem(Item item) {
        set(new Position(0, 1), item);
    }

    public void setResult(Item item) {
        set(new Position(0, 2), item);
    }
}
