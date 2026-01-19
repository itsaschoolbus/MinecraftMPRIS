package vn.nhu2410.minecraftmpris.metadata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;
import vn.nhu2410.minecraftmpris.config.ConfigHandler;

public class MetadataHandler {
    public static String title;
    public static String artist;
    public static int position;
    public static int length;
    public static boolean playing;
    public static String artUrl;

    private static long lastUpdateTime;
    private static final long UPDATE_INTERVAL_MS = 1000;
    private static volatile boolean isUpdating = false;

    private static void updateMediaInfo() {
        isUpdating = true;
        new Thread(() -> {
            try {
                String baseCommand = "playerctl metadata --format '{{title}}|{{artist}}|{{position}}|{{mpris:length}}|{{status}}|{{mpris:artUrl}}'";
                String fullCommand = (ConfigHandler.HANDLER.instance().usePlasmaBrowserIntegration
                    ? baseCommand + " -p plasma-browser-integration || " : "")
                    + baseCommand + " || echo 'Unknown|Unknown|0|0|Paused|'";
                String[] cmd = {"sh", "-c", fullCommand};

                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();

                if (line != null && !line.isEmpty()) {
                    String[] p = line.split("\\|", -1);

                    if (p.length >= 5) {
                        String newTitle = p[0].isEmpty() ? "Unknown Title" : p[0];
                        String newArtist = p[1].isEmpty() ? "Unknown Artist" : p[1];
                        long posMicros = Long.parseLong(p[2].isEmpty() ? "0" : p[2]);
                        long lenMicros = Long.parseLong(p[3].isEmpty() ? "0" : p[3]);
                        boolean isPlaying = p[4].equalsIgnoreCase("Playing");
                        String newArtUrl = (p.length > 5 && !p[5].isEmpty()) ? p[5] : null;

                        Minecraft.getInstance().execute(() -> {
                            title = newTitle;
                            artist = newArtist;
                            position = (int) (posMicros / 1_000_000);
                            length = (int) (lenMicros / 1_000_000);
                            playing = isPlaying;
                            artUrl = newArtUrl;
                        });
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                MinecraftMprisClient.LOGGER.error("Failed to update metadata", e);
            } finally {
                isUpdating = false;
            }
        }, "MinecraftMPRIS-Metadata").start();
    }

    public static void refreshMediaInfo(Minecraft mc) {
        if (mc == null ||
            mc.player == null ||
            mc.options.hideGui ||
            mc.getDebugOverlay().showDebugScreen() ||
            mc.screen != null) {
            return;
        }

        long currentTime = Util.getMillis();
        if (!isUpdating && currentTime - lastUpdateTime >= UPDATE_INTERVAL_MS) {
            updateMediaInfo();
            lastUpdateTime = currentTime;
        }
    }
}
