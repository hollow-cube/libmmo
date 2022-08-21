package net.minestom.server.test.truth;

import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ConstantConditions")
public class EntitySubject extends Subject {
    private final Entity actual;

    public static EntitySubject assertThat(@Nullable Entity entity) {
        return Truth.assertAbout(entities()).that(entity);
    }

    protected EntitySubject(FailureMetadata metadata, @Nullable Entity actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void isRemoved() {
        if (!actual.isRemoved()) {
            failWithActual(Fact.simpleFact("expected to be removed"));
        }
    }

    public void isNotRemoved() {
        if (actual.isRemoved()) {
            failWithActual(Fact.simpleFact("expected not to be removed"));
        }
    }

    public static Factory<EntitySubject, Entity> entities() {
        return EntitySubject::new;
    }

}
