import smbus
import math

# параметры инициализации гироскопа
power_mgmt_1 = 0x6b
power_mgmt_2 = 0x6c

# функция получения данных в ответ на команду reg
def read_word_2c(reg):
    val = read_word(reg)
    if (val >= 0x8000):
        return -((65535 - val) + 1)
    else:
        return val

bus = smbus.SMBus(1)
address = 0x68       # адрес гиро на шине i2c

# инициализация датчика
bus.write_byte_data(address, power_mgmt_1, 0)

# функция получения всех данных
def get_data():
    gyro_xout = read_word_2c(0x43)
    gyro_yout = read_word_2c(0x45)
    gyro_zout = read_word_2c(0x47)
    
    accel_xout = read_word_2c(0x3b)
    accel_yout = read_word_2c(0x3d)
    accel_zout = read_word_2c(0x3f)
    return(gyro_xout, gyro_yout, gyro_zout, accel_xout, accel_yout, accel_zout)

#print ("gyro_xout: ", ("%5d" % gyro_xout), " scailed: ", (gyro_xout / 131))
#print ("gyro_yout: ", ("%5d" % gyro_yout), " scailed: ", (gyro_yout / 131))
#print ("gyro_zout: ", ("%5d" % gyro_zout), " scailed: ", (gyro_zout / 131))

#print ("accel_xout: ", ("%6d" % accel_xout), " scailed: ", (accel_xout / 16384.0))
#print ("accel_yout: ", ("%6d" % accel_yout), " scailed: ", (accel_yout / 16384.0))
#print ("accel_zout: ", ("%6d" % accel_zout), " scailed: ", (accel_zout / 16384.0))
