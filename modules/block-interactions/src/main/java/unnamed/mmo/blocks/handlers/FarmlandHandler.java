package unnamed.mmo.blocks.handlers;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.BlockInteractionUtils;

import java.util.Set;

public class FarmlandHandler implements BlockHandler {

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        BlockHandler.super.onDestroy(destroy);
        Point cropPosition = destroy.getBlockPosition().add(0, 1, 0);
        if(plantables.contains(destroy.getInstance().getBlock(cropPosition).registry().material())) {
            // Break it too
            destroy.getInstance().setBlock(cropPosition, Block.AIR);
            // TODO Drop item
            // TODO Particle effects
            // TODO Sound
        }
    }

    private final Set<Material> plantables = Set.of(Material.WHEAT_SEEDS, Material.CARROT, Material.POTATO, Material.BEETROOT_SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS);

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Player player = interaction.getPlayer();
        Material heldMaterial = player.getItemInMainHand().material();
        if(plantables.contains(heldMaterial)) {
            Point cropPosition = interaction.getBlockPosition().add(0, 1, 0);
            if(heldMaterial == Material.WHEAT_SEEDS) {
                Block block = Material.WHEAT.block().withProperty("age", "0").withHandler(new CropHandler());
                interaction.getInstance().setBlock(cropPosition, block);
            }
            // TODO Other crops
            return true;
        } else {
            return false;
        }
    }

    private final int waterUpdateThreshold = 5*20; // every 5 seconds
    private int waterUpdateCount = 0;
    private final String propertyName = "moisture";

    @Override
    public void tick(@NotNull Tick tick) {
        waterUpdateCount++;
        if(waterUpdateCount >= waterUpdateThreshold) {
            waterUpdateCount = 0;
            if(hasNearbyWater(tick.getInstance(), tick.getBlockPosition())) {
                tick.getInstance().setBlock(tick.getBlockPosition(), tick.getBlock().withProperty(propertyName, "7"));
            } else {
                String count = tick.getBlock().getProperty(propertyName);
                int moistureCount = Integer.parseInt(count);
                if(moistureCount > 0) {
                    tick.getInstance().setBlock(tick.getBlockPosition(), tick.getBlock().withProperty(propertyName, Integer.toString(--moistureCount)));
                }
            }
        }
    }

    private boolean hasNearbyWater(Instance instance, Point point) {
        final int waterRange = 4;
        for(int x = point.blockX() - waterRange; x <= point.blockX() + waterRange; x++) {
            for(int y = point.blockY(); y < point.blockY() + 2; y++) { // Checks at farmland y level and 1 above
                for(int z = point.blockZ() - waterRange; z <= point.blockZ() + waterRange; z++) {
                    if(instance.getBlock(x, y, z).isLiquid()) { //TODO: Find a better water check, Water and flowing water aren't in the material list??
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return BlockInteractionUtils.createInteractionID("farmlandhandler");
    }
}
