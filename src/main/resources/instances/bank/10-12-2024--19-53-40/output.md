# Description

The model describes a simplified banking system using three primary classes: `Bank`, `Account`, and `Person`. The purpose is to represent the relationships and constraints involved in managing bank accounts and their associations with people and banks.

## Components

### Bank
- **Attributes**:
  - `country: String` - The country where the bank is located.
  - `name: String` - The name of the bank.
  - `bic: String` - The Bank Identifier Code, a unique identifier for the bank.

### Account
- **Attributes**:
  - `iban: String` - The International Bank Account Number, uniquely identifying the account.
  - `balance: Integer` - The current balance held in the account.

### Person
- **Attributes**:
  - `firstName: String` - The first name of the person.
  - `lastName: String` - The last name of the person.
  - `age: Integer` - The age of the person.

# Relationships

### Ownership Association
- **Between**: `Person` and `Account`
- **Multiplicity**: 
  - `Person` can own one or more (`1..*`) `Account`.
  - Each `Account` is owned by exactly one (`1`) `Person`.

### Use Association
- **Between**: `Person` and `Account`
- **Multiplicity**: 
  - A `Person` can use zero or more (`0..*`) `Account`.
  - An `Account` can be used by zero or more (`0..*`) `Person`.

### AccountOfBanks Composition
- **Between**: `Bank` and `Account`
- **Multiplicity**: 
  - A `Bank` manages one or more (`1..*`) `Account`.
  - Each `Account` is associated with exactly one (`1`) `Bank`.

# Invariants

### AdultOwners
- **Context**: Account
- **Constraint**: The owner of an account must be older than 18 years. This ensures that accounts are only owned by adults.

### PositiveBalance
- **Context**: Account
- **Constraint**: The balance of an account must always be positive. This prevents accounts from being overdrawn.

# Category: Baseline Instances
## Instance 1: Standard Urban Banking Scenario

### Bank
- **Attributes**:
  - `country: "United States"`
  - `name: "CityBank"`
  - `bic: "CITIUS33"`

### Account
1. **Attributes**:
   - `iban: "US12345678901234567890"`
   - `balance: 1500`
   
2. **Attributes**:
   - `iban: "US09876543210987654321"`
   - `balance: 2300`

### Person
1. **Attributes**:
   - `firstName: "John"`
   - `lastName: "Doe"`
   - `age: 45`

2. **Attributes**:
   - `firstName: "Sarah"`
   - `lastName: "Miller"`
   - `age: 30`

### Relationships
- **Ownership Association**:
  - `John Doe` owns the account with `IBAN: US12345678901234567890`.
  - `Sarah Miller` owns the account with `IBAN: US09876543210987654321`.
  
- **Use Association**:
  - `John Doe` uses the account with `IBAN: US12345678901234567890`.
  - `Sarah Miller` uses the account with `IBAN: US09876543210987654321`.
  
- **AccountOfBanks Composition**:
  - `CityBank` manages both accounts (`IBAN: US12345678901234567890` and `IBAN: US09876543210987654321`).

## Instance 2: European Rural Banking Scenario

### Bank
- **Attributes**:
  - `country: "Germany"`
  - `name: "RuralBank"`
  - `bic: "RURALDEFF"`

### Account
1. **Attributes**:
   - `iban: "DE44500105175407324931"`
   - `balance: 3050`

### Person
- **Attributes**:
  - `firstName: "Hans"`
  - `lastName: "Schmidt"`
  - `age: 52`

### Relationships
- **Ownership Association**:
  - `Hans Schmidt` owns the account with `IBAN: DE44500105175407324931`.

- **Use Association**:
  - `Hans Schmidt` uses the account with `IBAN: DE44500105175407324931`.

- **AccountOfBanks Composition**:
  - `RuralBank` manages the account (`IBAN: DE44500105175407324931`).

## Instance 3: Asian Metropolitan Banking Scenario

### Bank
- **Attributes**:
  - `country: "Japan"`
  - `name: "TokyoBank"`
  - `bic: "TOKYJPJT"`

### Account
1. **Attributes**:
   - `iban: "JP87654321098765432109"`
   - `balance: 50000`
   
2. **Attributes**:
   - `iban: "JP12345678909876543210"`
   - `balance: 10000`

### Person
1. **Attributes**:
   - `firstName: "Aiko"`
   - `lastName: "Tanaka"`
   - `age: 28`

2. **Attributes**:
   - `firstName: "Kenji"`
   - `lastName: "Ito"`
   - `age: 33`

### Relationships
- **Ownership Association**:
  - `Aiko Tanaka` owns the account with `IBAN: JP87654321098765432109`.
  - `Kenji Ito` owns the account with `IBAN: JP12345678909876543210`.

- **Use Association**:
  - `Aiko Tanaka` uses both accounts (`IBAN: JP87654321098765432109` and `IBAN: JP12345678909876543210`).
  - `Kenji Ito` uses the account with `IBAN: JP12345678909876543210`.

- **AccountOfBanks Composition**:
  - `TokyoBank` manages both accounts (`IBAN: JP87654321098765432109` and `IBAN: JP12345678909876543210`).

These instances cover different international settings and demonstrate the diversity in banking scenarios while adhering to the constraints and relationships defined in the model.
```
!new Bank('bank3')
!bank3.country := 'United States'
!bank3.name := 'CityBank'
!bank3.bic := 'CITIUS33'

!new Account('account3')
!account3.iban := 'US12345678901234567890'
!account3.balance := 1500

!new Account('account4')
!account4.iban := 'US09876543210987654321'
!account4.balance := 2300

!new Person('person4')
!person4.age := 45
!person4.firstName := 'John'
!person4.lastName := 'Doe'

!new Person('person5')
!person5.age := 30
!person5.firstName := 'Sarah'
!person5.lastName := 'Miller'

!insert (person4, account3) into Ownership
!insert (person5, account4) into Ownership
!insert (person4, account3) into Use
!insert (person5, account4) into Use
!insert (bank3, account3) into AccountOfBanks
!insert (bank3, account4) into AccountOfBanks

!new Bank('bank4')
!bank4.country := 'Germany'
!bank4.name := 'RuralBank'
!bank4.bic := 'RURALDEFF'

!new Account('account5')
!account5.iban := 'DE44500105175407324931'
!account5.balance := 3050

!new Person('person6')
!person6.age := 52
!person6.firstName := 'Hans'
!person6.lastName := 'Schmidt'

!insert (person6, account5) into Ownership
!insert (person6, account5) into Use
!insert (bank4, account5) into AccountOfBanks

!new Bank('bank5')
!bank5.country := 'Japan'
!bank5.name := 'TokyoBank'
!bank5.bic := 'TOKYJPJT'

!new Account('account6')
!account6.iban := 'JP87654321098765432109'
!account6.balance := 50000

!new Account('account7')
!account7.iban := 'JP12345678909876543210'
!account7.balance := 10000

!new Person('person7')
!person7.age := 28
!person7.firstName := 'Aiko'
!person7.lastName := 'Tanaka'

!new Person('person8')
!person8.age := 33
!person8.firstName := 'Kenji'
!person8.lastName := 'Ito'

!insert (person7, account6) into Ownership
!insert (person8, account7) into Ownership
!insert (person7, account6) into Use
!insert (person7, account7) into Use
!insert (person8, account7) into Use
!insert (bank5, account6) into AccountOfBanks
!insert (bank5, account7) into AccountOfBanks
```

# Category: Boundary Instances
## Instance 1: Minimum Multiplicity for Ownership and AccountOfBanks

### Bank
- **Attributes**:
  - `country: "Canada"`
  - `name: "MapleBank"`
  - `bic: "MAPLCA22"`

### Account
1. **Attributes**:
   - `iban: "CA00000000000000000001"`
   - `balance: 1`  (Minimum positive balance)

### Person
- **Attributes**:
  - `firstName: "Alice"`
  - `lastName: "Brown"`
  - `age: 19`  (Minimum adult age)

### Relationships
- **Ownership Association**:
  - `Alice Brown` owns the account with `IBAN: CA00000000000000000001`.

- **Use Association**:
  - `Alice Brown` uses the account with `IBAN: CA00000000000000000001`.

- **AccountOfBanks Composition**:
  - `MapleBank` manages the account (`IBAN: CA00000000000000000001`).

## Instance 2: Maximum Multiplicity for Ownership and Use

### Bank
- **Attributes**:
  - `country: "Australia"`
  - `name: "KoalaBank"`
  - `bic: "KOALAU33"`

### Account
1. **Attributes**:
   - `iban: "AU11111111111111111111"`
   - `balance: 5000`

2. **Attributes**:
   - `iban: "AU22222222222222222222"`
   - `balance: 10000`

### Person
- **Attributes**:
  - `firstName: "Liam"`
  - `lastName: "Johnson"`
  - `age: 35`

### Relationships
- **Ownership Association**:
  - `Liam Johnson` owns both accounts (`IBAN: AU11111111111111111111` and `IBAN: AU22222222222222222222`).

- **Use Association**:
  - `Liam Johnson` uses both accounts (`IBAN: AU11111111111111111111` and `IBAN: AU22222222222222222222`).

- **AccountOfBanks Composition**:
  - `KoalaBank` manages both accounts (`IBAN: AU11111111111111111111` and `IBAN: AU22222222222222222222`).

## Instance 3: Use Association with Empty Collection

### Bank
- **Attributes**:
  - `country: "India"`
  - `name: "LotusBank"`
  - `bic: "LOTIINBB"`

### Account
1. **Attributes**:
   - `iban: "IN33333333333333333333"`
   - `balance: 2000`

### Person
- **Attributes**:
  - `firstName: "Ravi"`
  - `lastName: "Patel"`
  - `age: 45`

### Relationships
- **Ownership Association**:
  - `Ravi Patel` owns the account with `IBAN: IN33333333333333333333`.

- **Use Association**:
  - There are no additional users for the account (`IBAN: IN33333333333333333333`), demonstrating an empty collection scenario.

- **AccountOfBanks Composition**:
  - `LotusBank` manages the account (`IBAN: IN33333333333333333333`).

## Instance 4: Extreme Values for Numeric Constraints

### Bank
- **Attributes**:
  - `country: "Brazil"`
  - `name: "AmazonBank"`
  - `bic: "AMZNBRAA"`

### Account
1. **Attributes**:
   - `iban: "BR44444444444444444444"`
   - `balance: 1000000000`  (A very large balance value)

### Person
- **Attributes**:
  - `firstName: "Carlos"`
  - `lastName: "Silva"`
  - `age: 100`  (An extreme age value)

### Relationships
- **Ownership Association**:
  - `Carlos Silva` owns the account with `IBAN: BR44444444444444444444`.

- **Use Association**:
  - `Carlos Silva` uses the account with `IBAN: BR44444444444444444444`.

- **AccountOfBanks Composition**:
  - `AmazonBank` manages the account (`IBAN: BR44444444444444444444`).

These instances test the boundaries of the given model, exploring multiplicities, optional relationships, and extreme values for numeric attributes, while adhering to the constraints.
```
!new Bank('bank6')
!bank6.country := 'Canada'
!bank6.name := 'MapleBank'
!bank6.bic := 'MAPLCA22'

!new Account('account8')
!account8.iban := 'CA00000000000000000001'
!account8.balance := 1

!new Person('person9')
!person9.age := 19
!person9.firstName := 'Alice'
!person9.lastName := 'Brown'

!insert (person9, account8) into Ownership
!insert (person9, account8) into Use
!insert (bank6, account8) into AccountOfBanks

!new Bank('bank7')
!bank7.country := 'Australia'
!bank7.name := 'KoalaBank'
!bank7.bic := 'KOALAU33'

!new Account('account9')
!account9.iban := 'AU11111111111111111111'
!account9.balance := 5000

!new Account('account10')
!account10.iban := 'AU22222222222222222222'
!account10.balance := 10000

!new Person('person10')
!person10.age := 35
!person10.firstName := 'Liam'
!person10.lastName := 'Johnson'

!insert (person10, account9) into Ownership
!insert (person10, account10) into Ownership
!insert (person10, account9) into Use
!insert (person10, account10) into Use
!insert (bank7, account9) into AccountOfBanks
!insert (bank7, account10) into AccountOfBanks

!new Bank('bank8')
!bank8.country := 'India'
!bank8.name := 'LotusBank'
!bank8.bic := 'LOTIINBB'

!new Account('account11')
!account11.iban := 'IN33333333333333333333'
!account11.balance := 2000

!new Person('person11')
!person11.age := 45
!person11.firstName := 'Ravi'
!person11.lastName := 'Patel'

!insert (person11, account11) into Ownership
!insert (bank8, account11) into AccountOfBanks

!new Bank('bank9')
!bank9.country := 'Brazil'
!bank9.name := 'AmazonBank'
!bank9.bic := 'AMZNBRAA'

!new Account('account12')
!account12.iban := 'BR44444444444444444444'
!account12.balance := 1000000000

!new Person('person12')
!person12.age := 100
!person12.firstName := 'Carlos'
!person12.lastName := 'Silva'

!insert (person12, account12) into Ownership
!insert (person12, account12) into Use
!insert (bank9, account12) into AccountOfBanks
```

# Category: Complex Instances
## Instance 1: Multi-Bank, Multi-Account, Multi-Person Scenario

### Banks
1. **Attributes**:
   - `country: "United Kingdom"`
   - `name: "RoyalBank"`
   - `bic: "ROYBGB22"`
   
2. **Attributes**:
   - `country: "France"`
   - `name: "ParisBank"`
   - `bic: "PARIFRPP"`

### Accounts
1. **Attributes**:
   - `iban: "GB99ROYB12345678901234"`
   - `balance: 2500`
   
2. **Attributes**:
   - `iban: "GB88ROYB98765432109876"`
   - `balance: 5000`
   
3. **Attributes**:
   - `iban: "FR76PARI12345678901234"`
   - `balance: 1500`
   
4. **Attributes**:
   - `iban: "FR45PARI98765432109876"`
   - `balance: 3000`

### Persons
1. **Attributes**:
   - `firstName: "Emma"`
   - `lastName: "Thompson"`
   - `age: 29`
   
2. **Attributes**:
   - `firstName: "Lucas"`
   - `lastName: "Durand"`
   - `age: 42`
   
3. **Attributes**:
   - `firstName: "Sophie"`
   - `lastName: "Martin"`
   - `age: 35`

### Relationships
- **Ownership Association**:
  - `Emma Thompson` owns `IBAN: GB99ROYB12345678901234` and `IBAN: FR76PARI12345678901234`.
  - `Lucas Durand` owns `IBAN: GB88ROYB98765432109876`.
  - `Sophie Martin` owns `IBAN: FR45PARI98765432109876`.

- **Use Association**:
  - `Emma Thompson` uses `IBAN: GB99ROYB12345678901234` and `IBAN: FR76PARI12345678901234`.
  - `Lucas Durand` uses `IBAN: GB88ROYB98765432109876` and also has access to `IBAN: FR76PARI12345678901234`.
  - `Sophie Martin` uses `IBAN: FR45PARI98765432109876` and occasionally uses `IBAN: GB88ROYB98765432109876`.

- **AccountOfBanks Composition**:
  - `RoyalBank` manages `IBAN: GB99ROYB12345678901234` and `IBAN: GB88ROYB98765432109876`.
  - `ParisBank` manages `IBAN: FR76PARI12345678901234` and `IBAN: FR45PARI98765432109876`.

## Instance 2: Intercontinental Bank with Shared Users

### Bank
- **Attributes**:
  - `country: "Singapore"`
  - `name: "GlobalBank"`
  - `bic: "GLBSGSGX"`

### Accounts
1. **Attributes**:
   - `iban: "SG12GLB12345678901234"`
   - `balance: 10000`
   
2. **Attributes**:
   - `iban: "SG98GLB98765432109876"`
   - `balance: 7000`

3. **Attributes**:
   - `iban: "SG34GLB11223344556677"`
   - `balance: 12000`

### Persons
1. **Attributes**:
   - `firstName: "Ananya"`
   - `lastName: "Chowdhury"`
   - `age: 39`

2. **Attributes**:
   - `firstName: "Jin"`
   - `lastName: "Lee"`
   - `age: 50`

### Relationships
- **Ownership Association**:
  - `Ananya Chowdhury` owns `IBAN: SG12GLB12345678901234` and `IBAN: SG98GLB98765432109876`.
  - `Jin Lee` owns `IBAN: SG34GLB11223344556677`.

- **Use Association**:
  - `Ananya Chowdhury` uses all three accounts (`IBAN: SG12GLB12345678901234`, `IBAN: SG98GLB98765432109876`, `IBAN: SG34GLB11223344556677`).
  - `Jin Lee` also uses `IBAN: SG12GLB12345678901234` for international transactions.

- **AccountOfBanks Composition**:
  - `GlobalBank` manages all three accounts (`IBAN: SG12GLB12345678901234`, `IBAN: SG98GLB98765432109876`, `IBAN: SG34GLB11223344556677`).

These instances demonstrate complex interactions involving multiple banks, accounts, and people, with varied use and ownership relationships across different countries, testing the model’s capacity to handle intricate, real-world scenarios.
```
!new Bank('bank10')
!bank10.country := 'United Kingdom'
!bank10.name := 'RoyalBank'
!bank10.bic := 'ROYBGB22'

!new Bank('bank11')
!bank11.country := 'France'
!bank11.name := 'ParisBank'
!bank11.bic := 'PARIFRPP'

!new Account('account13')
!account13.iban := 'GB99ROYB12345678901234'
!account13.balance := 2500

!new Account('account14')
!account14.iban := 'GB88ROYB98765432109876'
!account14.balance := 5000

!new Account('account15')
!account15.iban := 'FR76PARI12345678901234'
!account15.balance := 1500

!new Account('account16')
!account16.iban := 'FR45PARI98765432109876'
!account16.balance := 3000

!new Person('person13')
!person13.age := 29
!person13.firstName := 'Emma'
!person13.lastName := 'Thompson'

!new Person('person14')
!person14.age := 42
!person14.firstName := 'Lucas'
!person14.lastName := 'Durand'

!new Person('person15')
!person15.age := 35
!person15.firstName := 'Sophie'
!person15.lastName := 'Martin'

!insert (person13, account13) into Ownership
!insert (person13, account15) into Ownership
!insert (person14, account14) into Ownership
!insert (person15, account16) into Ownership
!insert (person13, account13) into Use
!insert (person13, account15) into Use
!insert (person14, account14) into Use
!insert (person14, account15) into Use
!insert (person15, account16) into Use
!insert (person15, account14) into Use
!insert (bank10, account13) into AccountOfBanks
!insert (bank10, account14) into AccountOfBanks
!insert (bank11, account15) into AccountOfBanks
!insert (bank11, account16) into AccountOfBanks

!new Bank('bank12')
!bank12.country := 'Singapore'
!bank12.name := 'GlobalBank'
!bank12.bic := 'GLBSGSGX'

!new Account('account17')
!account17.iban := 'SG12GLB12345678901234'
!account17.balance := 10000

!new Account('account18')
!account18.iban := 'SG98GLB98765432109876'
!account18.balance := 7000

!new Account('account19')
!account19.iban := 'SG34GLB11223344556677'
!account19.balance := 12000

!new Person('person16')
!person16.age := 39
!person16.firstName := 'Ananya'
!person16.lastName := 'Chowdhury'

!new Person('person17')
!person17.age := 50
!person17.firstName := 'Jin'
!person17.lastName := 'Lee'

!insert (person16, account17) into Ownership
!insert (person16, account18) into Ownership
!insert (person17, account19) into Ownership
!insert (person16, account17) into Use
!insert (person16, account18) into Use
!insert (person16, account19) into Use
!insert (person17, account17) into Use
!insert (bank12, account17) into AccountOfBanks
!insert (bank12, account18) into AccountOfBanks
!insert (bank12, account19) into AccountOfBanks
```

# Category: Unrealistc but valid
## Instance 1: One Person Owns and Uses All Bank Accounts

### Banks
1. **Attributes**:
   - `country: "Netherlands"`
   - `name: "TulipBank"`
   - `bic: "TULPNL22"`

2. **Attributes**:
   - `country: "Switzerland"`
   - `name: "AlpineBank"`
   - `bic: "ALPICHZZ"`

3. **Attributes**:
   - `country: "New Zealand"`
   - `name: "KiwiBank"`
   - `bic: "KIWINZ22"`

### Accounts
1. **Attributes**:
   - `iban: "NL11TULP000000000001"`
   - `balance: 500`

2. **Attributes**:
   - `iban: "CH22ALPI000000000002"`
   - `balance: 1000`

3. **Attributes**:
   - `iban: "NZ33KIWI000000000003"`
   - `balance: 1500`

### Person
- **Attributes**:
  - `firstName: "Zara"`
  - `lastName: "Smith"`
  - `age: 40`

### Relationships
- **Ownership Association**:
  - `Zara Smith` owns all accounts (`IBAN: NL11TULP000000000001`, `IBAN: CH22ALPI000000000002`, `IBAN: NZ33KIWI000000000003`).

- **Use Association**:
  - `Zara Smith` uses all accounts (`IBAN: NL11TULP000000000001`, `IBAN: CH22ALPI000000000002`, `IBAN: NZ33KIWI000000000003`).

- **AccountOfBanks Composition**:
  - `TulipBank` manages the account (`IBAN: NL11TULP000000000001`).
  - `AlpineBank` manages the account (`IBAN: CH22ALPI000000000002`).
  - `KiwiBank` manages the account (`IBAN: NZ33KIWI000000000003`).

## Instance 2: One Bank Manages Accounts for People in Different Continents

### Bank
- **Attributes**:
  - `country: "South Africa"`
  - `name: "SafariBank"`
  - `bic: "SAFRZAJJ"`

### Accounts
1. **Attributes**:
   - `iban: "ZA44SAFR000000000011"`
   - `balance: 2000`

2. **Attributes**:
   - `iban: "ZA55SAFR000000000022"`
   - `balance: 4000`

### Persons
1. **Attributes**:
   - `firstName: "Liam"`
   - `lastName: "Nguyen"`
   - `age: 30`

2. **Attributes**:
   - `firstName: "Sophia"`
   - `lastName: "Garcia"`
   - `age: 45`

### Relationships
- **Ownership Association**:
  - `Liam Nguyen` owns `IBAN: ZA44SAFR000000000011`.
  - `Sophia Garcia` owns `IBAN: ZA55SAFR000000000022`.

- **Use Association**:
  - `Liam Nguyen` uses `IBAN: ZA44SAFR000000000011`.
  - `Sophia Garcia` uses `IBAN: ZA55SAFR000000000022`.

- **AccountOfBanks Composition**:
  - `SafariBank` manages both accounts (`IBAN: ZA44SAFR000000000011`, `IBAN: ZA55SAFR000000000022`) despite the owners living on different continents (Asia and South America).

## Instance 3: Multiple Banks with the Same Person Using Accounts

### Banks
1. **Attributes**:
   - `country: "Italy"`
   - `name: "VinoBank"`
   - `bic: "VINOITRR"`

2. **Attributes**:
   - `country: "India"`
   - `name: "LotusBank"`
   - `bic: "LOTIINBB"`

### Accounts
1. **Attributes**:
   - `iban: "IT12VINO000000000099"`
   - `balance: 3500`

2. **Attributes**:
   - `iban: "IN21LOTI000000000088"`
   - `balance: 4500`

### Person
- **Attributes**:
  - `firstName: "Aria"`
  - `lastName: "Jones"`
  - `age: 55`

### Relationships
- **Ownership Association**:
  - `Aria Jones` owns both accounts (`IBAN: IT12VINO000000000099` and `IBAN: IN21LOTI000000000088`).

- **Use Association**:
  - `Aria Jones` uses both accounts (`IBAN: IT12VINO000000000099` and `IBAN: IN21LOTI000000000088`).

- **AccountOfBanks Composition**:
  - `VinoBank` manages the account (`IBAN: IT12VINO000000000099`).
  - `LotusBank` manages the account (`IBAN: IN21LOTI000000000088`).

These instances illustrate unlikely yet valid scenarios, such as one person owning all accounts across multiple banks, a single bank managing accounts for globally dispersed individuals, and a person using accounts managed by different banks in different countries.
```
!new Bank('bank13')
!bank13.country := 'Netherlands'
!bank13.name := 'TulipBank'
!bank13.bic := 'TULPNL22'

!new Bank('bank14')
!bank14.country := 'Switzerland'
!bank14.name := 'AlpineBank'
!bank14.bic := 'ALPICHZZ'

!new Bank('bank15')
!bank15.country := 'New Zealand'
!bank15.name := 'KiwiBank'
!bank15.bic := 'KIWINZ22'

!new Account('account20')
!account20.iban := 'NL11TULP000000000001'
!account20.balance := 500

!new Account('account21')
!account21.iban := 'CH22ALPI000000000002'
!account21.balance := 1000

!new Account('account22')
!account22.iban := 'NZ33KIWI000000000003'
!account22.balance := 1500

!new Person('person18')
!person18.age := 40
!person18.firstName := 'Zara'
!person18.lastName := 'Smith'

!insert (person18, account20) into Ownership
!insert (person18, account21) into Ownership
!insert (person18, account22) into Ownership
!insert (person18, account20) into Use
!insert (person18, account21) into Use
!insert (person18, account22) into Use
!insert (bank13, account20) into AccountOfBanks
!insert (bank14, account21) into AccountOfBanks
!insert (bank15, account22) into AccountOfBanks

!new Bank('bank16')
!bank16.country := 'South Africa'
!bank16.name := 'SafariBank'
!bank16.bic := 'SAFRZAJJ'

!new Account('account23')
!account23.iban := 'ZA44SAFR000000000011'
!account23.balance := 2000

!new Account('account24')
!account24.iban := 'ZA55SAFR000000000022'
!account24.balance := 4000

!new Person('person19')
!person19.age := 30
!person19.firstName := 'Liam'
!person19.lastName := 'Nguyen'

!new Person('person20')
!person20.age := 45
!person20.firstName := 'Sophia'
!person20.lastName := 'Garcia'

!insert (person19, account23) into Ownership
!insert (person20, account24) into Ownership
!insert (person19, account23) into Use
!insert (person20, account24) into Use
!insert (bank16, account23) into AccountOfBanks
!insert (bank16, account24) into AccountOfBanks

!new Bank('bank17')
!bank17.country := 'Italy'
!bank17.name := 'VinoBank'
!bank17.bic := 'VINOITRR'

!new Bank('bank18')
!bank18.country := 'India'
!bank18.name := 'LotusBank'
!bank18.bic := 'LOTIINBB'

!new Account('account25')
!account25.iban := 'IT12VINO000000000099'
!account25.balance := 3500

!new Account('account26')
!account26.iban := 'IN21LOTI000000000088'
!account26.balance := 4500

!new Person('person21')
!person21.age := 55
!person21.firstName := 'Aria'
!person21.lastName := 'Jones'

!insert (person21, account25) into Ownership
!insert (person21, account26) into Ownership
!insert (person21, account25) into Use
!insert (person21, account26) into Use
!insert (bank17, account25) into AccountOfBanks
!insert (bank18, account26) into AccountOfBanks
```

# Category: Realistic but invalid
## Instance 1: Account Owned by a Minor

### Bank
- **Attributes**:
  - `country: "United States"`
  - `name: "FamilyBank"`
  - `bic: "FAMBUS33"`

### Account
- **Attributes**:
  - `iban: "US12FAMB000000000001"`
  - `balance: 1000`

### Person
- **Attributes**:
  - `firstName: "Tommy"`
  - `lastName: "Anderson"`
  - `age: 17`  (Underage owner)

### Relationships
- **Ownership Association**:
  - `Tommy Anderson` owns the account with `IBAN: US12FAMB000000000001`.

- **Use Association**:
  - `Tommy Anderson` uses the account with `IBAN: US12FAMB000000000001`.

- **AccountOfBanks Composition**:
  - `FamilyBank` manages the account (`IBAN: US12FAMB000000000001`).

**Violation**: This instance violates the `AdultOwners` constraint, as the account is owned by a minor (17 years old). In real life, minors often have accounts managed by guardians or through joint accounts.

## Instance 2: Overdrawn Account

### Bank
- **Attributes**:
  - `country: "Canada"`
  - `name: "NorthernBank"`
  - `bic: "NRTNCA33"`

### Account
- **Attributes**:
  - `iban: "CA23NRTN000000000001"`
  - `balance: -500`  (Negative balance)

### Person
- **Attributes**:
  - `firstName: "Lisa"`
  - `lastName: "Wong"`
  - `age: 28`

### Relationships
- **Ownership Association**:
  - `Lisa Wong` owns the account with `IBAN: CA23NRTN000000000001`.

- **Use Association**:
  - `Lisa Wong` uses the account with `IBAN: CA23NRTN000000000001`.

- **AccountOfBanks Composition**:
  - `NorthernBank` manages the account (`IBAN: CA23NRTN000000000001`).

**Violation**: This instance violates the `PositiveBalance` constraint. In reality, accounts can be overdrawn, particularly those with overdraft facilities.

## Instance 3: Account Without an Owner

### Bank
- **Attributes**:
  - `country: "Australia"`
  - `name: "OutbackBank"`
  - `bic: "OUTBAU33"`

### Account
- **Attributes**:
  - `iban: "AU34OUTB000000000001"`
  - `balance: 2500`

### Person
- **Attributes**:
  - `firstName: "James"`
  - `lastName: "Taylor"`
  - `age: 50`

### Relationships
- **Ownership Association**:
  - No person owns the account with `IBAN: AU34OUTB000000000001`.

- **Use Association**:
  - `James Taylor` uses the account with `IBAN: AU34OUTB000000000001`.

- **AccountOfBanks Composition**:
  - `OutbackBank` manages the account (`IBAN: AU34OUTB000000000001`).

**Violation**: This instance violates the `Ownership Association` multiplicity constraint (1..*), as the account lacks an owner. In real-life scenarios, temporary or transitional states might exist where an account has no immediate owner due to administrative processes.

These instances highlight realistic scenarios that violate the given model's constraints, such as minors owning accounts, negative balances, and accounts temporarily without owners, which are plausible in real-world banking contexts.
```
!new Bank('bank19')
!bank19.country := 'United States'
!bank19.name := 'FamilyBank'
!bank19.bic := 'FAMBUS33'

!new Account('account27')
!account27.iban := 'US12FAMB000000000001'
!account27.balance := 1000

!new Person('person22')
!person22.age := 17
!person22.firstName := 'Tommy'
!person22.lastName := 'Anderson'

!insert (person22, account27) into Ownership
!insert (person22, account27) into Use
!insert (bank19, account27) into AccountOfBanks

!new Bank('bank20')
!bank20.country := 'Canada'
!bank20.name := 'NorthernBank'
!bank20.bic := 'NRTNCA33'

!new Account('account28')
!account28.iban := 'CA23NRTN000000000001'
!account28.balance := -500

!new Person('person23')
!person23.age := 28
!person23.firstName := 'Lisa'
!person23.lastName := 'Wong'

!insert (person23, account28) into Ownership
!insert (person23, account28) into Use
!insert (bank20, account28) into AccountOfBanks

!new Bank('bank21')
!bank21.country := 'Australia'
!bank21.name := 'OutbackBank'
!bank21.bic := 'OUTBAU33'

!new Account('account29')
!account29.iban := 'AU34OUTB000000000001'
!account29.balance := 2500

!new Person('person24')
!person24.age := 50
!person24.firstName := 'James'
!person24.lastName := 'Taylor'

!insert (person24, account29) into Use
!insert (bank21, account29) into AccountOfBanks
```