import socket
import ssl
from datetime import datetime
import time
from dfva_python.client import Client

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

def verificacion(datos):
    client = Client()
    sign_resp=client.sign( '04-0212-0119', DOCUMENT.encode(), "resumen ejemplo", _format='xml_cofirma')
    client.sign_check(sign_resp['id_transaction'])
    return datos

archivo = "./log/" + str(datetime.now().strftime('%Y_%m'))
while True:
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
                almac = open("almacenaje.txt", "a")
                almac.write(str(p)+"\n")
                almac.close()
                file = open(archivo + ".txt", "r")
                if (verificacion(p.split(" ")[2])):
                    file.write("Dia " + str(datetime.now().strftime('%d')) + " Hora " + str(datetime.now(
                        ).strftime('%H:%M')) + ": ACIERTO - La verificación se ha realizado correctamnte\n")
                else:
                    file.write("Dia " + str(datetime.now().strftime('%d')) + " Hora " + str(datetime.now(
                        ).strftime('%H:%M')) + ": FALLO - La verificación ha fallado\n")
                file.close()
                
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