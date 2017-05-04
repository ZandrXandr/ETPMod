package com.zandra.etp.blocks;

import com.zandra.etp.ETPMod;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ETPBlockRenderRegister {

	public static String modid = ETPMod.MODID;

	public static void registerBlockRenderer() {
	    reg(ETPBlocks.ETPBlock);
	}

	public static void reg(Block block) {
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(modid + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}
	
}
