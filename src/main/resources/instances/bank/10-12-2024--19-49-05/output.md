# Description

The UML-based conceptual model represents a banking system with three primary classes: Bank, Account, and Person. Each class includes specific attributes that describe their characteristics and functionalities.

- **Bank**: This class has three attributes:
  - `country` (String): Represents the country where the bank is located.
  - `name` (String): The name of the bank.
  - `bic` (String): The Bank Identifier Code used for international transactions.

- **Account**: This class has two attributes:
  - `iban` (String): The International Bank Account Number, a unique identifier for accounts.
  - `balance` (Integer): Represents the account balance, which is a numerical value.

- **Person**: This class has three attributes:
  - `firstName` (String): The first name of the person.
  - `lastName` (String): The last name of the person.
  - `age` (Integer): The age of the person.

# Relationships

- **Ownership Association**: This association links `Person` and `Account`.
  - A `Person` (owner) can own multiple `Accounts` (1 to many, denoted as [*]).
  - An `Account` is owned by exactly one `Person` (1).

- **Use Association**: This association connects `Person` and `Account`.
  - A `Person` (user) can use multiple `Accounts` (0 to many, denoted as [*]).
  - An `Account` can be used by multiple `Persons` (0 to many, denoted as [*]).

- **AccountOfBanks Composition**: This composition ties `Bank` and `Account`.
  - A `Bank` has multiple `Accounts` (0 to many, denoted as [*]).
  - An `Account` belongs to exactly one `Bank` (1).

# Invariants

- **AdultOwners**: This invariant ensures that every `Account` must be owned by a `Person` who is over 18 years of age. This is expressed as `self.owner.age > 18`.

- **positiveBalance**: This invariant mandates that every `Account` must maintain a positive balance. This is expressed as `self.balance > 0`.

# Category: Baseline Instances
Sure, let's create a set of baseline instances for the banking system model that includes all required classes, attributes, relationships, and respects the given constraints.

### Instance 1: European Bank Scenario

**Bank Instance:**
- `country`: "Germany"
- `name`: "Deutsche Bank"
- `bic`: "DEUTDEFF"

**Account Instances:**

1. **Account 1:**
   - `iban`: "DE89370400440532013000"
   - `balance`: 5000
   - **Owner**: 
     - **Person Instance:**
       - `firstName`: "Hans"
       - `lastName`: "Müller"
       - `age`: 45
   - **User**:
     - **Person Instance:**
       - `firstName`: "Anna"
       - `lastName`: "Schmidt"
       - `age`: 22

2. **Account 2:**
   - `iban`: "DE89370400440532013001"
   - `balance`: 1200
   - **Owner**:
     - **Person Instance:**
       - `firstName`: "Lisa"
       - `lastName`: "Weber"
       - `age`: 30
   - **User**:
     - **Person Instance:**
       - `firstName`: "Peter"
       - `lastName`: "Fischer"
       - `age`: 28

### Instance 2: Asian Bank Scenario

**Bank Instance:**
- `country`: "Japan"
- `name`: "Mizuho Bank"
- `bic`: "MHCBJPJT"

**Account Instances:**

1. **Account 3:**
   - `iban`: "JP12345678901234567890"
   - `balance`: 7500
   - **Owner**:
     - **Person Instance:**
       - `firstName`: "Akira"
       - `lastName`: "Tanaka"
       - `age`: 60
   - **User**:
     - **Person Instance:**
       - `firstName`: "Yuki"
       - `lastName`: "Sato"
       - `age`: 35

2. **Account 4:**
   - `iban`: "JP09876543210987654321"
   - `balance`: 3000
   - **Owner**:
     - **Person Instance:**
       - `firstName`: "Naoko"
       - `lastName`: "Kobayashi"
       - `age`: 40
   - **User**:
     - **Person Instance:**
       - `firstName`: "Kenji"
       - `lastName`: "Suzuki"
       - `age`: 38

### Instance 3: North American Bank Scenario

**Bank Instance:**
- `country`: "USA"
- `name`: "Bank of America"
- `bic`: "BOFAUS3N"

**Account Instances:**

1. **Account 5:**
   - `iban`: "US12345678901234567890"
   - `balance`: 9800
   - **Owner**:
     - **Person Instance:**
       - `firstName`: "John"
       - `lastName`: "Doe"
       - `age`: 50
   - **User**:
     - **Person Instance:**
       - `firstName`: "Emily"
       - `lastName`: "Johnson"
       - `age`: 25

2. **Account 6:**
   - `iban`: "US09876543210987654321"
   - `balance`: 1500
   - **Owner**:
     - **Person Instance:**
       - `firstName`: "Michael"
       - `lastName`: "Smith"
       - `age`: 42
   - **User**:
     - **Person Instance:**
       - `firstName`: "Jessica"
       - `lastName`: "Brown"
       - `age`: 29

### Structural and Regional Diversity

- The instances cover three different regions: Europe, Asia, and North America, highlighting diverse banking environments.
- Each scenario includes varied names and cultural backgrounds for persons, reflecting linguistic diversity.
- The accounts have different balances, and the people have varying ages and multiple users per account, demonstrating structural diversity.

These instances adhere to the model's constraints, such as ensuring all account owners are adults and balances are positive, while also illustrating the relationships between the entities.
```
### Instance 1: European Bank Scenario

!new Bank('bank1')
!bank1.country := 'Germany'
!bank1.name := 'Deutsche Bank'
!bank1.bic := 'DEUTDEFF'

!new Account('account1')
!account1.iban := 'DE89370400440532013000'
!account1.balance := 5000

!new Person('person1')
!person1.age := 45
!person1.firstName := 'Hans'
!person1.lastName := 'Müller'

!new Person('person2')
!person2.age := 22
!person2.firstName := 'Anna'
!person2.lastName := 'Schmidt'

!insert (person1, account1) into Ownership
!insert (person1, account1) into Use
!insert (person2, account1) into Use
!insert (bank1, account1) into AccountOfBanks

!new Account('account2')
!account2.iban := 'DE89370400440532013001'
!account2.balance := 1200

!new Person('person3')
!person3.age := 30
!person3.firstName := 'Lisa'
!person3.lastName := 'Weber'

!new Person('person4')
!person4.age := 28
!person4.firstName := 'Peter'
!person4.lastName := 'Fischer'

!insert (person3, account2) into Ownership
!insert (person3, account2) into Use
!insert (person4, account2) into Use
!insert (bank1, account2) into AccountOfBanks

### Instance 2: Asian Bank Scenario

!new Bank('bank2')
!bank2.country := 'Japan'
!bank2.name := 'Mizuho Bank'
!bank2.bic := 'MHCBJPJT'

!new Account('account3')
!account3.iban := 'JP12345678901234567890'
!account3.balance := 7500

!new Person('person5')
!person5.age := 60
!person5.firstName := 'Akira'
!person5.lastName := 'Tanaka'

!new Person('person6')
!person6.age := 35
!person6.firstName := 'Yuki'
!person6.lastName := 'Sato'

!insert (person5, account3) into Ownership
!insert (person5, account3) into Use
!insert (person6, account3) into Use
!insert (bank2, account3) into AccountOfBanks

!new Account('account4')
!account4.iban := 'JP09876543210987654321'
!account4.balance := 3000

!new Person('person7')
!person7.age := 40
!person7.firstName := 'Naoko'
!person7.lastName := 'Kobayashi'

!new Person('person8')
!person8.age := 38
!person8.firstName := 'Kenji'
!person8.lastName := 'Suzuki'

!insert (person7, account4) into Ownership
!insert (person7, account4) into Use
!insert (person8, account4) into Use
!insert (bank2, account4) into AccountOfBanks

### Instance 3: North American Bank Scenario

!new Bank('bank3')
!bank3.country := 'USA'
!bank3.name := 'Bank of America'
!bank3.bic := 'BOFAUS3N'

!new Account('account5')
!account5.iban := 'US12345678901234567890'
!account5.balance := 9800

!new Person('person9')
!person9.age := 50
!person9.firstName := 'John'
!person9.lastName := 'Doe'

!new Person('person10')
!person10.age := 25
!person10.firstName := 'Emily'
!person10.lastName := 'Johnson'

!insert (person9, account5) into Ownership
!insert (person9, account5) into Use
!insert (person10, account5) into Use
!insert (bank3, account5) into AccountOfBanks

!new Account('account6')
!account6.iban := 'US09876543210987654321'
!account6.balance := 1500

!new Person('person11')
!person11.age := 42
!person11.firstName := 'Michael'
!person11.lastName := 'Smith'

!new Person('person12')
!person12.age := 29
!person12.firstName := 'Jessica'
!person12.lastName := 'Brown'

!insert (person11, account6) into Ownership
!insert (person11, account6) into Use
!insert (person12, account6) into Use
!insert (bank3, account6) into AccountOfBanks
```