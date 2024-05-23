package com.example.webapp_backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import org.springframework.messaging.simp.stomp.StompFrameHandler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
public class WebSocketIntegrationTests {

    private WebSocketStompClient stompClient;
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void verifyWebSocketMessaging() throws InterruptedException, ExecutionException, TimeoutException {


        // Create WebSocket client
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Connect and subscribe to the topic
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
        String WEBSOCKET_URI = "/ws";
        CompletableFuture<StompSession> completableFuture = stompClient.connectAsync("ws://localhost:" + port + WEBSOCKET_URI, new StompSessionHandlerAdapter() {});
        StompSession stompSession = completableFuture.get(1, TimeUnit.SECONDS);

        String WEBSOCKET_TOPIC = "/topic/messages";
        stompSession.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler(blockingQueue));

        // Send a message
        String testMessage = "Hello, WebSocket!";
        stompSession.send(WEBSOCKET_TOPIC, testMessage.getBytes());

        String messageReceived = blockingQueue.poll(1, TimeUnit.SECONDS);
        assertNotNull(messageReceived);
        assertEquals(testMessage, messageReceived);

        // Disconnect
        stompSession.disconnect();
    }

    static class DefaultStompFrameHandler implements StompFrameHandler {
        private final BlockingQueue<String> blockingQueue;

        public DefaultStompFrameHandler(BlockingQueue<String> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            byte[] bytes = (byte[]) payload;
            String message = new String(bytes, StandardCharsets.UTF_8);
            blockingQueue.add(message);
        }
    }
}
