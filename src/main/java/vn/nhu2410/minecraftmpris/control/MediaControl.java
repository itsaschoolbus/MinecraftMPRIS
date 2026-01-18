package vn.nhu2410.minecraftmpris.control;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;
import vn.nhu2410.minecraftmpris.keybind.KeybindRegistry;

public class MediaControl {
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

    public static void handleKeybinds() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (KeybindRegistry.playPauseKey.consumeClick()) {
                executeMediaCommand("play-pause");
                client.player.displayClientMessage(
                    Component.translatable(
                        "minecraftmpris.ingamemsg.prefix"
                    ).append(
                        Component.translatable(
                            "minecraftmpris.ingamemsg.playpause"
                        )
                    ), true
                );
            }

            while (KeybindRegistry.nextTrackKey.consumeClick()) {
                executeMediaCommand("next");
                client.player.displayClientMessage(
                    Component.translatable(
                        "minecraftmpris.ingamemsg.prefix"
                    ).append(
                        Component.translatable(
                            "minecraftmpris.ingamemsg.next"
                        )
                    ), true
                );
            }

            while (KeybindRegistry.prevTrackKey.consumeClick()) {
                executeMediaCommand("previous");
                client.player.displayClientMessage(
                    Component.translatable(
                        "minecraftmpris.ingamemsg.prefix"
                    ).append(
                        Component.translatable(
                            "minecraftmpris.ingamemsg.previous"
                        )
                    ), true
                );
            }
        });
    }
}
