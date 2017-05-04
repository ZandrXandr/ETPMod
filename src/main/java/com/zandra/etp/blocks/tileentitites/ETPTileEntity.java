package com.zandra.etp.blocks.tileentitites;

import com.zandra.etp.ETPMod;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.TileFluidHandler;

public class ETPTileEntity extends TileFluidHandler implements IFluidHandler, ITickable, IEnergyReceiver, IFluidTank{
	
	private int containedRF = 0;
	//A max of 64 blocks, 50 rf a piece.
	private static int maxRF = 64*50;
	
	public int tickPoint = 20;
	
	//Block Position in chunk
	private BlockPos checkBlockPos;
	//Chunk Position
	private int chunkX = 0, chunkZ = 0;
	private int originalChunkX = 0, originalChunkZ = 0;
	
	
	//Stuff for chunk checking
	// (di, dj) is a vector - direction in which we move right now
    int di = 1;
    int dj = 0;
    // length of current segment
    int segment_length = 1;

    // current position (i, j) and how much of current segment we passed
    int i = 0;
    int j = 0;
    int segment_passed = 0;
    
    private Ticket chunkTicket;
	 
	public ETPTileEntity(){
		super();
		tank = new FluidTank(FluidRegistry.LAVA,0,64000);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		if(tickPoint == 9999){
			tickPoint = 20;
			originalChunkX = pos.getX()/16;
			originalChunkZ = pos.getZ()/16;
			
			if(pos.getX() < 0)
				originalChunkX -=1;
			if(pos.getZ() < 0)
				originalChunkZ -=1;
			checkBlockPos = new BlockPos(-1,pos.getY()-1,0);
			di = segment_length = 1;
			dj = j = i = chunkX = chunkZ = segment_passed = 0;
			
			LoadCorrectChunks();
		}
		
		checkForLava();
		
	}
	
	private void LoadCorrectChunks(){
		if(worldObj != null && !worldObj.isRemote){		
			 //Create ticket if needed
	        if(chunkTicket == null){
	        	ForgeChunkManager.setForcedChunkLoadingCallback(ETPMod.instance, this.LoadCallback());
	        	chunkTicket = ForgeChunkManager.requestTicket(ETPMod.instance, worldObj, Type.NORMAL);
	        	
	        	if(chunkTicket == null)
	        		return;
	            chunkTicket.setChunkListDepth(2);
	        }
	        
	        ForgeChunkManager.forceChunk(chunkTicket, new ChunkPos((chunkX + originalChunkX), (chunkZ + originalChunkZ)));
	        ForgeChunkManager.forceChunk(chunkTicket, new ChunkPos(originalChunkX, originalChunkZ));
		}
	}
	
	private LoadingCallback LoadCallback(){
		return null;
	}
	
	private void checkForLava(){
		if(tank.getFluidAmount() >= 64000)
			return;
		
		int i = 0;
		while(containedRF > 50 && i < 10){
			doLavaShiz(true);
			i++;
		}
	}
	
	private void doLavaShiz(boolean removeEnergy){
				
		if(tank.getFluidAmount() >= 64000){
			return;
		}
		
		//Check for lava block
		World w = this.getWorld();
		
		BlockPos newPos = checkBlockPos;
		IBlockState goalState = Blocks.LAVA.getDefaultState();
		IBlockState checkState = w.getBlockState(newPos);
				
		if(checkState != goalState){
			for(int i = 0; i < 16*16*256; i++){
				newPos = getNextBlockPos();
				checkState = w.getBlockState(newPos);
				if(checkState == goalState)
					break;
				i++;
			}
		}
				
		if(checkState == goalState){
			w.setBlockState(newPos, Blocks.COBBLESTONE.getDefaultState(),2);
			if(removeEnergy)
				containedRF -= 50;
			tank.fill(new FluidStack(FluidRegistry.LAVA, 1000), true);
		}
	}
	
	private BlockPos getNextBlockPos(){
		
		BlockPos newPos = checkBlockPos.add(1,0,0);
		if(newPos.getX() >= 16){
			newPos = newPos.add(-16,0,1);
			
			if(newPos.getZ() >= 16){
				newPos = newPos.add(0,-1,-16);
				
				if(newPos.getY() == 0){
					newPos = new BlockPos(0,pos.getY(),0);
					changeChunk();
				}
			}
		}
				
		checkBlockPos = newPos;
		
		return newPos.add((chunkX + originalChunkX) * 16, 0, (chunkZ + originalChunkZ) * 16);
	}
	
	private void changeChunk(){
        // make a step, add 'direction' vector (di, dj) to current position (i, j)
        i += di;
        j += dj;
        ++segment_passed;
        if (segment_passed == segment_length) {
            // done with current segment
            segment_passed = 0;

            // 'rotate' directions
            int buffer = di;
            di = -dj;
            dj = buffer;

            // increase segment length if necessary
            if (dj == 0) {
                ++segment_length;
            }
        }    
        
        chunkX = i;
        chunkZ = j;
        
        
		LoadCorrectChunks();
	}
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        
        //Write RF
        compound.setInteger("containedRF", this.containedRF);
        
        //Current block position
        compound.setInteger("checkPosX", this.checkBlockPos.getX());
        compound.setInteger("checkPosY", this.checkBlockPos.getY());
        compound.setInteger("checkPosZ", this.checkBlockPos.getZ());
        
        //Current chunk position
        compound.setInteger("chunkPosX", this.i);
        compound.setInteger("chunkPosZ", this.j);
        
        //Chunk loop data
        compound.setInteger("segmentLength", this.segment_length);
        compound.setInteger("segmentPased", this.segment_passed);
        
        //Chunk direction
        compound.setInteger("chunkDirX", di);
        compound.setInteger("chunkDirZ", dj);
        
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        //Write RF
        this.containedRF = compound.getInteger("containedRF");
        
        //Current block position
        this.checkBlockPos = new BlockPos(compound.getInteger("checkPosX"),compound.getInteger("checkPosY"),compound.getInteger("checkPosZ"));
        
        //Current chunk position
        this.i = this.chunkX = compound.getInteger("chunkPosX");
        this.j = this.chunkZ = compound.getInteger("chunkPosZ");
        
        //Chunk loop data
        this.segment_length = compound.getInteger("segmentLength");
        this.segment_passed = compound.getInteger("segmentPased");
        
        //Chunk direction
        this.di = compound.getInteger("chunkDirX");
        this.dj = compound.getInteger("chunkDirZ");
        
		LoadCorrectChunks();
    }
	
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}
	
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return true;
	}
	public boolean canDrain(EnumFacing from) {
		return true;
	}
	
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}
	public boolean canFill(EnumFacing from) {
		return false;
	}
	
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
	    if (tank != null) {
	    	doLavaShiz(false);
	    	if(!world.isRemote)
	    		player.addChatComponentMessage(new TextComponentString(tank.getFluidAmount() + "mb of lava buffered, " + containedRF + "RF buffered"));
	    	if(player.getHeldItemMainhand() != null)
	    		if(player.getHeldItemMainhand().getItem() == Items.DIAMOND)
	    			tickPoint = 9999;
	    }
	    return true;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return containedRF;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return maxRF;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		
		int chargeGain = 0;
		int maxChargeGain = maxRF - containedRF;
		chargeGain = Math.min(maxChargeGain, maxReceive);
		
		if(!simulate){
			containedRF += chargeGain;
		}
		return chargeGain;
	}

	@Override
	public FluidStack getFluid() {
		return tank.getFluid();
	}

	@Override
	public int getFluidAmount() {
		return tank.getFluidAmount();
	}

	@Override
	public int getCapacity() {
		return tank.getCapacity();
	}

	@Override
	public FluidTankInfo getInfo() {
		return tank.getInfo();
	}

	
}
