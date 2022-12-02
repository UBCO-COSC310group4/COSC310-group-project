# COSC310-group-project

## Github Link:

https://github.com/UBCO-COSC310group4/COSC310-group-project/tree/individual-stu (My individual additions are on the branch ```individual-stu```)

## Stuart McGorman Invidivual Project Updates

### New Features

- #### Sales Charts Page

I have added a page on the manager screen where the user can view sales charts usings the sales records in our data base. It displays sales per day and also includes a spinner where you can select the interval of days you want to see the data over. I used to JFreeChart API to implement this feature

- #### Output to PDF Feature

I have added the ability for our program to output tables, charts, and other information to pdf files. On the sales charts page I added a save to pdf button which askes the user to select a location and file name and then generates 2 page a pdf file containing the current chart on the first page and a list off all sales on the second page. I also added a save to pdf feature to the create order page where the user to select a checkbox before submitting and order. If the checkbox is sellected the program brings up the file selection menu and then saves a pdf containing a table of the order info. I used the JFreePDF API to implement this feature

### Lisst of newly added APIs

- JFreeCharts: https://www.jfree.org/jfreechart/
- JFreePDF: https://github.com/jfree/jfreepdf

## Previous Documentation:

Our project is an inventory management system for a retail store. The system will have 3 main components, a central database which keeps track of all information, 
an interface to be used by cashiers which can make transactions and update the database, and a more in-depth interface to be used by a manager to manage stock, 
input new orders, view sales stats and stock warnings, etc. The main features that we need to implement are a login system for entering credentials to access either 
the manager or cashier screen, a cashier system that can process sales, and a management system that can add products, view sales, and manage suppliers.

Instructions to compile and run the code: 
To run the system on your device you would need to install the docker application. Next, create a directory called cosc310 database in your sytem
You now need to compose the created directory in docker using the terminal and the command docker-compose up -d. Once that is done successfully
you can run the entire system using intelliJ code editor by opening the project file. You will need to add the JAR file provided into the intelliJ 
source code file for dependency and you will be able to use the system.


The whole system is divided into  five main classes which handle a singular core function of the system. The Cashier class handles
the cashier functions such as scanning the items of the customer's cart and the payment. The Login class manages the login for both
the cashiers and the manager. The manage employees class gives the manager the access to manage employees of the store. The manager
class gives manager the manager options to check the sale statistics, Inventory and low stocks. The suppliers class shows the list
of suppliers for that store set by the manager.

The cashier class contains methods to add an item, delete an item, to logout of the system and to record the sale made to the database.
The login class has two methods one for the manager login and one for the cashier login. Both the methods register the cashier and the 
manager within a databse and uses that database to verify the credentials to login successfully.

The manage employees class has a method which has a list of all the employees connected to the database. With the updateList method
the manager can update the list of employees. The class also has methods for logging out and going to the home screen.

The supplier class has methods to add and delete suppliers and the class is connected to the database which can be accessed locally

Lastly, the manager class has several methods which are easy to interpret such as to manage sales, the inventory and the low stock alerts
warning. The manager can add item and delte items in the inventory through respective methods.

We have now implemented further features in the system. We have added the ability to create new employees, In the manage employees screen, there are fields to enter the first name, last name, username, password, and enable manager abilities. Once the add employee button is pressed, the employee is added to the database and they are able to log in using the entered credentials. We also added the ability to create customer accounts with the system by using their first name, last name and email address. The system will send the purchase receipt via email if the button to do so is clicked. We also added the feature to create and view new orders.
