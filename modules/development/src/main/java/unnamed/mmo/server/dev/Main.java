package unnamed.mmo.server.dev;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.world.DimensionType;
import unnamed.mmo.blocks.BlockInteracter;
import unnamed.mmo.blocks.ore.Ore;
import unnamed.mmo.chat.ChatManager;
import unnamed.mmo.chat.storage.ChatStorage;
import unnamed.mmo.command.BaseCommandRegister;
import unnamed.mmo.damage.DamageProcessor;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemManager;
import unnamed.mmo.player.PlayerImpl;
import unnamed.mmo.quest.QuestFacet;
import unnamed.mmo.server.dev.tool.DebugToolManager;
import unnamed.mmo.server.instance.TickTrackingInstance;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        MojangAuth.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(PlayerImpl::new);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        Instance instance = new TickTrackingInstance(UUID.randomUUID(), DimensionType.OVERWORLD);
        instanceManager.registerInstance(instance);
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.STONE));

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.SURVIVAL);
            player.setPermissionLevel(4);
            player.setAllowFlying(true);

            // Testing
            event.getSpawnInstance().setBlock(5, 43, 5, Ore.fromNamespaceId("unnamed:gold_ore").asBlock());
            event.getSpawnInstance().setBlock(4, 43, 5, Ore.fromNamespaceId("unnamed:diamond_ore").asBlock());
            player.getInventory().addItemStack(Item.fromNamespaceId("unnamed:diamond_pickaxe").asItemStack());

            //todo this needs to be done elsewhere
            player.addEffect(new Potion(PotionEffect.MINING_FATIGUE, (byte) -1, Short.MAX_VALUE, (byte) 0x0));

            //todo a command for this
            player.getInventory().addItemStack(DebugToolManager.createTool("unnamed:hello"));
        });

        BaseCommandRegister.registerCommands();

        // For now, manually register chat (with conn to mongo :/ need a config system)
//        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
//                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
//                .uuidRepresentation(UuidRepresentation.STANDARD)
//                .build());
//        ChatStorage chatStorage = ChatStorage.mongo(mongoClient);
        ChatStorage chatStorage = ChatStorage.noop();
        ChatManager chatManager = new ChatManager(chatStorage);
        chatManager.hook(MinecraftServer.process());

        //todo properly implement a config system & use facets better
        ItemManager itemManager = new ItemManager();
        itemManager.hook(MinecraftServer.process());

        //todo stupid facet implementation
        DebugToolManager debugToolManager = new DebugToolManager();
        debugToolManager.hook(MinecraftServer.process());

        QuestFacet questFacet = new QuestFacet();
        questFacet.hook(MinecraftServer.process());

        MinecraftServer.getSchedulerManager().buildShutdownTask(() ->
                ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        BlockInteracter.registerEvents();
        DamageProcessor.init();

        server.start("0.0.0.0", 25565);
    }

}
