package telemechan.dev;

import telemechan.dev.media.MediaContainer;
import telemechan.dev.media.MediaHandler;
import telemechan.dev.settings.TimeRange;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    public void startSchedules(){
        AtomicInteger counter = new AtomicInteger(10);

        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime time = LocalDateTime.now();

            List<MediaContainer> filesInTimeRange = new ArrayList<>();

            for (TimeRange timeRange : Main.getSettings().getTimedDisplay().keySet()){
                MediaContainer file = MediaHandler.preloadedMedia.get(Main.getSettings().getTimedDisplay().get(timeRange));
                if(timeRange.isWithinRange(time)){
                    filesInTimeRange.add(file);
                }
            }

            filesInTimeRange.sort(null);

            if (filesInTimeRange.isEmpty()) {
                Main.updateMainFrame(new MediaContainer(Main.generatePlaceholder(), -1));
                return;
            }

            MediaContainer highest = filesInTimeRange.getFirst();

            if(highest.getPriority() > 0) {
                if (highest == Main.getCurrentFile()) {
                    return;
                }

                Main.updateMainFrame(highest);
                counter.set(0);
            }else{
                if (counter.get() > 9){
                    Main.updateMainFrame(filesInTimeRange.get(new Random().nextInt(filesInTimeRange.size())));
                    counter.set(0);
                }else {
                    counter.incrementAndGet();
                }
            }

        }, 0, 1, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Trying to perform an update...");

            if(Main.getSession() == null || !Main.getSession().isOpen()){
                System.out.println("Server connection is null or closed!!! Trying to reconnect...");

                Main.reconnectToServer();

                if(Main.getSession() == null || !Main.getSession().isOpen()){
                    System.out.println("Failed to reconnect to server!!!");

                    return;
                }else{
                    System.out.println("Successfully reconnected to the server!!! Resuming update...");
                }
            }

            try {
                Main.getSession().getBasicRemote().sendText("updaterequest:::" + Main.getSettings().getUuid());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Updated successfully!");
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void stopScheduler(){
        scheduler.shutdownNow();
    }
}
