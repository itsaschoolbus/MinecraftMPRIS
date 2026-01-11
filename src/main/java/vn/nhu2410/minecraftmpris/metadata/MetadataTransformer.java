package vn.nhu2410.minecraftmpris.metadata;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;

public class MetadataTransformer {
    public static DynamicTexture albumArtTexture = null;
    public static Identifier albumArtId = null;

    public static void loadAlbumArt(Minecraft mc, String url) {
        new Thread(() -> {
            NativeImage nativeImage = null;
            try {
                InputStream stream;

                // handle local players giving data:image/<file extension>;base64,...
                if (url.startsWith("data:image/")) {
                    int commaIndex = url.indexOf(',');
                    if (commaIndex == -1) {
                        MinecraftMprisClient.LOGGER.warn("Invalid data URI format: " + url);
                        clearAlbumArt(mc);
                        return;
                    }

                    String base64Data = url.substring(commaIndex + 1);
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                    stream = new java.io.ByteArrayInputStream(imageBytes);
                } else {
                    stream = URI.create(url).toURL().openStream();
                }

                BufferedImage bufferedImage = ImageIO.read(stream);
                stream.close();

                if (bufferedImage == null) {
                    MinecraftMprisClient.LOGGER.warn("ImageIO returned null for URL: " + url);
                    clearAlbumArt(mc);
                    return;
                }

                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                nativeImage = new NativeImage(width, height, true);

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int argb = bufferedImage.getRGB(x, y);
                        nativeImage.setPixel(x, y, argb);
                    }
                }

                NativeImage finalImage = nativeImage;
                mc.execute(() -> {
                    try {
                        if (albumArtTexture != null) {
                            albumArtTexture.close();
                        }
                        albumArtId = Identifier.fromNamespaceAndPath(
                            MinecraftMprisClient.MOD_ID, "albumart"
                        );
                        albumArtTexture = new DynamicTexture(() -> "albumart", finalImage);
                        mc.getTextureManager().register(albumArtId, albumArtTexture);
                    } catch (Exception e) {
                        MinecraftMprisClient.LOGGER.error("Failed to register album art texture", e);
                        if (finalImage != null) {
                            finalImage.close();
                        }
                        clearAlbumArt(mc);
                    }
                });
            } catch (Exception e) {
                MinecraftMprisClient.LOGGER.error("Failed to load album art from URL: " + url, e);
                if (nativeImage != null) {
                    nativeImage.close();
                }
                clearAlbumArt(mc);
            }
        }).start();
    }

    private static void clearAlbumArt(Minecraft mc) {
        mc.execute(() -> {
            if (albumArtTexture != null) {
                albumArtTexture.close();
                albumArtTexture = null;
            }
            albumArtId = null;
        });
    }
}
