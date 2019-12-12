# RuleSheetServer
Server to which files may be promoted via HTTP


**UPDATE:**
The server is currently turned off due to AWS free tier restrictions.  To turn back on, please contact the maintainer at rottney123@gmail.com.
----------------------------------------------------------------


**SUMMARY:**
This is the back end for the [PromoteFiles CLI app](https://github.com/rottney/PromoteFiles/blob/master/README.md).


**USAGE:**
There is nothing to install.  Three instances are currently running on Elastic Beanstalk:
* [cluster 1](http://cluster1.3dpqdi6p3x.us-west-2.elasticbeanstalk.com/home/view), for customers 0 to 499 (incl.)
* [cluster 2](http://cluster2.3dpqdi6p3x.us-west-2.elasticbeanstalk.com/home/view), for customers 500 to 999 (incl.)
* [cluster 3](http://cluster3.3dpqdi6p3x.us-west-2.elasticbeanstalk.com/home/view), for customers 1000 to 1499 (incl.)

Each of these links display the names, version numbers, and contents of the 10 most recently-promoted files to their respective clusters
(unless fewer than 10 entries exist for that cluster, in which case all entries are returned).
Results are ordered from most recently-added to least recently-added.


**RULES:**
Though these validations are also performed on the front end, the following validations are performed before files are written to the databases:
* File name must be of the format \<RuleType>\_\<CustomerID>\.txt
* RuleType is either ExpenseRouting, Compliance, or SubmitCompliance
* CustomerID is between 0 and 1499, inclusive


**VERSIONING:**
Any time a file whose name does not already exist on the server is promoted, version 1 will be created.
Any time a file whose name already exists on the server is promoted, version n+1 will be created, where n is the current version.


**TESTING:**
Please see the [client documentation](https://github.com/rottney/PromoteFiles/blob/master/README.md) for general application usage,
but if you wish to bypass this client-side validation for additional testing, please run the following command in a terminal to add a new file:

```curl http://cluster<n>.3dpqdi6p3x.us-west-2.elasticbeanstalk.com/home/add -d name=NAME -d contents="CONTENTS"```

replacing `<n>` with 1, 2, or 3 (and also removing the angle brackets).
Please note that using this method, the routing rules defined in the client documentation are not enforced; i.e., a file with any CustomerID may be routed to any cluster.
