package ru.center2m.services;

import org.apache.commons.collections.CollectionUtils;
import org.bytedeco.javacv.FrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProcessImage implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static ProcessImage instance;
    private Grabber grabber;

    public static synchronized ProcessImage getInstance(Grabber grabber) {
        if (instance == null) {
            instance = new ProcessImage(grabber);
        }
        return instance;
    }

    private ProcessImage(Grabber grabber) {
        this.grabber = grabber;
    }

    @Override
    public void run() {
        long timer = System.currentTimeMillis();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd'T'HH_mm_ss_SSS");
        while (true) {
            if (System.currentTimeMillis() - timer > 1000L) {
                try {
                    List<BufferedImage> imageList = grabber.read();
                    if (CollectionUtils.isNotEmpty(imageList)) {
                        imageList.forEach(image -> {
                            String name = formatter.format(new Date());
                            String filepath = "/data/temp/" + name;
                            try {
                                ImageIO.write(image, "jpg", new File(filepath));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        LOG.warn("frameList is NULL");
                    }
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }
                timer = System.currentTimeMillis();
            }
        }
    }
}
