package net.hollowcube.server.dev;

import net.hollowcube.item.crafting.RecipeList;
import net.hollowcube.item.crafting.ToolCraftingInventory;
import net.hollowcube.player.PlayerImpl;
import net.hollowcube.server.dev.tool.DebugToolManager;
import net.hollowcube.server.instance.TickTrackingInstance;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.Layer;
import com.mattworzala.debug.shape.Line;
import com.mattworzala.debug.shape.Text;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.adventure.MinestomAdventure;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.CachedPacket;
import net.minestom.server.network.packet.server.FramedPacket;
import net.minestom.server.network.packet.server.LazyPacket;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.network.packet.server.*;
import net.minestom.server.network.packet.server.play.EntityHeadLookPacket;
import net.minestom.server.network.packet.server.play.EntityPositionAndRotationPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.blocks.BlockInteracter;
import net.hollowcube.blocks.ore.Ore;
import net.hollowcube.item.Item;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;
import net.hollowcube.server.dev.command.BaseCommandRegister;
import unnamed.mmo.blocks.BlockInteracter;
import unnamed.mmo.blocks.ore.Ore;
import unnamed.mmo.chat.ChatManager;
import unnamed.mmo.chat.storage.ChatStorage;
import unnamed.mmo.command.BaseCommandRegister;
import unnamed.mmo.damage.DamageProcessor;
import unnamed.mmo.data.number.NumberProvider;
import unnamed.mmo.entity.HeadRotationZombie;
import unnamed.mmo.logging.LoggerFactory;
import unnamed.mmo.entity.UnnamedEntity;
import unnamed.mmo.entity.brain.task.*;
import unnamed.mmo.item.Item;
import unnamed.mmo.damage.DamageProcessor;
import unnamed.mmo.item.Item;
import unnamed.mmo.item.ItemManager;
import unnamed.mmo.mql.MqlScript;
import unnamed.mmo.item.entity.OwnedItemEntity;
import unnamed.mmo.player.PlayerImpl;
import unnamed.mmo.quest.QuestFacet;
import unnamed.mmo.server.dev.tool.DebugToolManager;
import unnamed.mmo.server.instance.TickTrackingInstance;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {
        System.setProperty("minestom.terminal.disabled", "true");

        MinecraftServer server = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        Instance instance = instanceManager.createInstanceContainer();
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.STONE));

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setPermissionLevel(2);
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


            //todo test entity
//            JsonElement json = JsonParser.parseString("""
//                    {
//                        "type": "unnamed:selector",
//                        "children": {
//                            "q.has_target": {
//                                "type": "unnamed:follow_target"
//                            },
//                            "": {
//                                "type": "unnamed:sequence",
//                                "children": [
//                                    {
//                                        "type": "unnamed:wander_in_region"
//                                    },
//                                    {
//                                        "type": "unnamed:idle",
//                                        "time": 5
//                                    }
//                                ],
//
//                                "canInterrupt": true
//                            }
//                        }
//                    }""");
//            JsonElement json = JsonParser.parseString("""
//                    {
//                        "type": "unnamed:sequence",
//                        "children": [
//                            {
//                                "type": "unnamed:wander_in_region"
//                            },
//                            {
//                                "type": "unnamed:idle",
//                                "time": 20
//                            }
//                        ]
//                    }""");
//            Task task = JsonOps.INSTANCE.withDecoder(Task.Spec.CODEC)
//                    .apply(json).getOrThrow(false, System.err::println).getFirst().create();
//            UnnamedEntity entity = new UnnamedEntity(task);
//            entity.setInstance(instance, new Pos(0, 40, 0))
//                    .thenAccept(unused -> System.out.println("Spawned"));

//            Entity entity = new Entity(EntityType.ZOMBIE) {
//                @Override
//                public void tick(long time) {
//                    super.tick(time);
//
//                    lookAt(player);
//                }
//            };
            Entity entity = new HeadRotationZombie();
            entity.setInstance(instance, new Pos(0, 40, 0));
        });

        BaseCommandRegister.registerCommands(); //todo this should be in a facet?


        // Discover loaded facets
        Map<Class<?>, Facet> facets = new HashMap<>();
        for (Facet facet : ServiceLoader.load(Facet.class)) {
            facets.put(facet.getClass(), facet);
        }

        // Hook each facet
        for (Facet facet : facets.values()) {
            facet.hook(new ServerWrapper() {
                @Override
                public @NotNull ServerProcess process() {
                    return MinecraftServer.process();
                }

                @Override
                public <F extends Facet> @Nullable F getFacet(@NotNull Class<F> type) {
                    return (F) facets.get(type);
                }

                @Override
                public void addEventNode(@NotNull EventNode<?> node) {
                    process().eventHandler().addChild(node);
                }

                @Override
                public void registerCommand(@NotNull Command command) {
                    process().command().register(command);
                }

                @Override
                public void registerBlockHandler(@NotNull Supplier<BlockHandler> handlerSupplier) {
                    process().block().registerHandler(handlerSupplier.get().getNamespaceId(), handlerSupplier);
                }
            });
        }


        MinecraftServer.getSchedulerManager().buildShutdownTask(() ->
                ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));


        BlockInteracter.registerEvents();

        server.start("0.0.0.0", 25565);

        Command craftCommand = new Command("craft");
        craftCommand.setDefaultExecutor((sender, context) -> {
            if (sender instanceof Player player) {
                player.openInventory(new ToolCraftingInventory(new RecipeList()));
            }
        });

        MinecraftServer.getCommandManager().register(craftCommand);
    }

}
