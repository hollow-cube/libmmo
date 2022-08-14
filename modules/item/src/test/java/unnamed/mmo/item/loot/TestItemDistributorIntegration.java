package unnamed.mmo.item.loot;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.test.Env;
import net.minestom.server.test.EnvTest;
import org.junit.jupiter.api.Test;
import unnamed.mmo.data.NumberSource;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.entity.OwnedItemEntity;
import unnamed.mmo.loot.LootContext;

import static com.google.common.truth.Truth.assertThat;

@EnvTest
public class TestItemDistributorIntegration {
    private static final ItemDistributor distributor = new ItemDistributor();

    @Test
    public void testDistributeNoContext(Env env) {
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        var context = LootContext.builder("test")
                .numbers(NumberSource.constant(1))
                .build();

        distributor.apply(context, Item.fromNamespaceId("test:item")).join();

        assertThat(instance.getEntities()).containsExactly(player);
    }

    @Test
    public void testDistributeToThisEntity(Env env) {
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        var context = LootContext.builder("test")
                .numbers(NumberSource.constant(1))
                .key(LootContext.THIS_ENTITY, player)
                .build();

        var item = Item.fromNamespaceId("test:item");
        distributor.apply(context, item).join();

        var itemEntity = (OwnedItemEntity) instance.getEntities().stream()
                .filter(ent -> ent instanceof OwnedItemEntity)
                .findFirst()
                .orElse(null);

        assertThat(itemEntity).isNotNull();
        assertThat(itemEntity.getItemStack()).isEqualTo(item.asItemStack());
        assertThat(itemEntity.getPosition().sameBlock(player.getPosition())).isTrue(); //todo Point/Pos subject
    }

    @Test
    public void testDistributeToThisAtPos(Env env) {
        var instance = env.createFlatInstance();
        var player = env.createPlayer(instance, new Pos(0, 42, 0));

        var pos = new Pos(2.5, 2.5, 2.5);
        var context = LootContext.builder("test")
                .numbers(NumberSource.constant(1))
                .key(LootContext.THIS_ENTITY, player)
                .key(LootContext.POSITION, pos)
                .build();

        var item = Item.fromNamespaceId("test:item");
        distributor.apply(context, item).join();

        var itemEntity = (OwnedItemEntity) instance.getEntities().stream()
                .filter(ent -> ent instanceof OwnedItemEntity)
                .findFirst()
                .orElse(null);

        assertThat(itemEntity).isNotNull();
        assertThat(itemEntity.getItemStack()).isEqualTo(item.asItemStack());
        assertThat(itemEntity.getPosition()).isEqualTo(pos);
    }
}
