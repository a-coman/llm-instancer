# Description

The given UML-based specification defines a conceptual model of a banking system. The primary purpose of this model is to represent the relationships and constraints among banks, accounts, and individuals (persons) within this system. The model includes three main classes: `Bank`, `Account`, and `Person`.

## Components

### Class: Bank
- **Attributes**:
  - `country: String`: Represents the country in which the bank is located.
  - `name: String`: The name of the bank.
  - `bic: String`: The Bank Identifier Code, a unique identifier for the bank.
- **Purpose**: This class encapsulates the details of a bank.

### Class: Account
- **Attributes**:
  - `iban: String`: The International Bank Account Number, uniquely identifying the account.
  - `balance: Integer`: The current balance of the account.
- **Purpose**: Represents a bank account with financial information.

### Class: Person
- **Attributes**:
  - `firstName: String`: The first name of the person.
  - `lastName: String`: The last name of the person.
  - `age: Integer`: The age of the person.
- **Purpose**: Represents an individual who can own or use bank accounts.

# Relationships

### Association: Ownership
- **Between**: `Person` and `Account`
- **Multiplicity**:
  - `Person [1]` (owner): Each account must have exactly one owner.
  - `Account [*]` (accounts): A person can own multiple accounts.

### Association: Use
- **Between**: `Person` and `Account`
- **Multiplicity**:
  - `Person [*]` (user): A person can be a user of multiple accounts.
  - `Account [*]` (usedAccounts): An account can be used by multiple people.

### Composition: AccountOfBanks
- **Between**: `Bank` and `Account`
- **Multiplicity**:
  - `Bank [1]` (bank): Each account belongs to exactly one bank.
  - `Account [*]` (accounts): A bank can have multiple accounts.

# Invariants

### Invariant: AdultOwners
- **Expression**: `self.owner.age > 18`
- **Description**: Ensures that every account owner must be over 18 years of age, implying that only adults can own bank accounts.

### Invariant: PositiveBalance
- **Expression**: `self.balance > 0`
- **Description**: Ensures that every account must have a positive balance, indicating that accounts cannot be overdrawn.

# Category: Baseline Instances
Below are diverse baseline instances that represent the conceptual model of a banking system, ensuring that all classes and relationships are covered:

### Instance 1: Standard European Bank
- **Bank**:
  - `country`: Germany
  - `name`: Deutsche Bank
  - `bic`: DEUTDEFF

- **Accounts**:
  - **Account 1**:
    - `iban`: DE89370400440532013000
    - `balance`: 1500
  - **Account 2**:
    - `iban`: DE89370400440532014000
    - `balance`: 2000

- **Persons**:
  - **Person 1**:
    - `firstName`: Klaus
    - `lastName`: Müller
    - `age`: 45
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1, Account 2
  - **Person 2**:
    - `firstName`: Anna
    - `lastName`: Schmidt
    - `age`: 30
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 2

### Instance 2: North American Bank with Multinational Users
- **Bank**:
  - `country`: United States
  - `name`: Bank of America
  - `bic`: BOFAUS3N

- **Accounts**:
  - **Account 1**:
    - `iban`: US12345678901234567890
    - `balance`: 2500

- **Persons**:
  - **Person 1**:
    - `firstName`: John
    - `lastName`: Doe
    - `age`: 50
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Maria
    - `lastName`: Gonzalez
    - `age`: 28
    - **Use**: Uses Account 1

### Instance 3: Asian Bank with Young Users
- **Bank**:
  - `country`: Japan
  - `name`: Mitsubishi UFJ Financial Group
  - `bic`: BOTKJPJT

- **Accounts**:
  - **Account 1**:
    - `iban`: JP7894561230123456789
    - `balance`: 10000

- **Persons**:
  - **Person 1**:
    - `firstName`: Hiroshi
    - `lastName`: Tanaka
    - `age`: 60
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Yuki
    - `lastName`: Sato
    - `age`: 25
    - **Use**: Uses Account 1

### Instance 4: African Bank with Community Accounts
- **Bank**:
  - `country`: South Africa
  - `name`: Standard Bank
  - `bic`: SBZAZAJJ

- **Accounts**:
  - **Account 1**:
    - `iban`: ZA1234567890123456789
    - `balance`: 500
  - **Account 2**:
    - `iban`: ZA9876543210987654321
    - `balance`: 800

- **Persons**:
  - **Person 1**:
    - `firstName`: Sipho
    - `lastName`: Ndlovu
    - `age`: 40
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1, Account 2
  - **Person 2**:
    - `firstName`: Thandi
    - `lastName`: Mokoena
    - `age`: 35
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 1, Account 2

### Instance 5: Middle Eastern Bank with Dual Ownership
- **Bank**:
  - `country`: United Arab Emirates
  - `name`: Emirates NBD
  - `bic`: EBILAEAD

- **Accounts**:
  - **Account 1**:
    - `iban`: AE070331234567890123456
    - `balance`: 3000

- **Persons**:
  - **Person 1**:
    - `firstName`: Ahmed
    - `lastName`: Al-Farsi
    - `age`: 34
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Fatima
    - `lastName`: Al-Mansoori
    - `age`: 29
    - **Use**: Uses Account 1

These instances cover a broad spectrum of regional and structural diversity, ensuring each class and relationship in the model is represented while respecting the outlined constraints and invariants.
```
Below are the instances created based on the provided conceptual model of a banking system, formatted according to the syntax example provided:

### Instance 1: Standard European Bank

!new Bank('bank1')
!bank1.country := 'Germany'
!bank1.name := 'Deutsche Bank'
!bank1.bic := 'DEUTDEFF'

!new Account('account1_1')
!account1_1.iban := 'DE89370400440532013000'
!account1_1.balance := 1500

!new Account('account1_2')
!account1_2.iban := 'DE89370400440532014000'
!account1_2.balance := 2000

!new Person('person1_1')
!person1_1.age := 45
!person1_1.firstName := 'Klaus'
!person1_1.lastName := 'Müller'

!new Person('person1_2')
!person1_2.age := 30
!person1_2.firstName := 'Anna'
!person1_2.lastName := 'Schmidt'

!insert (person1_1, account1_1) into Ownership
!insert (person1_1, account1_1) into Use
!insert (person1_1, account1_2) into Use
!insert (person1_2, account1_2) into Ownership
!insert (person1_2, account1_2) into Use
!insert (bank1, account1_1) into AccountOfBanks
!insert (bank1, account1_2) into AccountOfBanks

### Instance 2: North American Bank with Multinational Users

!new Bank('bank2')
!bank2.country := 'United States'
!bank2.name := 'Bank of America'
!bank2.bic := 'BOFAUS3N'

!new Account('account2_1')
!account2_1.iban := 'US12345678901234567890'
!account2_1.balance := 2500

!new Person('person2_1')
!person2_1.age := 50
!person2_1.firstName := 'John'
!person2_1.lastName := 'Doe'

!new Person('person2_2')
!person2_2.age := 28
!person2_2.firstName := 'Maria'
!person2_2.lastName := 'Gonzalez'

!insert (person2_1, account2_1) into Ownership
!insert (person2_1, account2_1) into Use
!insert (person2_2, account2_1) into Use
!insert (bank2, account2_1) into AccountOfBanks

### Instance 3: Asian Bank with Young Users

!new Bank('bank3')
!bank3.country := 'Japan'
!bank3.name := 'Mitsubishi UFJ Financial Group'
!bank3.bic := 'BOTKJPJT'

!new Account('account3_1')
!account3_1.iban := 'JP7894561230123456789'
!account3_1.balance := 10000

!new Person('person3_1')
!person3_1.age := 60
!person3_1.firstName := 'Hiroshi'
!person3_1.lastName := 'Tanaka'

!new Person('person3_2')
!person3_2.age := 25
!person3_2.firstName := 'Yuki'
!person3_2.lastName := 'Sato'

!insert (person3_1, account3_1) into Ownership
!insert (person3_1, account3_1) into Use
!insert (person3_2, account3_1) into Use
!insert (bank3, account3_1) into AccountOfBanks

### Instance 4: African Bank with Community Accounts

!new Bank('bank4')
!bank4.country := 'South Africa'
!bank4.name := 'Standard Bank'
!bank4.bic := 'SBZAZAJJ'

!new Account('account4_1')
!account4_1.iban := 'ZA1234567890123456789'
!account4_1.balance := 500

!new Account('account4_2')
!account4_2.iban := 'ZA9876543210987654321'
!account4_2.balance := 800

!new Person('person4_1')
!person4_1.age := 40
!person4_1.firstName := 'Sipho'
!person4_1.lastName := 'Ndlovu'

!new Person('person4_2')
!person4_2.age := 35
!person4_2.firstName := 'Thandi'
!person4_2.lastName := 'Mokoena'

!insert (person4_1, account4_1) into Ownership
!insert (person4_1, account4_1) into Use
!insert (person4_1, account4_2) into Use
!insert (person4_2, account4_2) into Ownership
!insert (person4_2, account4_1) into Use
!insert (person4_2, account4_2) into Use
!insert (bank4, account4_1) into AccountOfBanks
!insert (bank4, account4_2) into AccountOfBanks

### Instance 5: Middle Eastern Bank with Dual Ownership

!new Bank('bank5')
!bank5.country := 'United Arab Emirates'
!bank5.name := 'Emirates NBD'
!bank5.bic := 'EBILAEAD'

!new Account('account5_1')
!account5_1.iban := 'AE070331234567890123456'
!account5_1.balance := 3000

!new Person('person5_1')
!person5_1.age := 34
!person5_1.firstName := 'Ahmed'
!person5_1.lastName := 'Al-Farsi'

!new Person('person5_2')
!person5_2.age := 29
!person5_2.firstName := 'Fatima'
!person5_2.lastName := 'Al-Mansoori'

!insert (person5_1, account5_1) into Ownership
!insert (person5_1, account5_1) into Use
!insert (person5_2, account5_1) into Use
!insert (bank5, account5_1) into AccountOfBanks
```

# Category: Boundary Instances
Below are instances that test boundary values of constraints, including minimum and maximum multiplicities, use of empty collections, and extreme numeric values for the invariants.

### Instance 1: Minimum Age for Ownership
- **Bank**:
  - `country`: Australia
  - `name`: Commonwealth Bank
  - `bic`: CTBAAU2S

- **Account**:
  - **Account 1**:
    - `iban`: AU123456789012345678
    - `balance`: 100

- **Person**:
  - **Person 1**:
    - `firstName`: Jack
    - `lastName`: Smith
    - `age`: 19
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1

### Instance 2: Maximum Number of Accounts for a Person
- **Bank**:
  - `country`: Canada
  - `name`: Royal Bank of Canada
  - `bic`: ROYCCAT2

- **Accounts**:
  - **Account 1**:
    - `iban`: CA123456789012345678
    - `balance`: 200
  - **Account 2**:
    - `iban`: CA987654321098765432
    - `balance`: 500
  - **Account 3**:
    - `iban`: CA192837465091827364
    - `balance`: 750

- **Person**:
  - **Person 1**:
    - `firstName`: Emily
    - `lastName`: Johnson
    - `age`: 35
    - **Ownership**: Owns Account 1, Account 2, Account 3
    - **Use**: Uses Account 1, Account 2, Account 3

### Instance 3: Minimum Balance
- **Bank**:
  - `country`: United Kingdom
  - `name`: HSBC
  - `bic`: HBUKGB4B

- **Account**:
  - **Account 1**:
    - `iban`: GB29NWBK60161331926819
    - `balance`: 1

- **Person**:
  - **Person 1**:
    - `firstName`: Oliver
    - `lastName`: Brown
    - `age`: 40
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1

### Instance 4: Extreme Balance Values
- **Bank**:
  - `country`: Switzerland
  - `name`: UBS
  - `bic`: UBSWCHZH80A

- **Account**:
  - **Account 1**:
    - `iban`: CH9300762011623852957
    - `balance`: 2147483647  // Maximum positive integer value

- **Person**:
  - **Person 1**:
    - `firstName`: Lucas
    - `lastName`: Meyer
    - `age`: 60
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1

### Instance 5: No Users (Unused Account)
- **Bank**:
  - `country`: India
  - `name`: State Bank of India
  - `bic`: SBININBB

- **Account**:
  - **Account 1**:
    - `iban`: IN30134829201000234567
    - `balance`: 500

- **Person**:
  - **Person 1**:
    - `firstName`: Priya
    - `lastName`: Reddy
    - `age`: 28
    - **Ownership**: Owns Account 1
    - **Use**: (No additional users)

### Instance 6: Maximum Users on an Account
- **Bank**:
  - `country`: Nigeria
  - `name`: Zenith Bank
  - `bic`: ZEIBNGLA

- **Account**:
  - **Account 1**:
    - `iban`: NG00123456789012345678
    - `balance`: 1200

- **Persons**:
  - **Person 1**:
    - `firstName`: Amina
    - `lastName`: Bello
    - `age`: 38
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Chinedu
    - `lastName`: Okeke
    - `age`: 30
    - **Use**: Uses Account 1
  - **Person 3**:
    - `firstName`: Bisi
    - `lastName`: Adeyemi
    - `age`: 34
    - **Use**: Uses Account 1

These instances demonstrate the boundaries of the model, exploring minimum and maximum constraints, optional associations, and extreme values for numeric constraints, without violating the model's invariants.
```
Below are the instances created based on the provided constraints testing the boundary values, formatted according to the syntax example provided:

### Instance 1: Minimum Age for Ownership

!new Bank('bank1')
!bank1.country := 'Australia'
!bank1.name := 'Commonwealth Bank'
!bank1.bic := 'CTBAAU2S'

!new Account('account1_1')
!account1_1.iban := 'AU123456789012345678'
!account1_1.balance := 100

!new Person('person1_1')
!person1_1.age := 19
!person1_1.firstName := 'Jack'
!person1_1.lastName := 'Smith'

!insert (person1_1, account1_1) into Ownership
!insert (person1_1, account1_1) into Use
!insert (bank1, account1_1) into AccountOfBanks

### Instance 2: Maximum Number of Accounts for a Person

!new Bank('bank2')
!bank2.country := 'Canada'
!bank2.name := 'Royal Bank of Canada'
!bank2.bic := 'ROYCCAT2'

!new Account('account2_1')
!account2_1.iban := 'CA123456789012345678'
!account2_1.balance := 200

!new Account('account2_2')
!account2_2.iban := 'CA987654321098765432'
!account2_2.balance := 500

!new Account('account2_3')
!account2_3.iban := 'CA192837465091827364'
!account2_3.balance := 750

!new Person('person2_1')
!person2_1.age := 35
!person2_1.firstName := 'Emily'
!person2_1.lastName := 'Johnson'

!insert (person2_1, account2_1) into Ownership
!insert (person2_1, account2_1) into Use
!insert (person2_1, account2_2) into Ownership
!insert (person2_1, account2_2) into Use
!insert (person2_1, account2_3) into Ownership
!insert (person2_1, account2_3) into Use
!insert (bank2, account2_1) into AccountOfBanks
!insert (bank2, account2_2) into AccountOfBanks
!insert (bank2, account2_3) into AccountOfBanks

### Instance 3: Minimum Balance

!new Bank('bank3')
!bank3.country := 'United Kingdom'
!bank3.name := 'HSBC'
!bank3.bic := 'HBUKGB4B'

!new Account('account3_1')
!account3_1.iban := 'GB29NWBK60161331926819'
!account3_1.balance := 1

!new Person('person3_1')
!person3_1.age := 40
!person3_1.firstName := 'Oliver'
!person3_1.lastName := 'Brown'

!insert (person3_1, account3_1) into Ownership
!insert (person3_1, account3_1) into Use
!insert (bank3, account3_1) into AccountOfBanks

### Instance 4: Extreme Balance Values

!new Bank('bank4')
!bank4.country := 'Switzerland'
!bank4.name := 'UBS'
!bank4.bic := 'UBSWCHZH80A'

!new Account('account4_1')
!account4_1.iban := 'CH9300762011623852957'
!account4_1.balance := 2147483647

!new Person('person4_1')
!person4_1.age := 60
!person4_1.firstName := 'Lucas'
!person4_1.lastName := 'Meyer'

!insert (person4_1, account4_1) into Ownership
!insert (person4_1, account4_1) into Use
!insert (bank4, account4_1) into AccountOfBanks

### Instance 5: No Users (Unused Account)

!new Bank('bank5')
!bank5.country := 'India'
!bank5.name := 'State Bank of India'
!bank5.bic := 'SBININBB'

!new Account('account5_1')
!account5_1.iban := 'IN30134829201000234567'
!account5_1.balance := 500

!new Person('person5_1')
!person5_1.age := 28
!person5_1.firstName := 'Priya'
!person5_1.lastName := 'Reddy'

!insert (person5_1, account5_1) into Ownership
!insert (bank5, account5_1) into AccountOfBanks

### Instance 6: Maximum Users on an Account

!new Bank('bank6')
!bank6.country := 'Nigeria'
!bank6.name := 'Zenith Bank'
!bank6.bic := 'ZEIBNGLA'

!new Account('account6_1')
!account6_1.iban := 'NG00123456789012345678'
!account6_1.balance := 1200

!new Person('person6_1')
!person6_1.age := 38
!person6_1.firstName := 'Amina'
!person6_1.lastName := 'Bello'

!new Person('person6_2')
!person6_2.age := 30
!person6_2.firstName := 'Chinedu'
!person6_2.lastName := 'Okeke'

!new Person('person6_3')
!person6_3.age := 34
!person6_3.firstName := 'Bisi'
!person6_3.lastName := 'Adeyemi'

!insert (person6_1, account6_1) into Ownership
!insert (person6_1, account6_1) into Use
!insert (person6_2, account6_1) into Use
!insert (person6_3, account6_1) into Use
!insert (bank6, account6_1) into AccountOfBanks
```

# Category: Complex Instances
Here are complex instances involving multiple interrelated entities, testing interactions between constraints, relationships, and multiplicities within the banking model:

### Instance 1: Transnational Bank Network
This instance involves a network of accounts and users spread across different countries, demonstrating complex ownership and usage relationships.

- **Bank**:
  - `country`: France
  - `name`: BNP Paribas
  - `bic`: BNPAFRPP

- **Accounts**:
  - **Account 1**:
    - `iban`: FR7630006000011234567890189
    - `balance`: 5000
  - **Account 2**:
    - `iban`: FR7630006000019876543210123
    - `balance`: 3000

- **Persons**:
  - **Person 1**:
    - `firstName`: Jean
    - `lastName`: Dupont
    - `age`: 50
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1, Account 2
  - **Person 2**:
    - `firstName`: Marie
    - `lastName`: Dubois
    - `age`: 45
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 1, Account 2
  - **Person 3**:
    - `firstName`: Ahmed
    - `lastName`: Khan
    - `age`: 33
    - **Use**: Uses Account 2

### Instance 2: Family Bank Accounts
This instance represents a family with shared and individual accounts across different banks, testing complex usage patterns.

- **Banks**:
  - **Bank 1**:
    - `country`: United States
    - `name`: Wells Fargo
    - `bic`: WFBIUS6S
  - **Bank 2**:
    - `country`: Canada
    - `name`: Scotiabank
    - `bic`: NOSCCATT

- **Accounts**:
  - **Account 1** (Wells Fargo):
    - `iban`: US12345678912345678910
    - `balance`: 10000
  - **Account 2** (Scotiabank):
    - `iban`: CA98765432109876543210
    - `balance`: 2000

- **Persons**:
  - **Person 1**:
    - `firstName`: John
    - `lastName`: Williams
    - `age`: 55
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1, Account 2
  - **Person 2**:
    - `firstName`: Lucy
    - `lastName`: Williams
    - `age`: 50
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 1, Account 2
  - **Person 3**:
    - `firstName`: Emily
    - `lastName`: Williams
    - `age`: 22
    - **Use**: Uses Account 2

### Instance 3: Corporate and Personal Accounts
A complex instance where a corporate bank account is used by multiple employees, illustrating overlapping personal and professional usage.

- **Bank**:
  - `country`: Germany
  - `name`: Deutsche Bank
  - `bic`: DEUTDEFF

- **Accounts**:
  - **Account 1**:
    - `iban`: DE89370400440532013000
    - `balance`: 150000
  - **Account 2**:
    - `iban`: DE89370400440532014000
    - `balance`: 5000

- **Persons**:
  - **Person 1**:
    - `firstName`: Martin
    - `lastName`: Schneider
    - `age`: 40
    - **Ownership**: Owns Account 1 (Corporate)
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Julia
    - `lastName`: Bauer
    - `age`: 32
    - **Use**: Uses Account 1, Account 2
  - **Person 3**:
    - `firstName`: Sven
    - `lastName`: Koch
    - `age`: 38
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 1, Account 2

### Instance 4: Multigenerational Wealth Management
A scenario involving a multigenerational family with accounts managed across different banks, testing intergenerational account usage.

- **Banks**:
  - **Bank 1**:
    - `country`: Japan
    - `name`: Mizuho Bank
    - `bic`: MHCBJPJT
  - **Bank 2**:
    - `country`: Singapore
    - `name`: DBS Bank
    - `bic`: DBSSSGSG

- **Accounts**:
  - **Account 1** (Mizuho Bank):
    - `iban`: JP12345678901234567890
    - `balance`: 100000
  - **Account 2** (DBS Bank):
    - `iban`: SG98765432109876543210
    - `balance`: 30000

- **Persons**:
  - **Person 1**:
    - `firstName`: Kenji
    - `lastName`: Sakamoto
    - `age`: 70
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Haruto
    - `lastName`: Sakamoto
    - `age`: 45
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 1, Account 2
  - **Person 3**:
    - `firstName`: Yuki
    - `lastName`: Sakamoto
    - `age`: 18
    - **Use**: Uses Account 2

These instances reflect complex interactions within the model, demonstrating various scenarios where multiple accounts, banks, and users are interconnected, testing the limits of the model's constraints and relationships.
```
Below are the instances created for complex scenarios involving multiple interrelated entities, formatted according to the syntax example provided:

### Instance 1: Transnational Bank Network

!new Bank('bank1')
!bank1.country := 'France'
!bank1.name := 'BNP Paribas'
!bank1.bic := 'BNPAFRPP'

!new Account('account1_1')
!account1_1.iban := 'FR7630006000011234567890189'
!account1_1.balance := 5000

!new Account('account1_2')
!account1_2.iban := 'FR7630006000019876543210123'
!account1_2.balance := 3000

!new Person('person1_1')
!person1_1.age := 50
!person1_1.firstName := 'Jean'
!person1_1.lastName := 'Dupont'

!new Person('person1_2')
!person1_2.age := 45
!person1_2.firstName := 'Marie'
!person1_2.lastName := 'Dubois'

!new Person('person1_3')
!person1_3.age := 33
!person1_3.firstName := 'Ahmed'
!person1_3.lastName := 'Khan'

!insert (person1_1, account1_1) into Ownership
!insert (person1_1, account1_1) into Use
!insert (person1_1, account1_2) into Use
!insert (person1_2, account1_2) into Ownership
!insert (person1_2, account1_1) into Use
!insert (person1_2, account1_2) into Use
!insert (person1_3, account1_2) into Use
!insert (bank1, account1_1) into AccountOfBanks
!insert (bank1, account1_2) into AccountOfBanks

### Instance 2: Family Bank Accounts

!new Bank('bank2_1')
!bank2_1.country := 'United States'
!bank2_1.name := 'Wells Fargo'
!bank2_1.bic := 'WFBIUS6S'

!new Bank('bank2_2')
!bank2_2.country := 'Canada'
!bank2_2.name := 'Scotiabank'
!bank2_2.bic := 'NOSCCATT'

!new Account('account2_1')
!account2_1.iban := 'US12345678912345678910'
!account2_1.balance := 10000

!new Account('account2_2')
!account2_2.iban := 'CA98765432109876543210'
!account2_2.balance := 2000

!new Person('person2_1')
!person2_1.age := 55
!person2_1.firstName := 'John'
!person2_1.lastName := 'Williams'

!new Person('person2_2')
!person2_2.age := 50
!person2_2.firstName := 'Lucy'
!person2_2.lastName := 'Williams'

!new Person('person2_3')
!person2_3.age := 22
!person2_3.firstName := 'Emily'
!person2_3.lastName := 'Williams'

!insert (person2_1, account2_1) into Ownership
!insert (person2_1, account2_1) into Use
!insert (person2_1, account2_2) into Use
!insert (person2_2, account2_2) into Ownership
!insert (person2_2, account2_1) into Use
!insert (person2_2, account2_2) into Use
!insert (person2_3, account2_2) into Use
!insert (bank2_1, account2_1) into AccountOfBanks
!insert (bank2_2, account2_2) into AccountOfBanks

### Instance 3: Corporate and Personal Accounts

!new Bank('bank3')
!bank3.country := 'Germany'
!bank3.name := 'Deutsche Bank'
!bank3.bic := 'DEUTDEFF'

!new Account('account3_1')
!account3_1.iban := 'DE89370400440532013000'
!account3_1.balance := 150000

!new Account('account3_2')
!account3_2.iban := 'DE89370400440532014000'
!account3_2.balance := 5000

!new Person('person3_1')
!person3_1.age := 40
!person3_1.firstName := 'Martin'
!person3_1.lastName := 'Schneider'

!new Person('person3_2')
!person3_2.age := 32
!person3_2.firstName := 'Julia'
!person3_2.lastName := 'Bauer'

!new Person('person3_3')
!person3_3.age := 38
!person3_3.firstName := 'Sven'
!person3_3.lastName := 'Koch'

!insert (person3_1, account3_1) into Ownership
!insert (person3_1, account3_1) into Use
!insert (person3_2, account3_1) into Use
!insert (person3_2, account3_2) into Use
!insert (person3_3, account3_2) into Ownership
!insert (person3_3, account3_1) into Use
!insert (person3_3, account3_2) into Use
!insert (bank3, account3_1) into AccountOfBanks
!insert (bank3, account3_2) into AccountOfBanks

### Instance 4: Multigenerational Wealth Management

!new Bank('bank4_1')
!bank4_1.country := 'Japan'
!bank4_1.name := 'Mizuho Bank'
!bank4_1.bic := 'MHCBJPJT'

!new Bank('bank4_2')
!bank4_2.country := 'Singapore'
!bank4_2.name := 'DBS Bank'
!bank4_2.bic := 'DBSSSGSG'

!new Account('account4_1')
!account4_1.iban := 'JP12345678901234567890'
!account4_1.balance := 100000

!new Account('account4_2')
!account4_2.iban := 'SG98765432109876543210'
!account4_2.balance := 30000

!new Person('person4_1')
!person4_1.age := 70
!person4_1.firstName := 'Kenji'
!person4_1.lastName := 'Sakamoto'

!new Person('person4_2')
!person4_2.age := 45
!person4_2.firstName := 'Haruto'
!person4_2.lastName := 'Sakamoto'

!new Person('person4_3')
!person4_3.age := 18
!person4_3.firstName := 'Yuki'
!person4_3.lastName := 'Sakamoto'

!insert (person4_1, account4_1) into Ownership
!insert (person4_1, account4_1) into Use
!insert (person4_2, account4_2) into Ownership
!insert (person4_2, account4_1) into Use
!insert (person4_2, account4_2) into Use
!insert (person4_3, account4_2) into Use
!insert (bank4_1, account4_1) into AccountOfBanks
!insert (bank4_2, account4_2) into AccountOfBanks
```

# Category: Unrealistc but valid
Here are instances that are syntactically and constraints-wise valid but represent unrealistic scenarios in real life, pushing the boundaries of the conceptual model:

### Instance 1: One Person Owns All Accounts Globally

- **Banks**:
  - **Bank 1**:
    - `country`: Liechtenstein
    - `name`: LGT Bank
    - `bic`: LGTBLI2X
  - **Bank 2**:
    - `country`: Monaco
    - `name`: CMB Monaco
    - `bic`: CMBMMCMX

- **Accounts**:
  - **Account 1** (LGT Bank):
    - `iban`: LI21088100002324013AA
    - `balance`: 1000000
  - **Account 2** (CMB Monaco):
    - `iban`: MC5811222000010123456789030
    - `balance`: 5000000
  - **Account 3** (LGT Bank):
    - `iban`: LI21088100002324014BB
    - `balance`: 750000

- **Person**:
  - **Person 1**:
    - `firstName`: Maximus
    - `lastName`: GlobalOwner
    - `age`: 55
    - **Ownership**: Owns Account 1, Account 2, Account 3
    - **Use**: Uses Account 1, Account 2, Account 3

### Instance 2: Bank with Only One Customer and One Massive Account

- **Bank**:
  - `country`: Vatican City
  - `name`: Vatican Bank
  - `bic`: IOPVVAVX

- **Account**:
  - **Account 1**:
    - `iban`: VA590011230000123456789
    - `balance`: 999999999  // An extremely high balance

- **Person**:
  - **Person 1**:
    - `firstName`: Unique
    - `lastName`: Singleton
    - `age`: 35
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1

### Instance 3: Multiple Banks with Shared Accounts

- **Banks**:
  - **Bank 1**:
    - `country`: Nauru
    - `name`: Nauru Bank
    - `bic`: NRUNNRUX
  - **Bank 2**:
    - `country`: Tuvalu
    - `name`: Tuvalu Bank
    - `bic`: TUVLTUVX

- **Accounts**:
  - **Account 1**:
    - `iban`: NR1212345678901234567890123
    - `balance`: 100000
  - **Account 2**:
    - `iban`: TV3412345678909876543210123
    - `balance`: 200000

- **Persons**:
  - **Person 1**:
    - `firstName`: John
    - `lastName`: Doe
    - `age`: 40
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1, Account 2
  - **Person 2**:
    - `firstName`: Jane
    - `lastName`: Doe
    - `age`: 38
    - **Ownership**: Owns Account 2
    - **Use**: Uses Account 1, Account 2

Despite the banking system's constraints, these instances highlight extreme and improbable scenarios that are technically valid within the model. These instances test the flexibility of the model while maintaining adherence to the defined constraints and relationships.
```
Below are the instances created for unrealistic scenarios, formatted according to the syntax example provided:

### Instance 1: One Person Owns All Accounts Globally

!new Bank('bank1_1')
!bank1_1.country := 'Liechtenstein'
!bank1_1.name := 'LGT Bank'
!bank1_1.bic := 'LGTBLI2X'

!new Bank('bank1_2')
!bank1_2.country := 'Monaco'
!bank1_2.name := 'CMB Monaco'
!bank1_2.bic := 'CMBMMCMX'

!new Account('account1_1')
!account1_1.iban := 'LI21088100002324013AA'
!account1_1.balance := 1000000

!new Account('account1_2')
!account1_2.iban := 'MC5811222000010123456789030'
!account1_2.balance := 5000000

!new Account('account1_3')
!account1_3.iban := 'LI21088100002324014BB'
!account1_3.balance := 750000

!new Person('person1_1')
!person1_1.age := 55
!person1_1.firstName := 'Maximus'
!person1_1.lastName := 'GlobalOwner'

!insert (person1_1, account1_1) into Ownership
!insert (person1_1, account1_1) into Use
!insert (person1_1, account1_2) into Ownership
!insert (person1_1, account1_2) into Use
!insert (person1_1, account1_3) into Ownership
!insert (person1_1, account1_3) into Use
!insert (bank1_1, account1_1) into AccountOfBanks
!insert (bank1_2, account1_2) into AccountOfBanks
!insert (bank1_1, account1_3) into AccountOfBanks

### Instance 2: Bank with Only One Customer and One Massive Account

!new Bank('bank2')
!bank2.country := 'Vatican City'
!bank2.name := 'Vatican Bank'
!bank2.bic := 'IOPVVAVX'

!new Account('account2_1')
!account2_1.iban := 'VA590011230000123456789'
!account2_1.balance := 999999999

!new Person('person2_1')
!person2_1.age := 35
!person2_1.firstName := 'Unique'
!person2_1.lastName := 'Singleton'

!insert (person2_1, account2_1) into Ownership
!insert (person2_1, account2_1) into Use
!insert (bank2, account2_1) into AccountOfBanks

### Instance 3: Multiple Banks with Shared Accounts

!new Bank('bank3_1')
!bank3_1.country := 'Nauru'
!bank3_1.name := 'Nauru Bank'
!bank3_1.bic := 'NRUNNRUX'

!new Bank('bank3_2')
!bank3_2.country := 'Tuvalu'
!bank3_2.name := 'Tuvalu Bank'
!bank3_2.bic := 'TUVLTUVX'

!new Account('account3_1')
!account3_1.iban := 'NR1212345678901234567890123'
!account3_1.balance := 100000

!new Account('account3_2')
!account3_2.iban := 'TV3412345678909876543210123'
!account3_2.balance := 200000

!new Person('person3_1')
!person3_1.age := 40
!person3_1.firstName := 'John'
!person3_1.lastName := 'Doe'

!new Person('person3_2')
!person3_2.age := 38
!person3_2.firstName := 'Jane'
!person3_2.lastName := 'Doe'

!insert (person3_1, account3_1) into Ownership
!insert (person3_1, account3_1) into Use
!insert (person3_1, account3_2) into Use
!insert (person3_2, account3_2) into Ownership
!insert (person3_2, account3_1) into Use
!insert (person3_2, account3_2) into Use
!insert (bank3_1, account3_1) into AccountOfBanks
!insert (bank3_2, account3_2) into AccountOfBanks
```

# Category: Realistic but invalid
Below are instances that represent realistic scenarios but violate the constraints of the model, highlighting potentially overly restrictive or unrealistic constraints:

### Instance 1: Teenage Savings Account
- **Bank**:
  - `country`: United States
  - `name`: Chase Bank
  - `bic`: CHASUS33

- **Account**:
  - **Account 1**:
    - `iban`: US12345678901234567890
    - `balance`: 500

- **Person**:
  - **Person 1**:
    - `firstName`: Alex
    - `lastName`: Johnson
    - `age`: 17  // Violates AdultOwners invariant
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1

### Instance 2: Community Account with Negative Balance
- **Bank**:
  - `country`: United Kingdom
  - `name`: Barclays
  - `bic`: BARCGB22

- **Account**:
  - **Account 1**:
    - `iban`: GB29NWBK60161331926819
    - `balance`: -100  // Violates PositiveBalance invariant

- **Persons**:
  - **Person 1**:
    - `firstName`: Sarah
    - `lastName`: Thompson
    - `age`: 30
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Tom
    - `lastName`: Baker
    - `age`: 29
    - **Use**: Uses Account 1

### Instance 3: Shared Ownership Scenario
- **Bank**:
  - `country`: Australia
  - `name`: ANZ
  - `bic`: ANZBAU3M

- **Accounts**:
  - **Account 1**:
    - `iban`: AU123456789012345678
    - `balance`: 1500

- **Persons**:
  - **Person 1**:
    - `firstName`: Emma
    - `lastName`: Smith
    - `age`: 45
    - **Ownership**: Owns Account 1
    - **Use**: Uses Account 1
  - **Person 2**:
    - `firstName`: Liam
    - `lastName`: Smith
    - `age`: 47
    - **Ownership**: (Also owns Account 1)  // Violates the ownership multiplicity constraint
    - **Use**: Uses Account 1

### Instance 4: Account Used Before Age of Majority
- **Bank**:
  - `country`: Canada
  - `name`: TD Bank
  - `bic`: TDOMCATTTOR

- **Account**:
  - **Account 1**:
    - `iban`: CA123456789012345678
    - `balance`: 800

- **Persons**:
  - **Person 1**:
    - `firstName`: Lucas
    - `lastName`: Brown
    - `age`: 16  // Realistically could use the account, but not own it
    - **Use**: Uses Account 1

These instances are rooted in plausible real-world scenarios but reveal constraints within the model that may not align with practical banking operations, such as shared account ownership, teenage account holders, and accounts with negative balances.
```
Below are the instances created for scenarios that violate model constraints, formatted according to the syntax example provided. These instances illustrate realistic situations that challenge the model's constraints:

### Instance 1: Teenage Savings Account

!new Bank('bank1')
!bank1.country := 'United States'
!bank1.name := 'Chase Bank'
!bank1.bic := 'CHASUS33'

!new Account('account1_1')
!account1_1.iban := 'US12345678901234567890'
!account1_1.balance := 500

!new Person('person1_1')
!person1_1.age := 17
!person1_1.firstName := 'Alex'
!person1_1.lastName := 'Johnson'

!insert (person1_1, account1_1) into Ownership // Violates AdultOwners invariant
!insert (person1_1, account1_1) into Use
!insert (bank1, account1_1) into AccountOfBanks

### Instance 2: Community Account with Negative Balance

!new Bank('bank2')
!bank2.country := 'United Kingdom'
!bank2.name := 'Barclays'
!bank2.bic := 'BARCGB22'

!new Account('account2_1')
!account2_1.iban := 'GB29NWBK60161331926819'
!account2_1.balance := -100 // Violates PositiveBalance invariant

!new Person('person2_1')
!person2_1.age := 30
!person2_1.firstName := 'Sarah'
!person2_1.lastName := 'Thompson'

!new Person('person2_2')
!person2_2.age := 29
!person2_2.firstName := 'Tom'
!person2_2.lastName := 'Baker'

!insert (person2_1, account2_1) into Ownership
!insert (person2_1, account2_1) into Use
!insert (person2_2, account2_1) into Use
!insert (bank2, account2_1) into AccountOfBanks

### Instance 3: Shared Ownership Scenario

!new Bank('bank3')
!bank3.country := 'Australia'
!bank3.name := 'ANZ'
!bank3.bic := 'ANZBAU3M'

!new Account('account3_1')
!account3_1.iban := 'AU123456789012345678'
!account3_1.balance := 1500

!new Person('person3_1')
!person3_1.age := 45
!person3_1.firstName := 'Emma'
!person3_1.lastName := 'Smith'

!new Person('person3_2')
!person3_2.age := 47
!person3_2.firstName := 'Liam'
!person3_2.lastName := 'Smith'

!insert (person3_1, account3_1) into Ownership
!insert (person3_1, account3_1) into Use
!insert (person3_2, account3_1) into Ownership // Violates ownership multiplicity constraint
!insert (person3_2, account3_1) into Use
!insert (bank3, account3_1) into AccountOfBanks

### Instance 4: Account Used Before Age of Majority

!new Bank('bank4')
!bank4.country := 'Canada'
!bank4.name := 'TD Bank'
!bank4.bic := 'TDOMCATTTOR'

!new Account('account4_1')
!account4_1.iban := 'CA123456789012345678'
!account4_1.balance := 800

!new Person('person4_1')
!person4_1.age := 16
!person4_1.firstName := 'Lucas'
!person4_1.lastName := 'Brown'

!insert (person4_1, account4_1) into Use // Realistically can use but not own
!insert (bank4, account4_1) into AccountOfBanks
```