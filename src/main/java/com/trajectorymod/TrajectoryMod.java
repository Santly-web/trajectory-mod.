package com.trajectorymod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = TrajectoryMod.MODID, name = TrajectoryMod.NAME, version = TrajectoryMod.VERSION, clientSideOnly = true)
public class TrajectoryMod {
    public static final String MODID = "trajectorymod";
    public static final String NAME = "Trajectory Mod";
    public static final String VERSION = "1.0.0";
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TrajectoryRenderer());
    }
}
