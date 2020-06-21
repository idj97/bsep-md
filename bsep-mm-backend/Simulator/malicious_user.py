from util import *
from random import randint

machine_ip = get_my_public_ip()
paths = get_public_http_paths()
bf_request_interval = 0.2
dos_request_interval = 120


def brute_force_user(source_ip, username):
    while True:
        path = "POST /login?username=" + username + "&password=bad_password"
        protocol = "HTTP/1.1"
        status = "400"
        log = create_http_log(source_ip, machine_ip, path, protocol, status)
        process_request(log, bf_request_interval)


def dos_user(source_ip):
    while True:
        path = "POST DOS ATTACKER " + paths[randint(0, len(paths)-1)]
        protocol = "HTTP/1.1"
        status = "200"
        log = create_http_log(source_ip, machine_ip, path, protocol, status)
        process_request(log, dos_request_interval)
