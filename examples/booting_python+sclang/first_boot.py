import time
import supriya as sup

# Для запуска прописать полный путь к файлу
# Если падает с ошибкой - перезагрузится - где то в системе висит неубитый scsynth или типо того 
s = sup.Server().boot(executable="D:\\Soft_D\\Prog\\SuperColider\\scsynth.exe")
print(s.status)

s.quit()
