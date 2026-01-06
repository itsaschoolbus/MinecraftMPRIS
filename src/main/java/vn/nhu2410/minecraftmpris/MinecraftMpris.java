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

    private static final KeyMapping.Category MPRIS = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "controls"));

    @Override
    public void onInitializeClient() {
        // register overlay
        MusicOverlay.register();

        // register key bindings
        playPauseKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "minecraftmpris.playpause",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            MPRIS
        ));

        nextTrackKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "minecraftmpris.next",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            MPRIS
        ));

        prevTrackKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "minecraftmpris.previous",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_BRACKET,
            MPRIS
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // register key handling
            while (playPauseKey.consumeClick()) {
                executeCommand("play-pause");
                client.player.displayClientMessage(Component.translatable("ingame.minecraftmpris.prefix").append(Component.translatable("ingame.minecraftmpris.playpause")), true);
            }

            while (nextTrackKey.consumeClick()) {
                executeCommand("next");
                client.player.displayClientMessage(Component.translatable("ingame.minecraftmpris.prefix").append(Component.translatable("ingame.minecraftmpris.next")), true);
            }

            while (prevTrackKey.consumeClick()) {
                executeCommand("previous");
                client.player.displayClientMessage(Component.translatable("ingame.minecraftmpris.prefix").append(Component.translatable("ingame.minecraftmpris.previous")), true);
            }

            // update metadata every 20 ticks (1 sec)
            if (client.level != null && client.level.getGameTime() % 20 == 0) {
                updateTrackInfo();
            }
        });
    }

    private void executeCommand(String command) {
        new Thread(() -> {
            try {
                String[] cmd = {
                    "bash", "-c",
                    "playerctl " + command.toLowerCase() + " 2>/dev/null"
                };
                
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            } catch (Exception e) {
                LOGGER.error("Failed to execute MPRIS command: " + command, e);
            }
        }).start();
    }

    private void updateTrackInfo() {
        new Thread(() -> {
            try {
                String metadataFormat = "'{{title}}|{{artist}}|{{position}}|{{mpris:length}}|{{status}}|{{mpris:artUrl}}'";
                String[] cmd = {
                    "bash", "-c",
                    // prefer plasma browser integration if it exists
                    "playerctl metadata --format " + metadataFormat +
                    " -p plasma-browser-integration 2>/dev/null " +
                    "|| playerctl metadata --format " + metadataFormat +
                    " 2>/dev/null " +
                    "|| echo 'Unknown|Unknown|0|0|Paused|'"
                };

                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();

                if (line != null && !line.isEmpty()) {
                    String[] p = line.split("\\|");

                    float posSeconds = Float.parseFloat(p[2]) / 1_000_000f;
                    long lenMicros = Long.parseLong(p[3]);

                    Minecraft.getInstance().execute(() -> {
                        MusicOverlay.title  = p[0];
                        MusicOverlay.artist = p[1];

                        // fallback for when artist is in title (spotify chromium w/o integration)
                        if (MusicOverlay.artist == null || MusicOverlay.artist.isEmpty()) {
                            String t = MusicOverlay.title;
                            if (t.contains(" • ")) {
                                String[] parts = t.split(" • ");
                                MusicOverlay.title = parts[0];
                                MusicOverlay.artist = parts[1];
                            } else {
                                MusicOverlay.artist = "Unknown Artist";
                            }
                        }

                        MusicOverlay.position = (int) posSeconds;
                        MusicOverlay.length = (int) (lenMicros / 1_000_000);
                        MusicOverlay.playing = p[4].equalsIgnoreCase("Playing");

                        if (p.length > 5 && p[5] != null && !p[5].isEmpty()) {
                            MusicOverlay.artUrl = p[5];
                        }
                    });
                }
                process.waitFor();
            } catch (Exception e) {
                LOGGER.error("Failed to read metadata", e);
            }
        }).start();
    }
}