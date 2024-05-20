import paramiko
import sys

def upload(host, port, username, password, file_path, remote_dir):
    print("Attempting Upload")
    transport = paramiko.Transport(host, port)
    print("Connecting...")
    transport.connect(username = username, password = password)
    print("Connected.")

    sftp = paramiko.SFTPClient.from_transport(transport)
    print("Uploading...")
    sftp.put(file_path, remote_dir)
    sftp.close()
    transport.close()
    print("Upload done!")


host = "pterodactyl-na1.clovux.net:2022"
port = 2022
username = "lqzkko6d.fdcb92a3"
password = "dpL$dyB67qr%C6Z2PPeN"
local_path = "C:/Users/mahdi/OneDrive/Desktop/Server/plugins/FishingLoot-1.0-SNAPSHOT.jar"
remote_dir = "/plugins/FishingLoot-1.0-SNAPSHOT.jar"

upload(host, port, username, password, local_path, remote_dir)