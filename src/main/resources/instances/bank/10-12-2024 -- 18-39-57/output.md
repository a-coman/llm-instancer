# Description

The model represents a simplified banking system highlighting the relationship between banks, accounts, and people. It consists of three primary classes: Bank, Account, and Person, each with specific attributes and associated purposes.

- **Bank Class**: This class includes the attributes `country`, `name`, and `bic` to identify the bank's location, its name, and its Bank Identifier Code (BIC), respectively. The purpose of this class is to represent a financial institution.

- **Account Class**: This class has attributes `iban` and `balance`. The `iban` is a unique identifier for each bank account, while `balance` represents the current amount of money in the account. This class models the concept of a bank account held by individuals.

- **Person Class**: It includes attributes `firstName`, `lastName`, and `age`. These attributes are used to store personal information about individuals who own or use bank accounts. 

# Relationships

- **Ownership Association**: This association defines a relationship between `Person` and `Account`. A `Person` (owner) must be associated with at least one `Account` (accounts), whereas an `Account` can be owned by exactly one `Person`. This is represented by the multiplicity `Person [1]` to `Account [*]`.

- **Use Association**: This association allows a `Person` to use multiple accounts, and each `Account` can be used by multiple people. The multiplicity in this case is `Person [*]` to `Account [*]`, indicating many-to-many relationships.

- **AccountOfBanks Composition**: This composition indicates that each `Bank` is responsible for one or more `Accounts`. Each `Account` is associated with exactly one `Bank`. The multiplicity is `Bank [1]` to `Account [*]`, showing a one-to-many relationship.

# Invariants

- **AdultOwners**: This invariant ensures that only individuals older than 18 years can own an account. It is defined as `self.owner.age > 18`.

- **PositiveBalance**: This invariant ensures that the balance of an account must always be positive. It is defined as `self.balance > 0`.

# Category: Boundary Instances
## Instance 1: Minimal Boundary Instance

### Entities

- **Bank**
  - **Country**: "Japan"
  - **Name**: "Tokyo Financial Group"
  - **BIC**: "TFGJPJT1"

- **Account**
  - **IBAN**: "JP1230000001"
  - **Balance**: 0.01 (boundary of positive balance)

- **Person**
  - **FirstName**: "Yuki"
  - **LastName**: "Tanaka"
  - **Age**: 19 (boundary age for adult)

### Relationships

- **Ownership Association**
  - **Owner**: Yuki Tanaka
  - **Account**: JP1230000001

- **Use Association**
  - **User**: Yuki Tanaka
  - **Account**: JP1230000001

- **AccountOfBanks Composition**
  - **Bank**: Tokyo Financial Group
  - **Account**: JP1230000001

---

## Instance 2: Maximum Multiplicity with Diverse Regional Context

### Entities

- **Bank**
  - **Country**: "Brazil"
  - **Name**: "Banco do Brasil"
  - **BIC**: "BRASBRRJ"

- **Account 1**
  - **IBAN**: "BR12 0000 0000 0001"
  - **Balance**: 100000.00

- **Account 2**
  - **IBAN**: "BR12 0000 0000 0002"
  - **Balance**: 50000.00

- **Person**
  - **FirstName**: "Lucas"
  - **LastName**: "Silva"
  - **Age**: 30

### Relationships

- **Ownership Association**
  - **Owner**: Lucas Silva
  - **Accounts**: BR12 0000 0000 0001, BR12 0000 0000 0002

- **Use Association**
  - **User**: Lucas Silva
  - **Accounts**: BR12 0000 0000 0001, BR12 0000 0000 0002

- **AccountOfBanks Composition**
  - **Bank**: Banco do Brasil
  - **Accounts**: BR12 0000 0000 0001, BR12 0000 0000 0002

---

## Instance 3: Empty Collection Scenario

### Entities

- **Bank**
  - **Country**: "Germany"
  - **Name**: "Deutsche Bank"
  - **BIC**: "DEUTDEFF"

- **Account**
  - **IBAN**: "DE89370400440532013000"
  - **Balance**: 1500.75

- **Person**
  - **FirstName**: "Anna"
  - **LastName**: "Müller"
  - **Age**: 45

### Relationships

- **Ownership Association**
  - **Owner**: Anna Müller
  - **Account**: DE89370400440532013000

- **Use Association**
  - **Users**: None (empty collection of users, testing optionality)

- **AccountOfBanks Composition**
  - **Bank**: Deutsche Bank
  - **Account**: DE89370400440532013000

---

## Instance 4: Extreme Values and Multi-user Scenario

### Entities

- **Bank**
  - **Country**: "United States"
  - **Name**: "American National Bank"
  - **BIC**: "ANBUS33"

- **Account**
  - **IBAN**: "US64 0000 0000 0000 0000 0000"
  - **Balance**: 999999.99 (extreme high balance)

- **Person 1**
  - **FirstName**: "John"
  - **LastName**: "Doe"
  - **Age**: 60

- **Person 2**
  - **FirstName**: "Jane"
  - **LastName**: "Smith"
  - **Age**: 25

### Relationships

- **Ownership Association**
  - **Owner**: John Doe
  - **Account**: US64 0000 0000 0000 0000 0000

- **Use Association**
  - **Users**: John Doe, Jane Smith
  - **Account**: US64 0000 0000 0000 0000 0000

- **AccountOfBanks Composition**
  - **Bank**: American National Bank
  - **Account**: US64 0000 0000 0000 0000 0000

These instances cover a range of boundary conditions, including minimal and maximal multiplicities, empty collections, and extreme numeric values, while incorporating regional and cultural diversity.
```
!new Bank('bank1')
!bank1.country := 'Japan'
!bank1.name := 'Tokyo Financial Group'
!bank1.bic := 'TFGJPJT1'

!new Account('account1')
!account1.iban := 'JP1230000001'
!account1.balance := 0.01

!new Person('person1')
!person1.firstName := 'Yuki'
!person1.lastName := 'Tanaka'
!person1.age := 19

!insert (person1, account1) into Ownership
!insert (person1, account1) into Use
!insert (bank1, account1) into AccountOfBanks

!new Bank('bank2')
!bank2.country := 'Brazil'
!bank2.name := 'Banco do Brasil'
!bank2.bic := 'BRASBRRJ'

!new Account('account2')
!account2.iban := 'BR12 0000 0000 0001'
!account2.balance := 100000.00

!new Account('account3')
!account3.iban := 'BR12 0000 0000 0002'
!account3.balance := 50000.00

!new Person('person2')
!person2.firstName := 'Lucas'
!person2.lastName := 'Silva'
!person2.age := 30

!insert (person2, account2) into Ownership
!insert (person2, account3) into Ownership
!insert (person2, account2) into Use
!insert (person2, account3) into Use
!insert (bank2, account2) into AccountOfBanks
!insert (bank2, account3) into AccountOfBanks

!new Bank('bank3')
!bank3.country := 'Germany'
!bank3.name := 'Deutsche Bank'
!bank3.bic := 'DEUTDEFF'

!new Account('account4')
!account4.iban := 'DE89370400440532013000'
!account4.balance := 1500.75

!new Person('person3')
!person3.firstName := 'Anna'
!person3.lastName := 'Müller'
!person3.age := 45

!insert (person3, account4) into Ownership
!insert (bank3, account4) into AccountOfBanks

!new Bank('bank4')
!bank4.country := 'United States'
!bank4.name := 'American National Bank'
!bank4.bic := 'ANBUS33'

!new Account('account5')
!account5.iban := 'US64 0000 0000 0000 0000 0000'
!account5.balance := 999999.99

!new Person('person4')
!person4.firstName := 'John'
!person4.lastName := 'Doe'
!person4.age := 60

!new Person('person5')
!person5.firstName := 'Jane'
!person5.lastName := 'Smith'
!person5.age := 25

!insert (person4, account5) into Ownership
!insert (person4, account5) into Use
!insert (person5, account5) into Use
!insert (bank4, account5) into AccountOfBanks
```

# Category: Complex Instances
## Complex Instance 1: Multi-Bank, Multi-Account, Multi-User Scenario

### Entities

#### Banks

1. **Bank**
   - **Country**: "Canada"
   - **Name**: "Royal Bank of Canada"
   - **BIC**: "ROYCCAT2"

2. **Bank**
   - **Country**: "India"
   - **Name**: "State Bank of India"
   - **BIC**: "SBININBB"

#### Accounts

1. **Account**
   - **IBAN**: "CA12345678901234567"
   - **Balance**: 2500.50

2. **Account**
   - **IBAN**: "IN123456789012"
   - **Balance**: 8000.75

3. **Account**
   - **IBAN**: "IN987654321098"
   - **Balance**: 15000.00

#### People

1. **Person**
   - **FirstName**: "Alice"
   - **LastName**: "Brown"
   - **Age**: 35

2. **Person**
   - **FirstName**: "Raj"
   - **LastName**: "Kumar"
   - **Age**: 28

3. **Person**
   - **FirstName**: "Sophie"
   - **LastName**: "Martinez"
   - **Age**: 22

### Relationships

#### Ownership Association

- **Alice Brown** owns **CA12345678901234567**.
- **Raj Kumar** owns **IN123456789012**.
- **Sophie Martinez** owns **IN987654321098**.

#### Use Association

- **Alice Brown** uses **CA12345678901234567** and **IN987654321098**.
- **Raj Kumar** uses **IN123456789012** and **CA12345678901234567**.
- **Sophie Martinez** uses **IN987654321098** and **IN123456789012**.

#### AccountOfBanks Composition

- **Royal Bank of Canada** has **CA12345678901234567**.
- **State Bank of India** has **IN123456789012** and **IN987654321098**.

---

## Complex Instance 2: Intercontinental Collaboration with Varying Age and Balance

### Entities

#### Banks

1. **Bank**
   - **Country**: "United Kingdom"
   - **Name**: "Barclays UK"
   - **BIC**: "BARCGB22"

2. **Bank**
   - **Country**: "Australia"
   - **Name**: "Commonwealth Bank"
   - **BIC**: "CTBAAU2S"

#### Accounts

1. **Account**
   - **IBAN**: "GB12BARC20201530093459"
   - **Balance**: 500.00

2. **Account**
   - **IBAN**: "AU12345678901"
   - **Balance**: 10000.00

3. **Account**
   - **IBAN**: "AU10987654321"
   - **Balance**: 2000.00

#### People

1. **Person**
   - **FirstName**: "Thomas"
   - **LastName**: "Clark"
   - **Age**: 40

2. **Person**
   - **FirstName**: "Emily"
   - **LastName**: "Liu"
   - **Age**: 20

3. **Person**
   - **FirstName**: "Michael"
   - **LastName**: "Nguyen"
   - **Age**: 45

### Relationships

#### Ownership Association

- **Thomas Clark** owns **GB12BARC20201530093459**.
- **Emily Liu** owns **AU12345678901**.
- **Michael Nguyen** owns **AU10987654321**.

#### Use Association

- **Thomas Clark** uses **GB12BARC20201530093459** and **AU10987654321**.
- **Emily Liu** uses **AU12345678901** and **GB12BARC20201530093459**.
- **Michael Nguyen** uses **AU10987654321** and **AU12345678901**.

#### AccountOfBanks Composition

- **Barclays UK** has **GB12BARC20201530093459**.
- **Commonwealth Bank** has **AU12345678901** and **AU10987654321**.

---

These complex instances demonstrate intricate interactions among multiple banks, accounts, and people, adhering to the defined constraints and relationships. They reflect diverse geographic and cultural settings, with varied ownership and usage patterns, showcasing the flexibility and robustness of the model.
```
!new Bank('bank5')
!bank5.country := 'Canada'
!bank5.name := 'Royal Bank of Canada'
!bank5.bic := 'ROYCCAT2'

!new Bank('bank6')
!bank6.country := 'India'
!bank6.name := 'State Bank of India'
!bank6.bic := 'SBININBB'

!new Account('account6')
!account6.iban := 'CA12345678901234567'
!account6.balance := 2500.50

!new Account('account7')
!account7.iban := 'IN123456789012'
!account7.balance := 8000.75

!new Account('account8')
!account8.iban := 'IN987654321098'
!account8.balance := 15000.00

!new Person('person6')
!person6.firstName := 'Alice'
!person6.lastName := 'Brown'
!person6.age := 35

!new Person('person7')
!person7.firstName := 'Raj'
!person7.lastName := 'Kumar'
!person7.age := 28

!new Person('person8')
!person8.firstName := 'Sophie'
!person8.lastName := 'Martinez'
!person8.age := 22

!insert (person6, account6) into Ownership
!insert (person7, account7) into Ownership
!insert (person8, account8) into Ownership

!insert (person6, account6) into Use
!insert (person6, account8) into Use
!insert (person7, account7) into Use
!insert (person7, account6) into Use
!insert (person8, account8) into Use
!insert (person8, account7) into Use

!insert (bank5, account6) into AccountOfBanks
!insert (bank6, account7) into AccountOfBanks
!insert (bank6, account8) into AccountOfBanks

!new Bank('bank7')
!bank7.country := 'United Kingdom'
!bank7.name := 'Barclays UK'
!bank7.bic := 'BARCGB22'

!new Bank('bank8')
!bank8.country := 'Australia'
!bank8.name := 'Commonwealth Bank'
!bank8.bic := 'CTBAAU2S'

!new Account('account9')
!account9.iban := 'GB12BARC20201530093459'
!account9.balance := 500.00

!new Account('account10')
!account10.iban := 'AU12345678901'
!account10.balance := 10000.00

!new Account('account11')
!account11.iban := 'AU10987654321'
!account11.balance := 2000.00

!new Person('person9')
!person9.firstName := 'Thomas'
!person9.lastName := 'Clark'
!person9.age := 40

!new Person('person10')
!person10.firstName := 'Emily'
!person10.lastName := 'Liu'
!person10.age := 20

!new Person('person11')
!person11.firstName := 'Michael'
!person11.lastName := 'Nguyen'
!person11.age := 45

!insert (person9, account9) into Ownership
!insert (person10, account10) into Ownership
!insert (person11, account11) into Ownership

!insert (person9, account9) into Use
!insert (person9, account11) into Use
!insert (person10, account10) into Use
!insert (person10, account9) into Use
!insert (person11, account11) into Use
!insert (person11, account10) into Use

!insert (bank7, account9) into AccountOfBanks
!insert (bank8, account10) into AccountOfBanks
!insert (bank8, account11) into AccountOfBanks
```

# Category: Unrealistc but valid
## Unrealistic but Valid Instance 1: A Single Person Owning and Using All Accounts in Multiple Banks

### Entities

#### Banks

1. **Bank**
   - **Country**: "Antarctica"
   - **Name**: "Penguin Bank"
   - **BIC**: "PNGBANT1"

2. **Bank**
   - **Country**: "Atlantis"
   - **Name**: "Atlantis Financial"
   - **BIC**: "ATLAFIN1"

#### Accounts

1. **Account**
   - **IBAN**: "AQ123456789012345678"
   - **Balance**: 50.00

2. **Account**
   - **IBAN**: "AQ987654321098765432"
   - **Balance**: 100.00

3. **Account**
   - **IBAN**: "AT12345678901234567"
   - **Balance**: 200.00

4. **Account**
   - **IBAN**: "AT98765432109876543"
   - **Balance**: 300.00

#### Person

1. **Person**
   - **FirstName**: "Omni"
   - **LastName**: "User"
   - **Age**: 30

### Relationships

#### Ownership Association

- **Omni User** owns **AQ123456789012345678**, **AQ987654321098765432**, **AT12345678901234567**, **AT98765432109876543**.

#### Use Association

- **Omni User** uses **AQ123456789012345678**, **AQ987654321098765432**, **AT12345678901234567**, **AT98765432109876543**.

#### AccountOfBanks Composition

- **Penguin Bank** has **AQ123456789012345678**, **AQ987654321098765432**.
- **Atlantis Financial** has **AT12345678901234567**, **AT98765432109876543**.

---

## Unrealistic but Valid Instance 2: Multiple People Owning a Single Account Each with Excessive Balance

### Entities

#### Banks

1. **Bank**
   - **Country**: "Utopia"
   - **Name**: "Utopian Bank"
   - **BIC**: "UTPBUTP1"

#### Accounts

1. **Account**
   - **IBAN**: "UT00000000000000001"
   - **Balance**: 1000000000.00

2. **Account**
   - **IBAN**: "UT00000000000000002"
   - **Balance**: 1000000000.00

3. **Account**
   - **IBAN**: "UT00000000000000003"
   - **Balance**: 1000000000.00

#### People

1. **Person**
   - **FirstName**: "Richie"
   - **LastName**: "Rich"
   - **Age**: 25

2. **Person**
   - **FirstName**: "Wealthy"
   - **LastName**: "McWealth"
   - **Age**: 45

3. **Person**
   - **FirstName**: "Prosper"
   - **LastName**: "Gold"
   - **Age**: 60

### Relationships

#### Ownership Association

- **Richie Rich** owns **UT00000000000000001**.
- **Wealthy McWealth** owns **UT00000000000000002**.
- **Prosper Gold** owns **UT00000000000000003**.

#### Use Association

- **Richie Rich** uses **UT00000000000000001**, **UT00000000000000002**.
- **Wealthy McWealth** uses **UT00000000000000002**, **UT00000000000000003**.
- **Prosper Gold** uses **UT00000000000000003**, **UT00000000000000001**.

#### AccountOfBanks Composition

- **Utopian Bank** has **UT00000000000000001**, **UT00000000000000002**, **UT00000000000000003**.

---

## Unrealistic but Valid Instance 3: Bank with Accounts Owned by People from Different Continents

### Entities

#### Banks

1. **Bank**
   - **Country**: "Global"
   - **Name**: "Global Unity Bank"
   - **BIC**: "GLBLUNI1"

#### Accounts

1. **Account**
   - **IBAN**: "GL12345678901234567"
   - **Balance**: 10000.00

2. **Account**
   - **IBAN**: "GL98765432109876543"
   - **Balance**: 20000.00

3. **Account**
   - **IBAN**: "GL11223344556677889"
   - **Balance**: 30000.00

#### People

1. **Person**
   - **FirstName**: "John"
   - **LastName**: "Doe"
   - **Age**: 33
   - **Location**: "North America"

2. **Person**
   - **FirstName**: "Akira"
   - **LastName**: "Yamamoto"
   - **Age**: 29
   - **Location**: "Asia"

3. **Person**
   - **FirstName**: "Fatima"
   - **LastName**: "Hassan"
   - **Age**: 40
   - **Location**: "Africa"

### Relationships

#### Ownership Association

- **John Doe** owns **GL12345678901234567**.
- **Akira Yamamoto** owns **GL98765432109876543**.
- **Fatima Hassan** owns **GL11223344556677889**.

#### Use Association

- **John Doe** uses **GL12345678901234567**, **GL98765432109876543**.
- **Akira Yamamoto** uses **GL98765432109876543**, **GL11223344556677889**.
- **Fatima Hassan** uses **GL11223344556677889**, **GL12345678901234567**.

#### AccountOfBanks Composition

- **Global Unity Bank** has **GL12345678901234567**, **GL98765432109876543**, **GL11223344556677889**.

These instances are valid according to the model's constraints but represent scenarios that are highly improbable in reality, such as a person owning accounts in fictional or geographically extreme banks and ownership patterns that defy conventional banking practices.
```
!new Bank('bank9')
!bank9.country := 'Antarctica'
!bank9.name := 'Penguin Bank'
!bank9.bic := 'PNGBANT1'

!new Bank('bank10')
!bank10.country := 'Atlantis'
!bank10.name := 'Atlantis Financial'
!bank10.bic := 'ATLAFIN1'

!new Account('account12')
!account12.iban := 'AQ123456789012345678'
!account12.balance := 50.00

!new Account('account13')
!account13.iban := 'AQ987654321098765432'
!account13.balance := 100.00

!new Account('account14')
!account14.iban := 'AT12345678901234567'
!account14.balance := 200.00

!new Account('account15')
!account15.iban := 'AT98765432109876543'
!account15.balance := 300.00

!new Person('person12')
!person12.firstName := 'Omni'
!person12.lastName := 'User'
!person12.age := 30

!insert (person12, account12) into Ownership
!insert (person12, account13) into Ownership
!insert (person12, account14) into Ownership
!insert (person12, account15) into Ownership

!insert (person12, account12) into Use
!insert (person12, account13) into Use
!insert (person12, account14) into Use
!insert (person12, account15) into Use

!insert (bank9, account12) into AccountOfBanks
!insert (bank9, account13) into AccountOfBanks
!insert (bank10, account14) into AccountOfBanks
!insert (bank10, account15) into AccountOfBanks

!new Bank('bank11')
!bank11.country := 'Utopia'
!bank11.name := 'Utopian Bank'
!bank11.bic := 'UTPBUTP1'

!new Account('account16')
!account16.iban := 'UT00000000000000001'
!account16.balance := 1000000000.00

!new Account('account17')
!account17.iban := 'UT00000000000000002'
!account17.balance := 1000000000.00

!new Account('account18')
!account18.iban := 'UT00000000000000003'
!account18.balance := 1000000000.00

!new Person('person13')
!person13.firstName := 'Richie'
!person13.lastName := 'Rich'
!person13.age := 25

!new Person('person14')
!person14.firstName := 'Wealthy'
!person14.lastName := 'McWealth'
!person14.age := 45

!new Person('person15')
!person15.firstName := 'Prosper'
!person15.lastName := 'Gold'
!person15.age := 60

!insert (person13, account16) into Ownership
!insert (person14, account17) into Ownership
!insert (person15, account18) into Ownership

!insert (person13, account16) into Use
!insert (person13, account17) into Use
!insert (person14, account17) into Use
!insert (person14, account18) into Use
!insert (person15, account18) into Use
!insert (person15, account16) into Use

!insert (bank11, account16) into AccountOfBanks
!insert (bank11, account17) into AccountOfBanks
!insert (bank11, account18) into AccountOfBanks

!new Bank('bank12')
!bank12.country := 'Global'
!bank12.name := 'Global Unity Bank'
!bank12.bic := 'GLBLUNI1'

!new Account('account19')
!account19.iban := 'GL12345678901234567'
!account19.balance := 10000.00

!new Account('account20')
!account20.iban := 'GL98765432109876543'
!account20.balance := 20000.00

!new Account('account21')
!account21.iban := 'GL11223344556677889'
!account21.balance := 30000.00

!new Person('person16')
!person16.firstName := 'John'
!person16.lastName := 'Doe'
!person16.age := 33

!new Person('person17')
!person17.firstName := 'Akira'
!person17.lastName := 'Yamamoto'
!person17.age := 29

!new Person('person18')
!person18.firstName := 'Fatima'
!person18.lastName := 'Hassan'
!person18.age := 40

!insert (person16, account19) into Ownership
!insert (person17, account20) into Ownership
!insert (person18, account21) into Ownership

!insert (person16, account19) into Use
!insert (person16, account20) into Use
!insert (person17, account20) into Use
!insert (person17, account21) into Use
!insert (person18, account21) into Use
!insert (person18, account19) into Use

!insert (bank12, account19) into AccountOfBanks
!insert (bank12, account20) into AccountOfBanks
!insert (bank12, account21) into AccountOfBanks
```

# Category: Realistic but invalid
## Realistic but Invalid Instance 1: Minor Owning an Account

### Scenario
In many practical situations, minors may have bank accounts under a custodian's supervision. However, the model restricts account ownership to individuals over 18.

### Entities

#### Bank

- **Bank**
  - **Country**: "United States"
  - **Name**: "Bank of Youth"
  - **BIC**: "BOYUS33"

#### Account

- **Account**
  - **IBAN**: "US123456789012345678"
  - **Balance**: 100.00

#### Person

- **Person**
  - **FirstName**: "Tommy"
  - **LastName**: "Young"
  - **Age**: 16

### Relationships

#### Ownership Association

- **Tommy Young** owns **US123456789012345678**. *(Violates AdultOwners invariant)*

#### Use Association

- **Tommy Young** uses **US123456789012345678**.

#### AccountOfBanks Composition

- **Bank of Youth** has **US123456789012345678**.

---

## Realistic but Invalid Instance 2: Account with Zero Balance

### Scenario
Accounts are often allowed to have zero balance in real life, especially savings accounts that might have just been opened or have fees waived.

### Entities

#### Bank

- **Bank**
  - **Country**: "Germany"
  - **Name**: "Deutsche Savings"
  - **BIC**: "DEUSDEFF"

#### Account

- **Account**
  - **IBAN**: "DE12345678901234567890"
  - **Balance**: 0.00 *(Violates PositiveBalance invariant)*

#### Person

- **Person**
  - **FirstName**: "Klaus"
  - **LastName**: "Müller"
  - **Age**: 40

### Relationships

#### Ownership Association

- **Klaus Müller** owns **DE12345678901234567890**.

#### Use Association

- **Klaus Müller** uses **DE12345678901234567890**.

#### AccountOfBanks Composition

- **Deutsche Savings** has **DE12345678901234567890**.

---

## Realistic but Invalid Instance 3: Shared Account Ownership

### Scenario
Joint accounts are common in reality, where multiple people have ownership rights, not just usage rights, to an account. This scenario violates the single ownership constraint.

### Entities

#### Bank

- **Bank**
  - **Country**: "United Kingdom"
  - **Name**: "Joint Financial"
  - **BIC**: "JOINTGB2L"

#### Accounts

- **Account**
  - **IBAN**: "GB12345678901234567890"
  - **Balance**: 1500.00

#### People

1. **Person**
   - **FirstName**: "Robert"
   - **LastName**: "Smith"
   - **Age**: 50

2. **Person**
   - **FirstName**: "Emma"
   - **LastName**: "Jones"
   - **Age**: 48

### Relationships

#### Ownership Association

- **Robert Smith** and **Emma Jones** both own **GB12345678901234567890**. *(Violates single-owner constraint)*

#### Use Association

- **Robert Smith** uses **GB12345678901234567890**.
- **Emma Jones** uses **GB12345678901234567890**.

#### AccountOfBanks Composition

- **Joint Financial** has **GB12345678901234567890**.

---

These instances illustrate realistic scenarios that the current model constraints do not accommodate, such as minors owning accounts, accounts with zero balance, and joint ownership, highlighting areas where the model could be expanded to better reflect real-world banking situations.
```
!new Bank('bank13')
!bank13.country := 'United States'
!bank13.name := 'Bank of Youth'
!bank13.bic := 'BOYUS33'

!new Account('account22')
!account22.iban := 'US123456789012345678'
!account22.balance := 100.00

!new Person('person19')
!person19.firstName := 'Tommy'
!person19.lastName := 'Young'
!person19.age := 16

!insert (person19, account22) into Ownership
!insert (person19, account22) into Use
!insert (bank13, account22) into AccountOfBanks

!new Bank('bank14')
!bank14.country := 'Germany'
!bank14.name := 'Deutsche Savings'
!bank14.bic := 'DEUSDEFF'

!new Account('account23')
!account23.iban := 'DE12345678901234567890'
!account23.balance := 0.00

!new Person('person20')
!person20.firstName := 'Klaus'
!person20.lastName := 'Müller'
!person20.age := 40

!insert (person20, account23) into Ownership
!insert (person20, account23) into Use
!insert (bank14, account23) into AccountOfBanks

!new Bank('bank15')
!bank15.country := 'United Kingdom'
!bank15.name := 'Joint Financial'
!bank15.bic := 'JOINTGB2L'

!new Account('account24')
!account24.iban := 'GB12345678901234567890'
!account24.balance := 1500.00

!new Person('person21')
!person21.firstName := 'Robert'
!person21.lastName := 'Smith'
!person21.age := 50

!new Person('person22')
!person22.firstName := 'Emma'
!person22.lastName := 'Jones'
!person22.age := 48

!insert (person21, account24) into Ownership
!insert (person22, account24) into Ownership

!insert (person21, account24) into Use
!insert (person22, account24) into Use
!insert (bank15, account24) into AccountOfBanks
```

# Category: Baseline Instances
## Baseline Instance 1: Single Person with One Account in a National Bank

### Entities

#### Bank

- **Bank**
  - **Country**: "United States"
  - **Name**: "Chase Bank"
  - **BIC**: "CHASUS33"

#### Account

- **Account**
  - **IBAN**: "US12345678901234567890"
  - **Balance**: 500.00

#### Person

- **Person**
  - **FirstName**: "John"
  - **LastName**: "Doe"
  - **Age**: 30

### Relationships

#### Ownership Association

- **John Doe** owns **US12345678901234567890**.

#### Use Association

- **John Doe** uses **US12345678901234567890**.

#### AccountOfBanks Composition

- **Chase Bank** has **US12345678901234567890**.

---

## Baseline Instance 2: Family with Multiple Accounts

### Entities

#### Bank

- **Bank**
  - **Country**: "Canada"
  - **Name**: "Royal Bank of Canada"
  - **BIC**: "ROYCCAT2"

#### Accounts

1. **Account**
   - **IBAN**: "CA123456789012345678"
   - **Balance**: 1500.00

2. **Account**
   - **IBAN**: "CA987654321098765432"
   - **Balance**: 250.00

#### People

1. **Person**
   - **FirstName**: "Alice"
   - **LastName**: "Smith"
   - **Age**: 45

2. **Person**
   - **FirstName**: "Bob"
   - **LastName**: "Smith"
   - **Age**: 50

### Relationships

#### Ownership Association

- **Alice Smith** owns **CA123456789012345678**.
- **Bob Smith** owns **CA987654321098765432**.

#### Use Association

- **Alice Smith** uses **CA123456789012345678** and **CA987654321098765432**.
- **Bob Smith** uses **CA987654321098765432** and **CA123456789012345678**.

#### AccountOfBanks Composition

- **Royal Bank of Canada** has **CA123456789012345678** and **CA987654321098765432**.

---

## Baseline Instance 3: Business Account Used by Multiple Employees

### Entities

#### Bank

- **Bank**
  - **Country**: "United Kingdom"
  - **Name**: "HSBC UK"
  - **BIC**: "MIDLGB22"

#### Account

- **Account**
  - **IBAN**: "GB12345678901234567890"
  - **Balance**: 10000.00

#### People

1. **Person**
   - **FirstName**: "Charlie"
   - **LastName**: "Brown"
   - **Age**: 35

2. **Person**
   - **FirstName**: "Diana"
   - **LastName**: "Johnson"
   - **Age**: 40

3. **Person**
   - **FirstName**: "Edward"
   - **LastName**: "White"
   - **Age**: 28

### Relationships

#### Ownership Association

- **Charlie Brown** owns **GB12345678901234567890**.

#### Use Association

- **Charlie Brown**, **Diana Johnson**, and **Edward White** use **GB12345678901234567890**.

#### AccountOfBanks Composition

- **HSBC UK** has **GB12345678901234567890**.

---

These baseline instances represent typical scenarios within the banking model, demonstrating various relationships and configurations involving individuals and banks. They ensure that every class and relationship is represented in a straightforward and realistic manner, adhering to the model's constraints.
```
!new Bank('bank16')
!bank16.country := 'United States'
!bank16.name := 'Chase Bank'
!bank16.bic := 'CHASUS33'

!new Account('account25')
!account25.iban := 'US12345678901234567890'
!account25.balance := 500.00

!new Person('person23')
!person23.firstName := 'John'
!person23.lastName := 'Doe'
!person23.age := 30

!insert (person23, account25) into Ownership
!insert (person23, account25) into Use
!insert (bank16, account25) into AccountOfBanks

!new Bank('bank17')
!bank17.country := 'Canada'
!bank17.name := 'Royal Bank of Canada'
!bank17.bic := 'ROYCCAT2'

!new Account('account26')
!account26.iban := 'CA123456789012345678'
!account26.balance := 1500.00

!new Account('account27')
!account27.iban := 'CA987654321098765432'
!account27.balance := 250.00

!new Person('person24')
!person24.firstName := 'Alice'
!person24.lastName := 'Smith'
!person24.age := 45

!new Person('person25')
!person25.firstName := 'Bob'
!person25.lastName := 'Smith'
!person25.age := 50

!insert (person24, account26) into Ownership
!insert (person25, account27) into Ownership

!insert (person24, account26) into Use
!insert (person24, account27) into Use
!insert (person25, account27) into Use
!insert (person25, account26) into Use

!insert (bank17, account26) into AccountOfBanks
!insert (bank17, account27) into AccountOfBanks

!new Bank('bank18')
!bank18.country := 'United Kingdom'
!bank18.name := 'HSBC UK'
!bank18.bic := 'MIDLGB22'

!new Account('account28')
!account28.iban := 'GB12345678901234567890'
!account28.balance := 10000.00

!new Person('person26')
!person26.firstName := 'Charlie'
!person26.lastName := 'Brown'
!person26.age := 35

!new Person('person27')
!person27.firstName := 'Diana'
!person27.lastName := 'Johnson'
!person27.age := 40

!new Person('person28')
!person28.firstName := 'Edward'
!person28.lastName := 'White'
!person28.age := 28

!insert (person26, account28) into Ownership

!insert (person26, account28) into Use
!insert (person27, account28) into Use
!insert (person28, account28) into Use

!insert (bank18, account28) into AccountOfBanks
```