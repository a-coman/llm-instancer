package es.uma.Simple;

import es.uma.Agents;
import es.uma.Models;

public class Main {
    public static void main(String[] args) {
        ISimple simple = Agents.getSimple(Models.getModel("4o"));
        System.out.println(simple.chat("Hello!"));
    }
}
