# Software Design Techniques - Keki


# Monolithic Architecture


A monolithic architecture design puts all of the application's components in one single unit.

If we were to use it for keki, it would be like this:

* Presentation/UI Layer - the user interface where:
    * Costumers browse cakes and place orders
    * Staff members/ owner manage orders

* Business Logic/ Application Layer - it handles:
    * Order management
    * Cake Customization
    * Inventory management
    * Notifications

* Data Access Layer - the interaction with database basically, it stores and lets us insert/update/delete the following data:
    * Orders
    * Recipes
    * Inventory stock
    * User data

* Database:
    * Mariadb

Everything runs as a single process.

**Figure 1: Monolithic Architecture**


## Pros

* Everything runs in one process
* Simple and easy to understand
* low complexity
* consistent
* Fast development and deployment


## Cons

* Limited scalability
* It is more difficult to make modifications
* Any small change required redeploying the whole application
