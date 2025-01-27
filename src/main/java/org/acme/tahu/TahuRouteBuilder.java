package org.acme.tahu;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.tahu.message.model.SparkplugBPayloadMap;
import org.apache.camel.BindToRegistry;

public class TahuRouteBuilder extends RouteBuilder {

    String groupId = "SIMPLE";
    String edgeNode = "s01";

    @BindToRegistry
    SparkplugBPayloadMap payloadMap = new SparkplugBPayloadMap.SparkplugBPayloadMapBuilder().createPayload();


    @Override
    public void configure() throws Exception {
        from("timer:timerTest?period=1000")
            .setHeader("someId", constant("someValue")) // this does not work with MQTT messages
            .log("Sending ${header.someId}")
            .to("tahu-edge:"+groupId+"/"+edgeNode+"?deviceIds=&clientId=e01&servers=#property:mqtt-servers&metricDataTypePayloadMap=#payloadMap");

        from("tahu-host:SIMPLE-HOST?clientId=h01&servers=#property:mqtt-servers")
            .log("Received ${header.someId}");
    }
}

