The purpose of this file is to document the current status and progress of ongoing features that already have changes on main branch  
It also contains a list of ideas for future changes  

# Ongoing changes
## Tying data to user
- ~~Create user table and PO~~
- ~~Create user on login or load from db if already signed in previously~~
- ~~Create hibernate filter for filtering data by user and an aspect to activate it~~
- ~~Tie entities to user and filter them by the user when querying db~~ 
  - ~~BudgetCategory~~
  - ~~Category~~
  - ~~Income~~
- Review all the changes
  - ~~Make _user_id_ columns _not null_~~
  - ~~Consider changing the _UserPO_ object to just an integer in the other PO classes~~

# Future changes
## Features
- Enable removal of expenses
- Include total amount of money
- Add accounts / wallets
- (LONG TERM) Make an actual UI that does not look like shit

## Technical improvements
- Update htmx and validate functionality of all the app
- Make integration test that goes through a full login and usage flow
  - Might be a good idea to find some tooling for this
- Add liquibase for DB migrations
- (LONG TERM) Prepare AWS env and pipeline/script for deployment 
- Create builders for:
  - Persistence - High priority, persistence tests will be more common
  - Domain - Medium priority, domain is relatively simple right now
  - Web - Low priority, most testing will be done in other modules