import socket
import ssl
import random

def gen_transacciones():
    cuenta_Or   = random.randint(0,99999)
    cuenta_Dest = 65478
    cantidad    = random.randint(0,1000000)
    cadena = str(cuenta_Or) + " " + str(cuenta_Dest) + " " + str(cantidad)
    return cadena

file = open("./conf.txt", "r")
config = []
for line in file:
    line = line.strip()
    words = line.split("=")
    config.append(words[1])
num_pruebas = config[0]
file.close()

hostname = '192.168.40.179'
# PROTOCOL_TLS_CLIENT requires valid cert chain and hostname
context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
context.load_verify_locations('./certchain.pem')
context.verify_mode = ssl.CERT_REQUIRED
context.check_hostname = True

cipher = 'DHE-RSA-AES128-SHA:DHE-RSA-AES256-SHA:ECDHE-ECDSA-AES128-GCM-SHA256'
context.set_ciphers(cipher)
for i in range(10):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0) as sock:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(10)
        wrappedSocket = ssl.wrap_socket(sock, ssl_version=ssl.PROTOCOL_TLSv1_2)
        msgInput = gen_transacciones()
        print(msgInput)    
        wrappedSocket.connect((hostname, 8443))
        wrappedSocket.send(msgInput.encode())



