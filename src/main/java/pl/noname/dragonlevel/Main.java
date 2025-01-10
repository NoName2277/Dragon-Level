package pl.noname.dragonlevel;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.noname.dragonlevel.dragon.Dragon;
import pl.noname.dragonlevel.dragon.ElytraListeners;
import sun.tools.jconsole.Tab;

public final class Main extends JavaPlugin implements Listener {

    private Dragon dragon;
    private ElytraListeners elytraListeners;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        elytraListeners = new ElytraListeners(this);
        dragon = new Dragon(this, elytraListeners);
        getServer().getPluginManager().registerEvents(new Dragon(this, elytraListeners), this);
        getServer().getPluginManager().registerEvents(new ElytraListeners(this), this);
    }


    @Override
    public void onDisable() {

    }
}
