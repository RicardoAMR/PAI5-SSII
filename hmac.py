import hashlib

import binascii

def base64encode(s):
    encoded = binascii.b2a_base64(s, newline=False)
    return encoded

ipad = 0x36
inputSignature = bytes((Key ^ ipad) for Key in range(256))
opad = 0x5C
outputSignature = bytes((Key ^ opad) for Key in range(256))

class HMAC:
    blocksize = 64
    def __init__(self, key, msg=None, digestmod=''):

        if callable(digestmod):
            self.messageDigest = digestmod
        elif isinstance(digestmod, str):
            self.messageDigest = lambda d=b'': hashlib.new(digestmod, d)        
        else:
            self.messageDigest = lambda d=b'': digestmod.new(d)

        self.input = self.messageDigest()
        self.output = self.messageDigest()

        if hasattr(self.input, 'block_size'):
            blocksize = self.input.block_size
            if blocksize < 16:
                blocksize = self.blocksize

        else:
            blocksize = self.blocksize
        self.block_size = blocksize

        if len(key) > blocksize:
            key = self.messageDigest(key).digest()

        key = key.ljust(blocksize, b'\0')

        self.input.update(key.translate(inputSignature))

        self.output.update(key.translate(outputSignature))

        if msg is not None:
            self.update(msg)
    
    @property

    def outer(self):
        return self.output

    def update(self, msg):
        self.input.update(msg)

    def current(self):
        h = self.output.copy()
        h.update(self.input.digest())
        return h

    def digest(self):
        h = self.current()
        return h.digest()

def hmacGenerator(key, msg=None, digestmod=''):
    return HMAC(key, msg, digestmod)

def hmacFuncion(msgInput,keyInput):
    message = bytes(msgInput, 'utf-8')
    key = bytes(keyInput, 'utf-8')
    signature = base64encode(hmacGenerator(key, message, hashlib.sha256).digest())
    HMAC = signature.decode("utf-8")
    return HMAC