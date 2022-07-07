package dev.morazzer.smarterinventories;

import dev.morazzer.smarterinventories.inventories.InventoryContents;
import dev.morazzer.smarterinventories.inventories.InventoryProvider;
import dev.morazzer.smarterinventories.inventories.Position;
import dev.morazzer.smarterinventories.inventories.SmartInventory;
import dev.morazzer.smarterinventories.inventories.internal.SmartInventoryHolder;
import dev.morazzer.smarterinventories.inventories.items.ClickableItem;
import dev.morazzer.smarterinventories.inventories.items.CommandItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager {

    private ArrayList<InventoryListener<? extends Event>> listeners = new ArrayList<>();
    private ConcurrentHashMap<Player, SmartInventory<?>> inventories = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Player, InventoryProvider<?>> providers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Event>, EventExecutor> executors = new ConcurrentHashMap<>();
    private EventExecutor executor =
            (ignored, event) -> {
                if (event instanceof InventoryEvent) {
                    if (((InventoryEvent) event).getInventory().getHolder() instanceof SmartInventoryHolder) {
                        SmartInventory<?> inventory = ((SmartInventoryHolder) ((InventoryEvent) event).getInventory().getHolder()).getSmartInventory();
                        inventory.getListeners().stream().filter(listener -> listener.getEventClass().equals(event.getClass()))
                                .filter(listener -> !listener.isIgnoringHolder())
                                .forEach(listener -> listener.accept(event));
                    }

                    listeners.stream().filter(listener -> listener.getEventClass().equals(event.getClass()))
                            .filter(InventoryListener::isIgnoringHolder)
                            .forEach(listener -> listener.accept(event));

                    return;
                }

                listeners.stream().filter(listener -> listener.getEventClass().equals(event.getClass())).forEach(listener -> listener.accept(event));
            };
    private InventoryListeners inventoryListeners = new InventoryListeners(this);
    private Listener listener = new Listener() {
    };

    private final Plugin plugin;

    public InventoryManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(inventoryListeners, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateInventories, 0, 1);
    }

    public ConcurrentHashMap<Class<? extends Event>, EventExecutor> getExecutors() {
        return executors;
    }

    public void registerInventory(Player player, SmartInventory<?> inventory, InventoryProvider<?> provider) {
        providers.put(player, provider);
        listeners.addAll(inventory.getListeners());
        inventories.put(player, inventory);
        updateListeners();
        unloadUnusedListeners();
    }

    private void updateListeners() {
        listeners.stream().map(InventoryListener::getEventClass).forEach(eventClass -> {
            if (!executors.containsKey(eventClass)) {
                if (Arrays.stream(eventClass.getDeclaredMethods()).anyMatch(method ->
                        method.getParameterCount() == 0 && method.getName().equals("getHandlers"))) {
                    executors.put(eventClass, executor);
                    Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, executor, plugin);
                }
            }
        });
    }

    public void unloadUnusedListeners() {
        executors.forEach((aClass, eventExecutor) -> {
            if (listeners.stream().map(InventoryListener::getEventClass).noneMatch(aClass::equals)) {
                try {
                    ((HandlerList) aClass.getDeclaredMethod("getHandlerList").invoke(null)).unregister(listener);
                    executors.remove(aClass);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static class InventoryListeners implements Listener {

        private final InventoryManager manager;

        public InventoryListeners(InventoryManager manager) {
            this.manager = manager;
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (manager.inventories.containsKey((Player) event.getPlayer())) {
                SmartInventory<?> smartInventory = manager.inventories.remove(event.getPlayer());
                manager.listeners.removeAll(smartInventory.getListeners());
                manager.updateListeners();
                manager.unloadUnusedListeners();
            }
        }

        @EventHandler
        public void onInventoryClickEvent(InventoryClickEvent event) {
            if (manager.inventories.containsKey((Player) event.getWhoClicked())) {
                SmartInventory<?> smartInventory = manager.inventories
                        .get((Player) event.getWhoClicked());
                Optional<? extends InventoryContents> contents = smartInventory.getContents((Player) event.getWhoClicked());
                contents.get().get(new Position(event.getSlot(), (double) smartInventory.getColumns())).ifPresent(item -> {
                    if (item instanceof ClickableItem) {
                        ((ClickableItem) item).accept(event);
                    } else if (item instanceof CommandItem) {
                        ((CommandItem) item).execute((Player) event.getWhoClicked());
                    }
                });

                event.setCancelled(true);
            }
        }
    }

    public void updateInventories() {
        inventories.forEach((player, inventory) -> {
            inventory.getContents(player).ifPresent(contents -> {
                InventoryProvider<?> provider = providers.get(player);
                updateSingle(player, contents, provider);
            });
        });
    }

    private <T extends InventoryContents> void updateSingle(Player player, T contents, Object provider) {
        ((InventoryProvider<T>)provider).update(player, contents);
    }
}
