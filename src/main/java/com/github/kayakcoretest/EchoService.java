package com.github.kayakcoretest;

import com.github.kayak.core.Bus;
import com.github.kayak.core.BusURL;
import com.github.kayak.core.Frame;
import com.github.kayak.core.FrameReceiver;
import com.github.kayak.core.Subscription;
import com.github.kayak.core.TimeSource;

/**
 * A simple EchoService that uses the Kayak-core library to respond to
 * incoming CAN frames. The response is a frame with a different ID but
 * the same data.
 * 
 * @author Jan-Niklas Meier < dschanoeh@googlemail.com >
 */
public class EchoService {
    /* Configuration settings */
    private static final String HOST = "192.168.0.21";
    private static final int PORT = 28602;
    private static final String BUS = "vcan0";
    private static final int REQUEST_ID = 0x12;
    private static final int RESPONSE_ID = 0x13;
    
    private static Bus bus = new Bus();
    
    /* 
     * This FrameReceiver gets notified about incoming frames and sends
     * a response.
     */
    private static FrameReceiver receiver = new FrameReceiver() {

        private final Frame response = new Frame(RESPONSE_ID, new byte[] {0x11});
        
        public void newFrame(Frame frame) {
            if(frame.getIdentifier() == REQUEST_ID) {
                response.setData(frame.getData());
                bus.sendFrame(response);
            }
        }
    };
    
    public static void main( String[] args ) throws InterruptedException {
        /* Create a bus and connect all components */
        BusURL url = new BusURL(HOST, PORT, BUS);
        TimeSource ts = new TimeSource();
        bus.setConnection(url);
        bus.setTimeSource(ts);
        
        /* Only receive frames with the REQUEST_ID */
        Subscription s = new Subscription(receiver, bus);
        s.subscribe(REQUEST_ID);
        
        ts.play(); /* Start simulation time and open connections */
        
        while(true) /* Infinite loop */
            Thread.sleep(1000000);
    }
}
