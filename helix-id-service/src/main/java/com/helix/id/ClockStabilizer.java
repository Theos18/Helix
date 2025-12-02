package com.helix.id;

/**Helper function to decide - Given CurrenttimeStamp and LastUsedTimeStamp - Is it safe to use for ID generation or not*/

public class ClockStabilizer{
    
    public long ClockPolicy(long LastUsedTimeStamp, long CurrentTimeStamp){
        LastUsedTimeStamp = this.LastUsedTimeStamp;
        CurrentTimeStamp = this.CurrentTimeStamp;
        if(CurrentTimeStamp < LastUsedTimeStamp){
            CurrentTimeStamp = LastUsedTimeStamp + 1;
        }
        return CurrentTimeStamp;
    }
}