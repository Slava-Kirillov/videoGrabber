package ru.center2m;

import org.bytedeco.javacv.FrameGrabber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.center2m.services.Grabber;
import ru.center2m.services.ProcessImage;
import ru.center2m.services.Reader;

@SpringBootApplication
public class App {
    public static void main(String[] args) throws FrameGrabber.Exception {
        SpringApplication.run(App.class, args);

        String addressString = "rtsp://admin:SS0000000000@212.32.206.186:5543/ISAPI/Streaming/channels/101";
        Grabber grabber = new Grabber(addressString);

        Thread thread1 = new Thread(Reader.getInstance(grabber));
        Thread thread2 = new Thread(ProcessImage.getInstance(grabber));

        thread1.start();
        thread2.start();
    }
}