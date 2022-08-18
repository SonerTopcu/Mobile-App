import logging
import sys
import paho.mqtt.client as mqtt
import time
import os
import json
import threading


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"Connected with result code {rc}")
        client.connected_flag = True
        client.subscribe("siyaharge")
    else:
        print("Bad connection Returned code=", rc)


def on_message(client, userdata, msg):
    global messageOrangepi
    global timeMessage

    timeMessage = int(time.time())
    dict["timestamp"] = timeMessage
    print(msg.topic, msg.payload)
    string0 = msg.payload.decode("utf-8")

    if string0 == "Power On" and timeMessage == dict["timestamp"]:
        if dict["status"] == "0N":
            messageOrangepi = dict["mod"]
            client.publish("siyaharge", messageOrangepi)

        dict["status"] = "ON"
        os.system('irsend SEND_ONCE ac switch_on')
        messageOrangepi = dict["mod"]
        client.publish("siyaharge", messageOrangepi)

    elif string0 == "Temp Up" and dict["status"] == "ON" and timeMessage == dict["timestamp"]:
        if dict["mod"] == 30:
            messageOrangepi = dict["mod"]
            client.publish("siyaharge", messageOrangepi)

        dict["mod"] += 1
        os.system('irsend SEND_ONCE ac tempup_{}'.format(dict["mod"]))
        print('irsend SEND_ONCE ac tempup_{}'.format(dict["mod"]))
        messageOrangepi = dict["mod"]
        client.publish("siyaharge", messageOrangepi)

    elif string0 == "Temp Down" and dict["status"] == "ON" and timeMessage == dict["timestamp"]:
        if dict["mod"] == 18:
            messageOrangepi = dict["mod"]
            client.publish("siyaharge", messageOrangepi)

        dict["mod"] -= 1
        os.system('irsend SEND_ONCE ac tempd_{}'.format(dict["mod"]))
        print('irsend SEND_ONCE ac tempd_{}'.format(dict["mod"]))
        messageOrangepi = dict["mod"]
        client.publish("siyaharge", messageOrangepi)

    elif string0 == "Power Off" and timeMessage == dict["timestamp"]:
        if dict["status"] == "0FF":
            messageOrangepi = dict["mod"] - 1
            client.publish("siyaharge", messageOrangepi)

        dict["mod"] = 18
        dict["status"] = "OFF"
        os.system('irsend SEND_ONCE ac switch_off')
        messageOrangepi = dict["mod"] - 1
        client.publish("siyaharge", messageOrangepi)


def on_disconnect(client, userdata, rc):
    logging.info("Disconnected" + str(rc))
    client.connected_flag = False
    client.disconnect_flag = True


dict = {
    "timestamp": int(time.time()),
    "status": "OFF",
    "mod": 18
}
messageOrangepi = dict["mod"]
timeMessage = int(time.time())
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


class TimeStamp(threading.Thread):
    def __init__(self, threadID, name):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

    def run(self):
        after = int(time.time()) + 5
        while True:
            current = int(time.time())
            if current == after:
                dict["timestamp"] = current
                client.publish("siyaharge", "50")
                after = int(time.time()) + 5


timeThread = TimeStamp(1, "Time Thread")
timeThread.start()


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
