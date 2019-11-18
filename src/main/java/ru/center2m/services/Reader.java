package ru.center2m.services;

import org.bytedeco.javacv.FrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reader implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static Reader instance;
    private Grabber grabber;

    public static synchronized Reader getInstance(Grabber grabber) {
        if (instance == null) {
            instance = new Reader(grabber);
        }
        return instance;
    }

    private Reader(Grabber grabber) {
        this.grabber = grabber;
    }

    @Override
    public void run() {
        long timer = System.currentTimeMillis();
        while (true) {
            try {
                boolean restartGrub = System.currentTimeMillis() - timer > 240000L;
                if (restartGrub) {
                    timer = System.currentTimeMillis();
                }
                grabber.grub(restartGrub);
            } catch (FrameGrabber.Exception | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}