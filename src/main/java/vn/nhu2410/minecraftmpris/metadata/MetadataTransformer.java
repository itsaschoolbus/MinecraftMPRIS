package vn.nhu2410.minecraftmpris.metadata;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
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
                            mc.getTextureManager().release(albumArtId);
                        }
                        albumArtId = Identifier.fromNamespaceAndPath(MinecraftMprisClient.MOD_ID, "album_art");
                        albumArtTexture = new DynamicTexture(() -> "album_art", finalImage);
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

    public static void clearAlbumArt(Minecraft mc) {
        mc.execute(() -> {
            if (albumArtTexture != null) {
                albumArtTexture.close();
                albumArtTexture = null;
            }
            if (albumArtId != null) {
                mc.getTextureManager().release(albumArtId);
                albumArtId = null;
            }
        });
    }
}
