## Billing Service

As the [challenge](https://github.com/pleo-io/antaeus/blob/master/README.md) requires the logic of the BillingService class was fully implemented. This now allows the caller of this service to perform charges of any unpaid (Pending) invoices of Antaeus customer. Charges are performed only on the first day of a month. Upon a each charge, an External service is called to bill the customer and the Antaeus customer invoice status is updated to ‘Paid’.

## Feature Availability/Further Considerations
### Scalability
As the invoices charges currently run synchronously (fetched and processed one-by-one), this could be an expensive and inefficient task as Antaeus’ number of invoices grows larger. As a future improvement, a queue usage, for instance, can increase the performance as several different concurrent threads would pull the pending messages from the queue.
### Holidays
Local holidays might be taken into consideration and shift the charge from the 1st day of the month to the next business day in case there is a holiday on the 1st. External APIs would may provide the needed market holiday data.
### Monitoring
Customer payments are a very sensitive matter for both the SaaS company and the customer. Because of that, in a real-life service, I would invest in monitoring the charges in the following ways:
Maintain a database which stores the charges’ history.
Notify and handle immediately (possibly manually) any invoices which were charged by the External service but failed to be updated to ‘Paid’ due to an Anateuas internal error.
Create process to handle invoices which are pending for an unreasonable time period (could be caused by recurring internal failures, insufficient funds etc.).
