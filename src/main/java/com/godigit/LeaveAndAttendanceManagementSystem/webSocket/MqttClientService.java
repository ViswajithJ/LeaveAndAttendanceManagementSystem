package com.godigit.LeaveAndAttendanceManagementSystem.webSocket;


import com.godigit.LeaveAndAttendanceManagementSystem.webSocket.MqttConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttClientService {

    private final MqttConfig mqttConfig;

    public MqttClientService(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public void connectAndPublish() {
        String brokerUrl = mqttConfig.getBrokerUrl();
        String clientId = mqttConfig.getClientId();
        String topic = mqttConfig.getTopic();

        try {
            // Create MQTT client
            MqttClient client = new MqttClient(brokerUrl, clientId);

            // Set connection options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            // Connect to broker
            client.connect(options);
            System.out.println("Connected to broker: " + brokerUrl);

            // Create and publish message
            String payload = "Hello from Leave & Attendance System!";
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);

            client.publish(topic, message);
            System.out.println("Message published to topic: " + topic);

            // Disconnect
            client.disconnect();
            System.out.println("Disconnected from broker.");

        } catch (MqttException e) {
            System.err.println("Error in MQTT operation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
