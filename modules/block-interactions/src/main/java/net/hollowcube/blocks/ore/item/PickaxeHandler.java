package net.hollowcube.blocks.ore.item;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import net.hollowcube.blocks.ore.handler.OreBlockHandler;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.blocks.ore.Ore;
import net.hollowcube.item.Item;
import net.hollowcube.item.ItemComponentHandler;
import net.hollowcube.player.event.PlayerLongDiggingStartEvent;

@AutoService(ItemComponentHandler.class)
public class PickaxeHandler implements ItemComponentHandler<Pickaxe> {

    private final EventNode<Event> eventNode = EventNode.all("starlight:pickaxe/item_component_handler");

    public PickaxeHandler() {
        eventNode.addListener(PlayerLongDiggingStartEvent.class, this::handleLongDiggingStart);
    }

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("starlight:pickaxe");
    }

    @Override
    public @NotNull Class<Pickaxe> componentType() {
        return Pickaxe.class;
    }

    @Override
    public @NotNull Codec<@NotNull Pickaxe> codec() {
        return Pickaxe.CODEC;
    }

    @Override
    public @Nullable EventNode<Event> eventNode() {
        return eventNode;
    }

    private void handleLongDiggingStart(PlayerLongDiggingStartEvent event) {
//        var ore = Ore.fromBlock(event.getBlock());
//        if (ore == null || event.getBlock().compare(OreBlockHandler.REPLACEMENT_BLOCK, Block.Comparator.STATE)) return;
//
//        // Ensure they have a pickaxe in hand and get the pickaxe
//        var item = Item.fromItemStack(event.getPlayer().getItemInMainHand());
//        //todo will currently fail on any non-custom item
//
//        var pickaxe = item.getComponent(Pickaxe.class);
//        if (pickaxe == null) return; // Not holding a pickaxe
//
//        // Start mining the block
//        event.setDiggingBlock(
//                ore.health(),
//                pickaxe::miningSpeed
//        );
    }

}
