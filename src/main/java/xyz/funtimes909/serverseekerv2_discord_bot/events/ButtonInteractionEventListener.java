package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

public class ButtonInteractionEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "SearchButton1":
                Search.serverSelectedButtonEvent(findField(event, 1), (short) 25565);
                break;
            case "SearchButton2":
                Search.serverSelectedButtonEvent(findField(event, 2), (short) 25565);
                break;
            case "SearchButton3":
                Search.serverSelectedButtonEvent(findField(event, 3), (short) 25565);
                break;
            case "SearchButton4":
                Search.serverSelectedButtonEvent(findField(event, 4), (short) 25565);
                break;
            case "SearchButton5":
                Search.serverSelectedButtonEvent(findField(event, 5), (short) 25565);
                break;
            case "PagePrevious":
                if (Search.page != 1) Search.page -= 1;
                Search.scrollResults(-10, false);
                break;
            case "PageNext":
                if (Search.page != Search.rowCount / 5) Search.page += 1;
                Search.scrollResults(0, false);
                break;
        }
        event.deferEdit().complete();
    }

    private static String findField(ButtonInteractionEvent event, int fieldNumber) {
        String fieldName;
        fieldName = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith(String.valueOf(fieldNumber))).findFirst().get().getName();
        return fieldName.substring(fieldName.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
    }
}
