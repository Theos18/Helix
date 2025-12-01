package com.helix.id;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MachineIdResolver{

    private final long MachineId;

    public MachineIdResolver() throws UnknownHostException{
        this.MachineId = ResolveMachineId();
    }

    /**
     * Used in generating UUID (10-bits Machine ID)
     * Reads Machine ID passed in Environment Variable of Docker File
     * If Machine ID is not passed it fallbacks - Hash of HostName
     */
    private long ResolveMachineId() throws UnknownHostException {
        String MachineId = System.getenv("MACHINE_ID");
        if(MachineId.trim().isEmpty()){
            MachineId = InetAddress.getLocalHost().getHostName();
        }
        int hash = Math.abs(MachineId.hashCode());
        long ConvertedMachineId = hash % 1024;
        return ConvertedMachineId;
    }
}