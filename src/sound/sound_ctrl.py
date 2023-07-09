from pygame import mixer

# Управление sc
def sc_status():
    """3 состояния: running, upluged, error"""

def sc_boot():
    """Полный контроль sc сервера - запуск, перезапуск при ошибках"""

def sc_unplug():
    """Отключение"""

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
    pass

if __name__ != '__main__':
    main()
