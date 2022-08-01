package unnamed.mmo.blocks.handlers;

import net.minestom.server.item.Material;

// createAnotherBlock boolean is a flag set true for pumpkins and melons, means this seed creates another block when fully grown
public record CropBlockData(Material seedMaterial, Material cropBlockMaterial, int maximumAge, boolean createAnotherBlock) {}
