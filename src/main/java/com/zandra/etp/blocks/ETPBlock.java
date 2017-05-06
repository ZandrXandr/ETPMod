package com.zandra.etp.blocks;

import javax.annotation.Nullable;

import com.zandra.etp.blocks.tileentitites.ETPTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ETPBlock extends Block implements ITileEntityProvider{

    public ETPBlock(String unlocalizedName, Material material, float hardness, float resistance) {
        super(material);
        this.setUnlocalizedName(unlocalizedName);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setRegistryName("etpBlock");
    }

    public ETPBlock(String unlocalizedName, float hardness, float resistance) {
        this(unlocalizedName, Material.ROCK, hardness, resistance);
    }

    public ETPBlock(String unlocalizedName) {
        this(unlocalizedName, 2.0f, 10.0f);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		ETPTileEntity ent = new ETPTileEntity();
		ent.tickPoint = 9999;
		return ent;
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
	
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ETPTileEntity te = (ETPTileEntity)world.getTileEntity(pos);
		te.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
	    return true;
	}

}
