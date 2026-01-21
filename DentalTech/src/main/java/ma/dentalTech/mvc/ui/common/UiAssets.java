package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class UiAssets {

    private UiAssets() {}

    public static ImageIcon loadIcon(String resourcePath, int w, int h) {
        if (resourcePath == null || resourcePath.isBlank()) return null;
        URL url = UiAssets.class.getResource(resourcePath);
        if (url == null) return null;
        ImageIcon icon = new ImageIcon(url);
        return scaleIcon(icon, w, h);
    }

    public static ImageIcon loadIconFallback(String resourcePath, String filePath, int w, int h) {
        ImageIcon icon = loadIcon(resourcePath, w, h);
        if (icon != null) return icon;

        if (filePath == null || filePath.isBlank()) return null;
        Path p = Paths.get(filePath);
        if (!Files.exists(p)) return null;
        ImageIcon fileIcon = new ImageIcon(p.toAbsolutePath().toString());
        return scaleIcon(fileIcon, w, h);
    }

    private static ImageIcon scaleIcon(ImageIcon icon, int w, int h) {
        if (icon == null) return null;
        int ow = icon.getIconWidth();
        int oh = icon.getIconHeight();
        if (ow <= 0 || oh <= 0) return icon;
        if (w <= 0 || h <= 0) return icon;

        double scale = Math.min((double) w / ow, (double) h / oh);
        int nw = Math.max(1, (int) Math.round(ow * scale));
        int nh = Math.max(1, (int) Math.round(oh * scale));

        Image img = scaleImage(icon.getImage(), nw, nh);
        return new ImageIcon(img);
    }

    private static Image scaleImage(Image src, int w, int h) {
        if (src == null) return null;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return image;
    }
}
