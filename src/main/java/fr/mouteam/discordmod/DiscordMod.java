package fr.mouteam.discordmod;

import fr.mouteam.discordmod.config.DiscordModConfig;
import fr.mouteam.discordmod.discord.Discord;
import fr.mouteam.discordmod.event.ForgeEventHandler;
import fr.mouteam.discordmod.listener.DiscordChatListener;
import fr.mouteam.discordmod.util.References;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;

@Mod(modid = References.MOD_ID, version = References.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class DiscordMod {
    public static final Logger logger = LogManager.getLogger(References.MOD_ID);

    public static Discord discord;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configurationFile = new File(event.getModConfigurationDirectory(), "mouteam/DiscordMod/" + References.MOD_ID + ".cfg");
        Configuration configuration = new Configuration(configurationFile);

        configuration.load();

        if (configuration.hasChanged()) {
            configuration.save();
        }

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        discord = new Discord(DiscordModConfig.global.discordTokenSecret);
        discord.connect();
        discord.addEventListener(new DiscordChatListener());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStarting(final FMLServerStartedEvent event) {
        logger.info("Started Server...");

        // https://github.com/shadowfacts/DiscordChat/blob/rewrite/1.12.2/src/main/java/net/shadowfacts/discordchat/one_twelve_two/OneTwelveTwoAdapter.java
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        TextChannel channel = discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Title : " + server.getServerHostname());
        embedBuilder.setColor(new Color(0x55acee));
        embedBuilder.setDescription("Server is started");
        embedBuilder.addField("MOTD", server.getMOTD(), false);

        for (int dim : getAllDimensions()) {
            double tickTime = getTickTime(dim);
            double tps = Math.min(1000 / tickTime, 20);
            embedBuilder.addField("Dimension " + dim, String.format("Dimension %d: TPS: %.0f\n", dim, tps), true);
        }

        try {
            channel.sendMessage(embedBuilder.build()).queue();
        } catch (ErrorResponseException e) {
            logger.error(e.getMessage());
        }
    }

    @Mod.EventHandler
    public void serverStopping(final FMLServerStoppingEvent event) {
        logger.info("Stopping Server...");

        TextChannel channel = discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Server Status");
        embedBuilder.setColor(new Color(0xdd2e44));
        embedBuilder.setDescription("Server is stopping");

        try {
            channel.sendMessage(embedBuilder.build()).queue();
        } catch (ErrorResponseException e) {
            logger.error(e.getMessage());
        }
    }

    @Mod.EventHandler
    public void serverStopped(final FMLServerStoppedEvent event){
        discord.stop();
    }

    public double getTickTime(int dimension) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        long[] tickTimes = server.worldTickTimes.get(dimension);
        long sum = 0;
        for (int i = 0; i < tickTimes.length; i++) {
            sum += tickTimes[i];
        }
        return sum / tickTimes.length * 1.0E-6D;
    }

    public int[] getAllDimensions() {
        Integer[] boxed = DimensionManager.getIDs();
        int[] unboxed = new int[boxed.length];
        for (int i = 0; i < boxed.length; i++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }
}
