import paho.mqtt.client as mqtt
import time
import os
from datetime import datetime


def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")
    client.subscribe("siyaharge")
    client.subscribe("orangepi")


mod = 18
status = 0


def on_message(client, userdata, msg):
    global mod
    global status
    string0 = msg.payload.decode("utf-8")
    print(f"{msg.topic} {msg.payload}")

    if string0 == "Power On" and status != 1:
        status = 1
        os.system('irsend SEND_ONCE ac switch_on')
        client.publish("siyaharge", mod)
    elif string0 == "Temp Up" and mod < 30 and status == 1:
        mod += 1
        os.system('irsend SEND_ONCE ac tempup_{}'.format(mod))
        print('irsend SEND_ONCE ac tempup_{}'.format(mod))
        client.publish("siyaharge", mod)
    elif string0 == "Temp Down" and mod >= 19 and status == 1:
        mod = mod - 1
        os.system('irsend SEND_ONCE ac tempd_{}'.format(mod))
        print('irsend SEND_ONCE ac tempd_{}'.format(mod))
        client.publish("siyaharge", mod)
    elif string0 == "Power Off":
        mod = 18
        status = 0
        os.system('irsend SEND_ONCE ac switch_off')
        client.publish("siyaharge", status)


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.will_set('orangepi/status', '{"status": "Off"}')
client.connect("broker.hivemq.com", 1883, 60)

now = int(time.time())
now2 = time.time_ns()
