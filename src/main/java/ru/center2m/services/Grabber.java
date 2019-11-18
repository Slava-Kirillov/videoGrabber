package ru.center2m.services;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Grabber {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final FFmpegFrameGrabber grabber;
    private volatile AtomicInteger read = new AtomicInteger(0);

    static {
        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
    }

    public Grabber(String cameraUrl) throws FrameGrabber.Exception {
        this.grabber = new FFmpegFrameGrabber(cameraUrl);
        this.grabber.setOption("rtsp_transport", "tcp");
        this.grabber.setVideoCodecName("h264");
        grabber.start();
        grabber.flush();
    }

    public List<BufferedImage> read() throws FrameGrabber.Exception {
        read.getAndIncrement();
        return this.getImages();
    }

    public synchronized void grub(Boolean restartGrubber) throws FrameGrabber.Exception, InterruptedException {
        while (read.get() > 0) {
            wait();
        }
        if (restartGrubber) {
            LOG.info("Grubber restart");
            grabber.restart();
        }
        grabber.grabImage();
    }

    private synchronized List<BufferedImage> getImages() throws FrameGrabber.Exception {
        List<BufferedImage> imageList = new ArrayList<>();
        LOG.info("Read images");
        for (int i = 0; i < 10; i++) {
            Java2DFrameConverter paintConverter = new Java2DFrameConverter();
            BufferedImage image = paintConverter.getBufferedImage(grabber.grabImage());
            imageList.add(image);
        }
        read.getAndDecrement();
        notify();
        return imageList;
    }
}
