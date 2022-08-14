package unnamed.mmo.loot.context;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;

public interface ContextKeys {

    // Required keys

    /** The source of the loot table generation, eg "mining", "foraging". */
    ContextKey<String> SOURCE_NAME = new ContextKey<>("source_name", String.class);

    /** The target of the loot generation, if it is an entity. */
    ContextKey<Entity> THIS_ENTITY = new ContextKey<>("this", Entity.class);


    // Common hints

    /** The location of the loot generation. */
    ContextKey<Point> POSITION = new ContextKey<>("position", Point.class);
    /** The direction the loot should be generated in. */
    ContextKey<Vec> DIRECTION = new ContextKey<>("direction", Vec.class);

}
