package net.hollowcube.blocks.schem;

import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.collections.ImmutableByteArray;
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTInt;
import org.jglrxavpok.hephaistos.nbt.NBTReader;

import java.nio.file.Path;

public class SchematicReader {

    public static Schematic read(@NotNull Path path) throws Exception {
        var reader = new NBTReader(path, CompressedProcesser.GZIP);

        NBTCompound tag = (NBTCompound) reader.read();

        short width = tag.getShort("Width");
        short height = tag.getShort("Height");
        short length = tag.getShort("Length");

        NBTCompound metadata = tag.getCompound("Metadata");

        int offsetX = metadata.getInt("WEOffsetX");
        int offsetY = metadata.getInt("WEOffsetY");
        int offsetZ = metadata.getInt("WEOffsetZ");

        NBTCompound palette = tag.getCompound("Palette");
        ImmutableByteArray blockArray = tag.getByteArray("BlockData");

        int paletteSize = tag.getInt("PaletteMax");

        Block[] paletteBlocks = new Block[paletteSize];

        ArgumentBlockState state = new ArgumentBlockState("");

        palette.forEach((key, value) -> {
            int assigned = ((NBTInt) value).getValue();
            Block block = state.parse(key);
            paletteBlocks[assigned] = block;
        });

        return new Schematic(
                new Vec(width, height, length),
                new Vec(offsetX, offsetY, offsetZ),
                paletteBlocks,
                blockArray.copyArray()
        );
    }

}
