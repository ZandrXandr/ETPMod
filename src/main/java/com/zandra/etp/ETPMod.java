package com.zandra.etp;

import com.zandra.etp.blocks.ETPBlocks;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import scala.Console;

@Mod(modid = ETPMod.MODID, version = ETPMod.VERSION)
public class ETPMod
{
    public static final String MODID = "ETPMod";
    public static final String VERSION = "1.0";
    
    public static ETPMod instance;
    
    @SidedProxy(clientSide="com.zandra.etp.ClientProxy", serverSide="com.zandra.etp.ServerProxy")
    public static CommonProxy proxy;
    
    public ETPMod(){
    	instance = this;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	Console.print("PreInit");
    	proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	Console.print("Init");
    	proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	Console.print("PostInit");
    	proxy.postInit(event);
    }
}
