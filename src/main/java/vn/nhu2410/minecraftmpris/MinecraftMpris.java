package vn.nhu2410.minecraftmpris;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MinecraftMpris implements ClientModInitializer {
    public static final String MOD_ID = "minecraftmpris";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping playPauseKey;
    private static KeyMapping nextTrackKey;
    private static KeyMapping prevTrackKey;
    private static KeyMapping showInfoKey;

    private static final KeyMapping.Category MPRIS_CATEGORY =
        new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(MOD_ID, "controls")
        );

    @Override
    public void onInitializeClient() {
        // Register key bindings
        playPauseKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.playpause",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            MPRIS_CATEGORY
        ));

        nextTrackKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.next",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            MPRIS_CATEGORY
        ));

        prevTrackKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.previous",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_BRACKET,
            MPRIS_CATEGORY
        ));

        showInfoKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.info",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            MPRIS_CATEGORY
        ));

        // Register tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (playPauseKey.consumeClick()) {
                executeCommand("PlayPause");
                client.player.displayClientMessage(Component.literal("§6Music: §fPlay/Pause"), true);
            }

            while (nextTrackKey.consumeClick()) {
                executeCommand("Next");
                client.player.displayClientMessage(Component.literal("§6Music: §fNext Track"), true);
            }

            while (prevTrackKey.consumeClick()) {
                executeCommand("Previous");
                client.player.displayClientMessage(Component.literal("§6Music: §fPrevious Track"), true);
            }

            while (showInfoKey.consumeClick()) {
                showCurrentTrack(client);
            }
        });
    }

    private void executeCommand(String command) {
        new Thread(() -> {
            try {
                String[] cmd = {
                    "bash", "-c",
                    "playerctl " + command.toLowerCase() + " 2>/dev/null || " +
                    "dbus-send --print-reply --dest=org.mpris.MediaPlayer2.* " +
                    "/org/mpris/MediaPlayer2 org.mpris.MediaPlayer2.Player." + command
                };
                
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            } catch (Exception e) {
                LOGGER.error("Failed to execute MPRIS command: " + command, e);
            }
        }).start();
    }

    private void showCurrentTrack(Minecraft client) {
        new Thread(() -> {
            try {
                String[] cmd = {
                    "bash", "-c",
                    "playerctl metadata --format '{{artist}} - {{title}}' 2>/dev/null || " +
                    "echo 'No player found'"
                };
                
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
                );
                
                String trackInfo = reader.readLine();
                if (trackInfo != null && !trackInfo.isEmpty()) {
                    client.execute(() -> {
                        if (client.player != null) {
                            client.player.displayClientMessage(
                                Component.literal("§6Now Playing: §f" + trackInfo),
                                false
                            );
                        }
                    });
                }
                
                process.waitFor();
            } catch (Exception e) {
                LOGGER.error("Failed to get track info", e);
            }
        }).start();
    }
}