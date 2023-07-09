import os
import time
from time import sleep
import subprocess

from pygame import mixer
from pythonosc.udp_client import SimpleUDPClient


TIMEOUT = 10
IP = "127.0.0.1"
PORT = "57120"

# Управление sc
def sc_status(proc: subprocess.Popen) -> tuple[str, str]:
    """Спрашвивает статуc по udp"""         # ? нафига

def sc_plug() -> subprocess.Popen:
    '''Запуск sc'''
    return subprocess.Popen(['scsynth', '-u', PORT], stdout=subprocess.PIPE)

def sc_unplug(proc: subprocess.Popen):
    """Отключение"""
    proc.kill()

def sc_boot() -> subprocess.Popen:
    """Контроль запуска sc сервера - роняет модуль при ошибках"""
    msg = ""
    proc = sc_plug()
    start_time = time.time()

    try:
        for line in proc.stdout:
            line = line.decode()
            msg += line
            if "SuperCollider 3 server ready." in line:
                print('[info] SuperCollider запущен')               # TODO loguru
                return proc
            elif "failed to open UDP socket: address in use." in line:
                raise ConnectionError(f'На порту {PORT} уже запущен sc - убей его - sclang Server.killAll\n{msg}')
            elif time.time() - start_time > TIMEOUT:
                raise TimeoutError(f'SuperCollider не запустился в течение {TIMEOUT} секунд:\n{msg}')
    except Exception as e:
        sc_unplug(proc)
        raise Exception('[Err] При запуске sc произошла ошибка, sc убит:', msg)
        
    raise ConnectionError(f'Неизвестная ошибка при запуске SuperCollider, лог:\n{msg}')


# Обработка внешних комманд
def read_stdin():
    """Получение-чтение комманд"""

def cmd_handler():
    """Роутинг команд"""


class Player:
    '''Управление воспроизведением
    Что бы не создать несколько плееров - методы статичны'''
    track: mixer.Channel
    is_pause: bool = False

    def __init__(self) -> None:
        mixer.init()

    def play(self, path: str):
        '''Воспроизведение указанного файла'''
        if not os.path.exists(path):
            print(f"[Warning] Несуществующий путь к треку - {path}")
            return
        elif not (path[-4:] in ['.wav', '.ogg']):
            print(f"[Warning] Неподдерживаемый тип файла - {path}")
            return

        if self.is_pause:
            self.track.unpause()
            self.pause = False
        else:
            self.track = mixer.Sound(path).play()
            self.is_pause = True

        
    def pause(self):
        self.track.pause()
        self.is_pause = True

    def stop(self):
        self.track.stop()
        self.is_pause = True


class Osc:
    '''Команды sc серверу'''
    client: SimpleUDPClient
    
    def __init__(self, ip, port):
        self.client = SimpleUDPClient(ip, int(port))     # ? Обработка ошибок

    def fx_filter(self, freq: int=None, ratio: float=None):
        """Частота от 20 до 20000"""                # TODO управление частотой нелинейно в процентах
        if freq:
            self.client.send_message('/fx/filter/freq', freq)
        if ratio:
            self.client.send_message('/fx/filter/dry-wet', ratio)

    def fx_distort(self, bias: float, ratio: float):
        "bias - 0 ~ 2"
        if bias:
            self.client.send_message('/fx/distort/bias', bias)
        if ratio:
            self.client.send_message('/fx/distort/dry-wet', ratio)

    def fx_delay(self, ratio: float):
        self.client.send_message('/fx/delay/dry-wet', ratio)

    def fx_reverb(self, ratio: float):
        self.client.send_message('/fx/reverb/dry-wet', ratio)


def main():
    pass

main()
