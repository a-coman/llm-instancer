# Description

The conceptual model "BankAccount" is designed to represent a basic banking system involving banks, accounts, and people. It defines three primary classes: Bank, Account, and Person, each with specific attributes, and it establishes associations between them to model ownership and usage of accounts.

## Components

### Classes and Attributes

- **Bank**
  - **country**: String - Represents the country where the bank operates.
  - **name**: String - The name of the bank.
  - **bic**: String - The Bank Identifier Code, used for international transactions.

- **Account**
  - **iban**: String - The International Bank Account Number, uniquely identifying the account.
  - **balance**: Integer - The current amount of money in the account.

- **Person**
  - **firstName**: String - The first name of the person.
  - **lastName**: String - The last name of the person.
  - **age**: Integer - The age of the person.

# Relationships

### Associations

1. **Ownership Association**
   - **Person** (role: owner) - An individual who owns accounts.
   - **Account** (role: accounts) - Accounts owned by the person.
   - **Multiplicity**: 
     - A Person can own multiple (0 to many) Accounts.
     - Each Account must have exactly one owner (1).

2. **Use Association**
   - **Person** (role: user) - An individual who can use accounts.
   - **Account** (role: usedAccounts) - Accounts that can be used by the person.
   - **Multiplicity**: 
     - A Person can use multiple (0 to many) Accounts.
     - An Account can be used by multiple (0 to many) People.

3. **AccountOfBanks Composition**
   - **Bank** (role: bank) - The bank that holds the accounts.
   - **Account** (role: accounts) - Accounts that belong to the bank.
   - **Multiplicity**: 
     - A Bank can have multiple (0 to many) Accounts.
     - Each Account must belong to exactly one Bank (1).

# Invariants

- **AdultOwners**: Ensures that the owner of an account is over the age of 18. 
  - Constraint: `self.owner.age > 18`

- **positiveBalance**: Ensures that every account maintains a positive balance.
  - Constraint: `self.balance > 0`

# Category: Baseline Instances
Below are diverse baseline instances that adhere to the given conceptual model for the "BankAccount" system. Each instance is unique, representing different regional and structural configurations while maintaining compliance with the model's constraints and relationships.

### Instance 1: European Scenario

**Bank:**
- **country**: Germany
- **name**: Deutsche Bank
- **bic**: DEUTDEFF

**Person (Owner and User):**
- **firstName**: Hans
- **lastName**: Müller
- **age**: 35

**Account:**
- **iban**: DE89370400440532013000
- **balance**: 1500

**Relationships:**
- Hans Müller owns and uses the account with IBAN DE89370400440532013000, which is held at Deutsche Bank.

### Instance 2: North American Scenario

**Bank:**
- **country**: United States
- **name**: Bank of America
- **bic**: BOFAUS3N

**Person (Owner and User):**
- **firstName**: Emily
- **lastName**: Johnson
- **age**: 27

**Account:**
- **iban**: US12345678901234567890
- **balance**: 2500

**Relationships:**
- Emily Johnson owns and uses the account with IBAN US12345678901234567890, which is held at Bank of America.

### Instance 3: Asian Scenario

**Bank:**
- **country**: Japan
- **name**: Mitsubishi UFJ Financial Group
- **bic**: BOTKJPJT

**Person (Owner):**
- **firstName**: Akira
- **lastName**: Tanaka
- **age**: 42

**Person (User):**
- **firstName**: Yuki
- **lastName**: Sato
- **age**: 30

**Account:**
- **iban**: JP1234567890123456789012
- **balance**: 5000

**Relationships:**
- Akira Tanaka owns the account with IBAN JP1234567890123456789012.
- Both Akira Tanaka and Yuki Sato can use the account, which is held at Mitsubishi UFJ Financial Group.

### Instance 4: African Scenario

**Bank:**
- **country**: South Africa
- **name**: Standard Bank
- **bic**: SBZAZAJJ

**Person (Owner and User):**
- **firstName**: Thabo
- **lastName**: Ndlovu
- **age**: 29

**Account:**
- **iban**: ZA12345678901234567890001
- **balance**: 300

**Relationships:**
- Thabo Ndlovu owns and uses the account with IBAN ZA12345678901234567890001, which is held at Standard Bank.

### Instance 5: South American Scenario

**Bank:**
- **country**: Brazil
- **name**: Banco do Brasil
- **bic**: BRASBRRJOAO

**Person (Owner):**
- **firstName**: Maria
- **lastName**: Silva
- **age**: 33

**Person (User):**
- **firstName**: João
- **lastName**: Pereira
- **age**: 38

**Account:**
- **iban**: BR123456789012345678901234
- **balance**: 1200

**Relationships:**
- Maria Silva owns the account with IBAN BR123456789012345678901234.
- Both Maria Silva and João Pereira can use the account, which is held at Banco do Brasil.

### Additional Considerations
These instances demonstrate compliance with the model's invariants, such as ensuring adult ownership and positive account balances. Each scenario reflects a different regional banking context, providing a broad view of how the system can be applied globally.
```
### Instance 1: European Scenario

!new Bank('bank1')
!bank1.country := 'Germany'
!bank1.name := 'Deutsche Bank'
!bank1.bic := 'DEUTDEFF'

!new Person('person1')
!person1.age := 35
!person1.firstName := 'Hans'
!person1.lastName := 'Müller'

!new Account('account1')
!account1.iban := 'DE89370400440532013000'
!account1.balance := 1500

!insert (person1, account1) into Ownership
!insert (person1, account1) into Use
!insert (bank1, account1) into AccountOfBanks

### Instance 2: North American Scenario

!new Bank('bank2')
!bank2.country := 'United States'
!bank2.name := 'Bank of America'
!bank2.bic := 'BOFAUS3N'

!new Person('person2')
!person2.age := 27
!person2.firstName := 'Emily'
!person2.lastName := 'Johnson'

!new Account('account2')
!account2.iban := 'US12345678901234567890'
!account2.balance := 2500

!insert (person2, account2) into Ownership
!insert (person2, account2) into Use
!insert (bank2, account2) into AccountOfBanks

### Instance 3: Asian Scenario

!new Bank('bank3')
!bank3.country := 'Japan'
!bank3.name := 'Mitsubishi UFJ Financial Group'
!bank3.bic := 'BOTKJPJT'

!new Person('person3')
!person3.age := 42
!person3.firstName := 'Akira'
!person3.lastName := 'Tanaka'

!new Person('person4')
!person4.age := 30
!person4.firstName := 'Yuki'
!person4.lastName := 'Sato'

!new Account('account3')
!account3.iban := 'JP1234567890123456789012'
!account3.balance := 5000

!insert (person3, account3) into Ownership
!insert (person3, account3) into Use
!insert (person4, account3) into Use
!insert (bank3, account3) into AccountOfBanks

### Instance 4: African Scenario

!new Bank('bank4')
!bank4.country := 'South Africa'
!bank4.name := 'Standard Bank'
!bank4.bic := 'SBZAZAJJ'

!new Person('person5')
!person5.age := 29
!person5.firstName := 'Thabo'
!person5.lastName := 'Ndlovu'

!new Account('account4')
!account4.iban := 'ZA12345678901234567890001'
!account4.balance := 300

!insert (person5, account4) into Ownership
!insert (person5, account4) into Use
!insert (bank4, account4) into AccountOfBanks

### Instance 5: South American Scenario

!new Bank('bank5')
!bank5.country := 'Brazil'
!bank5.name := 'Banco do Brasil'
!bank5.bic := 'BRASBRRJOAO'

!new Person('person6')
!person6.age := 33
!person6.firstName := 'Maria'
!person6.lastName := 'Silva'

!new Person('person7')
!person7.age := 38
!person7.firstName := 'João'
!person7.lastName := 'Pereira'

!new Account('account5')
!account5.iban := 'BR123456789012345678901234'
!account5.balance := 1200

!insert (person6, account5) into Ownership
!insert (person6, account5) into Use
!insert (person7, account5) into Use
!insert (bank5, account5) into AccountOfBanks
```

# Category: Boundary Instances
Below are boundary instances designed to test the constraints and multiplicities defined in the "BankAccount" model. Each instance uniquely addresses different aspects of the constraints, such as minimum and maximum multiplicities, empty collections, and extreme values for numeric constraints.

### Instance 1: Minimum Multiplicity and Empty Collections

**Bank:**
- **country**: Canada
- **name**: Royal Bank of Canada
- **bic**: ROYCCAT2

**Person (Owner with no accounts):**
- **firstName**: Alicia
- **lastName**: Brown
- **age**: 25

**Person (User with no used accounts):**
- **firstName**: Michael
- **lastName**: Lee
- **age**: 32

**Relationships:**
- Alicia Brown currently owns no accounts.
- Michael Lee currently uses no accounts.
- No accounts are associated with Royal Bank of Canada.

### Instance 2: Maximum Multiplicity (Multiple Accounts and Users)

**Bank:**
- **country**: India
- **name**: State Bank of India
- **bic**: SBININBB

**Person (Owner):**
- **firstName**: Raj
- **lastName**: Kapoor
- **age**: 40

**Person (Users):**
- **firstName**: Priya
- **lastName**: Patel
- **age**: 28

- **firstName**: Amit
- **lastName**: Sharma
- **age**: 35

**Accounts:**
1. **Account 1:**
   - **iban**: IN12345678901234567890
   - **balance**: 10000

2. **Account 2:**
   - **iban**: IN09876543210987654321
   - **balance**: 5000

**Relationships:**
- Raj Kapoor owns both accounts with IBANs IN12345678901234567890 and IN09876543210987654321.
- Priya Patel and Amit Sharma can use both accounts.
- Both accounts are held at State Bank of India.

### Instance 3: Extreme Value for Balance

**Bank:**
- **country**: Switzerland
- **name**: UBS
- **bic**: UBSWCHZH80A

**Person (Owner and User):**
- **firstName**: Jakob
- **lastName**: Frei
- **age**: 50

**Account:**
- **iban**: CH9300762011623852957
- **balance**: 1 (Minimum positive balance)

**Relationships:**
- Jakob Frei owns and uses the account with IBAN CH9300762011623852957, which is held at UBS.

### Instance 4: Adult Age Boundary

**Bank:**
- **country**: Australia
- **name**: Commonwealth Bank
- **bic**: CTBAAU2S

**Person (Owner and User):**
- **firstName**: Emily
- **lastName**: White
- **age**: 19 (Minimum age for adult ownership)

**Account:**
- **iban**: AU123456789012345678901
- **balance**: 200

**Relationships:**
- Emily White owns and uses the account with IBAN AU123456789012345678901, which is held at Commonwealth Bank.

### Additional Considerations
These instances test various boundaries:
- Instance 1 tests empty collections by having persons without accounts and a bank without any accounts.
- Instance 2 demonstrates the maximum multiplicity by associating multiple accounts and users with a single owner.
- Instance 3 uses the minimum positive balance to test the positive balance invariant.
- Instance 4 examines the minimum age requirement for account ownership, ensuring the age constraint is respected at its boundary.
```
### Instance 1: Minimum Multiplicity and Empty Collections

!new Bank('bank6')
!bank6.country := 'Canada'
!bank6.name := 'Royal Bank of Canada'
!bank6.bic := 'ROYCCAT2'

!new Person('person8')
!person8.age := 25
!person8.firstName := 'Alicia'
!person8.lastName := 'Brown'

!new Person('person9')
!person9.age := 32
!person9.firstName := 'Michael'
!person9.lastName := 'Lee'

### Instance 2: Maximum Multiplicity (Multiple Accounts and Users)

!new Bank('bank7')
!bank7.country := 'India'
!bank7.name := 'State Bank of India'
!bank7.bic := 'SBININBB'

!new Person('person10')
!person10.age := 40
!person10.firstName := 'Raj'
!person10.lastName := 'Kapoor'

!new Person('person11')
!person11.age := 28
!person11.firstName := 'Priya'
!person11.lastName := 'Patel'

!new Person('person12')
!person12.age := 35
!person12.firstName := 'Amit'
!person12.lastName := 'Sharma'

!new Account('account6')
!account6.iban := 'IN12345678901234567890'
!account6.balance := 10000

!new Account('account7')
!account7.iban := 'IN09876543210987654321'
!account7.balance := 5000

!insert (person10, account6) into Ownership
!insert (person10, account7) into Ownership
!insert (person11, account6) into Use
!insert (person11, account7) into Use
!insert (person12, account6) into Use
!insert (person12, account7) into Use
!insert (bank7, account6) into AccountOfBanks
!insert (bank7, account7) into AccountOfBanks

### Instance 3: Extreme Value for Balance

!new Bank('bank8')
!bank8.country := 'Switzerland'
!bank8.name := 'UBS'
!bank8.bic := 'UBSWCHZH80A'

!new Person('person13')
!person13.age := 50
!person13.firstName := 'Jakob'
!person13.lastName := 'Frei'

!new Account('account8')
!account8.iban := 'CH9300762011623852957'
!account8.balance := 1

!insert (person13, account8) into Ownership
!insert (person13, account8) into Use
!insert (bank8, account8) into AccountOfBanks

### Instance 4: Adult Age Boundary

!new Bank('bank9')
!bank9.country := 'Australia'
!bank9.name := 'Commonwealth Bank'
!bank9.bic := 'CTBAAU2S'

!new Person('person14')
!person14.age := 19
!person14.firstName := 'Emily'
!person14.lastName := 'White'

!new Account('account9')
!account9.iban := 'AU123456789012345678901'
!account9.balance := 200

!insert (person14, account9) into Ownership
!insert (person14, account9) into Use
!insert (bank9, account9) into AccountOfBanks
```

# Category: Complex Instances
Here are complex instances involving multiple interrelated entities, designed to test the interactions within the "BankAccount" model, including constraints, relationships, and multiplicities.

### Instance 1: Interconnected Families with Shared Accounts

**Bank:**
- **country**: United Kingdom
- **name**: HSBC
- **bic**: HBUKGB4B

**Persons:**
1. **Person 1 (Owner and User):**
   - **firstName**: James
   - **lastName**: Smith
   - **age**: 45

2. **Person 2 (Owner and User):**
   - **firstName**: Sarah
   - **lastName**: Smith
   - **age**: 42

3. **Person 3 (User):**
   - **firstName**: Robert
   - **lastName**: Smith
   - **age**: 19

4. **Person 4 (User):**
   - **firstName**: Emma
   - **lastName**: Johnson
   - **age**: 67

**Accounts:**
1. **Joint Account (Owned by James and Sarah):**
   - **iban**: GB29HBUK12345698765432
   - **balance**: 3000

2. **Individual Account (Owned by James):**
   - **iban**: GB29HBUK12345612345678
   - **balance**: 1500

3. **Shared Account (Used by James, Sarah, and Emma):**
   - **iban**: GB29HBUK12345687654321
   - **balance**: 500

**Relationships:**
- James Smith and Sarah Smith jointly own the joint account with IBAN GB29HBUK12345698765432.
- James Smith owns the individual account with IBAN GB29HBUK12345612345678.
- James, Sarah, and Emma can use the shared account with IBAN GB29HBUK12345687654321.
- Robert Smith can use the joint account but does not own any accounts.
- All accounts are held at HSBC.

### Instance 2: Multinational Corporate Setup

**Banks:**
1. **Bank 1:**
   - **country**: Germany
   - **name**: Commerzbank
   - **bic**: COBADEFF

2. **Bank 2:**
   - **country**: France
   - **name**: BNP Paribas
   - **bic**: BNPAFRPP

**Persons:**
1. **Person 1 (Owner and User):**
   - **firstName**: Claudia
   - **lastName**: Müller
   - **age**: 39

2. **Person 2 (Owner and User):**
   - **firstName**: Pierre
   - **lastName**: Dupont
   - **age**: 55

3. **Person 3 (User):**
   - **firstName**: Sophie
   - **lastName**: Martin
   - **age**: 37

**Accounts:**
1. **Corporate Account 1 (Commerzbank, Owned by Claudia):**
   - **iban**: DE89370400440532013001
   - **balance**: 20000

2. **Corporate Account 2 (BNP Paribas, Owned by Pierre):**
   - **iban**: FR7630006000011234567890189
   - **balance**: 15000

3. **Project Account (Used by all):**
   - **iban**: FR7630006000019876543210765
   - **balance**: 7500

**Relationships:**
- Claudia Müller owns the corporate account at Commerzbank.
- Pierre Dupont owns the corporate account at BNP Paribas.
- All three persons (Claudia, Pierre, Sophie) can use the project account at BNP Paribas.
- Sophie Martin, while not owning any accounts, has access to the project account for operational purposes.

### Additional Considerations
These complex instances illustrate the following:
- Instance 1 involves a family with shared and individual accounts, testing multiple ownership and usage scenarios within a single bank.
- Instance 2 showcases a multinational setup with accounts across different banks, testing cross-border relationships and account usage among corporate entities.

Both instances respect all constraints, such as age requirements for owning accounts and maintaining positive balances, while demonstrating the flexibility of the model across diverse scenarios.
```
### Instance 1: Interconnected Families with Shared Accounts

!new Bank('bank10')
!bank10.country := 'United Kingdom'
!bank10.name := 'HSBC'
!bank10.bic := 'HBUKGB4B'

!new Person('person15')
!person15.age := 45
!person15.firstName := 'James'
!person15.lastName := 'Smith'

!new Person('person16')
!person16.age := 42
!person16.firstName := 'Sarah'
!person16.lastName := 'Smith'

!new Person('person17')
!person17.age := 19
!person17.firstName := 'Robert'
!person17.lastName := 'Smith'

!new Person('person18')
!person18.age := 67
!person18.firstName := 'Emma'
!person18.lastName := 'Johnson'

!new Account('account10')
!account10.iban := 'GB29HBUK12345698765432'
!account10.balance := 3000

!new Account('account11')
!account11.iban := 'GB29HBUK12345612345678'
!account11.balance := 1500

!new Account('account12')
!account12.iban := 'GB29HBUK12345687654321'
!account12.balance := 500

!insert (person15, account10) into Ownership
!insert (person16, account10) into Ownership
!insert (person15, account11) into Ownership
!insert (person15, account12) into Use
!insert (person16, account12) into Use
!insert (person18, account12) into Use
!insert (person17, account10) into Use
!insert (bank10, account10) into AccountOfBanks
!insert (bank10, account11) into AccountOfBanks
!insert (bank10, account12) into AccountOfBanks

### Instance 2: Multinational Corporate Setup

!new Bank('bank11')
!bank11.country := 'Germany'
!bank11.name := 'Commerzbank'
!bank11.bic := 'COBADEFF'

!new Bank('bank12')
!bank12.country := 'France'
!bank12.name := 'BNP Paribas'
!bank12.bic := 'BNPAFRPP'

!new Person('person19')
!person19.age := 39
!person19.firstName := 'Claudia'
!person19.lastName := 'Müller'

!new Person('person20')
!person20.age := 55
!person20.firstName := 'Pierre'
!person20.lastName := 'Dupont'

!new Person('person21')
!person21.age := 37
!person21.firstName := 'Sophie'
!person21.lastName := 'Martin'

!new Account('account13')
!account13.iban := 'DE89370400440532013001'
!account13.balance := 20000

!new Account('account14')
!account14.iban := 'FR7630006000011234567890189'
!account14.balance := 15000

!new Account('account15')
!account15.iban := 'FR7630006000019876543210765'
!account15.balance := 7500

!insert (person19, account13) into Ownership
!insert (person20, account14) into Ownership
!insert (person19, account15) into Use
!insert (person20, account15) into Use
!insert (person21, account15) into Use
!insert (bank11, account13) into AccountOfBanks
!insert (bank12, account14) into AccountOfBanks
!insert (bank12, account15) into AccountOfBanks
```

# Category: Unrealistc but valid
Here are some instances that are valid according to the constraints and multiplicities of the "BankAccount" model but are highly unlikely or unrealistic in real-world scenarios. These examples highlight unusual combinations of relationships and attributes while adhering to all specified rules.

### Instance 1: Single Bank with Global Presence and a Single Account

**Bank:**
- **country**: Antarctica
- **name**: Universal Bank of Antarctica
- **bic**: UNIBANTAA

**Person (Owner and User):**
- **firstName**: Aurora
- **lastName**: Borealis
- **age**: 28

**Account:**
- **iban**: AQ123456789012345678901
- **balance**: 1

**Relationships:**
- Aurora Borealis owns and uses the single account with IBAN AQ123456789012345678901.
- This is the only account held at the Universal Bank of Antarctica, a bank in a continent without a permanent population.

### Instance 2: The Infinite User Account

**Bank:**
- **country**: Nowhere Land
- **name**: Infinite Trust
- **bic**: INTRNOWHERE

**Person (Owner):**
- **firstName**: Maximus
- **lastName**: Overload
- **age**: 30

**Account:**
- **iban**: NW123456789012345678902
- **balance**: 100

**Persons (Users):**
1. **Person 1:**
   - **firstName**: John
   - **lastName**: Doe
   - **age**: 40

2. **Person 2:**
   - **firstName**: Jane
   - **lastName**: Doe
   - **age**: 35
   
3. **Person 3:**
   - **firstName**: Alex
   - **lastName**: Smith
   - **age**: 50

- *(Repeat adding more "Doe" and "Smith" variations with slight changes to ages to simulate large numbers of users.)*

**Relationships:**
- Maximus Overload owns the account with IBAN NW123456789012345678902.
- The account is used by a theoretically infinite number of users (represented here by a large but finite number of "Doe" and "Smith" variations).
- The account balance is extremely low given the number of users, which is unrealistic.

### Instance 3: A Bank with No Accounts

**Bank:**
- **country**: Atlantis
- **name**: Mythical Savings Bank
- **bic**: MYTHATLANT

**Relationships:**
- Despite being a bank, Mythical Savings Bank has no accounts associated with it, which defies the purpose of a bank but is technically permissible in the model.

### Instance 4: Teenager with Multiple Accounts

**Bank:**
- **country**: Wonderland
- **name**: Cheshire Bank
- **bic**: CHESHIREWB

**Person (Owner):**
- **firstName**: Alice
- **lastName**: Liddel
- **age**: 19

**Accounts:**
1. **Account 1:**
   - **iban**: WL123456789012345678901
   - **balance**: 500

2. **Account 2:**
   - **iban**: WL123456789012345678902
   - **balance**: 750

3. **Account 3:**
   - **iban**: WL123456789012345678903
   - **balance**: 1000

**Relationships:**
- Alice Liddel, a 19-year-old, owns three separate accounts at Cheshire Bank, each with a positive balance.
- This scenario is valid but unlikely for a teenager to independently manage multiple accounts with significant balances.

### Additional Considerations
These instances demonstrate the flexibility of the model while highlighting scenarios that are syntactically valid but highly improbable:
- A bank with a global reach but a single account.
- An account with an impractically high number of users.
- A bank existing without any accounts.
- A young adult with unusually numerous accounts. 

These examples stretch the logical applications of the model while respecting all constraints and multiplicities.
```
### Instance 1: Single Bank with Global Presence and a Single Account

!new Bank('bank13')
!bank13.country := 'Antarctica'
!bank13.name := 'Universal Bank of Antarctica'
!bank13.bic := 'UNIBANTAA'

!new Person('person22')
!person22.age := 28
!person22.firstName := 'Aurora'
!person22.lastName := 'Borealis'

!new Account('account16')
!account16.iban := 'AQ123456789012345678901'
!account16.balance := 1

!insert (person22, account16) into Ownership
!insert (person22, account16) into Use
!insert (bank13, account16) into AccountOfBanks

### Instance 2: The Infinite User Account

!new Bank('bank14')
!bank14.country := 'Nowhere Land'
!bank14.name := 'Infinite Trust'
!bank14.bic := 'INTRNOWHERE'

!new Person('person23')
!person23.age := 30
!person23.firstName := 'Maximus'
!person23.lastName := 'Overload'

!new Account('account17')
!account17.iban := 'NW123456789012345678902'
!account17.balance := 100

!new Person('person24')
!person24.age := 40
!person24.firstName := 'John'
!person24.lastName := 'Doe'

!new Person('person25')
!person25.age := 35
!person25.firstName := 'Jane'
!person25.lastName := 'Doe'

!new Person('person26')
!person26.age := 50
!person26.firstName := 'Alex'
!person26.lastName := 'Smith'

!insert (person23, account17) into Ownership
!insert (person24, account17) into Use
!insert (person25, account17) into Use
!insert (person26, account17) into Use
!insert (bank14, account17) into AccountOfBanks

### Instance 3: A Bank with No Accounts

!new Bank('bank15')
!bank15.country := 'Atlantis'
!bank15.name := 'Mythical Savings Bank'
!bank15.bic := 'MYTHATLANT'

### Instance 4: Teenager with Multiple Accounts

!new Bank('bank16')
!bank16.country := 'Wonderland'
!bank16.name := 'Cheshire Bank'
!bank16.bic := 'CHESHIREWB'

!new Person('person27')
!person27.age := 19
!person27.firstName := 'Alice'
!person27.lastName := 'Liddel'

!new Account('account18')
!account18.iban := 'WL123456789012345678901'
!account18.balance := 500

!new Account('account19')
!account19.iban := 'WL123456789012345678902'
!account19.balance := 750

!new Account('account20')
!account20.iban := 'WL123456789012345678903'
!account20.balance := 1000

!insert (person27, account18) into Ownership
!insert (person27, account19) into Ownership
!insert (person27, account20) into Ownership
!insert (bank16, account18) into AccountOfBanks
!insert (bank16, account19) into AccountOfBanks
!insert (bank16, account20) into AccountOfBanks
```

# Category: Realistic but invalid
Here are instances that represent realistic scenarios but violate constraints in the "BankAccount" model, highlighting potential areas where the model's constraints might be overly restrictive or unrealistic.

### Instance 1: Joint Account Ownership by Minors

**Bank:**
- **country**: United States
- **name**: Wells Fargo
- **bic**: WFBIUS6S

**Persons:**
1. **Person 1 (Owner):**
   - **firstName**: Tom
   - **lastName**: Anderson
   - **age**: 17

2. **Person 2 (Owner):**
   - **firstName**: Lucy
   - **lastName**: Anderson
   - **age**: 16

**Account:**
- **iban**: US12345678901234567890
- **balance**: 200

**Relationships:**
- Both Tom and Lucy Anderson are joint owners of their shared savings account, which is a common scenario in families where parents set up accounts for minors for educational purposes or allowances.
- This violates the `AdultOwners` constraint since neither owner is over the age of 18.

### Instance 2: Overdraft Enabled Account

**Bank:**
- **country**: Canada
- **name**: TD Bank
- **bic**: TDOMCATTTOR

**Person (Owner and User):**
- **firstName**: Natalie
- **lastName**: Green
- **age**: 30

**Account:**
- **iban**: CA12345678901234567890
- **balance**: -100

**Relationships:**
- Natalie Green uses her account with an overdraft protection feature, allowing for a negative balance temporarily to cover expenses.
- This common banking practice violates the `positiveBalance` constraint, which doesn't account for overdraft scenarios.

### Instance 3: Family Account for a Minor

**Bank:**
- **country**: Australia
- **name**: ANZ
- **bic**: ANZBAU3M

**Persons:**
1. **Person 1 (Owner):**
   - **firstName**: Jake
   - **lastName**: Miller
   - **age**: 15

2. **Person 2 (User):**
   - **firstName**: Laura
   - **lastName**: Miller
   - **age**: 40

**Account:**
- **iban**: AU12345678901234567890
- **balance**: 50

**Relationships:**
- Jake Miller, a minor, is the nominal owner of a savings account, often set up by parents for managing small savings.
- Laura Miller, his mother, can use the account for managing Jake's funds.
- This setup violates the `AdultOwners` constraint as Jake is under 18.

### Instance 4: Zero Balance Allowed

**Bank:**
- **country**: France
- **name**: Crédit Agricole
- **bic**: AGRIFRPP

**Person (Owner and User):**
- **firstName**: François
- **lastName**: Dubois
- **age**: 50

**Account:**
- **iban**: FR7630006000011234567890189
- **balance**: 0

**Relationships:**
- François Dubois has a checking account with a zero balance, a common scenario where accounts are maintained for transactions without maintaining a surplus.
- This violates the `positiveBalance` constraint, as the model does not accommodate zero balances which are often valid in real-life banking.

### Additional Considerations
These instances illustrate scenarios that are realistic but violate the model's constraints:
- Joint accounts for minors and family savings accounts set up for children are common but violate the age constraint.
- Overdraft and zero-balance accounts are practical features that the model's positive balance constraint does not accommodate.

These examples suggest that the model could be enhanced by allowing exceptions for joint ownership with minors, overdraft protections, and zero-balance accounts.
```
### Instance 1: Joint Account Ownership by Minors

!new Bank('bank17')
!bank17.country := 'United States'
!bank17.name := 'Wells Fargo'
!bank17.bic := 'WFBIUS6S'

!new Person('person28')
!person28.age := 17
!person28.firstName := 'Tom'
!person28.lastName := 'Anderson'

!new Person('person29')
!person29.age := 16
!person29.firstName := 'Lucy'
!person29.lastName := 'Anderson'

!new Account('account21')
!account21.iban := 'US12345678901234567890'
!account21.balance := 200

!insert (person28, account21) into Ownership
!insert (person29, account21) into Ownership
!insert (bank17, account21) into AccountOfBanks

### Instance 2: Overdraft Enabled Account

!new Bank('bank18')
!bank18.country := 'Canada'
!bank18.name := 'TD Bank'
!bank18.bic := 'TDOMCATTTOR'

!new Person('person30')
!person30.age := 30
!person30.firstName := 'Natalie'
!person30.lastName := 'Green'

!new Account('account22')
!account22.iban := 'CA12345678901234567890'
!account22.balance := -100

!insert (person30, account22) into Ownership
!insert (person30, account22) into Use
!insert (bank18, account22) into AccountOfBanks

### Instance 3: Family Account for a Minor

!new Bank('bank19')
!bank19.country := 'Australia'
!bank19.name := 'ANZ'
!bank19.bic := 'ANZBAU3M'

!new Person('person31')
!person31.age := 15
!person31.firstName := 'Jake'
!person31.lastName := 'Miller'

!new Person('person32')
!person32.age := 40
!person32.firstName := 'Laura'
!person32.lastName := 'Miller'

!new Account('account23')
!account23.iban := 'AU12345678901234567890'
!account23.balance := 50

!insert (person31, account23) into Ownership
!insert (person32, account23) into Use
!insert (bank19, account23) into AccountOfBanks

### Instance 4: Zero Balance Allowed

!new Bank('bank20')
!bank20.country := 'France'
!bank20.name := 'Crédit Agricole'
!bank20.bic := 'AGRIFRPP'

!new Person('person33')
!person33.age := 50
!person33.firstName := 'François'
!person33.lastName := 'Dubois'

!new Account('account24')
!account24.iban := 'FR7630006000011234567890189'
!account24.balance := 0

!insert (person33, account24) into Ownership
!insert (person33, account24) into Use
!insert (bank20, account24) into AccountOfBanks
```