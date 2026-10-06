The purpose of this file is to document the current status and progress of ongoing features that already have changes on main branch  
It also contains a list of ideas for future changes  

# Ongoing changes
- Update htmx and validate functionality of all the app

# Future changes
## Features
- Enable removal of expenses
- Include total amount of money
- Add accounts / wallets
- (LONG TERM) Make an actual UI that does not look like shit

## Technical improvements
- Make integration test that goes through a full login and usage flow
  - Might be a good idea to find some tooling for this
- Add liquibase for DB migrations
- (LONG TERM) Prepare AWS env and pipeline/script for deployment 
  - Adjust [compose](/compose.yaml) file to include the application
- Create builders for:
  - Persistence - High priority, persistence tests will be more common
  - Domain - Medium priority, domain is relatively simple right now
  - Web - Low priority, most testing will be done in other modules