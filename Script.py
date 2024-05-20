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
password = "*%1Unz@JBx5&tqwVExJy"
local_path = "C:/Users/mahdi/OneDrive/Desktop/Server/plugins/fishing-rewards-1.1.jar"
remote_dir = "/plugins/fishing-rewards-1.1.jar"

upload(host, port, username, password, local_path, remote_dir)