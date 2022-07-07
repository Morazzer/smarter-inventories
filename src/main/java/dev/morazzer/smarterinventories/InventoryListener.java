package dev.morazzer.smarterinventories;

import org.bukkit.event.Event;

import java.util.function.Consumer;

public class InventoryListener <T extends Event> {

    private final Class<T> eventClass;
    private final Consumer<T> consumer;
    private final boolean ignoreHolder;

    public InventoryListener(Class<T> eventClass, Consumer<T> consumer) {
        this.eventClass = eventClass;
        this.consumer = consumer;
        this.ignoreHolder = false;
    }

    public InventoryListener(Class<T> eventClass, Consumer<T> consumer, boolean ignoreHolder) {
        this.eventClass = eventClass;
        this.consumer = consumer;
        this.ignoreHolder = ignoreHolder;
    }

    public void accept(Event event) {
        assert event.getClass() == eventClass;
        consumer.accept((T) event);
    }

    public boolean isIgnoringHolder() {
        return ignoreHolder;
    }

    public Class<T> getEventClass() {
        return eventClass;
    }

    @Override
    public String toString() {
        return "InventoryListener{" +
                "eventClass=" + eventClass +
                ", consumer=" + consumer +
                '}';
    }
}
