# Java-Project - Car Rental System in Java
Runnable on terminal, commands are commented in the code

## Customer
- A sign-up and a login
- all the details are stored in MySQL DB

## Rental
- A 'book' form and a 'release' form
- When a car is rented from a login, it can be relieved only from the same login

## Admin
- A login, like everyone
- Can add to and remove from DB (Only car table)

## Design
### Back-End
- SQLHandler class interacts with the DB, GUI windows call its methods
### Front-End
- Spread through classes Login, Signup, AdminWindow, CustomerWindow, BookFrame and ReleaseFrame.
- CustomerWindow contains BookFrame and ReleaseFrame.
- AdminWindow implements the functionalities of editing car DB.
