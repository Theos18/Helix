package com.helix.id;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeIDGenerator {

    private static final long CUSTOM_EPOCH = 1420070400000L; // Jan 1, 2015 UTC

    private static final int MACHINE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 13;
    private static final int TIMESTAMP_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long MachineID;
    // state holds the combined (lastTimestamp << SEQUENCE_BITS) | sequence
    private final AtomicLong STATE; 

    public SnowflakeIDGenerator() throws UnknownHostException {
        MachineIdResolver mid = new MachineIdResolver();
        this.MachineID = mid.GetMachineID() << SEQUENCE_BITS;
        this.STATE = new AtomicLong(0);
    }

    public long UUID() {
        long OldState;
        long NewState;
        long CurrentTimeStamp;

        do {
            OldState = STATE.get();
            //Unpack the Old State - TimeSTamp and Sequence
            long OldTimeStamp = OldState >> TIMESTAMP_SHIFT;
            long CurrentSequence = OldState & MAX_SEQUENCE;
            
            CurrentTimeStamp = System.currentTimeMillis() - CUSTOM_EPOCH;

            if (CurrentTimeStamp < OldTimeStamp) {
                throw new RuntimeException(String.format(
                    "Clock moved backward! Refusing to generate ID for %dms", 
                    OldTimeStamp - CurrentTimeStamp
                ));
            } 
            
            if (CurrentTimeStamp == OldTimeStamp) {                
                if (CurrentSequence >= MAX_SEQUENCE) {
                    CurrentTimeStamp = WaitTillNextMS(OldTimeStamp);
                    CurrentSequence = 0;
                } else {
                    CurrentSequence++;
                }
            } else {
                CurrentSequence = 0;
            }

            NewState = (CurrentTimeStamp << TIMESTAMP_SHIFT) | MachineID | CurrentSequence;
        } while (!STATE.compareAndSet(OldState, NewState));
        
        return NewState; 
    }

    private long WaitTillNextMS(long lastTimestamp) {
        long timestamp = System.currentTimeMillis() - CUSTOM_EPOCH;
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis() - CUSTOM_EPOCH;
        }
        return timestamp;
    }
}