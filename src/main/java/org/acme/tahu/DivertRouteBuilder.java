package org.acme.tahu;

import static org.apache.camel.LoggingLevel.INFO;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.eclipse.tahu.message.model.SparkplugBPayloadMap;
import org.apache.camel.BindToRegistry;

public class DivertRouteBuilder extends RouteBuilder {

    AtomicInteger counter = new AtomicInteger(0);

    String brokerUrl = "tcp://localhost:1883";

    @BindToRegistry
    SparkplugBPayloadMap payloadMap = new SparkplugBPayloadMap.SparkplugBPayloadMapBuilder().createPayload();

    @Override
    public void configure() throws Exception {

        String groupId = "group01";
        String edgeNode = "edge01";

        from("timer:timerTest?period=3000")
            .id("tahu-producer")
            .log(INFO, "TAHU Producing")
            // .process(exchange -> {
            //     exchange.getIn().setBody("message: "+counter.getAndIncrement());
            // })
            .to("tahu-edge:"+groupId+"/"+edgeNode+"?deviceIds="
                +"&clientId=e01&servers=#property:mqtt-servers"
                +"&metricDataTypePayloadMap=#payloadMap");

        String shareName = "share01";
        String topicFilter = "#";
        String topic = "$share/"+shareName+"/"+topicFilter;
        from("paho-mqtt5:"+topic
                +"?brokerUrl="+brokerUrl
                +"&clientId=consumerA&qos=1")
            .id("paho-consumer-A")
            .log(INFO, "PAHO CONSUMER A: ${header.MESSAGE_SEQUENCE_NUMBER}");

        from("paho-mqtt5:"+topic
                +"?brokerUrl="+brokerUrl
                +"&clientId=consumerB&qos=1")
            .id("paho-consumer-B")
            .log(INFO, "PAHO CONSUMER B: ${header.MESSAGE_SEQUENCE_NUMBER}");

    }
}

