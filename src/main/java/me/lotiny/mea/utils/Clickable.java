package me.lotiny.mea.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class Clickable {

    private final List<TextComponent> components = new ArrayList<>();
    private String hoverText;
    private String text;

    /**
     * Create a clickable message with the specified text.
     *
     * @param msg The main text of the clickable message.
     */
    public Clickable(String msg) {
        TextComponent message = new TextComponent(CC.translate(msg));

        this.components.add(message);
        this.text = msg;
    }

    /**
     * Create a clickable message with the specified text, hover text, and click action.
     *
     * @param msg         The main text of the clickable message.
     * @param hoverMsg    The text to display when hovering over the clickable message (optional).
     * @param clickString The command or action to execute when the message is clicked (optional).
     */
    public Clickable(String msg, String hoverMsg, String clickString) {
        this.add(msg, hoverMsg, clickString);
        this.text = msg;
        this.hoverText = hoverMsg;
    }

    /**
     * Add a new text component to the clickable message with optional hover text and click action.
     *
     * @param msg         The main text of the new component.
     * @param hoverMsg    The text to display when hovering over the component (optional).
     * @param clickString The command or action to execute when the component is clicked (optional).
     * @return The added TextComponent.
     */
    public TextComponent add(String msg, String hoverMsg, String clickString) {
        TextComponent message = new TextComponent(CC.translate(msg));

        if (hoverMsg != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
        }

        if (clickString != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }

        this.components.add(message);
        this.text = msg;
        this.hoverText = hoverMsg;

        return message;
    }

    /**
     * Add a new text component to the clickable message.
     *
     * @param message The main text of the new component.
     */
    public void add(String message) {
        this.components.add(new TextComponent(message));
    }

    /**
     * Send the clickable message to a player.
     *
     * @param player The player to send the message to.
     */
    public void sendToPlayer(Player player) {
        player.spigot().sendMessage(this.asComponents());
    }

    /**
     * Send the clickable message to a player only if they have a specific permission.
     * Otherwise, send the plain text message.
     *
     * @param player          The player to send the message to.
     * @param hoverPermission The permission required to see hoverable content.
     */
    public void sendToPlayer(Player player, String hoverPermission) {
        if (!player.hasPermission(hoverPermission)) {
            player.sendMessage(this.text);
        } else {
            player.spigot().sendMessage(this.asComponents());
        }
    }

    /**
     * Convert the clickable message to an array of TextComponents.
     *
     * @return An array of TextComponents representing the clickable message.
     */
    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }
}
