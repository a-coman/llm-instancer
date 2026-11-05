# Comprehensive realism analysis

**Judge** — Gemini R/U labels in [`List.txt`](src/main/java/es/uma/Metrics/List.txt). Primary automated realism verdict.

**Formal constraints** — Syntax, multiplicities, and invariant checks from [`General`](src/main/java/es/uma/Metrics/General.java) on `src/main/resources/prompts/` diagrams. Invariants are a **partial** formal reference (not all semantic unrealism is encoded).

**Human review** — [`human_eval.xlsx`](src/main/java/es/uma/Metrics/JudgeAnalysis/human_eval.xlsx), one table row per judge statement line.

**Column codes** (R = realistic / agrees, U = unrealistic / problem exists):
- `judge` — Gemini verdict from [`List.txt`](src/main/java/es/uma/Metrics/List.txt).
- `inv` — invariant check: fail → `U`, pass → `R`.
- `human` — problem exists Yes → `U`, No → `R`, Partial → `Partial`; Specific traceable Yes → `R`, No → `NotTraceable`; Generic valid Yes → `R`, No → `NotJustified`, Unknown → `Unknown`.
- `human_comments` — column D when problem exists is No or Partial.
- `inv_detail` — failed invariant names when `inv` is `U`.

**Reading disagreements:**
- **R + invariant failure** → judge too lenient (`JUDGE_LENIENT`).
- **U + invariant failure + human Yes** → strong consensus (`AGREE_U_FORMAL`).
- **U + invariant pass + human Yes** → semantic unrealism without formal coverage (`SEMANTIC_ONLY`).
- **U + human No** → human disputes the judge (`HUMAN_REJECTS_U`), even if invariants fail.

## Summary

| Metric | Value |
|---|---|
| Batch instances analyzed | 100 |
| Judge labels in List.txt | 100 |
| Judge: Realistic (R) | 50 |
| Judge: Unrealistic (U) | 50 |
| Instances with invariant failures | 45 |
| Judge errors (R but invariants fail) | 6 |
| Agreement (judge matches invariants) | 83 |

### Judge × human (U instances only, n=50)

| Metric | Value |
|---|---|
| Human confirms problem (Yes) | 36 |
| Human partial (Partial) | 4 |
| Human rejects judge (No) | 10 |
| Human confirms rate (Yes + Partial) | 80.00% (40/50) |

### Invariants × human (U instances only)

|  | Human Yes | Human No |
|---|---|---|
| Invariant failures | 30 | 6 |
| No invariant failures | 6 | 4 |

### R explanation audit (R instances only)

| Metric | Value |
|---|---|
| R with non-traceable specific statement | 0 |
| R with invalid generic statement | 1 |

## Cross-table (judge × invariants)

|  | Invariant failures | No invariant failures |
|---|---|---|
| **Judge: Unrealistic (U)** | Agreement: judge U + formal support (39) | Judge U, invariants pass (11) |
| **Judge: Realistic (R)** | Judge error: said R, invariants fail (6) | Agreement: judge R + formally OK (44) |

## Judge quality metrics (judge × invariants)

| Metric | Value | Meaning |
|---|---|---|
| Judge error rate on R labels | 12.00% (6/50) | Said realistic but invariants failed |
| Judge U with formal support | 78.00% (39/50) | Said unrealistic and invariants agree |
| Judge U among invariant failures | 86.67% (39/45) | Of formally failing instances, how many judge marked U |
| Formal agreement rate | 83.00% (83/100) | Judge and invariants same sign |

## Three-way alignment counts

| Alignment | Count | Meaning |
|---|---|---|
| `AGREE_R` | 44 | R, invariants pass |
| `JUDGE_LENIENT` | 6 | R, invariants fail |
| `AGREE_U_FORMAL` | 30 | U, human Yes, invariants fail |
| `SEMANTIC_ONLY` | 6 | U, human Yes, invariants pass |
| `HUMAN_REJECTS_U` | 10 | U, human No |
| `HUMAN_PARTIAL` | 4 | U, human Partial |

## Per-statement table

| instanceId | statement | judge | inv | human | human_comments | inv_detail |
|---|---|---|---|---|---|---|
| gpt_5_2 / cot / vehiclerental / gen6 / baseline | R: The object model is semantically consistent and highly plausible. The attribute values (such as distances, gas tank capacity, and pricing) are common-sense and appropriate for a vehicle rental s... | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen6 / baseline | The scenario logically models a standard rental use case, including a realistic one-way 6-day truck rental from Connecticut to Colorado, which realistically returns the vehicle to its home office. | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | R: The object model is semantically consistent and highly detailed. Attributes like addresses, states, and vehicle types align perfectly | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | (e.g., a Florida office managing a Florida-registered truck). | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | The relationships describe logical real-world rental scenarios, | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | such as a 4-day one-way truck rental from Miami to Denver, | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | and a local 2-day flat-rate trailer rental (with a $0.00 rate per mile, | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | which is accurate for trailers since they lack odometers). | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | Values for truck gas tank capacity (26.5), mileage/MPG (17), | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen1 / baseline | and daily rates are standard and plausible. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen5 | R: The object model represents a plausible real-world scenario. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen5 | The rentals involve realistic combinations of cassettes | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen5 | (e.g., sequentially borrowing multiple episodes of the "Northern Lights" series). | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen5 | It is logically sound that a client like 'cz3' who has an outstanding rental with multiple cassettes might have an 'isSuspended' status set to true due to unreturned items. | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen1 / boundary | R: The object model is logically consistent and adheres to the domain model's constraints. The attributes reflect plausible real-world values (e.g., table capacities match the number of people in t... | R | R |  |  |  |
| gpt_5_2 / cot / restaurant / gen1 / boundary | Using a placeholder phone number | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen1 / boundary | and a 0.0 prep time for a "zero-prep demo" item are both common, valid scenarios in a real system. | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen7 | R: The object model defines two entirely plausible state machines: | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen7 | a clinic appointment flow and a continuous integration (CI) pipeline. | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen7 | The states are logically named and follow standard real-world lifecycles. | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen7 | The transitions connect appropriate states, and the time values assigned to events are logically consistent | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen7 | (e.g., 1440 minutes/1 day from scheduled to check-in, 15 minutes in the waiting room, standard CI build/test/deploy steps). | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen7 | All domain constraints are satisfied. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen3 | R: The object model provides a logically consistent and temporally valid set of business entities. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen3 | Attributes like gas tank capacity, odometer readings, and rental rates are well within plausible real-world boundaries. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen3 | The rental agreements follow a clear chronological sequence without overlapping a single vehicle's usage across physically distant rental periods. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen5 / complex | R: The object model is logically consistent with the domain model and represents a plausible real-world scenario for a video club. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen5 / complex | Client IDs, rental dates, and episode numbers are valid. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen5 / complex | Multiplicities and constraints are fully respected. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen5 / complex | Attributes like available copies and overlapping actors in different episodes correctly mirror real-world behaviors. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen5 / complex | The presence of a suspended client with a past rental is also a valid business scenario (e.g., suspended due to unreturned items). | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen5 / complex | R: The object model accurately represents a real-world clinical MRI workflow. | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen5 / complex | The states logically progress through the procedural phases | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen5 / complex | (e.g., registration, screening, preparation, sequencing of scans, contrast injection, and disinfection). | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen5 / complex | Alternate paths handling real-life exceptions, such as metal implant issues, motion artifacts, or allergic reactions, are logically routed. | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen5 / complex | The time events mapped to each transition represent highly plausible durations (in minutes) for these medical tasks. | R | R | Unknown |  |  |
| gpt_5_2 / cot / statemachine / gen5 / complex | All UML constraints are satisfied. | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen4 / boundary | U: The state 'st_carn_concentracao' has five different outgoing transitions (towards 'setorA', 'setorB', 'setorC', and 'apuracao'), and all of them are triggered by a `TimeEvent` with the exact sam... | U | U | R | The state machine is not about a technical system, it is a socio system which contains non-determinism. | State::NotEmptyState |
| gpt_5_2 / cot / statemachine / gen4 / boundary | U: Additionally, the state 'st_carn_vipLounge' is completely isolated with no incoming or outgoing transitions. | U | U | U |  | State::NotEmptyState |
| gpt_5_2 / cot / restaurant / gen5 / boundary | U: The objects 'K55' and 'K56' of class 'Cook' are instantiated as two distinct objects but share the exact same name ('Pavel Novák'), date of birth ('1993-03-03'), and personal phone number ('+420... | U | U | U |  | Banquet::RealisticBanquet; Worker::UniqueWorkerIdentity |
| gpt_5_2 / simple / videoclub / gen12 | R: The client, actor, and cassette properties are completely plausible and consistent with real-world entities. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen12 | The scenario where a suspended client ('vb') retains a rental ('vxR1') of series episodes with 0 available copies perfectly reflects common real-world logic for a video club snapshot. | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen5 / boundary | U: The account 'a103' has a balance of exactly 2147483647, which represents the maximum value of a 32-bit signed integer. While syntactically valid, this is a recognizable software testing artifact... | U | R | R | While the number is a special numbers for 32-bit signed integers, it is a realistic values for banks in general. Stil... |  |
| gpt_5_2 / simple / hotelmanagement / gen14 | U: The booking 'booking16' is marked as unconfirmed and canceled, yet its associated room reservation 'rr22' includes charges for on-site physical consumption like 'Late room service' and 'Express ... | U | R | U |  |  |
| gpt_5_2 / cot / addressbook / gen5 / edge | U: The object model contains severe logical contradictions and category errors in its relationships. For example, `P_Archivist_1` is simultaneously modeled as a BOSS and an EMPLOYEE of `P_Haakon_1`. | U | U | U |  | Relationship::BossOnlyBetweenPersons; Relationship::CoworkerSameCompany; Relationship::SubdivisionBetweenCompanies |
| gpt_5_2 / cot / addressbook / gen5 / edge | U: Furthermore, `P_Archivist_1` (a Person) is related to `C_MicroEtats_1` and `C_Paperless_1` (Companies) via a SUBDIVISION relationship, which makes no sense in the real world as a person cannot b... | U | U | U |  | Relationship::BossOnlyBetweenPersons; Relationship::CoworkerSameCompany; Relationship::SubdivisionBetweenCompanies |
| gpt_5_2 / cot / pickupnet / gen4 / complex | R: The object model is syntactically correct and semantically logical. | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / complex | The geographic coordinates accurately map to the Delhi-NCR region, | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / complex | the addresses match real-world locations, and the assignment of drivers corresponds correctly to the shipment statuses (i.e., 'NEW' shipments lack a driver, while other statuses have one). | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / complex | All constraints, including unique IDs and distinct pickup/delivery addresses, are satisfied. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen16 | R: The object model demonstrates strong semantic consistency with real-world rental business logic. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen16 | For instance, trailers are logically assigned a $0.00 per-mile rate because they lack odometers, while trucks have appropriate per-mile charges, realistic MPG (mileage), and gas tank capacities. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen16 | State identifiers, license plates, and branch locations correctly correspond to one another, | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen16 | and all multiplicity and domain constraints are fully satisfied. | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen12 | U: The banquet reservation 'banq12' has 120 people attending but is assigned to be served by only a single waiter ('w34'). It is physically impossible for one person to serve a banquet of this size... | U | R | U |  |  |
| gpt_5_2 / simple / myexpenses / gen28 | U: The expense 'expSepFilmProduction' sums up bills of different currencies (Ugandan Shilling and Sri Lankan Rupee) by directly adding their raw numerical values without any exchange rate conversio... | U | U | U |  | Expense::CrossCurrencySumOfBills |
| gpt_5_2 / simple / myexpenses / gen28 | U: Additionally, the amounts are drastically unrealistic for the chosen currencies (e.g., 450 UGX for a camera rig rental is approximately $0.12 USD). | U | U | U |  | Expense::CrossCurrencySumOfBills |
| gpt_5_2 / simple / statemachine / gen1 | U: The state machines 'smGreenStart', 'smYellowStart', and 'smOpenStart' do not contain any states themselves, yet they define states belonging to other state machines ('smTraffic' and 'smDoor') as... | U | U | U |  | State::NotEmptyState; StateMachine::NonEmptyStateMachine; StateMachine::StartStateIsContained |
| gpt_5_2 / simple / football / gen7 | R: The object model is entirely plausible and logically consistent for a football tracking system. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen7 | Attribute values such as 90 minutes for match duration, ages in the mid-20s for players, 0-0 match scores aligning correctly with the lack of goal events, and the realistic descriptive fields (e.g.... | R | R | R |  |  |
| gpt_5_2 / cot / hotelmanagement / gen2 / boundary | U: The object 'D16' of class 'FreeRoomTypesDTO' indicates there are 250 free "Presidential family suite" rooms with 12 beds each. A "Presidential suite" is by definition the most exclusive and limi... | U | U | U |  | Bill::SumOfExpenses |
| gpt_5_2 / simple / pickupnet / gen26 | U: The shipment 'shp_310004' has its status set to 'ASSIGNED', but it is not linked to any Driver object via the 'DriverShipment' association. In this domain context, transitioning from 'NEW' to 'A... | U | U | U |  | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / bank / gen27 | U: It is highly implausible in a real-world scenario that a single 18-year-old ('p105', Laura Gomez) would be granted 'Use' access to every bank account in the system ('accCH10' to 'accCH13'), whic... | U | U | R | Having access to four bank accounts may happen in the real world | Bank::RealisticBic |
| gpt_5_2 / simple / bank / gen27 | U: Additionally, the assigned BIC code for 'bankCO1' ('BBNORCOBXXXX') is 12 characters long, whereas valid standard SWIFT/BIC codes are strictly 8 or 11 characters. | U | U | U |  | Bank::RealisticBic |
| gpt_5_2 / simple / restaurant / gen5 | R: The object model portrays a highly consistent and plausible restaurant scenario. | R | R |  |  |  |
| gpt_5_2 / simple / restaurant / gen5 | All attributes accurately reflect real-world values (e.g., sensible prep times, matching | R | R |  |  |  |
| gpt_5_2 / simple / restaurant / gen5 | food measurements like 'Ounce' for fluids and 'Sheet' for wrappers, | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen5 | logical ages, and sequential reservation/order times). | R | R |  |  |  |
| gpt_5_2 / simple / restaurant / gen5 | Furthermore, the objects fully comply with the required multiplicities, capacities, and constraints | R | R |  |  |  |
| gpt_5_2 / simple / restaurant / gen5 | (such as the sum of owner shares equaling 100). | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen5 | The instantiated objects without associations (DietaryRequirement, ReportedAllergy) simply stem from a lack of associations in the domain model, but their values remain logical. | R | R | R |  |  |
| gpt_5_2 / simple / addressbook / gen7 | U: The relationship objects 'relGreenfieldCoworkerStonebridge' and 'relStonebridgeCoworkerGreenfield' assign a 'COWORKER' relationship type between 'greenfieldPrint' and 'stonebridgePublishing'. Bo... | U | U | U |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / cot / videoclub / gen1 / baseline | R: The object model represents a plausible scenario with valid real-world attributes (human names, reasonable dates, realistic titles, and correct quantities). The relationships correctly assign un... | R | R | R |  |  |
| gpt_5_2 / cot / addressbook / gen1 / complex | R: The object model is highly consistent and semantically sound. The geographic data (cities, phone country codes, and domains) match precisely, the hierarchical and peer relationships (Boss, Emplo... | R | U | R |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / cot / addressbook / gen2 / boundary | R: Creating a new, empty Address Book is a standard and necessary initial state before adding any contacts. Naming it for a specific upcoming event or purpose, such as a trip to Tehran, is perfectl... | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen4 / complex | R: The object model perfectly aligns with reality. | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen4 / complex | Account balances are positive, IBANs match the country context representing each bank, all owners are adults (satisfying the age constraint), and minors simply have "Use" access to older relatives'... | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen4 / complex | (e.g., 17-year-old Daan using 56-year-old Jeroen's account), which models real-world family banking. | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen5 / baseline | R: The object model is highly consistent and semantically plausible. | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen5 / baseline | The bank details (BICs, countries), IBAN formats, and person names accurately correspond to real-world Greek and Norwegian data. | R | R | R |  |  |
| gpt_5_2 / cot / bank / gen5 / baseline | Account balances are positive, all owners are adults, and the ownership multiplicities (1 to 2 owners per account) are strictly respected. Users who are not owners (e.g., accountants or partners) a... | R | R | R |  |  |
| gpt_5_2 / cot / football / gen4 / complex | R: The object model is highly consistent and semantically rich. | R | R | R |  |  |
| gpt_5_2 / cot / football / gen4 / complex | The player names and phone country codes correctly align with their club's physical locations (Qatar, New Zealand, Poland), | R | R | R |  |  |
| gpt_5_2 / cot / football / gen4 / complex | and the Academy team appropriately contains younger players (16-17). | R | R | R |  |  |
| gpt_5_2 / cot / football / gen4 / complex | Match events, individual player goals, and math scores sum up perfectly according to the domain constraints, and timestamps are within match durations. | R | R | R |  |  |
| gpt_5_2 / cot / football / gen4 / complex | Real-world training objectives and reasons for absence (e.g., school exams for a youth player, traffic delays) are also highly plausible. | R | R | R |  |  |
| gpt_5_2 / cot / football / gen5 / boundary | R: The object model perfectly aligns with the domain constraints and real-world logic. | R | R | R |  |  |
| gpt_5_2 / cot / football / gen5 / boundary | The ages, geographical locations (Lagos/Nigeria and Jeju/South Korea), | R | R | R |  |  |
| gpt_5_2 / cot / football / gen5 / boundary | and phone number formats are authentic. | R | R | R |  |  |
| gpt_5_2 / cot / football / gen5 / boundary | Furthermore, the match score (1-0) accurately corresponds to the single registered `GOAL` match event, and the total goal count is correctly attributed to the match player. | R | R | R |  |  |
| gpt_5_2 / cot / hotelmanagement / gen2 / baseline | R: The object model represents a plausible hotel scenario. The attributes and prices are realistic, | R | U | R |  | Bill::SumOfExpenses |
| gpt_5_2 / cot / hotelmanagement / gen2 / baseline | the dates match the New Year's context for the specific 'New Year's menu' extra, | R | U | R |  | Bill::SumOfExpenses |
| gpt_5_2 / cot / hotelmanagement / gen2 / baseline | and the relationships appropriately associate a customer, a canceled booking, and its generated bill. | R | U | R |  | Bill::SumOfExpenses |
| gpt_5_2 / cot / hotelmanagement / gen2 / baseline | Having multiple rooms under a single room reservation is permitted by the domain model and common for families or groups. | R | U | R |  | Bill::SumOfExpenses |
| gpt_5_2 / cot / hotelmanagement / gen5 / complex | R: The object model is highly coherent and semantically sound. | R | R | R |  |  |
| gpt_5_2 / cot / hotelmanagement / gen5 / complex | The check-in and check-out dates of all room reservations correctly fit within the main booking's timeframe. | R | R | R |  |  |
| gpt_5_2 / cot / hotelmanagement / gen5 / complex | Furthermore, the billed prices match the exact calculation of the number of nights multiplied by the room rates, plus the prices of any respective room extras. | R | R | R |  |  |
| gpt_5_2 / cot / hotelmanagement / gen5 / complex | The overlapping dates for some room reservations are also plausible for a single customer booking multiple rooms for a group. | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen1 / complex | R: The object model strictly adheres to the domain model's multiplicities and constraints, including the exact summation of bill amounts for each expense. | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen1 / complex | The dates are logically ordered, and the expense amounts are highly accurate and plausible relative to their real-world currencies | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen1 / complex | (e.g., 3200 JPY for the Narita Express, 189 EUR for a Madrid hotel, 900 MAD for a Casablanca riad). | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen1 / complex | The statuses and comments closely match the real-world accounting contexts described. | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen6 / complex | R: The object model is perfectly consistent with the domain model. | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen6 / complex | All OCL constraints are satisfied (the sum of the bills equals the total expense amounts). | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen6 / complex | The dates of the bills fall within the start and end dates of their respective expenses. | R | R | R |  |  |
| gpt_5_2 / cot / myexpenses / gen6 / complex | The semantic content—such as currencies, real-world prices, business travel categories, and justification comments that match the statuses—is highly logical and plausible. | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen3 / complex | R: The object model represents a highly plausible and logically consistent logistics network. | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen3 / complex | The geographic coordinates accurately correspond to the assigned real-world locations (São Paulo and Marrakech), | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen3 / complex | the stations maintain clean boundaries without geographically impossible assignments, | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen3 / complex | and the shipment statuses (`NEW`, `ASSIGNED`, `UNDERWAY`, `DELIVERED`) strictly correlate with proper driver assignments. | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen3 / complex | Furthermore, business entities and physical addresses align realistically | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen3 / complex | (e.g., "Instituto Butantã" dispatching from a distribution center to a hospital). | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / baseline | R: The object model represents a highly plausible logistics scenario. | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / baseline | The attributes use realistic Brazilian names, recognizable Twitter handles, | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / baseline | and valid real-world addresses in São Paulo with accurately corresponding latitude (-23.5) and longitude (-46.6 to -46.7) coordinates. | R | R | R |  |  |
| gpt_5_2 / cot / pickupnet / gen4 / baseline | The relationships between customers, drivers, and shipments are logical, and no constraints (such as distinct pickup and delivery addresses) are violated. | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen3 / baseline | R: The object model perfectly aligns with the domain model and represents a highly plausible real-world restaurant scenario. | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen3 / baseline | The reservation timings match the chronologically ordered meal courses, ingredient allergens correctly map to the food items | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen3 / baseline | (e.g., lactose in cream, gluten in crispbread), | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen3 / baseline | owner shares sum to exactly 100%, | R | R | R |  |  |
| gpt_5_2 / cot / restaurant / gen3 / baseline | and table capacities logically accommodate the number of guests for each reservation. | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen1 / boundary | R: The object model describes a semantically plausible state machine for a 'Paris metro timer'. | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen1 / boundary | A self-transition ('boucleAttente') on the 'départ' state triggered by a positive time event correctly represents a typical waiting loop behavior in control logic. | R | R | R |  |  |
| gpt_5_2 / cot / statemachine / gen1 / boundary | The naming conventions also correctly obey the constraints. | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen3 / baseline | R: The objects, attributes, and relationships are logically consistent with real-world vehicle rental operations. | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen3 / baseline | The attribute values (e.g., dates, truck fuel capacities, mpg bounds, and financial figures) fall within physically possible and commercially plausible ranges. | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen3 / baseline | The associations correctly link customers, vehicles, and rental offices without contradictions. | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen5 / baseline | R: The object model uses plausible, real-world formats for prices, miles, standard US addresses, and dates. | R | R |  |  |  |
| gpt_5_2 / cot / vehiclerental / gen5 / baseline | It properly respects inheritance (only assigning attributes like gas tank capacity and odometer to `Truck` objects, not general `Vehicles` like trailers). | R | R |  |  |  |
| gpt_5_2 / cot / vehiclerental / gen5 / baseline | Relationships are logically consistent: there are no conflicting/overlapping rental dates for the same vehicle, and the time gaps between rentals across different states | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen5 / baseline | (e.g., from Arizona to Florida) allow enough physical time for the vehicles to have been relocated. | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen5 / baseline | Individuals and Companies also behave reasonably within standard business practices | R | R | R |  |  |
| gpt_5_2 / cot / vehiclerental / gen5 / baseline | (e.g., a company simultaneously renting multiple assets in different states). | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen4 / baseline | R: The object model represents a highly plausible scenario for a video club. | R | R |  |  |  |
| gpt_5_2 / cot / videoclub / gen4 / baseline | Attribute values such as client IDs, available copies, and episode numbers are all positive and internally consistent. | R | R |  |  |  |
| gpt_5_2 / cot / videoclub / gen4 / baseline | The movie and series titles align logically with their genres, and associations reflect realistic behavior | R | R |  |  |  |
| gpt_5_2 / cot / videoclub / gen4 / baseline | (e.g., identical series titles having sequenced episodes and sharing an actor, clients renting multiple cassettes concurrently under one rental). | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen6 / baseline | R: The object model represents a highly plausible scenario for a video club. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen6 / baseline | The entities have realistic attributes (recognized names, | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen6 / baseline | sensible dates from Oct 2022 to Jan 2023, | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen6 / baseline | valid IDs, and logical quantities). | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen6 / baseline | The relationships correctly map clients to rentals containing realistic amounts of movies/series episodes, and actors are logically assigned to their respective media. | R | R | R |  |  |
| gpt_5_2 / cot / videoclub / gen6 / baseline | Furthermore, all domain constraints (positive IDs, episodes, and non-negative copies) are respected. | R | R | R |  |  |
| gpt_5_2 / cot / addressbook / gen3 / baseline | U: The relationships between the company objects 'C_Arctic_PolarAirCargo_1' (an air cargo division) and 'C_Arctic_HotelKalaallit_1' (a hotel) are nonsensical. In the real world, a hotel cannot be t... | U | U | U |  | Relationship::BossOnlyBetweenPersons; Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / cot / addressbook / gen6 / boundary | U: The relationship 'Rel_Aurora_R5_1' designates the company 'C_Himalaya_1' as a '#COWORKER' to the company 'C_Aurora_1'. The concept of a coworker strictly applies to individual persons working to... | U | U | U |  | Relationship::BossOnlyBetweenPersons; Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / cot / bank / gen1 / edge | U: The bank 'b2' is definitively located in 'Monaco', but its associated account 'a3' has a German IBAN (starting with 'DE') and account 'a4' has a French IBAN (starting with 'FR'). In the real wor... | U | R | U |  |  |
| gpt_5_2 / cot / bank / gen5 / edge | U: The account 'a107' has a balance of exactly 2147483647, which is the maximum 32-bit signed integer value, indicating synthetic or test generation data rather than a real-world amount. | U | U | R | While the number is a special numbers for 32-bit signed integers, it is a realistic values for banks in general. Stil... | Account::ValidIbanCountry |
| gpt_5_2 / cot / bank / gen5 / edge | U: Furthermore, it is highly implausible that an 8-year-old child ('p168') is granted usage rights to 5 different bank accounts owned by unrelated adults ('p163', 'p164'). | U | U | U |  | Account::ValidIbanCountry |
| gpt_5_2 / cot / football / gen3 / edge | U: The object model contains multiple impossible values and contradictions:  1. `MatchEvent` 'ME94' occurs at `time := -1`, which is physically impossible. | U | U | U |  | MatchEvent::RealisticTimeEvent; MatchPlayerPosition::RealisticPlayerPositions; MatchReport::EventTimeInMatch |
| gpt_5_2 / cot / football / gen3 / edge | 2. `MatchPlayer` 'MP95' has a `rating` of 2147483647 (the maximum 32-bit integer), which is not a valid performance rating. | U | U | R | While the number is a special numbers for 32-bit signed integers, it is a realistic values for ratings in general. St... | MatchEvent::RealisticTimeEvent; MatchPlayerPosition::RealisticPlayerPositions; MatchReport::EventTimeInMatch |
| gpt_5_2 / cot / football / gen3 / edge | 3. `MatchEvent` 'ME98' occurs at `time := 121`, far exceeding the match `duration` of 1 recorded in `MatchReport` 'MR17'. | U | U | U |  | MatchEvent::RealisticTimeEvent; MatchPlayerPosition::RealisticPlayerPositions; MatchReport::EventTimeInMatch |
| gpt_5_2 / cot / football / gen3 / edge | 4. `MatchPlayerPosition` 'MPP96' uses shirt `number := 0`, which is invalid in standard football. | U | U | U |  | MatchEvent::RealisticTimeEvent; MatchPlayerPosition::RealisticPlayerPositions; MatchReport::EventTimeInMatch |
| gpt_5_2 / cot / football / gen4 / edge | U: The object model contains several contradictions and impossible values. Player P148 (via MP146) is recorded as scoring 12 goals in the match, yet the MatchReport (MR25) indicates a final score o... | U | U | U |  | Match::SameMatchDuplicatedPlayers; MatchEvent::RealisticTimeEvent; MatchReport::EventTimeInMatch; MatchReport::RealisticMatchDuration; MatchReport::SumOfGoals |
| gpt_5_2 / cot / football / gen4 / edge | Additionally, the match duration is 0 | U | U | U |  | Match::SameMatchDuplicatedPlayers; MatchEvent::RealisticTimeEvent; MatchReport::EventTimeInMatch; MatchReport::RealisticMatchDuration; MatchReport::SumOfGoals |
| gpt_5_2 / cot / football / gen4 / edge | Player P148 has two separate MatchPlayer instances with different shirt numbers (1 and 10) for the same match | U | U | U |  | Match::SameMatchDuplicatedPlayers; MatchEvent::RealisticTimeEvent; MatchReport::EventTimeInMatch; MatchReport::RealisticMatchDuration; MatchReport::SumOfGoals |
| gpt_5_2 / cot / football / gen4 / edge | MatchPlayer MP149 has an impossible negative rating (-50). | U | U | R | Players could have a negative rating, its a possible value. eg. an unpopular player | Match::SameMatchDuplicatedPlayers; MatchEvent::RealisticTimeEvent; MatchReport::EventTimeInMatch; MatchReport::RealisticMatchDuration; MatchReport::SumOfGoals |
| gpt_5_2 / cot / football / gen5 / edge | U: MatchPlayer 'MP165' (Player 'P162') is recorded as having scored 9 goals in Match 'M29', but the match report ('MR28') indicates a total score of 1-0, making this mathematically impossible. | U | U | U |  | MatchReport::SumOfGoals; TrainingObjective::RealisticDates |
| gpt_5_2 / cot / football / gen5 / edge | Additionally, TrainingObjective 'TO179' has a start date (2026-03-10) that occurs after its end date (2026-03-01). | U | U | U |  | MatchReport::SumOfGoals; TrainingObjective::RealisticDates |
| gpt_5_2 / cot / hotelmanagement / gen1 / edge | U: The object 'B999999' of class 'Booking' has an 'endDate' ('2026-03-05') that chronologically occurs before its 'startDate' ('2026-03-10'), which is impossible. | U | U | U |  | Booking::CanceledNotConfirmed; Booking::ConfirmedNotCanceled; FreeRoomTypesDTO::RealisticPrice |
| gpt_5_2 / cot / hotelmanagement / gen1 / edge | Additionally, the booking is simultaneously marked as both confirmed and canceled, | U | U | Partial | Bad model design. At some point in time a reservation could be confirmed by the hotel and at the same time cancelled ... | Booking::CanceledNotConfirmed; Booking::ConfirmedNotCanceled; FreeRoomTypesDTO::RealisticPrice |
| gpt_5_2 / cot / hotelmanagement / gen1 / edge | and Room 'R13' has an implausible 'pricePerNight' of 0.01. | U | U | U |  | Booking::CanceledNotConfirmed; Booking::ConfirmedNotCanceled; FreeRoomTypesDTO::RealisticPrice |
| gpt_5_2 / cot / hotelmanagement / gen6 / boundary | U: The object 'RM_MIN_R16' has a negative room number (-2147483647), representing an integer underflow or artificial boundary value test. | U | U | Partial | The explanation is not appropriate. The problem is that a room doesn't usually have negative number. The explanation ... | FreeRoomTypesDTO::RealisticNumberOfRooms; Room::RealisticRoomNumber |
| gpt_5_2 / cot / hotelmanagement / gen6 / boundary | Additionally, the 'D65' object claims to have 1,000,000 free rooms, which exceeds the physical capacity of any real-world hotel, | U | U | U |  | FreeRoomTypesDTO::RealisticNumberOfRooms; Room::RealisticRoomNumber |
| gpt_5_2 / cot / hotelmanagement / gen6 / boundary | and a price of 0.01 per night is implausibly low. | U | U | U |  | FreeRoomTypesDTO::RealisticNumberOfRooms; Room::RealisticRoomNumber |
| gpt_5_2 / cot / myexpenses / gen3 / edge | U: - The object `expenseEXPEDGE006_2` has an `endDate` (1582-10-04) that is chronologically before its `startDate` (1582-10-15). | U | U | U |  | Bill::RealisticDates; Expense::CrossCurrencySumOfBills; Expense::EndDateAfterStartDate; Expense::RealisticDates |
| gpt_5_2 / cot / myexpenses / gen3 / edge | - The dates used across the model are absurd for a real-world expense system, including dates in the distant future (year 9999) and the distant past (years 1582 and 1800) paid using modern methods ... | U | U | U |  | Bill::RealisticDates; Expense::CrossCurrencySumOfBills; Expense::EndDateAfterStartDate; Expense::RealisticDates |
| gpt_5_2 / cot / myexpenses / gen3 / edge | - `expenseEXPEDGE005_2` financially aggregates bills blindly using a 1:1 ratio across completely different currencies (Moroccan Dirham and Icelandic Króna), which is financially nonsensical. (The i... | U | U | U |  | Bill::RealisticDates; Expense::CrossCurrencySumOfBills; Expense::EndDateAfterStartDate; Expense::RealisticDates |
| gpt_5_2 / cot / myexpenses / gen6 / baseline | U: The 'amount' of the Expense 'expenseEXP2026091' (2006.25) is calculated by directly summing the raw amounts of its bills. However, these bills are associated with different currencies (GBP, NOK,... | U | U | U |  | Expense::CrossCurrencySumOfBills |
| gpt_5_2 / cot / myexpenses / gen6 / boundary | U: The dates for the expense and bills are set to '1900-02-28', but the bills use 'CREDIT_CARD' payment methods and the 'South African Rand' (ZAR) currency. Credit cards did not exist in 1900, | U | R | U |  |  |
| gpt_5_2 / cot / myexpenses / gen6 / boundary | and the South African Rand was not introduced until 1961, making the scenario anachronistic and historically impossible. | U | R | U |  |  |
| gpt_5_2 / cot / pickupnet / gen1 / edge | U: Driver 'EDGE_D2' is assigned to both 'EDGE_SHP3' (located in Japan) and 'EDGE_SHP4' (which is 'UNDERWAY' from Svalbard, Norway to Antarctica). A single driver handling local Japanese routes whil... | U | U | Partial | The concept of station is unclear. If its a local station it would be unrealistic because it cannot be in Japan and N... | Shipment::AssignedShouldHaveDriver; Shipment::NewShouldntHaveDriver |
| gpt_5_2 / cot / pickupnet / gen1 / edge | Furthermore, 'EDGE_SHP3' has a status of 'NEW' despite already being assigned to a driver. | U | U | U |  | Shipment::AssignedShouldHaveDriver; Shipment::NewShouldntHaveDriver |
| gpt_5_2 / cot / pickupnet / gen5 / boundary | U: Shipment 'SHP6003' involves a pickup at the North Pole and a delivery at the South Pole, handled by a single driver, which is physically impossible for a typical delivery network. | U | R | Partial | The concept of station is unclear. If its a local station it would be unrealistic because it cannot be in Japan and N... |  |
| gpt_5_2 / cot / pickupnet / gen5 / boundary | Additionally, address 'ABLANK600' has an empty text string and is located in the middle of the Pacific Ocean (0.0, -179.9999), making it invalid as a pickup/delivery location for shipments 'SHP6001... | U | R | U |  |  |
| gpt_5_2 / cot / restaurant / gen2 / edge | U: The object model contains duplicate employee records: Cooks 'K23' and 'K24' share the exact same name, date of birth, and personal phone number, which is physically unrealistic for two distinct ... | U | U | U |  | Banquet::RealisticBanquet; Worker::UniqueWorkerIdentity |
| gpt_5_2 / cot / restaurant / gen2 / edge | Additionally, the Banquet 'BR14' is registered for 1 person but requests a bus service, which contradicts the concept of both a banquet and bus transportation. | U | U | U |  | Banquet::RealisticBanquet; Worker::UniqueWorkerIdentity |
| gpt_5_2 / cot / restaurant / gen5 / baseline | U: The object model contains two distinct `Cook` instances ('K50' and 'K51') that have identical values for name ('Jae-min Park'), date of birth ('1985-06-05'), phone number, and years of experienc... | U | U | U |  | Worker::UniqueWorkerIdentity |
| gpt_5_2 / cot / statemachine / gen3 / boundary | U: The object model uses extreme magic numbers for time events (`te_ata_t_maintenanceDone` with 2147483647, which is INT_MAX, and `te_ata_t_intakeStall` with 999999999). Considering another timer (... | U | R | R | The LLM as a Judge assumed that the time units are seconds |  |
| gpt_5_2 / cot / statemachine / gen6 / baseline | U: The transition 'tr_add_landAndReport' moves the drone from the 'emergencyLanding' state directly into the 'charging' state after a 180-second timer. A drone that performs an emergency landing in... | U | R | U |  |  |
| gpt_5_2 / cot / vehiclerental / gen6 / edge | U: The object model contains several illogical and physically impossible values. The truck `V500_EC6` has a `gasTankCapacity` of 0.0 | U | U | U |  | RentalAgreement::PositiveAnticipatedDuration; RentalAgreement::PositiveNumberId; RentalAgreement::RealisticDepositPaid; RentalAgreement::RealisticPricing; Truck::RealisticDistances; Truck::Realisti... |
| gpt_5_2 / cot / vehiclerental / gen6 / edge | and an unrealistically long license plate number. | U | U | U |  | RentalAgreement::PositiveAnticipatedDuration; RentalAgreement::PositiveNumberId; RentalAgreement::RealisticDepositPaid; RentalAgreement::RealisticPricing; Truck::RealisticDistances; Truck::Realisti... |
| gpt_5_2 / cot / vehiclerental / gen6 / edge | Furthermore, the rental agreement `RA500_EC6` has a negative `anticipatedDuration` (-1) and `number` (-500), | U | U | U |  | RentalAgreement::PositiveAnticipatedDuration; RentalAgreement::PositiveNumberId; RentalAgreement::RealisticDepositPaid; RentalAgreement::RealisticPricing; Truck::RealisticDistances; Truck::Realisti... |
| gpt_5_2 / cot / vehiclerental / gen6 / edge | while `RA501_EC6` has an absurdly high `quotedRatePerMile` of over 123 million and an astronomical `depositPaid`. | U | U | U |  | RentalAgreement::PositiveAnticipatedDuration; RentalAgreement::PositiveNumberId; RentalAgreement::RealisticDepositPaid; RentalAgreement::RealisticPricing; Truck::RealisticDistances; Truck::Realisti... |
| gpt_5_2 / cot / videoclub / gen4 / boundary | U: The object 'rentalHubR30' of class 'Rental' has its date set to '0001-01-01', which historically precedes the invention of cassettes and video clubs by almost two millennia. | U | U | U |  | Rental::RealisticRentalDate |
| gpt_5_2 / simple / bank / gen13 | R: The relationships and attribute values logically map to a real-world banking scenario. | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen13 | Ages and balances represent standard variations, while bank details like country logic | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen13 | (e.g., Australian banks matched with AU IBANs) are consistent. | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen13 | The object model aligns with the domain constraints | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen13 | (all account owners are 18 or older), | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen13 | while plausibly allowing minors, such as 13-year-old Mia Chen, to be authorized users on accounts owned by adults. | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen21 | R: The object model represents a plausible banking scenario. | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen21 | Bank details and account identifiers are logically structured, balances are within normal positive ranges, and joint ownership is correctly portrayed. | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen21 | Furthermore, the scenario respects the domain constraints by ensuring account owners are adults, while realistically reflecting that a minor | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen21 | (age 16) can be an authorized user of existing accounts. | R | U | R |  | Account::ValidIbanCountry |
| gpt_5_2 / simple / football / gen6 | R: The object model represents a highly consistent and logically sound football scenario. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen6 | The relationships between clubs, teams, players, and match events closely mirror real-world mechanics. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen6 | Furthermore, the match data is meticulously accurate: the 6 'GOAL' events in match 2 perfectly correlate with the 4-2 MatchReport scores and the individual goal counts tallied by the participating ... | R | R | R |  |  |
| gpt_5_2 / simple / football / gen6 | while ages, ratings, and match minute timings are all contextually plausible. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen17 | R: The object model perfectly aligns with domain constraints and real-world football logic. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen17 | Attributes such as player ages (21-31), match ratings (6-9), and match durations (90 minutes) are totally plausible. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen17 | Furthermore, the goals scored by individual match players precisely match the total match score and the tally of goal events for each team. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen17 | The timeline and logic of matches, training sessions, and absences also make complete sense. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen27 | R: The object model perfectly aligns with the domain model and real-world football logic. | R | R | R |  |  |
| gpt_5_2 / simple / football / gen27 | Player ages appropriately match their team categories | R | R | R |  |  |
| gpt_5_2 / simple / football / gen27 | (e.g., 15-16 year olds in U16 teams), | R | R | R |  |  |
| gpt_5_2 / simple / football / gen27 | the match events and match notes correspond logically (a late penalty matches the event log, goals sum exactly to the final scores), and standard constraints like teams belonging to different clubs... | R | R | R |  |  |
| gpt_5_2 / simple / football / gen27 | All attributes contain plausible and contextually accurate values. | R | R | R |  |  |
| gpt_5_2 / simple / hotelmanagement / gen26 | R: The object model represents a valid hotel booking scenario where a customer changes rooms during their stay. | R | R | R |  |  |
| gpt_5_2 / simple / hotelmanagement / gen26 | The dates for the two room reservations perfectly cover the overall booking period, and the bill prices match exactly with the daily room rates multiplied by the number of nights for each reservation. | R | R | R |  |  |
| gpt_5_2 / simple / hotelmanagement / gen28 | R: The dates for the booking and room reservation match and represent a logical 7-night stay. | R | R | R |  |  |
| gpt_5_2 / simple / hotelmanagement / gen28 | The calculation for the bill is accurate based on the room's requested price per night (7 nights * $165.0/night = $1155.0). | R | R | R |  |  |
| gpt_5_2 / simple / hotelmanagement / gen28 | Setting the future reserved room to blocked and unoccupied is also logically sound in a hotel management context. | R | R | R |  |  |
| gpt_5_2 / simple / myexpenses / gen3 | R: The object model represents a standard business purchase (a $1299.00 laptop) | R | R | R |  |  |
| gpt_5_2 / simple / myexpenses / gen3 | with perfectly plausible attribute values for amounts, dates, and categories. | R | R | R |  |  |
| gpt_5_2 / simple / myexpenses / gen3 | The currency and payment method align with real-world practices, the multiple comments provide logical context, and the expense amount correctly matches the sum of its associated bill. | R | R | R |  |  |
| gpt_5_2 / simple / myexpenses / gen10 | R: The object model represents a very consistent and logically sound real-world scenario of moving to a new apartment. | R | R | R |  |  |
| gpt_5_2 / simple / myexpenses / gen10 | The bills are properly categorized, their dates fall perfectly within the expense start and end dates, payment methods and statuses align with standard practices (as corroborated by the detailed co... | R | R | R |  |  |
| gpt_5_2 / simple / pickupnet / gen18 | R: The object model represents a highly logically consistent logistics scenario. | R | U | R |  | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / pickupnet / gen18 | Real-world geography (cities in Thuringia like Erfurt, Jena, Weimar, and their coordinates) aligns perfectly with the addresses. | R | U | R |  | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / pickupnet / gen18 | The shipments validly transition through plausible statuses (NEW, ASSIGNED, UNDERWAY, DELIVERED), constraints like unique IDs and differing pickup/delivery addresses are respected, and relationship... | R | U | R |  | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / pickupnet / gen21 | R: The object model is logically consistent with real-world logistics. | R | R | R |  |  |
| gpt_5_2 / simple / pickupnet / gen21 | Geolocation attributes map accurately to the provided text addresses in Hanover, Germany. | R | R | R |  |  |
| gpt_5_2 / simple / pickupnet / gen21 | Shipment states align perfectly with driver associations ( | R | R | R |  |  |
| gpt_5_2 / simple / pickupnet / gen21 | e.g., the "NEW" shipment has no driver assigned, while "ASSIGNED", "UNDERWAY", and "DELIVERED" shipments do), | R | R | R |  |  |
| gpt_5_2 / simple / pickupnet / gen21 | and all shipments have distinct pick-up and delivery addresses. | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen3 | R: The object model represents a highly plausible restaurant scenario. | R | R |  |  |  |
| gpt_5_2 / simple / restaurant / gen3 | Attributes like birth dates, preparation times, and cooking experience fall within logical bounds | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen3 | (e.g., measuring saffron in grams). | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen3 | The relationships are semantically consistent; table capacities precisely match reservation group sizes, menu items are composed of appropriate and logical ingredients with correct allergens, | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen3 | and the assignment of a German-speaking waiter to a German-preferring customer adds strong real-world validity. | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen7 | R: The object model represents a highly plausible scenario that adheres perfectly to real-world restaurant logistics. | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen7 | The attributes and relationships make logical sense | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen7 | (e.g., combining two 2-seat tables for a 4-person walk-in, assigning two buses for a 60-person banquet, and placing item orders chronologically 15-20 minutes after the reservation time). | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen7 | Capacities, ingredient units, and worker ages are entirely consistent. | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen14 | R: The object model perfectly aligns with the domain constraints and real-world logic. | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen14 | The owner shares sum up exactly to 100%, | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen14 | the tables assigned to each reservation have adequate capacity to accommodate the number of guests, staff ages and experience levels are plausible, and the menu items correctly match their sensible... | R | R | R |  |  |
| gpt_5_2 / simple / restaurant / gen14 | All sequences of events (orders placed after reservation time) are chronologically sound. | R | R | R |  |  |
| gpt_5_2 / simple / statemachine / gen14 | R: The created state machines logically represent real-world processes. | R | U | NotJustified |  | State::NotEmptyState |
| gpt_5_2 / simple / statemachine / gen14 | The states (proposed, scheduled, ongoing, paused, finished, cancelled) | R | U | R |  | State::NotEmptyState |
| gpt_5_2 / simple / statemachine / gen14 | and transitions accurately model the lifecycle of a conference session. | R | U | R |  | State::NotEmptyState |
| gpt_5_2 / simple / statemachine / gen14 | The timers have positive integer values, the state names follow the lowercase constraint, and the workflow is completely plausible for the given domain without any logical fallacies. | R | U | R |  | State::NotEmptyState |
| gpt_5_2 / simple / vehiclerental / gen11 | R: The object model portrays a highly logical and consistent vehicle rental scenario. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen11 | The attributes (e.g., fuel capacities, truck vs. trailer mileage rates, realistic geographical locations, area codes) all align with real-world norms. | R | R | R |  |  |
| gpt_5_2 / simple / vehiclerental / gen11 | Furthermore, the physical movement of the truck ('truck_fl_24ft_31') is chronologically sound: it is rented from Orlando to Hartford on Oct 4th, returned, and then smartly re-rented from Hartford b... | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen24 | R: The object model represents a plausible state for a video club. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen24 | Attribute values such as client IDs, available copies, and episode numbers are logical and within realistic bounds. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen24 | The titles and actor names are sensible, and | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen24 | the reuse of actors ('nrA1', 'nrA6') across different episodes of the same series ('Library Mysteries') reflects real-world consistency. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen29 | R: The object model represents a mathematically and logically sound state for a video club. | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen29 | Attribute values are physically and temporally plausible | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen29 | (e.g., sensible 2006 dates, positive available copies, and valid episode numbers). | R | R | R |  |  |
| gpt_5_2 / simple / videoclub / gen29 | Scenarios such as renting multiple series at once, having 0 current available copies for highly demanded items, and keeping historical records of rentals for suspended clients are all fully consist... | R | R | R |  |  |
| gpt_5_2 / simple / addressbook / gen6 | U: The relationship objects 'relAuroraCoworkerOrg', 'relOrgCoworkerArena', and 'relTicketingCoworkerAurora' use the type 'COWORKER' to link two 'Company' instances (e.g., auroraMedia and lumenFestO... | U | U | U |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / simple / addressbook / gen8 | U: The relationship 'relLibraryCoworkerCoop' defines two distinct companies ('cityLibrary' and 'artisansCoop') as coworkers, which is logically nonsensical as "coworker" applies only to individual ... | U | U | U |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / simple / addressbook / gen8 | Furthermore, 'marcoSilva' and 'nadiaKhan' are linked as coworkers despite being explicitly modeled as employees of different companies. | U | U | U |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / simple / addressbook / gen20 | U: The relationship 'relSahanaCoworkerHousing' links a Person ('sahanaRao') and a Company ('orchardHousing') with the relationship type '#COWORKER'. It is logically nonsensical for a single human t... | U | U | U |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / simple / addressbook / gen23 | U: The relationships `relMarineCoworkerPolar` and `relPolarCoworkerShipyard` assign the `#COWORKER` relationship type between `Company` objects (`kestrelMarine` and `polarInsurance`, `polarInsuranc... | U | U | U |  | Relationship::CoworkerOnlyBetweenPersons; Relationship::CoworkerSameCompany |
| gpt_5_2 / simple / bank / gen16 | U: The relationships constructed between owners, users, and accounts are highly illogical and indicate randomly generated data. For instance, the South Korean account 'krAcc1' is owned by a South K... | U | U | R | Unsupported claim. Inferring nationalities from names is not a valid procedure | Account::ValidIbanCountry |
| gpt_5_2 / simple / bank / gen20 | U: A 12-year-old Moroccan child ('n6', Ines ElAmrani) is listed as an authorized user for a Chilean joint bank account ('clAcc1') owned by two unrelated Chilean adults ('n4' and 'n5'). While she re... | U | U | R | Unsupported claim. | Account::ValidIbanCountry |
| gpt_5_2 / simple / football / gen5 | U: The object model schedules an official competitive cup match (`matchSDC_1` in the "Spring Derby Cup") between 'teamEA_W' (explicitly named "Eastvale Athletic Women", populated by female players ... | U | R | R | Unsupported claim. Male and females could play against each other |  |
| gpt_5_2 / simple / hotelmanagement / gen5 | U: The booking 'booking7' is simultaneously set to `canceled := true` and `confirmed := true`, which represents a contradictory state. | U | U | Partial | Bad model design. At some point in time a reservation could be confirmed by the hotel and at the same time cancelled ... | Bill::SumOfExpenses; Booking::CanceledNotConfirmed; Booking::ConfirmedNotCanceled |
| gpt_5_2 / simple / hotelmanagement / gen5 | Furthermore, the single booking requires the customer to switch to completely different rooms with fluctuating total bed capacities (4 beds on night 1, 2 beds on night 2, 3 beds on night 3) every s... | U | U | R | Although its strange it could happen in some scenarios. eg. no the same room available in the 3 different days | Bill::SumOfExpenses; Booking::CanceledNotConfirmed; Booking::ConfirmedNotCanceled |
| gpt_5_2 / simple / hotelmanagement / gen29 | U: The price of `bill44` (310.0) is mathematically inconsistent with the rest of the model. It is linked to `rr43`, which is a 3-night reservation for two rooms priced at 155.0 per night, plus a 30... | U | U | U |  | Bill::SumOfExpenses |
| gpt_5_2 / simple / myexpenses / gen16 | U: The expense 'expQ1BusinessSupplies' directly sums the raw 'amount' values of bills that belong to completely different currencies (Taiwanese Dollars and Chinese Yuan) to yield its total of 1035.... | U | U | U |  | Expense::CrossCurrencySumOfBills |
| gpt_5_2 / simple / myexpenses / gen25 | U: The bill `billParkingRefundProcessed_2026_04_06` represents a refund transaction but is assigned a positive amount (6.00). As a result, it is added to the total `Expense` sum (yielding 984.00), ... | U | R | U |  |  |
| gpt_5_2 / simple / pickupnet / gen11 | U: Shipment 'shp_14003' has a status of 'ASSIGNED' but is not linked to any driver. | U | U | U |  | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / pickupnet / gen11 | Furthermore, driver 'drv_1101' is composed within the Ulm station ('st_ulm') but is assigned to deliver 'shp_14004', which is a shipment managed by the Kassel station ('st_kassel') with pickup and ... | U | U | R | Although strange this can happen in real life | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / pickupnet / gen19 | U: The shipment 'shp_90002' has its status set to 'ASSIGNED', indicating it has been dispatched or allocated, but it is not linked to any 'Driver' instance via the 'DriverShipment' association. Thi... | U | U | U |  | Shipment::AssignedShouldHaveDriver |
| gpt_5_2 / simple / statemachine / gen12 | U: The object model creates numerous empty `StateMachine` instances (e.g., `smAtmStartCardInserted`, `smSprinklerStartScheduled`) that contain no states or transitions. | U | U | U |  | StateMachine::NonEmptyStateMachine; StateMachine::StartStateIsContained |
| gpt_5_2 / simple / statemachine / gen12 | Furthermore, these empty state machines are assigned start states that are contained within entirely different state machines (`smAtm`, `smSprinkler`). In standard state machine semantics, a start ... | U | U | U |  | StateMachine::NonEmptyStateMachine; StateMachine::StartStateIsContained |
| gpt_5_2 / simple / statemachine / gen16 | U: The object model creates multiple unnatural, empty "helper" state machines (e.g., 'smRobotStartIdle', 'smTicketGateStartReading') simply to assign almost every state as a start state of some sta... | U | U | U |  | StateMachine::NonEmptyStateMachine; StateMachine::StartStateIsContained |
| gpt_5_2 / simple / vehiclerental / gen24 | U: 1. **Physical/Time Impossibility**: Rental `ra_110001` has an `anticipatedDuration` of 1 day to move a 24-foot truck from Wilmington, DE to Key West, FL (~1,200 miles). This would require ~20 ho... | U | U | R | In the model there is no concept of drivers. In the truck there could be multiple drivers. The distance and required ... | RentalAgreement::RealisticPricing |
| gpt_5_2 / simple / vehiclerental / gen24 | 2. **Business Logic Failure**: Rental `ra_110004` grants a 30-day, cross-country rental with a zero deposit (`depositPaid := 0.00`) to `cust_co_seabreeze11`, despite the customer being explicitly f... | U | U | Partial | Usually a high risk customer should pay a desposit, but there might be situations where this wont happen | RentalAgreement::RealisticPricing |
| gpt_5_2 / simple / vehiclerental / gen24 | 3. **Logistical Fallacy**: `truck_fl_24ft_1101` finishes `ra_110001` in Key West, FL on January 5th. It then starts `ra_110003` in Fort Collins, CO on January 10th. Dedicating 5 days just to deadhe... | U | U | R | Situation that could happen in real life, trucks might be unloaded, comming home, emergency, etc... -> realistic | RentalAgreement::RealisticPricing |
| gpt_5_2 / simple / vehiclerental / gen30 | U: The vehicle 'trailer_ct_covered_16b01' is assigned to two overlapping rental agreements: 'ra_16b_001' (starting 2026-05-06 for 28 days) and 'ra_16b_002' (starting 2026-05-15). A single physical ... | U | U | U |  | RentalAgreement::RealisticPricing |
| gpt_5_2 / simple / vehiclerental / gen30 | Additionally, 'ra_16b_002' quotes a rate per mile for this trailer, but in the domain model, mileage and odometer readings are only tracked for the 'Truck' subclass, making it impossible to calcula... | U | U | Partial | Usually trailers are rented by day, but its not inplausible to rent them by mileage. Probably mileage should be at th... | RentalAgreement::RealisticPricing |
| gpt_5_2 / simple / videoclub / gen23 | U: It is highly implausible for a single actor ('na_common') to star in all six completely unrelated media items, which span wildly different formats and genres (a jazz music session, a photography... | U | R | R | Situation is possible in real life |  |

_Total statement rows: 278_

## Judge errors (JUDGE_LENIENT) (6)

- **gpt_5_2 / cot / addressbook / gen1 / complex** — judge: R, inv: U, invariants: 2, audit: 1 stmt, 0 issues
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / cot / hotelmanagement / gen2 / baseline** — judge: R, inv: U, invariants: 1, audit: 4 stmt, 0 issues
  - `checking invariant (1) `Bill::SumOfExpenses': FAILED.`
- **gpt_5_2 / simple / bank / gen13** — judge: R, inv: U, invariants: 1, audit: 6 stmt, 0 issues
  - `checking invariant (2) `Account::ValidIbanCountry': FAILED.`
- **gpt_5_2 / simple / bank / gen21** — judge: R, inv: U, invariants: 1, audit: 4 stmt, 0 issues
  - `checking invariant (2) `Account::ValidIbanCountry': FAILED.`
- **gpt_5_2 / simple / pickupnet / gen18** — judge: R, inv: U, invariants: 1, audit: 3 stmt, 0 issues
  - `checking invariant (3) `Shipment::AssignedShouldHaveDriver': FAILED.`
- **gpt_5_2 / simple / statemachine / gen14** — judge: R, inv: U, invariants: 1, audit: 4 stmt, 1 issues
  - `checking invariant (1) `State::NotEmptyState': FAILED.`

## Human rejects judge U (HUMAN_REJECTS_U) (10)

- **gpt_5_2 / cot / statemachine / gen4 / boundary** — judge: U, inv: U, invariants: 1, human: No
  - _Human note:_ The state machine is not about a technical system, it is a socio system which contains non-determinism.
  - `checking invariant (1) `State::NotEmptyState': FAILED.`
- **gpt_5_2 / cot / bank / gen5 / boundary** — judge: U, inv: R, invariants: 0, human: No
  - _Human note:_ While the number is a special numbers for 32-bit signed integers, it is a realistic values for banks in general. Still, for testing specific implementations of Integer it would be an interesting and special value, but this does not change the fact that it is a realistic number in a bank context.
- **gpt_5_2 / simple / bank / gen27** — judge: U, inv: U, invariants: 1, human: No
  - _Human note:_ Having access to four bank accounts may happen in the real world
  - `checking invariant (5) `Bank::RealisticBic': FAILED.`
- **gpt_5_2 / cot / bank / gen5 / edge** — judge: U, inv: U, invariants: 1, human: No
  - _Human note:_ While the number is a special numbers for 32-bit signed integers, it is a realistic values for banks in general. Still, for testing specific implementations of Integer it would be an interesting and special value, but this does not change the fact that it is a realistic number in a bank context.
  - `checking invariant (2) `Account::ValidIbanCountry': FAILED.`
- **gpt_5_2 / cot / statemachine / gen3 / boundary** — judge: U, inv: R, invariants: 0, human: No
  - _Human note:_ The LLM as a Judge assumed that the time units are seconds
- **gpt_5_2 / simple / bank / gen16** — judge: U, inv: U, invariants: 1, human: No
  - _Human note:_ Unsupported claim. Inferring nationalities from names is not a valid procedure
  - `checking invariant (2) `Account::ValidIbanCountry': FAILED.`
- **gpt_5_2 / simple / bank / gen20** — judge: U, inv: U, invariants: 1, human: No
  - _Human note:_ Unsupported claim.
  - `checking invariant (2) `Account::ValidIbanCountry': FAILED.`
- **gpt_5_2 / simple / football / gen5** — judge: U, inv: R, invariants: 0, human: No
  - _Human note:_ Unsupported claim. Male and females could play against each other
- **gpt_5_2 / simple / vehiclerental / gen24** — judge: U, inv: U, invariants: 1, human: No
  - _Human note:_ In the model there is no concept of drivers. In the truck there could be multiple drivers. The distance and required time is logical in the US as car an trucks go to the same speed Usually a high risk customer should pay a desposit, but there might be situations where this wont happen Situation that could happen in real life, trucks might be unloaded, comming home, emergency, etc... -> realistic
  - `checking invariant (4) `RentalAgreement::RealisticPricing': FAILED.`
- **gpt_5_2 / simple / videoclub / gen23** — judge: U, inv: R, invariants: 0, human: No
  - _Human note:_ Situation is possible in real life

## Semantic-only unrealism (SEMANTIC_ONLY) (6)

- **gpt_5_2 / simple / hotelmanagement / gen14** — judge: U, inv: R, invariants: 0, human: Yes
- **gpt_5_2 / simple / restaurant / gen12** — judge: U, inv: R, invariants: 0, human: Yes
- **gpt_5_2 / cot / bank / gen1 / edge** — judge: U, inv: R, invariants: 0, human: Yes
- **gpt_5_2 / cot / myexpenses / gen6 / boundary** — judge: U, inv: R, invariants: 0, human: Yes
- **gpt_5_2 / cot / statemachine / gen6 / baseline** — judge: U, inv: R, invariants: 0, human: Yes
- **gpt_5_2 / simple / myexpenses / gen25** — judge: U, inv: R, invariants: 0, human: Yes

## Triple agreement — U, human Yes, invariants fail (AGREE_U_FORMAL) (30)

- **gpt_5_2 / cot / restaurant / gen5 / boundary** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (1) `Banquet::RealisticBanquet': FAILED.`
  - `checking invariant (6) `Worker::UniqueWorkerIdentity': FAILED.`
- **gpt_5_2 / cot / addressbook / gen5 / edge** — judge: U, inv: U, invariants: 3, human: Yes
  - `checking invariant (2) `Relationship::BossOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
  - `checking invariant (6) `Relationship::SubdivisionBetweenCompanies': FAILED.`
- **gpt_5_2 / simple / myexpenses / gen28** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (2) `Expense::CrossCurrencySumOfBills': FAILED.`
- **gpt_5_2 / simple / statemachine / gen1** — judge: U, inv: U, invariants: 3, human: Yes
  - `checking invariant (1) `State::NotEmptyState': FAILED.`
  - `checking invariant (3) `StateMachine::NonEmptyStateMachine': FAILED.`
  - `checking invariant (4) `StateMachine::StartStateIsContained': FAILED.`
- **gpt_5_2 / cot / hotelmanagement / gen2 / boundary** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (1) `Bill::SumOfExpenses': FAILED.`
- **gpt_5_2 / simple / pickupnet / gen26** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (3) `Shipment::AssignedShouldHaveDriver': FAILED.`
- **gpt_5_2 / simple / addressbook / gen7** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / cot / addressbook / gen3 / baseline** — judge: U, inv: U, invariants: 3, human: Yes
  - `checking invariant (2) `Relationship::BossOnlyBetweenPersons': FAILED.`
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / cot / addressbook / gen6 / boundary** — judge: U, inv: U, invariants: 3, human: Yes
  - `checking invariant (2) `Relationship::BossOnlyBetweenPersons': FAILED.`
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / cot / football / gen3 / edge** — judge: U, inv: U, invariants: 3, human: Yes
  - `checking invariant (5) `MatchEvent::RealisticTimeEvent': FAILED.`
  - `checking invariant (6) `MatchPlayerPosition::RealisticPlayerPositions': FAILED.`
  - `checking invariant (7) `MatchReport::EventTimeInMatch': FAILED.`
- **gpt_5_2 / cot / football / gen4 / edge** — judge: U, inv: U, invariants: 5, human: Yes
  - `checking invariant (2) `Match::SameMatchDuplicatedPlayers': FAILED.`
  - `checking invariant (5) `MatchEvent::RealisticTimeEvent': FAILED.`
  - `checking invariant (7) `MatchReport::EventTimeInMatch': FAILED.`
  - `checking invariant (9) `MatchReport::RealisticMatchDuration': FAILED.`
  - `checking invariant (10) `MatchReport::SumOfGoals': FAILED.`
- **gpt_5_2 / cot / football / gen5 / edge** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (10) `MatchReport::SumOfGoals': FAILED.`
  - `checking invariant (12) `TrainingObjective::RealisticDates': FAILED.`
- **gpt_5_2 / cot / hotelmanagement / gen1 / edge** — judge: U, inv: U, invariants: 3, human: Yes
  - `checking invariant (4) `Booking::CanceledNotConfirmed': FAILED.`
  - `checking invariant (5) `Booking::ConfirmedNotCanceled': FAILED.`
  - `checking invariant (7) `FreeRoomTypesDTO::RealisticPrice': FAILED.`
- **gpt_5_2 / cot / myexpenses / gen3 / edge** — judge: U, inv: U, invariants: 4, human: Yes
  - `checking invariant (1) `Bill::RealisticDates': FAILED.`
  - `checking invariant (2) `Expense::CrossCurrencySumOfBills': FAILED.`
  - `checking invariant (3) `Expense::EndDateAfterStartDate': FAILED.`
  - `checking invariant (4) `Expense::RealisticDates': FAILED.`
- **gpt_5_2 / cot / myexpenses / gen6 / baseline** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (2) `Expense::CrossCurrencySumOfBills': FAILED.`
- **gpt_5_2 / cot / restaurant / gen2 / edge** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (1) `Banquet::RealisticBanquet': FAILED.`
  - `checking invariant (6) `Worker::UniqueWorkerIdentity': FAILED.`
- **gpt_5_2 / cot / restaurant / gen5 / baseline** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (6) `Worker::UniqueWorkerIdentity': FAILED.`
- **gpt_5_2 / cot / vehiclerental / gen6 / edge** — judge: U, inv: U, invariants: 6, human: Yes
  - `checking invariant (1) `RentalAgreement::PositiveAnticipatedDuration': FAILED.`
  - `checking invariant (2) `RentalAgreement::PositiveNumberId': FAILED.`
  - `checking invariant (3) `RentalAgreement::RealisticDepositPaid': FAILED.`
  - `checking invariant (4) `RentalAgreement::RealisticPricing': FAILED.`
  - `checking invariant (7) `Truck::RealisticDistances': FAILED.`
  - `checking invariant (8) `Truck::RealisticGasTankCapacity': FAILED.`
- **gpt_5_2 / cot / videoclub / gen4 / boundary** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (4) `Rental::RealisticRentalDate': FAILED.`
- **gpt_5_2 / simple / addressbook / gen6** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / simple / addressbook / gen8** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / simple / addressbook / gen20** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / simple / addressbook / gen23** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `Relationship::CoworkerOnlyBetweenPersons': FAILED.`
  - `checking invariant (4) `Relationship::CoworkerSameCompany': FAILED.`
- **gpt_5_2 / simple / hotelmanagement / gen29** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (1) `Bill::SumOfExpenses': FAILED.`
- **gpt_5_2 / simple / myexpenses / gen16** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (2) `Expense::CrossCurrencySumOfBills': FAILED.`
- **gpt_5_2 / simple / pickupnet / gen11** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (3) `Shipment::AssignedShouldHaveDriver': FAILED.`
- **gpt_5_2 / simple / pickupnet / gen19** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (3) `Shipment::AssignedShouldHaveDriver': FAILED.`
- **gpt_5_2 / simple / statemachine / gen12** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `StateMachine::NonEmptyStateMachine': FAILED.`
  - `checking invariant (4) `StateMachine::StartStateIsContained': FAILED.`
- **gpt_5_2 / simple / statemachine / gen16** — judge: U, inv: U, invariants: 2, human: Yes
  - `checking invariant (3) `StateMachine::NonEmptyStateMachine': FAILED.`
  - `checking invariant (4) `StateMachine::StartStateIsContained': FAILED.`
- **gpt_5_2 / simple / vehiclerental / gen30** — judge: U, inv: U, invariants: 1, human: Yes
  - `checking invariant (4) `RentalAgreement::RealisticPricing': FAILED.`

## Human partial on U (HUMAN_PARTIAL) (4)

- **gpt_5_2 / cot / hotelmanagement / gen6 / boundary** — judge: U, inv: U, invariants: 2, human: Partial
  - _Human note:_ The explanation is not appropriate. The problem is that a room doesn't usually have negative number. The explanation the LLM does it not appropriate as this number doesn't cause an overflow
  - `checking invariant (6) `FreeRoomTypesDTO::RealisticNumberOfRooms': FAILED.`
  - `checking invariant (9) `Room::RealisticRoomNumber': FAILED.`
- **gpt_5_2 / cot / pickupnet / gen1 / edge** — judge: U, inv: U, invariants: 2, human: Partial
  - _Human note:_ The concept of station is unclear. If its a local station it would be unrealistic because it cannot be in Japan and Norway at the same time, but if we consider a station as a shipping company it would make sense to be in different places
  - `checking invariant (3) `Shipment::AssignedShouldHaveDriver': FAILED.`
  - `checking invariant (4) `Shipment::NewShouldntHaveDriver': FAILED.`
- **gpt_5_2 / cot / pickupnet / gen5 / boundary** — judge: U, inv: R, invariants: 0, human: Partial
  - _Human note:_ The concept of station is unclear. If its a local station it would be unrealistic because it cannot be in Japan and Norway at the same time, but if we consider a station as a shipping company it would make sense to be in different places
- **gpt_5_2 / simple / hotelmanagement / gen5** — judge: U, inv: U, invariants: 3, human: Partial
  - _Human note:_ Bad model design. At some point in time a reservation could be confirmed by the hotel and at the same time cancelled (by the customer). Although its strange it could happen in some scenarios. eg. no the same room available in the 3 different days
  - `checking invariant (1) `Bill::SumOfExpenses': FAILED.`
  - `checking invariant (4) `Booking::CanceledNotConfirmed': FAILED.`
  - `checking invariant (5) `Booking::ConfirmedNotCanceled': FAILED.`

