package net.hollowcube.entity.pathfinding;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record PFNode(
        @NotNull Point point,
        float cost
) {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PFNode pfNode = (PFNode) o;
        return point.equals(pfNode.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }



    private static Point findGround(Block.Getter blockGetter, Point point) {
        Point ground = point.sub(0, 1, 0);
        while (!blockGetter.getBlock(ground, Block.Getter.Condition.TYPE).isSolid()) {
            ground = ground.sub(0, 1, 0);
            if (Math.abs(ground.blockY() - point.blockY()) > 30) return null;
        }
        Point newPoint = ground.add(0, 1, 0);
        return newPoint.withY(newPoint.blockY());
    }

    public static Point gravitySnap(Block.Getter blockGetter, Point orgPoint) {
        var above = blockGetter.getBlock(orgPoint, Block.Getter.Condition.TYPE).isSolid();

        if (above) return orgPoint.add(0, 1, 0);
        return findGround(blockGetter, orgPoint);
    }
}
