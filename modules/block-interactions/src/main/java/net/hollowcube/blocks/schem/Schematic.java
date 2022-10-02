package net.hollowcube.blocks.schem;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.batch.BatchOption;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Rotation;
import net.minestom.server.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Schematic {
    private final Point size;
    private final Point offset;

    private final Block[] palette;
    private final byte[] blocks;

    public Schematic(Point size, Point offset, Block[] palette, byte[] blocks) {
        this.size = size;
        this.offset = offset;
        this.palette = palette;
        this.blocks = blocks;
    }

    public Point size() {
        return size;
    }

    public Point offset() {
        return offset;
    }

    public @NotNull RelativeBlockBatch build(@NotNull Rotation rawRotation, Function<Block, Block> blockModifier) {
        Rotation rotation = switch (rawRotation) {
            case NONE -> Rotation.NONE;
            case CLOCKWISE -> Rotation.CLOCKWISE;
            case FLIPPED -> Rotation.FLIPPED;
            case COUNTER_CLOCKWISE -> Rotation.COUNTER_CLOCKWISE;
            default -> throw new IllegalArgumentException("Unexpected value: " + rawRotation);
        };

        ByteBuffer blocks = ByteBuffer.wrap(this.blocks);
        RelativeBlockBatch batch = new RelativeBlockBatch(new BatchOption().setCalculateInverse(true));
        for (int y = 0; y < size().y(); y++) {
            for (int z = 0; z < size.z(); z++) {
                for (int x = 0; x < size.x(); x++) {
                    int blockVal = Utils.readVarInt(blocks);
                    Block b = palette[blockVal];

                    if (b == null || b.isAir()) {
                        continue;
                    }

                    Vec pos = new Vec(x + offset.x(), y + offset.y(), z + offset.z());
                    batch.setBlock(rotatePos(pos, rotation), blockModifier.apply(rotateBlock(b, rotation)));
                }
            }
        }

        return batch;
    }

    public void applyManual(Rotation rawRotation, BiConsumer<Point, Block> applicator) {
        Rotation rotation = switch (rawRotation) {
            case NONE -> Rotation.NONE;
            case CLOCKWISE -> Rotation.CLOCKWISE;
            case FLIPPED -> Rotation.FLIPPED;
            case COUNTER_CLOCKWISE -> Rotation.COUNTER_CLOCKWISE;
            default -> throw new IllegalArgumentException("Unexpected value: " + rawRotation);
        };

        ByteBuffer blocks = ByteBuffer.wrap(this.blocks);
        for (int y = 0; y < size().y(); y++) {
            for (int z = 0; z < size.z(); z++) {
                for (int x = 0; x < size.x(); x++) {
                    int blockVal = Utils.readVarInt(blocks);
                    Block b = palette[blockVal];

                    if (b == null || b.isAir()) {
                        continue;
                    }

                    Vec blockPos = new Vec(x + offset.x(), y + offset.y(), z + offset.z());
                    applicator.accept(rotatePos(blockPos, rotation), rotateBlock(b, rotation));
                }
            }
        }
    }

    public void forEachPoint(Point base, Consumer<Point> pointConsumer) {
        ByteBuffer blocks = ByteBuffer.wrap(this.blocks);
        for (int y = 0; y < size().y(); y++) {
            for (int z = 0; z < size.z(); z++) {
                for (int x = 0; x < size.x(); x++) {
                    int blockVal = Utils.readVarInt(blocks);
                    Block b = palette[blockVal];

                    if (b == null || b.isAir()) {
                        continue;
                    }

                    pointConsumer.accept(base.add(x + offset.x(), y + offset.y(), z + offset.z()));
                }
            }
        }
    }

    private @NotNull Point rotatePos(@NotNull Point point, @NotNull Rotation rotation) {
        return switch (rotation) {
            case NONE -> point;
            case CLOCKWISE -> new Vec(-point.z(), point.y(), point.x());
            case FLIPPED -> new Vec(-point.x(), point.y(), -point.z());
            case COUNTER_CLOCKWISE -> new Vec(point.z(), point.y(), -point.x());
            default -> throw new IllegalArgumentException("Unexpected value: " + rotation);
        };
    }

    private @NotNull Block rotateBlock(@NotNull Block block, @NotNull Rotation rotation) {
        if (block.name().contains("stair")) {
            return rotateStair(block, rotation);
        } else {
            return block;
        }
    }

    private static Block rotateStair(Block block, Rotation rotation) {
        return switch (rotation) {
            case NONE -> block;
            case CLOCKWISE -> block.withProperty("facing", rotate90(block.getProperty("facing")));
            case FLIPPED -> block.withProperty("facing", rotate90(rotate90(block.getProperty("facing"))));
            case COUNTER_CLOCKWISE -> block.withProperty("facing", rotate90(rotate90(rotate90(block.getProperty("facing")))));
            default -> throw new IllegalArgumentException("Unexpected value: " + rotation);
        };
    }

    private static String rotate90(String in) {
        return switch (in) {
            case "north" -> "east";
            case "east" -> "south";
            case "south" -> "west";
            default -> "north";
        };
    }
}
