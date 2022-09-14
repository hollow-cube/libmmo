package net.hollowcube.blocks.handlers;

import net.hollowcube.blocks.BlockInteractionUtils;
import net.hollowcube.blocks.data.CropBlockData;
import net.hollowcube.blocks.util.BlockUtil;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import net.hollowcube.util.ParticleUtils;

import java.util.Map;

public class FarmlandHandler implements BlockHandler {

    // A Map between a crop's seed item material and data about the seed
    private static final Map<Material, CropBlockData> cropMap = Map.of(
            Material.WHEAT_SEEDS, new CropBlockData(Material.WHEAT_SEEDS, Material.WHEAT, Block.WHEAT, 7, false),
            Material.CARROT, new CropBlockData(Material.CARROT, Material.CARROT, Block.CARROTS, 7, false),
            Material.BEETROOT_SEEDS, new CropBlockData(Material.BEETROOT_SEEDS, Material.BEETROOT, Block.BEETROOTS, 3, false),
            Material.PUMPKIN_SEEDS, new CropBlockData(Material.PUMPKIN_SEEDS, Material.PUMPKIN_SEEDS, Block.PUMPKIN_STEM, 7, true),
            Material.MELON_SEEDS, new CropBlockData(Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Block.MELON_STEM, 7, true)
    );
    private static final int waterUpdateThreshold = 5 * 20; // every 5 seconds
    private static final String moisturePropertyName = "moisture";

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        // If the block ids of the previous and current block match, stop here
        // This is most likely because a property/tag updated, and we shouldn't process things like they have been destroyed.
        if (destroy.getBlock().id() == destroy.getInstance().getBlock(destroy.getBlockPosition()).id()) return;

        Point cropPosition = destroy.getBlockPosition().add(0, 1, 0);
        Instance instance = destroy.getInstance();
        Material material = instance.getBlock(cropPosition).registry().material();
        if (material != null && cropMap.containsKey(material)) {
            CropBlockData data = cropMap.get(instance.getBlock(cropPosition).registry().material());
            // Break block, will call its onDestroy method and drop items
            instance.setBlock(cropPosition, Block.AIR);
            // Spawn particles
            if (destroy instanceof PlayerDestroy playerDestroy) {
                ParticleUtils.spawnBlockBreakParticles(playerDestroy.getPlayer(), playerDestroy.getBlockPosition(), data.seedMaterial());
            }
            instance.playSound(Sound.sound(SoundEvent.BLOCK_GRASS_HIT, Sound.Source.BLOCK, 1f, 1f), cropPosition.blockX(), cropPosition.blockY(), cropPosition.blockZ());
        }
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Player player = interaction.getPlayer();
        Material heldMaterial = player.getItemInMainHand().material();
        if (cropMap.containsKey(heldMaterial)) {
            Point cropPosition = interaction.getBlockPosition().add(0, 1, 0);
            CropBlockData cropData = cropMap.get(heldMaterial);
            Block block = BlockInteractionUtils.storeDataOntoBlock(cropData.cropBlock(), cropData);
            interaction.getInstance().setBlock(cropPosition, block.withHandler(new CropHandler()));
            System.out.println(interaction.getInstance().getBlock(cropPosition));
        }
        return true;
    }

    private int waterUpdateCount = 0;

    @Override
    public void tick(@NotNull Tick tick) {
        waterUpdateCount++;
        if (waterUpdateCount >= waterUpdateThreshold) {
            waterUpdateCount = 0;
            if (hasNearbyWater(tick.getInstance(), tick.getBlockPosition())) {
                tick.getInstance().setBlock(tick.getBlockPosition(), tick.getBlock().withProperty(moisturePropertyName, "7"));
            } else {
                String count = tick.getBlock().getProperty(moisturePropertyName);
                int moistureCount = Integer.parseInt(count);
                if (moistureCount > 0) {
                    tick.getInstance().setBlock(tick.getBlockPosition(), tick.getBlock().withProperty(moisturePropertyName, Integer.toString(--moistureCount)));
                }
            }
        }
    }

    private boolean hasNearbyWater(@NotNull Instance instance, @NotNull Point point) {
        final int waterRange = 4;
        for (int x = point.blockX() - waterRange; x <= point.blockX() + waterRange; x++) {
            for (int y = point.blockY(); y < point.blockY() + 2; y++) { // Checks at farmland y level and 1 above
                for (int z = point.blockZ() - waterRange; z <= point.blockZ() + waterRange; z++) {
                    if (BlockUtil.isWater(instance.getBlock(x, y, z))) {
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
        return BlockInteractionUtils.FARMLAND_HANDLER_ID;
    }
}
