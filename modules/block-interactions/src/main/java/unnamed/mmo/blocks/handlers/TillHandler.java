package unnamed.mmo.blocks.handlers;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import unnamed.mmo.blocks.BlockInteractionUtils;

import java.util.Set;

public class TillHandler implements BlockHandler {

    private final Set<Material> hoes = Set.of(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE);

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        // Till if you have a hoe
        Player player = interaction.getPlayer();
        if(hoes.contains(player.getItemInMainHand().material())) {
            // convert block to farmland
            Block block = Block.FARMLAND.withHandler(new FarmlandHandler());
            interaction.getInstance().setBlock(interaction.getBlockPosition(), block);
            interaction.getInstance().playSound(Sound.sound(SoundEvent.ITEM_HOE_TILL, Sound.Source.BLOCK, 1f, 1f));
            // TODO: Damage item in hand?
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return BlockInteractionUtils.createInteractionID("tillhandler");
    }
}
