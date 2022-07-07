package dev.morazzer.smarterinventories.inventories.items;

import dev.morazzer.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandItem extends Item {
    private final String command;

    public CommandItem(ItemStack itemStack, String command) {
        super(itemStack);
        this.command = command;
    }

    public CommandItem(ItemBuilder<?> itemBuilder, String command) {
        super(itemBuilder);
        this.command = command;
    }

    public void execute(Player player) {
        Bukkit.dispatchCommand(player, command);
    }
}
