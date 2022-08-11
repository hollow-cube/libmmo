package unnamed.mmo.loot.test;

import unnamed.mmo.loot.context.GenerationContext;

public class GenerationContexts {

    public static GenerationContext fixed(double value) {
        return new GenerationContext() {
            @Override
            public double random() {
                return value;
            }
        };
    }

}
