package dev.morazzer.smarterinventories;

import dev.morazzer.smarterinventories.inventories.types.anvil.AnvilInventory;
import dev.morazzer.smarterinventories.inventories.types.chest.ChestInventory;
import dev.morazzer.smarterinventories.inventories.SmartInventory;
import dev.morazzer.smarterinventories.inventories.types.anvil.AnvilContents;
import dev.morazzer.smarterinventories.inventories.types.chest.ChestContents;
import dev.morazzer.smarterinventories.inventories.InventoryContents;

public class InventoryType<I extends SmartInventory, C extends InventoryContents, B> {
    public static final InventoryType<ChestInventory, ChestContents, ChestInventory.Builder> CHEST = new InventoryType<>(ChestInventory.class, ChestContents.class, ChestInventory.Builder.class);
    public static final InventoryType<AnvilInventory, AnvilContents, SmartInventory.DefaultBuilder<AnvilInventory, AnvilContents>> ANVIL =
            new InventoryType<>(AnvilInventory.class, AnvilContents.class);

    private Class<I> smartInventoryClass;
    private Class<C> inventoryContentsClass;
    private Class<B> builderClass;


    protected InventoryType(Class<I> smartInventoryClass, Class<C> inventoryContentsClass) {
        this.smartInventoryClass = smartInventoryClass;
        this.inventoryContentsClass = inventoryContentsClass;
        this.builderClass = null;
    }

    protected InventoryType(Class<I> smartInventoryClass, Class<C> inventoryContentsClass, Class<B> builderClass) {
        this.smartInventoryClass = smartInventoryClass;
        this.inventoryContentsClass = inventoryContentsClass;
        this.builderClass = builderClass;
    }

    public Class<B> getBuilderClass() {
        return builderClass;
    }

    public Class<C> getInventoryContentsClass() {
        return inventoryContentsClass;
    }

    public Class<I> getSmartInventoryClass() {
        return smartInventoryClass;
    }
}
