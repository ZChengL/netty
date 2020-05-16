package com.cof.controller;

import com.cof.client.UdpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("v1/netty")
@RestController
public class NettyController {


    @GetMapping("/createClient")
    public String nettyClient() {
        new Thread(() -> {
            UdpClient udpClient = new UdpClient();
            try {
                udpClient.main(new String[]{});
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return "success";
    }
}
