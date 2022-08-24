package unnamed.mmo.server.dev.navmesh;

import com.google.auto.service.AutoService;
import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.Layer;
import com.mattworzala.debug.shape.Line;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        voxels.clear();
        pointMap.clear();
        createMeshRecursive(start, instance, start, null);
        // Add neighbors
        for (Voxel voxel : voxels) {
            for (Point neighbor : List.of(voxel.min().add(1, 0, 0), voxel.min().add(-1, 0, 0), voxel.min().add(0, 0, 1), voxel.min().add(0, 0, -1))) {
                Voxel neighborVoxel = pointMap.get(neighbor);
                if (neighborVoxel == null) continue;
                voxel.edges.add(neighborVoxel);
            }
        }
        mergeOnX(player);
        mergeOnY(player);

        var debug = DebugMessage.builder();
//                .clear("minecraft");
//        for (var voxel : voxels) {
//            drawVoxelsRecursive(debug, new ArrayList<>(), voxel);
//        }
        drawVoxelsRecursive(debug, new ArrayList<>(), voxels.get(0));
        debug.build().sendTo(player);

        return itemStack;
    }

    private void drawVoxelsRecursive(DebugMessage.Builder debug, List<Voxel> visited, Voxel voxel) {
        if (visited.contains(voxel)) {
            return;
        }
        visited.add(voxel);

        // Draw the box
        debug.set(NamespaceID.from(voxel.x1() + "_" + voxel.y1() + "_" + voxel.z1() + "_box"),
                new OutlineBox.Builder()
                        .start(new Vec(voxel.x1(), voxel.y1(), voxel.z1()))
                        .end(new Vec(voxel.x2(), voxel.y2(), voxel.z2()))
                        .layer(Layer.INLINE)
                        .layerLine(Layer.INLINE)
                        .color(0x22FFFFFF)
                        .colorLine(0x88FF0000)
                        .build());
        // Draw the dot
        debug.set(NamespaceID.from(voxel.x1() + "_" + voxel.y1() + "_" + voxel.z1() + "_dot"),
                new OutlineBox.Builder()
                        .start(voxel.center().sub(0.05, -0.05, 0.05))
                        .end(voxel.center().add(0.05, 0.15, 0.05))
                        .layer(Layer.INLINE)
                        .layerLine(Layer.INLINE)
                        .color(0x22FFFFFF)
                        .colorLine(0xFF0000FF)
                        .build());

        // Draw each neighbor
        for (Voxel edge : voxel.edges) {
            debug.set(NamespaceID.from(voxel.x1() + "_" + voxel.y1() + "_" + voxel.z1() + "_line_" + ThreadLocalRandom.current().nextInt(10000)),
                    new Line.Builder()
                            .point(voxel.center())
                            .point(edge.center())
                            .color(0xFF00FF00)
                            .layer(Layer.TOP)
                            .build());
            drawVoxelsRecursive(debug, visited, edge);
        }
    }

    private List<Voxel> voxels = new ArrayList<>();

    private void mergeOnX(Player player) {
        List<Voxel> newVoxels = new ArrayList<>();


//        int a = 0;
//        while (a == 0) {
//            a = 1;
        while (!voxels.isEmpty()) {

            Voxel start = voxels.get(0);
            voxels.remove(0);

            // Can determine if two boxes form a convex shape by checking if the area of them joined is exactly the area of them added individually
            // i think

            boolean found = true;
            while (found) {
                found = false;
                var iter = voxels.iterator();
                while (iter.hasNext()) {
                    var voxel = iter.next();
                    if (voxel.contains(start.x2(), start.y1(), start.z1()) || voxel.contains(start.x1() - 1, start.y1(), start.z1())) {
                        var newVoxel = Voxel.merge(start, voxel);
                        for (var edge : newVoxel.edges) {
                            edge.edges.remove(start);
                            edge.edges.remove(voxel);
                            edge.edges.add(newVoxel);
                        }
                        start.edges.clear();
                        voxel.edges.clear();
                        start = newVoxel;
                        iter.remove();
                        found = true;
                    }
                }
            }

            newVoxels.add(start);
        }

        voxels = newVoxels;
        System.out.println(voxels.get(0).edges.size());
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
                    if (voxel.contains(start.x1(), start.y1(), start.z2()) || voxel.contains(start.x1(), start.y1(), start.z1() - 1)) {
                        Voxel newVoxel = Voxel.mergeSafe(start, voxel);
                        if (newVoxel != null) {
                            for (var edge : newVoxel.edges) {
                                edge.edges.remove(start);
                                edge.edges.remove(voxel);
                                edge.edges.add(newVoxel);
                            }
                            start.edges.clear();
                            voxel.edges.clear();
                            start = newVoxel;
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


    private Map<Point, Voxel> pointMap = new HashMap<>();
    private void createMeshRecursive(Point from, Instance instance, Point point, Voxel fromVoxel) {
        if (visited.contains(point) || !from.sameChunk(point) || !instance.getBlock(point).isAir())
            return;

        visited.add(point);
        Voxel voxel = new Voxel(point, point.add(1, 1, 1));
        voxels.add(voxel);
        pointMap.put(point, voxel);

        createMeshRecursive(point, instance, point.add(1, 0, 0), voxel);
        createMeshRecursive(point, instance, point.add(-1, 0, 0), voxel);
        createMeshRecursive(point, instance, point.add(0, 0, 1), voxel);
        createMeshRecursive(point, instance, point.add(0, 0, -1), voxel);
    }
}
