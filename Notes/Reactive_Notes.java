tradition (blocking ) application
we have to wait from client to rest controller to service and repo to db 
every layer we have to wait
embeded tomcat is a blocking web server

traditional spring MVC web applications are called blocking is because they are
they are deployed on a web server that uses thread per request architecture
why this was a probelm ?
webserver like tomcat whehter it is embedded or external it uses thread per request architectre. 
i.e for every request apache tomcat will use a separate thread.

one thread that tomcat started to handle this http request it will be blocked until spring application 
does not finish all of its business logic 
and while wating for spring mvc application to finis this thread it cannot process any other incoming HTTP request, 
and only after HTTP request is fully process and response is sent back to a client application
This thread is returned to a thread pool and become available to handle new HTTP requests

apache tomcat can start more than one thread when you start up Apache tomcat intiailly it will create a thread pool 
Thread pool. min=25, max=200
it will create a small number of threads right away and at the time. 
the default minimum number of threads that tomcat will always keep alive is 25.

the pool is dyanamically manages meaning that thread are created as needed 
but there is a limit. 

if maximum number of thread that is configured for your tomcat is 200 only then 
http requests 201th will not come through they will have to wait for existing threads to 
become available and this might lead to error on the client side.
by default it can service maximum 200 concurrent requests.
this architecutre has a limit. 

solution to thread per request problem. 
* configure tomcat to allocate more threads. but it has a limited has 
computer resources are limited
there is a 1:1 relation to  reqest-> java thread-> os thread-> memory+ cpu
each java thread is tied to os thread and this thread in O.S it consumes computer resources
we know that computer resources are limited we cannot exceed that limit
eventaully our computer will run out of resources that are available to service 
concurrent requests. and this is why there is a limit in number of threads that 
apache tomcat can allcoate to service 
we could configure tomcat to increaese the thread but there is a certain limti to it 
as resouce of one single computer is limted then we upgrade more  our server 
and add more memory and cpu (down point)

* vertical scaling(add more memory and more CPU cores)
* Horizontal scaling ( add more servers) 
* java 21 ( project loom and virtual threads)
* Reactive programming with non-blocking I/O


Reactive 
https://chatgpt.com/share/6971a188-5a40-800d-bb96-4c7ae9d5f458

Spring framework uses spring webflux which is specifically designed to
create reactive application 
spring webflux uses project reactor to create reactive web applications 
project reactor is a libratry which helps us to hangle data Asrocnouly and 
non blocking way
it allows our application to hanlde multiple http request and at
the same time wihout waiting for one task to finish before starting another one
to do this project reactor gives us two reactive data types, which 
are mono and flux.

mono when we have to return atmost 1 item
flux when we have to return more than 1 item 


Mono and flux are implementation of publisher interface that is 
provided to us by reactive streams specification

publisher is what emits data in a non blocking way,
if we have a publisher that emits at most one item, then we 
use mono daya type and if we have publisher that emits mulitle then we 
use flux dataype

Reactive Streams Specification is a standard for handling data in a 
way that doesn't block our applicaion and it defines several interfaces 
that libraries like prject Reactor implement 
it defines four core  interfaces are publisher, subsriber, subscription and processor
that other libraries provide implemnation for  and one of this library is project Reactor
Publisher is what sends data synchronously to one or more subscriber
Subscriber is what consumes data is emitted by publisher and processes it asyncronously
Subscription represents connection between publisher and subcriber  and subcriber can use this subcription object to request data or to cancel subcscription
Processor that act as both subscriber and publisher it can recive data, it can process it and then publish result


Intoduction to Reactive programmings 
it enables deeloper to build non blocking application that can handle asyncronous and synchronous operations
non blocking doesn't always mean Asynchronous  Reactive application can very well perform synchronous operations, especailly when they work with in memory data
or when they execute business logic that does not involve input and output operations
reactive programming are non blocking but they dont need to be absoulety asyncronous

no need to try to make every single line of code asyncronous

* focuses on data streams and propogation of change
Data stream is sequence of data elements that are made available over time. 
like continous flow of data as water flows in the river.

In Reactive, you work with this flow of data as it arrives instead of waiting for 
the entire list of elements to become available, you work with each element one by one 
as it arrives without wating,

propogation of change means when something changes, that chagne automatically spreads to other part of the system that depends on it

for ex if you update a cell in spreadsheet taht contain numbers and formula
so if you change value in one cell then cells that depend on this volume through formulas will automatically update
and this automatic updating is what we mean by progration of change


* Useful for application that need to ahndle a large number of concurrent users or data streams efficiently
* Typically employs a functinal programming style rather than an imperative one.
	*Reactive streams to handle data flow
	*lamda functions for consise code
	* use operators like map and filter to process data

diff between imperative and function programmign ref. Intoduction to Reactive programming slide

in functional programming you will not use try catch block anymore instead use special function that can use to handle errors

getUserById(userId) method is not stored in a separate variable and instead it is immediatly passes to a map operators
function chaining 
in Functional programming, Instead of creating separate variable and then using if else condition or try catch blocks to control flow of programmign we 
use functions that are chained to one single pipeline 

switchIfempty is uses instead of try catch block  

*Data streams in Reactive applications
in tradition spring mvc achritecute
when DAO reads 500 records from the database these recores they will be read and stored in memory 
as one large list of 500 objects this large collection of 500 objcets
it will return back to a service layer class and then back to a controller.
and before returng 500 objects to the controller class if needed, you can conver them 
to a list of objects of a different types, but these 500 objects they are moved from one layer to another layer
as one large piece 
in reactive application.
when items are read from the database  the DAO will act as as a publisher and method in the service layer  class act as subcriber
instead of returning all 500 items as a single collection, publisher will emit these items one by one.
to each subcriber, in this case subscriber will process each item as soon as it arrives.
it doesn't wait for all 500 items to arrive at first before it can start processing first item.

it starts to process each item as soon as it arrives then each item one by one continues to flow to a controller as soon as it processed
 so this continous and asyncronous flow of items through the system is called reactive data stream.
 
 reacgtive data stream is memory efficient and this is because it doen't hold all these 500 recors in memory at once.
its not blocking because while one item is being been processed our application can still handle other taska 
and it is streaming because in this kind of applicaton you can start sendign results back to a clinet application 
even before all 500 items have been read from a database. 

service<-----------dao<--                    db
subscriber      publisher               Datasource


publisher is a component that emits a sequence of items to one or more subscribers according to their demand.
the way they communicate is 
to request data from publisher, subscriber will call subscribe method on the publisher this method will intiate
subscrition process and it will return immediatly it is a non blocking method it does not wait for any data.
publisher then call onSubscribe method on subscrivber to pass subscription object.

subscription object allows subscriber to request data and to manage subscribtion
for ex you can use this subcription object to cancel subscription and to stop reciving data 
stream at any time. 

subcriber will use this subcription objcet to call request(long n)
n paramter will tell publisher how many items subcriber want to recive.

publisher knows from the request(long n) method how many data subcriber wants and deping on the 
number of tiems requested in the request method onNext(T item) method can be callled multiples items one time per each item. 
one item at a time 

once all the requested item is being proceses subcriber can request more items by calling the request method again 
subscirber will controll data flow and to manage the rate at which it processes
once there are no more items to send publisher calls onComplete() method tells this is the end of the stream,
of if error take place, then instead of calling on comlete method 
publisher will call on error method to tell subsriber that this data stream cannot continue 

in most cases these will happens automatically and we dont need to call it manually. 

Reactive demo

intrestingly, data is beings streams through different component one item at a time.
also how one component can be acting as both a publisher and subscirber
for ex serivce class is a subcriber to DAO layer and at same time publisher to controller 

what happnes when publisher publishes data faster then the subscriber can consume.
i.e where backpressure comes to play. 

Back pressure is a way for Subscriber to manage how much dat they recive from publisher
different ways to manage backpressure and one of the ways is to use request method  paramter

if you want to control the number of items that publisher sends you can use this method 

otherwise you can ignore this method  and contine with a default behaviour  
by default  request method will be called with a maximun number which means that it will request from publisher 
all items at once
this might cause problem if your subsriber is very slow and if it cannot process items quickly 
enouh extra items will be kept in memory 
if there is not enoguht memory to keep all items then your application will slow downn.


