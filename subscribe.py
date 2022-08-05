import logging
import sys
import paho.mqtt.client as mqtt
import time
import os
import json


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"Connected with result code {rc}")
        client.connected_flag = True
        client.subscribe("siyaharge")
        client.publish("siyaharge", messageOrangepi)
    else:
        print("Bad connection Returned code=", rc)


def on_message(client, userdata, msg):
    global messageOrangepi
    dic["timestamp"] = int(time.time())

    print(f"{msg.topic} {msg.payload}")
    string0 = msg.payload.decode("utf-8")

    if string0 == "Power On" and dic["status"] != "ON":
        dic["status"] = "ON"
        os.system('irsend SEND_ONCE ac switch_on')
        messageOrangepi = json.dumps(dic)
        client.publish("siyaharge", messageOrangepi)

    elif string0 == "Temp Up" and dic["mod"] < 30 and dic["status"] == "ON":
        dic["mod"] += 1
        os.system('irsend SEND_ONCE ac tempup_{}'.format(dic["mod"]))
        print('irsend SEND_ONCE ac tempup_{}'.format(dic["mod"]))
        messageOrangepi = json.dumps(dic)
        client.publish("siyaharge", messageOrangepi)

    elif string0 == "Temp Down" and dic["mod"] >= 19 and dic["status"] == "ON":
        dic["mod"] -= 1
        os.system('irsend SEND_ONCE ac tempd_{}'.format(dic["mod"]))
        print('irsend SEND_ONCE ac tempd_{}'.format(dic["mod"]))
        messageOrangepi = json.dumps(dic)
        client.publish("siyaharge", messageOrangepi)

    elif string0 == "Power Off":
        dic["mod"] = 18
        dic["status"] = "OFF"
        os.system('irsend SEND_ONCE ac switch_off')
        messageOrangepi = json.dumps(dic)
        client.publish("siyaharge", messageOrangepi)

    while True:
        current = int(time.time())
        if dic["timestamp"] == current - 5:
            messageOrangepi = json.dumps(dic)
            client.publish("siyaharge", messageOrangepi)
            break


def on_disconnect(client, userdata, rc):
    logging.info("Disconnected" + str(rc))
    client.connected_flag = False
    client.disconnect_flag = True


dic = {
    "timestamp": int(time.time()),
    "status": "OFF",
    "mod": 18,
}

messageOrangepi = json.dumps(dic)
client = mqtt.Client()
client.connected_flag = False
client.disconnect_flag = False
run_flag = True
retry_limit = 0
retry = 0
retry_delay_fixed = 2
connected_once = False
count = 0
stime = time.time()
retry_delay = retry_delay_fixed
client.on_connect = on_connect
client.on_message = on_message

while run_flag:
    client.loop(0.01)
    if client.connected_flag:
        client.on_message = on_message

    rdelay = time.time() - stime

    if not client.connected_flag and rdelay > retry_delay:
        print("rdelay = ", rdelay)
        try:
            retry += 1
            if connected_once:
                print("Reconnecting attempt number = ", retry)
            else:
                print("Connecting attempt number = ", retry)

            client.connect("broker.hivemq.com", 1883, 60)

            while not client.connected_flag:
                client.loop(0.01)
                stime = time.time()
                retry_delay = retry_delay_fixed

            connected_once = True
            retry = 0

        except Exception as e:
            print("\nConnect failed : ", e)
            retry_delay = retry_delay * retry_delay
            if retry_delay > 100:
                retry_delay = 100
            print("retry Interval = ", retry_delay)
            if retry > retry_limit != 0:
                sys.exit(1)
