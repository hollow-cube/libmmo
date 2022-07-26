package net.hollowcube.server.dev;

import net.hollowcube.player.PlayerImpl;
import net.hollowcube.server.dev.tool.DebugToolManager;
import net.hollowcube.server.instance.TickTrackingInstance;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.hollowcube.blocks.BlockInteracter;
import net.hollowcube.blocks.ore.Ore;
import net.hollowcube.item.Item;
import net.hollowcube.server.Facet;
import net.hollowcube.server.ServerWrapper;
import net.hollowcube.server.dev.command.BaseCommandRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {
        System.setProperty("minestom.terminal.disabled", "true");

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
            event.getSpawnInstance().setBlock(5, 43, 5, Ore.fromNamespaceId("starlight:gold_ore").asBlock());
            event.getSpawnInstance().setBlock(4, 43, 5, Ore.fromNamespaceId("starlight:diamond_ore").asBlock());
            player.getInventory().addItemStack(Item.fromNamespaceId("starlight:diamond_pickaxe").asItemStack());

            //todo this needs to be done elsewhere
            player.addEffect(new Potion(PotionEffect.MINING_FATIGUE, (byte) -1, Short.MAX_VALUE, (byte) 0x0));

            //todo a command for this
            player.getInventory().addItemStack(DebugToolManager.createTool("starlight:hello"));
        });

        BaseCommandRegister.registerCommands(); //todo this should be in a facet?


        // Discover loaded facets
        Map<Class<?>, Facet> facets = new HashMap<>();
        for (Facet facet : ServiceLoader.load(Facet.class)) {
            System.out.println("FACET : " + facet.getClass().getName());
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
    }

}
