The purpose of this file is to document the current status and progress of ongoing features that already have changes on main branch

# Tying data to user
- ~~Create user table and PO~~
- ~~Create user on login or load from db if already signed in previously~~
- ~~Create hibernate filter for filtering data by user and an aspect to activate it~~
- ~~Tie entities to user and filter them by the user when querying db~~ 
  - ~~BudgetCategory~~
  - ~~Category~~
  - ~~Income~~
- Review all the changes
  - Make _user_id_ columns _not null_
  - Consider changing the _UserPO_ object to just an integer in the other PO classes
