package vn.nhu2410.minecraftmpris.control;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;

public class MediaController {
    private static KeyMapping playPauseKey;
    private static KeyMapping nextTrackKey;
    private static KeyMapping prevTrackKey;

    public static void registerKeyBindings() {
        final KeyMapping.Category MPRIS_BINDS = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(
                MinecraftMprisClient.MOD_ID, "controls"
            )
        );

        playPauseKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.playpause",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            MPRIS_BINDS
        ));

        nextTrackKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.next",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            MPRIS_BINDS
        ));

        prevTrackKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.minecraftmpris.previous",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_BRACKET,
            MPRIS_BINDS
        ));
    }

    private static void executeMediaCommand(String command) {
        new Thread(() -> {
            try {
                String[] cmd = {
                    "bash", "-c",
                    "playerctl " + command
                };

                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            } catch (Exception e) {
                MinecraftMprisClient.LOGGER.error("Failed to execute MPRIS command: " + command, e);
            }
        }).start();
    }

    public static void handleKeyBindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (playPauseKey.consumeClick()) {
                executeMediaCommand("play-pause");
                client.player.displayClientMessage(
                    Component.translatable(
                        "ingamemsg.minecraftmpris.prefix"
                    ).append(
                        Component.translatable(
                            "ingamemsg.minecraftmpris.playpause"
                        )
                    ), true
                );
            }

            while (nextTrackKey.consumeClick()) {
                executeMediaCommand("next");
                client.player.displayClientMessage(
                    Component.translatable(
                        "ingamemsg.minecraftmpris.prefix"
                    ).append(
                        Component.translatable(
                            "ingamemsg.minecraftmpris.next"
                        )
                    ), true
                );
            }

            while (prevTrackKey.consumeClick()) {
                executeMediaCommand("previous");
                client.player.displayClientMessage(
                    Component.translatable(
                        "ingamemsg.minecraftmpris.prefix"
                    ).append(
                        Component.translatable(
                            "ingamemsg.minecraftmpris.previous"
                        )
                    ), true
                );
            }
        });
    }
}
