package net.hollowcube.entity;

import net.hollowcube.entity.motion.MotionNavigator;
import net.hollowcube.entity.navigator.Navigator;
import net.hollowcube.entity.task.Task;
import net.hollowcube.mql.MqlScript;
import net.hollowcube.mql.foreign.MqlForeignFunctions;
import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.runtime.MqlScopeImpl;
import net.hollowcube.mql.runtime.MqlScriptScope;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmartEntity extends LivingEntity {

    // Scripting
    private final MqlScope queryScope = MqlForeignFunctions.create(SmartEntity.class, this);
    private final MqlScope.Mutable actorScope = new MqlScopeImpl.Mutable();

    // Brain
    private final Navigator navigator;
    private final Task rootTask;

    // Runtime state
    private Entity target = null;

    public SmartEntity(@NotNull EntityType entityType) {
        super(net.minestom.server.entity.EntityType.fromNamespaceId(entityType.model()));

        this.navigator = new MotionNavigator(this);
        this.rootTask = entityType.behavior().create();
    }

    public @NotNull Navigator navigator() {
        return navigator;
    }

    public @Nullable Entity getTarget() {
        return target;
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    @Override
    public void update(long time) {
        super.update(time);

        // Do not tick until it is in an instance.
        if (!isActive()) return;

        navigator.tick(time);
        switch (rootTask.getState()) {
            case INIT, COMPLETE -> rootTask.start(this);
            case RUNNING -> rootTask.tick(this, time);
            case FAILED -> {
                //todo this probably isnt the best way to handle this
                remove();
                throw new RuntimeException("Entity root task failed: " + this);
            }
        }
    }

    public void attack(@NotNull Entity target) {
        swingMainHand();
        EntityAttackEvent attackEvent = new EntityAttackEvent(this, target);
        EventDispatcher.call(attackEvent);
    }

    // Scripting

    public double evalScript(@NotNull MqlScript script) {
        return script.evaluate(new MqlScriptScope(queryScope, actorScope, MqlScope.EMPTY));
    }

    public boolean evalScriptBool(@NotNull MqlScript script) {
        return script.evaluateToBool(new MqlScriptScope(queryScope, actorScope, MqlScope.EMPTY));
    }
}
