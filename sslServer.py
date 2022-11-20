import socket
import ssl
from datetime import datetime

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


while True:
    cont = 0
    archivo = "./log/" + str(datetime.now().strftime('%Y_%m'))
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0) as sock:
        sock.bind(('192.168.40.179', 8443))
        #for i in range(int(num_pruebas)):
        sock.listen(2100)
        with context.wrap_socket(sock, server_side=True) as ssock:
            conn, addr = ssock.accept()
            with conn:
                data = conn.recv(1024)
                p = data.decode()
                print(p)

    """#########################"""
    """# CÓDIGO PARA MODIFICAR #"""
    """#########################"""
    file = open(archivo + ".txt", "r")
    cont = 0
    cont1 = 0
    for line in file:
        line = line.strip()
        words = line.split(" ")
        for word in words:
            if word == 'FALLOMANMIDDLE':
                cont = cont + 1
            elif word == 'FALLOREPLAY':
                cont1 +=1

    file.close()
    file = open(archivo + ".txt", "a")
    file.write("Han ocurrido un total de " + str(cont) + " fallos de man in the middle\n")
    file.write("Han ocurrido un total de " + str(cont1) + " fallos de replay\n")
    file.close()