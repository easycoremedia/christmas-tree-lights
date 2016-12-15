package com.easycore.ChristmasTreeLights.led;

import com.easycore.ChristmasTreeLights.entity.LightRequest;
import com.easycore.ChristmasTreeLights.firebase.ChangeHandler;
import com.easycore.ChristmasTreeLights.helper.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.websocket.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@ClientEndpoint
public class LedController implements ChangeHandler<LightRequest> {

    private Session userSession;

    /**
     * Number of defined nodes.
     */
    private static final int NUM_NODES = 45;

    /**
     * Delay between changing color of next node in milliseconds.
     */
    private static final int DELAY_MILLIS = 50;

    /**
     * Delay between different custom steps in milliseconds.
     */
    private static final int SHORT_DELAY = 200;

    private static final Logger logger = Logger.getLogger(LedController.class.getName());

    /**
     * @param webSocketURI uri of webSocket endpoint
     * @throws IOException         error with network connection
     * @throws DeploymentException internal error in class implementation
     */
    public LedController(URI webSocketURI) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, webSocketURI);
        } catch (DeploymentException e) {
            logger.log(Level.SEVERE, "Error occurred while connecting to websocket.", e);
            throw e;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Error occurred while connecting to websocket.", e);
            throw e;
        }

    }


    @OnOpen
    public void onOpen(Session p) {
        userSession = p;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
        logger.log(Level.INFO, "Websocket connection was closed.", reason);
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        logger.log(Level.WARNING, "Error on websocket connection occured.", thr);
    }

    @OnMessage
    public void onMessage(String ignored) {
    }


    /**
     * Implementation of ChangeHandler.
     *
     * @param lightRequest Object that specifies change.
     */
    @Override
    public synchronized void onChange(LightRequest lightRequest) {
        if (lightRequest.defined()) {
            logger.info(String.format("Incoming request to change color to '%s'.\n", lightRequest.getColor()));
            changeLedColor(lightRequest.color(), DELAY_MILLIS);
            for (int i = 0; i < 2; ++i) {
                Utils.fatalSleep(SHORT_DELAY);
                changeLedColor(new Color(0,0,0), 0);
                Utils.fatalSleep(SHORT_DELAY);
                changeLedColor(lightRequest.color(), 0);
            }
        } else {
            logger.info("Incoming request to random color.\n");
            changeLedColor(null, DELAY_MILLIS);
            Utils.fatalSleep(1000);
            for (int i = 0; i < 5; ++i) {
                changeLedColor(null, 0);
                Utils.fatalSleep(SHORT_DELAY);
            }
        }

    }

    /**
     * Continuously changing color of nodes, changing color of each node after period specified by delay.
     *
     * @param color color to change to, or null if each node should have random color
     * @param delay delay between changing color (in millis)
     */
    private void changeLedColor(@Nullable Color color, int delay) {
        for (int i = 0; i < NUM_NODES; i++) {
            Color nodeColor;
            if (color != null) {
                nodeColor = color;
            } else {
                nodeColor = Utils.randomColor();
            }

            changeNodeColor(i, nodeColor);
            if (delay > 0) {
                Utils.fatalSleep(delay);
            }
        }
    }

    /**
     * Changes color of specified node.
     *
     * @param node  index of node which is subject to change
     * @param color color to change to
     */
    private void changeNodeColor(int node, @Nonnull Color color) {
        int[] colors = new int[]{color.getBlue(), color.getRed(), color.getGreen()};
        for (int i = 0; i < colors.length; i++) {
            int channel = 1 + node * 3 + i;
            int value = colors[i];
            sendChannelValue(channel, value);
        }
    }

    /**
     * Sends message to webSocket, changing value of specified channel.
     *
     * @param channel id of channel
     * @param value   value of channel
     */
    private void sendChannelValue(int channel, int value) {
        String message = String.format("CH|%d|%d", channel, value);
        sendMessage(message);
    }

    /**
     * Sends message to webSocket, thus changing value of channel.
     *
     * @param text string to send to webSocket.
     */
    private void sendMessage(String text) {
        if (userSession == null) {
            logger.log(Level.FINE, "Could not send message, because connection dropped.", text);
            return;
        }

        try {
            userSession.getBasicRemote().sendText(text);
            logger.log(Level.FINE, "Message was sent to websocket connection.", text);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not send message to websocket connection.", e.toString());
        }
    }
}
