package es.uma.CoT;

import java.util.concurrent.BlockingQueue;

import es.uma.Experiment;
import es.uma.Listener;
import es.uma.Llms;

public class ListCreator implements Runnable {

    private Experiment experiment;
    private String categoryId;
    private String categoryPrompt;
    private String modelDescription;
    private BlockingQueue<List> queue;

    public ListCreator(Experiment experiment, String categoryId, String categoryPrompt, String modelDescription, BlockingQueue<List> queue) {
        this.experiment = experiment;
        this.categoryId = categoryId;
        this.categoryPrompt = categoryPrompt;
        this.modelDescription = modelDescription;
        this.queue = queue;
    }

    @Override
    public void run() {
        IListCreator listCreator = Llms.getAgent(IListCreator.class, experiment.model);
        for (int gen = 1; gen <= experiment.repetitions; gen++) {
            Listener.setCurrentCategory(categoryId + gen);
            Listener.logsPath = experiment.instancePath + "gen" + gen + "/";
            String value;
            if(gen == 1) {
                value = listCreator.chat(categoryPrompt, modelDescription);
            } else {
                value = listCreator.chat("For the same cateogry and model description, let's continue generating more instances that are structurally and semantically different from the previous ones"); 
            }
            
            List list = new List(gen, categoryId, value);        
            queue.add(list);
        }
    }
    
}
