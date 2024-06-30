# ContextAIDesignExercise
Context AI Design Exercise
     

Approach

I'll start by reading through the problem and sleeping on it. I'll work progressively over the end of this week and the beginning of next week as I have pockets of free time. I'll eventually write code to be run with scala-cli. (The instructions say pseudocode is fine, but then ask to have it run from the command line, which means it's real code.)
         
Running the prototype

I jotted out a prototype with Scala CLI. To run it:

If you open this README.md in Intellij it will let you run the shell scripts in the terminal by clicking on double triangles next to them. Otherwise, cut/paste.

I used brew to install `scala-cli` on my Mac. (See https://scala-cli.virtuslab.org/docs/overview for other options.) :

```shell
brew install Virtuslab/scala-cli/scala-cli
```

To run the prototype:

```shell
scala-cli run src/ProductScan.scala
```


Journal

Sleeping on it let me remember RDF from Time0's WW Grainger project. 

Rules engines, rules expression, and rules matching is its own problem space. I'll use partial functions for conditions - likely use Scala's match/case structures to whip through them. I can teach it to better 5th-graders, so it could fit in a business plan. 
                                      
Reinterpreting the Exercise:

Each product has a set of attributes (so this really does match RDF). 

Each product is described by a set of attributes, such as name, type, color, cost and weight, each of which may have a different data type (String, Boolean, Number). Each product will always have a price.

Rules are made up of conditions - individual logic clauses - and a score. 

Score for a single product for a single rule is the percentage of conditions that match the products attributes times the score.

For each product loop over all the rules to sum a total score. 
  
Filter products by meeting some minimum threshold - 50 by default. (Not 50% meaning half the products)

Calculate the total and average product prices

I set up a simple scala CLI project. This will likely be a one-file prototype.

I'm not sure what I want to say at the level of a UML diagram. There's not a lot going on. Maybe I make something to wave my hands at when I get to the end.

Finally carved some code Sunday afternoon while waiting for the kiddo.




Assumptions 
                                       
I get to choose the data structures I'll be using, and don't have to worry about data plumbing. Normally I would expect a lot of structures to describe the products the company is selling - overengineered standards like RDFs https://www.w3.org/RDF/ or (more likely) organic systems like Amazon's Product Data JSON  https://jsonstudio.io/samples/amazon-product-data-response-json . Data plumbing would be the lion's share of the work, unless it were eclipsed by data clean-up. I'm assuming that working with existing data structures is not the interesting part of this exercise.

Rules engines like DROOLS exist which neatly solve this sort of problem. I'm assuming that's out-of-bounds for the exercise, but is worth discussing. If something already available will not work then why is this problem special? 

I'm assuming the sales people and customer are letting me encode the rules - or know some Scala. Even thought that that last clause may sound crazy Wildfire had laypeople writing rules using organic - and incomplete - domain-specific languages at the heart of their business plans. (I decided not to go to a start-up that was headed down that same path in an interview cycle.) Using a subset of a fully-grown language would solve a significant part of the problem. That's the basic model for Scala DSLs. If you're up for a 30-minute tangent - my next NE Scala talk - ask me about my DSL thesis. It's completely clear to me that the specified little scripting language is not going to be enough for anything of scale. 

I want to have a long discussion about how these rules are interpreted and how that might be useful for the customer. I'm having trouble imagining how this sort of score might be useful for anything beyond prizes for amusement park games.
               
I'm assuming the product set and rules set are both small enough to fit comfortably in memory. I'm going to write the product scan so that it is just iterating though some map transformations and then summed and averaged as a gather at the end. If the products don't fit then streaming or even spreading it out and using Spark would not require much of a change. 

The text description talks about String attributes, but the example and UML diagram use enumerated attribute and BLUE. I'll put together something that can support both.

It's not clear what to do if a product doesn't have a given feature defined. I TODO

---

The Design Problem

Design Exercise

Description
A salesman is selling a set of products. Each product is described by a set of attributes, such as name, type, color, cost and weight, each of which may have a different data type (String, Boolean, Number).

A company is looking to buy products at the best possible prices, and which best match its needs. It has many different products it is looking to purchase. It defines the products it wishes to buy with a set of rules. Each rule is defined by a set of conditions, and a score for if that rule matches. Each condition is made up of an attribute name, a value, and a comparison operator. Some rules might be negative, meaning that if the rule matches, the scoring should be negative. A rule might look like:

      color == BLUE && price < 17.75 && quantity > 750  100

The company realizes that it is very time consuming and error prone to sort through the salesman’s goods and is looking to implement a system that will:

Score all of the salesman’s products on how well they match their product definitions by calculating the sum of the rule scores, which is the percentage of conditions which match, multiplied by the score.
Filter the potential products to just those that pass a given threshold (assume 50% as the cutoff).
Calculate the total and average prices for all the products that score sufficiently highly.

Tasks
Your task is to design the system for the company, based on the requirements above.

Draw a rough UML diagram showing the classes for the objects described above, and in particular rules and conditions. See the sample UML for product below.
Write out, in code or psuedocode, a function that will calculate the scores, and the total and average prices for the products.
If you make any assumptions, write them down.
Make sure your solution is runnable from the command line via your tool of choice.
The program must print out the total and average prices that score sufficiently highly when run from the command line.


NOTE: Perfect UML and perfect syntax are not expected. This is a starting point for a design discussion, not a test.

