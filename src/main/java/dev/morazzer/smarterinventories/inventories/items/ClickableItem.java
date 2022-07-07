package dev.morazzer.smarterinventories.inventories.items;

import dev.morazzer.itembuilder.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem extends Item {

    private final Consumer<InventoryClickEvent> consumer;

    public ClickableItem(ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        super(itemStack);
        this.consumer = consumer;
    }

    public ClickableItem(ItemBuilder<?> itemBuilder, Consumer<InventoryClickEvent> consumer) {
        super(itemBuilder);
        this.consumer = consumer;
    }

    public void accept(InventoryClickEvent event) {
        consumer.accept(event);
    }
}
