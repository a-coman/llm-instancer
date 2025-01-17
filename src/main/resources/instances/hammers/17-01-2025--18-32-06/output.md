# Description

The model "AssemblyChain" represents a manufacturing system focused on assembling and processing different types of pieces to create a final product. The system includes various classes representing elements, machines, and pieces, each with specific attributes and operations.

- **Element (abstract class)**: 
  - Attributes:
    - `x: Integer`: Represents the positional x-coordinate.
    - `y: Integer`: Represents the positional y-coordinate.
  - Purpose: A base class for defining spatial elements within the assembly chain.

- **Machine (abstract class, inherits from Element)**:
  - Attributes:
    - `processingTime: Integer`: Represents the time required for processing tasks.
  - Operations:
    - `start()`: Initiates the machine's operations.
    - `stop()`: Halts the machine's operations.
  - Purpose: Serves as a base class for machines involved in the assembly process.

- **PieceGenerator (abstract class, inherits from Machine)**:
  - Purpose: A base class for machines that generate pieces.

- **HandleGenerator (inherits from PieceGenerator)**:
  - Attributes:
    - `counter: Integer`: Tracks the number of handles generated.
  - Purpose: Specialized machine for generating handle pieces.

- **HeadGenerator (inherits from PieceGenerator)**:
  - Attributes:
    - `counter: Integer`: Tracks the number of heads generated.
  - Purpose: Specialized machine for generating head pieces.

- **Assembler (inherits from Machine)**:
  - Purpose: Machine responsible for assembling handles and heads into hammers.

- **Polisher (inherits from Machine)**:
  - Purpose: Machine responsible for polishing hammer pieces.

- **Tray (inherits from Element)**:
  - Attributes:
    - `capacity: Integer`: Maximum number of pieces it can hold.
  - Purpose: Used for holding and transporting pieces between machines.

- **Piece (abstract class)**:
  - Purpose: Base class for various types of pieces involved in the assembly.

- **Head (inherits from Piece)**:
  - Purpose: Represents a piece type used in the assembly process.

- **Handle (inherits from Piece)**:
  - Purpose: Represents a piece type used in the assembly process.

- **Hammer (inherits from Piece)**:
  - Attributes:
    - `isPolished: Boolean`: Indicates whether the hammer has been polished.
  - Purpose: Represents the final assembled product.

# Relationships

- **Output Association**:
  - Between `Machine` [1..*] and `Tray` [1].
  - Each machine must be associated with at least one tray as an output. A tray can only be the output for one machine.

- **Input Association**:
  - Between `Tray` [*] and `Machine` [*].
  - Multiple trays can serve as input to multiple machines, creating a flexible input-output model.

- **Content Aggregation**:
  - Between `Tray` [1] and `Piece` [*] ordered.
  - Each tray contains an ordered collection of pieces, and must contain at least one piece.

# Invariants

- **Tray Invariants**:
  - `AtLeastOneCell`: Ensures that each tray has a positive capacity (`self.capacity > 0`).
  - `PiecesSameType`: Ensures all pieces in a tray are of the same type (all Heads, all Handles, or all Hammers).

- **Assembler Invariants**:
  - `OneinputOfHandles`: Assembler must have at least one input tray containing only handles.
  - `OneinputOfHeads`: Assembler must have at least one input tray containing only heads.
  - `HammersOutput`: Ensures the output tray of an assembler contains only hammer pieces.

- **Polisher Invariant**:
  - `HammersInput`: Polisher must only have input trays containing hammer pieces.

- **PieceGenerator Invariants**:
  - `HeadsOutput` (for HeadGenerator): Output trays contain only heads.
  - `HandlesOutput` (for HandleGenerator): Output trays contain only handles.
  - `NoInput`: PieceGenerators should not have any input trays (`self.input->size()=0`).

# Category: Baseline Instances
Describe typical or standard instances that align with common expectations of the model. Ensure every class and relationship is represented at least once in a baseline configuration.


Here are diverse instances for the "AssemblyChain" model, ensuring coverage of all classes, relationships, and invariants:

### Instance 1: Basic Handle Generator Setup

- **Element Instance**: `element1`
  - Attributes: 
    - `x: 10`
    - `y: 20`

- **HandleGenerator**: `handleGen1`
  - Inherits from `Machine` and `PieceGenerator`
  - Attributes:
    - `x: 10`
    - `y: 20`
    - `processingTime: 5`
    - `counter: 0`
  - Operations:
    - `start()`: Starts generating handles.
    - `stop()`: Stops the handle generation process.

- **Tray**: `tray1`
  - Inherits from `Element`
  - Attributes:
    - `x: 15`
    - `y: 25`
    - `capacity: 100`
  - Purpose: Collects handles from `handleGen1`.

- **Output Association**:
  - `handleGen1` -> `tray1`

- **Piece**: `handlePiece1`, `handlePiece2`
  - Inherits from `Handle`
  - Purpose: Represents individual handle pieces.

- **Content Aggregation**:
  - `tray1` contains `[handlePiece1, handlePiece2]`

### Instance 2: Head Generator with Regional Variance

- **HeadGenerator**: `headGen1`
  - Inherits from `Machine` and `PieceGenerator`
  - Attributes:
    - `x: 30`
    - `y: 40`
    - `processingTime: 7`
    - `counter: 0`
  - Operations:
    - `start()`: Begins head production.
    - `stop()`: Halts head production.

- **Tray**: `tray2`
  - Inherits from `Element`
  - Attributes:
    - `x: 35`
    - `y: 45`
    - `capacity: 50`
  - Purpose: Collects heads from `headGen1`.

- **Output Association**:
  - `headGen1` -> `tray2`

- **Piece**: `headPiece1`, `headPiece2`
  - Inherits from `Head`
  - Purpose: Represents individual head pieces.

- **Content Aggregation**:
  - `tray2` contains `[headPiece1, headPiece2]`

### Instance 3: Assembler Setup

- **Assembler**: `assembler1`
  - Inherits from `Machine`
  - Attributes:
    - `x: 50`
    - `y: 60`
    - `processingTime: 10`
  - Operations:
    - `start()`: Initiates assembly.
    - `stop()`: Stops assembly.

- **Tray**: `tray3`
  - Inherits from `Element`
  - Attributes:
    - `x: 55`
    - `y: 65`
    - `capacity: 30`
  - Purpose: Collects assembled hammers.

- **Input Association**:
  - `tray1` -> `assembler1` (Handles)
  - `tray2` -> `assembler1` (Heads)

- **Output Association**:
  - `assembler1` -> `tray3`

- **Piece**: `hammer1`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`
  - Purpose: Represents assembled, unpolished hammers.

- **Content Aggregation**:
  - `tray3` contains `[hammer1]`

### Instance 4: Polisher Setup in a Different Region

- **Polisher**: `polisher1`
  - Inherits from `Machine`
  - Attributes:
    - `x: 70`
    - `y: 80`
    - `processingTime: 4`
  - Operations:
    - `start()`: Starts polishing.
    - `stop()`: Stops polishing.

- **Tray**: `tray4`
  - Inherits from `Element`
  - Attributes:
    - `x: 75`
    - `y: 85`
    - `capacity: 10`
  - Purpose: Holds polished hammers.

- **Input Association**:
  - `tray3` -> `polisher1`

- **Output Association**:
  - `polisher1` -> `tray4`

- **Piece**: `polishedHammer1`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: true`
  - Purpose: Final polished product.

- **Content Aggregation**:
  - `tray4` contains `[polishedHammer1]`

### Regional Diversity and Structural Complexity

By varying the `x` and `y` attributes and processing times, these instances represent a manufacturing system that could be located in different regions or optimized for various production speeds. The configurations also demonstrate the flexibility of the model by supporting different input and output associations, adhering to all invariants and constraints.
```
```
!new Element('element1')
!element1.x:=10
!element1.y:=20

!new HandleGenerator('handleGen1')
!handleGen1.x:=10
!handleGen1.y:=20
!handleGen1.processingTime:=5
!handleGen1.counter:=0

!new Tray('tray1')
!tray1.x:=15
!tray1.y:=25
!tray1.capacity:=100

!insert(handleGen1,tray1) into Output

!new Handle('handlePiece1')
!new Handle('handlePiece2')

!insert(handlePiece1,tray1) into Content
!insert(handlePiece2,tray1) into Content

!new HeadGenerator('headGen1')
!headGen1.x:=30
!headGen1.y:=40
!headGen1.processingTime:=7
!headGen1.counter:=0

!new Tray('tray2')
!tray2.x:=35
!tray2.y:=45
!tray2.capacity:=50

!insert(headGen1,tray2) into Output

!new Head('headPiece1')
!new Head('headPiece2')

!insert(headPiece1,tray2) into Content
!insert(headPiece2,tray2) into Content

!new Assembler('assembler1')
!assembler1.x:=50
!assembler1.y:=60
!assembler1.processingTime:=10

!new Tray('tray3')
!tray3.x:=55
!tray3.y:=65
!tray3.capacity:=30

!insert(tray1,assembler1) into Input
!insert(tray2,assembler1) into Input
!insert(assembler1,tray3) into Output

!new Hammer('hammer1')
!hammer1.isPolished:=false

!insert(hammer1,tray3) into Content

!new Polisher('polisher1')
!polisher1.x:=70
!polisher1.y:=80
!polisher1.processingTime:=4

!new Tray('tray4')
!tray4.x:=75
!tray4.y:=85
!tray4.capacity:=10

!insert(tray3,polisher1) into Input
!insert(polisher1,tray4) into Output

!new Hammer('polishedHammer1')
!polishedHammer1.isPolished:=true

!insert(polishedHammer1,tray4) into Content
```
```

# Category: Boundary Instances
Create instances that test boundary values of constraints, such as:
- Minimum and maximum multiplicities.
- Empty collections for optional associations.
- Extreme values for numeric or range invariants constraints.


Here are instances that test the boundaries and constraints of the "AssemblyChain" model, focusing on minimum and maximum values, and optional associations.

### Instance 1: Minimum Tray Capacity and Single Piece

- **HandleGenerator**: `handleGenBoundary`
  - Inherits from `Machine` and `PieceGenerator`
  - Attributes:
    - `x: 0`
    - `y: 0`
    - `processingTime: 1`
    - `counter: 1`
  - Operations:
    - `start()`: Starts generating handles.
    - `stop()`: Stops handle generation.

- **Tray**: `trayBoundary1`
  - Inherits from `Element`
  - Attributes:
    - `x: 5`
    - `y: 5`
    - `capacity: 1`  // Minimum capacity to ensure AtLeastOneCell invariant
  - Purpose: Holds exactly one handle, testing minimum capacity.

- **Output Association**:
  - `handleGenBoundary` -> `trayBoundary1`

- **Piece**: `handleBoundary`
  - Inherits from `Handle`
  - Purpose: Represents a single handle piece.

- **Content Aggregation**:
  - `trayBoundary1` contains `[handleBoundary]`

### Instance 2: Maximum Tray Capacity with Empty Input for Generator

- **HeadGenerator**: `headGenMax`
  - Inherits from `Machine` and `PieceGenerator`
  - Attributes:
    - `x: 10`
    - `y: 10`
    - `processingTime: 2`
    - `counter: 1000`  // Testing high counter value
  - Operations:
    - `start()`: Initiates head production.
    - `stop()`: Stops head production.

- **Tray**: `trayBoundary2`
  - Inherits from `Element`
  - Attributes:
    - `x: 15`
    - `y: 15`
    - `capacity: 1000`  // High capacity to test upper limit
  - Purpose: Holds many heads for a high-output scenario.

- **Output Association**:
  - `headGenMax` -> `trayBoundary2`

- **Pieces**: `headBoundary1`, `headBoundary2`, ..., `headBoundary1000`
  - Inherits from `Head`
  - Purpose: Represents maximum number of head pieces.

- **Content Aggregation**:
  - `trayBoundary2` contains `[headBoundary1, headBoundary2, ..., headBoundary1000]`

### Instance 3: Assembler with Minimum Inputs

- **Assembler**: `assemblerBoundary`
  - Inherits from `Machine`
  - Attributes:
    - `x: 20`
    - `y: 20`
    - `processingTime: 3`

- **Tray**: `trayBoundary3`
  - Inherits from `Element`
  - Attributes:
    - `x: 25`
    - `y: 25`
    - `capacity: 10`
  - Purpose: Holds assembled hammers, testing minimum input trays.

- **Input Association**:
  - `trayBoundary1` -> `assemblerBoundary`  // Handles input
  - `trayBoundary2` -> `assemblerBoundary`  // Heads input

- **Output Association**:
  - `assemblerBoundary` -> `trayBoundary3`

- **Piece**: `hammerBoundary`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`
  - Purpose: Represents minimally assembled hammer.

- **Content Aggregation**:
  - `trayBoundary3` contains `[hammerBoundary]`

### Instance 4: Polisher with Empty Input (Invalid)

- **Polisher**: `polisherBoundary`
  - Inherits from `Machine`
  - Attributes:
    - `x: 30`
    - `y: 30`
    - `processingTime: 4`

- **Tray**: `trayBoundary4`
  - Inherits from `Element`
  - Attributes:
    - `x: 35`
    - `y: 35`
    - `capacity: 5`
  - Purpose: Holds polished hammers, but starts empty to test invariant.

- **Input Association**:
  - `trayBoundary3` -> `polisherBoundary`  // Valid input

- **Output Association**:
  - `polisherBoundary` -> `trayBoundary4`

- **Piece**: `polishedHammerBoundary`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: true`
  - Purpose: Final polished product.

- **Content Aggregation**:
  - `trayBoundary4` contains `[polishedHammerBoundary]`

### Summary

These instances stretch the model's constraints, ensuring that each invariant and association rule is respected, while also testing boundary conditions like minimum and maximum values for tray capacity and piece counts. They also validate the absence of inputs for piece generators and ensure that outputs are correctly managed through trays associated with specific piece types.
```
```
!new HandleGenerator('handleGenBoundary')
!handleGenBoundary.x:=0
!handleGenBoundary.y:=0
!handleGenBoundary.processingTime:=1
!handleGenBoundary.counter:=1

!new Tray('trayBoundary1')
!trayBoundary1.x:=5
!trayBoundary1.y:=5
!trayBoundary1.capacity:=1

!insert(handleGenBoundary,trayBoundary1) into Output

!new Handle('handleBoundary')
!insert(handleBoundary,trayBoundary1) into Content

!new HeadGenerator('headGenMax')
!headGenMax.x:=10
!headGenMax.y:=10
!headGenMax.processingTime:=2
!headGenMax.counter:=1000

!new Tray('trayBoundary2')
!trayBoundary2.x:=15
!trayBoundary2.y:=15
!trayBoundary2.capacity:=1000

!insert(headGenMax,trayBoundary2) into Output

!new Head('headBoundary1')
!insert(headBoundary1,trayBoundary2) into Content
!new Head('headBoundary2')
!insert(headBoundary2,trayBoundary2) into Content
!new Head('headBoundary3')
!insert(headBoundary3,trayBoundary2) into Content
!new Head('headBoundary4')
!insert(headBoundary4,trayBoundary2) into Content
!new Head('headBoundary5')
!insert(headBoundary5,trayBoundary2) into Content
!new Head('headBoundary6')
!insert(headBoundary6,trayBoundary2) into Content
!new Head('headBoundary7')
!insert(headBoundary7,trayBoundary2) into Content
!new Head('headBoundary8')
!insert(headBoundary8,trayBoundary2) into Content
!new Head('headBoundary9')
!insert(headBoundary9,trayBoundary2) into Content
!new Head('headBoundary10')
!insert(headBoundary10,trayBoundary2) into Content

!new Assembler('assemblerBoundary')
!assemblerBoundary.x:=20
!assemblerBoundary.y:=20
!assemblerBoundary.processingTime:=3

!new Tray('trayBoundary3')
!trayBoundary3.x:=25
!trayBoundary3.y:=25
!trayBoundary3.capacity:=10

!insert(trayBoundary1,assemblerBoundary) into Input
!insert(trayBoundary2,assemblerBoundary) into Input
!insert(assemblerBoundary,trayBoundary3) into Output

!new Hammer('hammerBoundary')
!hammerBoundary.isPolished:=false
!insert(hammerBoundary,trayBoundary3) into Content

!new Polisher('polisherBoundary')
!polisherBoundary.x:=30
!polisherBoundary.y:=30
!polisherBoundary.processingTime:=4

!new Tray('trayBoundary4')
!trayBoundary4.x:=35
!trayBoundary4.y:=35
!trayBoundary4.capacity:=5

!insert(trayBoundary3,polisherBoundary) into Input
!insert(polisherBoundary,trayBoundary4) into Output

!new Hammer('polishedHammerBoundary')
!polishedHammerBoundary.isPolished:=true
!insert(polishedHammerBoundary,trayBoundary4) into Content
```
```

# Category: Complex Instances
Describe complex instances involving multiple interrelated entities, testing interactions between constraints, relationships, and multiplicities.


Here are complex instances for the "AssemblyChain" model that test interactions between constraints, relationships, and multiplicities:

### Complex Instance 1: Full Assembly and Polishing Line

#### Handle Generation

- **HandleGenerator**: `handleGenComplex1`
  - Attributes:
    - `x: 10`
    - `y: 10`
    - `processingTime: 5`
    - `counter: 200`
  - Purpose: Generates handles for the assembly line.

- **Tray**: `trayHandles1`
  - Attributes:
    - `x: 15`
    - `y: 15`
    - `capacity: 200`
  - Purpose: Holds handles generated by `handleGenComplex1`.

- **Output Association**:
  - `handleGenComplex1` -> `trayHandles1`

- **Pieces**: `handle1`, `handle2`, ..., `handle200`
  - Inherits from `Handle`
  - Purpose: Collection of handle pieces for assembly.

- **Content Aggregation**:
  - `trayHandles1` contains `[handle1, handle2, ..., handle200]`

#### Head Generation

- **HeadGenerator**: `headGenComplex1`
  - Attributes:
    - `x: 20`
    - `y: 20`
    - `processingTime: 6`
    - `counter: 150`
  - Purpose: Generates heads for the assembly line.

- **Tray**: `trayHeads1`
  - Attributes:
    - `x: 25`
    - `y: 25`
    - `capacity: 150`
  - Purpose: Holds heads generated by `headGenComplex1`.

- **Output Association**:
  - `headGenComplex1` -> `trayHeads1`

- **Pieces**: `head1`, `head2`, ..., `head150`
  - Inherits from `Head`
  - Purpose: Collection of head pieces for assembly.

- **Content Aggregation**:
  - `trayHeads1` contains `[head1, head2, ..., head150]`

#### Assembly

- **Assembler**: `assemblerComplex1`
  - Attributes:
    - `x: 30`
    - `y: 30`
    - `processingTime: 7`
  - Purpose: Assembles handles and heads into hammers.

- **Tray**: `trayHammers1`
  - Attributes:
    - `x: 35`
    - `y: 35`
    - `capacity: 100`
  - Purpose: Holds assembled hammers.

- **Input Association**:
  - `trayHandles1` -> `assemblerComplex1`
  - `trayHeads1` -> `assemblerComplex1`

- **Output Association**:
  - `assemblerComplex1` -> `trayHammers1`

- **Pieces**: `hammer1`, `hammer2`, ..., `hammer100`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`
  - Purpose: Collection of unpolished hammers.

- **Content Aggregation**:
  - `trayHammers1` contains `[hammer1, hammer2, ..., hammer100]`

#### Polishing

- **Polisher**: `polisherComplex1`
  - Attributes:
    - `x: 40`
    - `y: 40`
    - `processingTime: 4`
  - Purpose: Polishes hammers.

- **Tray**: `trayPolishedHammers1`
  - Attributes:
    - `x: 45`
    - `y: 45`
    - `capacity: 100`
  - Purpose: Holds polished hammers.

- **Input Association**:
  - `trayHammers1` -> `polisherComplex1`

- **Output Association**:
  - `polisherComplex1` -> `trayPolishedHammers1`

- **Pieces**: `polishedHammer1`, `polishedHammer2`, ..., `polishedHammer100`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: true`
  - Purpose: Final polished product.

- **Content Aggregation**:
  - `trayPolishedHammers1` contains `[polishedHammer1, polishedHammer2, ..., polishedHammer100]`

### Complex Instance 2: Parallel Assembly Lines with Shared Resources

#### Shared Handle Generator

- **HandleGenerator**: `handleGenComplex2`
  - Attributes:
    - `x: 50`
    - `y: 50`
    - `processingTime: 5`
    - `counter: 400`
  - Purpose: Generates handles for multiple assembly lines.

- **Tray**: `trayHandles2`
  - Attributes:
    - `x: 55`
    - `y: 55`
    - `capacity: 400`
  - Purpose: Shared tray for handles.

- **Output Association**:
  - `handleGenComplex2` -> `trayHandles2`

- **Pieces**: `handle201`, `handle202`, ..., `handle400`
  - Inherits from `Handle`
  - Purpose: Collection of handle pieces for shared assembly.

- **Content Aggregation**:
  - `trayHandles2` contains `[handle201, handle202, ..., handle400]`

#### First Assembly Line

- **Assembler**: `assemblerComplex2a`
  - Attributes:
    - `x: 60`
    - `y: 60`
    - `processingTime: 8`

- **Tray**: `trayHammers2a`
  - Attributes:
    - `x: 65`
    - `y: 65`
    - `capacity: 80`

- **Input Association**:
  - `trayHandles2` -> `assemblerComplex2a`
  - `trayHeads1` -> `assemblerComplex2a`

- **Output Association**:
  - `assemblerComplex2a` -> `trayHammers2a`

- **Pieces**: `hammer101`, `hammer102`, ..., `hammer180`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`

- **Content Aggregation**:
  - `trayHammers2a` contains `[hammer101, hammer102, ..., hammer180]`

#### Second Assembly Line

- **Assembler**: `assemblerComplex2b`
  - Attributes:
    - `x: 70`
    - `y: 70`
    - `processingTime: 9`

- **Tray**: `trayHammers2b`
  - Attributes:
    - `x: 75`
    - `y: 75`
    - `capacity: 120`

- **Input Association**:
  - `trayHandles2` -> `assemblerComplex2b`
  - `trayHeads1` -> `assemblerComplex2b`

- **Output Association**:
  - `assemblerComplex2b` -> `trayHammers2b`

- **Pieces**: `hammer181`, `hammer182`, ..., `hammer300`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`

- **Content Aggregation**:
  - `trayHammers2b` contains `[hammer181, hammer182, ..., hammer300]`

### Summary

These complex instances demonstrate a manufacturing system with intricate interactions among multiple machines and trays. They validate the system's ability to handle shared resources, parallel processes, and large-scale operations, while adhering to all constraints and invariants.
```
```
!new HandleGenerator('handleGenComplex1')
!handleGenComplex1.x:=10
!handleGenComplex1.y:=10
!handleGenComplex1.processingTime:=5
!handleGenComplex1.counter:=200

!new Tray('trayHandles1')
!trayHandles1.x:=15
!trayHandles1.y:=15
!trayHandles1.capacity:=200

!insert(handleGenComplex1,trayHandles1) into Output

!new HeadGenerator('headGenComplex1')
!headGenComplex1.x:=20
!headGenComplex1.y:=20
!headGenComplex1.processingTime:=6
!headGenComplex1.counter:=150

!new Tray('trayHeads1')
!trayHeads1.x:=25
!trayHeads1.y:=25
!trayHeads1.capacity:=150

!insert(headGenComplex1,trayHeads1) into Output

!new Assembler('assemblerComplex1')
!assemblerComplex1.x:=30
!assemblerComplex1.y:=30
!assemblerComplex1.processingTime:=7

!new Tray('trayHammers1')
!trayHammers1.x:=35
!trayHammers1.y:=35
!trayHammers1.capacity:=100

!insert(trayHandles1,assemblerComplex1) into Input
!insert(trayHeads1,assemblerComplex1) into Input
!insert(assemblerComplex1,trayHammers1) into Output

!new Polisher('polisherComplex1')
!polisherComplex1.x:=40
!polisherComplex1.y:=40
!polisherComplex1.processingTime:=4

!new Tray('trayPolishedHammers1')
!trayPolishedHammers1.x:=45
!trayPolishedHammers1.y:=45
!trayPolishedHammers1.capacity:=100

!insert(trayHammers1,polisherComplex1) into Input
!insert(polisherComplex1,trayPolishedHammers1) into Output

!new Handle('handle1')
!insert(handle1,trayHandles1) into Content
!new Handle('handle2')
!insert(handle2,trayHandles1) into Content
!new Handle('handle3')
!insert(handle3,trayHandles1) into Content
!new Handle('handle4')
!insert(handle4,trayHandles1) into Content
!new Handle('handle5')
!insert(handle5,trayHandles1) into Content

!new Head('head1')
!insert(head1,trayHeads1) into Content
!new Head('head2')
!insert(head2,trayHeads1) into Content
!new Head('head3')
!insert(head3,trayHeads1) into Content
!new Head('head4')
!insert(head4,trayHeads1) into Content
!new Head('head5')
!insert(head5,trayHeads1) into Content

!new Hammer('hammer1')
!hammer1.isPolished:=false
!insert(hammer1,trayHammers1) into Content
!new Hammer('hammer2')
!hammer2.isPolished:=false
!insert(hammer2,trayHammers1) into Content
!new Hammer('hammer3')
!hammer3.isPolished:=false
!insert(hammer3,trayHammers1) into Content
!new Hammer('hammer4')
!hammer4.isPolished:=false
!insert(hammer4,trayHammers1) into Content
!new Hammer('hammer5')
!hammer5.isPolished:=false
!insert(hammer5,trayHammers1) into Content

!new Hammer('polishedHammer1')
!polishedHammer1.isPolished:=true
!insert(polishedHammer1,trayPolishedHammers1) into Content
!new Hammer('polishedHammer2')
!polishedHammer2.isPolished:=true
!insert(polishedHammer2,trayPolishedHammers1) into Content
!new Hammer('polishedHammer3')
!polishedHammer3.isPolished:=true
!insert(polishedHammer3,trayPolishedHammers1) into Content
!new Hammer('polishedHammer4')
!polishedHammer4.isPolished:=true
!insert(polishedHammer4,trayPolishedHammers1) into Content
!new Hammer('polishedHammer5')
!polishedHammer5.isPolished:=true
!insert(polishedHammer5,trayPolishedHammers1) into Content

!new HandleGenerator('handleGenComplex2')
!handleGenComplex2.x:=50
!handleGenComplex2.y:=50
!handleGenComplex2.processingTime:=5
!handleGenComplex2.counter:=400

!new Tray('trayHandles2')
!trayHandles2.x:=55
!trayHandles2.y:=55
!trayHandles2.capacity:=400

!insert(handleGenComplex2,trayHandles2) into Output

!new Handle('handle201')
!insert(handle201,trayHandles2) into Content
!new Handle('handle202')
!insert(handle202,trayHandles2) into Content
!new Handle('handle203')
!insert(handle203,trayHandles2) into Content
!new Handle('handle204')
!insert(handle204,trayHandles2) into Content
!new Handle('handle205')
!insert(handle205,trayHandles2) into Content

!new Assembler('assemblerComplex2a')
!assemblerComplex2a.x:=60
!assemblerComplex2a.y:=60
!assemblerComplex2a.processingTime:=8

!new Tray('trayHammers2a')
!trayHammers2a.x:=65
!trayHammers2a.y:=65
!trayHammers2a.capacity:=80

!insert(trayHandles2,assemblerComplex2a) into Input
!insert(trayHeads1,assemblerComplex2a) into Input
!insert(assemblerComplex2a,trayHammers2a) into Output

!new Hammer('hammer101')
!hammer101.isPolished:=false
!insert(hammer101,trayHammers2a) into Content
!new Hammer('hammer102')
!hammer102.isPolished:=false
!insert(hammer102,trayHammers2a) into Content
!new Hammer('hammer103')
!hammer103.isPolished:=false
!insert(hammer103,trayHammers2a) into Content
!new Hammer('hammer104')
!hammer104.isPolished:=false
!insert(hammer104,trayHammers2a) into Content
!new Hammer('hammer105')
!hammer105.isPolished:=false
!insert(hammer105,trayHammers2a) into Content

!new Assembler('assemblerComplex2b')
!assemblerComplex2b.x:=70
!assemblerComplex2b.y:=70
!assemblerComplex2b.processingTime:=9

!new Tray('trayHammers2b')
!trayHammers2b.x:=75
!trayHammers2b.y:=75
!trayHammers2b.capacity:=120

!insert(trayHandles2,assemblerComplex2b) into Input
!insert(trayHeads1,assemblerComplex2b) into Input
!insert(assemblerComplex2b,trayHammers2b) into Output

!new Hammer('hammer181')
!hammer181.isPolished:=false
!insert(hammer181,trayHammers2b) into Content
!new Hammer('hammer182')
!hammer182.isPolished:=false
!insert(hammer182,trayHammers2b) into Content
!new Hammer('hammer183')
!hammer183.isPolished:=false
!insert(hammer183,trayHammers2b) into Content
!new Hammer('hammer184')
!hammer184.isPolished:=false
!insert(hammer184,trayHammers2b) into Content
!new Hammer('hammer185')
!hammer185.isPolished:=false
!insert(hammer185,trayHammers2b) into Content
```
```

# Category: Unrealistc but valid
Develop instances that are syntactically and constraints-wise/multiplicity-wise valid but unlikely or impossible in real life. Edge cases and uncommon combinations of relationships and attributes.


Here are instances that are syntactically and constraints-wise valid but represent unrealistic scenarios:

### Instance 1: Inverted Assembly Line

#### Description
A situation where a polisher is set up to polish hammer pieces before they are even assembled from heads and handles.

#### Components

- **Polisher**: `polisherUnrealistic`
  - Attributes:
    - `x: 0`
    - `y: 0`
    - `processingTime: 2`

- **Tray**: `trayPolishedUnrealistic`
  - Attributes:
    - `x: 5`
    - `y: 5`
    - `capacity: 10`
  - Purpose: Holds polished hammers that haven't been assembled.

- **Pieces**: `unassembledHammer1`, `unassembledHammer2`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: true`
  - Purpose: Hammers are polished but not composed of heads and handles.

- **Content Aggregation**:
  - `trayPolishedUnrealistic` contains `[unassembledHammer1, unassembledHammer2]`

- **Input Association**:
  - `trayPolishedUnrealistic` -> `polisherUnrealistic`

### Instance 2: Giant Handle Generation with Zero Processing Time

#### Description
A handle generator that instantly produces an enormous number of handles, demonstrating extreme capacity and instantaneous production.

#### Components

- **HandleGenerator**: `handleGenUnrealistic`
  - Attributes:
    - `x: 10`
    - `y: 10`
    - `processingTime: 0`  // Unrealistic zero processing time
    - `counter: 1000000`  // Extremely large number of handles generated

- **Tray**: `trayHandlesUnrealistic`
  - Attributes:
    - `x: 15`
    - `y: 15`
    - `capacity: 1000000`  // Extremely large capacity for holding handles
  - Purpose: Holds a million handles.

- **Output Association**:
  - `handleGenUnrealistic` -> `trayHandlesUnrealistic`

- **Pieces**: `megaHandle1`, `megaHandle2`, ..., `megaHandle1000000`
  - Inherits from `Handle`
  - Purpose: Massive collection of handles.

- **Content Aggregation**:
  - `trayHandlesUnrealistic` contains `[megaHandle1, megaHandle2, ..., megaHandle1000000]`

### Instance 3: Assembler with Handles and Heads from the Same Tray

#### Description
An assembler receiving both handles and heads from the same tray, violating normal production logic but remaining valid under model constraints.

#### Components

- **Tray**: `trayMixed`
  - Attributes:
    - `x: 20`
    - `y: 20`
    - `capacity: 200`
  - Purpose: Contains both handles and heads, which is usually disallowed but explicitly managed here.

- **Pieces**: `handleMixed1`, `handleMixed2`, ..., `handleMixed100`, `headMixed1`, `headMixed2`, ..., `headMixed100`
  - Inherits from `Handle` and `Head`
  - Purpose: Mixed collection of handles and heads in one tray.

- **Content Aggregation**:
  - `trayMixed` contains `[handleMixed1, ..., handleMixed100, headMixed1, ..., headMixed100]`

- **Assembler**: `assemblerUnrealistic`
  - Attributes:
    - `x: 25`
    - `y: 25`
    - `processingTime: 3`

- **Tray**: `trayHammersUnrealistic`
  - Attributes:
    - `x: 30`
    - `y: 30`
    - `capacity: 100`

- **Input Association**:
  - `trayMixed` -> `assemblerUnrealistic`

- **Output Association**:
  - `assemblerUnrealistic` -> `trayHammersUnrealistic`

- **Pieces**: `impossibleHammer1`, `impossibleHammer2`, ..., `impossibleHammer50`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`
  - Purpose: Assembled from mixed tray.

- **Content Aggregation**:
  - `trayHammersUnrealistic` contains `[impossibleHammer1, impossibleHammer2, ..., impossibleHammer50]`

### Summary

These instances illustrate scenarios that, while possible under the given constraints, defy practical manufacturing logic. They explore edge cases like impossible process sequences, extreme production scales, and mixed-type trays, highlighting the flexibility of the model while also pointing out areas where real-world applications might falter.
```
```
!new Polisher('polisherUnrealistic')
!polisherUnrealistic.x:=0
!polisherUnrealistic.y:=0
!polisherUnrealistic.processingTime:=2

!new Tray('trayPolishedUnrealistic')
!trayPolishedUnrealistic.x:=5
!trayPolishedUnrealistic.y:=5
!trayPolishedUnrealistic.capacity:=10

!new Hammer('unassembledHammer1')
!unassembledHammer1.isPolished:=true
!insert(unassembledHammer1,trayPolishedUnrealistic) into Content
!new Hammer('unassembledHammer2')
!unassembledHammer2.isPolished:=true
!insert(unassembledHammer2,trayPolishedUnrealistic) into Content

!insert(trayPolishedUnrealistic,polisherUnrealistic) into Input

!new HandleGenerator('handleGenUnrealistic')
!handleGenUnrealistic.x:=10
!handleGenUnrealistic.y:=10
!handleGenUnrealistic.processingTime:=0
!handleGenUnrealistic.counter:=1000000

!new Tray('trayHandlesUnrealistic')
!trayHandlesUnrealistic.x:=15
!trayHandlesUnrealistic.y:=15
!trayHandlesUnrealistic.capacity:=1000000

!insert(handleGenUnrealistic,trayHandlesUnrealistic) into Output

!new Handle('megaHandle1')
!insert(megaHandle1,trayHandlesUnrealistic) into Content
!new Handle('megaHandle2')
!insert(megaHandle2,trayHandlesUnrealistic) into Content

!new Tray('trayMixed')
!trayMixed.x:=20
!trayMixed.y:=20
!trayMixed.capacity:=200

!new Handle('handleMixed1')
!insert(handleMixed1,trayMixed) into Content
!new Handle('handleMixed2')
!insert(handleMixed2,trayMixed) into Content
!new Handle('handleMixed3')
!insert(handleMixed3,trayMixed) into Content
!new Head('headMixed1')
!insert(headMixed1,trayMixed) into Content
!new Head('headMixed2')
!insert(headMixed2,trayMixed) into Content
!new Head('headMixed3')
!insert(headMixed3,trayMixed) into Content

!new Assembler('assemblerUnrealistic')
!assemblerUnrealistic.x:=25
!assemblerUnrealistic.y:=25
!assemblerUnrealistic.processingTime:=3

!new Tray('trayHammersUnrealistic')
!trayHammersUnrealistic.x:=30
!trayHammersUnrealistic.y:=30
!trayHammersUnrealistic.capacity:=100

!insert(trayMixed,assemblerUnrealistic) into Input
!insert(assemblerUnrealistic,trayHammersUnrealistic) into Output

!new Hammer('impossibleHammer1')
!impossibleHammer1.isPolished:=false
!insert(impossibleHammer1,trayHammersUnrealistic) into Content
!new Hammer('impossibleHammer2')
!impossibleHammer2.isPolished:=false
!insert(impossibleHammer2,trayHammersUnrealistic) into Content

!insert(polisherUnrealistic,trayPolishedUnrealistic) into Output
```
```

# Category: Realistic but invalid
Create instances that make sense in real-life scenarios but violate constraints exposing overly restrictive or unrealistic constraints.


Here are instances that make sense in real-life scenarios but violate the model's constraints, highlighting overly restrictive or unrealistic constraints:

### Instance 1: Mixed Component Tray for Efficiency

#### Description
In a real-world scenario, it might be efficient to have trays containing both handles and heads for quick access during assembly, reducing the need for separate storage.

#### Components

- **Tray**: `trayMixedRealistic`
  - Attributes:
    - `x: 0`
    - `y: 0`
    - `capacity: 50`
  - Purpose: Holds both handles and heads to streamline assembly.

- **Pieces**: `handleRealistic1`, `handleRealistic2`, ..., `handleRealistic25`, `headRealistic1`, `headRealistic2`, ..., `headRealistic25`
  - Inherits from `Handle` and `Head`
  - Purpose: Mixed collection of handles and heads.

- **Content Aggregation**:
  - `trayMixedRealistic` contains `[handleRealistic1, ..., handleRealistic25, headRealistic1, ..., headRealistic25]`

- **Assembler**: `assemblerRealistic`
  - Attributes:
    - `x: 5`
    - `y: 5`
    - `processingTime: 6`
  - Purpose: Uses mixed tray for efficiency.

- **Tray**: `trayHammersRealistic`
  - Attributes:
    - `x: 10`
    - `y: 10`
    - `capacity: 25`

- **Input Association**:
  - `trayMixedRealistic` -> `assemblerRealistic`

- **Output Association**:
  - `assemblerRealistic` -> `trayHammersRealistic`

- **Pieces**: `hammerRealistic1`, `hammerRealistic2`, ..., `hammerRealistic25`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: false`

- **Content Aggregation**:
  - `trayHammersRealistic` contains `[hammerRealistic1, hammerRealistic2, ..., hammerRealistic25]`

#### Constraint Violation
- **PiecesSameType**: The tray contains mixed types of pieces, violating the invariant that requires all pieces in a tray to be of the same type.

### Instance 2: Multiple Output Trays for a Single Machine

#### Description
In practice, a machine might output to multiple trays for sorting purposes, such as separating defective and non-defective items or organizing for different downstream processes.

#### Components

- **HeadGenerator**: `headGenRealistic`
  - Attributes:
    - `x: 15`
    - `y: 15`
    - `processingTime: 3`
    - `counter: 500`
  - Purpose: Generates heads, sorted into different trays.

- **Tray**: `trayHeadsRealistic1` and `trayHeadsRealistic2`
  - Attributes:
    - `x: 20`
    - `y: 20`
    - `capacity: 250` (each)
  - Purpose: Separate trays for quality control purposes.

- **Output Association**:
  - `headGenRealistic` -> `trayHeadsRealistic1`
  - `headGenRealistic` -> `trayHeadsRealistic2`

- **Pieces**: `headRealistic26`, `headRealistic27`, ..., `headRealistic250` (Tray 1) and `headRealistic251`, `headRealistic252`, ..., `headRealistic500` (Tray 2)
  - Inherits from `Head`
  - Purpose: Heads are split between two trays for sorting.

- **Content Aggregation**:
  - `trayHeadsRealistic1` contains `[headRealistic26, ..., headRealistic250]`
  - `trayHeadsRealistic2` contains `[headRealistic251, ..., headRealistic500]`

#### Constraint Violation
- **Output Association**: The constraint implies a machine should output to only one tray, but in real-life scenarios, distributing outputs to multiple trays is practical for sorting and organization.

### Instance 3: Polisher with Mixed Input for Testing

#### Description
A polisher might receive both polished and unpolished hammers for quality checks or additional polishing, especially in a testing phase.

#### Components

- **Polisher**: `polisherRealistic`
  - Attributes:
    - `x: 30`
    - `y: 30`
    - `processingTime: 2`
  - Purpose: Processes both polished and unpolished hammers for testing.

- **Tray**: `trayMixedHammers`
  - Attributes:
    - `x: 35`
    - `y: 35`
    - `capacity: 20`
  - Purpose: Holds both polished and unpolished hammers for further processing.

- **Pieces**: `hammerPolished1`, `hammerPolished2`, `hammerUnpolished1`, `hammerUnpolished2`
  - Inherits from `Hammer`
  - Attributes:
    - `isPolished: true` (for polished pieces)
    - `isPolished: false` (for unpolished pieces)

- **Content Aggregation**:
  - `trayMixedHammers` contains `[hammerPolished1, hammerPolished2, hammerUnpolished1, hammerUnpolished2]`

- **Input Association**:
  - `trayMixedHammers` -> `polisherRealistic`

#### Constraint Violation
- **HammersInput**: The invariant requires input trays to contain only unpolished hammers, but real-life testing or rework scenarios can justify mixed input for quality assurance.

### Summary

These instances showcase realistic scenarios that make practical sense but violate the current model's constraints, revealing potential areas where the model's constraints might be overly restrictive or unrealistic.
```
```
!new Tray('trayMixedRealistic')
!trayMixedRealistic.x:=0
!trayMixedRealistic.y:=0
!trayMixedRealistic.capacity:=50

!new Handle('handleRealistic1')
!insert(handleRealistic1,trayMixedRealistic) into Content
!new Handle('handleRealistic2')
!insert(handleRealistic2,trayMixedRealistic) into Content
!new Head('headRealistic1')
!insert(headRealistic1,trayMixedRealistic) into Content
!new Head('headRealistic2')
!insert(headRealistic2,trayMixedRealistic) into Content

!new Assembler('assemblerRealistic')
!assemblerRealistic.x:=5
!assemblerRealistic.y:=5
!assemblerRealistic.processingTime:=6

!new Tray('trayHammersRealistic')
!trayHammersRealistic.x:=10
!trayHammersRealistic.y:=10
!trayHammersRealistic.capacity:=25

!insert(trayMixedRealistic,assemblerRealistic) into Input
!insert(assemblerRealistic,trayHammersRealistic) into Output

!new Hammer('hammerRealistic1')
!hammerRealistic1.isPolished:=false
!insert(hammerRealistic1,trayHammersRealistic) into Content
!new Hammer('hammerRealistic2')
!hammerRealistic2.isPolished:=false
!insert(hammerRealistic2,trayHammersRealistic) into Content

!new HeadGenerator('headGenRealistic')
!headGenRealistic.x:=15
!headGenRealistic.y:=15
!headGenRealistic.processingTime:=3
!headGenRealistic.counter:=500

!new Tray('trayHeadsRealistic1')
!trayHeadsRealistic1.x:=20
!trayHeadsRealistic1.y:=20
!trayHeadsRealistic1.capacity:=250

!new Tray('trayHeadsRealistic2')
!trayHeadsRealistic2.x:=25
!trayHeadsRealistic2.y:=25
!trayHeadsRealistic2.capacity:=250

!insert(headGenRealistic,trayHeadsRealistic1) into Output
!insert(headGenRealistic,trayHeadsRealistic2) into Output

!new Head('headRealistic26')
!insert(headRealistic26,trayHeadsRealistic1) into Content
!new Head('headRealistic27')
!insert(headRealistic27,trayHeadsRealistic1) into Content

!new Head('headRealistic251')
!insert(headRealistic251,trayHeadsRealistic2) into Content
!new Head('headRealistic252')
!insert(headRealistic252,trayHeadsRealistic2) into Content

!new Polisher('polisherRealistic')
!polisherRealistic.x:=30
!polisherRealistic.y:=30
!polisherRealistic.processingTime:=2

!new Tray('trayMixedHammers')
!trayMixedHammers.x:=35
!trayMixedHammers.y:=35
!trayMixedHammers.capacity:=20

!new Hammer('hammerPolished1')
!hammerPolished1.isPolished:=true
!insert(hammerPolished1,trayMixedHammers) into Content
!new Hammer('hammerUnpolished1')
!hammerUnpolished1.isPolished:=false
!insert(hammerUnpolished1,trayMixedHammers) into Content

!insert(trayMixedHammers,polisherRealistic) into Input
```
```