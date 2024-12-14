# Description

The AssemblyChain model represents a manufacturing process involving machines, trays, and pieces. The goal is to depict the interactions and constraints within an assembly line where different types of pieces are generated, assembled, and polished.

## Components

### Abstract Classes

- **Element**: The base class with attributes `x` and `y` (both Integers) representing positional coordinates.
- **Machine**: Inherits from `Element`, adds `processingTime` (Integer) attribute, and includes operations `start()` and `stop()`.
- **PieceGenerator**: Extends `Machine`, and serves as a base for specific piece generators.

### Concrete Classes

- **HandleGenerator** & **HeadGenerator**: Both inherit from `PieceGenerator`, with an additional attribute `counter` (Integer) that likely tracks the number of pieces generated.
- **Assembler**: Inherits from `Machine`, responsible for assembling pieces into a new product.
- **Polisher**: Inherits from `Machine`, responsible for polishing assembled products.
- **Tray**: Extends `Element`, includes a `capacity` (Integer) attribute indicating its storage limit.
- **Piece**: An abstract class representing a general piece in the assembly process.
- **Head** & **Handle**: Concrete classes extending `Piece`, representing specific piece types.
- **Hammer**: Extends `Piece`, with an additional attribute `isPolished` (Boolean) to indicate its polishing status.

# Relationships

- **Output Association**:
  - **Machine** (minimum 1, maximum many) can output to one **Tray**.
  - **Tray** receives input from one or more **Machines**.

- **Input Association**:
  - **Tray** (minimum 0, maximum many) can provide input to one or more **Machines**.
  - **Machine** can receive input from one or more **Trays**.

- **Content Aggregation**:
  - **Tray** contains one or more **Pieces**, and these pieces are ordered.

# Invariants

- **Tray Invariants**:
  - `AtLeastOneCell`: A tray must have a capacity greater than 0.
  - `PiecesSameType`: All pieces within a tray must be of the same type, specifically all Hammers, Handles, or Heads.

- **Assembler Invariants**:
  - `OneinputOfHandles`: The assembler must have at least one input tray containing only handles.
  - `OneinputOfHeads`: The assembler must have at least one input tray containing only heads.
  - `HammersOutput`: The assembler outputs trays containing only hammers.

- **Polisher Invariant**:
  - `HammersInput`: The polisher only accepts input trays containing hammers.

- **PieceGenerator Invariants**:
  - `HeadsOutput`: The head generator outputs only heads.
  - `HandlesOutput`: The handle generator outputs only handles.
  - `NoInput`: Piece generators do not have any input trays.

This model captures the constraints and operations necessary for the effective functioning of an assembly chain, ensuring that each component interacts in a specified and controlled manner.

# Category: Baseline Instances
## Baseline Instances

### Instance 1: Simple Assembly Line

#### Elements

1. **HandleGenerator_A**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 10
     - `y`: 20
     - `processingTime`: 5
     - `counter`: 100
   - **Relationships**: Outputs to `Tray_A`

2. **HeadGenerator_B**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 30
     - `y`: 20
     - `processingTime`: 7
     - `counter`: 150
   - **Relationships**: Outputs to `Tray_B`

3. **Assembler_C**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 50
     - `y`: 20
     - `processingTime`: 10
   - **Relationships**: 
     - Inputs from `Tray_A`, `Tray_B`
     - Outputs to `Tray_C`

4. **Polisher_D**
   - **Type**: Polisher
   - **Attributes**: 
     - `x`: 70
     - `y`: 20
     - `processingTime`: 8
   - **Relationships**: 
     - Inputs from `Tray_C`
     - Outputs to `Tray_D`

5. **Tray_A**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 15
     - `y`: 25
     - `capacity`: 50
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

6. **Tray_B**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 35
     - `y`: 25
     - `capacity`: 50
   - **Content**: [Head, Head, ..., Head] (up to capacity)

7. **Tray_C**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 55
     - `y`: 25
     - `capacity`: 20
   - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

8. **Tray_D**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 75
     - `y`: 25
     - `capacity`: 20
   - **Content**: [Hammer (isPolished: True), Hammer (isPolished: True), ..., Hammer] (up to capacity)

#### Pieces

1. **Handle**
   - **Type**: Handle
   - **Attributes**: 
     - Generic attributes since specific ones are not defined in the abstract class.

2. **Head**
   - **Type**: Head
   - **Attributes**: 
     - Generic attributes since specific ones are not defined in the abstract class.

3. **Hammer**
   - **Type**: Hammer
   - **Attributes**: 
     - `isPolished`: False (in `Tray_C`), True (in `Tray_D`)

### Regional and Structural Diversity

#### Instance 2: Advanced Assembly Line with Regional Differences

1. **HandleGenerator_E (Regional: Japan)**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 5
     - `y`: 15
     - `processingTime`: 4
     - `counter`: 200
   - **Relationships**: Outputs to `Tray_E`

2. **HeadGenerator_F (Regional: Germany)**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 25
     - `y`: 15
     - `processingTime`: 9
     - `counter`: 180
   - **Relationships**: Outputs to `Tray_F`

3. **Assembler_G**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 45
     - `y`: 15
     - `processingTime`: 12
   - **Relationships**: 
     - Inputs from `Tray_E`, `Tray_F`
     - Outputs to `Tray_G`

4. **Polisher_H**
   - **Type**: Polisher
   - **Attributes**: 
     - `x`: 65
     - `y`: 15
     - `processingTime`: 10
   - **Relationships**: 
     - Inputs from `Tray_G`
     - Outputs to `Tray_H`

5. **Tray_E**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 10
     - `y`: 20
     - `capacity`: 100
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

6. **Tray_F**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 30
     - `y`: 20
     - `capacity`: 100
   - **Content**: [Head, Head, ..., Head] (up to capacity)

7. **Tray_G**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 50
     - `y`: 20
     - `capacity`: 50
   - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

8. **Tray_H**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 70
     - `y`: 20
     - `capacity`: 50
   - **Content**: [Hammer (isPolished: True), Hammer (isPolished: True), ..., Hammer] (up to capacity)

This set of instances provides a comprehensive view of the AssemblyChain process, including both a simple and an advanced assembly line with regional diversity, adhering to the constraints and relationships defined in the model.
```
!new HandleGenerator('HandleGenerator_A')
!HandleGenerator_A.x:=10
!HandleGenerator_A.y:=20
!HandleGenerator_A.processingTime:=5
!HandleGenerator_A.counter:=100

!new Tray('Tray_A')
!Tray_A.x:=15
!Tray_A.y:=25
!Tray_A.capacity:=50

!insert(HandleGenerator_A, Tray_A) into Output

!new HeadGenerator('HeadGenerator_B')
!HeadGenerator_B.x:=30
!HeadGenerator_B.y:=20
!HeadGenerator_B.processingTime:=7
!HeadGenerator_B.counter:=150

!new Tray('Tray_B')
!Tray_B.x:=35
!Tray_B.y:=25
!Tray_B.capacity:=50

!insert(HeadGenerator_B, Tray_B) into Output

!new Assembler('Assembler_C')
!Assembler_C.x:=50
!Assembler_C.y:=20
!Assembler_C.processingTime:=10

!new Tray('Tray_C')
!Tray_C.x:=55
!Tray_C.y:=25
!Tray_C.capacity:=20

!insert(Tray_A, Assembler_C) into Input
!insert(Tray_B, Assembler_C) into Input
!insert(Assembler_C, Tray_C) into Output

!new Polisher('Polisher_D')
!Polisher_D.x:=70
!Polisher_D.y:=20
!Polisher_D.processingTime:=8

!new Tray('Tray_D')
!Tray_D.x:=75
!Tray_D.y:=25
!Tray_D.capacity:=20

!insert(Tray_C, Polisher_D) into Input
!insert(Polisher_D, Tray_D) into Output

!new HandleGenerator('HandleGenerator_E')
!HandleGenerator_E.x:=5
!HandleGenerator_E.y:=15
!HandleGenerator_E.processingTime:=4
!HandleGenerator_E.counter:=200

!new Tray('Tray_E')
!Tray_E.x:=10
!Tray_E.y:=20
!Tray_E.capacity:=100

!insert(HandleGenerator_E, Tray_E) into Output

!new HeadGenerator('HeadGenerator_F')
!HeadGenerator_F.x:=25
!HeadGenerator_F.y:=15
!HeadGenerator_F.processingTime:=9
!HeadGenerator_F.counter:=180

!new Tray('Tray_F')
!Tray_F.x:=30
!Tray_F.y:=20
!Tray_F.capacity:=100

!insert(HeadGenerator_F, Tray_F) into Output

!new Assembler('Assembler_G')
!Assembler_G.x:=45
!Assembler_G.y:=15
!Assembler_G.processingTime:=12

!new Tray('Tray_G')
!Tray_G.x:=50
!Tray_G.y:=20
!Tray_G.capacity:=50

!insert(Tray_E, Assembler_G) into Input
!insert(Tray_F, Assembler_G) into Input
!insert(Assembler_G, Tray_G) into Output

!new Polisher('Polisher_H')
!Polisher_H.x:=65
!Polisher_H.y:=15
!Polisher_H.processingTime:=10

!new Tray('Tray_H')
!Tray_H.x:=70
!Tray_H.y:=20
!Tray_H.capacity:=50

!insert(Tray_G, Polisher_H) into Input
!insert(Polisher_H, Tray_H) into Output
```

# Category: Boundary Instances
## Boundary Instances

### Instance 1: Minimum Capacity Tray

#### Elements

1. **HandleGenerator_Min**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 1
     - `y`: 1
     - `processingTime`: 3
     - `counter`: 0
   - **Relationships**: Outputs to `Tray_Min`

2. **Tray_Min**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 2
     - `y`: 1
     - `capacity`: 1
   - **Content**: [Handle]

This instance tests the `AtLeastOneCell` invariant by setting the tray capacity to its minimum valid value of 1.

### Instance 2: Maximum Input Trays for Assembler

#### Elements

1. **HandleGenerator_MaxIn1**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 3
     - `y`: 3
     - `processingTime`: 6
     - `counter`: 300
   - **Relationships**: Outputs to `Tray_Handles1`

2. **HandleGenerator_MaxIn2**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 4
     - `y`: 3
     - `processingTime`: 6
     - `counter`: 300
   - **Relationships**: Outputs to `Tray_Handles2`

3. **HeadGenerator_MaxIn**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 5
     - `y`: 3
     - `processingTime`: 5
     - `counter`: 300
   - **Relationships**: Outputs to `Tray_Heads`

4. **Assembler_MaxIn**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 6
     - `y`: 3
     - `processingTime`: 8
   - **Relationships**: 
     - Inputs from `Tray_Handles1`, `Tray_Handles2`, `Tray_Heads`
     - Outputs to `Tray_Hammers_MaxIn`

5. **Tray_Handles1**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 3
     - `y`: 4
     - `capacity`: 100
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

6. **Tray_Handles2**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 4
     - `y`: 4
     - `capacity`: 100
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

7. **Tray_Heads**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 5
     - `y`: 4
     - `capacity`: 100
   - **Content**: [Head, Head, ..., Head] (up to capacity)

8. **Tray_Hammers_MaxIn**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 6
     - `y`: 4
     - `capacity`: 50
   - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

This instance tests the maximum number of input trays for the assembler, ensuring it can handle multiple trays of handles and one of heads.

### Instance 3: Empty Optional Associations

#### Elements

1. **Polisher_Empty**
   - **Type**: Polisher
   - **Attributes**: 
     - `x`: 7
     - `y`: 5
     - `processingTime`: 9
   - **Relationships**: 
     - Inputs from no trays initially.
     - Outputs to `Tray_EmptyOutput`

2. **Tray_EmptyOutput**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 8
     - `y`: 5
     - `capacity`: 1
   - **Content**: []

This instance tests the scenario where a polisher initially has no input trays, representing an optional association.

### Instance 4: Extreme Values for Coordinates

#### Elements

1. **HandleGenerator_Extreme**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 999999
     - `y`: -999999
     - `processingTime`: 2
     - `counter`: 500
   - **Relationships**: Outputs to `Tray_Extreme`

2. **Tray_Extreme**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 999999
     - `y`: -999999
     - `capacity`: 10
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

This instance tests the handling of extreme values for the positional coordinates `x` and `y`.

These instances together explore the boundaries of the AssemblyChain model by testing minimum and maximum constraints, optional associations, and extreme attribute values.
```
!new HandleGenerator('HandleGenerator_Min')
!HandleGenerator_Min.x:=1
!HandleGenerator_Min.y:=1
!HandleGenerator_Min.processingTime:=3
!HandleGenerator_Min.counter:=0

!new Tray('Tray_Min')
!Tray_Min.x:=2
!Tray_Min.y:=1
!Tray_Min.capacity:=1

!insert(HandleGenerator_Min, Tray_Min) into Output

!new HandleGenerator('HandleGenerator_MaxIn1')
!HandleGenerator_MaxIn1.x:=3
!HandleGenerator_MaxIn1.y:=3
!HandleGenerator_MaxIn1.processingTime:=6
!HandleGenerator_MaxIn1.counter:=300

!new Tray('Tray_Handles1')
!Tray_Handles1.x:=3
!Tray_Handles1.y:=4
!Tray_Handles1.capacity:=100

!insert(HandleGenerator_MaxIn1, Tray_Handles1) into Output

!new HandleGenerator('HandleGenerator_MaxIn2')
!HandleGenerator_MaxIn2.x:=4
!HandleGenerator_MaxIn2.y:=3
!HandleGenerator_MaxIn2.processingTime:=6
!HandleGenerator_MaxIn2.counter:=300

!new Tray('Tray_Handles2')
!Tray_Handles2.x:=4
!Tray_Handles2.y:=4
!Tray_Handles2.capacity:=100

!insert(HandleGenerator_MaxIn2, Tray_Handles2) into Output

!new HeadGenerator('HeadGenerator_MaxIn')
!HeadGenerator_MaxIn.x:=5
!HeadGenerator_MaxIn.y:=3
!HeadGenerator_MaxIn.processingTime:=5
!HeadGenerator_MaxIn.counter:=300

!new Tray('Tray_Heads')
!Tray_Heads.x:=5
!Tray_Heads.y:=4
!Tray_Heads.capacity:=100

!insert(HeadGenerator_MaxIn, Tray_Heads) into Output

!new Assembler('Assembler_MaxIn')
!Assembler_MaxIn.x:=6
!Assembler_MaxIn.y:=3
!Assembler_MaxIn.processingTime:=8

!new Tray('Tray_Hammers_MaxIn')
!Tray_Hammers_MaxIn.x:=6
!Tray_Hammers_MaxIn.y:=4
!Tray_Hammers_MaxIn.capacity:=50

!insert(Tray_Handles1, Assembler_MaxIn) into Input
!insert(Tray_Handles2, Assembler_MaxIn) into Input
!insert(Tray_Heads, Assembler_MaxIn) into Input
!insert(Assembler_MaxIn, Tray_Hammers_MaxIn) into Output

!new Polisher('Polisher_Empty')
!Polisher_Empty.x:=7
!Polisher_Empty.y:=5
!Polisher_Empty.processingTime:=9

!new Tray('Tray_EmptyOutput')
!Tray_EmptyOutput.x:=8
!Tray_EmptyOutput.y:=5
!Tray_EmptyOutput.capacity:=1

!insert(Polisher_Empty, Tray_EmptyOutput) into Output

!new HandleGenerator('HandleGenerator_Extreme')
!HandleGenerator_Extreme.x:=999999
!HandleGenerator_Extreme.y:=-999999
!HandleGenerator_Extreme.processingTime:=2
!HandleGenerator_Extreme.counter:=500

!new Tray('Tray_Extreme')
!Tray_Extreme.x:=999999
!Tray_Extreme.y:=-999999
!Tray_Extreme.capacity:=10

!insert(HandleGenerator_Extreme, Tray_Extreme) into Output
```

# Category: Complex Instances
## Complex Instances

### Instance 1: Multi-Stage Assembly Line with Feedback Loop

#### Elements

1. **HandleGenerator_Complex1**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 10
     - `y`: 10
     - `processingTime`: 5
     - `counter`: 500
   - **Relationships**: Outputs to `Tray_Complex_Handles1`

2. **HandleGenerator_Complex2**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 15
     - `y`: 10
     - `processingTime`: 5
     - `counter`: 500
   - **Relationships**: Outputs to `Tray_Complex_Handles2`

3. **HeadGenerator_Complex**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 20
     - `y`: 10
     - `processingTime`: 7
     - `counter`: 800
   - **Relationships**: Outputs to `Tray_Complex_Heads`

4. **Assembler_Complex**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 30
     - `y`: 10
     - `processingTime`: 12
   - **Relationships**: 
     - Inputs from `Tray_Complex_Handles1`, `Tray_Complex_Handles2`, `Tray_Complex_Heads`
     - Outputs to `Tray_Complex_Hammers`

5. **Polisher_Complex**
   - **Type**: Polisher
   - **Attributes**: 
     - `x`: 40
     - `y`: 10
     - `processingTime`: 10
   - **Relationships**: 
     - Inputs from `Tray_Complex_Hammers`
     - Outputs to `Tray_Complex_PolishedHammers`

6. **Tray_Complex_Handles1**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 11
     - `y`: 11
     - `capacity`: 200
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

7. **Tray_Complex_Handles2**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 16
     - `y`: 11
     - `capacity`: 200
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

8. **Tray_Complex_Heads**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 21
     - `y`: 11
     - `capacity`: 200
   - **Content**: [Head, Head, ..., Head] (up to capacity)

9. **Tray_Complex_Hammers**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 31
     - `y`: 11
     - `capacity`: 100
   - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

10. **Tray_Complex_PolishedHammers**
    - **Type**: Tray
    - **Attributes**: 
      - `x`: 41
      - `y`: 11
      - `capacity`: 100
    - **Content**: [Hammer (isPolished: True), Hammer (isPolished: True), ..., Hammer] (up to capacity)

#### Interaction and Complexity

- This instance includes two HandleGenerators feeding the Assembler, providing redundancy and ensuring continuous production even if one generator is down.
- The Assembler receives input from two different handle trays and one head tray, adhering to its invariant requirements.
- The Polisher receives unpolished hammers and outputs polished hammers, maintaining its input invariant.
- The system is designed to create a feedback loop where polished hammers could potentially be sent back for further processing or quality checks (not shown here but implied as a future step).

### Instance 2: Parallel Assembly Lines with Shared Resources

#### Elements

1. **HandleGenerator_Parallel**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 5
     - `y`: 20
     - `processingTime`: 4
     - `counter`: 600
   - **Relationships**: Outputs to both `Tray_Parallel_Handles1` and `Tray_Parallel_Handles2`

2. **HeadGenerator_Parallel1**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 25
     - `y`: 20
     - `processingTime`: 6
     - `counter`: 700
   - **Relationships**: Outputs to `Tray_Parallel_Heads1`

3. **HeadGenerator_Parallel2**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 35
     - `y`: 20
     - `processingTime`: 6
     - `counter`: 700
   - **Relationships**: Outputs to `Tray_Parallel_Heads2`

4. **Assembler_Parallel1**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 45
     - `y`: 20
     - `processingTime`: 11
   - **Relationships**: 
     - Inputs from `Tray_Parallel_Handles1`, `Tray_Parallel_Heads1`
     - Outputs to `Tray_Parallel_Hammers1`

5. **Assembler_Parallel2**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 55
     - `y`: 20
     - `processingTime`: 11
   - **Relationships**: 
     - Inputs from `Tray_Parallel_Handles2`, `Tray_Parallel_Heads2`
     - Outputs to `Tray_Parallel_Hammers2`

6. **Tray_Parallel_Handles1**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 6
     - `y`: 21
     - `capacity`: 150
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

7. **Tray_Parallel_Handles2**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 6
     - `y`: 22
     - `capacity`: 150
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

8. **Tray_Parallel_Heads1**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 26
     - `y`: 21
     - `capacity`: 150
   - **Content**: [Head, Head, ..., Head] (up to capacity)

9. **Tray_Parallel_Heads2**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 36
     - `y`: 22
     - `capacity`: 150
   - **Content**: [Head, Head, ..., Head] (up to capacity)

10. **Tray_Parallel_Hammers1**
    - **Type**: Tray
    - **Attributes**: 
      - `x`: 46
      - `y`: 21
      - `capacity`: 75
    - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

11. **Tray_Parallel_Hammers2**
    - **Type**: Tray
    - **Attributes**: 
      - `x`: 56
      - `y`: 22
      - `capacity`: 75
    - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

#### Interaction and Complexity

- Two parallel assembly lines are operating with shared resources, such as a common HandleGenerator providing handles to both lines.
- Each line independently processes its own heads and handles to produce hammers, demonstrating scalability and parallel processing capabilities.
- This setup tests the model's ability to handle shared resources and multiple independent production lines simultaneously.

These complex instances illustrate the AssemblyChain model's flexibility and capacity to manage intricate interactions and constraints, showcasing real-world manufacturing scenarios with redundancy, parallelism, and feedback loops.
```
!new HandleGenerator('HandleGenerator_Complex1')
!HandleGenerator_Complex1.x:=10
!HandleGenerator_Complex1.y:=10
!HandleGenerator_Complex1.processingTime:=5
!HandleGenerator_Complex1.counter:=500

!new HandleGenerator('HandleGenerator_Complex2')
!HandleGenerator_Complex2.x:=15
!HandleGenerator_Complex2.y:=10
!HandleGenerator_Complex2.processingTime:=5
!HandleGenerator_Complex2.counter:=500

!new HeadGenerator('HeadGenerator_Complex')
!HeadGenerator_Complex.x:=20
!HeadGenerator_Complex.y:=10
!HeadGenerator_Complex.processingTime:=7
!HeadGenerator_Complex.counter:=800

!new Tray('Tray_Complex_Handles1')
!Tray_Complex_Handles1.x:=11
!Tray_Complex_Handles1.y:=11
!Tray_Complex_Handles1.capacity:=200

!new Tray('Tray_Complex_Handles2')
!Tray_Complex_Handles2.x:=16
!Tray_Complex_Handles2.y:=11
!Tray_Complex_Handles2.capacity:=200

!new Tray('Tray_Complex_Heads')
!Tray_Complex_Heads.x:=21
!Tray_Complex_Heads.y:=11
!Tray_Complex_Heads.capacity:=200

!new Assembler('Assembler_Complex')
!Assembler_Complex.x:=30
!Assembler_Complex.y:=10
!Assembler_Complex.processingTime:=12

!new Tray('Tray_Complex_Hammers')
!Tray_Complex_Hammers.x:=31
!Tray_Complex_Hammers.y:=11
!Tray_Complex_Hammers.capacity:=100

!new Polisher('Polisher_Complex')
!Polisher_Complex.x:=40
!Polisher_Complex.y:=10
!Polisher_Complex.processingTime:=10

!new Tray('Tray_Complex_PolishedHammers')
!Tray_Complex_PolishedHammers.x:=41
!Tray_Complex_PolishedHammers.y:=11
!Tray_Complex_PolishedHammers.capacity:=100

!insert(HandleGenerator_Complex1, Tray_Complex_Handles1) into Output
!insert(HandleGenerator_Complex2, Tray_Complex_Handles2) into Output
!insert(HeadGenerator_Complex, Tray_Complex_Heads) into Output

!insert(Tray_Complex_Handles1, Assembler_Complex) into Input
!insert(Tray_Complex_Handles2, Assembler_Complex) into Input
!insert(Tray_Complex_Heads, Assembler_Complex) into Input
!insert(Assembler_Complex, Tray_Complex_Hammers) into Output

!insert(Tray_Complex_Hammers, Polisher_Complex) into Input
!insert(Polisher_Complex, Tray_Complex_PolishedHammers) into Output

!new HandleGenerator('HandleGenerator_Parallel')
!HandleGenerator_Parallel.x:=5
!HandleGenerator_Parallel.y:=20
!HandleGenerator_Parallel.processingTime:=4
!HandleGenerator_Parallel.counter:=600

!new Tray('Tray_Parallel_Handles1')
!Tray_Parallel_Handles1.x:=6
!Tray_Parallel_Handles1.y:=21
!Tray_Parallel_Handles1.capacity:=150

!insert(HandleGenerator_Parallel, Tray_Parallel_Handles1) into Output

!new HeadGenerator('HeadGenerator_Parallel1')
!HeadGenerator_Parallel1.x:=25
!HeadGenerator_Parallel1.y:=20
!HeadGenerator_Parallel1.processingTime:=6
!HeadGenerator_Parallel1.counter:=700

!new Tray('Tray_Parallel_Heads1')
!Tray_Parallel_Heads1.x:=26
!Tray_Parallel_Heads1.y:=21
!Tray_Parallel_Heads1.capacity:=150

!insert(HeadGenerator_Parallel1, Tray_Parallel_Heads1) into Output

!new Assembler('Assembler_Parallel1')
!Assembler_Parallel1.x:=45
!Assembler_Parallel1.y:=20
!Assembler_Parallel1.processingTime:=11

!new Tray('Tray_Parallel_Hammers1')
!Tray_Parallel_Hammers1.x:=46
!Tray_Parallel_Hammers1.y:=21
!Tray_Parallel_Hammers1.capacity:=75

!insert(Tray_Parallel_Handles1, Assembler_Parallel1) into Input
!insert(Tray_Parallel_Heads1, Assembler_Parallel1) into Input
!insert(Assembler_Parallel1, Tray_Parallel_Hammers1) into Output

!new Tray('Tray_Parallel_Handles2')
!Tray_Parallel_Handles2.x:=6
!Tray_Parallel_Handles2.y:=22
!Tray_Parallel_Handles2.capacity:=150

!insert(HandleGenerator_Parallel, Tray_Parallel_Handles2) into Output

!new HeadGenerator('HeadGenerator_Parallel2')
!HeadGenerator_Parallel2.x:=35
!HeadGenerator_Parallel2.y:=20
!HeadGenerator_Parallel2.processingTime:=6
!HeadGenerator_Parallel2.counter:=700

!new Tray('Tray_Parallel_Heads2')
!Tray_Parallel_Heads2.x:=36
!Tray_Parallel_Heads2.y:=22
!Tray_Parallel_Heads2.capacity:=150

!insert(HeadGenerator_Parallel2, Tray_Parallel_Heads2) into Output

!new Assembler('Assembler_Parallel2')
!Assembler_Parallel2.x:=55
!Assembler_Parallel2.y:=20
!Assembler_Parallel2.processingTime:=11

!new Tray('Tray_Parallel_Hammers2')
!Tray_Parallel_Hammers2.x:=56
!Tray_Parallel_Hammers2.y:=22
!Tray_Parallel_Hammers2.capacity:=75

!insert(Tray_Parallel_Handles2, Assembler_Parallel2) into Input
!insert(Tray_Parallel_Heads2, Assembler_Parallel2) into Input
!insert(Assembler_Parallel2, Tray_Parallel_Hammers2) into Output
```

# Category: Unrealistc but valid
## Unrealistic but Valid Instances

### Instance 1: Infinite Loop of Generators and Assemblers

#### Elements

1. **HandleGenerator_Loop**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 100
     - `y`: 100
     - `processingTime`: 1
     - `counter`: 0  (never increments due to infinite loop)
   - **Relationships**: Outputs to `Tray_Loop_Handles`

2. **HeadGenerator_Loop**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 110
     - `y`: 100
     - `processingTime`: 1
     - `counter`: 0  (never increments due to infinite loop)
   - **Relationships**: Outputs to `Tray_Loop_Heads`

3. **Assembler_Loop**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 120
     - `y`: 100
     - `processingTime`: 1
   - **Relationships**: 
     - Inputs from `Tray_Loop_Handles`, `Tray_Loop_Heads`
     - Outputs to `Tray_Loop_Hammers`

4. **Tray_Loop_Handles**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 105
     - `y`: 105
     - `capacity`: 10
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

5. **Tray_Loop_Heads**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 115
     - `y`: 105
     - `capacity`: 10
   - **Content**: [Head, Head, ..., Head] (up to capacity)

6. **Tray_Loop_Hammers**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 125
     - `y`: 105
     - `capacity`: 10
   - **Content**: [Hammer, Hammer, ..., Hammer] (up to capacity)

#### Unrealistic Scenario

- Both generators and the assembler operate in a rapid, infinite loop where no real production occurs because the `counter` never increases, despite continuous operations.
- Although syntactically valid, such perpetual operation without actual output progress is impractical.

### Instance 2: Polisher Without Initial Input

#### Elements

1. **Polisher_NoInput**
   - **Type**: Polisher
   - **Attributes**: 
     - `x`: 200
     - `y`: 200
     - `processingTime`: 5
   - **Relationships**: 
     - Inputs from no trays initially
     - Outputs to `Tray_NoInput_Polished`

2. **Tray_NoInput_Polished**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 210
     - `y`: 205
     - `capacity`: 0
   - **Content**: []

#### Unrealistic Scenario

- The polisher operates with no input trays, theoretically adhering to the multiplicity constraints but not contributing to the production process.
- Empty input leads to an empty output tray, showcasing a scenario where operational setup exists without functioning input.

### Instance 3: Generator with Overlapping Coordinates

#### Elements

1. **HandleGenerator_Overlap**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 300
     - `y`: 300
     - `processingTime`: 2
     - `counter`: 100
   - **Relationships**: Outputs to `Tray_Overlap_Handles`

2. **HeadGenerator_Overlap**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 300
     - `y`: 300
     - `processingTime`: 2
     - `counter`: 100
   - **Relationships**: Outputs to `Tray_Overlap_Heads`

3. **Tray_Overlap_Handles**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 305
     - `y`: 305
     - `capacity`: 50
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

4. **Tray_Overlap_Heads**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 305
     - `y`: 305
     - `capacity`: 50
   - **Content**: [Head, Head, ..., Head] (up to capacity)

#### Unrealistic Scenario

- Both generators occupy the exact same coordinates, simulating an impractical physical overlap.
- While this configuration is syntactically valid, real-world constraints would prevent machines from existing in the same space without collision.

### Instance 4: Machine with Maximum Processing Time and Zero Output

#### Elements

1. **Assembler_MaxTime**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 500
     - `y`: 500
     - `processingTime`: 999999
   - **Relationships**: 
     - Inputs from `Tray_MaxTime_Handles`, `Tray_MaxTime_Heads`
     - Outputs to `Tray_MaxTime_Hammers`

2. **Tray_MaxTime_Handles**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 505
     - `y`: 505
     - `capacity`: 5
   - **Content**: [Handle, Handle, Handle, Handle, Handle]

3. **Tray_MaxTime_Heads**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 510
     - `y`: 505
     - `capacity`: 5
   - **Content**: [Head, Head, Head, Head, Head]

4. **Tray_MaxTime_Hammers**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 515
     - `y`: 505
     - `capacity`: 5
   - **Content**: [] (empty due to excessive processing time)

#### Unrealistic Scenario

- The assembler has an extremely high processing time, making it unlikely to complete any assembly within practical time constraints.
- Though the setup is valid, the operation is effectively non-functional, with no output generated due to the unrealistic processing duration.

These instances demonstrate that while the AssemblyChain model can accommodate these configurations without violating syntactic or multiplicity constraints, they represent scenarios that are either impractical or impossible in real-world manufacturing settings.
```
!new HandleGenerator('HandleGenerator_Loop')
!HandleGenerator_Loop.x:=100
!HandleGenerator_Loop.y:=100
!HandleGenerator_Loop.processingTime:=1
!HandleGenerator_Loop.counter:=0

!new HeadGenerator('HeadGenerator_Loop')
!HeadGenerator_Loop.x:=110
!HeadGenerator_Loop.y:=100
!HeadGenerator_Loop.processingTime:=1
!HeadGenerator_Loop.counter:=0

!new Tray('Tray_Loop_Handles')
!Tray_Loop_Handles.x:=105
!Tray_Loop_Handles.y:=105
!Tray_Loop_Handles.capacity:=10

!new Tray('Tray_Loop_Heads')
!Tray_Loop_Heads.x:=115
!Tray_Loop_Heads.y:=105
!Tray_Loop_Heads.capacity:=10

!new Assembler('Assembler_Loop')
!Assembler_Loop.x:=120
!Assembler_Loop.y:=100
!Assembler_Loop.processingTime:=1

!new Tray('Tray_Loop_Hammers')
!Tray_Loop_Hammers.x:=125
!Tray_Loop_Hammers.y:=105
!Tray_Loop_Hammers.capacity:=10

!insert(HandleGenerator_Loop, Tray_Loop_Handles) into Output
!insert(HeadGenerator_Loop, Tray_Loop_Heads) into Output

!insert(Tray_Loop_Handles, Assembler_Loop) into Input
!insert(Tray_Loop_Heads, Assembler_Loop) into Input
!insert(Assembler_Loop, Tray_Loop_Hammers) into Output

!new Polisher('Polisher_NoInput')
!Polisher_NoInput.x:=200
!Polisher_NoInput.y:=200
!Polisher_NoInput.processingTime:=5

!new Tray('Tray_NoInput_Polished')
!Tray_NoInput_Polished.x:=210
!Tray_NoInput_Polished.y:=205
!Tray_NoInput_Polished.capacity:=1

!insert(Polisher_NoInput, Tray_NoInput_Polished) into Output

!new HandleGenerator('HandleGenerator_Overlap')
!HandleGenerator_Overlap.x:=300
!HandleGenerator_Overlap.y:=300
!HandleGenerator_Overlap.processingTime:=2
!HandleGenerator_Overlap.counter:=100

!new HeadGenerator('HeadGenerator_Overlap')
!HeadGenerator_Overlap.x:=300
!HeadGenerator_Overlap.y:=300
!HeadGenerator_Overlap.processingTime:=2
!HeadGenerator_Overlap.counter:=100

!new Tray('Tray_Overlap_Handles')
!Tray_Overlap_Handles.x:=305
!Tray_Overlap_Handles.y:=305
!Tray_Overlap_Handles.capacity:=50

!new Tray('Tray_Overlap_Heads')
!Tray_Overlap_Heads.x:=305
!Tray_Overlap_Heads.y:=305
!Tray_Overlap_Heads.capacity:=50

!insert(HandleGenerator_Overlap, Tray_Overlap_Handles) into Output
!insert(HeadGenerator_Overlap, Tray_Overlap_Heads) into Output

!new Assembler('Assembler_MaxTime')
!Assembler_MaxTime.x:=500
!Assembler_MaxTime.y:=500
!Assembler_MaxTime.processingTime:=999999

!new Tray('Tray_MaxTime_Handles')
!Tray_MaxTime_Handles.x:=505
!Tray_MaxTime_Handles.y:=505
!Tray_MaxTime_Handles.capacity:=5

!new Tray('Tray_MaxTime_Heads')
!Tray_MaxTime_Heads.x:=510
!Tray_MaxTime_Heads.y:=505
!Tray_MaxTime_Heads.capacity:=5

!new Tray('Tray_MaxTime_Hammers')
!Tray_MaxTime_Hammers.x:=515
!Tray_MaxTime_Hammers.y:=505
!Tray_MaxTime_Hammers.capacity:=5

!insert(Tray_MaxTime_Handles, Assembler_MaxTime) into Input
!insert(Tray_MaxTime_Heads, Assembler_MaxTime) into Input
!insert(Assembler_MaxTime, Tray_MaxTime_Hammers) into Output
```

# Category: Realistic but invalid
## Realistic but Invalid Instances

### Instance 1: Mixed Pieces in a Tray

#### Elements

1. **HandleGenerator_Realistic**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 5
     - `y`: 5
     - `processingTime`: 4
     - `counter`: 100
   - **Relationships**: Outputs to `Tray_Mixed`

2. **HeadGenerator_Realistic**
   - **Type**: HeadGenerator
   - **Attributes**: 
     - `x`: 10
     - `y`: 5
     - `processingTime`: 4
     - `counter`: 100
   - **Relationships**: Outputs to `Tray_Mixed`

3. **Tray_Mixed**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 7
     - `y`: 6
     - `capacity`: 50
   - **Content**: [Handle, Head, Handle, Head, ...]

#### Violation

- **Constraint Violated**: `PiecesSameType`
- **Realistic Scenario**: In real-life, it might be practical to store different types of pieces temporarily in the same tray for space optimization or mixed product assembly, but the model restricts mixed content in trays.

### Instance 2: Assembler with Only One Type of Input

#### Elements

1. **HandleGenerator_SingleInput**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 15
     - `y`: 15
     - `processingTime`: 3
     - `counter`: 200
   - **Relationships**: Outputs to `Tray_SingleInput`

2. **Assembler_SingleInput**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 20
     - `y`: 15
     - `processingTime`: 6
   - **Relationships**: 
     - Inputs from `Tray_SingleInput`
     - Outputs to `Tray_InvalidHammers`

3. **Tray_SingleInput**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 17
     - `y`: 16
     - `capacity`: 50
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

4. **Tray_InvalidHammers**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 25
     - `y`: 15
     - `capacity`: 10
   - **Content**: [] (no output due to invalid input)

#### Violation

- **Constraint Violated**: `OneinputOfHeads`
- **Realistic Scenario**: Sometimes, production lines might temporarily switch to assembling only partial components (e.g., only handles) due to supply shortages or specific orders, but the model disallows this.

### Instance 3: Polisher with Mixed Input

#### Elements

1. **Assembler_MixedInput**
   - **Type**: Assembler
   - **Attributes**: 
     - `x`: 30
     - `y`: 30
     - `processingTime`: 8
   - **Relationships**: 
     - Inputs from `Tray_Handles_A`, `Tray_Heads_A`
     - Outputs to `Tray_MixedHammers`

2. **Tray_Handles_A**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 31
     - `y`: 31
     - `capacity`: 25
   - **Content**: [Handle, Handle, ..., Handle] (up to capacity)

3. **Tray_Heads_A**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 32
     - `y`: 31
     - `capacity`: 25
   - **Content**: [Head, Head, ..., Head] (up to capacity)

4. **Tray_MixedHammers**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 35
     - `y`: 31
     - `capacity`: 20
   - **Content**: [Hammer (isPolished: False), Head, Handle]

5. **Polisher_MixedInput**
   - **Type**: Polisher
   - **Attributes**: 
     - `x`: 40
     - `y`: 30
     - `processingTime`: 7
   - **Relationships**: 
     - Inputs from `Tray_MixedHammers`
     - Outputs to `Tray_PolishedMixed`

6. **Tray_PolishedMixed**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 45
     - `y`: 30
     - `capacity`: 20
   - **Content**: [] (no output due to invalid input)

#### Violation

- **Constraint Violated**: `HammersInput`
- **Realistic Scenario**: In practice, a polisher might need to handle mixed or incorrectly sorted inputs due to errors in previous stages, but the model enforces strict input content.

### Instance 4: PieceGenerator with Input

#### Elements

1. **HandleGenerator_WithInput**
   - **Type**: HandleGenerator
   - **Attributes**: 
     - `x`: 50
     - `y`: 50
     - `processingTime`: 4
     - `counter`: 150
   - **Relationships**: 
     - Inputs from `Tray_UnnecessaryInput`
     - Outputs to `Tray_Handles_Output`

2. **Tray_UnnecessaryInput**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 51
     - `y`: 51
     - `capacity`: 30
   - **Content**: [Head, Head, Head]

3. **Tray_Handles_Output**
   - **Type**: Tray
   - **Attributes**: 
     - `x`: 55
     - `y`: 50
     - `capacity`: 50
   - **Content**: [Handle, Handle, ...]

#### Violation

- **Constraint Violated**: `NoInput`
- **Realistic Scenario**: Although piece generators are typically independent, they might sometimes need input for calibration or quality checks.

These instances highlight scenarios where real-world logic might dictate a certain configuration, but the model's constraints prevent it, suggesting potential areas for model refinement or flexibility.
```
!new HandleGenerator('HandleGenerator_Realistic')
!HandleGenerator_Realistic.x:=5
!HandleGenerator_Realistic.y:=5
!HandleGenerator_Realistic.processingTime:=4
!HandleGenerator_Realistic.counter:=100

!new HeadGenerator('HeadGenerator_Realistic')
!HeadGenerator_Realistic.x:=10
!HeadGenerator_Realistic.y:=5
!HeadGenerator_Realistic.processingTime:=4
!HeadGenerator_Realistic.counter:=100

!new Tray('Tray_Mixed')
!Tray_Mixed.x:=7
!Tray_Mixed.y:=6
!Tray_Mixed.capacity:=50

!insert(HandleGenerator_Realistic, Tray_Mixed) into Output
!insert(HeadGenerator_Realistic, Tray_Mixed) into Output

!new HandleGenerator('HandleGenerator_SingleInput')
!HandleGenerator_SingleInput.x:=15
!HandleGenerator_SingleInput.y:=15
!HandleGenerator_SingleInput.processingTime:=3
!HandleGenerator_SingleInput.counter:=200

!new Tray('Tray_SingleInput')
!Tray_SingleInput.x:=17
!Tray_SingleInput.y:=16
!Tray_SingleInput.capacity:=50

!new Assembler('Assembler_SingleInput')
!Assembler_SingleInput.x:=20
!Assembler_SingleInput.y:=15
!Assembler_SingleInput.processingTime:=6

!new Tray('Tray_InvalidHammers')
!Tray_InvalidHammers.x:=25
!Tray_InvalidHammers.y:=15
!Tray_InvalidHammers.capacity:=10

!insert(HandleGenerator_SingleInput, Tray_SingleInput) into Output
!insert(Tray_SingleInput, Assembler_SingleInput) into Input
!insert(Assembler_SingleInput, Tray_InvalidHammers) into Output

!new Assembler('Assembler_MixedInput')
!Assembler_MixedInput.x:=30
!Assembler_MixedInput.y:=30
!Assembler_MixedInput.processingTime:=8

!new Tray('Tray_Handles_A')
!Tray_Handles_A.x:=31
!Tray_Handles_A.y:=31
!Tray_Handles_A.capacity:=25

!new Tray('Tray_Heads_A')
!Tray_Heads_A.x:=32
!Tray_Heads_A.y:=31
!Tray_Heads_A.capacity:=25

!new Tray('Tray_MixedHammers')
!Tray_MixedHammers.x:=35
!Tray_MixedHammers.y:=31
!Tray_MixedHammers.capacity:=20

!new Polisher('Polisher_MixedInput')
!Polisher_MixedInput.x:=40
!Polisher_MixedInput.y:=30
!Polisher_MixedInput.processingTime:=7

!new Tray('Tray_PolishedMixed')
!Tray_PolishedMixed.x:=45
!Tray_PolishedMixed.y:=30
!Tray_PolishedMixed.capacity:=20

!insert(Tray_Handles_A, Assembler_MixedInput) into Input
!insert(Tray_Heads_A, Assembler_MixedInput) into Input
!insert(Assembler_MixedInput, Tray_MixedHammers) into Output
!insert(Tray_MixedHammers, Polisher_MixedInput) into Input
!insert(Polisher_MixedInput, Tray_PolishedMixed) into Output

!new HandleGenerator('HandleGenerator_WithInput')
!HandleGenerator_WithInput.x:=50
!HandleGenerator_WithInput.y:=50
!HandleGenerator_WithInput.processingTime:=4
!HandleGenerator_WithInput.counter:=150

!new Tray('Tray_UnnecessaryInput')
!Tray_UnnecessaryInput.x:=51
!Tray_UnnecessaryInput.y:=51
!Tray_UnnecessaryInput.capacity:=30

!new Tray('Tray_Handles_Output')
!Tray_Handles_Output.x:=55
!Tray_Handles_Output.y:=50
!Tray_Handles_Output.capacity:=50

!insert(Tray_UnnecessaryInput, HandleGenerator_WithInput) into Input
!insert(HandleGenerator_WithInput, Tray_Handles_Output) into Output
```