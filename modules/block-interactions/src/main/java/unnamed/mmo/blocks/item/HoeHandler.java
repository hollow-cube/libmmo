package unnamed.mmo.blocks.item;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.blocks.handlers.FarmlandHandler;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemComponentHandler;

@AutoService(ItemComponentHandler.class)
public class HoeHandler implements ItemComponentHandler<Hoe> {
    private final EventNode<Event> eventNode = EventNode.all("comphandler_unnamed:hoe");

    public HoeHandler() {
        eventNode.addListener(PlayerUseItemOnBlockEvent.class, this::handleUseItem);
    }

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:hoe");
    }

    @Override
    public @NotNull Class<Hoe> componentType() {
        return Hoe.class;
    }

    @Override
    public @NotNull Codec<@NotNull Hoe> codec() {
        return Hoe.CODEC;
    }

    @Override
    public @Nullable EventNode<Event> eventNode() {
        return eventNode;
    }

    private void handleUseItem(PlayerUseItemOnBlockEvent event) {
        // Emulates behavior from original TillHandler, but i'm not sure it
        // should be here. Minestom doesnt have proper hand ordering (eg main
        // first and if an action was taken don't do off hand) i dont think.
        if (event.getHand() != Player.Hand.MAIN) return;

        final Instance instance = event.getInstance();
        final Point blockPosition = event.getPosition();
        if (instance.getBlock(blockPosition, Block.Getter.Condition.TYPE).id() != Block.DIRT.id())
            return;

        final ItemStack itemStack = event.getItemStack();
        final Item item = Item.fromItemStack(itemStack);

        // Ensure the item is a hoe
        final Hoe hoe = item.getComponent(Hoe.class);
        if (hoe == null) return;

        // Convert block to farmland
        final Block newBlock = Block.FARMLAND.withHandler(new FarmlandHandler());

        instance.setBlock(blockPosition, newBlock);
        instance.playSound(Sound.sound(SoundEvent.ITEM_HOE_TILL, Sound.Source.BLOCK, 1f, 1f),
                blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ());

        // TODO: Damage item in hand?
    }
}
