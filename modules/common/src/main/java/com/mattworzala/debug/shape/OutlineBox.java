package com.mattworzala.debug.shape;

import com.mattworzala.debug.Layer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.binary.BinaryWriter;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

//todo remove me (this isn't present in mainline debug renderer currently)
public record OutlineBox(
        Vec start,
        Vec end,
        int color,
        Layer layer,
        int colorLine,
        Layer layerLine
) implements Shape {
    private static final int ID = 3;

    @Override
    public void write(@NotNull BinaryWriter buffer) {
        buffer.writeVarInt(ID);
        buffer.writeDouble(start.x());
        buffer.writeDouble(start.y());
        buffer.writeDouble(start.z());
        buffer.writeDouble(end.x());
        buffer.writeDouble(end.y());
        buffer.writeDouble(end.z());
        buffer.writeInt(color);
        buffer.writeVarInt(layer.ordinal());
        buffer.writeInt(colorLine);
        buffer.writeVarInt(layerLine.ordinal());
    }

    public static class Builder {
        private Vec start;
        private Vec end;
        private int color = 0xFFFFFFFF;
        private Layer layer = Layer.INLINE;
        private int colorLine = 0xFFFFFFFF;
        private Layer layerLine = Layer.INLINE;

        public Builder start(Vec start) {
            this.start = start;
            return this;
        }

        public Builder end(Vec end) {
            this.end = end;
            return this;
        }

        public Builder block(int x, int y, int z) {
            return block(x, y, z, 0.05);
        }

        public Builder block(int x, int y, int z, double expand) {
            return start(new Vec(x - expand, y - expand, z - expand))
                    .end(new Vec(x + 1 + expand, y + 1 + expand, z + 1 + expand));
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder layer(Layer layer) {
            this.layer = layer;
            return this;
        }

        public Builder colorLine(int color) {
            this.colorLine = color;
            return this;
        }

        public Builder layerLine(Layer layer) {
            this.layerLine = layer;
            return this;
        }

        public OutlineBox build() {
            Check.notNull(start, "start");
            Check.notNull(end, "end");
            return new OutlineBox(start, end, color, layer, colorLine, layerLine);
        }
    }
}
