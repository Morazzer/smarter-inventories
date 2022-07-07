package dev.morazzer.smarterinventories.inventories.types.anvil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.smarterinventories.InventoryListener;
import dev.morazzer.smarterinventories.inventories.InventoryContents;
import dev.morazzer.smarterinventories.inventories.InventoryProvider;
import dev.morazzer.smarterinventories.inventories.SmartInventory;
import dev.morazzer.smarterinventories.inventories.internal.SmartInventoryHolder;
import dev.morazzer.smarterinventories.utils.ReflectionUtils;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Proxy;
import java.sql.Ref;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static dev.morazzer.smarterinventories.utils.ReflectionUtils.*;

public class AnvilInventory extends SmartInventory<AnvilContents> {
    public HashMap<Player, AnvilContents> contents = new HashMap<>();
    public HashMap<Player, Inventory> inventories = new HashMap<>();
    private Consumer<AnvilComplete> onComplete;
    private int experienceCost = 0;

    public AnvilInventory() {
        super();
    }

    @Override
    public void afterCompletion() {
        listeners.add(new InventoryListener<>(InventoryClickEvent.class, event -> {
            if (event.getInventory() != inventories.get((Player) event.getWhoClicked())) {
                return;
            }
            if (event.getSlot() == 2) {
                onComplete.accept(new AnvilComplete(((org.bukkit.inventory.AnvilInventory) event.getInventory()).getRenameText(),
                        ((org.bukkit.inventory.AnvilInventory) event.getInventory()).getRepairCost(), event.getCurrentItem()));
            }
        }, true));
        listeners.add(new InventoryListener<>(InventoryCloseEvent.class, event -> {
            if (event.getInventory() != inventories.get((Player) event.getPlayer())) {
                return;
            }

            inventories.remove((Player) event.getPlayer());
        }, true));
        listeners.add(new InventoryListener<>(PrepareAnvilEvent.class, event -> {
            if (event.getInventory() != inventories.get((Player) event.getView().getPlayer())) {
                return;
            }
            event.getInventory().setRepairCost(experienceCost);
            event.getInventory().setMaximumRepairCost(experienceCost);
            event.getInventory().setRepairCostAmount(experienceCost);
        }, true));
    }

    @Override
    public Inventory open(Player player, InventoryProvider<AnvilContents> provider) {
        Class<?> containerAnvil = ReflectionUtils.getClass("net.minecraft.world.inventory.ContainerAnvil");
        Class<?> craftPlayer = getCraftClass("entity.CraftPlayer");
        Class<?> craftWorld = getCraftClass("CraftWorld");
        Class<?> entityPlayer = ReflectionUtils.getClass("net.minecraft.server.level.EntityPlayer");
        Class<?> entityHuman = ReflectionUtils.getClass("net.minecraft.world.entity.player.EntityHuman");
        Class<?> containerAccess = ReflectionUtils.getClass("net.minecraft.world.inventory.ContainerAccess");
        Class<?> craftEventFactory = getCraftClass("event.CraftEventFactory");
        Class<?> blockPosition = ReflectionUtils.getClass("net.minecraft.core.BlockPosition");
        Class<?> chatSerializer = ReflectionUtils.getClass("net.minecraft.network.chat.IChatBaseComponent").getDeclaredClasses()[0];
        Class<?> world = ReflectionUtils.getClass("net.minecraft.world.level.World");
        Class<?> playerInventory = ReflectionUtils.getClass("net.minecraft.world.entity.player.PlayerInventory");
        Class<?> containerC = ReflectionUtils.getClass("net.minecraft.world.inventory.Container");
        Class<?> baseComponent = ReflectionUtils.getClass("net.minecraft.network.chat.IChatBaseComponent");
        Class<?> playerConnection = ReflectionUtils.getClass("net.minecraft.server.network.PlayerConnection");
        Class<?> packetPlayOutOpen = ReflectionUtils.getClass("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow");
        Class<?> containers = ReflectionUtils.getClass("net.minecraft.world.inventory.Containers");
        Class<?> packet = ReflectionUtils.getClass("net.minecraft.network.protocol.Packet");

        Object cPlayer = craftPlayer.cast(player);
        Object nmsPlayer = invoke(craftPlayer, cPlayer, "getHandle");
        invoke(craftEventFactory, null, "handleInventoryCloseEvent", new Class[]{entityHuman}, nmsPlayer);
        setValue(entityHuman, nmsPlayer, "bU", getValue(entityHuman, nmsPlayer, "bT"));
        Object container = createInstance(containerAnvil,
                new Class[]{Integer.TYPE, playerInventory, containerAccess},
                invoke(entityPlayer, nmsPlayer, "nextContainerCounter"),
                invoke(entityHuman, nmsPlayer, "fB"),
                invoke(containerAccess, null, "a", new Class[]{world, blockPosition}, invoke(craftWorld, player.getWorld(), "getHandle"),
                        createInstance(blockPosition, new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE}, 0, 0, 0))
        );
        setValue(containerC, container, "checkReachable", false);
        Object baseTitle = invoke(chatSerializer, createInstance(chatSerializer), "b",
                GsonComponentSerializer.gson().serialize(title));
        invoke(containerC, container, "setTitle", new Class[]{baseComponent}, baseTitle);
        Integer containerId = getValue(containerC, container, "j", Integer.class);
        Object playerC = getValue(entityPlayer, nmsPlayer, "b");
        invoke(playerConnection, playerC, "a", new Class[]{packet},
                createInstance(packetPlayOutOpen, new Class[]{Integer.TYPE, containers, baseComponent}, containerId, getValue(containers, null, "h"), baseTitle));
        setValue(entityHuman, nmsPlayer, "bU", container);
        invoke(entityPlayer, nmsPlayer, "a", new Class[]{containerC}, container);

        InventoryView inventoryView = invoke(containerAnvil, container, "getBukkitView", InventoryView.class);
        if (provider == null && this.provider == null) {
            throw new IllegalStateException("Inventory provider cannot be null");
        }
        InventoryProvider<AnvilContents> providerToUse = Objects.requireNonNullElse(provider, this.provider);
        Inventory inventory = inventoryView.getTopInventory();
        AnvilContents anvilContents = getContent(player);
        providerToUse.initialize(player, anvilContents);
        InventoryContents.fillInventory(inventory, anvilContents, 3);

        manager.registerInventory(player, this, providerToUse);
        inventories.put(player, inventory);
        return inventory;
    }

    @Override
    public int getColumns() {
        return 3;
    }

    @Override
    public Optional<AnvilContents> getContents(Player player) {
        return Optional.ofNullable(contents.get(player));
    }

    @Override
    protected AnvilContents getContent(Player player) {
        AnvilContents anvilContents = new AnvilContents(player.getUniqueId());
        contents.put(player, anvilContents);
        return anvilContents;
    }


    public void setExperienceCost(int experienceCost) {
        this.experienceCost = experienceCost;
    }

    @Override
    protected Inventory createInventory() {
        return null;
    }

    public void onComplete(Consumer<AnvilComplete> consumer) {
        onComplete = consumer;
    }

    public static class AnvilComplete {
        private final String text;
        private final int cost;
        private final ItemStack result;

        public AnvilComplete(String text, int cost, ItemStack result) {
            this.text = text;
            this.cost = cost;
            this.result = result;
        }

        public int getCost() {
            return cost;
        }

        public ItemStack getResult() {
            return result;
        }

        public String getText() {
            return text;
        }
    }
}
