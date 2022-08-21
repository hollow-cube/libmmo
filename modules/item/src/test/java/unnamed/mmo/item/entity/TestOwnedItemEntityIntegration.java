package unnamed.mmo.item.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static net.minestom.server.test.truth.MinestomTruth.assertThat;

@EnvTest
public class TestOwnedItemEntityIntegration {

    @Test
    public void testHappyCase(Env env) throws Exception {
        // Register associated handlers
        new OwnedItemEntity.Handler().hook(env.process());

        // Create the player in an instance
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        Thread.sleep(1000);

        // Create the item entity in the instance
        var itemStack = ItemStack.of(Material.COAL);
        var itemEntity = new OwnedItemEntity(player.getUuid(), itemStack);
        itemEntity.setInstance(instance, new Pos(0, 42, 0)).join();

        env.tick();

        assertThat(itemEntity).isRemoved();
        assertThat(player.getInventory())
                .containsExactly(itemStack);
    }

    @Test
    public void testNonOwnerPickup(Env env) {
        // Register associated handlers
        new OwnedItemEntity.Handler().hook(env.process());

        // Create the player in an instance
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        // Create the item entity in the instance with a random uuid
        var itemStack = ItemStack.of(Material.COAL);
        var itemEntity = new OwnedItemEntity(UUID.randomUUID(), itemStack);
        itemEntity.setInstance(instance, new Pos(0, 42, 0)).join();

        env.tick();

        assertThat(itemEntity).isNotRemoved();
        assertThat(player.getInventory()).isEmpty();
    }

    @Test
    public void testFullInventoryPickup(Env env) {
        // Register associated handlers
        new OwnedItemEntity.Handler().hook(env.process());

        // Create the player in an instance
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        // Fill the player inventory with diamond
        var diamond = ItemStack.of(Material.DIAMOND);
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            player.getInventory().setItemStack(i, diamond);
        }

        // Create the item entity in the instance
        var itemStack = ItemStack.of(Material.COAL);
        var itemEntity = new OwnedItemEntity(player.getUuid(), itemStack);
        itemEntity.setInstance(instance, new Pos(0, 42, 0)).join();

        env.tick();

        assertThat(itemEntity).isNotRemoved();
        assertThat(player.getInventory()).doesNotContain(itemStack);
    }

    @Test
    public void testSameItemSameOwnerMerge(Env env) {
        // Register associated handlers
        new OwnedItemEntity.Handler().hook(env.process());

        // Create an instance and add two of the same item
        var instance = env.createFlatInstance();
        instance.loadChunk(0, 0).join();

        var itemStack = ItemStack.of(Material.COAL);
        var uuid = UUID.randomUUID();

        var itemEntity1 = new OwnedItemEntity(uuid, itemStack);
        itemEntity1.setInstance(instance, new Pos(0, 42, 0)).join();

        var itemEntity2 = new OwnedItemEntity(uuid, itemStack);
        itemEntity2.setInstance(instance, new Pos(0, 42, 0)).join();

        env.tick();

        assertThat(itemEntity1).isNotRemoved();
        assertThat(itemEntity2).isRemoved();
        assertThat(itemEntity1.getItemStack())
                .hasAmount(2);
    }

    @Test
    public void testSameItemDiffOwnerDontMerge(Env env) {
        // Register associated handlers
        new OwnedItemEntity.Handler().hook(env.process());

        // Create an instance and add two of the same item
        var instance = env.createFlatInstance();
        instance.loadChunk(0, 0).join();

        var itemStack = ItemStack.of(Material.COAL);

        var itemEntity1 = new OwnedItemEntity(UUID.randomUUID(), itemStack);
        itemEntity1.setInstance(instance, new Pos(0, 42, 0)).join();

        var itemEntity2 = new OwnedItemEntity(UUID.randomUUID(), itemStack);
        itemEntity2.setInstance(instance, new Pos(0, 42, 0)).join();

        env.tick();

        assertThat(itemEntity1).isNotRemoved();
        assertThat(itemEntity2).isNotRemoved();
    }
}
