package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.records.ServerEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.HttpUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchEmbedBuilder {
    public static MessageEmbed parse(HashMap<Integer, ServerEmbed> servers, int rowCount, int page) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        int longestAddress = 0;
        int longestVersion = 0;

        for (ServerEmbed entry : servers.values()) {
            if (entry.address().length() > longestAddress) longestAddress = entry.address().length();
            if (entry.version().length() > longestVersion) longestVersion = entry.version().length();
        }

        if (longestVersion > 24) longestVersion = 24;

        int index = 1;
        for (ServerEmbed entry : servers.values()) {
            StringBuilder address = new StringBuilder("``").append(entry.address()).append("``");
            StringBuilder version = new StringBuilder("``").append(entry.version()).append("``");
            String timestamp = "<t:" + entry.timestamp() + ":R>";

            if (entry.country() != null) {
                address.insert(0, ":flag_" + entry.country().toLowerCase() + ": **:** ");
            } else {
                String primaryResponse = HttpUtils.run(entry.address());
                if (primaryResponse != null) {
                    JsonObject parsedPrimaryResponse = JsonParser.parseString(primaryResponse).getAsJsonObject();
                    if (parsedPrimaryResponse.has("countryCode")) address.insert(0, ":flag_" + parsedPrimaryResponse.get("countryCode").getAsString().toLowerCase() + ": **:** ");
                }
            }

            // Make everything the same length
            if (longestVersion == 24 && version.length() >= longestVersion) {
                version.replace(0, version.length(), version.substring(0, longestVersion) + "...``");
            }

            while (version.length() < longestVersion) {
                version.insert(version.length() - 2, " ");
            }

            while (address.length() < longestAddress + 20) {
                address.insert(address.length() - 2, " ");
            }

            MessageEmbed.Field addressField = new MessageEmbed.Field(index + ". " + address + " - " + version + " - " + timestamp, "_ _", false);
            fields.add(addressField);
            index++;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setTitle("Page: " + page + " (Total Results: " + rowCount + ")")
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif");

        for (MessageEmbed.Field field : fields) {
            embed.addField(field);
        }

        return embed.build();
    }
}
