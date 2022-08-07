package unnamed.mmo.blocks.handlers;

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
import unnamed.mmo.blocks.BlockInteractionUtils;

import java.util.Set;

public class TillHandler implements BlockHandler {

    private static final Set<Material> hoes = Set.of(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE);

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        // Till if you have a hoe
        Player player = interaction.getPlayer();
        Instance instance = interaction.getInstance();
        Point point = interaction.getBlockPosition();
        if (hoes.contains(player.getItemInMainHand().material())) {
            // convert block to farmland
            Block block = Block.FARMLAND.withHandler(new FarmlandHandler());
            instance.setBlock(point, block);
            instance.playSound(Sound.sound(SoundEvent.ITEM_HOE_TILL, Sound.Source.BLOCK, 1f, 1f), point.blockX(), point.blockY(), point.blockZ());
            // TODO: Damage item in hand?
        }
        return true;
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return BlockInteractionUtils.TILL_HANDLER_ID;
    }
}
