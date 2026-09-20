package com.p2p.discovery.model;

import java.time.Instant;
import java.util.List;

public class PeerInfo {
    private String username;
    private String ip;          // Địa chỉ IP Tailscale (100.x.y.z)
    private int p2pPort;        // Port Socket P2P của client (vd: 9001)
    private List<String> sharedFiles;
    private Instant lastSeen;   // Dùng để kiểm tra Timeout Heartbeat

    public PeerInfo() {}

    public PeerInfo(String username, String ip, int p2pPort, List<String> sharedFiles) {
        this.username = username;
        this.ip = ip;
        this.p2pPort = p2pPort;
        this.sharedFiles = sharedFiles;
        this.lastSeen = Instant.now();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getP2pPort() { return p2pPort; }
    public void setP2pPort(int p2pPort) { this.p2pPort = p2pPort; }

    public List<String> getSharedFiles() { return sharedFiles; }
    public void setSharedFiles(List<String> sharedFiles) { this.sharedFiles = sharedFiles; }

    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }
}