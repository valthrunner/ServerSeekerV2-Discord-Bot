package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Mod;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Random {
    public static void random(SlashCommandInteractionEvent event) {
        try (Connection conn = Database.getConnection(event.getMessageChannel())) {
            Statement statement = conn.createStatement();
            long startTime = System.currentTimeMillis() / 1000;
            String query = "SELECT * FROM servers LEFT JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port LEFT JOIN mods ON servers.address = mods.address AND servers.port = mods.port ORDER BY RANDOM() LIMIT 1";
            Main.logger.debug("Query took {}ms", System.currentTimeMillis() / 1000L - startTime);

            ResultSet results = statement.executeQuery(query);
            Server.Builder server = new Server.Builder();
            List<Player> players = new ArrayList<>();
            List<Mod> mods = new ArrayList<>();

            while (results.next()) {
                server.setAddress(results.getString("address"));
                server.setPort(results.getShort("port"));
                server.setMotd(results.getString("motd"));
                server.setVersion(results.getString("version"));
                server.setFirstSeen(results.getLong("firstseen"));
                server.setLastSeen(results.getLong("lastseen"));
                server.setProtocol(results.getInt("protocol"));
                server.setCountry(results.getString("country"));
                server.setAsn(results.getString("asn"));
                server.setReverseDns(results.getString("reversedns"));
                server.setOrganization(results.getString("organization"));
                server.setWhitelist(results.getBoolean("whitelist"));
                server.setEnforceSecure(results.getBoolean("enforceSecure"));
                server.setCracked(results.getBoolean("cracked"));
                server.setPreventsReports(results.getBoolean("preventsReports"));
                server.setMaxPlayers(results.getInt("maxPlayers"));
                server.setTimesSeen(results.getInt("timesSeen"));
                server.setFmlNetworkVersion(results.getInt("fmlnetworkversion"));

                if (results.getString("playername") != null) players.add(new Player(results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
                if (results.getString("modid") != null) mods.add(new Mod(results.getString("modid"), results.getString("modmarker")));
            }

            server.setPlayers(players);
            server.setMods(mods);

            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server.build());
            MessageEmbed embed = embedBuilder.build(false);

            if (embed != null) event.getHook().sendMessageEmbeds(embed).queue();
        } catch (SQLException e) {
            Main.logger.warn("SQL Exception while running the random command!", e);
        }
    }
}
