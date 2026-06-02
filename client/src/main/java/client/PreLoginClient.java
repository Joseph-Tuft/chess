package client;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.requests.LoginRequest;
import model.requests.RegisterRequest;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Scanner;

import static client.EscapeSequences.*;


public class PreLoginClient {
    private final ServerFacade server;
    private State state = State.PRELOGIN;

    public PreLoginClient(String serverUrl) throws DataAccessException {
        server = new ServerFacade(serverUrl);
    }

    public void preLoginRun(){
        System.out.println(LOGO + "Welcome to Chess, type 'help' to get started!");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e){
                String msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = tokens.length > 0 ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch(DataAccessException e) {
            return e.getMessage();
            }

    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

    public String help(){
        return """
                register <USERNAME> <PASSWORD> <EMAIL>
                login <USERNAME> <PASSWORD>
                help - list commands
                quit
                """;
    }

    private String register(String[] params){
        if (params.length < 3){
            params = Arrays.copyOf(params, 3);
        }
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        try {
            server.register(request);
            state = State.LOGGEDIN;
            return "You have been successfully registered";
        } catch (DataAccessException e){
            return e.toString();
        }
    }

    private String login(String[] params){
        if (params.length < 2){
            params = Arrays.copyOf(params, 2);
        }
        LoginRequest request = new LoginRequest(params[0], params[1]);
        try{
            server.login(request);
            state = State.LOGGEDIN;
            return "You have been successfully logged in";
        } catch (DataAccessException e){
            return e.toString();
        }
    }

}
