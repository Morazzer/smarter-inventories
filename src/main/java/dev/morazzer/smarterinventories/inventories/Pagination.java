package dev.morazzer.smarterinventories.inventories;

import dev.morazzer.smarterinventories.inventories.items.Item;

import java.util.List;

public class Pagination {

    private List<Item> items;
    private final Position from;
    private final Position to;
    private Item firstItem;
    private final int columns;
    private final int rows;
    final int maxPage;
    int currentPage = 1;
    int firstItemSize;

    public Pagination(List<Item> items, Position from, Position to, Item firstItem) {
        this.items = items;
        this.from = from;
        this.to = to;
        this.firstItem = firstItem;

        rows = Math.max(from.row(), to.row()) - Math.min(from.row(), to.row()) + 1;
        columns = Math.max(from.column(), to.column()) - Math.min(from.column(), to.column()) + 1;
        maxPage = (items.size() + (firstItem != null ? 1 : 0)) / (rows * columns) + 1;
        firstItemSize = firstItem != null ? 1 : 0;
    }

    public void nextPage() {
        if (currentPage < maxPage) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
        }
    }

    public void setItems(InventoryContents contents) {
        for (int i = 0; i < rows * columns; i++) {
            if (i + (rows * columns * (currentPage - 1)) - firstItemSize >= items.size()) {
                break;
            }
            if (i == 0 && currentPage == 1 && firstItem != null) {
                contents.set(from, firstItem);
                continue;
            }
            Item item = items.get(i + (rows * columns * (currentPage - 1)) - firstItemSize);
            contents.set(from.add(new Position(i / columns, i % columns)), item);
        }
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setFirstItem(Item firstItem) {
        this.firstItem = firstItem;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<Item> getItems() {
        return items;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Item getFirstItem() {
        return firstItem;
    }

    public boolean hasNextPage() {
        return currentPage < maxPage;
    }
}
