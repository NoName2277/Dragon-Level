package pl.noname.dragonlevel.dragon;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.noname.dragonlevel.Main;


public class Dragon implements Listener {

    private final Main plugin;
    private final ElytraListeners elytraListeners;

    public Dragon(Main plugin, ElytraListeners elytraListeners){
        this.plugin = plugin;
        this.elytraListeners = elytraListeners;
    }

    @EventHandler
    public void onDragonRespawn(CreatureSpawnEvent event){
        if(!(event.getEntity() instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) event.getEntity();
        dragon.setCustomName(ChatColor.LIGHT_PURPLE + "Ender Dragon Level " + ChatColor.BOLD + plugin.getConfig().getInt("DragonLevel"));
        dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200 + plugin.getConfig().getInt("DragonLevel"));
        plugin.getLogger().info("Utworzono Smoka o levelu " + plugin.getConfig().getInt("DragonLevel"));
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event){
        if(!(event.getEntity() instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) event.getEntity();
        plugin.getConfig().set("DragonLevel", plugin.getConfig().getInt("DragonLevel") + 1);
        elytraListeners.dropElytra(dragon.getLocation());
        plugin.saveConfig();
    }
}
