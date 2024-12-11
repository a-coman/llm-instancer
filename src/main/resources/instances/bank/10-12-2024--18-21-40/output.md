# Description

The UML model represents a system for managing bank accounts, involving three main classes: `Bank`, `Account`, and `Person`. Each class has specific attributes that define its properties and roles within the banking environment.

## Components

- **Bank**: Represents a financial institution. It has the following attributes:
  - `country`: A string indicating the country where the bank operates.
  - `name`: A string for the bank's name.
  - `bic`: A string for the Bank Identifier Code, which uniquely identifies the bank.

- **Account**: Represents a bank account held at a bank. It has the following attributes:
  - `iban`: A string representing the International Bank Account Number, which uniquely identifies the account.
  - `balance`: An integer representing the current balance of the account.

- **Person**: Represents an individual who can own or use bank accounts. It has the following attributes:
  - `firstName`: A string for the person's first name.
  - `lastName`: A string for the person's last name.
  - `age`: An integer representing the person's age.

# Relationships

- **Ownership**: This association links `Person` to `Account` indicating ownership.
  - A `Person` can own one or more accounts (multiplicity: 1 to * for `Account`).
  - An `Account` must have exactly one owner (multiplicity: 1 for `Person`).

- **Use**: This association indicates which accounts a `Person` can use.
  - A `Person` can use multiple accounts (multiplicity: * for `Account`).
  - An `Account` can be used by multiple people (multiplicity: * for `Person`).

- **AccountOfBanks**: This composition relationship connects `Bank` to `Account`.
  - A `Bank` must have one or more accounts (multiplicity: 1 to * for `Account`).
  - An `Account` is associated with exactly one bank (multiplicity: 1 for `Bank`).

# Invariants

- **AdultOwners**: This invariant ensures that all account owners must be adults, meaning the `age` attribute of the `Person` associated with an `Account` must be greater than 18.

- **PositiveBalance**: This invariant enforces that the balance of an account must always be positive, ensuring no account has a negative balance.

# Category: Boundary Instances
### Instance 1: Minimum Constraints

#### Entities

- **Bank**
  - `country`: "Iceland"
  - `name`: "Arctic Bank"
  - `bic`: "ICELISRE"

- **Person**
  - `firstName`: "Anna"
  - `lastName`: "Bjornsdottir"
  - `age`: 19

- **Account**
  - `iban`: "IS140159260076545510730339"
  - `balance`: 1

#### Relationships

- **Ownership**
  - Owner: Anna Bjornsdottir owns the account with IBAN "IS140159260076545510730339".

- **Use**
  - User: Anna Bjornsdottir can use the account with IBAN "IS140159260076545510730339".

- **AccountOfBanks**
  - Bank: Account with IBAN "IS140159260076545510730339" is associated with "Arctic Bank".

### Instance 2: Maximum Constraints with Regional Diversity

#### Entities

- **Bank**
  - `country`: "India"
  - `name`: "Bharat Bank"
  - `bic`: "INBBINBB"

- **Person**
  - `firstName`: "Raj"
  - `lastName`: "Kumar"
  - `age`: 45

- **Account 1**
  - `iban`: "IN0212450000001234567890"
  - `balance`: 1000000000

- **Account 2**
  - `iban`: "IN0212450000009876543210"
  - `balance`: 500000000

- **Person (Additional User)**
  - `firstName`: "Priya"
  - `lastName`: "Kapoor"
  - `age`: 32

#### Relationships

- **Ownership**
  - Owner: Raj Kumar owns both accounts with IBANs "IN0212450000001234567890" and "IN0212450000009876543210".

- **Use**
  - Users: Raj Kumar and Priya Kapoor can use the account with IBAN "IN0212450000001234567890".
  - Users: Priya Kapoor can use the account with IBAN "IN0212450000009876543210".

- **AccountOfBanks**
  - Bank: Both accounts "IN0212450000001234567890" and "IN0212450000009876543210" are associated with "Bharat Bank".

### Instance 3: Optional Collections and Extreme Age

#### Entities

- **Bank**
  - `country`: "Japan"
  - `name`: "Fuji Bank"
  - `bic`: "JPJPJPJP"

- **Person**
  - `firstName`: "Kenji"
  - `lastName`: "Tanaka"
  - `age`: 100

- **Account**
  - `iban`: "JP4321000000006789012345"
  - `balance`: 100

#### Relationships

- **Ownership**
  - Owner: Kenji Tanaka owns the account with IBAN "JP4321000000006789012345".

- **Use**
  - User: Kenji Tanaka can use the account with IBAN "JP4321000000006789012345".

- **AccountOfBanks**
  - Bank: Account with IBAN "JP4321000000006789012345" is associated with "Fuji Bank".

### Instance 4: Empty Collection for Use

#### Entities

- **Bank**
  - `country`: "Brazil"
  - `name`: "Rio Bank"
  - `bic`: "BRBRBRBR"

- **Person**
  - `firstName`: "Carlos"
  - `lastName`: "Silva"
  - `age`: 30

- **Account**
  - `iban`: "BR1800000000000000000000"
  - `balance`: 300

#### Relationships

- **Ownership**
  - Owner: Carlos Silva owns the account with IBAN "BR1800000000000000000000".

- **Use**
  - User: No additional users for the account with IBAN "BR1800000000000000000000" (demonstrates an empty collection).

- **AccountOfBanks**
  - Bank: Account with IBAN "BR1800000000000000000000" is associated with "Rio Bank".
```
!new Bank('bank1')
!bank1.country := 'Iceland'
!bank1.name := 'Arctic Bank'
!bank1.bic := 'ICELISRE'

!new Person('person1')
!person1.age := 19
!person1.firstName := 'Anna'
!person1.lastName := 'Bjornsdottir'

!new Account('account1')
!account1.iban := 'IS140159260076545510730339'
!account1.balance := 1

!insert (person1, account1) into Ownership
!insert (person1, account1) into Use
!insert (bank1, account1) into AccountOfBanks

!new Bank('bank2')
!bank2.country := 'India'
!bank2.name := 'Bharat Bank'
!bank2.bic := 'INBBINBB'

!new Person('person2')
!person2.age := 45
!person2.firstName := 'Raj'
!person2.lastName := 'Kumar'

!new Account('account2')
!account2.iban := 'IN0212450000001234567890'
!account2.balance := 1000000000

!new Account('account3')
!account3.iban := 'IN0212450000009876543210'
!account3.balance := 500000000

!new Person('person3')
!person3.age := 32
!person3.firstName := 'Priya'
!person3.lastName := 'Kapoor'

!insert (person2, account2) into Ownership
!insert (person2, account3) into Ownership
!insert (person2, account2) into Use
!insert (person3, account2) into Use
!insert (person3, account3) into Use
!insert (bank2, account2) into AccountOfBanks
!insert (bank2, account3) into AccountOfBanks

!new Bank('bank3')
!bank3.country := 'Japan'
!bank3.name := 'Fuji Bank'
!bank3.bic := 'JPJPJPJP'

!new Person('person4')
!person4.age := 100
!person4.firstName := 'Kenji'
!person4.lastName := 'Tanaka'

!new Account('account4')
!account4.iban := 'JP4321000000006789012345'
!account4.balance := 100

!insert (person4, account4) into Ownership
!insert (person4, account4) into Use
!insert (bank3, account4) into AccountOfBanks

!new Bank('bank4')
!bank4.country := 'Brazil'
!bank4.name := 'Rio Bank'
!bank4.bic := 'BRBRBRBR'

!new Person('person5')
!person5.age := 30
!person5.firstName := 'Carlos'
!person5.lastName := 'Silva'

!new Account('account5')
!account5.iban := 'BR1800000000000000000000'
!account5.balance := 300

!insert (person5, account5) into Ownership
!insert (bank4, account5) into AccountOfBanks
```

# Category: Complex Instances
### Complex Instance 1: Multi-National Bank with Diverse Accounts and Users

#### Entities

- **Bank 1: Global Bank**
  - `country`: "United States"
  - `name`: "Global Bank"
  - `bic`: "USGLBANK"

- **Bank 2: EuroBank**
  - `country`: "Germany"
  - `name`: "EuroBank"
  - `bic`: "DEEBANK"

- **Person 1: John Doe**
  - `firstName`: "John"
  - `lastName`: "Doe"
  - `age`: 35

- **Person 2: Maria Müller**
  - `firstName`: "Maria"
  - `lastName`: "Müller"
  - `age`: 28

- **Person 3: Kenji Yamamoto**
  - `firstName`: "Kenji"
  - `lastName`: "Yamamoto"
  - `age`: 45

- **Account 1: John's Global Account**
  - `iban`: "US12345678901234567890"
  - `balance`: 5000

- **Account 2: Maria's Euro Account**
  - `iban`: "DE09876543210987654321"
  - `balance`: 15000

- **Account 3: Kenji's Global Account**
  - `iban`: "US23456789012345678901"
  - `balance`: 100000

- **Account 4: Shared Euro Account**
  - `iban`: "DE12345098765432109876"
  - `balance`: 20000

#### Relationships

- **Ownership**
  - John Doe owns "John's Global Account" with IBAN "US12345678901234567890".
  - Maria Müller owns "Maria's Euro Account" with IBAN "DE09876543210987654321".
  - Kenji Yamamoto owns "Kenji's Global Account" with IBAN "US23456789012345678901".
  - Shared ownership: Maria Müller owns "Shared Euro Account" with IBAN "DE12345098765432109876".

- **Use**
  - John Doe can use "John's Global Account" and "Shared Euro Account".
  - Maria Müller can use "Maria's Euro Account" and "Shared Euro Account".
  - Kenji Yamamoto can use "Kenji's Global Account" and "Shared Euro Account".

- **AccountOfBanks**
  - "John's Global Account" and "Kenji's Global Account" are associated with "Global Bank".
  - "Maria's Euro Account" and "Shared Euro Account" are associated with "EuroBank".

### Complex Instance 2: Family-Run Accounts with Global Presence

#### Entities

- **Bank: Family Trust Bank**
  - `country`: "Australia"
  - `name`: "Family Trust Bank"
  - `bic`: "AUFTBANK"

- **Person 1: Emily Wang**
  - `firstName`: "Emily"
  - `lastName`: "Wang"
  - `age`: 40

- **Person 2: Liam Smith**
  - `firstName`: "Liam"
  - `lastName`: "Smith"
  - `age`: 42

- **Person 3: Noah Wang**
  - `firstName`: "Noah"
  - `lastName`: "Wang"
  - `age`: 19

- **Account 1: Emily's Main Account**
  - `iban`: "AU12345678098765432100"
  - `balance`: 25000

- **Account 2: Household Savings**
  - `iban`: "AU00987654321012345678"
  - `balance`: 50000

- **Account 3: Noah's Education Fund**
  - `iban`: "AU11223344556677889900"
  - `balance`: 10000

#### Relationships

- **Ownership**
  - Emily Wang owns "Emily's Main Account" with IBAN "AU12345678098765432100".
  - Emily Wang and Liam Smith jointly own "Household Savings" with IBAN "AU00987654321012345678".
  - Noah Wang owns "Noah's Education Fund" with IBAN "AU11223344556677889900".

- **Use**
  - Emily Wang can use all three accounts: "Emily's Main Account", "Household Savings", and "Noah's Education Fund".
  - Liam Smith can use "Household Savings".
  - Noah Wang can use "Noah's Education Fund".

- **AccountOfBanks**
  - All accounts "Emily's Main Account", "Household Savings", and "Noah's Education Fund" are associated with "Family Trust Bank".

These complex instances demonstrate a diverse interaction of constraints, relationships, and multiplicities, showcasing multi-national banking scenarios and family-run accounts with shared and individual ownerships.
```
!new Bank('bank1')
!bank1.country := 'United States'
!bank1.name := 'Global Bank'
!bank1.bic := 'USGLBANK'

!new Bank('bank2')
!bank2.country := 'Germany'
!bank2.name := 'EuroBank'
!bank2.bic := 'DEEBANK'

!new Person('person1')
!person1.age := 35
!person1.firstName := 'John'
!person1.lastName := 'Doe'

!new Person('person2')
!person2.age := 28
!person2.firstName := 'Maria'
!person2.lastName := 'Müller'

!new Person('person3')
!person3.age := 45
!person3.firstName := 'Kenji'
!person3.lastName := 'Yamamoto'

!new Account('account1')
!account1.iban := 'US12345678901234567890'
!account1.balance := 5000

!new Account('account2')
!account2.iban := 'DE09876543210987654321'
!account2.balance := 15000

!new Account('account3')
!account3.iban := 'US23456789012345678901'
!account3.balance := 100000

!new Account('account4')
!account4.iban := 'DE12345098765432109876'
!account4.balance := 20000

!insert (person1, account1) into Ownership
!insert (person2, account2) into Ownership
!insert (person3, account3) into Ownership
!insert (person2, account4) into Ownership

!insert (person1, account1) into Use
!insert (person1, account4) into Use
!insert (person2, account2) into Use
!insert (person2, account4) into Use
!insert (person3, account3) into Use
!insert (person3, account4) into Use

!insert (bank1, account1) into AccountOfBanks
!insert (bank1, account3) into AccountOfBanks
!insert (bank2, account2) into AccountOfBanks
!insert (bank2, account4) into AccountOfBanks

!new Bank('bank3')
!bank3.country := 'Australia'
!bank3.name := 'Family Trust Bank'
!bank3.bic := 'AUFTBANK'

!new Person('person4')
!person4.age := 40
!person4.firstName := 'Emily'
!person4.lastName := 'Wang'

!new Person('person5')
!person5.age := 42
!person5.firstName := 'Liam'
!person5.lastName := 'Smith'

!new Person('person6')
!person6.age := 19
!person6.firstName := 'Noah'
!person6.lastName := 'Wang'

!new Account('account5')
!account5.iban := 'AU12345678098765432100'
!account5.balance := 25000

!new Account('account6')
!account6.iban := 'AU00987654321012345678'
!account6.balance := 50000

!new Account('account7')
!account7.iban := 'AU11223344556677889900'
!account7.balance := 10000

!insert (person4, account5) into Ownership
!insert (person4, account6) into Ownership
!insert (person5, account6) into Ownership
!insert (person6, account7) into Ownership

!insert (person4, account5) into Use
!insert (person4, account6) into Use
!insert (person4, account7) into Use
!insert (person5, account6) into Use
!insert (person6, account7) into Use

!insert (bank3, account5) into AccountOfBanks
!insert (bank3, account6) into AccountOfBanks
!insert (bank3, account7) into AccountOfBanks
```

# Category: Unrealistc but valid
### Unrealistic but Valid Instance 1: A Bank with Accounts Owned by Incredibly Rich Individuals

#### Entities

- **Bank: Lunar Bank**
  - `country`: "Lunar Colony"
  - `name`: "Lunar Bank"
  - `bic`: "LUNAR001"

- **Person 1: Zog the Wealthy**
  - `firstName`: "Zog"
  - `lastName`: "Wealthy"
  - `age`: 150

- **Person 2: Xara the Magnificent**
  - `firstName`: "Xara"
  - `lastName`: "Magnificent"
  - `age`: 200

- **Account 1: Zog's Infinite Fortune**
  - `iban`: "LU12345678901234567890"
  - `balance`: 999999999999

- **Account 2: Xara's Galactic Wealth**
  - `iban`: "LU09876543210987654321"
  - `balance`: 999999999999

#### Relationships

- **Ownership**
  - Zog the Wealthy owns "Zog's Infinite Fortune" with IBAN "LU12345678901234567890".
  - Xara the Magnificent owns "Xara's Galactic Wealth" with IBAN "LU09876543210987654321".

- **Use**
  - Zog the Wealthy and Xara the Magnificent can use both "Zog's Infinite Fortune" and "Xara's Galactic Wealth".

- **AccountOfBanks**
  - Both accounts "Zog's Infinite Fortune" and "Xara's Galactic Wealth" are associated with "Lunar Bank".

### Unrealistic but Valid Instance 2: A Bank Without Physical Presence and Child Owners in a Fantasy World

#### Entities

- **Bank: Virtual Bank of Atlantis**
  - `country`: "Atlantis"
  - `name`: "Virtual Bank of Atlantis"
  - `bic`: "ATLANTIS"

- **Person 1: Alice from Wonderland**
  - `firstName`: "Alice"
  - `lastName`: "Wonderland"
  - `age`: 19

- **Person 2: Peter Pan**
  - `firstName`: "Peter"
  - `lastName`: "Pan"
  - `age`: 19

- **Account 1: Alice's Imaginary Wealth**
  - `iban`: "AT12345678901234567890"
  - `balance`: 500

- **Account 2: Peter's Neverland Fortune**
  - `iban`: "AT09876543210987654321"
  - `balance`: 1000

#### Relationships

- **Ownership**
  - Alice from Wonderland owns "Alice's Imaginary Wealth" with IBAN "AT12345678901234567890".
  - Peter Pan owns "Peter's Neverland Fortune" with IBAN "AT09876543210987654321".

- **Use**
  - Alice from Wonderland and Peter Pan can use both "Alice's Imaginary Wealth" and "Peter's Neverland Fortune".

- **AccountOfBanks**
  - Both accounts "Alice's Imaginary Wealth" and "Peter's Neverland Fortune" are associated with "Virtual Bank of Atlantis".

### Unrealistic but Valid Instance 3: A Bank with a Single Account Used by Hundreds

#### Entities

- **Bank: United Bank of Everywhere**
  - `country`: "Global"
  - `name`: "United Bank of Everywhere"
  - `bic`: "GLOBALLY"

- **Person 1: John Global**
  - `firstName`: "John"
  - `lastName`: "Global"
  - `age`: 30

- **Account 1: World's Shared Account**
  - `iban`: "GB12345678901234567890"
  - `balance`: 10000

- **Additional Persons (for demonstration)**
  - 100 individuals named `User1` to `User100`, each with `age` 30.

#### Relationships

- **Ownership**
  - John Global owns "World's Shared Account" with IBAN "GB12345678901234567890".

- **Use**
  - John Global and all individuals from `User1` to `User100` can use "World's Shared Account".

- **AccountOfBanks**
  - "World's Shared Account" is associated with "United Bank of Everywhere".

These instances show implausible but valid scenarios such as accounts owned by fantasy characters, extreme wealth, and massive shared access, all adhering to the constraints and relationships defined in the UML model.
```
!new Bank('bank1')
!bank1.country := 'Lunar Colony'
!bank1.name := 'Lunar Bank'
!bank1.bic := 'LUNAR001'

!new Person('person1')
!person1.age := 150
!person1.firstName := 'Zog'
!person1.lastName := 'Wealthy'

!new Person('person2')
!person2.age := 200
!person2.firstName := 'Xara'
!person2.lastName := 'Magnificent'

!new Account('account1')
!account1.iban := 'LU12345678901234567890'
!account1.balance := 999999999999

!new Account('account2')
!account2.iban := 'LU09876543210987654321'
!account2.balance := 999999999999

!insert (person1, account1) into Ownership
!insert (person2, account2) into Ownership

!insert (person1, account1) into Use
!insert (person1, account2) into Use
!insert (person2, account1) into Use
!insert (person2, account2) into Use

!insert (bank1, account1) into AccountOfBanks
!insert (bank1, account2) into AccountOfBanks

!new Bank('bank2')
!bank2.country := 'Atlantis'
!bank2.name := 'Virtual Bank of Atlantis'
!bank2.bic := 'ATLANTIS'

!new Person('person3')
!person3.age := 19
!person3.firstName := 'Alice'
!person3.lastName := 'Wonderland'

!new Person('person4')
!person4.age := 19
!person4.firstName := 'Peter'
!person4.lastName := 'Pan'

!new Account('account3')
!account3.iban := 'AT12345678901234567890'
!account3.balance := 500

!new Account('account4')
!account4.iban := 'AT09876543210987654321'
!account4.balance := 1000

!insert (person3, account3) into Ownership
!insert (person4, account4) into Ownership

!insert (person3, account3) into Use
!insert (person3, account4) into Use
!insert (person4, account3) into Use
!insert (person4, account4) into Use

!insert (bank2, account3) into AccountOfBanks
!insert (bank2, account4) into AccountOfBanks

!new Bank('bank3')
!bank3.country := 'Global'
!bank3.name := 'United Bank of Everywhere'
!bank3.bic := 'GLOBALLY'

!new Person('person5')
!person5.age := 30
!person5.firstName := 'John'
!person5.lastName := 'Global'

!new Account('account5')
!account5.iban := 'GB12345678901234567890'
!account5.balance := 10000

!insert (person5, account5) into Ownership

!insert (person5, account5) into Use

!insert (bank3, account5) into AccountOfBanks

!for i in range(1, 101):
    !new Person(f'user{i}')
    !user{i}.age := 30
    !user{i}.firstName := f'User{i}'
    !user{i}.lastName := f'UserLast{i}'
    !insert (f'user{i}', account5) into Use
```

# Category: Realistic but invalid
### Realistic but Invalid Instance 1: Joint Account Ownership

#### Entities

- **Bank: Friendly Bank**
  - `country`: "Canada"
  - `name`: "Friendly Bank"
  - `bic`: "CANFRIEND"

- **Person 1: Sarah Brown**
  - `firstName`: "Sarah"
  - `lastName`: "Brown"
  - `age`: 30

- **Person 2: Michael Brown**
  - `firstName`: "Michael"
  - `lastName`: "Brown"
  - `age`: 32

- **Account: Joint Checking Account**
  - `iban`: "CA12345678901234567890"
  - `balance`: 5000

#### Relationships

- **Violation:** The account "Joint Checking Account" is realistically owned by both Sarah and Michael Brown, which violates the constraint that an account must have exactly one owner.

- **Use**
  - Sarah Brown and Michael Brown can use "Joint Checking Account".

- **AccountOfBanks**
  - "Joint Checking Account" is associated with "Friendly Bank".

### Realistic but Invalid Instance 2: Child-Owned Savings Account

#### Entities

- **Bank: Future Bank**
  - `country`: "United Kingdom"
  - `name`: "Future Bank"
  - `bic`: "UKFUTURE"

- **Person: Emily Green**
  - `firstName`: "Emily"
  - `lastName`: "Green"
  - `age`: 16

- **Account: Emily's Savings Account**
  - `iban`: "GB09876543210987654321"
  - `balance`: 1000

#### Relationships

- **Violation:** Emily Green is a minor owning "Emily's Savings Account", which violates the invariant that account owners must be adults.

- **Use**
  - Emily Green can use "Emily's Savings Account".

- **AccountOfBanks**
  - "Emily's Savings Account" is associated with "Future Bank".

### Realistic but Invalid Instance 3: Account with Temporary Overdraft

#### Entities

- **Bank: City Bank**
  - `country`: "USA"
  - `name`: "City Bank"
  - `bic`: "USACITYBK"

- **Person: John Smith**
  - `firstName`: "John"
  - `lastName`: "Smith"
  - `age`: 45

- **Account: John's Overdrawn Account**
  - `iban`: "US23456789012345678901"
  - `balance`: -50

#### Relationships

- **Violation:** "John's Overdrawn Account" has a negative balance due to an overdraft, which violates the invariant that account balances must always be positive.

- **Ownership**
  - John Smith owns "John's Overdrawn Account".

- **Use**
  - John Smith can use "John's Overdrawn Account".

- **AccountOfBanks**
  - "John's Overdrawn Account" is associated with "City Bank".

These instances demonstrate real-life scenarios such as joint accounts, child-owned savings accounts, and temporary overdrafts that are common in banking but violate the model's constraints, highlighting potential areas for model refinement.
```
!new Bank('bank1')
!bank1.country := 'Canada'
!bank1.name := 'Friendly Bank'
!bank1.bic := 'CANFRIEND'

!new Person('person1')
!person1.age := 30
!person1.firstName := 'Sarah'
!person1.lastName := 'Brown'

!new Person('person2')
!person2.age := 32
!person2.firstName := 'Michael'
!person2.lastName := 'Brown'

!new Account('account1')
!account1.iban := 'CA12345678901234567890'
!account1.balance := 5000

!insert (person1, account1) into Ownership
!insert (person2, account1) into Ownership  // Violation: More than one owner

!insert (person1, account1) into Use
!insert (person2, account1) into Use

!insert (bank1, account1) into AccountOfBanks

!new Bank('bank2')
!bank2.country := 'United Kingdom'
!bank2.name := 'Future Bank'
!bank2.bic := 'UKFUTURE'

!new Person('person3')
!person3.age := 16
!person3.firstName := 'Emily'
!person3.lastName := 'Green'

!new Account('account2')
!account2.iban := 'GB09876543210987654321'
!account2.balance := 1000

!insert (person3, account2) into Ownership  // Violation: Owner is a minor

!insert (person3, account2) into Use

!insert (bank2, account2) into AccountOfBanks

!new Bank('bank3')
!bank3.country := 'USA'
!bank3.name := 'City Bank'
!bank3.bic := 'USACITYBK'

!new Person('person4')
!person4.age := 45
!person4.firstName := 'John'
!person4.lastName := 'Smith'

!new Account('account3')
!account3.iban := 'US23456789012345678901'
!account3.balance := -50  // Violation: Negative balance

!insert (person4, account3) into Ownership

!insert (person4, account3) into Use

!insert (bank3, account3) into AccountOfBanks
```

# Category: Baseline Instances
### Baseline Instance 1: Standard Single-Account Holder

#### Entities

- **Bank: National Bank**
  - `country`: "USA"
  - `name`: "National Bank"
  - `bic`: "USNATBANK"

- **Person: Alice Johnson**
  - `firstName`: "Alice"
  - `lastName`: "Johnson"
  - `age`: 30

- **Account: Alice's Checking Account**
  - `iban`: "US12345678901234567890"
  - `balance`: 2500

#### Relationships

- **Ownership**
  - Alice Johnson owns "Alice's Checking Account" with IBAN "US12345678901234567890".

- **Use**
  - Alice Johnson can use "Alice's Checking Account".

- **AccountOfBanks**
  - "Alice's Checking Account" is associated with "National Bank".

### Baseline Instance 2: Typical Family with Multiple Accounts

#### Entities

- **Bank: Family Bank**
  - `country`: "Canada"
  - `name`: "Family Bank"
  - `bic`: "CANFAMBANK"

- **Person 1: David Miller**
  - `firstName`: "David"
  - `lastName`: "Miller"
  - `age`: 45

- **Person 2: Emma Miller**
  - `firstName`: "Emma"
  - `lastName`: "Miller"
  - `age`: 42

- **Account 1: David's Savings Account**
  - `iban`: "CA12345678901234567890"
  - `balance`: 15000

- **Account 2: Emma's Personal Account**
  - `iban`: "CA09876543210987654321"
  - `balance`: 5000

- **Account 3: Miller Family Joint Account**
  - `iban`: "CA11223344556677889900"
  - `balance`: 10000

#### Relationships

- **Ownership**
  - David Miller owns "David's Savings Account" with IBAN "CA12345678901234567890".
  - Emma Miller owns "Emma's Personal Account" with IBAN "CA09876543210987654321".
  - Both David and Emma Miller jointly own "Miller Family Joint Account" (represented by two ownerships in real implementation).

- **Use**
  - David Miller can use "David's Savings Account" and "Miller Family Joint Account".
  - Emma Miller can use "Emma's Personal Account" and "Miller Family Joint Account".

- **AccountOfBanks**
  - All accounts "David's Savings Account", "Emma's Personal Account", and "Miller Family Joint Account" are associated with "Family Bank".

### Baseline Instance 3: Business Account with Multiple Users

#### Entities

- **Bank: Business Bank**
  - `country`: "UK"
  - `name`: "Business Bank"
  - `bic`: "UKBUSBANK"

- **Person 1: Oliver Brown**
  - `firstName`: "Oliver"
  - `lastName`: "Brown"
  - `age`: 38

- **Person 2: Sophie White**
  - `firstName`: "Sophie"
  - `lastName`: "White"
  - `age`: 36

- **Account: Company Expense Account**
  - `iban`: "GB12345678901234567890"
  - `balance`: 30000

#### Relationships

- **Ownership**
  - Oliver Brown owns "Company Expense Account" with IBAN "GB12345678901234567890".

- **Use**
  - Oliver Brown and Sophie White can use "Company Expense Account".

- **AccountOfBanks**
  - "Company Expense Account" is associated with "Business Bank".

These baseline instances cover typical banking scenarios, including individual account holders, family accounts with joint ownership, and business accounts with multiple users, ensuring all components and relationships of the model are represented in practical configurations.
```
!new Bank('bank1')
!bank1.country := 'USA'
!bank1.name := 'National Bank'
!bank1.bic := 'USNATBANK'

!new Person('person1')
!person1.age := 30
!person1.firstName := 'Alice'
!person1.lastName := 'Johnson'

!new Account('account1')
!account1.iban := 'US12345678901234567890'
!account1.balance := 2500

!insert (person1, account1) into Ownership
!insert (person1, account1) into Use
!insert (bank1, account1) into AccountOfBanks

!new Bank('bank2')
!bank2.country := 'Canada'
!bank2.name := 'Family Bank'
!bank2.bic := 'CANFAMBANK'

!new Person('person2')
!person2.age := 45
!person2.firstName := 'David'
!person2.lastName := 'Miller'

!new Person('person3')
!person3.age := 42
!person3.firstName := 'Emma'
!person3.lastName := 'Miller'

!new Account('account2')
!account2.iban := 'CA12345678901234567890'
!account2.balance := 15000

!new Account('account3')
!account3.iban := 'CA09876543210987654321'
!account3.balance := 5000

!new Account('account4')
!account4.iban := 'CA11223344556677889900'
!account4.balance := 10000

!insert (person2, account2) into Ownership
!insert (person3, account3) into Ownership
!insert (person2, account4) into Ownership
!insert (person3, account4) into Ownership

!insert (person2, account2) into Use
!insert (person3, account3) into Use
!insert (person2, account4) into Use
!insert (person3, account4) into Use

!insert (bank2, account2) into AccountOfBanks
!insert (bank2, account3) into AccountOfBanks
!insert (bank2, account4) into AccountOfBanks

!new Bank('bank3')
!bank3.country := 'UK'
!bank3.name := 'Business Bank'
!bank3.bic := 'UKBUSBANK'

!new Person('person4')
!person4.age := 38
!person4.firstName := 'Oliver'
!person4.lastName := 'Brown'

!new Person('person5')
!person5.age := 36
!person5.firstName := 'Sophie'
!person5.lastName := 'White'

!new Account('account5')
!account5.iban := 'GB12345678901234567890'
!account5.balance := 30000

!insert (person4, account5) into Ownership
!insert (person4, account5) into Use
!insert (person5, account5) into Use

!insert (bank3, account5) into AccountOfBanks
```