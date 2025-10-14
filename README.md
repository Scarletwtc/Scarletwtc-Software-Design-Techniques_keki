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

---

### Example Workflow  

1. A **customer** places an order (e.g., a chocolate birthday cake).  
2. The **manager** reviews and confirms the order.  
3. The **chef** receives an update and begins preparation.  
4. The **inventory manager** automatically adjusts ingredient levels.  
5. Once complete, **staff** handle delivery and mark the order as fulfilled.  

---

### Core Features
| **Role** | **Key Features** |
|:----------:|:-----------------:|
| **Customer** | Browse cakes, place orders,  rate experience |
| **Chef** | View active orders, update baking progress |
| **Staff** | Deliver ready orders, handle order fulfillment, communicate with chef/manager |
| **Manager** | Manage cake menu, track inventory, assign staff tasks, view sales reports |

**Scope:** Initially developed as a **Java desktop application**, later adaptable for **web or mobile use**. The system architecture encourages **scalability**, **code reuse**, and **maintainability** through the strategic application of software design patterns.  

---
## Design Patterns

The **Smart Cake Shop Management System** leverages multiple design patterns to ensure **clean, maintainable, and reusable code**. These patterns follow the principles taught in the course, and are applied to core aspects of the system: menu management, inventory tracking, staff coordination, and order processing.


| **Pattern** | **Type** | **Use in SCMS / Justification** |
|-------------|---------|--------------------------------|
| **Singleton** | Creational | Used for the **InventoryManager** to ensure there’s only one global source of truth for ingredient data. This prevents conflicts between concurrent operations (e.g., multiple chefs accessing stock simultaneously). Unlike using static variables, Singleton allows controlled access, lazy initialization, and future extension if database connections are introduced. |
| **Factory Method** | Creational | Handles the creation of **Order** objects (custom cakes, standard cakes, drinks) without exposing instantiation details. Compared to simple constructors, the Factory Method centralizes creation logic, making it easier to introduce new order types (like gift boxes or catering orders) without altering existing code, the **Open/Closed Principle**. |
| **Builder** | Used for constructing complex **MenuItem** or **Cake** objects that can vary in ingredients, size, and decoration. Instead of cluttering constructors with multiple optional parameters, Builder allows step-by-step configuration, making code more readable, flexible, and reusable across different cake combinations. |
| **Observer** | Behavioral | Keeps **Chef** and **Staff** modules in sync with **Order** status updates. When a manager or chef updates an order, all subscribed observers (e.g., staff) are notified automatically. This pattern ensures real-time updates and eliminates unnecessary coupling between components. |
| **Strategy** | Lets the **Manager** switch between different pricing or discount algorithms dynamically (e.g., holiday discounts, loyalty points). Instead of embedding multiple conditional statements, Strategy encapsulates each pricing method in its own class. |
| **Proxy** | Structural | Acts as a security layer between the user interface and **Manager** functionalities like financial reports or employee data. The Proxy pattern ensures only authorized roles can access sensitive operations, promoting encapsulation and security over using direct object references. |
| **Delegation** | Structural | The Manager gives order validation and inventory checking to separate classes. This keeps each class focused on one job and avoids repeating code. |

**Why these patterns:** 
1. **Simplify object creation** : Singleton, Factory Method, Builder 
2. **Decouple modules and improve maintainability** : Observer, Strategy, Proxy
3. **Support future extension** without modifying existing code : OCP, flexible pattern use 
4. **Increase code reuse and clarity** : delegation where necessary

