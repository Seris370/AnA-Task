package com.task.ana.model;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Item {
    private static volatile int count = 0;

    private static volatile LinkedList<Item> items = new LinkedList<>();

    private static final Object lock = new Object();

    private int id;

    private String text;

    private Item(String text) {
        this.id = count;
        this.text = text;
    }

    private int getId() {
        return this.id;
    }

    private void changeText(String text) {
        this.text = text;
    }

    private String getItem() {
        return this.id + ": " + this.text;
    }

    public static int addItem(String text) {
        synchronized (lock) {
            Item item = new Item(text);
            count += 1;
            items.add(item);
            return count - 1;
        }
    }

    public static boolean replaceItem(int id, String text) {
        synchronized (lock) {
            for (Item item : items) {
                if (item.getId() == id) {
                    item.changeText(text);
                    return true;
                } else if (item.getId() > id) {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean deleteItem(int id) {
        synchronized (lock) {
            for (Item item : items) {
                if (item.getId() == id) {
                    items.remove(item);
                    return true;
                } else if (item.getId() > id) {
                    return false;
                }
            }
            return false;
        }
    }

    public static String getSerializedItems() {
        return items.stream().map(Item::getItem).collect(Collectors.joining("\n"));
    }

    public static void refresh() {
        synchronized (lock) {
            count = 0;
            items = new LinkedList<>();
        }
    }
}
