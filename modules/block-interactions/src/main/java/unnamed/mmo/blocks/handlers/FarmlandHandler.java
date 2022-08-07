package unnamed.mmo.blocks.handlers;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.BlockInteractionUtils;
import unnamed.mmo.blocks.ParticleUtils;

import java.util.Map;

public class FarmlandHandler implements BlockHandler {

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        // Called when moisture updates, since you have to set the block again with a different property
        // Todo: Fix somehow?
        Point cropPosition = destroy.getBlockPosition().add(0, 1, 0);
        Instance instance = destroy.getInstance();
        Material material = instance.getBlock(cropPosition).registry().material();
        if(material != null && cropMap.containsKey(material)) {
            // Drop item
            Entity entity = new Entity(EntityType.ITEM);
            CropBlockData data = cropMap.get(instance.getBlock(cropPosition).registry().material());
            if(entity.getEntityMeta() instanceof ItemEntityMeta itemEntityMeta) {
                itemEntityMeta.setItem(ItemStack.of(data.seedMaterial()));
            }
            //entity.setInstance(instance, cropPosition);
            // Break it too
            instance.setBlock(cropPosition, Block.AIR);
            // Spawn particles
            if(destroy instanceof PlayerDestroy playerDestroy) {
                ParticleUtils.spawnBlockBreakParticles(playerDestroy.getPlayer(), playerDestroy.getBlockPosition(), data.seedMaterial());
            }
            instance.playSound(Sound.sound(SoundEvent.BLOCK_GRASS_HIT, Sound.Source.BLOCK, 1f, 1f), cropPosition.blockX(), cropPosition.blockY(), cropPosition.blockZ());
        }
    }

    // A Map between a crop's seed item material and data about the seed
    private final Map<Material, CropBlockData> cropMap = Map.of(
            Material.WHEAT_SEEDS, new CropBlockData(Material.WHEAT_SEEDS, Material.WHEAT, Block.WHEAT, 7, false),
            Material.CARROT, new CropBlockData(Material.CARROT, Material.CARROT, Block.CARROTS, 7, false),
            Material.BEETROOT_SEEDS, new CropBlockData(Material.BEETROOT_SEEDS, Material.BEETROOT, Block.BEETROOTS, 3, false),
            Material.PUMPKIN_SEEDS, new CropBlockData(Material.PUMPKIN_SEEDS, Material.PUMPKIN_SEEDS, Block.PUMPKIN_STEM, 7, true),
            Material.MELON_SEEDS, new CropBlockData(Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Block.MELON_STEM, 7, true)
    );

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Player player = interaction.getPlayer();
        Material heldMaterial = player.getItemInMainHand().material();
        if(cropMap.containsKey(heldMaterial)) {
            Point cropPosition = interaction.getBlockPosition().add(0, 1, 0);
            CropBlockData cropData = cropMap.get(heldMaterial);
            Block block = BlockInteractionUtils.storeDataOntoBlock(cropData.cropBlock(), cropData);
            interaction.getInstance().setBlock(cropPosition, block.withHandler(new CropHandler()));
            System.out.println(interaction.getInstance().getBlock(cropPosition));
        }
        return true;
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

    private boolean hasNearbyWater(@NotNull Instance instance, @NotNull Point point) {
        final int waterRange = 4;
        for(int x = point.blockX() - waterRange; x <= point.blockX() + waterRange; x++) {
            for(int y = point.blockY(); y < point.blockY() + 2; y++) { // Checks at farmland y level and 1 above
                for(int z = point.blockZ() - waterRange; z <= point.blockZ() + waterRange; z++) {
                    if(instance.getBlock(x, y, z).id() == Block.WATER.id()) { //TODO: Waterlogged blocks should count as well
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
