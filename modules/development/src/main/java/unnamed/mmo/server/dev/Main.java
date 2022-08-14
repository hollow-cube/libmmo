package unnamed.mmo.server.dev;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
import org.bson.UuidRepresentation;
import unnamed.mmo.blocks.BlockInteracter;
import unnamed.mmo.chat.ChatManager;
import unnamed.mmo.chat.storage.ChatStorage;
import unnamed.mmo.command.BaseCommandRegister;
import unnamed.mmo.damage.DamageProcessor;
import unnamed.mmo.item.ItemManager;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        MojangAuth.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        Instance instance = instanceManager.createInstanceContainer();
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
        });

        BaseCommandRegister.registerCommands();

        // For now, manually register chat (with conn to mongo :/ need a config system)
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build());
        ChatStorage chatStorage = ChatStorage.mongo(mongoClient);
        ChatManager chatManager = new ChatManager(chatStorage);
        chatManager.hook(MinecraftServer.process());

        //todo properly implement a config system & use facets better
        ItemManager itemManager = new ItemManager();
        itemManager.hook(MinecraftServer.process());

        MinecraftServer.getSchedulerManager().buildShutdownTask(() ->
                ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        BlockInteracter.registerEvents();
        DamageProcessor.init();

        server.start("0.0.0.0", 25565);
    }

}
