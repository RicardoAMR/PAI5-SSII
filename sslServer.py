import socket
import ssl
from datetime import datetime
import time

#server
context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
context.load_cert_chain('./certchain.pem', './private.key')

file = open("./conf.txt", "r")
config = []
for line in file:
    line = line.strip()
    words = line.split("=")
    config.append(words[1])
num_pruebas = config[0]
file.close()
m=0

archivo = "./log/" + str(datetime.now().strftime('%Y_%m'))
while m<1244:
    cont = 0
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0) as sock:
        sock.bind(('127.0.0.1', 8443))
        #for i in range(int(num_pruebas)):
        sock.listen(2100)
        with context.wrap_socket(sock, server_side=True) as ssock:
            conn, addr = ssock.accept()
            with conn:
                data = conn.recv(1024)
                p = data.decode()
                print(p)
                file.write("Dia " + str(datetime.now().strftime('%d')) + " Hora " + str(datetime.now(
                        ).strftime('%H:%M')) + ": ACIERTO - La verificación se ha realizado correctamnte\n")
                
    time.sleep(0)
    despues = "./log/" + str(datetime.now().strftime('%Y_%m'))
    if despues != archivo:
        file = open(archivo + ".txt", "r")
        cont = 0
        cont1 = len(file)
        for line in file:
            line = line.strip()
            words = line.split(" ")
            for word in words:
                if word == 'ACIERTO':
                    cont = cont + 1

        file.close()
        file = open(archivo + ".txt", "a")
        file.write("Hay un ratio de acierto de "+cont/cont1)
        file.close()
        archivo = despues