from pythonosc.udp_client import SimpleUDPClient

ip = "127.0.0.1"
port = 57120

client = SimpleUDPClient(ip, port)  # Create client

client.send_message("/track/select", 440)   # Send float message

import time

time.sleep(3)

client.send_message("/track/select", 1337)   # Send float message

# Для воспроизведения запустить sc скрипт в IDE, проинициализировать OSC функцию 