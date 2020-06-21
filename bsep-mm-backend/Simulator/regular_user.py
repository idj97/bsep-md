from pysm import State, Event
from random import choices, randint
from time import sleep
from util import *

machine_ip = get_my_public_ip()
paths = get_http_paths()
request_interval = 1


def regular_user(source_ip, username, sm):

    def login(state, event):
        path = "POST /login?username=" + username + "&password=ok_password"
        protocol = "HTTP/1.1"
        status = "200"
        log = create_http_log(source_ip, machine_ip, path, protocol, status)
        process_request(log, request_interval)

        events = [Event("normal"), Event("forbidden"), Event("logout")]
        weights = [80, 10, 10]
        new_event = choices(events, weights, k=1)[0]
        sm.dispatch(new_event)

    def fail_login(state, event):
        path = "POST /login?username=" + username + "&password=bad_password"
        protocol = "HTTP/1.1"
        status = "400"
        log = create_http_log(source_ip, machine_ip, path, protocol, status)
        process_request(log, request_interval)

    def logout(state, event):
        path = "POST /logout?username=" + username
        protocol = "HTTP/1.1"
        status = "200"
        log = create_http_log(source_ip, machine_ip, path, protocol, status)
        process_request(log, request_interval)
        sleep(1)

    def normal(state, event):
        num_of_requets = randint(1, 5)
        for _ in range(num_of_requets):
            path = generete_random_path()
            protocol = "HTTP/1.1"
            status = "200"
            log = create_http_log(source_ip, machine_ip, path, protocol, status)
            process_request(log, request_interval)

        events = [Event("normal"), Event("forbidden"), Event("logout")]
        weights = [70, 10, 20]
        state.parent.dispatch(choices(events, weights, k=1)[0])

    def forbidden(state, event):
        num_of_requets = randint(1, 5)
        for _ in range(num_of_requets):
            path = generate_forbidden_path()
            protocol = "HTTP/1.1"
            status = "403"
            log = create_http_log(source_ip, machine_ip, path, protocol, status)
            process_request(log, request_interval)

        events = [Event("normal"), Event("forbidden"), Event("logout")]
        weights = [50, 30, 20]
        state.parent.dispatch(choices(events, weights, k=1)[0])

    def begin():
        events = [Event("login"), Event("fail_login")]
        weights = [70, 30]
        while True:
            sm.dispatch(choices(events, weights, k=1)[0])

    start = State("start")
    logged = State("logged")
    forbidden_requests = State("forbidden_requests")
    normal_requests = State("normal_requests")

    sm.add_state(start, initial=True)
    sm.add_state(logged)
    sm.add_state(forbidden_requests)
    sm.add_state(normal_requests)

    sm.add_transition(start, start, events=["fail_login"], after=fail_login)
    sm.add_transition(start, logged, events=["login"], after=login)
    sm.add_transition(logged, normal_requests, events=["normal"], after=normal)
    sm.add_transition(logged, forbidden_requests, events=["forbidden"], after=forbidden)
    sm.add_transition(logged, start, events=["logout"], after=logout)
    sm.add_transition(normal_requests, normal_requests, events=["normal"], after=normal)
    sm.add_transition(normal_requests, forbidden_requests, events=["forbidden"], after=forbidden)
    sm.add_transition(forbidden_requests, forbidden_requests, events=["forbidden"], after=forbidden)
    sm.add_transition(forbidden_requests, normal_requests, events=["normal"], after=normal)
    sm.add_transition(forbidden_requests, start, events=["logout"], after=logout)
    sm.add_transition(normal_requests, start, events=["logout"], after=logout)

    sm.initialize()
    begin()


#regular_user("94.32.42.124", "admin", StateMachine("regular_user"))