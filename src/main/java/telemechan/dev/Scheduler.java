package telemechan.dev;

import telemechan.dev.media.MediaFile;
import telemechan.dev.settings.TimeRange;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    public void startSchedules(){
        scheduler.scheduleAtFixedRate(() -> {
            LocalTime time = LocalTime.now();

            for (TimeRange timeRange : Main.getSettings().getTimedDisplay().keySet()){
                MediaFile file = Main.getSettings().getTimedDisplay().get(timeRange);
                if(timeRange.isWithinRange(time) && Main.getCurrentFile() != file){
                    Main.updateMainFrame(file);
                    return;
                }else if(timeRange.isWithinRange(time) && Main.getCurrentFile() == file){
                    return;
                }
            }

            Main.updateMainFrame(new MediaFile(Main.generatePlaceholder()));
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
                Main.getSession().getBasicRemote().sendText("updaterequest:::placeholder");
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Updated successfully!");
        }, 5, 1, TimeUnit.MINUTES);
    }

    public void stopScheduler(){
        scheduler.shutdownNow();
    }
}
