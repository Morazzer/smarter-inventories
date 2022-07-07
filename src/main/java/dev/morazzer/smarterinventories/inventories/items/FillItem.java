package dev.morazzer.smarterinventories.inventories.items;

import dev.morazzer.itembuilder.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class FillItem extends Item {

    public FillItem(Material material) {
        super(ItemBuilder.of(material).displayName(Component.empty()).build());
    }

}
