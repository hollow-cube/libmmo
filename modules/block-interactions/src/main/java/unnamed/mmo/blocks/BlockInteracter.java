package unnamed.mmo.blocks;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

public class BlockInteracter {

    public static void registerEvents() {

        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getItemInMainHand().material() == Material.WATER_BUCKET) {
                player.getInstance().setBlock(event.getBlockPosition().relative(event.getBlockFace()), Block.WATER);
                player.setItemInMainHand(player.getItemInMainHand().withMaterial(Material.BUCKET));
            }
        });
        BlockInteractionUtils.registerHandlers();
    }
}
