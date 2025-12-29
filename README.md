# text-book-inventory
Textbook Inventory System: Abstract Data Type (ADT) Design

Prepared by: Shishir Simha K

Project: Bookstore Inventory System

Version: 1.0

Overview

This document outlines the algorithm headers for the core functionality of the Textbook Inventory System. The system manages stock levels for both new and used textbooks, handles pricing logic, and processes transactions.

1. Core Inventory Processes

a. Ordering Textbooks

Name: placeTextbookOrder
Parameters: * isbn (String): The unique International Standard Book Number.

vendorId (String): The identifier for the supplier.

quantity (Integer): The number of copies to order.

Purpose: Creates a purchase order for a specific textbook to replenish stock.
Pre-conditions: * The vendorId must exist in the vendor database.

quantity must be greater than 0.

Post-conditions: * A purchase order record is created with status "Pending".

The system logs the expected arrival date.

Return Value: Boolean (True if order placed successfully, False otherwise).

b. Receiving Textbooks

Name: receiveShipment
Parameters: * orderId (String): The reference number of the purchase order.

isbn (String): The identifier of the book being received.

quantityReceived (Integer): Actual number of books in the box.

invoiceCost (Float): The cost per unit charged by the vendor.

condition (Enum: NEW/USED): The condition of the received books.

Purpose: Updates the inventory count based on an incoming shipment and closes out the purchase order.
Pre-conditions: * orderId must exist and be in "Pending" or "Partial" status.

Post-conditions: * The quantityOnHand for the specific isbn and condition is incremented by quantityReceived.

The cost field for the textbook is updated (typically using a weighted average if costs changed).

The order status is updated to "Closed" if the order is fully fulfilled.

Return Value: Integer (The new total quantity on hand).

c. Determining Retail Price (New)

Name: calculateNewRetailPrice
Parameters: * invoiceCost (Float): The wholesale cost of the book.

markupPercentage (Float): The bookstore's standard markup rate (e.g., 0.25 for 25%).

Purpose: Calculates the selling price for a new textbook based on cost and store policy.
Pre-conditions: * invoiceCost must be non-negative.

markupPercentage must be non-negative.

Post-conditions: * None (this is a calculation function, it does not alter state).

Return Value: Float (The calculated retail price rounded to two decimal places).

d. Pricing Used Textbooks

Name: calculateUsedRetailPrice
Parameters: * newRetailPrice (Float): The current shelf price of a new copy.

depreciationRate (Float): The standard discount for used books (e.g., 0.25 for 25% off new price).

Purpose: Calculates the selling price for a used textbook based on the new textbook's price.
Pre-conditions: * newRetailPrice must be greater than 0.

Post-conditions: * None.

Return Value: Float (The calculated used price).

e. Determining Quantity on Hand

Name: getInventoryLevel
Parameters: * isbn (String): The book identifier.

condition (Enum: NEW/USED/ALL): Filters for specific stock types.

Purpose: Retrieves the current physical stock count for a specific title.
Pre-conditions: * The isbn must exist in the system catalog.

Post-conditions: * System state remains unchanged.

Return Value: Integer (Current count).

f. Recording Textbook Sales

Name: processSale
Parameters: * isbn (String): The book being sold.

quantity (Integer): Number of copies being purchased.

condition (Enum: NEW/USED): The type of book being purchased.

Purpose: Decrements inventory and logs a transaction when a student buys a book.
Pre-conditions: * quantity > 0.

getInventoryLevel(isbn, condition) must be greater than or equal to quantity.

Post-conditions: * quantityOnHand for the specific isbn and condition is decreased by quantity.

A sales transaction record is generated.

Revenue accounts are updated.

Return Value: Float (Total transaction amount).

g. Recording Textbook Returns

Name: processCustomerReturn
Parameters: * transactionId (String): The receipt number from the original sale.

isbn (String): The book being returned.

condition (Enum: NEW/USED): The condition of the book at the time of return.

Purpose: Handles a student returning a book, verifying the condition and restocking.
Pre-conditions: * transactionId must be valid and within the allowable return window (e.g., 14 days).

The book must be inspected and deemed resalable.

Post-conditions: * quantityOnHand is incremented.

A refund transaction is generated.

Return Value: Boolean (True if return processed successfully).

2. Additional Required Algorithms (System Utilities)

To make the system functional, the following helper algorithms are required to manage the catalog and reporting.

h. Adding New Title to Catalog

Name: addNewTextbookEntry
Parameters: * isbn (String)

title (String)

author (String)

publisher (String)

edition (String)

Purpose: Onboards a brand new textbook that the bookstore has never carried before.
Pre-conditions: * isbn must not already exist in the database.

Post-conditions: * A new textbook record is created with quantityOnHand set to 0.

Return Value: Boolean (Success status).

i. Low Stock Alerting

Name: checkReorderThresholds
Parameters: * None (runs on entire database).

Purpose: Scans inventory to identify books that have fallen below minimum required levels.
Pre-conditions: * Textbooks must have a minimumThreshold field defined.

Post-conditions: * A list of ISBNs requiring reordering is generated.

Return Value: List<String> (List of ISBNs).

j. Update Book Condition (Buyback)

Name: convertNewToUsed
Parameters: * isbn (String)

quantity (Integer)

Purpose: Moves stock from "New" to "Used" category (usually happens if a new book is damaged or during buyback periods where students sell books back).
Pre-conditions: * Sufficient "New" stock (if damaged) or buyback logic validation.

Post-conditions: * "New" inventory decreases.

"Used" inventory increases.

Return Value: Boolean.
