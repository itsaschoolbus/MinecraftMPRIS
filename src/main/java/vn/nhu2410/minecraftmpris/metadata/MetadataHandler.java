package vn.nhu2410.minecraftmpris.metadata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;

public class MetadataHandler {
    public static String title = "Unknown Title";
    public static String artist = "Unknown Artist";
    public static int position = 0;
    public static int length = 1;
    public static boolean playing = false;
    public static String artUrl = null;

    private static long lastUpdateTime;
    private static final long UPDATE_INTERVAL_MS = 1000;
    private static volatile boolean isUpdating = false;

    private static void updateMediaInfo() {
        isUpdating = true;
        new Thread(() -> {
            try {
                String metadataFormat = "'{{title}}|{{artist}}|{{position}}|{{mpris:length}}|{{status}}|{{mpris:artUrl}}'";
                String[] cmd = {
                    "bash", "-c",
                    // prefer plasma browser integration if it exists
                    "playerctl metadata --format " + metadataFormat + " -p plasma-browser-integration " +
                    "|| playerctl metadata --format " + metadataFormat + " " +
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
                        title  = p[0];
                        artist = p[1];

                        // fallback for when artist is in title (spotify chromium w/o integration)
                        if (artist == null || artist.isEmpty()) {
                            String t = title;
                            if (t.contains(" • ")) {
                                String[] parts = t.split(" • ");
                                title = parts[0];
                                artist = parts[1];
                            } else {
                                artist = "Unknown Artist";
                            }
                        }

                        position = (int) posSeconds;
                        length = (int) (lenMicros / 1_000_000);
                        playing = p[4].equalsIgnoreCase("Playing");

                        if (p.length > 5 && p[5] != null && !p[5].isEmpty()) {
                            artUrl = p[5];
                        }
                    });
                }
                process.waitFor();
            } catch (Exception e) {
                MinecraftMprisClient.LOGGER.error("Failed to update metadata", e);
            } finally {
                isUpdating = false;
            }
        }).start();
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
