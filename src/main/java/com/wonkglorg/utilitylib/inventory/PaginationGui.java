package com.wonkglorg.utilitylib.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * A panel in an InventoryGUI which can be used to paginate items and buttons
 *
 * @author Redempt
 */
@SuppressWarnings("unused")
public final class PaginationGui {
    private final GuiInventory gui;
    private int page = 1;
    private final List<IntConsumer> buttons = new ArrayList<>();
    private final Map<Object, IntConsumer> items = new HashMap<>();
    private final Set<Integer> slots = new TreeSet<>();
    private Runnable onUpdate = () -> {
    };
    private ItemStack fillerItem;

    private final int maxRows = 9;
    private final int maxColumns = 6;
    private Button previousButton;
    private int previousButtonSlot;
    private Button nextButton;
    private int nextButtonSlot;

    /**
     * Constructs a PaginationPanel to work on a given InventoryGUI
     *
     * @param gui The InventoryGUI to paginate
     */
    public PaginationGui(GuiInventory gui) {
        this(gui, null);
    }

    /**
     * Constructs a PaginationPanel to work on a given InventoryGUI
     *
     * @param gui        The InventoryGUI to paginate
     * @param fillerItem The item to use for the background
     */
    public PaginationGui(GuiInventory gui, ItemStack fillerItem) {
        this.gui = gui;
        this.fillerItem = fillerItem;
    }

    /**
     * Sets a task to be run whenever the page updates, can be used to update a page indicator or similar
     *
     * @param onUpdate The task to be run on update
     */
    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    private void addPagedButton0(Button button) {
        IntConsumer setter = i -> gui.addButton(button, i);
        items.put(button, setter);
        buttons.add(setter);
    }

    /**
     * Adds a paged button to the panel
     *
     * @param button The button to add
     */
    public void addPagedButton(Button button) {
        addPagedButton0(button);
        updatePage();
    }

    private void addPagedItem0(ItemStack item) {
        IntConsumer setter = i -> gui.getInventory().setItem(i, item);
        items.put(item, setter);
        buttons.add(setter);
    }

    /**
     * Adds a paged item to the panel
     *
     * @param item The item to add
     */
    public void addPagedItem(ItemStack item) {
        addPagedItem0(item);
        updatePage();
    }

    /**
     * Adds multiple buttons to the paged panel
     *
     * @param buttons The buttons to add
     */
    public void addPagedButtons(Iterable<Button> buttons) {
        for (Button button : buttons) {
            addPagedButton0(button);
        }
        updatePage();
    }

    /**
     * Adds multiple items to the paged panel
     *
     * @param items The items to add
     */
    public void addPagedItems(Iterable<ItemStack> items) {
        for (ItemStack item : items) {
            addPagedItem0(item);
        }
        updatePage();
    }

    /**
     * Removes an item from the paged panel.
     *
     * @param item The item to remove
     */
    public void removePagedItem(ItemStack item) {
        buttons.remove(items.remove(item));
        updatePage();
    }

    /**
     * Removes a button from the paged panel.
     *
     * @param button The button to remove
     */
    public void removePagedButton(Button button) {
        buttons.remove(items.remove(button));
        updatePage();
    }

    /**
     * Removes multiple items from the paged panel
     *
     * @param items The items to remove
     */
    public void removePagedItems(Iterable<ItemStack> items) {
        for (ItemStack item : items) {
            buttons.remove(this.items.remove(item));
        }
        updatePage();
    }

    /**
     * Removes multiple buttons from the paged panel
     *
     * @param buttons The buttons to remove
     */
    public void removePagedButtons(Iterable<Button> buttons) {
        for (Button button : buttons) {
            this.buttons.remove(items.remove(button));
        }
        updatePage();
    }

    /**
     * @return The page this panel is currently on
     */
    public int getPage() {
        return page;
    }

    /**
     * @return The max number of elements displayed on each page
     */
    public int getPageSize() {
        return slots.size();
    }

    /**
     * @return The maximum page number of this panel with the current number of elements
     */
    public int getMaxPage() {
        return (Math.max(0, buttons.size() - 1) / Math.max(1, slots.size())) + 1;
    }

    /**
     * Adds a slot which will be used to display elements
     *
     * @param slot The slot to add
     */
    public void addSlot(int slot) {
        slots.add(slot);
        updatePage();
    }

    /**
     * Adds a range of slots which will be used to display elements
     *
     * @param start The start index of slots to add, inclusive (0-indexed)
     * @param end   The end index of slots to add, inclusive  (0-indexed)
     */
    public void addSlots(int start, int end) {
        for (int i = start; i <= end; i++) {
            slots.add(i);
        }
        updatePage();
    }

    /**
     * Adds a rectangular area of slots which will be used to display elements
     *
     * @param x1 The starting X of slots to add, inclusive (0-indexed)
     * @param y1 The starting Y of slots to add, inclusive (0-indexed)
     * @param x2 The ending X of slots to add, inclusive (0-indexed)
     * @param y2 The ending Y of slots to add, inclusive (0-indexed)
     */
    public void addSlots(int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                slots.add((y * maxRows) + x);
            }
        }
        updatePage();
    }

    /**
     * Removes a slot which will be used to display elements
     *
     * @param slot The slot to remove
     */
    public void removeSlot(int slot) {
        slots.forEach(gui::clearSlot);
        slots.forEach(gui::clearSlot);
        slots.remove(slot);
        updatePage();
    }

    /**
     * Removes a range of slots which will be used to display elements
     *
     * @param start The start index of slots to remove, inclusive (0-indexed)
     * @param end   The end index of slots to remove, inclusive (0-indexed)
     */
    public void removeSlots(int start, int end) {
        slots.forEach(gui::clearSlot);
        for (int i = start; i <= end; i++) {
            slots.remove(i);
        }
        updatePage();
    }

    /**
     * Removes a rectangular area of slots which will be used to display elements
     *
     * @param x1 The starting X of slots to remove, inclusive (0-indexed)
     * @param y1 The starting Y of slots to remove, inclusive (0-indexed)
     * @param x2 The ending X of slots to remove, inclusive (0-indexed)
     * @param y2 The ending Y of slots to remove, inclusive (0-indexed)
     */
    public void removeSlots(int x1, int y1, int x2, int y2) {
        slots.forEach(gui::clearSlot);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                slots.remove((y * maxRows) + x);
            }
        }
        updatePage();
    }

    /**
     * Updates the elements displayed on the current page
     */
    public void updatePage() {
        slots.forEach(gui::clearSlot);
        slots.forEach(i -> gui.getInventory().setItem(i, fillerItem));
        if (getPageSize() == 0 || buttons.isEmpty()) {
            onUpdate.run();
            return;
        }
        int start = (page - 1) * getPageSize();
        int end = Math.min(buttons.size(), page * getPageSize());
        Iterator<Integer> iter = slots.iterator();
        for (int i = start; i < end; i++) {
            buttons.get(i).accept(iter.next());
        }
        onUpdate.run();
    }

    /**
     * Sets the page of this panel
     *
     * @param page The page to set
     */
    public void setPage(int page) {
        if (page < 1 || page > getMaxPage()) {
            throw new IllegalArgumentException("Invalid page: " + page);
        }
        this.page = page;
        updatePage();
    }

    /**
     * Removes all items and buttons from the panel
     */
    public void clear() {
        buttons.clear();
        items.clear();
        updatePage();
    }

    /**
     * @return All ItemStacks added to this panel
     */
    public List<ItemStack> getItems() {
        return items.keySet().stream().filter(ItemStack.class::isInstance).map(ItemStack.class::cast).collect(Collectors.toList());
    }

    /**
     * @return All ItemButtons added to this panel
     */
    public List<Button> getButtons() {
        return items.keySet().stream().filter(Button.class::isInstance).map(Button.class::cast).collect(Collectors.toList());
    }

    /**
     * Navigates to the next page, if there is one
     */
    public void nextPage() {
        page = Math.min(page + 1, getMaxPage());
        updatePage();
    }

    /**
     * Navigates to the previous page, if there is one
     */
    public void prevPage() {
        page = Math.max(1, page - 1);
        updatePage();
    }

    public void setPreviousButton(Button button, int slot) {
        this.previousButton = button;
        this.previousButtonSlot = slot;
    }

    public void setNextButton(Button button, int slot) {
        this.nextButton = button;
        this.nextButtonSlot = slot;
    }

    /**
     * updates the page buttons based on the current page count with an itemstack
     *
     * @param fillerItem
     */
    public void updatePageButtons(ItemStack fillerItem) {
        if (previousButton == null || nextButton == null) return;

        if (getMaxPage() == 1) {
            gui.addItem(fillerItem, previousButtonSlot);
            gui.addItem(fillerItem, nextButtonSlot);
            return;
        }

        if (getPage() > 1) {
            gui.addButton(previousButton, previousButtonSlot);
        } else {
            gui.addItem(fillerItem, previousButtonSlot);
        }

        if (getPage() < getMaxPage()) {
            gui.addButton(nextButton, nextButtonSlot);
        } else {
            gui.addItem(fillerItem, nextButtonSlot);
        }
        gui.update();
    }

    /**
     * updates the page buttons based on the current page count with the default filler item
     */
    public void updatePageButtons() {
        updatePageButtons(fillerItem);
    }

    /**
     * updates the page buttons based on the current page count, set 2 buttons for previous and next to replace them if not needed
     *
     * @param previousButtonReplacer
     * @param nextButtonReplacer
     */
    public void updatePageButtons(Button previousButtonReplacer, Button nextButtonReplacer) {
        if (previousButton == null || nextButton == null) return;

        if (getMaxPage() == 1) {
            gui.addButton(previousButtonReplacer, previousButtonSlot);
            gui.addButton(nextButtonReplacer, nextButtonSlot);
            return;
        }

        if (getPage() > 1) {
            gui.addButton(previousButton, previousButtonSlot);
        } else {
            gui.addButton(previousButtonReplacer, previousButtonSlot);
        }

        if (getPage() < getMaxPage()) {
            gui.addButton(nextButton, nextButtonSlot);
        } else {
            gui.addButton(nextButtonReplacer, nextButtonSlot);
        }
        gui.update();
    }

    /**
     * Sets the filler item
     */
    public void setFillerItem(ItemStack item) {
        this.fillerItem = item;
    }

    /**
     * Gets the filler item
     */
    public ItemStack getFillerItem() {
        return fillerItem;
    }


    public int getButtonSize() {
        return buttons.size();
    }

    public int getSlotSize() {
        return slots.size();
    }
}
