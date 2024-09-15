package mwdetection;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
       // Read Config File
        ReadConfig config = new ReadConfig();

        switch (config.get_projectName()){
            case "MW":
                MindWandering mw = new MindWandering(config.get_rootFolder(), config.get_mlModel());
                System.out.println("Project : Mind Wandering ");
                break;
            default:
                System.out.println("Invalid Code for Project Name ! ");
                break;
        }

    }
}
