package com.p2p.discovery.controller;

import com.p2p.discovery.model.PeerInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/peers")
@CrossOrigin(origins = "*") // Hỗ trợ nếu sau này test qua Postman hoặc Web Browser
public class DiscoveryController {

    // Danh bạ lưu các Peer đang online trên bộ nhớ RAM
    private final Map<String, PeerInfo> peerRegistry = new ConcurrentHashMap<>();

    // 1. Đăng ký & Gửi Heartbeat định kỳ (Mỗi 10 - 15 giây)
    @PostMapping("/register")
    public ResponseEntity<?> registerPeer(@RequestBody PeerInfo peer) {
        if (peer.getUsername() == null || peer.getIp() == null) {
            return ResponseEntity.badRequest().body("Username và IP không được rỗng!");
        }
        peer.setLastSeen(Instant.now());
        peerRegistry.put(peer.getUsername(), peer);
        System.out.printf("[+] Peer Heartbeat: %s (%s:%d)%n", peer.getUsername(), peer.getIp(), peer.getP2pPort());
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "updated the discovery: "));
    }

    // 2. Lấy danh sách toàn bộ các Peer đang online (Đã loại bỏ node offline)
    @GetMapping
    public ResponseEntity<Collection<PeerInfo>> getOnlinePeers() {
        // Tự động dọn dẹp các Peer không gửi Heartbeat quá 45 giây
        Instant timeoutThreshold = Instant.now().minusSeconds(45);
        peerRegistry.entrySet().removeIf(entry -> entry.getValue().getLastSeen().isBefore(timeoutThreshold));
        return ResponseEntity.ok(peerRegistry.values());
    }

    // 3. Tra cứu địa chỉ trực tiếp của 1 peer (Signaling Phase)
    @GetMapping("/{username}")
    public ResponseEntity<?> lookupPeer(@PathVariable String username) {
        PeerInfo peer = peerRegistry.get(username);
        if (peer != null) {
            return ResponseEntity.ok(peer);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Tìm kiếm file trên toàn mạng (Mô phỏng cơ chế Napster)
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchFiles(@RequestParam String filename) {
        List<Map<String, Object>> searchResults = new ArrayList<>();
        String query = filename.toLowerCase();

        for (PeerInfo peer : peerRegistry.values()) {
            if (peer.getSharedFiles() != null) {
                for (String file : peer.getSharedFiles()) {
                    if (file.toLowerCase().contains(query)) {
                        searchResults.add(Map.of(
                                "filename", file,
                                "owner", peer.getUsername(),
                                "ip", peer.getIp(),
                                "port", peer.getP2pPort()
                        ));
                    }
                }
            }
        }
        return ResponseEntity.ok(searchResults);
    }
}