# from pygame import mixer
import time
import subprocess

TIMEOUT = 10
PORT = "8081"

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
                raise TimeoutError(f'SuperCollider не запустился в течение 10 секунд:\n{msg}')
    except Exception as e:
        sc_unplug(proc)
        raise Exception('[Err] При запуске sc произошла ошибка, sc убит:', msg)
        
    raise ConnectionError(f'Неизвестная ошибка при запуске SuperCollider, лог:\n{"n".join(msg)}')


# Обработка внешних комманд
def read_stdin():
    """Получение-чтение комманд"""

def cmd_handler():
    """Роутинг команд"""

# Команды sc серверу
def fx_filter(freq: int, ratio: float):
    """Частота от 20 до 20000"""                  # TODO управление частотой нелинейно в процентах

def fx_distort(bias: float, ratio: float):
    "bias - 0 ~ 7"

def fx_delay(ratio: float):
    """"""

def fx_reverb(ratio: float):
    """"""

# Управляющий цикл
def main():
    ps = sc_boot()
    time.sleep(3)
    sc_unplug(ps)


main()
