package io.github.defective4.onematch.game.ui.components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class CaptchaDisplay extends JComponent {

    private BufferedImage captcha;

    public CaptchaDisplay(BufferedImage captcha) {
        this.captcha = captcha;
        super.setBounds(0, 0, 200, 50);
    }

    public BufferedImage getCaptcha() {
        return captcha;
    }

    public void setCaptcha(BufferedImage captcha) {
        this.captcha = captcha;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (captcha != null) g.drawImage(captcha, 0, 0, 200, 50, null);
    }

}
