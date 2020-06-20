import random
from datetime import datetime
from requests import get
from threading import Semaphore
from time import sleep

num_of_connections = 80
num_of_cores = 16
threads = Semaphore(num_of_connections)
cores = Semaphore(num_of_cores)


def process_request(requst, request_interval):
    usage = str(num_of_connections - threads._value) + "/" + str(num_of_connections) + "," + str(num_of_cores - cores._value) + "/" + str(num_of_cores)
    threads.acquire()
    cores.acquire()
    sleep(request_interval)
    save_http_log(requst + " " + usage)
    cores.release()
    threads.release()


def save_http_log(log):
    with open("logs/server.log", "a") as sever_log:
        sever_log.write(log + "\n")
        print(log)


def create_http_log(source_ip, dest_ip, path, protocol, status):
    timestamp = get_current_timestamp()
    packet_size = str(random.randint(1024, 2048))
    return " ".join([timestamp, packet_size, source_ip, dest_ip, path, protocol, status])


def save_firewall_log(log):
    with open("logs/firewall.log", "a") as firewall_log:
        firewall_log.write(log + "\n")


def generete_random_path():
    random.seed(random.randint(0, 100000))
    method = random.choice(get_http_methods())
    path = random.choice(get_http_paths())
    return " ".join([method, path])


def generate_forbidden_path():
    random.seed(random.randint(0, 100000))
    method = random.choice(get_http_methods())
    path = random.choice(get_protected_http_paths())
    return " ".join([method, path])


def generate_random_ip():
    random.seed(random.randint(0, 100000))
    return ".".join(str(random.randint(0, 256)) for _ in range(4))


def generate_ips():
    valid_ips = []
    attacker_ips = []
    firewall_ips = []

    for i in range(1000):
        valid_ips.append(generate_random_ip())

        ip = generate_random_ip()
        if not ip in valid_ips:
            attacker_ips.append(ip)

        ip = generate_random_ip()
        if not (ip in valid_ips or ip in attacker_ips):
            firewall_ips.append(ip)

    return valid_ips, attacker_ips, firewall_ips


def generate_interfaces():
    interfaces = []
    for i in range(4):
        interfaces.append("interface" + str(i))
    return interfaces


def get_my_public_ip():
    return get('http://checkip.amazonaws.com').text[:-1]


def get_current_timestamp():
    now = datetime.now()
    return now.strftime("%d/%m/%Y %H:%M:%S")


def get_http_methods():
    return ["GET", "POST", "PUT", "DELETE", "PATCH"]


def get_public_http_paths():
    return ["/send_request"]


def get_http_paths():
    return ["/home", "/dashboard", "/units", "/management", "/profile"]


def get_protected_http_paths():
    return ["/intel", "/reports", "/plans"]

