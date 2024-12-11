# Description

The model "AssemblyChain" represents a conceptual framework for an assembly line process, where various machines generate and process components to produce finished products. The main entities in this model are classes that represent elements of the assembly line, such as machines and pieces.

### Classes and Attributes

1. **Element (abstract class)**: 
   - Attributes: `x` (Integer), `y` (Integer)
   - Purpose: Serves as a base class for positioning elements in a 2D space.

2. **Machine (abstract class)**:
   - Inherits: `Element`
   - Attributes: `processingTime` (Integer)
   - Operations: `start()`, `stop()`
   - Purpose: Represents a machine in the assembly line capable of processing tasks.

3. **PieceGenerator (abstract class)**:
   - Inherits: `Machine`
   - Purpose: Specializes in generating pieces.

4. **HandleGenerator**:
   - Inherits: `PieceGenerator`
   - Attributes: `counter` (Integer)
   - Purpose: Generates handle components.

5. **HeadGenerator**:
   - Inherits: `PieceGenerator`
   - Attributes: `counter` (Integer)
   - Purpose: Generates head components.

6. **Assembler**:
   - Inherits: `Machine`
   - Purpose: Combines different pieces to form a final product.

7. **Polisher**:
   - Inherits: `Machine`
   - Purpose: Polishes the final product or components.

8. **Tray**:
   - Inherits: `Element`
   - Attributes: `capacity` (Integer)
   - Purpose: Holds pieces in the assembly line.

9. **Piece (abstract class)**:
   - Purpose: Base class for components handled in the assembly line.

10. **Head**:
    - Inherits: `Piece`
    - Purpose: Represents the head part of a product.

11. **Handle**:
    - Inherits: `Piece`
    - Purpose: Represents the handle part of a product.

12. **Hammer**:
    - Inherits: `Piece`
    - Attributes: `isPolished` (Boolean)
    - Purpose: Represents the final product.

# Relationships

1. **Output Association**:
   - Between: `Machine` and `Tray`
   - Multiplicity: Machine [1..*] can have exactly one Tray [1] as output.
   - Purpose: Machines output pieces to a Tray.

2. **Input Association**:
   - Between: `Tray` and `Machine`
   - Multiplicity: Tray [*] can be input to multiple Machines [*].
   - Purpose: Trays provide pieces as input to Machines.

3. **Content Aggregation**:
   - Between: `Tray` and `Piece`
   - Multiplicity: Each Tray [1] can contain multiple ordered Pieces [*].
   - Purpose: Trays aggregate pieces.

# Invariants

1. **Tray Invariant - AtLeastOneCell**: 
   - Ensures that every Tray has a capacity greater than zero.

2. **Tray Invariant - PiecesSameType**: 
   - All pieces in a Tray must be of the same type (either all Hammers, all Handles, or all Heads).

3. **Assembler Invariant - OneinputOfHandles**: 
   - At least one input Tray to an Assembler must consist entirely of Handles.

4. **Assembler Invariant - OneinputOfHeads**: 
   - At least one input Tray to an Assembler must consist entirely of Heads.

5. **Polisher Invariant - HammersInput**: 
   - All input Trays to a Polisher must consist entirely of Hammers.

6. **HeadGenerator Invariant - HeadsOutput**: 
   - The output Tray of a HeadGenerator must consist entirely of Heads.

7. **HandleGenerator Invariant - HandlesOutput**: 
   - The output Tray of a HandleGenerator must consist entirely of Handles.

8. **Assembler Invariant - HammersOutput**: 
   - The output Tray of an Assembler must consist entirely of Hammers.

9. **PieceGenerator Invariant - NoInput**: 
   - A PieceGenerator must have no input Trays.

# Category: Baseline Instances
### Instance 1: Basic Assembly Line for Hammers

This instance represents a simple assembly line setup to produce hammers, demonstrating the basic structure and workflow of the "AssemblyChain" model.

#### Entities and Configuration

1. **HandleGenerator A**
   - Attributes:
     - `x`: 10
     - `y`: 20
     - `processingTime`: 5
     - `counter`: 100
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray 1`

2. **Tray 1**
   - Attributes:
     - `capacity`: 50
   - Pieces: 50 `Handle` instances
   - Relationship:
     - Aggregates pieces from `HandleGenerator A`.
     - Serves as input to `Assembler X`.

3. **HeadGenerator B**
   - Attributes:
     - `x`: 15
     - `y`: 25
     - `processingTime`: 6
     - `counter`: 100
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray 2`

4. **Tray 2**
   - Attributes:
     - `capacity`: 50
   - Pieces: 50 `Head` instances
   - Relationship:
     - Aggregates pieces from `HeadGenerator B`.
     - Serves as input to `Assembler X`.

5. **Assembler X**
   - Attributes:
     - `x`: 20
     - `y`: 30
     - `processingTime`: 10
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray 1`, `Tray 2`
   - Output Tray: `Tray 3`

6. **Tray 3**
   - Attributes:
     - `capacity`: 100
   - Pieces: 50 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `Assembler X`.
     - Serves as input to `Polisher C`.

7. **Polisher C**
   - Attributes:
     - `x`: 25
     - `y`: 35
     - `processingTime`: 8
   - Operations: `start()`, `stop()`
   - Input Tray: `Tray 3`
   - Output Tray: `Tray 4`

8. **Tray 4**
   - Attributes:
     - `capacity`: 100
   - Pieces: 50 `Hammer` instances (marked as `isPolished: true`)
   - Relationship:
     - Aggregates polished pieces from `Polisher C`.

### Instance 2: Regional Variant - Japanese Assembly Line

This instance illustrates a regional variation in the assembly line process, focusing on a Japanese factory known for precision.

#### Entities and Configuration

1. **HandleGenerator Sakura**
   - Attributes:
     - `x`: 30
     - `y`: 40
     - `processingTime`: 4
     - `counter`: 200
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray Sakura`

2. **Tray Sakura**
   - Attributes:
     - `capacity`: 70
   - Pieces: 70 `Handle` instances
   - Relationship:
     - Aggregates pieces from `HandleGenerator Sakura`.
     - Serves as input to `Assembler Fuji`.

3. **HeadGenerator Fuji**
   - Attributes:
     - `x`: 35
     - `y`: 45
     - `processingTime`: 5
     - `counter`: 200
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray Fuji`

4. **Tray Fuji**
   - Attributes:
     - `capacity`: 70
   - Pieces: 70 `Head` instances
   - Relationship:
     - Aggregates pieces from `HeadGenerator Fuji`.
     - Serves as input to `Assembler Fuji`.

5. **Assembler Fuji**
   - Attributes:
     - `x`: 40
     - `y`: 50
     - `processingTime`: 9
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray Sakura`, `Tray Fuji`
   - Output Tray: `Tray Yamato`

6. **Tray Yamato**
   - Attributes:
     - `capacity`: 140
   - Pieces: 70 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `Assembler Fuji`.
     - Serves as input to `Polisher Kyoto`.

7. **Polisher Kyoto**
   - Attributes:
     - `x`: 45
     - `y`: 55
     - `processingTime`: 7
   - Operations: `start()`, `stop()`
   - Input Tray: `Tray Yamato`
   - Output Tray: `Tray Nara`

8. **Tray Nara**
   - Attributes:
     - `capacity`: 140
   - Pieces: 70 `Hammer` instances (marked as `isPolished: true`)
   - Relationship:
     - Aggregates polished pieces from `Polisher Kyoto`.

These instances showcase a baseline setup for the "AssemblyChain" model, highlighting the essential processes and satisfying all the defined invariants for the assembly line operation.
```
!new HandleGenerator('A')
!A.x:=10
!A.y:=20
!A.processingTime:=5
!A.counter:=100

!new Tray('Tray1')
!Tray1.capacity:=50

!new HeadGenerator('B')
!B.x:=15
!B.y:=25
!B.processingTime:=6
!B.counter:=100

!new Tray('Tray2')
!Tray2.capacity:=50

!new Assembler('X')
!X.x:=20
!X.y:=30
!X.processingTime:=10

!new Tray('Tray3')
!Tray3.capacity:=100

!new Polisher('C')
!C.x:=25
!C.y:=35
!C.processingTime:=8

!new Tray('Tray4')
!Tray4.capacity:=100

!insert(A,Tray1) into Output
!insert(B,Tray2) into Output
!insert(Tray1,X) into Input
!insert(Tray2,X) into Input
!insert(X,Tray3) into Output
!insert(Tray3,C) into Input
!insert(C,Tray4) into Output

!new HandleGenerator('Sakura')
!Sakura.x:=30
!Sakura.y:=40
!Sakura.processingTime:=4
!Sakura.counter:=200

!new Tray('TraySakura')
!TraySakura.capacity:=70

!new HeadGenerator('Fuji')
!Fuji.x:=35
!Fuji.y:=45
!Fuji.processingTime:=5
!Fuji.counter:=200

!new Tray('TrayFuji')
!TrayFuji.capacity:=70

!new Assembler('FujiAssembler')
!FujiAssembler.x:=40
!FujiAssembler.y:=50
!FujiAssembler.processingTime:=9

!new Tray('TrayYamato')
!TrayYamato.capacity:=140

!new Polisher('Kyoto')
!Kyoto.x:=45
!Kyoto.y:=55
!Kyoto.processingTime:=7

!new Tray('TrayNara')
!TrayNara.capacity:=140

!insert(Sakura,TraySakura) into Output
!insert(Fuji,TrayFuji) into Output
!insert(TraySakura,FujiAssembler) into Input
!insert(TrayFuji,FujiAssembler) into Input
!insert(FujiAssembler,TrayYamato) into Output
!insert(TrayYamato,Kyoto) into Input
!insert(Kyoto,TrayNara) into Output
```

# Category: Boundary Instances
### Instance 1: Minimal Boundary Configuration

This instance explores the minimal boundary of the assembly line, focusing on the smallest possible configuration that still adheres to the model's constraints.

#### Entities and Configuration

1. **HandleGenerator MinHandleGen**
   - Attributes:
     - `x`: 0
     - `y`: 0
     - `processingTime`: 1
     - `counter`: 1
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray A`

2. **Tray A**
   - Attributes:
     - `capacity`: 1
   - Pieces: 1 `Handle` instance
   - Relationship:
     - Aggregates pieces from `MinHandleGen`.
     - Serves as input to `Assembler MinAssembler`.

3. **HeadGenerator MinHeadGen**
   - Attributes:
     - `x`: 1
     - `y`: 1
     - `processingTime`: 1
     - `counter`: 1
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray B`

4. **Tray B**
   - Attributes:
     - `capacity`: 1
   - Pieces: 1 `Head` instance
   - Relationship:
     - Aggregates pieces from `MinHeadGen`.
     - Serves as input to `Assembler MinAssembler`.

5. **Assembler MinAssembler**
   - Attributes:
     - `x`: 2
     - `y`: 2
     - `processingTime`: 1
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray A`, `Tray B`
   - Output Tray: `Tray C`

6. **Tray C**
   - Attributes:
     - `capacity`: 1
   - Pieces: 1 `Hammer` instance
   - Relationship:
     - Aggregates pieces from `MinAssembler`.

### Instance 2: Maximum Capacity and Multiplicity

This instance tests the upper boundary limits of the assembly line, using the maximum possible values to stress-test the model.

#### Entities and Configuration

1. **HandleGenerator MaxHandleGen**
   - Attributes:
     - `x`: 1000
     - `y`: 1000
     - `processingTime`: 100
     - `counter`: 1000
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray X`

2. **Tray X**
   - Attributes:
     - `capacity`: 1000
   - Pieces: 1000 `Handle` instances
   - Relationship:
     - Aggregates pieces from `MaxHandleGen`.
     - Serves as input to multiple Assembler machines.

3. **HeadGenerator MaxHeadGen**
   - Attributes:
     - `x`: 1001
     - `y`: 1001
     - `processingTime`: 100
     - `counter`: 1000
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray Y`

4. **Tray Y**
   - Attributes:
     - `capacity`: 1000
   - Pieces: 1000 `Head` instances
   - Relationship:
     - Aggregates pieces from `MaxHeadGen`.
     - Serves as input to multiple Assembler machines.

5. **Assembler MaxAssembler1**
   - Attributes:
     - `x`: 1002
     - `y`: 1002
     - `processingTime`: 50
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray X`, `Tray Y`
   - Output Tray: `Tray Z1`

6. **Assembler MaxAssembler2**
   - Attributes:
     - `x`: 1003
     - `y`: 1003
     - `processingTime`: 50
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray X`, `Tray Y`
   - Output Tray: `Tray Z2`

7. **Tray Z1**
   - Attributes:
     - `capacity`: 500
   - Pieces: 500 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `MaxAssembler1`.

8. **Tray Z2**
   - Attributes:
     - `capacity`: 500
   - Pieces: 500 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `MaxAssembler2`.

### Instance 3: Optional Associations and Extreme Values

This instance examines scenarios with optional associations and extreme attribute values.

#### Entities and Configuration

1. **HandleGenerator OptionalHandleGen**
   - Attributes:
     - `x`: -999
     - `y`: -999
     - `processingTime`: 0
     - `counter`: 0
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray OptionalA`

2. **Tray OptionalA**
   - Attributes:
     - `capacity`: 0
   - Pieces: No pieces (tests content aggregation with zero capacity)
   - Relationship:
     - Serves as input to an assembler but remains empty due to zero capacity.

3. **Assembler OptionalAssembler**
   - Attributes:
     - `x`: -998
     - `y`: -998
     - `processingTime`: 100
   - Operations: `start()`, `stop()`
   - Input Tray: `Tray OptionalA` (tests if the system can handle an empty input scenario)

These instances establish boundary conditions by using minimal and maximal values and optional associations, providing comprehensive coverage of extreme cases for the "AssemblyChain" model.
```
!new HandleGenerator('MinHandleGen')
!MinHandleGen.x:=0
!MinHandleGen.y:=0
!MinHandleGen.processingTime:=1
!MinHandleGen.counter:=1

!new Tray('TrayA')
!TrayA.capacity:=1

!new HeadGenerator('MinHeadGen')
!MinHeadGen.x:=1
!MinHeadGen.y:=1
!MinHeadGen.processingTime:=1
!MinHeadGen.counter:=1

!new Tray('TrayB')
!TrayB.capacity:=1

!new Assembler('MinAssembler')
!MinAssembler.x:=2
!MinAssembler.y:=2
!MinAssembler.processingTime:=1

!new Tray('TrayC')
!TrayC.capacity:=1

!insert(MinHandleGen,TrayA) into Output
!insert(MinHeadGen,TrayB) into Output
!insert(TrayA,MinAssembler) into Input
!insert(TrayB,MinAssembler) into Input
!insert(MinAssembler,TrayC) into Output

!new HandleGenerator('MaxHandleGen')
!MaxHandleGen.x:=1000
!MaxHandleGen.y:=1000
!MaxHandleGen.processingTime:=100
!MaxHandleGen.counter:=1000

!new Tray('TrayX')
!TrayX.capacity:=1000

!new HeadGenerator('MaxHeadGen')
!MaxHeadGen.x:=1001
!MaxHeadGen.y:=1001
!MaxHeadGen.processingTime:=100
!MaxHeadGen.counter:=1000

!new Tray('TrayY')
!TrayY.capacity:=1000

!new Assembler('MaxAssembler1')
!MaxAssembler1.x:=1002
!MaxAssembler1.y:=1002
!MaxAssembler1.processingTime:=50

!new Assembler('MaxAssembler2')
!MaxAssembler2.x:=1003
!MaxAssembler2.y:=1003
!MaxAssembler2.processingTime:=50

!new Tray('TrayZ1')
!TrayZ1.capacity:=500

!new Tray('TrayZ2')
!TrayZ2.capacity:=500

!insert(MaxHandleGen,TrayX) into Output
!insert(MaxHeadGen,TrayY) into Output
!insert(TrayX,MaxAssembler1) into Input
!insert(TrayY,MaxAssembler1) into Input
!insert(MaxAssembler1,TrayZ1) into Output
!insert(TrayX,MaxAssembler2) into Input
!insert(TrayY,MaxAssembler2) into Input
!insert(MaxAssembler2,TrayZ2) into Output

!new HandleGenerator('OptionalHandleGen')
!OptionalHandleGen.x:=-999
!OptionalHandleGen.y:=-999
!OptionalHandleGen.processingTime:=0
!OptionalHandleGen.counter:=0

!new Tray('TrayOptionalA')
!TrayOptionalA.capacity:=1

!new Assembler('OptionalAssembler')
!OptionalAssembler.x:=-998
!OptionalAssembler.y:=-998
!OptionalAssembler.processingTime:=100

!new Tray('TrayOptionalOutput')
!TrayOptionalOutput.capacity:=0

!insert(OptionalHandleGen,TrayOptionalA) into Output
!insert(TrayOptionalA,OptionalAssembler) into Input
!insert(OptionalAssembler,TrayOptionalOutput) into Output
```

# Category: Complex Instances
### Complex Instance: Multi-Stage Assembly Line with Interdependencies

This instance demonstrates a comprehensive and intertwined assembly line process with multiple stages, leveraging complex interactions between entities and testing the constraints and invariants in depth.

#### Entities and Configuration

1. **HandleGenerator Alpha**
   - Attributes:
     - `x`: 5
     - `y`: 10
     - `processingTime`: 3
     - `counter`: 500
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray Handles 1`

2. **Tray Handles 1**
   - Attributes:
     - `capacity`: 250
   - Pieces: 250 `Handle` instances
   - Relationship:
     - Aggregates pieces from `HandleGenerator Alpha`.
     - Serves as input to `Assembler Delta`.

3. **HandleGenerator Beta**
   - Attributes:
     - `x`: 6
     - `y`: 12
     - `processingTime`: 3
     - `counter`: 500
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray Handles 2`

4. **Tray Handles 2**
   - Attributes:
     - `capacity`: 250
   - Pieces: 250 `Handle` instances
   - Relationship:
     - Aggregates pieces from `HandleGenerator Beta`.
     - Serves as input to `Assembler Delta`.

5. **HeadGenerator Gamma**
   - Attributes:
     - `x`: 7
     - `y`: 14
     - `processingTime`: 4
     - `counter`: 500
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray Heads 1`

6. **Tray Heads 1**
   - Attributes:
     - `capacity`: 500
   - Pieces: 500 `Head` instances
   - Relationship:
     - Aggregates pieces from `HeadGenerator Gamma`.
     - Serves as input to `Assembler Delta` and `Assembler Epsilon`.

7. **Assembler Delta**
   - Attributes:
     - `x`: 8
     - `y`: 16
     - `processingTime`: 5
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray Handles 1`, `Tray Heads 1`
   - Output Tray: `Tray Hammers 1`

8. **Tray Hammers 1**
   - Attributes:
     - `capacity`: 200
   - Pieces: 200 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `Assembler Delta`.
     - Serves as input to `Polisher Zeta`.

9. **Assembler Epsilon**
   - Attributes:
     - `x`: 9
     - `y`: 18
     - `processingTime`: 6
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray Handles 2`, `Tray Heads 1`
   - Output Tray: `Tray Hammers 2`

10. **Tray Hammers 2**
    - Attributes:
      - `capacity`: 300
    - Pieces: 300 `Hammer` instances
    - Relationship:
      - Aggregates pieces from `Assembler Epsilon`.
      - Serves as input to `Polisher Zeta`.

11. **Polisher Zeta**
    - Attributes:
      - `x`: 10
      - `y`: 20
      - `processingTime`: 7
    - Operations: `start()`, `stop()`
    - Input Trays: `Tray Hammers 1`, `Tray Hammers 2`
    - Output Tray: `Tray Polished Hammers`

12. **Tray Polished Hammers**
    - Attributes:
      - `capacity`: 500
    - Pieces: 500 `Hammer` instances (marked as `isPolished: true`)
    - Relationship:
      - Aggregates polished pieces from `Polisher Zeta`.

#### Key Interaction Points and Invariants

- **Multiple Input Trays**: `Assembler Delta` and `Assembler Epsilon` both utilize trays that demonstrate the model's support for handling multiple inputs of the same type, ensuring that the invariant constraints for handles and heads are respected.
- **Shared Tray**: `Tray Heads 1` is a shared resource between `Assembler Delta` and `Assembler Epsilon`, showcasing the model's capacity to support complex tray dependencies.
- **Polisher Constraint**: `Polisher Zeta` enforces the constraint that all input trays must consist of hammers, ensuring compliance with the `Polisher Invariant - HammersInput`.
- **Capacity Management**: The trays are designed with varying capacities to test the handling of large volumes and ensure the system processes the maximum output efficiently.

This complex instance pushes the boundaries of the model by incorporating multiple machines and trays with interdependencies, providing a robust test of the system’s ability to manage intricate assembly line operations while adhering to all defined constraints and invariants.
```
!new HandleGenerator('Alpha')
!Alpha.x:=5
!Alpha.y:=10
!Alpha.processingTime:=3
!Alpha.counter:=500

!new Tray('TrayHandles1')
!TrayHandles1.capacity:=250

!new HandleGenerator('Beta')
!Beta.x:=6
!Beta.y:=12
!Beta.processingTime:=3
!Beta.counter:=500

!new Tray('TrayHandles2')
!TrayHandles2.capacity:=250

!new HeadGenerator('Gamma')
!Gamma.x:=7
!Gamma.y:=14
!Gamma.processingTime:=4
!Gamma.counter:=500

!new Tray('TrayHeads1')
!TrayHeads1.capacity:=500

!new Assembler('Delta')
!Delta.x:=8
!Delta.y:=16
!Delta.processingTime:=5

!new Tray('TrayHammers1')
!TrayHammers1.capacity:=200

!new Assembler('Epsilon')
!Epsilon.x:=9
!Epsilon.y:=18
!Epsilon.processingTime:=6

!new Tray('TrayHammers2')
!TrayHammers2.capacity:=300

!new Polisher('Zeta')
!Zeta.x:=10
!Zeta.y:=20
!Zeta.processingTime:=7

!new Tray('TrayPolishedHammers')
!TrayPolishedHammers.capacity:=500

!insert(Alpha,TrayHandles1) into Output
!insert(Beta,TrayHandles2) into Output
!insert(Gamma,TrayHeads1) into Output
!insert(TrayHandles1,Delta) into Input
!insert(TrayHeads1,Delta) into Input
!insert(Delta,TrayHammers1) into Output
!insert(TrayHandles2,Epsilon) into Input
!insert(TrayHeads1,Epsilon) into Input
!insert(Epsilon,TrayHammers2) into Output
!insert(TrayHammers1,Zeta) into Input
!insert(TrayHammers2,Zeta) into Input
!insert(Zeta,TrayPolishedHammers) into Output
```

# Category: Unrealistc but valid
### Unrealistic but Valid Instance: Circular Assembly Line

This instance demonstrates an assembly line configuration that is technically valid according to the model’s constraints but is highly unlikely in a practical scenario due to its circular dependency logic.

#### Entities and Configuration

1. **HandleGenerator CircularHandleGen**
   - Attributes:
     - `x`: 50
     - `y`: 50
     - `processingTime`: 2
     - `counter`: 10
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray CircularHandles`

2. **Tray CircularHandles**
   - Attributes:
     - `capacity`: 10
   - Pieces: 10 `Handle` instances
   - Relationship:
     - Aggregates pieces from `CircularHandleGen`.
     - Serves as input to `Assembler CircularAssembler`.

3. **HeadGenerator CircularHeadGen**
   - Attributes:
     - `x`: 60
     - `y`: 60
     - `processingTime`: 2
     - `counter`: 10
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray CircularHeads`

4. **Tray CircularHeads**
   - Attributes:
     - `capacity`: 10
   - Pieces: 10 `Head` instances
   - Relationship:
     - Aggregates pieces from `CircularHeadGen`.
     - Serves as input to `Assembler CircularAssembler`.

5. **Assembler CircularAssembler**
   - Attributes:
     - `x`: 70
     - `y`: 70
     - `processingTime`: 3
   - Operations: `start()`, `stop()`
   - Input Trays: `Tray CircularHandles`, `Tray CircularHeads`
   - Output Tray: `Tray CircularHammers`

6. **Tray CircularHammers**
   - Attributes:
     - `capacity`: 10
   - Pieces: 10 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `CircularAssembler`.
     - Serves as input to `Polisher CircularPolisher`.

7. **Polisher CircularPolisher**
   - Attributes:
     - `x`: 80
     - `y`: 80
     - `processingTime`: 4
   - Operations: `start()`, `stop()`
   - Input Tray: `Tray CircularHammers`
   - Output Tray: `Tray FinalHammers`

8. **Tray FinalHammers**
   - Attributes:
     - `capacity`: 10
   - Pieces: 10 `Hammer` instances (marked as `isPolished: true`)
   - Relationship:
     - Aggregates polished pieces from `CircularPolisher`.

9. **Unrealistic Circular Setup**
   - After polishing, the `Tray FinalHammers` is fictitiously considered as an input to a hypothetical machine that would generate heads or handles, which violates practical logic but remains structurally valid due to lack of specific constraints prohibiting it in the model.
   - This setup could theoretically send hammers back to a stage that only expects raw pieces, demonstrating an unrealistic loop.

#### Key Points

- **Circular Dependency**: This setup creates a loop where finished products could theoretically re-enter the assembly line as raw materials, which doesn’t violate any explicit constraints but defies practical logic.
- **Capacity and Multiplicity**: All trays and machines operate within valid capacity and multiplicity constraints, maintaining model integrity.
- **Invariant Compliance**: All trays contain uniform piece types as required, and each machine’s input/output structure is respected, fitting within the model's rules.

This instance highlights the flexibility of the model in accommodating configurations that are valid on paper but implausible in real-world applications, emphasizing the importance of practical logic beyond structural constraints.
```
!new HandleGenerator('CircularHandleGen')
!CircularHandleGen.x:=50
!CircularHandleGen.y:=50
!CircularHandleGen.processingTime:=2
!CircularHandleGen.counter:=10

!new Tray('TrayCircularHandles')
!TrayCircularHandles.capacity:=10

!new HeadGenerator('CircularHeadGen')
!CircularHeadGen.x:=60
!CircularHeadGen.y:=60
!CircularHeadGen.processingTime:=2
!CircularHeadGen.counter:=10

!new Tray('TrayCircularHeads')
!TrayCircularHeads.capacity:=10

!new Assembler('CircularAssembler')
!CircularAssembler.x:=70
!CircularAssembler.y:=70
!CircularAssembler.processingTime:=3

!new Tray('TrayCircularHammers')
!TrayCircularHammers.capacity:=10

!new Polisher('CircularPolisher')
!CircularPolisher.x:=80
!CircularPolisher.y:=80
!CircularPolisher.processingTime:=4

!new Tray('TrayFinalHammers')
!TrayFinalHammers.capacity:=10

!insert(CircularHandleGen,TrayCircularHandles) into Output
!insert(CircularHeadGen,TrayCircularHeads) into Output
!insert(TrayCircularHandles,CircularAssembler) into Input
!insert(TrayCircularHeads,CircularAssembler) into Input
!insert(CircularAssembler,TrayCircularHammers) into Output
!insert(TrayCircularHammers,CircularPolisher) into Input
!insert(CircularPolisher,TrayFinalHammers) into Output
```

# Category: Realistic but invalid
### Realistic but Invalid Instance: Mixed Component Processing

This instance illustrates a real-life scenario where a flexible assembly line processes multiple types of components simultaneously, which is a common practice in modern manufacturing but violates the model’s constraints.

#### Entities and Configuration

1. **HandleGenerator MixedGen**
   - Attributes:
     - `x`: 10
     - `y`: 10
     - `processingTime`: 3
     - `counter`: 50
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray MixedPieces`

2. **HeadGenerator MixedGen**
   - Attributes:
     - `x`: 15
     - `y`: 15
     - `processingTime`: 3
     - `counter`: 50
   - Operations: `start()`, `stop()`
   - Output Tray: `Tray MixedPieces`

3. **Tray MixedPieces**
   - Attributes:
     - `capacity`: 100
   - Pieces: 50 `Handle` instances, 50 `Head` instances
   - Relationship:
     - Aggregates pieces from both `HandleGenerator MixedGen` and `HeadGenerator MixedGen`.
     - Serves as input to `Assembler FlexibleAssembler`.

4. **Assembler FlexibleAssembler**
   - Attributes:
     - `x`: 20
     - `y`: 20
     - `processingTime`: 5
   - Operations: `start()`, `stop()`
   - Input Tray: `Tray MixedPieces`
   - Output Tray: `Tray AssembledHammers`

5. **Tray AssembledHammers**
   - Attributes:
     - `capacity`: 50
   - Pieces: 50 `Hammer` instances
   - Relationship:
     - Aggregates pieces from `FlexibleAssembler`.
     - Serves as input to `Polisher FinalPolisher`.

6. **Polisher FinalPolisher**
   - Attributes:
     - `x`: 25
     - `y`: 25
     - `processingTime`: 4
   - Operations: `start()`, `stop()`
   - Input Tray: `Tray AssembledHammers`
   - Output Tray: `Tray PolishedHammers`

7. **Tray PolishedHammers**
   - Attributes:
     - `capacity`: 50
   - Pieces: 50 `Hammer` instances (marked as `isPolished: true`)
   - Relationship:
     - Aggregates polished pieces from `FinalPolisher`.

#### Violations and Realism

- **Tray Invariant - PiecesSameType**: `Tray MixedPieces` contains both `Handles` and `Heads`, violating the constraint that all pieces in a tray must be of the same type. In reality, mixed trays are often used for efficiency.
- **Assembler Invariant - OneinputOfHandles/Heads**: `Assembler FlexibleAssembler` uses a single tray with mixed components, which violates the requirement for separate trays of handles and heads.
- **Model Flexibility**: In many real-world scenarios, assembly lines are designed to handle mixed inputs to optimize space and reduce the need for multiple trays, highlighting a potential area for model improvement.

This instance demonstrates a plausible and efficient real-world manufacturing process that the current constraints of the model do not accommodate, suggesting that the model could be expanded to support more flexible and practical assembly line configurations.
```
!new HandleGenerator('MixedGenHandle')
!MixedGenHandle.x:=10
!MixedGenHandle.y:=10
!MixedGenHandle.processingTime:=3
!MixedGenHandle.counter:=50

!new HeadGenerator('MixedGenHead')
!MixedGenHead.x:=15
!MixedGenHead.y:=15
!MixedGenHead.processingTime:=3
!MixedGenHead.counter:=50

!new Tray('TrayMixedPieces')
!TrayMixedPieces.capacity:=100

!new Assembler('FlexibleAssembler')
!FlexibleAssembler.x:=20
!FlexibleAssembler.y:=20
!FlexibleAssembler.processingTime:=5

!new Tray('TrayAssembledHammers')
!TrayAssembledHammers.capacity:=50

!new Polisher('FinalPolisher')
!FinalPolisher.x:=25
!FinalPolisher.y:=25
!FinalPolisher.processingTime:=4

!new Tray('TrayPolishedHammers')
!TrayPolishedHammers.capacity:=50

!insert(MixedGenHandle,TrayMixedPieces) into Output
!insert(MixedGenHead,TrayMixedPieces) into Output
!insert(TrayMixedPieces,FlexibleAssembler) into Input
!insert(FlexibleAssembler,TrayAssembledHammers) into Output
!insert(TrayAssembledHammers,FinalPolisher) into Input
!insert(FinalPolisher,TrayPolishedHammers) into Output
```