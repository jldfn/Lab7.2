/**
 * Created by Денис on 25.05.2017.
 */
public class TimeoutThread extends Thread{
    Thread monitoringThread;
    volatile long sleepTime;
    TimeoutThread(Thread toStop,long sleepTime){
        monitoringThread=toStop;
        this.sleepTime=sleepTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (sleepTime < 0) {
                    monitoringThread.join();
                    break;
                } else {
                   Thread.sleep(sleepTime); sleepTime=-1; monitoringThread.interrupt();
                }
            } catch (InterruptedException e) {
            }

        }
    }
}
