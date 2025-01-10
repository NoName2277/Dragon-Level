package pl.noname.dragonlevel.dragon;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import pl.noname.dragonlevel.Main;

import java.util.*;

public class ElytraListeners implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    public ElytraListeners(Main plugin){
        this.plugin = plugin;
    }

    private static final Vector[] ElytraLoc = new Vector[]{
            new Vector(-5.5, 256.0, -5.5),
            new Vector(0.5, 256.0, -7.5),
            new Vector(5.5, 256.0, -5.5),
            new Vector(7.5, 256.0, 0.5),
            new Vector(5.5, 256.0, 5.5),
            new Vector(0.5, 256.0, 7.5),
            new Vector(-5.5, 256.0, 5.5),
            new Vector(-7.5, 256.0, 0.5)
    };

    public void dropElytra(Location location) {

        Vector randomVector = ElytraLoc[random.nextInt(ElytraLoc.length)];
        Location dropLocation = randomVector.toLocation(location.getWorld());
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        Item droppedItem = location.getWorld().dropItem(dropLocation, elytra);

        droppedItem.setGlowing(true);
        droppedItem.setCustomName(ChatColor.YELLOW + "Elytra");
        droppedItem.setCustomNameVisible(true);

    }

    @EventHandler
    public void onElytraPickup(EntityPickupItemEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        if(!isElytra(event.getItem().getItemStack())) return;
        lock(event.getItem().getItemStack(), (Player) event.getEntity());
    }

    private boolean isElytra(ItemStack itemStack) {
        return itemStack.getType().equals(Material.ELYTRA);
    }

    private UUID resolveOwner(ItemStack itemStack) {
        if (!isElytra(itemStack))
            throw new IllegalArgumentException("not elytra");
        if (!itemStack.hasItemMeta())
            return null;
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        if (container.has(this.key, PersistentDataType.STRING)) {
            String ownerIdString = (String)container.get(this.key, PersistentDataType.STRING);
            if (ownerIdString != null)
                try {
                    return UUID.fromString(ownerIdString);
                } catch (IllegalArgumentException illegalArgumentException) {}
        }
        return null;
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if (!event.isGliding())
            return;
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;
        ItemStack chestplate = ((Player)entity).getInventory().getChestplate();
        if (chestplate == null || !isElytra(chestplate))
            return;
        UUID ownerId = resolveOwner(chestplate);
        if (ownerId == null || !ownerId.equals(entity.getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void blockElytra(InventoryClickEvent event) {
        if (!event.getSlotType().equals(InventoryType.SlotType.ARMOR))
            return;
        if (event.getSlot() != 38)
            return;
        HumanEntity whoClicked = event.getWhoClicked();
        if (!(whoClicked instanceof Player))
            return;
        Player player = (Player)whoClicked;
        Inventory clickedInventory = event.getClickedInventory();
        if (!(clickedInventory instanceof org.bukkit.inventory.PlayerInventory))
            return;
        ItemStack chestplate = clickedInventory.getItem(38);
        if (chestplate != null)
            return;
        ItemStack cursor = event.getCursor();
        if (cursor == null || !isElytra(cursor))
            return;
        UUID ownerId = resolveOwner(cursor);
        if (ownerId == null || !ownerId.equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Nie morzesz korzystać cudzej elytry!");
        }
    }


    NamespacedKey key = new NamespacedKey(Main.getProvidingPlugin(Main.class), "elytraKEey");
    public ItemStack lock(ItemStack itemStack, Player player){
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, player.getUniqueId().toString());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Elytra Gracza: " + player.getDisplayName());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
