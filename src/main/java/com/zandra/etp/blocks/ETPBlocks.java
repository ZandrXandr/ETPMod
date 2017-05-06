package com.zandra.etp.blocks;

import com.zandra.etp.blocks.tileentitites.ETPTileEntity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ETPBlocks {
    public static Block ETPBlock;

    public static void CreateBlocks() {
    	RegisterBlock(ETPBlock = new ETPBlock("etpblock"));
    }
    
    private static Block RegisterBlock(Block block) {
    	GameRegistry.register(block);
    	GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    	GameRegistry.registerTileEntity(ETPTileEntity.class, "etp_tile_entity");
    	return block;
    }
}
