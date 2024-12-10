# [WIP] llm-instancer

This project is developed as part of a bachelor's thesis at the **University of Málaga** and aims to streamline the generation, and validation of UML/OCL-based instances of class digrams. The main objective is to investigate the feasability of using AI models for these task and assist in the automated creation of instances in `.soil` format, ensuring adherence to constraints and multiplicities defined in the diagrams, while creating diverse intances that cover various scenarios and edge cases. 

The app uses the [LangChain4j](https://github.com/langchain4j/langchain4j) library to interact with the OpenAI API and the [USE/OCL pre-compiled binaries](https://github.com/useocl/use/) to check the generated instances against the class diagrams and ensure compliance.

The project uses modular agents to divide responsibilities:
- **`IModelAnalyzer`**: Decomposes and analyzes UML/OCL diagrams for a structured understanding.
- **`IListCreator`**: Creates diverse lists of instances, ensuring adherence to defined constraints and multiplicities.
- **`IModelInstantiator`**: Generates `.soil` files based on the provided model and instance lists, using USE software to check compliance and feedback the model if necessary.

### Requirements
- **Java** and the *[JAVA_HOME]* path. 
- **Maven**
- **OpenAI API key**
- **USE/OCL 7.1.1 pre-compiled binaries** which can be downloaded from [use releases](https://github.com/useocl/use/releases/tag/v7.1.1) and should be placed in the `src/main/resources/use-7.1.1` folder. 

Just run the `mvn clean install` command to build the project and generate the `.jar` file.

### Acknowledgements
As mentioned before, this project uses the **USE (UML-based Specification Environment)**, which is licensed under the GNU General Public License (GPL). For more details, visit the [USE project](https://github.com/useocl/use/). Their distribution contains the following libraries from external parties which are available at:
- [ANTLR parser generator tool](http://www.antlr.org)
- [JUnit library](http://www.junit.org)
