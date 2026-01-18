package vn.nhu2410.minecraftmpris.metadata;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;

public class MetadataTransformer {
    public static NativeImageBackedTexture albumArtTexture = null;
    public static Identifier albumArtId = null;

    public static void loadAlbumArt(MinecraftClient mc, String url) {
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
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    stream = new ByteArrayInputStream(imageBytes);
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

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                byte[] pngBytes = baos.toByteArray();
                baos.close();
                bufferedImage.flush();

                nativeImage = NativeImage.read(new ByteArrayInputStream(pngBytes));
                NativeImage finalImage = nativeImage;

                mc.execute(() -> {
                    try {
                        if (albumArtTexture != null) {
                            albumArtTexture.close();
                        }
                        if (albumArtId != null) {
                            mc.getTextureManager().destroyTexture(albumArtId);
                        }
                        albumArtId = new Identifier(MinecraftMprisClient.MOD_ID, "album_art");
                        albumArtTexture = new NativeImageBackedTexture(finalImage);
                        mc.getTextureManager().registerTexture(albumArtId, albumArtTexture);
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

    public static void clearAlbumArt(MinecraftClient mc) {
        mc.execute(() -> {
            if (albumArtTexture != null) {
                albumArtTexture.close();
                albumArtTexture = null;
            }
            if (albumArtId != null) {
                mc.getTextureManager().destroyTexture(albumArtId);
                albumArtId = null;
            }
        });
    }
}
