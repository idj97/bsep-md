package service;

import java.util.Scanner;

public class InputService {


    private Scanner scanner;

    public InputService() {
        this.scanner = new Scanner(System.in);
    }

    public String getUserInput(String inputDescription) {

        System.out.println(inputDescription);
        while(true) {
            String input = scanner.nextLine();

            if(!input.isEmpty()) {
                return input;
            }
        }

    }

}
