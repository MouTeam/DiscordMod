package fr.mouteam.discordmod.config;

import fr.mouteam.discordmod.util.References;
import net.minecraftforge.common.config.Config;

@Config(modid = References.MOD_ID)
public class DiscordModConfig {
    @Config.Comment("All configuration related to global config mod")
    public static Global global = new Global();

    public static class Global {
        @Config.Comment("Your discord secret token")
        public String discordTokenSecret = "NDAwMTk5NzI5MjcyODQ4Mzg1.DTZpuA.XNY8l613gt2OsGzrUTElxCEG5to";


        @Config.Comment("Your discord token channel")
        public String discordTokenChannel = "400203087408660482-ANOTHER";


        @Config.Comment("Your discord prefix command")
        public String discordPrefixCommand = "?";
    }
}
