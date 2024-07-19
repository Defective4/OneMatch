package io.github.defective4.onematch.game.ui;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

public final class Icons {

    public static Image APP_ICON = Toolkit.getDefaultToolkit().getImage(Icons.class.getResource("/icon.png"));

    public static ImageIcon AUDIO = new ImageIcon(Icons.class.getResource("/icons/audio.png"));
    public static ImageIcon COPY = new ImageIcon(Icons.class.getResource("/icons/copy.png"));
    public static ImageIcon DISCORD = new ImageIcon(Icons.class.getResource("/icons/discord.png"));
    public static ImageIcon GITHUB = new ImageIcon(Icons.class.getResource("/icons/github.png"));
    public static ImageIcon HEART = new ImageIcon(Icons.class.getResource("/icons/heart_small.png"));
    public static ImageIcon LINK = new ImageIcon(Icons.class.getResource("/icons/link.png"));
    public static ImageIcon REFRESH = new ImageIcon(Icons.class.getResource("/icons/refresh.png"));
    public static ImageIcon STATS = new ImageIcon(Icons.class.getResource("/icons/stats.png"));
    public static ImageIcon USER = new ImageIcon(Icons.class.getResource("/icons/user.png"));

    private Icons() {}
}
