package com.zandra.etp;

import com.zandra.etp.blocks.ETPBlocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {
		ETPBlocks.CreateBlocks();
    }

    public void init(FMLInitializationEvent e) {
    	GameRegistry.addRecipe(new ItemStack(ETPBlocks.ETPBlock), new Object[] {"OPO", "bEB", "OPO", 'O', Blocks.OBSIDIAN, 'P', Items.DIAMOND_PICKAXE, 'b', Items.LAVA_BUCKET, 'B', Items.WATER_BUCKET, 'E', Items.ENDER_EYE});
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
