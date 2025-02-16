package es.uma.Simple;

import es.uma.Agents;
import es.uma.Models;

public class Main {
    public static void main(String[] args) {
        ISimple simple = Agents.getSimple(Models.getModel("4o"));
        System.out.println(simple.chat("Hello, my name is Andrei"));
        System.out.println(simple.chat("How are you?"));
        System.out.println(simple.chat("Whats my name?"));
    }
}
