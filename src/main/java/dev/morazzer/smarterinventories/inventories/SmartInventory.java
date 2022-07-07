package dev.morazzer.smarterinventories.inventories;

import dev.morazzer.smarterinventories.InventoryListener;
import dev.morazzer.smarterinventories.InventoryManager;
import dev.morazzer.smarterinventories.InventoryType;
import dev.morazzer.smarterinventories.inventories.types.chest.ChestContents;
import dev.morazzer.smarterinventories.utils.ReflectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import java.util.*;

public abstract class SmartInventory<C extends InventoryContents> {
    protected Component title;
    protected List<InventoryListener<? extends Event>> listeners;
    protected InventoryProvider<C> provider;
    protected InventoryManager manager;

    protected SmartInventory() {
    }

    public final Inventory open(Player player) {
        return open(player, provider);
    }

    public Inventory open(Player player, InventoryProvider<C> provider) {
        if (provider == null && this.provider == null) {
            throw new IllegalStateException("Inventory provider cannot be null");
        }
        InventoryProvider<C> providerToUse = Objects.requireNonNullElse(provider, this.provider);
        C content = getContent(player);
        Inventory inventory = createInventory();
        providerToUse.initialize(player, content);
        InventoryContents.fillInventory(inventory, content, getColumns());
        player.openInventory(inventory);

        manager.registerInventory(player, this, providerToUse);
        return inventory;
    }

    public abstract int getColumns();

    public void afterCompletion() {}

    abstract public Optional<C> getContents(Player player);

    protected abstract C getContent(Player player);

    protected abstract Inventory createInventory();

    public List<InventoryListener<? extends Event>> getListeners() {
        return listeners;
    }

    public static class Builder<T extends Builder<T, I, C>, I extends SmartInventory, C extends InventoryContents> {
        protected Builder() {
        }

        public static <I extends SmartInventory, B extends Builder<B, I, C>, C extends InventoryContents> B of(InventoryType<I, C, B> type) {
            if (type.getBuilderClass() == null) {
                return (B) new DefaultBuilder<>(type.getSmartInventoryClass());
            }

            return ReflectionUtils.createInstance(type.getBuilderClass());
        }

        private Component title;
        private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();
        private InventoryManager manager;
        private InventoryProvider<C> provider;

        public final T provider(InventoryProvider<C> provider) {
            this.provider = provider;
            return (T) this;
        }

        public final T manager(InventoryManager manager) {
            this.manager = manager;
            return (T) this;
        }

        public final T title(Component title) {
            this.title = title;
            return (T) this;
        }

        public final T listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return (T) this;
        }

        protected void build(I smartInventory) {
            Objects.requireNonNull(manager, "manager");
            Objects.requireNonNull(title, "title");

            smartInventory.manager = this.manager;
            smartInventory.title = this.title;
            smartInventory.listeners = this.listeners;
            smartInventory.provider = this.provider;
        }
    }

    public static class DefaultBuilder<I extends SmartInventory<C>, C extends InventoryContents> extends Builder<DefaultBuilder<I, C>, I, C> {
        private final Class<I> smartInventoryClass;

        protected DefaultBuilder(Class<I> smartInventoryClass) {
            this.smartInventoryClass = smartInventoryClass;
        }

        public final I build() {
            I smartInventory = ReflectionUtils.createInstance(smartInventoryClass);
            build(smartInventory);
            smartInventory.afterCompletion();
            return smartInventory;
        }
    }
}
