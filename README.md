## Billing Service

As the [challenge](https://github.com/pleo-io/antaeus/blob/master/README.md) requires the logic of the BillingService class was fully implemented. This now allows the caller of this service to perform charges of unpaid (Pending) invoices of Antaeus customers.
- charges are performed only on the first day of the month. 
- upon each charge, an external service is called to bill the customer and the Antaeus customer invoice status is updated to ‘Paid’.
- a result object is returned with the summary of the processed invoices (failed, processed, rejected).
- failed invoices (exceptions) are recorded as part of the result, however additional logging/handling would be more suitable in a real-world application.

## Tests
A test class of the Billing Service was added. The class includes basic unit tests, with some extended tests which would be able to run with a database access that is beyond what is currently provided in by the MockK library. 

## Feature Availability/Further Considerations
### Monitoring
Payment charges are a very sensitive matter for both the SaaS company and the customer. Because of this, in a real-life service, I would invest more resouces into monitoring those charges in the following ways:
- Maintain a database which stores the charges’ history.
- Notify and handle immediately (possibly manually) any invoices which were charged by the External service but failed to be updated to ‘Paid’ due to an Anateuas internal error.
- Create a process to handle invoices which are pending for an unreasonable amount of time (could be due to recurring internal failures, insufficient funds etc.).

### Scalability
As the invoice charges currently run synchronously (fetched and processed one-by-one), this could be an expensive and inefficient task as Antaeus’ number of invoices grows larger. As a future improvement, a queue, for instance, could be used to increase the performance when several different concurrent threads would pull the pending messages from the queue.

### Holidays
Local holidays might be taken into consideration and shift the charge from the 1st day of the month to the next business day in case there is a holiday on the 1st. External APIs would may provide the needed market holiday data.



##
_Love getting feedback and making my applications even better, thank you!_

[Jenny R](https://www.linkedin.com/in/jennyrukman/).
