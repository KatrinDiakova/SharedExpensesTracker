# Project Title: Shared Expenses Tracker Application

## Summary:
Developed a Shared Expenses Tracker, a practical Java-based Spring Boot application designed to manage and split shared expenses among individuals and groups efficiently. It's a versatile tool ideal for use in social settings like dinner parties, gift-sharing events, and group purchases.

## Key Features:

* **Transaction Management:** Created a feature to track and sum up small loans between friends, ensuring clarity in personal financial interactions.
* **Group Expense Splitting:** Implemented functionality for dividing the cost of group gifts, simplifying shared expenses in predefined groups.
* **Special Occasion Handling:** Adapted the program to exclude individuals (like a birthday person) from bill splitting, catering to specific event requirements.
* **Secret Santa Organizer:** Enhanced the application to organize Secret Santa events, including pairing participants, managing a database of gift information, and handling special deals.
* **Complex Splitting Algorithms:** Developed advanced bill splitting schemes that consider various details to ensure fairness in shared expenses.
* **Optimized Repayment Solutions:** Improved the application to minimize the total number of transactions, reducing bank fees and simplifying repayment processes.

This project showcases an advanced application of Java and Spring Boot in developing a user-friendly and highly functional tool for financial management in social and group settings.

## Program description
### Commands presently supported:
1. Borrow Command:
   
```[date] borrow PersonOne PersonTwo amount``` 

On the specified date, PersonTwo lends a specified amount to PersonOne.

2. Repay Command:
   
```[date] repay PersonOne PersonTwo amount```

On the given date, PersonOne repays a certain amount to PersonTwo.

3. Balance Command:

```[date] balance [open|close] \[(+/- list of names and groups)\]```

Calculates and displays repayments list on a balance date. 'Open' denotes the opening balance at the month's start, and 'close' indicates the closing balance on the specified date. Filters to show balances involving the resolved names or groups.

4. BalancePerfect Command:
   
```[date] balancePerfect [open|close] \[(+/- list of names and groups)\]```

Similar to the balance command, it calculates and displays repayments, but optimizes them to minimize the number of transactions necessary for settling all debts.

5.Group Management Command:

```group create|show|add|remove GROUPNAME [(+/- list of names and groups)]```

Manages groups of individuals: create, display, add or remove members from a group, with names and groups listed in the command.

6. Purchase Command:
   
```[date] purchase Person itemName amount (+/- list of names and groups)```

Logs a purchase made by a person for a certain amount. The cost is evenly distributed among the resolved group, with any cents remainder split among the first N persons in alphabetical order, each person pays an extra 0.01.

7. SecretSanta Command:
   
```secretSanta GROUPNAME```

Organizes a Secret Santa within a group, ensuring no one gets themselves and avoiding reciprocal gifting in larger groups.

8. Cashback Command:
   
```[date] cashback Person itemName amount [(list of [+|-] persons | GROUPS)]```

Records a commitment to reimburse an expense to a group, dividing it equally among group members, following the same logic as in the purchase command.

9. WriteOff Command:
    
```[date] writeOff```

Erases all transactions in the database up to and including a specified limit date (default is the current date).

10. Help Command:
    
```help```

Displays a naturally sorted list of all available commands.

11. Exit Command
    
```exit```
