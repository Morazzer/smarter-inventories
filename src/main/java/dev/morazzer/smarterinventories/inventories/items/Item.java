package dev.morazzer.smarterinventories.inventories.items;

import dev.morazzer.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Item {

    private final ItemStack itemStack;
    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item(ItemBuilder<?> itemBuilder) {
        this(itemBuilder.build());
    }

    public final ItemStack item() {
        return itemStack;
    }

    public static Item fillerOf(Material material) {
        return new FillItem(material);
    }

    public static Item clickableOf(ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(itemStack, consumer);
    }

    public static Item clickableOf(ItemBuilder<?> itemBuilder, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(itemBuilder, consumer);
    }

    public static Item commandOf(ItemStack itemStack, String command) {
        return new CommandItem(itemStack, command);
    }

    public static Item commandOf(ItemBuilder<?> itemBuilder, String command) {
        return new CommandItem(itemBuilder, command);
    }

    public static Item of(ItemStack itemStack) {
        return new Item(itemStack);
    }

    public static Item of(ItemBuilder<?> itemBuilder) {
        return new Item(itemBuilder);
    }
}
