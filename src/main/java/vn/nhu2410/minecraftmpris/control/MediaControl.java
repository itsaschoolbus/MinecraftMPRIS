package vn.nhu2410.minecraftmpris.control;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
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

            while (KeybindRegistry.playPauseKey.wasPressed()) {
                executeMediaCommand("play-pause");
                client.player.sendMessage(
                    Text.translatable(
                        "minecraftmpris.ingamemsg.prefix"
                    ).append(
                        Text.translatable(
                            "minecraftmpris.ingamemsg.playpause"
                        )
                    ), true
                );
            }

            while (KeybindRegistry.nextTrackKey.wasPressed()) {
                executeMediaCommand("next");
                client.player.sendMessage(
                    Text.translatable(
                        "minecraftmpris.ingamemsg.prefix"
                    ).append(
                        Text.translatable(
                            "minecraftmpris.ingamemsg.next"
                        )
                    ), true
                );
            }

            while (KeybindRegistry.prevTrackKey.wasPressed()) {
                executeMediaCommand("previous");
                client.player.sendMessage(
                    Text.translatable(
                        "minecraftmpris.ingamemsg.prefix"
                    ).append(
                        Text.translatable(
                            "minecraftmpris.ingamemsg.previous"
                        )
                    ), true
                );
            }
        });
    }
}
