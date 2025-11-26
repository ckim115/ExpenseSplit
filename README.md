# Expense Split
Expense Split is an Android application for splitting expenses among friends. The app automatically calculates who owes whom, tracks upcoming deadlines, highlights overdue payments, and organizes completed expenses separately.

## Features
### Split Page
Create and configure new expenses:
- Enter the expense amount, date, and title
- Add any number of payer names
- Choose how to split the amount:
  - Equally
  - By percentage per payer
- Automatically calculates individual owed amounts
- Generates a clear breakdown of who owes whom

### Deadlines Page
Displays all active expenses:
- Shows all outstanding expenses
- Overdue expenses are displayed in red
- Long-press an expense to move it to the History page

Supports sorting by:
- Expense title
- Payer name
- Expense amount
- Date

### History Page
Displays completed or inactive expenses:
- Long-press to return an expense back to Deadlines
- Supports the same sorting options as Deadlines

## UI Behavior
- Overdue dates appear in red; normal due dates remain black
- Long-press gestures move expenses between Deadlines and History

## Data Flow Overview
1. Create an expense using the Split Page
2. The expense is added to the Deadlines list
3. Long-press to mark it as complete and move it to History
4. Expenses can be moved back to Deadlines if needed

## Getting Started
1. Clone the repository:
`git clone https://github.com/your-username/expense-split.git`
2. Open the project in Android Studio and run it on an emulator or physical device.
