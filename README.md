# llm-instancer

**Paper**: "LLM-based Generation of Realistic and Diverse Domain Model Instances". [ArXiv Preprint](https://arxiv.org/pdf/2604.10350)

**Repository**: Main repository with examples [instagen](https://github.com/a-coman/instagen)

This project aims to streamline the generation, and validation of UML/OCL-based instances of class digrams. The main objective is to investigate the feasability of using AI models for these task and assist in the automated creation of instances in `.soil` format, ensuring adherence to constraints and multiplicities defined in the diagrams, while creating diverse intances that cover various scenarios and edge cases. 

The project has two approaches that use the [LangChain4j](https://github.com/langchain4j/langchain4j) library to interact with the OpenAI API and create this two AI pipelines:
- **Simple (Instruction Learning + One Shot)**: The app generates instances directly in .soil format from the class diagram and an shot example of the soil syntax, without any intermediate reasoning steps. It uses the USE software to check compliance and feedback the model if necessary.
- **Chain-of-thought (CoT)**: The app decomposes the task into multiple steps, generating intermediate reasoning and analysis before producing the final .soil instances:
    - **`IModelAnalyzer`**: Decomposes and analyzes UML/OCL diagrams for a structured understanding outputing a natural language description.
    - **`IListCreator`**: Creates diverse lists of instances in natural language, ensuring adherence to defined constraints and multiplicities.
    - **`IModelInstantiator`**: Generates `.soil` files based on the provided model and instance lists, using USE software to check compliance and feedback the model if necessary.

### Requirements
- **Java 21** and the *[JAVA_HOME]* path. 
- **Maven**
- **Environment Variables** in a `.env` file: `OPENROUTER_API_KEY`, `OPENAI_KEY`, `DEEPSEEK_KEY` or `GEMINI_KEY`, depending on the model selected in `Main`.
- **Modified USE 7.5.0 build** This project runs [USE](https://github.com/useocl/use/) as a separate process (`java -jar use-gui.jar -nogui`, see `src/main/java/es/uma/Use.java`) to load the class diagrams, execute the generated `.soil` instances and check their invariants and multiplicities. `UseLocator` uses `src/main/resources/use-7.5.0` if it is there. Otherwise it downloads the [v7.5.0-export release](https://github.com/a-coman/use/releases/tag/v7.5.0-export) and unzips it into that folder (gitignored). This build is a **modified version of USE 7.5.0**, not an official release. The modification adds a headless `export` command that writes the current object diagram to a PDF file without opening any GUI dialogs:
`
export [--hierarchical|--hierarchicalUD|--horizontal|--horizontalRTL|--swimlaneLandscape|--swimlanePortrait] [--spaceH N] [--spaceV N] {path}/{filename}.pdf
`
llm-evaluator uses it to render each generated instance as a PDF (`Use.instanceToPdf`). The rest of USE behaves as in the upstream 7.5.0 release. You could also use the upstream 7.5.0 release, but llm-evaluator will not generate the PDF files.


### Running the app
Set the model, strategy, domain, repetitions and model set in `src/main/java/es/uma/Main.java`. The model set picks the class diagrams:
- `ModelSet.MODELS`: the 10 domain models.
- `ModelSet.MODELS_WITH_OCL`: the same models with the OCL constraints added after the experts' evaluation.

Then just run the `mvn clean install` command to build the project and generate the `.jar` file.

The diagrams are read from `src/main/resources/models` and `src/main/resources/models-with-ocl`, copies of `docs/models` and `docs/experts-judge/models-with-ocl` in [instagen](https://github.com/a-coman/instagen). Other local/test diagrams can go in `src/main/resources/examples/<name>/diagram.use` (gitignored).

Instances are written to `src/main/resources/instances/<MODEL>/<Simple|CoT>/<domain>/<time>/gen<N>/`, the same layout as `docs/instances` in instagen. Runs against `models-with-ocl` get an `-ocl` suffix on the time folder.

Evaluating the finished instances (well-formedness, conformance, realism metrics and PDF export) is done by [llm-evaluator](https://github.com/a-coman/llm-evaluator), graph-based diversity using [ged](https://github.com/a-coman/ged) or [grakel](https://github.com/a-coman/graphdiff). llm-instancer only runs USE while generating, to send failed checks back to the model.

### License and acknowledgements
**USE** is copyright © 1999-2024 University of Bremen & University of Applied Sciences Hamburg and is licensed under the **GNU General Public License v2** (see `COPYING` in the USE build). The modified version used here is distributed under the same license, and its source code is available at the link above. USE comes with no warranty of any kind. The USE distribution includes libraries and plugins from third parties (such as the [ANTLR parser generator](http://www.antlr.org) and the USE plugins in `lib/plugins`), each under its own license.

**llm-instancer** is licensed under the **MIT License** (see `LICENSE` in this repo). llm-instancer includes libraries and plugins from third parties (such as LangChain4j, Log4j, and dotenv-java), each under its own license. See `pom.xml` for the full list of dependencies and their licenses.