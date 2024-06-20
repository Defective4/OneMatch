package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.CaptchaDisplay;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class CaptchaDialog extends JDialog {

    private boolean canContinue;
    private Clip audioPlayer;
    private CaptchaDisplay captchaDisplay;
    private byte[] cachedAudio;

    /**
     * Create the dialog.
     */
    private CaptchaDialog(Window parent, Application app) {
        super(parent);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                if (audioPlayer != null) audioPlayer.close();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (audioPlayer != null) audioPlayer.close();
            }
        });
        setTitle("OneMatch - Captcha");
        setModal(true);
        setResizable(false);
        setBounds(100, 100, 310, 185);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        captchaDisplay = new CaptchaDisplay((BufferedImage) null);
        captchaDisplay.setBounds(49, 17, 200, 50);
        contentPanel.add(captchaDisplay);

        JPanel panel = new JPanel();
        panel.setBounds(59, 79, 190, 21);
        contentPanel.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(new JLabel("Answer   "));

        JTextField answerField = new JTextField();
        panel.add(answerField);
        answerField.setColumns(6);

        panel.add(new JLabel("   "));

        JButton refreshButton = new JButton("");
        refreshButton.setToolTipText("Refresh");
        refreshButton.setIcon(new ImageIcon(CaptchaDialog.class.getResource("/icons/refresh.png")));
        if (app != null)
            refreshButton.addActionListener(e -> AsyncProgressDialog.run(this, "Refreshing captcha...", dial -> {
                try {
                    WebResponse resp = app.getWebClient().refreshCaptcha();
                    dial.dispose();
                    if (resp.getCode() == 200) {
                        ByteArrayInputStream buffer = new ByteArrayInputStream(resp.getResponse());
                        BufferedImage img = ImageIO.read(buffer);
                        captchaDisplay.setCaptcha(img);
                        cachedAudio = null;
                        if (audioPlayer != null) audioPlayer.close();
                        answerField.setText("");
                    } else {
                        ErrorDialog.show(this, resp.getResponseString(), "Couldn't refresh captcha");
                    }
                } catch (IOException e1) {
                    dial.dispose();
                    e1.printStackTrace();
                    ExceptionDialog.show(this, e1, "Couldn't refresh captcha");
                }
            }));
        panel.add(refreshButton);

        panel.add(new JLabel(" "));

        JButton audioButton = new JButton("");
        audioButton.setToolTipText("Play");
        audioButton.setIcon(new ImageIcon(CaptchaDialog.class.getResource("/icons/audio.png")));
        if (app != null)
            audioButton.addActionListener(e -> AsyncProgressDialog.run(this, "Downloading audio sample...", dial -> {
                try {
                    audioPlayer = AudioSystem.getClip();
                    audioPlayer.addLineListener(new LineListener() {

                        @Override
                        public void update(LineEvent event) {
                            if (event.getType() == LineEvent.Type.STOP) {
                                audioPlayer.close();
                                audioButton.setEnabled(true);
                            }
                        }
                    });
                } catch (Exception e2) {
                    dial.dispose();
                    e2.printStackTrace();
                    ExceptionDialog.show(this, e2, "Couldn't prepare audio player");
                    return;
                }

                byte[] data;
                audioButton.setEnabled(false);
                if (cachedAudio != null) data = cachedAudio;
                else try {
                    WebResponse response = app.getWebClient().getAudioCaptcha();
                    dial.dispose();
                    if (response.getCode() != 200) {
                        ErrorDialog.show(this, response.getResponseString(), "Couldn't download audio sample!");
                        audioPlayer.close();
                        audioButton.setEnabled(true);
                        return;
                    }
                    data = response.getResponse();
                } catch (Exception e3) {
                    dial.dispose();
                    e3.printStackTrace();
                    ExceptionDialog.show(this, e3, "Couldn't download audio sample");
                    audioPlayer.close();
                    audioButton.setEnabled(true);
                    return;
                }

                try (AudioInputStream in = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data))) {
                    audioPlayer.open(in);
                    audioPlayer.start();
                    cachedAudio = data;
                } catch (Exception e3) {
                    e3.printStackTrace();
                    ExceptionDialog.show(this, e3, "Couldn't play audio sample");
                    audioPlayer.close();
                    audioButton.setEnabled(true);
                }

            }));
        panel.add(audioButton);

        JPanel buttonPane = new JPanel();
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPane.add(cancelButton);

        JButton okButton = new JButton("Check");
        okButton.setEnabled(false);
        buttonPane.add(okButton);
    }

    public static boolean verifyCaptcha(Window parent, JDialog dialC) {
        CaptchaDialog dialog = new CaptchaDialog(parent, Application.getInstance());
        WebResponse resp;
        try {
            resp = Application.getInstance().getWebClient().getCaptchaStatus();
            if (dialC != null) dialC.dispose();
        } catch (Exception e) {
            if (dialC != null) dialC.dispose();
            e.printStackTrace();
            ExceptionDialog.show(parent, e, "Couldn't check captcha status");
            return false;
        }

        int code = resp.getCode();
        if (code == 204) return true;
        if (code != 200) {
            ErrorDialog.show(parent, resp.getResponseString(), "Couldn't check captcha status");
            return false;
        }
        try {
            ByteArrayInputStream buffer = new ByteArrayInputStream(resp.getResponse());
            BufferedImage img = ImageIO.read(buffer);
            dialog.captchaDisplay.setCaptcha(img);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionDialog.show(parent, e, "Couldn't read captcha image");
            return false;
        }

        SwingUtils.showAndCenter(dialog);
        return false;
    }
}