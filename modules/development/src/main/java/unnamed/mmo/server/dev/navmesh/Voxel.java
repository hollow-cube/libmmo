package unnamed.mmo.server.dev.navmesh;

import com.mattworzala.debug.shape.OutlineBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Voxel {
    public static Voxel merge(Voxel a, Voxel b) {
        Voxel v = new Voxel(
                Math.min(a.x1(), b.x1()),
                Math.min(a.y1(), b.y1()),
                Math.min(a.z1(), b.z1()),
                Math.max(a.x2(), b.x2()),
                Math.max(a.y2(), b.y2()),
                Math.max(a.z2(), b.z2())
        );
        for (var edge : a.edges) {
            if (edge != b) {
                v.edges.add(edge);
            }
        }
        for (var edge : b.edges) {
            if (edge != a) {
                v.edges.add(edge);
            }
        }
        return v;
    }

    public static Voxel mergeSafe(Voxel a, Voxel b) {
        Voxel merged = merge(a, b);
        if (merged.area() == a.area() + b.area())
            return merged;
        return null;
    }

    Collection<Voxel> edges = new HashSet<>();

    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public Voxel(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);

    }

    public Voxel(@NotNull Point start, @NotNull Point end) {
        this(start.blockX(), start.blockY(), start.blockZ(), end.blockX(), end.blockY(), end.blockZ());
    }

    public int x1() {
        return x1;
    }

    public int y1() {
        return y1;
    }

    public int z1() {
        return z1;
    }

    public int x2() {
        return x2;
    }

    public int y2() {
        return y2;
    }

    public int z2() {
        return z2;
    }

    public boolean contains(int x, int y, int z) {
        //todo y is ignored
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }

    public int area() {
        return (x2 - x1) * (y2 - y1) * (z2 - z1);
    }

    public Vec center() {
        return new Vec((x1 + x2) / 2f, (y1 + y2) / 2f, (z1 + z2) / 2f);
    }

    public Vec min() {
        return new Vec(x1, y1, z1);
    }

    @Override
    public String toString() {
        return "Voxel{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", z1=" + z1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", z2=" + z2 +
                '}';
    }
}
