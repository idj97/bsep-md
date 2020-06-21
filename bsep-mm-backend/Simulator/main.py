from regular_user import regular_user
from malicious_user import brute_force_user, dos_user
from random import randint
from threading import Thread
from pysm import StateMachine
from time import sleep
import util
import socket
import sys

valid_ips, attacker_ips, firewall_ips = util.generate_ips()
interfaces = util.generate_interfaces()
my_ip = util.get_my_public_ip()
hostname = socket.gethostname()


def create_firewall():
    def firewall_simulator():
        while True:
            timestamp = util.get_current_timestamp()
            status = "BLOCK"
            interface = interfaces[randint(0, len(interfaces) - 1)]
            source_ip = firewall_ips[randint(0, len(firewall_ips) - 1)]
            destination_ip = my_ip
            log = " ".join([timestamp, status, interface, source_ip, destination_ip])
            util.save_firewall_log(log)
            print(log)
            sleep(2)
    Thread(target=firewall_simulator).start()


def create_regular_users(n):
    for i in range(n):
        index = randint(0, len(valid_ips)-1)
        ip = valid_ips.pop(index)
        Thread(target=regular_user, args=[ip, "generic_username", StateMachine("generic_username")]).start()


def create_brute_force_attacker(n):
    for i in range(n):
        index = randint(0, len(attacker_ips)-1)
        ip = attacker_ips.pop(index)
        Thread(target=brute_force_user, args=[ip, "generic_username_ATTACKER"]).start()


def create_ddos_attackers(n):
    for i in range(n):
        index = randint(0, len(attacker_ips) - 1)
        ip = attacker_ips.pop(index)
        Thread(target=dos_user, args=[ip]).start()


def is_arg_present(args, key):
    try:
        for arg in args:
            if key in arg:
                return [True, int(arg.split("=")[1])]
        return [False]
    except:
        print("Bad arguments")
        exit()


if __name__ == "__main__":
    regular_n = 30
    ddos_n = 0
    bf_n = 0

    regular_in = is_arg_present(sys.argv, "regular")
    if regular_in[0]:
        regular_n = regular_in[1]

    ddos_in = is_arg_present(sys.argv, "ddos")
    if ddos_in[0]:
        ddos_n = ddos_in[1]

    bf_in = is_arg_present(sys.argv, "bf")
    if bf_in[0]:
        bf_n = bf_in[1]

    print("Configuration:")
    print("Regular:", regular_n)
    print("DDOS:", ddos_n)
    print("BF login:", bf_n)

    create_firewall()
    create_regular_users(regular_n)
    create_ddos_attackers(ddos_n)
    create_brute_force_attacker(bf_n)
