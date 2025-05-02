Budget Tracker App
Users can register, log in, add expenses with categories, attach receipt photos, and analyze spending trends.

Features
User Registration & Login (local only)

Add Expense:

Amount, Description, Category

Custom categories

Date and Time pickers

Attach a photo (receipt)

View Expenses:

View by user

Filter by date range and category

Clear filters

Delete entries

Analytics:

Set monthly spending goals

See total spending vs. goals


Setup
Clone this repo.

Open in Android Studio.

Sync Gradle.

Run on an emulator or device (API 21+).

Project Structure:

kotlin
data/
    AppDatabase.kt
    Expense.kt
    ExpenseDao.kt
 viewmodel/
    ExpenseViewModel.kt
    LoginViewModel.kt
 ui/
    theme/
    MainActivity.kt
    AddExpenseScreen.kt
    ExpenseListScreen.kt
    LoginScreen.kt
    RegisterScreen.kt
    DashboardScreen.kt
    AnalyticsScreen.kt

Notes
All user data is stored locally on the device using Room.

No backend or authentication provider is used.

All categories and filters are client-side only.