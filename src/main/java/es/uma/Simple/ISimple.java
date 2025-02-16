package es.uma.Simple;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ISimple {
    String chat(String message);
}
