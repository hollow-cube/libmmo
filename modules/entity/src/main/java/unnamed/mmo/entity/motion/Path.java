package unnamed.mmo.entity.motion;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Path(@NotNull List<Point> nodes) {
}
