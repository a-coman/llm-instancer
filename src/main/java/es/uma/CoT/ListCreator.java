package es.uma.CoT;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import es.uma.Experiment;
import es.uma.Llms;
import es.uma.Utils;

public class ListCreator implements Runnable {

    private Experiment experiment;
    private ReentrantLock lock;
    private String categoryId;
    private String categoryPrompt;
    private String modelDescription;
    private BlockingQueue<List> queue;

    public ListCreator(Experiment experiment, ReentrantLock lock, String categoryId, String categoryPrompt, String modelDescription, BlockingQueue<List> queue) {
        this.experiment = experiment;
        this.lock = lock;
        this.categoryId = categoryId;
        this.categoryPrompt = categoryPrompt;
        this.modelDescription = modelDescription;
        this.queue = queue;
    }

    @Override
    public void run() {
        IListCreator listCreator = Llms.getAgent(IListCreator.class, experiment.model);
        String value = listCreator.chat(categoryPrompt, modelDescription);
        List list = new List(categoryId, value);
        
        lock.lock();
        Utils.saveFile("\n\n" + categoryPrompt + value, experiment.instancePath, "output.md");
        lock.unlock();
        
        queue.add(list);
    }
    
}
