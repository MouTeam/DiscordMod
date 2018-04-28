package fr.mouteam.discordmod.event;

import fr.mouteam.discordmod.DiscordMod;
import fr.mouteam.discordmod.config.DiscordModConfig;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.TextChannel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;

public class ForgeEventHandler {


    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (event.getMessage() != null) {
            TextChannel channel = DiscordMod.discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);

            List<Emote> emotes = DiscordMod.discord.getJda().getEmotesByName(event.getPlayer().getName(), true);

            String message = "[Minecraft] ";
            if (!emotes.isEmpty()) {
                message += emotes.get(0).getAsMention() + " ";
            }

            message += event.getPlayer().getName() + " " + event.getMessage();

            channel.sendMessage(message).queue();
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase player = event.getEntityLiving();

        if (!(player instanceof EntityPlayer)) {
            return;
        }

        TextChannel channel = DiscordMod.discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);
        channel.sendMessage("[Minecraft] " + player.getCombatTracker().getDeathMessage().getUnformattedText()).queue();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        TextChannel channel = DiscordMod.discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);

        channel.sendMessage("[Minecraft] " + event.player.getName() + " join the game").queue();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {



        TextChannel channel = DiscordMod.discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);

        channel.sendMessage("[Minecraft] " + event.player.getName() + " left the game").queue();

    }

    @SubscribeEvent
    public void onPlayerLoggedOut(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {

        TextChannel channel = DiscordMod.discord.getJda().getTextChannelById(DiscordModConfig.global.discordTokenChannel);

        channel.sendMessage("[SERVER] server disconnection from client event" + event.getManager().getRemoteAddress()).queue();

    }
}
