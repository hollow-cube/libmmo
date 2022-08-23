package unnamed.mmo.server.dev.navmesh;

import com.google.auto.service.AutoService;
import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.Layer;
import com.mattworzala.debug.shape.OutlineBox;
import com.mattworzala.debug.shape.Text;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unnamed.mmo.server.dev.tool.DebugTool;
import unnamed.mmo.util.ComponentUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AutoService(DebugTool.class)
public class NavmeshDebugTool implements DebugTool {

    @Override
    public @NotNull NamespaceID namespace() {
        return NamespaceID.from("unnamed:navmesh");
    }

    @Override
    public @NotNull ItemStack itemStack() {
        return ItemStack.builder(Material.BLAZE_ROD)
                .displayName(ComponentUtil.fromStringSafe("Navmesh"))
                .lore(ComponentUtil.fromStringSafe("Right click to create mesh"))
                .build();
    }

    private static List<Point> visited = new ArrayList<>();

    @Override
    public @NotNull ItemStack rightClicked(@NotNull Player player, @NotNull ItemStack itemStack, @Nullable Point targetBlock, @Nullable Entity targetEntity) {
        if (targetBlock == null) {
            player.sendMessage("Click a block to create a mesh");
            return itemStack;
        }

        player.sendMessage("Creating mesh");
        Instance instance = player.getInstance();
        Point start = targetBlock.add(0, 1, 0);

        visited.clear();
        createMeshRecursive(start, instance, start);
//        mergeOnX(player);
//        mergeOnY(player);

        var debug = DebugMessage.builder()
                .clear("minecraft");
        for (Voxel voxel : voxels) {
            debug.set(NamespaceID.from("blah_" + ThreadLocalRandom.current().nextInt(10000)),
                    new OutlineBox.Builder()
                            .start(new Vec(voxel.x1(), voxel.y1(), voxel.z1()))
                            .end(new Vec(voxel.x2(), voxel.y2(), voxel.z2()))
                            .layer(Layer.INLINE)
                            .layerLine(Layer.INLINE)
                            .color(0x22FFFFFF)
                            .colorLine(0x88FF0000)
                            .build());
        }
        debug.build().sendTo(player);

        return itemStack;
    }

    private List<Voxel> voxels = new ArrayList<>();

    private void mergeOnX(Player player) {
        List<Voxel> newVoxels = new ArrayList<>();

        while (!voxels.isEmpty()) {

            Voxel start = voxels.get(0);
            voxels.remove(0);

            // Can determine if two boxes form a convex shape by checking if the area of them joined is exactly the area of them added individually

            boolean found = true;
            while (found) {
                found = false;
                var iter = voxels.iterator();
                while (iter.hasNext()) {
                    var voxel = iter.next();
                    if (voxel.contains(start.x2(), start.y1(), start.z1())) {
                        start = Voxel.merge(start, voxel);
                        iter.remove();
                        found = true;
                    } else if (voxel.contains(start.x1() - 1, start.y1(), start.z1())) {
                        start = Voxel.merge(start, voxel);
                        iter.remove();
                        found = true;
                    }
                }
            }

            newVoxels.add(start);
        }

        voxels = newVoxels;
    }

    private void mergeOnY(Player player) {
        List<Voxel> newVoxels = new ArrayList<>();

        while (!voxels.isEmpty()) {

            Voxel start = voxels.get(0);
            voxels.remove(0);

            // Can determine if two boxes form a convex shape by checking if the area of them joined is exactly the area of them added individually

            boolean found = true;
            while (found) {
                found = false;
                var iter = voxels.iterator();
                while (iter.hasNext()) {
                    var voxel = iter.next();
                    if (voxel.contains(start.x1(), start.y1(), start.z2())) {
                        Voxel merged = Voxel.mergeSafe(start, voxel);
                        if (merged != null) {
                            start = merged;
                            iter.remove();
                            found = true;
                        }
                    }
                    if (voxel.contains(start.x1(), start.y1(), start.z1() - 1)) {
                        Voxel merged = Voxel.mergeSafe(start, voxel);
                        if (merged != null) {
                            start = merged;
                            iter.remove();
                            found = true;
                        }
                    }
                }
            }

            newVoxels.add(start);
        }

        voxels = newVoxels;
    }


    private void createMeshRecursive(Point from, Instance instance, Point point) {
        if (visited.contains(point) || !from.sameChunk(point) || !instance.getBlock(point).isAir())
            return;

        visited.add(point);
        voxels.add(new Voxel(point, point.add(1, 1, 1)));
//        debug.set(NamespaceID.from(point.blockX() + "_" + point.blockY() + "_" + point.blockZ()),
//                new OutlineBox.Builder()
//                        .start(new Vec(point.blockX(), point.blockY(), point.blockZ()))
//                        .end(new Vec(point.blockX() + 1, point.blockY() + 0.05, point.blockZ() + 1))
//                        .layer(Layer.INLINE)
//                        .layerLine(Layer.INLINE)
//                        .color(0x22FFFFFF)
//                        .colorLine(0x88FF0000)
//                        .build());
//        debug.set(NamespaceID.from(point.blockX() + "_" + point.blockY() + "_" + point.blockZ() + "_dot"),
//                new OutlineBox.Builder()
//                        .start(new Vec(point.blockX() + 0.45, point.blockY() + 0.05, point.blockZ() + 0.45))
//                        .end(new Vec(point.blockX() + 0.55, point.blockY() + 0.15, point.blockZ() + 0.55))
//                        .layer(Layer.INLINE)
//                        .layerLine(Layer.INLINE)
//                        .color(0x88FF0000)
//                        .colorLine(0xFFFFFFFF)
//                        .build());

        createMeshRecursive(point, instance, point.add(1, 0, 0));
        createMeshRecursive(point, instance, point.add(-1, 0, 0));
        createMeshRecursive(point, instance, point.add(0, 0, 1));
        createMeshRecursive(point, instance, point.add(0, 0, -1));
    }
}
