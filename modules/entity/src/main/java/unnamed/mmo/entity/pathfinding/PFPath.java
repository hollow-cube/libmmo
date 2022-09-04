package unnamed.mmo.entity.pathfinding;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PFPath {
    List<Point> nodes;
    public int index = 0;

    public PFPath(List<Point> nodes) {
        this.nodes = nodes;
    }

    public List<Point> getNodes() {
        return nodes;
    }

    @Nullable
    public Point getCurrent() {
        if (index >= nodes.size()) return null;
        var current = nodes.get(index);
        return current;
    }

    public void next() {
        if (index >= nodes.size()) return;
        index++;
    }

    @Override
    public String toString() {
        return nodes.toString();
    }

}
