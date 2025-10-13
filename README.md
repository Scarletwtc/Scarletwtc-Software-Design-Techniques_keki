# Software-Design-Techniques_keki
# Smart Cake Shop Management System

# Team

- **El-Ghoul Layla** — GitHub [@scarletwtc](https://github.com/scarletwtc)  
- **Mahmoud Mirghani Abdelrahman** — GitHub [@Abd210](https://github.com/Abd210)  
- **Uritu Andra-Ioana** — GitHub [@andrauritu](https://github.com/andrauritu)

# Project Description 

The **Smart Cake Shop Management System (SCMS)** is an application designed to streamline and digitalize the daily operations of **Keki**, a modern cake shop.  

The system allows **managers, chefs, and staff** to efficiently manage all aspects of the shop, including **menu items, ingredient inventory, and staff assignments**, while providing a seamless ordering experience for customers.  

With this Smart Cake Shop Management System:  
- **Chefs** can view active orders and update the preparation status of cakes.  
- **Staff** can manage order fulfillment and communicate with the chef or manager.  
- **Managers** can oversee inventory levels, update the menu, and monitor daily sales and performance reports.  

**Keki’s Smart Cake Shop Management System aims to increase operational efficiency, reduce errors, and provide accurate tracking of inventory and orders, ensuring both staff and customers enjoy a smooth and organized experience.**

### Core Features


### Core Features
| **Role** | **Key Features** |
|:----------:|:-----------------:|
| **Customer** | Browse cakes, place orders,  rate experience |
| **Chef** | View active orders, update baking progress |
| **Staff** | Deliver ready orders, handle order fulfillment, communicate with chef/manager |
| **Manager** | Manage cake menu, track inventory, assign staff tasks, view sales reports |

**Scope:** Initially implemented as a  Java application; later refactored into microservices for scalability and maintainability.

## Design Patterns

The **Smart Cake Shop Management System** leverages multiple design patterns to ensure **clean, maintainable, and reusable code**. These patterns follow the principles taught in the course, and are applied to core aspects of the system: menu management, inventory tracking, staff coordination, and order processing.


| **Pattern** | **Type** | **Use in SCMS / Justification** |
|-------------|---------|--------------------------------|
| **Singleton** | Creational | Ensures a single instance of the **InventoryManager** class, centralizing ingredient tracking and preventing inconsistent stock data. This avoids multiple sources of truth for inventory and simplifies management. |
| **Factory Method** | Creational | Used to create **Order** objects of different types (custom cake, standard cake, drinks) without exposing the instantiation logic. This allows flexibility in adding new order types in the future, adhering to the **Open/Closed Principle**. |
| **Builder** | Creational | Constructs complex **MenuItem** objects step by step, allowing flexible assembly of cake ingredients, sizes, and decorations. This simplifies creation of complex products without cluttering the main logic. |
| **Observer** | Behavioral | Enables **Chef** and **Staff** modules to automatically receive updates when an **Order** status changes, ensuring smooth workflow and timely notifications. This reduces tight coupling and improves maintainability. |
| **Strategy** | Behavioral | Allows the **Manager** to dynamically change pricing strategies or discount policies without modifying core menu or order classes. This promotes flexibility and adheres to **SRP**. |
| **Proxy** | Structural | Provides controlled access to **sensitive manager operations**, such as viewing sales reports, enforcing security and encapsulation. This reduces the risk of unauthorized access. |

**Why:** These patterns were chosen to:  
1. **Simplify object creation** : Singleton, Factory Method, Builder 
2. **Decouple modules and improve maintainability** : Observer, Strategy, Proxy
3. **Support future extension** without modifying existing code : OCP, flexible pattern use 
4. **Increase code reuse and clarity** : delegation where necessary

