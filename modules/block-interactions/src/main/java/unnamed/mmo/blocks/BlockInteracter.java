package unnamed.mmo.blocks;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.item.Material;
import unnamed.mmo.blocks.handlers.TillHandler;

public class BlockInteracter {

    public static void registerEvents() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, event -> {
           if(event.getBlock().registry().material() == Material.DIRT) {
               event.setBlock(event.getBlock().withHandler(new TillHandler()));
           }
        });
    }
}
