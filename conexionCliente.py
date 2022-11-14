import socket
import random
import nonces
import time
HOST = "127.0.0.1"  # The server's hostname or IP address
PORT = 65432  # The port used by the server

inicio = time.time()

file = open("./conf.txt", "r")
config = []
for line in file:
    line = line.strip()
    words = line.split("=")
    config.append(words[1])
num_pruebas = config[0]
file.close()

def comprobacion(user_input):
    try:
        if(int(user_input)):
            return True
    except ValueError:
        return False

def gen_transacciones():
    cuenta_Or   = random.randint(0,99999)
    cuenta_Dest = 65478
    cantidad    = random.randint(0,1000000)
    cadena = str(cuenta_Or) + " " + str(cuenta_Dest) + " " + str(cantidad) + " " + str(nonces.gen_nonce())
    return cadena


for i in range(int(num_pruebas)):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((HOST, PORT))  
        msgInput = gen_transacciones()
        print(msgInput)
        #keyInput = input("Enter the Secret Key = ")
        s.send(msgInput.encode())
        time.sleep(0.5)
final = time.time()

tiempoFinal = final - inicio
print(tiempoFinal)